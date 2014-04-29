/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.core.events;

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
