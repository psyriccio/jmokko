/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.meta;

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
@XmlRootElement(name = "property")
public class Property {

    @XmlID
    @XmlAttribute(name = "uid")
    @XmlJavaTypeAdapter(UUIDXmlAdapter.class)
    @XmlSchemaType(name = "property:uid")
    private UUID uid;
    
    @XmlID
    @XmlAttribute(name = "name")
    @XmlSchemaType(name = "property:name")
    private String name;

    @XmlAttribute(name = "type")
    private Class type;

    public Property() {
    }

    public Property(UUID uid, String name, Class type) {
        this.uid = uid;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }
    
}
