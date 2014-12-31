/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.comm;

import java.net.InetAddress;
import java.util.UUID;

/**
 *
 * @author psyriccio
 */
public class Node {
    
    private final NodeDescriptor nodeDescriptor;
    private final InetAddress inetAddress;
    private final int port;

    public Node(NodeDescriptor nodeDescriptor, InetAddress inetAddress, int port) {
        this.nodeDescriptor = nodeDescriptor;
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public NodeDescriptor getNodeDescriptor() {
        return nodeDescriptor;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }

    public UUID getUid() {
        return nodeDescriptor.getUid();
    }

    public String getName() {
        return nodeDescriptor.getName();
    }

}
