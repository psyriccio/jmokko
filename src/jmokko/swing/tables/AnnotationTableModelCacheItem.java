/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.swing.tables;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author psyriccio
 */
public class AnnotationTableModelCacheItem {

    private final List<Field> fieldsObjects;
    private final List<Class> fieldClasses;
    private final List<String> fieldCaptions;
    private final List<Boolean> fieldEditable;
    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, String>> valuesReplacements;
    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, String>> valuesReplacementsReverse;

    public AnnotationTableModelCacheItem() {
        this.fieldsObjects = new ArrayList<>();
        this.fieldClasses = new ArrayList<>();
        this.fieldCaptions = new ArrayList<>();
        this.fieldEditable = new ArrayList<>();
        this.valuesReplacements = new ConcurrentHashMap<>();
        this.valuesReplacementsReverse = new ConcurrentHashMap<>();
    }
    
    public AnnotationTableModelCacheItem(List<Field> fieldsObjects, List<Class> fieldClasses, List<String> fieldCaptions, List<Boolean> fieldEditable, ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, String>> valuesReplacements, ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, String>> valuesReplacementsReverse) {
        this.fieldsObjects = fieldsObjects;
        this.fieldClasses = fieldClasses;
        this.fieldCaptions = fieldCaptions;
        this.fieldEditable = fieldEditable;
        this.valuesReplacements = valuesReplacements;
        this.valuesReplacementsReverse = valuesReplacementsReverse;
    }
    
    public List<Field> getFieldsObjects() {
        return fieldsObjects;
    }

    public List<Class> getFieldClasses() {
        return fieldClasses;
    }

    public List<String> getFieldCaptions() {
        return fieldCaptions;
    }

    public List<Boolean> getFieldEditable() {
        return fieldEditable;
    }

    public ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, String>> getValuesReplacements() {
        return valuesReplacements;
    }

    public ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, String>> getValuesReplacementsReverse() {
        return valuesReplacementsReverse;
    }
    
}
