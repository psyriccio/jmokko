/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.jaxb;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author psyriccio
 */
public class JAXBSchemaFileOutputResolver extends SchemaOutputResolver {

    @Override
    public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
        File dir = new File("JAXB");
        dir.mkdirs();
        return new StreamResult(new File(dir, suggestedFileName));
    }
    
}
