/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.jaxb;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 *
 * @author psyriccio
 */
public class JAXB {
    
    private static Map<String, Object> defaultMarshallerProperties = null;
    private static Map<String, Object> defaultUnmarshallerProperties = null;
    private static JAXBContext context = null;
    
    public static void addDefaultMarshallerProperty(String name, Object value) {
        if(defaultMarshallerProperties == null) {
            defaultMarshallerProperties = new HashMap<>();
        }
        defaultMarshallerProperties.put(name, value);
    }
    
    public static void addDefaultUnmarshallerProperty(String name, Object value) {
        if(defaultUnmarshallerProperties == null) {
            defaultUnmarshallerProperties = new HashMap<>();
        }
        defaultUnmarshallerProperties.put(name, value);
    }
    
    public static void clearDefaultMarshallerProperties() {
        if(defaultMarshallerProperties != null) {
            defaultMarshallerProperties.clear();
        }
    }
    
    public static void clearDefaultUnmarshallerProperties() {
        if(defaultUnmarshallerProperties != null) {
            defaultUnmarshallerProperties.clear();
        }
    }
    
    public static JAXBContext buildContext() throws JAXBException {
        ConfigurationBuilder confBuilder = new ConfigurationBuilder();
        confBuilder.addUrls(ClasspathHelper.forClassLoader());
        Reflections reflections = new Reflections(confBuilder);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(jmokko.jaxb.Xml.class);
        List<Class> jaxbClasses = new ArrayList<>();
        for(Class cl : types) {
            if(cl.getSuperclass().equals(JAXBClassesCollection.class)) {
                for(Field fl : cl.getFields()) {
                   fl.setAccessible(true);
                   jaxbClasses.add(fl.getType());
                }
            } else {
                jaxbClasses.add(cl);
            }
        }
        context = JAXBContext.newInstance(jaxbClasses.toArray(new Class[0]));
        return context;
    }
    
    public static JAXBContext buildContext(String... packages) throws JAXBException {
        ConfigurationBuilder confBuilder = new ConfigurationBuilder();
        for(String pack : packages) {
            confBuilder.addUrls(ClasspathHelper.forPackage(pack));
        }
        confBuilder.addUrls(ClasspathHelper.forClassLoader());
        Reflections reflections = new Reflections(confBuilder);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(jmokko.jaxb.Xml.class);
        List<Class> jaxbClasses = new ArrayList<>();
        for(Class cl : types) {
            if(cl.getSuperclass().equals(JAXBClassesCollection.class)) {
                for(Field fl : cl.getDeclaredFields()) {
                   fl.setAccessible(true);
                   jaxbClasses.add(fl.getType());
                }
            } else {
                jaxbClasses.add(cl);
            }
        }
        context = JAXBContext.newInstance(jaxbClasses.toArray(new Class[0]));
        return context;
    }

    public static Marshaller buildMarshaller() throws JAXBException {
        Marshaller marshaller = buildContext().createMarshaller();
        if(defaultMarshallerProperties != null) {
            defaultMarshallerProperties.forEach((keys, value) -> {
                try {
                    marshaller.setProperty(keys, value);
                } catch (PropertyException ex) {
                    //none
                }
            });
        }
        return marshaller;
    }
    
    public static Unmarshaller buildUnmarshaller() throws JAXBException {
        Unmarshaller unmarshaller = buildContext().createUnmarshaller();
        if(defaultUnmarshallerProperties != null) {
            defaultUnmarshallerProperties.forEach((key, value) -> {
                try {
                    unmarshaller.setProperty(key, value);
                } catch (PropertyException ex) {
                    //none
                }
            });
        }
        return unmarshaller;
    }

    public static Marshaller buildMarshaller(String... packages) throws JAXBException {
        Marshaller marshaller = buildContext(packages).createMarshaller();
        if(defaultMarshallerProperties != null) {
            defaultMarshallerProperties.forEach((keys, value) -> {
                try {
                    marshaller.setProperty(keys, value);
                } catch (PropertyException ex) {
                    //none
                }
            });
        }
        return marshaller;
    }

    public static Unmarshaller buildUnmarshaller(String... packages) throws JAXBException {
        Unmarshaller unmarshaller = buildContext(packages).createUnmarshaller();
        if(defaultUnmarshallerProperties != null) {
            defaultUnmarshallerProperties.forEach((key, value) -> {
                try {
                    unmarshaller.setProperty(key, value);
                } catch (PropertyException ex) {
                    //none
                }
            });
        }
        return unmarshaller;
    }

    public static Marshaller buildMarshaller(Map<String, ?> properties, String... packages) throws JAXBException {
        Marshaller marshaller = buildMarshaller(packages);
        properties.forEach((key, value) -> {
            try {
                marshaller.setProperty(key, value);
            } catch (PropertyException ex) {
                //none
            }
        });
        return marshaller;
    }
    
    public static Unmarshaller buildUnmarshaller(Map<String, ?> properties, String... packages) throws JAXBException {
        Unmarshaller unmarshaller = buildUnmarshaller(packages);
        properties.forEach((key, value) -> {
            try {
                unmarshaller.setProperty(key, value);
            } catch (PropertyException ex) {
                //none
            }
        });
        return unmarshaller;
    }

    public static Marshaller buildMarshaller(Map<String, ?> properties) throws JAXBException {
        Marshaller marshaller = buildMarshaller();
        properties.forEach((key, value) -> {
            try {
                marshaller.setProperty(key, value);
            } catch (PropertyException ex) {
                //none
            }
        });
        return marshaller;
    }
    
    public static Unmarshaller buildUnmarshaller(Map<String, ?> properties) throws JAXBException {
        Unmarshaller unmarshaller = buildUnmarshaller();
        properties.forEach((key, value) -> {
            try {
                unmarshaller.setProperty(key, value);
            } catch (PropertyException ex) {
                //none
            }
        });
        return unmarshaller;
    }
    
    public static void generateSchema() throws IOException {
        if(context != null) {
            context.generateSchema(new JAXBSchemaFileOutputResolver());
        }
    }
    
}
