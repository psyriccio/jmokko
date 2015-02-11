/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.comm;

import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jmokko.jaxb.UUIDXmlAdapter;
import jmokko.jaxb.Xml;

/**
 *
 * @author psyriccio
 */
@Xml
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "node")
public class NodeDescriptor {

    @XmlID
    @XmlAttribute(name = "uid")
    @XmlJavaTypeAdapter(UUIDXmlAdapter.class)
    @XmlSchemaType(name = "node:uid")
    private UUID uid;
    
    @XmlID
    @XmlAttribute(name = "name")
    @XmlSchemaType(name = "node:name")
    private String name;
    
    public NodeDescriptor() {
        uid = UUID.randomUUID();
    }

    public NodeDescriptor(String name) {
        this.uid = UUID.randomUUID();
        this.name = name;
    }

    public NodeDescriptor(UUID uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
