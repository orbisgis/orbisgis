/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */

package org.orbisgis.core.renderer.se.parameter;

/**
 * A "Map" entry, where values of type <code>Type</code> are mapped according to
 * <code>String</code> keys.
 * @author maxence, alexis
 * @param <Type> 
 */
public class MapItem<Type> implements Comparable {

    private Literal key = null;
    private Type value = null;

    /**
     * Create a new <code>MapItem</code>, where <code>value</code> is referenced by 
     * <code>key</code>.
     * @param value
     * @param key 
     */
    public MapItem(Type value, Literal key){
        this.value = value;
        this.key = key;
    }

    /**
     * Get the value stored in this <code>MapItem</code>
     * @return 
     */
    public Type getValue(){
        return value;
    }

    /**
     * Set the value stored in this <code>MapItem</code>.
     * @param value 
     */
    public void setValue(Type value){
        this.value = value;
    }

    /**
     * Get the key stored in this <code>MapItem</code>
     * @return 
     */
    public Literal getKey(){
        return key;
    }
    
    /**
     * Set the key stored in this <code>MapItem</code>.
     * @param value 
     */
    public void setKey(Literal key){
        this.key = key;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof MapItem){
            return o.hashCode() == this.hashCode();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int hasht = 5;
        final int hashm = 41;
        return hashm * hasht + (this.key != null ? this.key.hashCode() : 0);
    }

    @Override
    public int compareTo(Object o) {
        MapItem<Type> item = (MapItem<Type>) o;
        return item.toString().compareTo(this.key.toString());
    }

}
