/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.comm;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import javax.naming.NamingException;
import jmokko.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.remoting.rmi.JndiRmiServiceExporter;

/**
 *
 * @author psyriccio
 */
public class JNDIServerNode extends Node {

    private static final Logger log = LogManager.getLogger(JNDIServerNode.class);
    
    private JndiRmiServiceExporter jndiServiceExporter = null;
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<TransportMessage>> queues;
    private final ConcurrentLinkedQueue<TransportMessage> inQueue;
    private final SessionManager sessionManager = new SessionManager(10000);
    private final IAuthenticationService authService;
    private final InetAddress inetAddress;
    private final int port;
    private final ITransportPipe pipe = new ITransportPipe() {

        @Override
        public String init(String id, byte [] initData) throws IOException {
            log.info("ITransportPipe.init() " + id + ", initData = " + Utils.bytesToHex(initData));
            String sessionId = authService.authentication(id, initData);
            ConcurrentLinkedQueue<TransportMessage> newQueue = new ConcurrentLinkedQueue<>();
            queues.put(sessionId, newQueue);
            log.info("Created session id = " + sessionId);
            return sessionId;
        }
        
        @Override
        public void close(String sessionId) throws IOException {
            log.info("ITransportPipe.close()");
            if(queues.contains(sessionId)) {
                queues.remove(sessionId);
            }
            log.info("Closed session id = " + sessionId);
        }

        @Override
        public boolean messageAvaible(String sessionId) throws IOException {
            return !queues.get(sessionId).isEmpty();
        }

        @Override
        public TransportMessage get(String sessionId) throws IOException {
            return queues.get(sessionId).poll();
        }

        @Override
        public void put(String sessionId, TransportMessage msg) throws IOException {
            inQueue.add(msg);
        }

    };
            
    public JNDIServerNode(NodeDescriptor nodeDescriptor, InetAddress inetAddress, int port, IAuthenticationService authService) {
        super(nodeDescriptor, inetAddress, port);
        this.queues = new ConcurrentHashMap<>();
        this.inQueue = new ConcurrentLinkedQueue<>();
        this.authService = authService;
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    public void start() throws RemoteException {
        log.info("Starting JNDIServerNode");
        if(jndiServiceExporter == null) {
            jndiServiceExporter = new JndiRmiServiceExporter();
            Properties env = new Properties();
            env.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
            env.put("java.naming.provider.url", "iiop://" + inetAddress.getHostName() + ":" + Integer.toString(port));
            jndiServiceExporter.setJndiEnvironment(env);
            jndiServiceExporter.setJndiName("jmokko.comm.ITransportPipe");
            jndiServiceExporter.setService(pipe);
            jndiServiceExporter.setServiceInterface(ITransportPipe.class);
            try {
                //jndiServiceExporter.setRegistryHost(getInetAddress().getHostAddress());
                //jndiServiceExporter.setServicePort(getPort());
                //jndiServiceExporter.setRegistryPort(getPort());
                jndiServiceExporter.afterPropertiesSet();
                jndiServiceExporter.prepare();
                jndiServiceExporter.rebind();
            } catch (NamingException ex) {
                java.util.logging.Logger.getLogger(JNDIServerNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        log.info("JNDIServerNode started");
    }
    
    public void stop() throws RemoteException {
        log.info("Stopping JNDIServerNode");
        if(jndiServiceExporter != null) {
            try {
                jndiServiceExporter.destroy();
            } catch (NamingException ex) {
                java.util.logging.Logger.getLogger(JNDIServerNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchObjectException ex) {
                java.util.logging.Logger.getLogger(JNDIServerNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            jndiServiceExporter = null;
        }
        log.info("JNDIServerNode stopped");
    }

    public ConcurrentLinkedQueue<TransportMessage> getInQueue() {
        return inQueue;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<TransportMessage>> getQueues() {
        return queues;
    }
    
}
