/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.swing.tables;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
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
 *
 * @author psyriccio
 */
public class AnnotationTableModel extends AbstractTableModel {
    
    private final Logger log = LogManager.getLogger(this.getClass().getName());
    private static final long serialVersionUID = 1L;
    private final List<Field> fieldsObjects;
    private final List<Class> fieldClasses;
    private final List<String> fieldCaptions;
    private final List<Boolean> fieldEditable;
    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, String>> valuesReplacements;
    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, String>> valuesReplacementsReverse;
    private List items;
    
    public AnnotationTableModel(Class clazz, List items, String appPackage) throws ClassNotFoundException {
        log.debug("Initializing AnnotationTableModel for class " + clazz.getName());
        this.fieldClasses = new ArrayList<>();
        this.fieldCaptions = new ArrayList<>();
        this.fieldsObjects = new ArrayList<>();
        this.fieldEditable = new ArrayList<>();
        this.items = items;
        this.valuesReplacements = new ConcurrentHashMap<>();
        this.valuesReplacementsReverse = new ConcurrentHashMap<>();
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
                fieldsObjects.add(fld);
                fieldClasses.add(fieldClass);
                fieldCaptions.add(fieldCaption);
                fieldEditable.add(fEd);
            }
        });
        log.debug("Searching @EnumDescriptionInsteadName, using ClassLoader: " + clazz.getClassLoader().toString());
        ConfigurationBuilder cfgBuilder = new ConfigurationBuilder();
        Set<URL> urls = ClasspathHelper.forPackage("mokko");
        urls.addAll(ClasspathHelper.forPackage(appPackage));
        cfgBuilder.addUrls(urls);
        Reflections reflections = new Reflections(cfgBuilder);
        Class enumDescINAnnotationClass = clazz.getClassLoader().loadClass("jmokko.swing.tables.EnumDescriptionInsteadName");
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(enumDescINAnnotationClass); //reflections.getTypesAnnotatedWith(mokko.swing.tables.EnumDescriptionInsteadName.class);
        for(Class cl : types) {
            log.debug("   " + cl.getName() + ", superClass: " + cl.getSuperclass().getName());
            if(cl.getSuperclass().equals(Enum.class)) {
                ConcurrentHashMap<String, String> repMap;
                ConcurrentHashMap<String, String> repMapRev;
                if(valuesReplacements.containsKey(cl)) {
                    repMap = valuesReplacements.get(cl);
                } else {
                    repMap = new ConcurrentHashMap<>();
                    valuesReplacements.put(cl, repMap);
                }
                if(valuesReplacementsReverse.containsKey(cl)) {
                    repMapRev = valuesReplacementsReverse.get(cl);
                } else {
                    repMapRev = new ConcurrentHashMap<>();
                    valuesReplacementsReverse.put(cl, repMapRev);
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
        return fieldsObjects.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object res = null;
        try {
            res = fieldsObjects.get(columnIndex).get(items.get(rowIndex));
            if(valuesReplacements.containsKey(fieldClasses.get(columnIndex))) {
                ConcurrentHashMap<String, String> repMap = valuesReplacements.get(fieldClasses.get(columnIndex));
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
            if(valuesReplacements.containsKey(fieldClasses.get(columnIndex))) {
                ConcurrentHashMap<String, String> repMapRev = valuesReplacementsReverse.get(fieldClasses.get(columnIndex));
                if(repMapRev.containsKey(aValue)) {
                    newValue = Enum.valueOf((Class<Enum>) fieldClasses.get(columnIndex), (String) aValue);
                }
            }
            fieldsObjects.get(columnIndex).set(items.get(rowIndex), newValue);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            log.catching(ex);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return fieldClasses.get(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return fieldCaptions.get(column);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return fieldEditable.get(columnIndex);
    }

    public List getItems() {
        return items;
    }
    
}
