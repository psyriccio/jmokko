/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.comm;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import jmokko.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 *
 * @author psyriccio
 */
public class HttpServerNode extends Node {

    private static final Logger log = LogManager.getLogger(HttpServerNode.class);
    
    private HttpInvokerServiceExporter httpServiceExporter = null;
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<TransportMessage>> queues;
    private final ConcurrentLinkedQueue<TransportMessage> inQueue;
    private final SessionManager sessionManager = new SessionManager(10000);
    private final IAuthenticationService authService;
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
            
    public HttpServerNode(NodeDescriptor nodeDescriptor, InetAddress inetAddress, int port, IAuthenticationService authService) {
        super(nodeDescriptor, inetAddress, port);
        this.queues = new ConcurrentHashMap<>();
        this.inQueue = new ConcurrentLinkedQueue<>();
        this.authService = authService;
    }
    
    public void start() throws RemoteException {
        log.info("Starting HTTPServerNode");
        if(httpServiceExporter == null) {
            httpServiceExporter = new HttpInvokerServiceExporter();
            //httpServiceExporter.setServiceName("jmokko.comm.ITransportPipe");
            httpServiceExporter.
                    setService(pipe);
            httpServiceExporter.setServiceInterface(ITransportPipe.class);
            //httpServiceExporter.setRegistryHost(getInetAddress().getHostAddress());
            //httpServiceExporter.setServicePort(getPort());
            //httpServiceExporter.setRegistryPort(getPort());
            httpServiceExporter.afterPropertiesSet();
        }
        log.info("HTTPServerNode started");
    }
    
    public void stop() throws RemoteException {
        log.info("Stopping HTTPServerNode");
        if(httpServiceExporter != null) {
            //httpServiceExporter.destroy();
            httpServiceExporter = null;
        }
        log.info("HTTPServerNode stopped");
    }

    public ConcurrentLinkedQueue<TransportMessage> getInQueue() {
        return inQueue;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<TransportMessage>> getQueues() {
        return queues;
    }
    
}
