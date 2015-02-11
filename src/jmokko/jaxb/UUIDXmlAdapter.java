/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.jaxb;

import java.util.UUID;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author psyriccio
 */
public class UUIDXmlAdapter extends XmlAdapter<String, UUID> {

    @Override
    public UUID unmarshal(String v) throws Exception {
        return UUID.fromString(v);
    }

    @Override
    public String marshal(UUID v) throws Exception {
        return v.toString();
    }

    
}
