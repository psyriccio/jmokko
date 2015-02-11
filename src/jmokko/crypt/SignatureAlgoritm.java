/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.crypt;

/**
 *
 * @author psyriccio
 */
public enum SignatureAlgoritm {
    
    NONEwithRSA, 
    MD2withRSA, 
    MD5withRSA, 
    HA1withRSA, 
    SHA256withRSA, 
    SHA384withRSA, 
    SHA512withRSA, 
    NONEwithDSA, 
    SHA1withDSA, 
    NONEwithECDSA, 
    SHA1withECDSA, 
    SHA256withECDSA, 
    SHA384withECDSA, 
    SHA512withECDSA;
    
}
