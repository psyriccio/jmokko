/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.jaxb;

import com.google.common.io.Resources;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static Map<String, Marshaller> marshallerCache = new ConcurrentHashMap<>();
    private static Map<String, Unmarshaller> unmarshallerCache = new ConcurrentHashMap<>();
    private static Map<String, JAXBContext> contextCache = new ConcurrentHashMap<>();
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
        if(!contextCache.containsKey("")) {
            ConfigurationBuilder confBuilder = new ConfigurationBuilder();
            confBuilder.addUrls(ClasspathHelper.forClassLoader());
            Set<Class<?>> types;
            try {
                URL url = Resources.getResource("_xml_class_cache.xml");
                try (ObjectInputStream objIn = new ObjectInputStream(url.openStream())) {
                    types = (Set<Class<?>>) objIn.readObject();
                }
            } catch (Exception ex) {
                Reflections reflections = new Reflections(confBuilder);
                types = reflections.getTypesAnnotatedWith(jmokko.jaxb.Xml.class);
                try {
                    try (FileOutputStream fout = new FileOutputStream(new File("_xml_class_cache.xml")); ObjectOutputStream objOut = new ObjectOutputStream(fout)) {
                        objOut.writeObject(types);
                    }
                } catch (FileNotFoundException ex1) {
                    //
                } catch (IOException ex1) {
                    //
                }
            }
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
            contextCache.put("", context);
        } else {
            context = contextCache.get("");
        }
        return context;
    }
    
    public static JAXBContext buildContext(String... packages) throws JAXBException {
        String cacheKey = "";
        for(String pack : packages) {
            cacheKey = cacheKey + pack + ";";
        }
        String resCacheKey = cacheKey.replace(";", "_");
        if(!contextCache.containsKey(cacheKey)) {
            ConfigurationBuilder confBuilder = new ConfigurationBuilder();
            for(String pack : packages) {
                confBuilder.addUrls(ClasspathHelper.forPackage(pack));
            }
            confBuilder.addUrls(ClasspathHelper.forClassLoader());
            Set<Class<?>> types;
            try {
                URL url = Resources.getResource("_xml_class_cache_" + resCacheKey + ".xml");
                try (ObjectInputStream objIn = new ObjectInputStream(url.openStream())) {
                    types = (Set<Class<?>>) objIn.readObject();
                }
            } catch (Exception ex) {
                Reflections reflections = new Reflections(confBuilder);
                types = reflections.getTypesAnnotatedWith(jmokko.jaxb.Xml.class);
                try {
                    try (FileOutputStream fout = new FileOutputStream(new File("_xml_class_cache_" + resCacheKey + ".xml")); ObjectOutputStream objOut = new ObjectOutputStream(fout)) {
                        objOut.writeObject(types);
                    }
                } catch (FileNotFoundException ex1) {
                    //
                } catch (IOException ex1) {
                    //
                }
            }
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
            contextCache.put(cacheKey, context);
        } else {
            context = contextCache.get(cacheKey);
        }
        return context;
    }

    public static Marshaller buildMarshaller() throws JAXBException {
        Marshaller marshaller;
        if(!marshallerCache.containsKey("")) {
            marshaller = buildContext().createMarshaller();
            if(defaultMarshallerProperties != null) {
                defaultMarshallerProperties.forEach((keys, value) -> {
                    try {
                        marshaller.setProperty(keys, value);
                    } catch (PropertyException ex) {
                        //none
                    }
                });
            }
            marshallerCache.put("", marshaller);
        } else {
            marshaller = marshallerCache.get("");
        }
        return marshaller;
    }
    
    public static Unmarshaller buildUnmarshaller() throws JAXBException {
        Unmarshaller unmarshaller;
        if(!unmarshallerCache.containsKey("")) {
            unmarshaller = buildContext().createUnmarshaller();
            if(defaultUnmarshallerProperties != null) {
                defaultUnmarshallerProperties.forEach((key, value) -> {
                    try {
                        unmarshaller.setProperty(key, value);
                    } catch (PropertyException ex) {
                        //none
                    }
                });
            }
            unmarshallerCache.put("", unmarshaller);
        } else {
            unmarshaller = unmarshallerCache.get("");
        }
        return unmarshaller;
    }

    public static Marshaller buildMarshaller(String... packages) throws JAXBException {
        String cacheKey = "";
        for(String pack : packages) {
            cacheKey = cacheKey + pack + ";";
        }
        Marshaller marshaller;
        if(!marshallerCache.containsKey(cacheKey)) {
            marshaller = buildContext(packages).createMarshaller();
            if(defaultMarshallerProperties != null) {
                defaultMarshallerProperties.forEach((keys, value) -> {
                    try {
                        marshaller.setProperty(keys, value);
                    } catch (PropertyException ex) {
                        //none
                    }
                });
            }
            marshallerCache.put(cacheKey, marshaller);
        } else {
            marshaller = marshallerCache.get(cacheKey);
        }
        return marshaller;
    }

    public static Unmarshaller buildUnmarshaller(String... packages) throws JAXBException {
        String cacheKey = "";
        for(String pack : packages) {
            cacheKey = cacheKey + pack + ";";
        }
        Unmarshaller unmarshaller;
        if(!unmarshallerCache.containsKey(cacheKey)) {
            unmarshaller = buildContext(packages).createUnmarshaller();
            if(defaultUnmarshallerProperties != null) {
                defaultUnmarshallerProperties.forEach((key, value) -> {
                    try {
                        unmarshaller.setProperty(key, value);
                    } catch (PropertyException ex) {
                        //none
                    }
                });
            }
            unmarshallerCache.put(cacheKey, unmarshaller);
        } else {
            unmarshaller = unmarshallerCache.get(cacheKey);
        }
        return unmarshaller;
    }

    public static Marshaller buildMarshaller(Map<String, ?> properties, String... packages) throws JAXBException {
        String cacheKey = "";
        for(String pack : packages) {
            cacheKey = cacheKey + pack + ";";
        }
        for(String key : properties.keySet()) {
            cacheKey = cacheKey + key + "=" + properties.get(key).toString();
        }
        Marshaller marshaller;
        if(!marshallerCache.containsKey(cacheKey)) {
            marshaller = buildMarshaller(packages);
            properties.forEach((key, value) -> {
                try {
                    marshaller.setProperty(key, value);
                } catch (PropertyException ex) {
                    //none
                }
            });
            marshallerCache.put(cacheKey, marshaller);
        } else {
            marshaller = marshallerCache.get(cacheKey);
        }
        return marshaller;
    }
    
    public static Unmarshaller buildUnmarshaller(Map<String, ?> properties, String... packages) throws JAXBException {
        String cacheKey = "";
        for(String pack : packages) {
            cacheKey = cacheKey + pack + ";";
        }
        for(String key : properties.keySet()) {
            cacheKey = cacheKey + key + "=" + properties.get(key).toString();
        }
        Unmarshaller unmarshaller;
        if(!unmarshallerCache.containsKey(cacheKey)) {
            unmarshaller = buildUnmarshaller(packages);
            properties.forEach((key, value) -> {
                try {
                    unmarshaller.setProperty(key, value);
                } catch (PropertyException ex) {
                    //none
                }
            });
            unmarshallerCache.put(cacheKey, unmarshaller);
        } else {
            unmarshaller = unmarshallerCache.get(cacheKey);
        }
        return unmarshaller;
    }

    public static Marshaller buildMarshaller(Map<String, ?> properties) throws JAXBException {
        String cacheKey = "";
        for(String key : properties.keySet()) {
            cacheKey = cacheKey + key + "=" + properties.get(key).toString();
        }
        Marshaller marshaller;
        if(!marshallerCache.containsKey(cacheKey)) {
            marshaller = buildMarshaller();
            properties.forEach((key, value) -> {
                try {
                    marshaller.setProperty(key, value);
                } catch (PropertyException ex) {
                    //none
                }
            });
            marshallerCache.put(cacheKey, marshaller);
        } else {
            marshaller = marshallerCache.get(cacheKey);
        }
        return marshaller;
    }
    
    public static Unmarshaller buildUnmarshaller(Map<String, ?> properties) throws JAXBException {
        String cacheKey = "";
        for(String key : properties.keySet()) {
            cacheKey = cacheKey + key + "=" + properties.get(key).toString();
        }
        Unmarshaller unmarshaller;
        if(!unmarshallerCache.containsKey(cacheKey)) {
            unmarshaller = buildUnmarshaller();
            properties.forEach((key, value) -> {
                try {
                    unmarshaller.setProperty(key, value);
                } catch (PropertyException ex) {
                    //none
                }
            });
            unmarshallerCache.put(cacheKey, unmarshaller);
        } else {
            unmarshaller = unmarshallerCache.get(cacheKey);
        }
        return unmarshaller;
    }
    
    public static void generateSchema() throws IOException {
        if(context != null) {
            context.generateSchema(new JAXBSchemaFileOutputResolver());
        }
    }
    
}
