/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
@XmlRootElement(name = "entity")
public class Entity {

    @XmlID
    @XmlAttribute(name = "uid")
    @XmlJavaTypeAdapter(UUIDXmlAdapter.class)
    @XmlSchemaType(name = "entity:uid")
    private UUID uid;
    
    @XmlID
    @XmlAttribute(name = "name")
    @XmlSchemaType(name = "entity:name")
    private String name;

    @XmlElement(name = "property")
    private List<Property> properties;

    public void addProperty(Property property) {
        properties.add(property);
    }
    
    public List<Property> getProperties() {
        return properties;
    }
    
    public Entity() {
        properties = new ArrayList<>();
    }

    public Entity(UUID uid, String name) {
        properties = new ArrayList<>();
        this.uid = uid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }
    
}
