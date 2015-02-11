/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.core;

import java.io.EOFException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import jmokko.jaxb.JAXB;
import org.fusesource.hawtbuf.BufferInputStream;
import org.fusesource.hawtbuf.BufferOutputStream;

/**
 *
 * @author psyriccio
 * @param <T>
 */
public class Entity<T> {
    private static JAXBContext jaxbContext = null;
    private static Marshaller marshaller = null;
    private static Unmarshaller unmarshaller = null;

    public static JAXBContext getJAXBContext() throws JAXBException {
        if(jaxbContext == null) {
            jaxbContext = JAXB.buildContext("mokko.crypt");
        }
        return jaxbContext;
    }
    
    public static Marshaller getMarshaller() throws JAXBException {
        if(marshaller == null) {
            marshaller = getJAXBContext().createMarshaller();
        }
        return marshaller;
    }

    public static Unmarshaller getUnmarshaller() throws JAXBException {
        if(unmarshaller == null) {
            unmarshaller = getJAXBContext().createUnmarshaller();
        }
        return unmarshaller;
    }

    public static Entity fromXML(URL url) throws JAXBException {
        return (Entity) getUnmarshaller().unmarshal(url);
    }

    public static Entity fromXML(InputStream inStream) throws JAXBException {
        return (Entity) getUnmarshaller().unmarshal(inStream);
    }
    
    public static Entity fromXML(File file) throws JAXBException {
        return (Entity) getUnmarshaller().unmarshal(file);
    }
    
    public static Entity fromXML(String xml) throws JAXBException {
        BufferInputStream buf = new BufferInputStream(xml.getBytes());
        return (Entity) getUnmarshaller().unmarshal(buf);
    }
    
    public void toXML(String xml) throws JAXBException {
        xml = toXMLString();
    }
    
    public void toXML(OutputStream outStream) throws JAXBException {
        getMarshaller().marshal(this, outStream);
    }
    
    public void toXML(File file) throws JAXBException {
        getMarshaller().marshal(this, file);
    }
    
    public String toXMLString() throws JAXBException {
        BufferOutputStream buf = new BufferOutputStream(1024);
        try {
            getMarshaller().marshal(this, buf);
        } catch (Exception ex) {
            if(ex.getClass().equals(EOFException.class)) {
                buf = new BufferOutputStream(1024*1024);
                try {
                    getMarshaller().marshal(this, buf);
                } catch (Exception ex1) {
                    if(ex1.getClass().equals(EOFException.class)) {
                        buf = new BufferOutputStream(1024*1024*10);
                        try {
                            getMarshaller().marshal(this, buf);
                        } catch (Exception ex2) {
                            if(ex2.getClass().equals(EOFException.class)) {
                                buf = new BufferOutputStream(1014*1024*100);
                                getMarshaller().marshal(this, buf);
                            } else {
                                throw ex2;
                            }
                        }
                    } else {
                        throw ex1;
                    }
                }
            } else {
                throw ex;
            }
        }
        return new String(buf.toByteArray());
    }
    
}
