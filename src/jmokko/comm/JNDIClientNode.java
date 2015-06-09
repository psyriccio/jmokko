/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.comm;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import javax.naming.NamingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.remoting.rmi.JndiRmiProxyFactoryBean;

/**
 *
 * @author psyriccio
 */
public class JNDIClientNode extends Node {

    private static final Logger log = LogManager.getLogger(JNDIClientNode.class);
    
    private final ConcurrentLinkedQueue<TransportMessage> inQueue;
    private final ConcurrentLinkedQueue<TransportMessage> outQueue;
    private final Thread transportThread;
    
    public JNDIClientNode(NodeDescriptor nodeDescriptor, InetAddress inetAddress, int port, byte[] initData) {
        super(nodeDescriptor, inetAddress, port);
        
        this.inQueue = new ConcurrentLinkedQueue<>();
        this.outQueue = new ConcurrentLinkedQueue<>();
        
        log.info("Staring transport thread");
        transportThread = new Thread(new Runnable() {

            @Override
            public void run() {
                log.info("Creating JNDI-factory");
                JndiRmiProxyFactoryBean jndiProxyFactory = new JndiRmiProxyFactoryBean();
                jndiProxyFactory.setLookupStubOnStartup(false);
                jndiProxyFactory.setRefreshStubOnConnectFailure(true);
                Properties env = new Properties();
                env.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
                env.put("java.naming.provider.url", "iiop://" + inetAddress.getHostName() + ":" + Integer.toString(port));
                jndiProxyFactory.setJndiEnvironment(env);
                String url = "jndi://" + inetAddress.getHostName() + ":" + Integer.toString(port) + "/jmokko.comm.ITransportPipe";
                log.info("url> " + url);
                //jndiProxyFactory.setServiceUrl(url);
                jndiProxyFactory.setJndiName("jmokko.comm.ITransportPipe");
                jndiProxyFactory.setServiceInterface(jmokko.comm.ITransportPipe.class);
                try {
                    jndiProxyFactory.afterPropertiesSet();
                } catch (NamingException ex) {
                    java.util.logging.Logger.getLogger(JNDIClientNode.class.getName()).log(Level.SEVERE, null, ex);
                }
                log.info("JNDI-factory created");
                log.info("Connecting to ITransportPipe object");
                ITransportPipe client = (ITransportPipe) jndiProxyFactory.getObject();
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
