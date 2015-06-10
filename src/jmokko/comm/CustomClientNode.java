/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.comm;

import com.jcabi.http.Request;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.response.RestResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.hawtbuf.ByteArrayInputStream;

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
    private String sessionKey;
    
    public CustomClientNode(NodeDescriptor nodeDescriptor, String host, int port, byte[] initData) throws UnknownHostException {
        super(nodeDescriptor, InetAddress.getByName(host), port);
        
        this.inQueue = new ConcurrentLinkedQueue<>();
        this.outQueue = new ConcurrentLinkedQueue<>();
        this.host = host;
        
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
                    RestResponse responce = new ApacheRequest("http://" + host)
                        .uri()
                        .path("/init/" + nodeDescriptor.getUid().toString())
                        .back()
                        .method(Request.POST)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK);
                    sessionId = responce.body();
                    sessionKey = responce.headers().get("Session-Key").toString();
                    log.info("returned sessionId = " + sessionId);
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
                                new ApacheRequest("http://" + host)
                                    .uri()
                                    .path("/message_avaible/" + nodeDescriptor.getUid().toString())
                                    .back()
                                    .header("Session-Key", sessionKey)
                                    .method(Request.GET)
                                    .fetch()
                                    .as(RestResponse.class)
                                    .assertStatus(HttpURLConnection.HTTP_OK)
                                    .body()
                        );
                        if(msgAvaible) {
                            byte[] buf = new ApacheRequest("http://" + host)
                                .uri()
                                .path("/message_get/" + nodeDescriptor.getUid().toString())
                                .back()
                                .header("Session-Key", sessionKey)
                                .method(Request.GET)
                                .fetch()
                                .as(RestResponse.class)
                                .assertStatus(HttpURLConnection.HTTP_OK)
                                .binary();
                            try (ByteArrayInputStream inStr = new ByteArrayInputStream(buf);ObjectInputStream inObj = new ObjectInputStream(inStr)) {
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
                            new ApacheRequest("http://" + host)
                                .uri()
                                .path("/message_put/" + nodeDescriptor.getUid().toString())
                                .back()
                                .header("Session-Key", sessionKey)
                                .method(Request.POST)
                                .body().set(buf)
                                .back()
                                .fetch()
                                .as(RestResponse.class)
                                .assertStatus(HttpURLConnection.HTTP_OK)
                                .body();
                        }
                        Thread.sleep(300);
                    } catch (InterruptedException ex) {
                        log.info("Transport thread interrupted");
                        log.catching(ex);
                        break;
                    } catch (IOException ex) {
                        log.catching(ex);
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
