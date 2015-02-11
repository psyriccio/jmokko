/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author psyriccio
 */
public class SuffixFileNameFilter implements FilenameFilter {

    private String suffix;

    public SuffixFileNameFilter(String suffix) {
        this.suffix = suffix;
    }
    
    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(suffix);
    }
    
}
