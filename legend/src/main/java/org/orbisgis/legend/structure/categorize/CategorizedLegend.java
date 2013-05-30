/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.legend.structure.categorize;

import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.ValueReference;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.legend.structure.parameter.AbstractAttributeLegend;
import org.orbisgis.legend.structure.parameter.ParameterLegend;
import org.orbisgis.legend.structure.recode.type.TypeEvent;
import org.orbisgis.legend.structure.recode.type.TypeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alexis
 */
public abstract class CategorizedLegend extends AbstractAttributeLegend implements ParameterLegend {

    protected String field = "";
    private List<TypeListener> listeners = new ArrayList<TypeListener>();

    @Override
    public ValueReference getValueReference() {
        SeParameter c = getParameter();
        if(c instanceof Categorize){
            RealParameter ret = ((Categorize) c).getLookupValue();
            if(ret instanceof ValueReference){
                return (ValueReference) ret;
            } else {
                throw new ClassCastException("We're not working with an authorized Categorize instance");
            }
        }
        return null;
    }

    /**
     * Gets the name of the field on which the analysis is performed.
     * @return The name of the analyzed field.
     */
    public String getField() {
        return field;
    }


    /**
     * Sets the name of the field on which the analysis is performed.
     * @param field The name of the analyzed field.
     */
    public void setField(String field) {
        this.field = field;
        SeParameter c = getParameter();
        if(c instanceof Categorize){
            RealParameter lookupValue = ((Categorize) c).getLookupValue();
            if(lookupValue instanceof RealAttribute){
                ((RealAttribute) lookupValue).setColumnName(field);
            }
        }
    }

    /**
    * Adds a listener that will be notified when {@link CategorizedLegend#   fireTypeChanged} is called.
    * @param l The listener that will be added.
    */
    public void addListener(TypeListener l){
        listeners.add(l);
    }

    /**
    * Notifies that the actual type of the inner {@code SeParameter} has changed.
    */
    public void fireTypeChanged(){
          TypeEvent te = new TypeEvent(this);
          for(TypeListener tl : listeners){
              tl.typeChanged(te);
          }
    }

    /**
     * Implementation of the visitor pattern.
     * @param cpv The external visitor.
     */
    public void acceptVisitor(CategorizedParameterVisitor cpv){
        cpv.visit(this);
    }
}
