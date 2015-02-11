/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.crypt;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseMotionListener;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import jmokko.utils.Utils;

/**
 *
 * @author psyriccio
 */
public class SecureRandomGeneratorForm extends javax.swing.JDialog {

    private int oldX = 0;
    private int oldY = 0;
    private byte[] dataBuf = new byte[64];
    int count = 0;
    byte[] aSeed = new byte[8];
    int aSeedPos = 0;
    SecureRandom sRnd;
    boolean done = false;
    byte lastKeyTimeSeed = 0;
    ByteBuffer paramsBuffer = ByteBuffer.allocate(38);
    
    public void updateDataBufRepresentation() {
        String hex = Utils.bytesToHex(dataBuf);
        String txt = "<html>";
        for(int k = 0; k <= (hex.length()-1); k++) {
            txt = txt + hex.substring(k, k + 1);
            if( (k+1) % 2 != 1) {
                txt = txt + " ";
            }
            if( (k+1) % 32 == 0) {
                txt = txt + "<br>";
            }
        }
        txt = txt + "</html>";
        jXLabelDataBuf.setText(txt);
    }
    
    public void updateParamsBuf() {
        String hex = Utils.bytesToHex(paramsBuffer.array());
        String txt = "<html>";
        for(int k = 0; k <= (hex.length()-1); k++) {
            txt = txt + hex.substring(k, k + 1);
            if( (k+1) % 2 != 1) {
                txt = txt + " ";
            }
            if( (k+1) % 32 == 0) {
                txt = txt + "<br>";
            }
        }
        txt = txt + "</html>";
        jXLabelParameters.setText(txt);
    }
    
    public void updateSeed() {
        String hex = Utils.bytesToHex(aSeed);
        String txt = "<html>";
        for(int k = 0; k <= (hex.length()-1); k++) {
            txt = txt + hex.substring(k, k + 1);
            if( (k+1) % 2 != 1) {
                txt = txt + " ";
            }
        }
        txt = txt + "</html>";
        jXLabelSeed.setText(txt);
    }
    
    /**
     * Creates new form SecureRandomGeneratorForm
     * @param message
     * @param parameters
     */
    public SecureRandomGeneratorForm(String message, String parameters) {
        initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int locationX = (screenSize.width - getWidth()) / 2;
        int locationY = (screenSize.height - getHeight()) / 2;
        setBounds(locationX, locationY, getWidth(), getHeight());
        jXLabelMessage.setText(message);
        jXLabelParameters.setText(parameters);
        for(int k = 0; k <= 63; k++) {
            dataBuf[k] = 0;
        }
        jProgressBar1.setMaximum(100);
        jProgressBar1.setValue(0);
        sRnd = new SecureRandom();
        updateDataBufRepresentation();
    }

    public int generate() {
        int seed_a;
        return 0;
    }
    
