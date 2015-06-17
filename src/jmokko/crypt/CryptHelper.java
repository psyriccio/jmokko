/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.crypt;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import sun.security.pkcs.PKCS8Key;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPrivateKeyImpl;

/**
 *
 * @author psyriccio
 */
public class CryptHelper {

    private final Cipher cipher;
    private KeyPairContainer keyPair;
    private Signature signature;
    private final CryptAlgoritm cryptAlgoritm;
    private final CryptAlgoritmMode cryptAlgoritmMode;
    private final CryptPadding cryptPadding;
    private final SignatureAlgoritm signatureAlgoritm;
    
    public CryptHelper(CryptAlgoritm cryptAlgoritm, CryptAlgoritmMode cryptAlgoritmMode, CryptPadding cryptPadding, SignatureAlgoritm signatureAlgoritm, KeyPairContainer keyPair) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance(cryptAlgoritm.name() + "/" + cryptAlgoritmMode.name() + "/" + cryptPadding.name());
        this.signature = Signature.getInstance(signatureAlgoritm.name());
        this.keyPair = keyPair;
        this.cryptAlgoritm = cryptAlgoritm;
        this.cryptAlgoritmMode = cryptAlgoritmMode;
        this.cryptPadding = cryptPadding;
        this.signatureAlgoritm = signatureAlgoritm;
    }
    
    public byte[] encrypt(byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublicKeyObj());
        return cipher.doFinal(data);
    }
    
    public byte[] encrypt(byte[] data, KeyPairContainer key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, key.getPublicKeyObj());
        return cipher.doFinal(data);
    }
    
    public byte[] decrypt(byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivateKeyObj());
        return cipher.doFinal(data);
    }
    
    public byte[] decrypt(byte[] data, KeyPairContainer key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, key.getPrivateKeyObj());
        return cipher.doFinal(data);
    }
    
    public byte[] sign(byte[] data) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException {
        signature.initSign(KeyFactory.getInstance("RSA").generatePrivate(keyPair.getPrivateKeyPKCS8()));
        signature.update(data);
        return signature.sign();
    }
    
    public byte[] sign(byte[] data, KeyPairContainer key) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException {
        signature.initSign(KeyFactory.getInstance("RSA").generatePrivate(key.getPrivateKeyPKCS8()));
        signature.update(data);
        return signature.sign();
    }

    public boolean varifySign(byte[] data, byte[] sign) throws InvalidKeyException, SignatureException {
        signature.initVerify(keyPair);
        signature.update(data);
        return signature.verify(sign);
    }
    
    public boolean verifySign(byte[] data, byte[] sign, KeyPairContainer key) throws InvalidKeyException, SignatureException {
        signature.initVerify(key.getPublicKeyObj());
        signature.update(data);
        return signature.verify(sign);
    }
    
    public CipherOutputStream createOutputStream(OutputStream out, KeyPairContainer key) throws InvalidKeyException {
        cipher.init(Cipher.ENCRYPT_MODE, key.getPrivateKeyObj());
        return new CipherOutputStream(out, cipher);
    }
    
    public CipherInputStream createInputStream (InputStream in, KeyPairContainer key) throws InvalidKeyException {
        cipher.init(Cipher.DECRYPT_MODE, key.getPublicKeyObj());
        return new CipherInputStream(in, cipher);
    }
    
}
