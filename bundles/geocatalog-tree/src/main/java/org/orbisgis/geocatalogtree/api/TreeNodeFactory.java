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
package org.orbisgis.geocatalogtree.api;

import javax.swing.JTree;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Nicolas Fortin
 */
public interface TreeNodeFactory {
    /**
     * {@link GeoCatalogTreeNode#getNodeType()}
     * @return Parent node type of generated nodes
     */
    String[] getParentNodeType();

    /**
     * Add/Remove children of parent.
     * @param parent Parent node.
     * @param connection Active connection, do not close it.
     * @param tree JTree instance
     */
    void updateChildren(GeoCatalogTreeNode parent, Connection connection, JTree tree) throws SQLException;

    /**
     * Called before the value of a node is updated after a user edition.
     * @param node Edited node
     * @param newValue New label
     * @throws PropertyVetoException Throw if the new value is not accepted.
     */
    void nodeValueVetoableChange(GeoCatalogTreeNode node, String newValue) throws PropertyVetoException;

    /**
     * Called after the value of a node is updated after a user edition.
     * @param node Edited node
     * @param oldValue Old value of the node
     * @param newValue New value of the node
     */
    void nodeValueChange(GeoCatalogTreeNode node, String oldValue, String newValue);

    /**
     * Create transferable using tree selection
     * @param tree Tree where selection belongs to this factory
     * @return Transferable to send to external components.
     */
    Transferable createTransferable(JTree tree);
}
