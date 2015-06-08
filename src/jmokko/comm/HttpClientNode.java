/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.comm;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

/**
 *
 * @author psyriccio
 */
public class HttpClientNode extends Node {

    private static final Logger log = LogManager.getLogger(HttpClientNode.class);
    
    private final ConcurrentLinkedQueue<TransportMessage> inQueue;
    private final ConcurrentLinkedQueue<TransportMessage> outQueue;
    private final Thread transportThread;
    
    public HttpClientNode(NodeDescriptor nodeDescriptor, InetAddress inetAddress, int port, byte[] initData) {
        super(nodeDescriptor, inetAddress, port);
        
        this.inQueue = new ConcurrentLinkedQueue<>();
        this.outQueue = new ConcurrentLinkedQueue<>();
        
        log.info("Staring transport thread");
        transportThread = new Thread(new Runnable() {

            @Override
            public void run() {
                log.info("Creating HTTP-factory");
                HttpInvokerProxyFactoryBean httpProxyFactory = new HttpInvokerProxyFactoryBean();
                //httpProxyFactory.setLookupStubOnStartup(false);
                //httpProxyFactory.setRefreshStubOnConnectFailure(true);
                String url = "http://" + inetAddress.getHostName() + ":" + Integer.toString(port) + "/jmokko.comm.ITransportPipe";
                log.info("url> " + url);
                httpProxyFactory.setServiceUrl(url);
                httpProxyFactory.setServiceInterface(jmokko.comm.ITransportPipe.class);
                httpProxyFactory.afterPropertiesSet();
                log.info("HTTP-factory created");
                log.info("Connecting to ITransportPipe object");
                ITransportPipe client = (ITransportPipe) httpProxyFactory.getObject();
                String sessionId;
                try {
                    log.info("call to ITransportPipe.init()");
                    sessionId = client.init(nodeDescriptor.getUid().toString(), initData);
                    log.info("returned sessionId = " + sessionId);
                } catch (Exception ex) {
                    log.info("Exception in transport thread");
                    log.catching(ex);
                    throw new RuntimeException(ex.getMessage());
                }
                while(true) {
                    try {
                        if(client.messageAvaible(sessionId)) {
                            inQueue.add(client.get(sessionId));
                        }
                        TransportMessage msg = outQueue.poll();
                        if(msg != null) {
                            msg.setSessionId(sessionId);
                            client.put(sessionId, msg);
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
