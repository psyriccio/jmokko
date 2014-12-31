/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.tests;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author psyriccio
 */
public class RMIClientServerTest {
    
    public RMIClientServerTest() {
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
    public void test() throws UnknownHostException, InterruptedException, UnsupportedEncodingException, RemoteException {
        //NodeDescriptor srvNodeDesc = new NodeDescriptor("server");
        //NodeDescriptor cliNodeDesc = new NodeDescriptor("client");
        //RMIServerNode srvNode = new RMIServerNode(srvNodeDesc, InetAddress.getLocalHost(), 1111);
        //srvNode.start();
        //RMIClientNode cliNode = new RMIClientNode(cliNodeDesc, InetAddress.getLocalHost(), 1111);
        //cliNode.start();
        //cliNode.post(new TransportMessage(UUID.randomUUID(), UUID.randomUUID(), "YOYOYOYOYOYO".getBytes("UTF-8")));
        //Thread.sleep(1000);
        //Thread.sleep(1000);
        //Thread.sleep(1000);
        //Thread.sleep(1000);
        //assertArrayEquals(srvNode.getInQueue().poll().getBody(), "YOYOYOYOYOYO".getBytes("UTF-8"));
    }
}
