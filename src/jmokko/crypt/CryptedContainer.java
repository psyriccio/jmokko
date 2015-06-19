/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.crypt;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author psyriccio
 */
public class CryptedContainer {

    private static final byte[] MAGIC = { '~', 'C', 'C', 0, '~' };
    private final byte[] content;
    private final byte[] body;
    private final KeyPairContainer key;
    private SecretKey symKey;
    private byte[] symKeyIV;
    
    public CryptedContainer(byte[] content, byte[] body, KeyPairContainer key) {
        this.content = content;
        this.body = body;
        this.key = key;
    }

    private void initSymKey() {
        try {
            javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("AES");
            keyGen.init(256, new SecureRandom());
            symKey = new SecretKeySpec(keyGen.generateKey().getEncoded(), "AES");
            Cipher cp = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cp.init(Cipher.ENCRYPT_MODE, symKey);
            symKeyIV = cp.getIV();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public void pack() {
        ByteBuffer bb = ByteBuffer.allocate(1024 * 1024 * 100);
        bb.put(CryptedContainer.MAGIC);
    }
    
    public void unpack() {
        
    }
    
}
