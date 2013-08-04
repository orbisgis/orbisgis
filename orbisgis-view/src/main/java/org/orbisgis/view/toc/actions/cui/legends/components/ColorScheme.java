/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */
package org.orbisgis.view.toc.actions.cui.legends.components;

import com.vividsolutions.jts.util.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

/**
 * The colour schemes were taken from the following sources:
 * <ul>
 * <li>
 * Visual Mining, Inc. "Charts--Color Palettes" 2003.
 * Available from http://chartworks.com/resources/palettes.html.
 * Internet; accessed 24 April 2003.
 * <li>
 * Brewer, Cindy and Harrower, Mark. "ColorBrewer".
 * Available from http://www.personal.psu.edu/faculty/c/a/cab38/ColorBrewerBeta2.html.
 * Internet; accessed 24 April 2003.
 * </ul>
 */
public class ColorScheme {
    private static ArrayList<String> rangeColorSchemeNames;
    private static ArrayList<String> discreteColorSchemeNames;
    private static Map<String,List<Color>> nameToColorsMap;

    /**
     * Builds the ColorScheme matching the given name.
     * @param name The name of the scheme
     * @return The ColorScheme
     */
    public static ColorScheme create(String name) {
        return new ColorScheme(name, nameToColorsMap().get(name));
    }

    /**
     * Fills the inner collections using the dedicated resource file.
     */
    private static void load() {
        rangeColorSchemeNames = new ArrayList<String>();
        discreteColorSchemeNames = new ArrayList<String>();
        nameToColorsMap = new HashMap<String,List<Color>>();
        InputStream stream = ColorScheme.class.getResourceAsStream("ColorScheme.txt");
        InputStreamReader br = new InputStreamReader(stream);
        LineIterator lineIterator = IOUtils.lineIterator(br);
        try{
            while(lineIterator.hasNext()){
                String line = lineIterator.next();
                add(line);
            }
        }  finally {
            LineIterator.closeQuietly(lineIterator);
        }
    }

    /**
     * Parses the given String and adds the results in the collections.
     * @param line The line to be parsed.
     */
    private static void add(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        String name = tokenizer.nextToken();
        boolean range = tokenizer.nextToken().equals("range");
        (range ? rangeColorSchemeNames : discreteColorSchemeNames).add(name);
        List<Color> list = new ArrayList<Color>();
        while (tokenizer.hasMoreTokens()) {
            String hex = tokenizer.nextToken();
            Assert.isTrue(hex.length() == 6, hex);
            list.add(Color.decode("#" + hex));
        }
        nameToColorsMap().put(name, list);
    }

    /**
     * retrieve the [Name -> List&lt;Color&gt;] mapping statically stored. If not already instanciated, it will
     * be created.
     * @return The color scheme mapping
     */
    private static Map<String,List<Color>> nameToColorsMap() {
        if (nameToColorsMap == null) {
            load();
        }
        return nameToColorsMap;
    }

    /**
     * Gets the names of the color schemes that can be used for interval classification
     * @return The names of the ranged color schemes.
     */
    public static List<String> rangeColorSchemeNames() {
        if (rangeColorSchemeNames == null) {
            load();
        }
        return Collections.unmodifiableList(rangeColorSchemeNames);
    }

    /**
     * Gets the names of the color schemes that can be used for discrete classification
     * @return The names of the discrete color schemes
     */
    public static List<String> discreteColorSchemeNames() {
        if (discreteColorSchemeNames == null) {
            load();
        }
        return Collections.unmodifiableList(discreteColorSchemeNames);
    }

    /**
     * The name of this scheme.
     */
    private String name;
    /**
     * The inner List of colors.
     */
    private List<Color> colors;
    /**
     * Used for iteration.
     */
    private int lastColorReturned = -1;

    /**
     * Builds a new ColorScheme
     * @param name The name of the scheme.
     * @param colors The colors registered in the scheme.
     */
    public ColorScheme(String name, Collection<Color> colors) {
        this.name = name;
        this.colors = new ArrayList<Color>(colors);
    }

    /**
     * Gets the index of the last returned color
     * @return The index of the last returned color.
     */
    public int getLastColorReturned() {
        return lastColorReturned;
    }

    /**
     * Sets the index of the (in theory...) last returned color.
     * @param lastColorReturned the index of the last returned color
     */
    public void setLastColorReturned(int lastColorReturned) {
        this.lastColorReturned = lastColorReturned;
    }

    /**
     * Gets the next color in the inner color collection. This methods loops when it reaches the end of the collection.
     * @return The next color.
     */
    public Color next() {
        lastColorReturned++;
        if (lastColorReturned >= colors.size()) {
            lastColorReturned = 0;
        }
        return colors.get(lastColorReturned);
    }

    /**
     * Gets the list of colors defined in this color scheme
     * @return The list of colors.
     */
    public List<Color> getColors() {
        return Collections.unmodifiableList(colors);
    }

    /**
     * Gets the name of this ColorScheme.
     * @return The name of this color scheme
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a subset of the palette, selecting {@code num} colours in the given palette.
     * @param num The desired number of colours
     * @return The colours in a List.
     */
    public List<Color> getSubset(int num){
        List<Color> destination = new ArrayList<Color>();

        List<Color> originalList = getColors();
        for (int i = 0; i < num; i++) {
            destination.add(originalList.get(
                    (int) Math.round(
                            i * originalList.size() / (double) num)));
        }
        return destination;
    }

}
