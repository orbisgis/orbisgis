/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.parameter.string;

import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.LiteralType;
import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.orbisgis.core.renderer.se.parameter.Literal;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * A {@code StringParameter} with a stored, inner {@code String} value.
 * @author alexis
 */
public class StringLiteral extends Literal implements StringParameter{

    private String v;
    private String[] restriction;

    /**
     * Instanciates a new {@code StringLiteral} with an empty inner {@code String} value.
     */
    public StringLiteral(){
        v = "";
    }

    /**
     * Instanciates a new {@code StringLiteral} with inner {@code String} set to {@code value}.
     */
    public StringLiteral(String value){
        v = value;
    }

    /**
     * Builds a new {@code StringLiteral} from the given {@code JAXBElement.}.
     */
    public StringLiteral(JAXBElement<LiteralType> l) {
        this(l.getValue().getContent().get(0).toString());
    }

    @Override
    public String getValue(DataSource sds, long fid){
        return v;
    }

    @Override
    public String getValue(Map<String, Value> feature){
        return v;
    }

    /**
     * Set the inner {@code String} of this {@code StringLiteral} to {@code value}.
     */
    public void setValue(String value){
        v = value;
    }

    @Override
    public String toString(){
        return v;
    }

    @Override
    public void setRestrictionTo(String[] list) {
        restriction = list.clone();
    }

    /**
     * Get the list of restrictions currently associated to this {@code StringLiteral}
     * @return 
     * The restrictions as an array of {@code String}
     */
    public String[] getRestriction(){
        return restriction;
    }

    @Override
    public boolean equals(Object o){
            return (o instanceof StringLiteral) ? v.equals(o.toString()) : false;
    }

    @Override
    public int hashCode() {
            int hash = 7;
            hash = 11 * hash + (this.v != null ? this.v.hashCode() : 0);
            return hash;
    }

    @Override
    public int compareTo(Object o) {
        StringLiteral st = (StringLiteral) o;
        return this.v.compareTo(st.v);
    }

}
