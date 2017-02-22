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

package org.orbisgis.legend.structure.recode;

import org.orbisgis.coremap.renderer.se.parameter.Literal;
import org.orbisgis.coremap.renderer.se.parameter.Recode;
import org.orbisgis.coremap.renderer.se.parameter.SeParameter;
import org.orbisgis.coremap.renderer.se.parameter.ValueReference;
import org.orbisgis.coremap.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.coremap.renderer.se.parameter.string.StringParameter;
import org.orbisgis.legend.structure.parameter.ParameterLegend;
import org.orbisgis.legend.structure.recode.type.TypeEvent;
import org.orbisgis.legend.structure.recode.type.TypeListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This trait intends to provide some useful methods for the representation of parameters included in unique value
 * classifications. We can retrieve the {@link ValueReference} used to get data from the input source, change the field
 * on which the analysis is made. Finally, we can add listeners that will be notified when the type of the inner
 * parameter changes.
 *
 * @author Alexis, Sylvain PALOMINOS
 */
public abstract class RecodedLegend implements ParameterLegend {

    protected String field = "";
    private List<TypeListener> listeners = new ArrayList<>();

    /**
     * Gets the {@code ValueReference} used to retrieve the input values for this
     * value classification.
     */
    public ValueReference getValueReference() {
        SeParameter seParameter = getParameter();
        if (seParameter instanceof Recode) {
            StringParameter stringParameter = ((Recode) seParameter).getLookupValue();
            if(stringParameter instanceof ValueReference){
                return (ValueReference)stringParameter;
            }
            else{
                throw new ClassCastException("We're not working with an authorized Recode2String");
            }
        } else {
            return null;
        }
    }

    /**
     * Sets the field used to make the analysis
     *
     * @param s The new field name.
     */
    public void setField(String s){
        field = s;
        SeParameter seParameter = getParameter();
        if(getParameter() instanceof Recode){
            ((Recode)seParameter).setLookupValue(new StringAttribute(s));
        }
    }

    /**
     * Adds a listener that will be notified when fireTypeChanged() is called.
     *
     * @param typeListener The listener that will be added.
     */
    public void addListener(TypeListener typeListener) {
        listeners.add(typeListener);
    }

    /**
     * Notifies that the actual type of the inner {@code SeParameter} has changed.
     */
    public void fireTypeChanged() {
        TypeEvent typeEvent = new TypeEvent(this);
        for (TypeListener typeListener : listeners) {
            typeListener.typeChanged(typeEvent);
        }
    }

    /**
     * Accepts the given visitor.
     *
     * @param visitor A visitor for RecodedLegend instances.
     */
    public void acceptVisitor(RecodedParameterVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Gets the keys that define this RecodedLegend.
     *
     * @return The string keys in a Set.
     */
    public Set<String> getKeys() {
        SeParameter seParameter = getParameter();
        if (seParameter instanceof Literal) {
            return new HashSet<>();
        } else if (seParameter instanceof Recode) {
            return ((Recode) seParameter).getKeys();
        } else {
            return new HashSet<>();
        }
    }

    public String getField(){
        return field;
    }
}
