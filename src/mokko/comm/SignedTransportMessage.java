/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.comm;

/**
 *
 * @author psyriccio
 */
public class SignedTransportMessage extends TransportMessage {
    
    private byte[] signature;

    public byte[] getSignature() {
        return signature;
    }

    public SignedTransportMessage() {
        super();
    }

    
    
}
