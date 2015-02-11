/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.crypt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.BufferInputStream;
import org.fusesource.hawtbuf.BufferOutputStream;

/**
 *
 * @author psyriccio
 */
public class SecureSession {
    
    public final static byte[] handshakeMagic = { '~', 'S', 'S', 0, '~' };
    
    private final KeyPairContainer ourKeys;
    private final KeyPairContainer opponentKeys;
    private SecretKey sessionKey;
    private byte[] sessionKeyIV;
    
    private final CryptAlgoritm cryptAlgoritm;
    private final CryptAlgoritmMode cryptAlgoritmMode;
    private final CryptPadding cryptPadding;
    private final CryptAlgoritm sessionCryptAlgoritm;
    private final CryptAlgoritmMode sessionCryptAlgoritmMode;
    private final int sessionKeySize;
    private final CryptPadding sessionCryptPadding;
    private final SignatureAlgoritm signatureAlgoritm;
    private final CipherSettings cryptSettings;
    private final CipherSettings sessionCryptSettings;

    private Cipher cipherOut;
    private Cipher cipherIn;
    private Cipher sessionCipherOut;
    private Cipher sessionCipherIn;
    private Signature signatureOut;
    private Signature signatureIn;
    
    public SecureSession(KeyPairContainer ourKeys, KeyPairContainer opponentKeys, CryptAlgoritm cryptAlgoritm, CryptAlgoritmMode cryptAlgoritmMode, CryptPadding cryptPadding, CryptAlgoritm sessionCryptAlgoritm, CryptAlgoritmMode sessionCryptAlgoritmMode, CryptPadding sessionCryptPadding, int sessionKeySize, SignatureAlgoritm signatureAlgoritm) {
        this.ourKeys = ourKeys;
        this.opponentKeys = opponentKeys;
        this.cryptAlgoritm = cryptAlgoritm;
        this.cryptAlgoritmMode = cryptAlgoritmMode;
        this.cryptPadding = cryptPadding;
        this.sessionCryptAlgoritm = sessionCryptAlgoritm;
        this.sessionCryptAlgoritmMode = sessionCryptAlgoritmMode;
        this.sessionKeySize = sessionKeySize;
        this.sessionCryptPadding = sessionCryptPadding;
        this.signatureAlgoritm = signatureAlgoritm;
        this.cryptSettings = new CipherSettings(cryptAlgoritm, cryptAlgoritmMode, cryptPadding, 4096);
        this.sessionCryptSettings = new CipherSettings(sessionCryptAlgoritm, sessionCryptAlgoritmMode, sessionCryptPadding, sessionKeySize);
    }

    public SecureSession(KeyPairContainer ourKeys, KeyPairContainer opponentKeys, CipherSettings cryptSettings, CipherSettings sessionCryptSettings, SignatureAlgoritm signatureAlgoritm) {
        this.ourKeys = ourKeys;
        this.opponentKeys = opponentKeys;
        this.cryptAlgoritm = cryptSettings.getAlgoritm();
        this.cryptAlgoritmMode = cryptSettings.getAlgoritmMode();
        this.cryptPadding = cryptSettings.getPadding();
        this.sessionCryptAlgoritm = sessionCryptSettings.getAlgoritm();
        this.sessionCryptAlgoritmMode = sessionCryptSettings.getAlgoritmMode();
        this.sessionKeySize = sessionCryptSettings.getKeySize();
        this.sessionCryptPadding = sessionCryptSettings.getPadding();
        this.signatureAlgoritm = signatureAlgoritm;
        this.cryptSettings = cryptSettings;
        this.sessionCryptSettings = sessionCryptSettings;
    }

