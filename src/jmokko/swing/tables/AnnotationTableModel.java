/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.swing.tables;

import com.google.common.io.Resources;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.table.AbstractTableModel;
import jmokko.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 **
 * @author psyriccio
 */
public class AnnotationTableModel extends AbstractTableModel {
    
    private final Logger log = LogManager.getLogger(this.getClass().getName());
    private static final long serialVersionUID = 1L;
    private AnnotationTableModelCacheItem cache;
    private List items;
    
    public AnnotationTableModel(Class clazz, List items, String appPackage) throws ClassNotFoundException {
        log.debug("Initializing AnnotationTableModel for class " + clazz.getName());
        this.items = items;
        try {
            URL url = Resources.getResource("_atm_cache_" + clazz.getCanonicalName() + ".cch");
            try (ObjectInputStream objIn = new ObjectInputStream(url.openStream())) {
                cache = (AnnotationTableModelCacheItem) objIn.readObject();
                return;
            }
        } catch (Exception ex) {
            //
        }
        cache = new AnnotationTableModelCacheItem();
        log.debug("Searching @TableColumn and @Order");
        Class tableColumnAnnotationClass = clazz.getClassLoader().loadClass("jmokko.swing.tables.TableColumn");
        Class orderAnnotation = clazz.getClassLoader().loadClass("jmokko.swing.tables.Order");
        Set<Field> fieldsSet = ReflectionUtils.getAllFields(clazz, ReflectionUtils.withAnnotation(tableColumnAnnotationClass));
        HashMap<Field, Integer> fields = new HashMap<>();
        for(Field fld : fieldsSet) {
            int order = 0;
            Order orderAn = (Order) fld.getAnnotation(orderAnnotation);
            if(orderAn != null) {
                order = orderAn.value();
            }
            fields.put(fld, order);
            log.debug("   " + fld.getClass().getName() + ", order: " + Integer.toString(order));
        }
        fields = Utils.sortHashMapByValues(fields);
        fields.forEach((fld, order) -> {
            fld.setAccessible(true);
            if(fld.isAnnotationPresent(tableColumnAnnotationClass)) {
                Class fieldClass = fld.getType();
                TableColumn annotation = fld.getAnnotation(TableColumn.class);
                String fieldCaption = annotation.caption();
                Boolean fEd = annotation.editable();
                if(fieldCaption.isEmpty()) {
                    fieldCaption = fld.getName();
                }
                cache.getFieldsObjects().add(fld);
                cache.getFieldClasses().add(fieldClass);
                cache.getFieldCaptions().add(fieldCaption);
                cache.getFieldEditable().add(fEd);
            }
        });
        log.debug("Searching @EnumDescriptionInsteadName, using ClassLoader: " + clazz.getClassLoader().toString());
        ConfigurationBuilder cfgBuilder = new ConfigurationBuilder();
        Set<URL> urls = ClasspathHelper.forPackage("jmokko");
        urls.addAll(ClasspathHelper.forPackage(appPackage));
        cfgBuilder.addUrls(urls);
        Reflections reflections = new Reflections(cfgBuilder);
        Class enumDescINAnnotationClass = clazz.getClassLoader().loadClass("jmokko.swing.tables.EnumDescriptionInsteadName");
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(enumDescINAnnotationClass); //reflections.getTypesAnnotatedWith(jmokko.swing.tables.EnumDescriptionInsteadName.class);
        for(Class cl : types) {
            log.debug("   " + cl.getName() + ", superClass: " + cl.getSuperclass().getName());
            if(cl.getSuperclass().equals(Enum.class)) {
                ConcurrentHashMap<String, String> repMap;
                ConcurrentHashMap<String, String> repMapRev;
                if(cache.getValuesReplacements().containsKey(cl)) {
                    repMap = cache.getValuesReplacements().get(cl);
                } else {
                    repMap = new ConcurrentHashMap<>();
                    cache.getValuesReplacements().put(cl, repMap);
                }
                if(cache.getValuesReplacementsReverse().containsKey(cl)) {
                    repMapRev = cache.getValuesReplacementsReverse().get(cl);
                } else {
                    repMapRev = new ConcurrentHashMap<>();
                    cache.getValuesReplacementsReverse().put(cl, repMapRev);
                }
                Field[] fieldsArr = cl.getFields();
                for(Field fld : fieldsArr) {
                    log.debug("Get @Description for field " + fld.getName());
                    Class descriptionAnnotationClass = clazz.getClassLoader().loadClass("jmokko.swing.tables.Description");
                    if(fld.isAnnotationPresent(descriptionAnnotationClass)) {
                        Class fieldClass = fld.getType();
                        Description descr = fld.getAnnotation(Description.class);
                        repMap.put(fld.getName(), descr.value());
                        repMapRev.put(descr.value(), fld.getName());
                        log.debug("   " + descr.value());
                    }
                }
            }
        }
        try {
            try (FileOutputStream fout = new FileOutputStream(new File("_atm_cache_" + clazz.getCanonicalName() + ".cch")); ObjectOutputStream objOut = new ObjectOutputStream(fout)) {
                objOut.writeObject(cache);
            }
        } catch (FileNotFoundException ex1) {
            //
        } catch (IOException ex1) {
            //
        }

    }
    
    public void updateModel(List items) {
        this.items = items;
        fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() {
        return  items.size();
    }

    @Override
    public int getColumnCount() {
        return cache.getFieldsObjects().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object res = null;
        try {
            res = cache.getFieldsObjects().get(columnIndex).get(items.get(rowIndex));
            if(cache.getValuesReplacements().containsKey(cache.getFieldClasses().get(columnIndex))) {
                ConcurrentHashMap<String, String> repMap = cache.getValuesReplacements().get(cache.getFieldClasses().get(columnIndex));
                res = repMap.get(res.toString());
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            log.catching(ex);
        }
        return res;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            Object newValue = aValue;
            if(cache.getValuesReplacements().containsKey(cache.getFieldClasses().get(columnIndex))) {
                ConcurrentHashMap<String, String> repMapRev = cache.getValuesReplacementsReverse().get(cache.getFieldClasses().get(columnIndex));
                if(repMapRev.containsKey(aValue)) {
                    newValue = Enum.valueOf((Class<Enum>) cache.getFieldClasses().get(columnIndex), (String) aValue);
                }
            }
            cache.getFieldsObjects().get(columnIndex).set(items.get(rowIndex), newValue);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            log.catching(ex);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return cache.getFieldClasses().get(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return cache.getFieldCaptions().get(column);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return cache.getFieldEditable().get(columnIndex);
    }

    public List getItems() {
        return items;
    }
    
}
