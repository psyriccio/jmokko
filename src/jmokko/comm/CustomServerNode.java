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

/**
 *
 * @author psyriccio
 */
public class CustomServerNode extends Node {

    private static final Logger log = LogManager.getLogger(CustomServerNode.class);
    
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
            log.info("messageAvaible(), sessionId=" + sessionId);
            if(!queues.containsKey(sessionId)) {
                log.info("queues not contain key " + sessionId);
                log.info("queues dump:");
                for(String key : queues.keySet()) {
                    log.info(">" + key);
                }
                return false;
            } else {
                log.info("queue finded");
            }
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
            
    public CustomServerNode(NodeDescriptor nodeDescriptor, InetAddress inetAddress, int port, IAuthenticationService authService) {
        super(nodeDescriptor, inetAddress, port);
        this.queues = new ConcurrentHashMap<>();
        this.inQueue = new ConcurrentLinkedQueue<>();
        this.authService = authService;
    }
    
    public void start() throws RemoteException {
        log.info("Starting ServerNode");
        //
        log.info("ServerNode started");
    }
    
    public void stop() throws RemoteException {
        log.info("Stopping ServerNode");
        //
        log.info("ServerNode stopped");
    }

    public ConcurrentLinkedQueue<TransportMessage> getInQueue() {
        return inQueue;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<TransportMessage>> getQueues() {
        return queues;
    }

    public ITransportPipe getPipe() {
        return pipe;
    }
    
}
