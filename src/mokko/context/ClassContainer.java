/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 *
 * @author psyriccio
 */
public class ClassContainer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String libName;
    private byte[] data;

    public static ClassContainer deserialize(byte[] data) {
        return new ClassContainer(data);
    }
    
    public static ClassContainer loadFromStream(ObjectInputStream inStream) throws IOException {
        int len = inStream.readInt();
        byte[] buf = new byte[len];
        inStream.readFully(buf);
        return new ClassContainer(buf);
    }
    
    public ClassContainer(String libName, String name, byte[] data) {
        this.libName = libName;
        this.name = name;
        this.data = data;
    }

    public ClassContainer(byte[] serialized) {
        try {
            ByteBuffer buf = ByteBuffer.wrap(serialized);
            int len = buf.getInt();
            byte[] nm = new byte[len];
            buf.get(nm);
            this.libName = new String(nm, "UTF-8");
            len = buf.getInt();
            nm = new byte[len];
            buf.get(nm);
            this.name = new String(nm, "UTF-8");
            len = buf.getInt();
            this.data = new byte[len];
            buf.get(this.data);
        } catch (UnsupportedEncodingException ex) {
            //none
        }
    }

    public String getLibName() {
        return libName;
    }
    
    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] serialize() {
        try {
            byte[] libNameBuf = libName.getBytes("UTF-8");
            byte[] nameBuf = name.getBytes("UTF-8");
            return ByteBuffer.allocate(4 + libNameBuf.length + 4 + nameBuf.length + 4 + data.length).putInt(libNameBuf.length).put(libNameBuf).putInt(nameBuf.length).put(nameBuf).putInt(data.length).put(data).array();
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }
    
    public void writeToStream(ObjectOutputStream outStream) throws IOException {
        byte[] buf = serialize();
        outStream.writeInt(buf.length);
        outStream.write(buf);
    }
    
}
