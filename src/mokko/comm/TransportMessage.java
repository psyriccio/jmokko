/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.comm;

import java.io.Serializable;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import mokko.jaxb.Xml;

/**
 *
 * @author psyriccio
 */
@Xml
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "tmsg")
public class TransportMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @XmlAttribute(name = "from")
    private UUID uidFrom;
    
    @XmlAttribute(name = "to")
    private UUID uidTo;
    
    @XmlAttribute(name = "sessionId")
    private String sessionId;
    
    @XmlElement(name = "body")
    private byte[] body;

    public TransportMessage() {
        this.uidFrom = null;
        this.uidTo = null;
        this.body = new byte[1];
    }

    public TransportMessage(UUID uidFrom, UUID uidTo, byte[] body) {
        this.uidFrom = uidFrom;
        this.uidTo = uidTo;
        this.body = body;
    }

    public UUID getUidFrom() {
        return uidFrom;
    }

    public void setUidFrom(UUID uidFrom) {
        this.uidFrom = uidFrom;
    }

    public UUID getUidTo() {
        return uidTo;
    }

    public void setUidTo(UUID uidTo) {
        this.uidTo = uidTo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
   
}
