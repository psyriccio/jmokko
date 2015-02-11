/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.crypt;

import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import jmokko.jaxb.Xml;

/**
 *
 * @author psyriccio
 */
@Xml
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "KeyPair")
public class KeyPairContainer implements PublicKey {
    
    public final static String DEFAULT_PRIVATE_FORMAT = "PKCS#8";
    public final static String DEFAULT_PUBLIC_FORMAT = "X.509";
    
    @XmlTransient
    private final PrivateKey privateKeyObj = new PrivateKey() {

        @Override
        public String getAlgorithm() {
            return algorithm;
        }

        @Override
        public String getFormat() {
            return privateFormat;
        }

        @Override
        public byte[] getEncoded() {
            return privateKey;
        }
    };
    
    @XmlTransient
    private final PublicKey publicKeyObj = new PublicKey() {
        @Override
        public String getAlgorithm() {
            return algorithm;
        }

        @Override
        public String getFormat() {
            return publicFormat;
        }

        @Override
        public byte[] getEncoded() {
            return publicKey;
        }
            
    };
    
    @XmlAttribute(name = "algorithm")
    private String algorithm;
    
    @XmlAttribute(name = "privateFormat")
    private String privateFormat;

    @XmlAttribute(name = "publicFormat")
    private String publicFormat;
    
    @XmlElement(name = "private")
    private byte[] privateKey;
    
    @XmlElement(name = "public")
    private byte[] publicKey;

    public KeyPairContainer() {
    }

    public KeyPairContainer(KeyPair keyPair) {
        if(keyPair.getPrivate() != null) {
            privateKey = keyPair.getPrivate().getEncoded();
            algorithm = keyPair.getPrivate().getAlgorithm();
            privateFormat = keyPair.getPrivate().getFormat();
        }
        if(keyPair.getPublic() != null) {
            publicKey = keyPair.getPublic().getEncoded();
            algorithm = keyPair.getPublic().getAlgorithm();
            publicFormat = keyPair.getPublic().getFormat();
        }
    }
    
    public KeyPairContainer(String algorithm, String publicFormat, byte[] publicKey, String privateFormat, byte[] privateKey) {
        this.algorithm = algorithm;
        this.privateFormat = privateFormat;
        this.publicFormat = publicFormat;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public KeyPairContainer(String algorithm, String privateFormat, byte[] privateKey) {
        this.algorithm = algorithm;
        this.privateFormat = privateFormat;
        this.publicFormat = "";
        this.privateKey = privateKey;
        this.publicKey = null;
    }

    
    public KeyPairContainer(String algorithm, byte[] privateKey, byte[] publicKey) {
        this.algorithm = algorithm;
        this.privateFormat = DEFAULT_PRIVATE_FORMAT;
        this.publicFormat = DEFAULT_PUBLIC_FORMAT;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public KeyPairContainer(String algorithm, byte[] privateKey) {
        this.algorithm = algorithm;
        this.privateFormat = DEFAULT_PRIVATE_FORMAT;
        this.publicFormat = DEFAULT_PUBLIC_FORMAT;
        this.privateKey = privateKey;
        this.publicKey = null;
    }
    
    public byte[] getDigest(String algoritm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algoritm);
        md.update(publicKey);
        return md.digest();
    }
    
    public byte[] getSHA256Digest() throws NoSuchAlgorithmException {
        return getDigest("SHA-256");
    }
    
    public byte[] getFingerprint() throws NoSuchAlgorithmException {
        return getSHA256Digest();
    }
    
    public UUID getUID() throws NoSuchAlgorithmException {
        return UUID.nameUUIDFromBytes(getFingerprint());
    }
    
    public String getPrivateKeyString() {
        return new String(privateKey);
    }
    
    public String getPublicKeyString() {
        return new String(publicKey);
    }
    
    public PrivateKey getPrivateKeyObj() {
        return privateKeyObj;
    }

    public PublicKey getPublicKeyObj() {
        return publicKeyObj;
    }
    
    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }
    
    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    
    public String getPrivateFormat() {
        return privateFormat;
    }

    public void setPrivateFormat(String privateFormat) {
        this.privateFormat = privateFormat;
    }

    public String getPublicFormat() {
        return publicFormat;
    }

    public void setPublicFormat(String publicFormat) {
        this.publicFormat = publicFormat;
    }

    @Override
    public String getFormat() {
        return publicFormat;
    }

    @Override
    public byte[] getEncoded() {
        return publicKey;
    }
    
    public X509EncodedKeySpec getPublicKeyX509() {
        return new X509EncodedKeySpec(publicKeyObj.getEncoded());
    }
    
    public PKCS8EncodedKeySpec getPrivateKeyPKCS8() {
        return new PKCS8EncodedKeySpec(privateKeyObj.getEncoded());
    }
    
}
