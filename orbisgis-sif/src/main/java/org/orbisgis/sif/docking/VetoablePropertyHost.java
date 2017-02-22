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
package org.orbisgis.sif.docking;

import java.beans.VetoableChangeListener;

/**
 * Class that use the {@link java.beans.VetoableChangeSupport}
 * @author Nicolas Fortin
 */
public interface VetoablePropertyHost {
    /**
     * Add a listener that will be called before the property is updated.
     * The listener can throw a PropertyVetoException in order to cancel the property value update.
     * {@link java.beans.VetoableChangeSupport#addVetoableChangeListener(java.beans.VetoableChangeListener)}
     * @param vetoableChangeListener The vetoableChangeListener to add
     */
    void addVetoableChangeListener(VetoableChangeListener vetoableChangeListener);

    /**
     * Remove a listener.
     * {@link java.beans.VetoableChangeSupport#removeVetoableChangeListener(java.beans.VetoableChangeListener)}
     * @param vetoableChangeListener the instance to remove
     */
    void removeVetoableChangeListener(VetoableChangeListener vetoableChangeListener);

    /**
     * Add a listener that will be called before the property is updated.
     * The listener can throw a PropertyVetoException in order to cancel the property value update.
     * {@link java.beans.VetoableChangeSupport#addVetoableChangeListener(String, java.beans.VetoableChangeListener)}
     * @param s Property to listen
     * @param vetoableChangeListener The vetoableChangeListener to add
     */
    void addVetoableChangeListener(String s, VetoableChangeListener vetoableChangeListener);

    /**
     * Remove a listener.
     * {@link java.beans.VetoableChangeSupport#removeVetoableChangeListener(String, java.beans.VetoableChangeListener)}
     * @param s Remove the listen for a specific property
     * @param vetoableChangeListener the instance to remove
     */
    void removeVetoableChangeListener(String s, VetoableChangeListener vetoableChangeListener);
}
