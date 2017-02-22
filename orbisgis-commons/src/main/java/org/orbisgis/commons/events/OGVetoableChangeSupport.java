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
package org.orbisgis.commons.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;

/**
 * This class fixes a problem with the {@link VetoableChangeSupport} when it returns a RuntimeException instead of a PropertyVetoException
 * @author Nicolas Fortin
 */
public class OGVetoableChangeSupport extends VetoableChangeSupport {

        
        public OGVetoableChangeSupport(Object sourceBean) {
                super(sourceBean);
        }

        @Override
        public void fireVetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                try {
                        super.fireVetoableChange(evt);
                } catch (RuntimeException re) {
                        throw (PropertyVetoException) re.getCause();
                }

        }

        @Override
        public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
                try {
                        super.fireVetoableChange(propertyName, oldValue, newValue);
                } catch (RuntimeException re) {
                        throw (PropertyVetoException) re.getCause();
                }
        }

        @Override
        public void fireVetoableChange(String propertyName, boolean oldValue, boolean newValue) throws PropertyVetoException {
                try {
                        super.fireVetoableChange(propertyName, oldValue, newValue);
                } catch (RuntimeException re) {
                        throw (PropertyVetoException) re.getCause();
                }
        }

        @Override
        public void fireVetoableChange(String propertyName, int oldValue, int newValue) throws PropertyVetoException {
                try {
                        super.fireVetoableChange(propertyName, oldValue, newValue);
                } catch (RuntimeException re) {
                        throw (PropertyVetoException) re.getCause();
                }

        }
}
