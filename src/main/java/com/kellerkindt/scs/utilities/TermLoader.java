/*
 * ShowCaseStandalone - A Minecraft-Bukkit-API Shop Plugin
 * Copyright (C) 2016-08-16 22:43 +02 kellerkindt (Michael Watzko) <copyright at kellerkindt.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kellerkindt.scs.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.kellerkindt.scs.ShowCaseStandalone;

public class TermLoader {
    private static final String regexFilter = "(\\s+?|.+?)[ ]*:[ ]*\\\"(\\s+?|.*?)\\\"";
    private static final String regexKey    = "$1";
    private static final String regexValue  = "$2";
    private static final String encoding    = "UTF8";
    
    private TermLoader () {}
    
    /**
     * Loads the terms from the given File
     * @param file
     * @throws IOException
     */
    public static void loadTerms (File file) throws IOException {
        // init the input stream

        try (InputStream fis = new FileInputStream(file)) {
            // load the terms
            loadTerms(fis);
        }
    }
    
    /**
     * Loads the terms from the given InputStream
     * @param is    InputStream to load from
     * @throws IOException
     */
    public static void loadTerms (InputStream is) throws IOException {
        InputStreamReader        isr        = new InputStreamReader    (is, encoding);
        BufferedReader            br        = new BufferedReader    (isr);
        
        String                    line    = null;
        HashMap<String, String>    terms    = new HashMap<>();
        
        while ((line = br.readLine()) != null) {
            String key        = line.replaceAll(regexFilter, regexKey);
            String value    = line.replaceAll(regexFilter, regexValue);

            terms.put(key, value);
        }
        
        br.close();
        isr.close();
        
        for (Term term :Term.values()) {
            // get the term value
            String termValue = terms.get(term.toString());
            
            // check whether the value is valid
            if (termValue == null) {
                ShowCaseStandalone.get().getLogger().warning("Missing Term: "+term.toString());
            } else {
                term.setTerm(termValue);
            }
        }
    }
}
