/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer.se.parameter;

/**
 * <code>InterpolationPoint</code> are used to map a <code>Type</code> instance (the <b>value</b> to 
 * a <code>double</code> (the <b>data</b>.
 * @author Alexis Guéganno
 * @param <Type> 
 */
public class InterpolationPoint<Type extends SeParameter> implements Comparable {

    private double d;
    private Type v = null;
    
    /**
     * Get the value used for the interpolation.
     * @return 
     */
    public Type getValue() {
        return v;
    }

    /**
     * Set the value used for the interpolation.
     * @return 
     */
    public void setValue(Type value) {
        this.v = value;
    }
    
    /**
     * Get the data used for the interpolation.
     * @return 
     */
    public double getData() {
        return d;
    }

    /**
     * Get the data used for the interpolation.
     * @return 
     */
    public void setData(double data) {
        this.d = data;
    }

    /**
     * Compares this <code>InterpolationPoint&lt;Type></code> and o. They are compared
     * if o can be cast to a <code>InterpolationPoint&lt;Type></code>.
     * @param o
     * @return 
     * <ul><li><b>1</b> if <code>this.data > o.data</code></li>
     * <ul><li><b>0</b> if <code>this.data == o.data</code></li>
     * <ul><li><b>-1</b> if <code>this.data &lt; o.data</code></li>
     * @throws <code>ClassCastException</code> if <code>o</code> can't be cast to 
     * <code>InterpolationPoint&lt;Type></code>.
     */
    @Override
    public int compareTo(Object o) {
        InterpolationPoint<Type> ip = (InterpolationPoint<Type>) o;

        double l = ip.d;
        double r = this.d;

        if (l < r) {
            return 1;
        } else if (l > r) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.d) ^ (Double.doubleToLongBits(this.d) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof InterpolationPoint){
            InterpolationPoint ip = (InterpolationPoint) o;
            return this.d == ip.d;
        }
        return false;
    }

}
