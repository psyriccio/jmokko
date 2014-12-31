/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import mokko.crypt.CipherSettings;
import mokko.crypt.CryptAlgoritm;
import mokko.crypt.CryptAlgoritmMode;
import mokko.crypt.CryptPadding;
import mokko.crypt.KeyGenerator;
import mokko.crypt.KeyPairContainer;
import mokko.crypt.SecureSession;
import mokko.crypt.SignatureAlgoritm;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author psyriccio
 */
public class SecureSessionTest {
    
    public SecureSessionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test() throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, SignatureException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
        KeyPairContainer key1 = new KeyPairContainer(KeyGenerator.generateRSAKeyPair());
        KeyPairContainer key2 = new KeyPairContainer(KeyGenerator.generateRSAKeyPair());
        KeyPairContainer key1pub = new KeyPairContainer(new KeyPair(key1.getPublicKeyObj(), null));
        KeyPairContainer key2pub = new KeyPairContainer(new KeyPair(key2.getPublicKeyObj(), null));
        CipherSettings cryptSettings = new CipherSettings(CryptAlgoritm.RSA, CryptAlgoritmMode.ECB, CryptPadding.PKCS1Padding, 4096);
        CipherSettings sessionCryptSettings = new CipherSettings(CryptAlgoritm.AES, CryptAlgoritmMode.CBC, CryptPadding.PKCS5Padding, 256);
        SecureSession ss1 = new SecureSession(key1, key2pub, cryptSettings, sessionCryptSettings, SignatureAlgoritm.MD5withRSA);
        SecureSession ss2 = new SecureSession(key2, ss1.createHandshake());
        byte[] buf = "TESTTESTTEST".getBytes("UTF-8");
        byte[] bufBig = (UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString()).getBytes("UTF-8");
        ss1.init();
        ss2.init();
        ss1.generateSessionKey();
        ss2.decryptCheckAndLoadSessionKey(ss1.cryptAndSignSessionKey());
        byte[] buf1 = ss2.processAsymIn(ss1.processAsymOut(buf));
        byte[] buf2 = ss1.processAsymInSigned(ss2.processAsymOutSigned(buf));
        byte[] buf3 = ss2.processSymInSigned(ss1.processSymOutSigned(buf));
        byte[] buf4 = ss1.processSymIn(ss2.processSymOut(buf));
        byte[] bufBig1 = ss1.processSymIn(ss2.processSymOut(bufBig));
        byte[] bufBig2 = ss2.processSymIn(ss1.processSymOut(bufBig));
        byte[] bufBig3 = ss2.processSymIn(ss1.processSymOut(bufBig));
        byte[] bufBig4 = ss1.processSymIn(ss2.processSymOut(bufBig));
        byte[] bufBig5 = ss1.processSymIn(ss2.processSymOut(bufBig));
        assertArrayEquals(buf, buf1);
        assertArrayEquals(buf, buf2);
        assertArrayEquals(buf, buf3);
        assertArrayEquals(buf, buf4);
        assertArrayEquals(bufBig, bufBig1);
        assertArrayEquals(bufBig, bufBig2);
        assertArrayEquals(bufBig, bufBig3);
        assertArrayEquals(bufBig, bufBig4);
        assertArrayEquals(bufBig, bufBig5);
    }
}
