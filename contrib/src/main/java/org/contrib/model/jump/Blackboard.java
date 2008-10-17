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

package org.contrib.model.jump;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
* String-to-Object map that anyone can use.
* For example, the Options dialog has a single instance, and
* it's stored on the Workbench Blackboard.
*/
public class Blackboard implements Cloneable, Serializable {
    private static final long serialVersionUID = 6504993615735124204L;
    private HashMap properties = new HashMap();

    /**
     * Used by Java2XML
     */
    public HashMap getProperties() {
        return properties;
    }

    /**
     * Used by Java2XML
     */
    public void setProperties(HashMap properties) {
        this.properties = properties;
    }

    public Blackboard put(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public Object get(String key) {
        return properties.get(key);
    }

    public Blackboard put(String key, boolean value) {
        put(key, new Boolean(value));
        return this;
    }
    public Blackboard putAll(Map properties) {
        this.properties.putAll(properties);
        return this;
    }

    public boolean get(String key, boolean defaultValue) {
        if (get(key) == null) {
            put(key, defaultValue);
        }

        return getBoolean(key);
    }

    public boolean getBoolean(String key) {
        return ((Boolean) get(key)).booleanValue();
    }

    public Blackboard put(String key, int value) {
        put(key, new Integer(value));
        return this;
    }

    public Blackboard put(String key, double value) {
        put(key, new Double(value));
        return this;
    }

    public double get(String key, double defaultValue) {
        if (get(key) == null) {
            put(key, defaultValue);
        }

        return getDouble(key);
    }

    public int get(String key, int defaultValue) {
        if (get(key) == null) {
            put(key, defaultValue);
        }

        return getInt(key);
    }

    public int getInt(String key) {
        return ((Integer) get(key)).intValue();
    }

    public double getDouble(String key) {
        return ((Double) get(key)).doubleValue();
    }

    public Object get(String key, Object defaultValue) {
        if (get(key) == null) {
            put(key, defaultValue);
        }

        return get(key);
    }
    
    public Object clone() {
        return new Blackboard().putAll(properties);
    }
}
