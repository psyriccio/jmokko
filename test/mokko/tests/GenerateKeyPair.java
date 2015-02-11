/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.tests;

import java.io.File;
import java.security.KeyPair;
import javax.xml.bind.JAXBException;
import jmokko.crypt.KeyGenerator;
import jmokko.crypt.KeyPairContainer;
import jmokko.jaxb.JAXB;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author psyriccio
 */
public class GenerateKeyPair {
    
    public GenerateKeyPair() {
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
    public void generate() throws JAXBException {
        KeyPair kp = KeyGenerator.generateRSAKeyPair();
        KeyPairContainer keys = new KeyPairContainer(kp);
        KeyPairContainer keysPub = new KeyPairContainer(keys.getAlgorithm(), keys.getPublicFormat(), keys.getPublicKey(), null, null);
        KeyPairContainer keysPrv = new KeyPairContainer(keys.getAlgorithm(), keys.getPrivateFormat(), keys.getPrivateKey(), null, null);
        JAXB.buildContext("mokko.tests").createMarshaller().marshal(keys, new File("key.pair.xml"));
        JAXB.buildContext("mokko.tests").createMarshaller().marshal(keysPub, new File("key.pub.xml"));
        JAXB.buildContext("mokko.tests").createMarshaller().marshal(keysPrv, new File("key.priv.xml"));
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
