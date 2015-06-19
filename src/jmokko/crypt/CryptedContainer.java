/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.crypt;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author psyriccio
 */
public class CryptedContainer {

    private static final byte[] MAGIC = { '~', 'C', 'C', 0, '~' };
    private byte[] content;
    private byte[] body;
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
    
    private byte[] getPackedSymKey() {
        byte[] encodedKey = symKey.getEncoded();
        return ByteBuffer.allocate(64).put(encodedKey).put(symKeyIV).array();
        
    }
    
    private void unpackSymKey(byte[] packedKey) {
        ByteBuffer bb = ByteBuffer.wrap(packedKey);
        byte[] keyEnc = new byte[32];
        symKeyIV = new byte[32];
        bb.get(keyEnc);
        bb.get(symKeyIV);
        symKey = new SecretKeySpec(keyEnc, "AES");
    }
    
    public void pack() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        ByteBuffer bb = ByteBuffer.allocate(1024 * 1024 * 100);
        bb.put(CryptedContainer.MAGIC);
        CryptHelper crypt = new CryptHelper(CryptAlgoritm.RSA, CryptAlgoritmMode.ECB, CryptPadding.PKCS5Padding, SignatureAlgoritm.SHA512withRSA, key);
        byte[] cryptedSymKey = crypt.encrypt(getPackedSymKey(), key);
        bb.put(cryptedSymKey);
        crypt = new CryptHelper(CryptAlgoritm.AES, CryptAlgoritmMode.CBC, CryptPadding.PKCS1Padding, SignatureAlgoritm.SHA512withRSA, new KeyPairContainer("AES", symKey.getEncoded()), symKeyIV);
        body = crypt.encrypt(content);
        bb.putInt(body.length);
        bb.put(body);
    }
    
    public void unpack() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        ByteBuffer bb = ByteBuffer.wrap(body);
        byte[] magic = new byte[5];
        byte[] cryptedKey = new byte[64];
        bb.get(magic);
        bb.get(cryptedKey);
        int dataLength = bb.getInt();
        body = new byte[dataLength];
        bb.get(body);
        CryptHelper crypt = new CryptHelper(CryptAlgoritm.RSA, CryptAlgoritmMode.ECB, CryptPadding.PKCS5Padding, SignatureAlgoritm.SHA512withRSA, key);
        byte[] decryptedKey = crypt.decrypt(cryptedKey);
        ByteBuffer bbkey = ByteBuffer.wrap(decryptedKey);
        byte[] encodedKey = new byte[32];
        bbkey.get(encodedKey);
        bbkey.get(symKeyIV);
        symKey = new SecretKeySpec(encodedKey, "AES");
        crypt = new CryptHelper(CryptAlgoritm.AES, CryptAlgoritmMode.CBC, CryptPadding.PKCS1Padding, SignatureAlgoritm.SHA512withRSA, new KeyPairContainer("AES", encodedKey), symKeyIV);
        content = crypt.decrypt(body);
    }
    
}
