/**
 * This software is released under the University of Illinois/Research and
 *  Academic Use License. See the LICENSE file in the root folder for details.
 * Copyright (c) 2016
 *
 * Developed by:
 * The Cognitive Computations Group
 * University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
/**
 * 
 */
package edu.illinois.cs.cogcomp.lbjava.examples.badges;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.illinois.cs.cogcomp.lbjava.parse.Parser;

/**
 * Reads documents, given a directory
 * 
 * @author Gourab Kundu
 * 
 */
public class BadgeDataReader implements Parser {

    private List<String> lines;

    private int currentline;
    private BufferedReader br;
    
    /**
     * 
     */
    public BadgeDataReader(String file) {
        lines = new ArrayList<String>();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String str = br.readLine();
            while (str != null) {
                lines.add(str);
                str = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            
        }
    }
    
    public void close() {
    }
    
    public Object next() {
        
        if (currentline == lines.size()) {
            return null;
        } else {
            currentline++;
            return lines.get(currentline - 1);
        }
    }
    
    public void reset() {
        currentline = 0;
    }
}
