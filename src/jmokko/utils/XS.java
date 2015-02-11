/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.utils;

import com.thoughtworks.xstream.XStream;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 *
 * @author psyriccio
 */
public class XS {

    private static final XStream xstream = new XStream();
    
    static {
        try {
            xstream.registerConverter(new com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter(xstream.getMapper()));
            xstream.registerConverter(new com.thoughtworks.xstream.hibernate.converter.HibernatePersistentMapConverter(xstream.getMapper()));
            xstream.registerConverter(new com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedSetConverter(xstream.getMapper()));
            xstream.registerConverter(new com.thoughtworks.xstream.hibernate.converter.HibernateProxyConverter());
            Logger log = LogManager.getLogger();
            log.info("Initializing XStream serealization system by reflection info");
            Reflections reflections = new Reflections(
                    new ConfigurationBuilder()
                            .addUrls(
                                    ClasspathHelper.forPackage("serviceprofi.dao")
                            )
            );
            Set<Class<?>> xstreamClasses = reflections.getTypesAnnotatedWith(com.thoughtworks.xstream.annotations.XStreamAlias.class);
            xstreamClasses.stream().map((clazz) -> {
                log.info("XS: Added class " + clazz.getCanonicalName());
                return clazz;
            }).forEach((clazz) -> {
                xstream.processAnnotations(clazz);
            });
        } catch (Exception ex) {
            //
        }
    }

    public static XStream getXstream() {
        return xstream;
    }
    
}