    public void updateSeed(int x, int y) {
        int deltaX = oldX - x;
        int deltaY = oldY - y;
        oldX = x;
        oldY = y;
        paramsBuffer.clear();
        byte[] bX = ByteBuffer.allocate(4).putInt(deltaX).array();
        byte[] bY = ByteBuffer.allocate(4).putInt(deltaY).array();
        byte sX = (byte) (((bX[0] ^ bX[1]) ^ bX[2]) ^ bX[3]);
        byte sY = (byte) (((bY[0] ^ bY[1]) ^ bY[2]) ^ bY[3]);
        byte sS = (byte) (sX ^ sY);
        byte[] timeSeedVM = ByteBuffer.allocate(8).putLong(System.nanoTime()).array();
        sS = (byte) (sS ^ timeSeedVM[6]);
        sS = (byte) (sS ^ timeSeedVM[7]);
        int oldX_ = oldX;
        oldX = (oldX >> sS) ^ oldY;
        oldY = (oldY >> sS) ^ oldX_;
        aSeed[aSeedPos] = (byte) (aSeed[aSeedPos] ^ sS);
        paramsBuffer.putInt(deltaX).putInt(deltaY).putInt(sX).putInt(sY).putInt(sS).put(timeSeedVM);
        if(aSeedPos == 7) {
            updateSeed();
            sRnd.setSeed(aSeed);
            byte[] toBuf = new byte[64];
            sRnd.nextBytes(toBuf);
            for(int k = 0; k <= 63; k++) {
                dataBuf[k] = (byte) (dataBuf[k] ^ toBuf[k]);
            }
            aSeedPos = 0;
            if(count == 100) {
                sRnd.setSeed(dataBuf);
                sRnd.nextBytes(dataBuf);
                done = true;
                for(MouseMotionListener ml : jSeedPanel.getMouseMotionListeners()) {
                    jSeedPanel.removeMouseMotionListener(ml);
                }
                setVisible(false);
            } else {
                count++;
                jProgressBar1.setValue(count);
            }
        } else {
            aSeedPos++;
        }
        updateDataBufRepresentation();
        byte[] timeSeed = ByteBuffer.allocate(8).putLong(System.currentTimeMillis()).array();
        paramsBuffer.put(timeSeed);
        updateParamsBuf();
        oldX = oldX ^ timeSeed[7];
        oldY = oldY ^ timeSeed[6];
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeedPanel = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jXLabelMessage = new org.jdesktop.swingx.JXLabel();
        jXLabelParameters = new org.jdesktop.swingx.JXLabel();
        jXLabelDataBuf = new org.jdesktop.swingx.JXLabel();
        jXLabelSeed = new org.jdesktop.swingx.JXLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jSeedPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.textHighlight));
        jSeedPanel.setPreferredSize(new java.awt.Dimension(300, 300));
        jSeedPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                seedPanelMouseMovedAction(evt);
            }
        });
        jSeedPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                seedPanelKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jSeedPanelLayout = new javax.swing.GroupLayout(jSeedPanel);
        jSeedPanel.setLayout(jSeedPanelLayout);
        jSeedPanelLayout.setHorizontalGroup(
            jSeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 298, Short.MAX_VALUE)
        );
        jSeedPanelLayout.setVerticalGroup(
            jSeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 298, Short.MAX_VALUE)
        );

        jXLabelMessage.setText("<html>\nLine1<br>\nLine2<br>\nLine3\n</html>");
        jXLabelMessage.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jXLabelParameters.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.activeCaptionBorder));
        jXLabelParameters.setText("<html>\nLine1<br>\nLine2<br>\nLine3<br>\nLine4\n</html>");
        jXLabelParameters.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        jXLabelDataBuf.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.activeCaptionBorder));
        jXLabelDataBuf.setText("<html>\n0A 0B 0C 0D 0E 0F 00 FF 0A 0B 0C 0D 0E 0F 00 FF<br>\n0A 0B 0C 0D 0E 0F 00 FF 0A 0B 0C 0D 0E 0F 00 FF<br>\n0A 0B 0C 0D 0E 0F 00 FF 0A 0B 0C 0D 0E 0F 00 FF<br>\n0A 0B 0C 0D 0E 0F 00 FF 0A 0B 0C 0D 0E 0F 00 FF\n</html>");
        jXLabelDataBuf.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        jXLabelSeed.setBackground(java.awt.SystemColor.activeCaptionBorder);
        jXLabelSeed.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.activeCaptionBorder));
        jXLabelSeed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXLabelSeed.setText("-");
        jXLabelSeed.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jXLabelMessage)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jXLabelParameters, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jXLabelDataBuf, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jXLabelSeed, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 10, Short.MAX_VALUE))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSeedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jXLabelMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jXLabelParameters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXLabelSeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXLabelDataBuf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void seedPanelMouseMovedAction(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_seedPanelMouseMovedAction
        updateSeed(evt.getX(), evt.getY());
    }//GEN-LAST:event_seedPanelMouseMovedAction

    private void seedPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_seedPanelKeyPressed
        byte keyTimeSeed = ByteBuffer.allocate(8).putLong(System.currentTimeMillis()).array()[7];
        byte keyTimeSeedVM = ByteBuffer.allocate(8).putLong(System.nanoTime()).array()[7];
        int x = Math.abs(lastKeyTimeSeed - keyTimeSeed);
        int y = Math.abs(lastKeyTimeSeed - keyTimeSeedVM);
        x = (int) (x ^ evt.getWhen());
        y = y ^ evt.getExtendedKeyCode();
        byte[] aX = ByteBuffer.allocate(4).putInt(x).array();
        byte[] aY = ByteBuffer.allocate(4).putInt(y).array();
        updateSeed(aX[0], aY[0]);
        updateSeed(aX[1], aY[1]);
        updateSeed(aX[2], aY[2]);
        updateSeed(aX[3], aY[3]);
    }//GEN-LAST:event_seedPanelKeyPressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        seedPanelKeyPressed(evt);
    }//GEN-LAST:event_formKeyPressed

    public byte[] getDataBuf() {
        return dataBuf;
    }

    public synchronized boolean isDone() {
        return done;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JPanel jSeedPanel;
    private org.jdesktop.swingx.JXLabel jXLabelDataBuf;
    private org.jdesktop.swingx.JXLabel jXLabelMessage;
    private org.jdesktop.swingx.JXLabel jXLabelParameters;
    private org.jdesktop.swingx.JXLabel jXLabelSeed;
    // End of variables declaration//GEN-END:variables
}
