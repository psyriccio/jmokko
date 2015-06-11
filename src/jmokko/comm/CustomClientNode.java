/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.comm;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author psyriccio
 */
public class CustomClientNode extends Node {

    private static final Logger log = LogManager.getLogger(CustomClientNode.class);
    
    private final ConcurrentLinkedQueue<TransportMessage> inQueue;
    private final ConcurrentLinkedQueue<TransportMessage> outQueue;
    private final Thread transportThread;
    private final String host;
    private final String resource;
    private String sessionKey;
    
    public CustomClientNode(NodeDescriptor nodeDescriptor, String host, String resource, int port, byte[] initData) throws UnknownHostException {
        super(nodeDescriptor, InetAddress.getByName(host), port);
        
        this.inQueue = new ConcurrentLinkedQueue<>();
        this.outQueue = new ConcurrentLinkedQueue<>();
        this.host = host;
        this.resource = resource;
        
        log.info("Staring transport thread");
        transportThread = new Thread(new Runnable() {

            @Override
            public void run() {
                log.info("Creating factory");
                log.info("Factory created");
                log.info("Connecting to ITransportPipe object");
                //ITransportPipe client = (ITransportPipe) null;
                String sessionId;
                try {
                    log.info("call to ITransportPipe.init()");
                    //sessionId = client.init(nodeDescriptor.getUid().toString(), initData);
                    HttpResponse<String> responce = Unirest.post("http://" + host + "/" + resource + "/init/" + nodeDescriptor.getUid().toString()).asString();
                    sessionId = responce.getBody();
                    log.info("sessionId=" + sessionId);
                    sessionKey = responce.getHeaders().get("Session-Key").get(0);
                    log.info("sessionKey=" + sessionKey);
                } catch (Exception ex) {
                    log.info("Exception in transport thread");
                    log.catching(ex);
                    throw new RuntimeException(ex.getMessage());
                }
                while(true) {
                    try {
                        //if(client.messageAvaible(sessionId)) {
                        //    inQueue.add(client.get(sessionId));
                        //}
                        boolean msgAvaible = Boolean.parseBoolean(
                                Unirest.get("http://" + host + "/" + resource + "/message_avaible/" + nodeDescriptor.getUid().toString())
                                        .header("Session-Key", sessionKey)
                                        .asString()
                                        .getBody()
                        );
                        if(msgAvaible) {
                            InputStream inBuf = Unirest.get("http://" + host + "/" + resource + "/message_get/" + nodeDescriptor.getUid().toString())
                                .header("Session-Key", sessionKey)
                                .asBinary()
                                .getBody();

                            try (ObjectInputStream inObj = new ObjectInputStream(inBuf)) {
                                try {
                                    inQueue.add((TransportMessage) inObj.readObject());
                                } catch (ClassNotFoundException ex) {
                                    java.util.logging.Logger.getLogger(CustomClientNode.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                        TransportMessage msg = outQueue.poll();
                        if(msg != null) {
                            msg.setSessionId(sessionId);
                            //client.put(sessionId, msg);
                            byte[] buf;
                            try ( 
                                ByteArrayOutputStream out = new ByteArrayOutputStream();ObjectOutputStream outObj = new ObjectOutputStream(out)) {
                                outObj.writeObject(msg);
                                buf = out.toByteArray();
                            }
                            Unirest.post("http://" + host + "/" + resource + "/message_put/" + nodeDescriptor.getUid().toString())
                                .header("Session-Key", sessionKey)
                                .body(buf)
                                .asString()
                                .getBody();
                        }
                        Thread.sleep(300);
                    } catch (InterruptedException ex) {
                        log.info("Transport thread interrupted");
                        log.catching(ex);
                        break;
                    } catch (IOException ex) {
                        log.catching(ex);
                    } catch (UnirestException ex) {
                        java.util.logging.Logger.getLogger(CustomClientNode.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        log.info("Transport thread started");
        
    }

    public TransportMessage poll() {
        return inQueue.poll();
    } 
    
    public void post(TransportMessage msg) {
        outQueue.add(msg);
    }
    
    public void start() {
        transportThread.start();
    }
    
}
