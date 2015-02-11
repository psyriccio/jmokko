/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import jmokko.jaxb.Xml;

/**
 *
 * @author psyriccio
 */
@Xml
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "nodes")
public class NodeRegistry {

    @XmlElement(name = "node")
    private List<NodeDescriptor> nodes;

    public NodeRegistry() {
        nodes = new ArrayList<>();
    }
    
    public void add(NodeDescriptor node) {
        nodes.add(node);
    }
    
    public void remove(NodeDescriptor node) {
        nodes.remove(node);
    }
    
    public void remove(int index) {
        nodes.remove(index);
    }    

    public void indexOf(NodeDescriptor node) {
        nodes.indexOf(node);
    }
    
    public boolean contains(NodeDescriptor node) {
        return nodes.contains(node);
    }

    public NodeDescriptor resolve(UUID uid) {
        for(NodeDescriptor node : nodes) {
            if(node.getUid().compareTo(uid) == 0) {
                return node;
            }
        }
        return null;
    }
    
    public NodeDescriptor resolve(String uidOrName) {
        NodeDescriptor nodeRs = resolve(UUID.fromString(uidOrName));
        if(nodeRs == null) {
            for(NodeDescriptor node : nodes) {
                if(node.getName().equals(uidOrName)) {
                    return node;
                }
            }
        } else {
            return nodeRs;
        }
        return null;
    }
    
    public List<NodeDescriptor> getNodes() {
        return nodes;
    }
    
}
