/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.crypt;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import mokko.utils.fastforms.BusyForm;

/**
 *
 * @author psyriccio
 */
public class KeyGenerator {
    
    public static KeyPairContainer generate(String algoritm, int size) {
        
        SecureRandomGeneratorForm secRndForm = new SecureRandomGeneratorForm("<html>←<br>Перемещайте мышь внутри квадрата,<br> или нажимайте на клавиши,<br> для получения случайной информации,<br> необходимой для генерации ключей.</html>", "←←←");
        secRndForm.setModal(true);
        secRndForm.setVisible(true);
        
        BusyForm bf = new BusyForm(null, "Генерация ключей", "Идет генерация ключей, подождите...");
        class RunnableWork implements Runnable {

            private KeyPairContainer keyPair;
            private Exception exception;
            
            @Override
            public void run() {
                KeyPairGenerator kpGen;
                try {
                    kpGen = KeyPairGenerator.getInstance(algoritm);
                    SecureRandom sRnd = new SecureRandom(secRndForm.getDataBuf());
                    kpGen.initialize(size, sRnd);
                    keyPair = new KeyPairContainer(kpGen.generateKeyPair());
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(KeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        
        RunnableWork wrk = new RunnableWork();
        bf.run(wrk);
        return wrk.keyPair;
        
    }
    
    public static KeyPair generateRSAKeyPair() {

        SecureRandomGeneratorForm secRndForm = new SecureRandomGeneratorForm("<html>←<br>Перемещайте мышь внутри квадрата,<br> или нажимайте на клавиши,<br> для получения случайной информации,<br> необходимой для генерации ключей.</html>", "←←←");
        secRndForm.setModal(true);
        secRndForm.setVisible(true);
        
        BusyForm bf = new BusyForm(null, "Генерация ключей", "Идет генерация ключей, подождите...");
        class RunnableWork implements Runnable {

            private KeyPair keyPair;
            private Exception exception;
            
            @Override
            public void run() {
                KeyPairGenerator kpGen;
                try {
                    kpGen = KeyPairGenerator.getInstance("RSA");
                    SecureRandom sRnd = new SecureRandom(secRndForm.getDataBuf());
                    kpGen.initialize(4096, sRnd);
                    keyPair = kpGen.generateKeyPair();
                } catch (NoSuchAlgorithmException ex) {
                    keyPair = null;
                    exception = ex;
                }
            }
            
        }

        RunnableWork wrk = new RunnableWork();
        bf.run(wrk);
        return wrk.keyPair;
        
    }
    
}