    public SecureSession(KeyPairContainer ourKeys, byte[] handshake) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.ourKeys = ourKeys;
        ByteBuffer bb = ByteBuffer.wrap(handshake);
        byte[] magic = new byte[5];
        bb.get(magic);
        if(!Arrays.equals(magic, handshakeMagic)) {
            throw new IOException("Invalid magic sequence");
        }
        int sett_length = bb.getInt();
        byte[] settings = new byte[sett_length];
        bb.get(settings);
        int pk_length = bb.getInt();
        byte[] pk = new byte[pk_length];
        bb.get(pk);
        Buffer settingsBuf = new Buffer(settings);
        BufferInputStream settingsInStream = new BufferInputStream(settingsBuf);
        ObjectInputStream settingsObjInStream = new ObjectInputStream(settingsInStream);
        this.cryptSettings = (CipherSettings) settingsObjInStream.readObject();
        this.sessionCryptSettings = (CipherSettings) settingsObjInStream.readObject();
        this.signatureAlgoritm = SignatureAlgoritm.values()[settingsObjInStream.readInt()];
        this.opponentKeys = new KeyPairContainer(new KeyPair(KeyFactory.getInstance(this.cryptSettings.getAlgoritm().name()).generatePublic(new X509EncodedKeySpec(pk)), null));
        this.cryptAlgoritm = cryptSettings.getAlgoritm();
        this.cryptAlgoritmMode = cryptSettings.getAlgoritmMode();
        this.cryptPadding = cryptSettings.getPadding();
        this.sessionCryptAlgoritm = sessionCryptSettings.getAlgoritm();
        this.sessionCryptAlgoritmMode = sessionCryptSettings.getAlgoritmMode();
        this.sessionKeySize = sessionCryptSettings.getKeySize();
        this.sessionCryptPadding = sessionCryptSettings.getPadding();
    }
    
    public void generateSessionKey() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance(sessionCryptAlgoritm.name());
        keyGen.init(sessionKeySize);
        sessionKey = new SecretKeySpec(keyGen.generateKey().getEncoded(), sessionCryptAlgoritm.name());
        sessionCipherOut = Cipher.getInstance(sessionCryptAlgoritm.name() + "/" + sessionCryptAlgoritmMode.name() + "/" + sessionCryptPadding.name());
        sessionCipherOut.init(Cipher.ENCRYPT_MODE, sessionKey);
        sessionKeyIV = sessionCipherOut.getIV();
        sessionCipherIn = Cipher.getInstance(sessionCryptAlgoritm.name() + "/" + sessionCryptAlgoritmMode.name() + "/" + sessionCryptPadding.name());
        sessionCipherIn.init(Cipher.DECRYPT_MODE, sessionKey, new IvParameterSpec(sessionKeyIV));
    }
    
    public void loadSessionKey(SecretKey newSessionKey, byte[] newSessionKeyIV) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        sessionKeyIV = newSessionKeyIV;
        sessionCipherOut = Cipher.getInstance(sessionCryptAlgoritm.name() + "/" + sessionCryptAlgoritmMode.name() + "/" + sessionCryptPadding.name());
        sessionCipherOut.init(Cipher.ENCRYPT_MODE, sessionKey, new IvParameterSpec(newSessionKeyIV));
        sessionCipherIn = Cipher.getInstance(sessionCryptAlgoritm.name() + "/" + sessionCryptAlgoritmMode.name() + "/" + sessionCryptPadding.name());
        sessionCipherIn.init(Cipher.DECRYPT_MODE, sessionKey, new IvParameterSpec(newSessionKeyIV));
    }
    
    public void init() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        cipherOut = Cipher.getInstance(cryptAlgoritm.name() + "/" + cryptAlgoritmMode.name() + "/" + cryptPadding.name());
        cipherIn = Cipher.getInstance(cryptAlgoritm.name() + "/" + cryptAlgoritmMode.name() + "/" + cryptPadding.name());
        cipherOut.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance(cryptAlgoritm.name()).generatePublic(opponentKeys.getPublicKeyX509()));
        cipherIn.init(Cipher.DECRYPT_MODE, KeyFactory.getInstance(cryptAlgoritm.name()).generatePrivate(ourKeys.getPrivateKeyPKCS8()));
        signatureOut = Signature.getInstance(signatureAlgoritm.name());
        signatureIn = Signature.getInstance(signatureAlgoritm.name());
        signatureOut.initSign(KeyFactory.getInstance(cryptAlgoritm.name()).generatePrivate(ourKeys.getPrivateKeyPKCS8()));
        signatureIn.initVerify(KeyFactory.getInstance(cryptAlgoritm.name()).generatePublic(opponentKeys.getPublicKeyX509()));
        sessionCipherOut = Cipher.getInstance(sessionCryptAlgoritm.name() + "/" + sessionCryptAlgoritmMode.name() + "/" + sessionCryptPadding.name());
        sessionCipherIn = Cipher.getInstance(sessionCryptAlgoritm.name() + "/" + sessionCryptAlgoritmMode.name() + "/" + sessionCryptPadding.name());
    }
    
    public byte[] sign(byte[] data) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException {
        signatureOut.initSign(KeyFactory.getInstance(cryptAlgoritm.name()).generatePrivate(ourKeys.getPrivateKeyPKCS8()));
        signatureOut.update(data);
        return signatureOut.sign();
    }
    
    public void checkSign(byte[] data, byte[] sign) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, SignatureException {
        signatureIn.initVerify(KeyFactory.getInstance(cryptAlgoritm.name()).generatePublic(opponentKeys.getPublicKeyX509()));
        signatureIn.update(data);
        if(!signatureIn.verify(sign)) {
            throw new SignatureException("Signature check failed");
        }
    }
    
    public byte[] blocksChain(byte[]... blocks) {
        int length = 0;
        for(byte[] block : blocks) {
            length += block.length + 4;
        }
        ByteBuffer bb = ByteBuffer.allocate(length);
        for(byte[] block : blocks) {
            bb.putInt(block.length).put(block);
        }
        return bb.array();
    }
    
    public byte[][] blocksDechain(byte[] data) {
        ArrayList<byte[]> arr = new ArrayList<>();
        ByteBuffer bb = ByteBuffer.wrap(data);
        while(bb.hasRemaining()) {
            int length = bb.getInt();
            byte[] block = new byte[length];
            bb.get(block);
            arr.add(block);
        }
        return arr.toArray(new byte[0][0]);
    }
    
    public byte[] processAsymOutSigned(byte[] data) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException {
        byte[] dataEnc = cipherOut.doFinal(data);
        byte[] sign = sign(dataEnc);
        return blocksChain(dataEnc, sign);
    }
    
    public byte[] processAsymOut(byte[] data) throws IllegalBlockSizeException, BadPaddingException {
        return cipherOut.doFinal(data);
    }
    
    public byte[] processAsymInSigned(byte[] data) throws SignatureException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        byte[][] buf = blocksDechain(data);
        byte[] dataEnc = buf[0];
        byte[] sign = buf[1];
        checkSign(dataEnc, sign);
        return cipherIn.doFinal(dataEnc);
    }
    
    public byte[] processAsymIn(byte[] data) throws IllegalBlockSizeException, BadPaddingException {
        return cipherIn.doFinal(data);
    }
    
    public byte[] processSymOutSigned(byte[] data) throws IllegalBlockSizeException, BadPaddingException, SignatureException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        byte[] dataEnc = sessionCipherOut.doFinal(data);
        byte[] sign = sign(dataEnc);
        return blocksChain(dataEnc, sign);
    }
    
    public byte[] processSymInSigned(byte[] data) throws SignatureException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        byte[][] buf = blocksDechain(data);
        byte[] dataEnc = buf[0];
        byte[] sign = buf[1];
        checkSign(dataEnc, sign);
        return sessionCipherIn.doFinal(dataEnc);
    }
    
    public byte[] processSymOut(byte[] data) throws IllegalBlockSizeException, BadPaddingException {
        return sessionCipherOut.doFinal(data);
    }
    
    public byte[] processSymIn(byte[] data) throws IllegalBlockSizeException, BadPaddingException {
        return sessionCipherIn.doFinal(data);
    }
    
    public byte[] cryptAndSignSessionKey() throws IllegalBlockSizeException, BadPaddingException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        byte[] sessKeyEnc = sessionKey.getEncoded();
        byte[] keyPack = ByteBuffer.allocate(4 + sessionKeyIV.length + 4 + sessKeyEnc.length).putInt(sessionKeyIV.length).put(sessionKeyIV).putInt(sessKeyEnc.length).put(sessKeyEnc).array();
        byte[] cryptedKey = cipherOut.doFinal(keyPack);
        byte[] sign = sign(cryptedKey);
        return blocksChain(cryptedKey, sign);
    }
    
    public void decryptCheckAndLoadSessionKey(byte[] data) throws SignatureException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {
        byte[][] buf = blocksDechain(data);
        byte[] cryptedKey = buf[0];
        byte[] sign = buf[1];
        checkSign(cryptedKey, sign);
        byte[] keyPack = cipherIn.doFinal(cryptedKey);
        byte[][] buf1 = blocksDechain(keyPack);
        sessionKeyIV = buf1[0];
        byte[] key = buf1[1];
        sessionKey = new SecretKeySpec(key, sessionCryptAlgoritm.name());
        sessionCipherOut.init(Cipher.ENCRYPT_MODE, sessionKey, new IvParameterSpec(sessionKeyIV));
        sessionCipherIn.init(Cipher.DECRYPT_MODE, sessionKey, new IvParameterSpec(sessionKeyIV));
    }
    
    public byte[] createHandshake() throws IOException {
        Buffer settingsBuf = new Buffer(1024);
        try (BufferOutputStream settingsBufOut = new BufferOutputStream(settingsBuf);ObjectOutputStream settingsObjOut = new ObjectOutputStream(settingsBufOut)) {
            settingsObjOut.writeObject(cryptSettings);
            settingsObjOut.writeObject(sessionCryptSettings);
            settingsObjOut.writeInt(signatureAlgoritm.ordinal());
        }
        byte[] settings = settingsBuf.toByteArray();
        byte[] pubKey = ourKeys.getEncoded();
        return ByteBuffer.allocate(5 + 4 + settings.length + 4 + pubKey.length).put(handshakeMagic).putInt(settings.length).put(settings).putInt(pubKey.length).put(pubKey).array();
    }
    
    public byte[] exportPublicKey() {
        return ourKeys.getPublicKeyX509().getEncoded();
    }

    public KeyPairContainer getOurKeys() {
        return ourKeys;
    }

    public KeyPairContainer getOpponentKeys() {
        return opponentKeys;
    }
    
}
