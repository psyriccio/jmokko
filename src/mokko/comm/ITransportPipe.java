/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.comm;

import java.io.IOException;

/**
 *
 * @author psyriccio
 */
public interface ITransportPipe {
    
    public String init(String id, byte[] initData) throws IOException;
    public void close(String sessionId) throws IOException;
    public boolean messageAvaible(String sessionId) throws IOException;
    public TransportMessage get(String sessionId) throws IOException;
    public void put(String sessionId, TransportMessage msg) throws IOException;
    
}
