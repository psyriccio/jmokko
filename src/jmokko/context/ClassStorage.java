/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.context;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author psyriccio
 */
public class ClassStorage {
    
    private ConcurrentHashMap<String, ClassContainer> classes;
    private ConcurrentHashMap<String, ClassContainer> resources;
    private ConcurrentHashMap<String, Integer> classesIndex;
    private ConcurrentHashMap<String, Integer> resourcesIndex;
    
    public ClassStorage() {
        classes = new ConcurrentHashMap<>();
        resources = new ConcurrentHashMap<>();
        classesIndex = new ConcurrentHashMap<>();
        resourcesIndex = new ConcurrentHashMap<>();
    }

    public ClassStorage(ConcurrentHashMap<String, ClassContainer> classes, ConcurrentHashMap<String, ClassContainer> resources) {
        this.classes = classes;
        this.resources = resources;
    }

        
    
}
