/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.crypt;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import jmokko.jaxb.Xml;

/**
 *
 * @author psyriccio
 */
@Xml
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CipherSettings")
public class CipherSettings implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @XmlAttribute(name = "algoritm")
    private CryptAlgoritm algoritm;
    
    @XmlAttribute(name = "algoritmMode")
    private CryptAlgoritmMode algoritmMode;
    
    @XmlAttribute(name = "padding")
    private CryptPadding padding;

    @XmlAttribute(name = "keySize")
    private int keySize;
    
    public CipherSettings() {
    }

    public CipherSettings(CryptAlgoritm algoritm, CryptAlgoritmMode algoritmMode, CryptPadding padding, int keySize) {
        this.algoritm = algoritm;
        this.algoritmMode = algoritmMode;
        this.padding = padding;
        this.keySize = keySize;
    }

    public CryptAlgoritm getAlgoritm() {
        return algoritm;
    }

    public void setAlgoritm(CryptAlgoritm algoritm) {
        this.algoritm = algoritm;
    }

    public CryptAlgoritmMode getAlgoritmMode() {
        return algoritmMode;
    }

    public void setAlgoritmMode(CryptAlgoritmMode algoritmMode) {
        this.algoritmMode = algoritmMode;
    }

    public CryptPadding getPadding() {
        return padding;
    }

    public void setPadding(CryptPadding padding) {
        this.padding = padding;
    }

    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

}
