/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.crypt;

/**
 *
 * @author psyriccio
 */
public enum CryptPadding {
    
    NoPadding, 
    ISO10126Padding, 
    OAEPPadding, 
    PKCS1Padding, 
    PKCS5Padding, 
    SSL3Padding;
    
}
