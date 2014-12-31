/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.comm;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import mokko.jaxb.Xml;

/**
 *
 * @author psyriccio
 */
@Xml
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "HM-MSG")
public class HashMapMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "body")
    private ConcurrentHashMap<String, Object> body;

    public HashMapMessage() {
        this.body = new ConcurrentHashMap<>();
    }

    public HashMapMessage(ConcurrentHashMap<String, Object> body) {
        this.body = body;
    }

    public ConcurrentHashMap<String, Object> getBody() {
        return body;
    }
    
}
