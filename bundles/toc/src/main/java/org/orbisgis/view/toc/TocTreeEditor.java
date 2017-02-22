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
package org.orbisgis.view.toc;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;

import org.orbisgis.tocapi.TocTreeNodeLayer;
import org.orbisgis.tocapi.TocTreeNodeStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.renderer.se.Style;

public class TocTreeEditor implements TreeCellEditor {
        private TocTreeEditorPanel lastEditedCell;
        private static final Logger LOGGER = LoggerFactory.getLogger("gui." + TocTreeEditor.class);
        
    @Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
                if(value instanceof TocTreeNodeLayer) {
                        TocTreeEditorLayerPanel editedCell = new TocTreeEditorLayerPanel(tree,((TocTreeNodeLayer) value).getLayer());
                        lastEditedCell = editedCell;
                        return editedCell;
                } else if(value instanceof TocTreeNodeStyle) {
                        TocTreeEditorStylePanel editedCell = new TocTreeEditorStylePanel(tree,((TocTreeNodeStyle) value).getStyle());
                        lastEditedCell = editedCell;
                        return editedCell;                        
                } else {
                        throw new IllegalArgumentException("A tree cell editor for the provided node type is not found");
                }
	}

        @Override
	public void addCellEditorListener(CellEditorListener l) {

	}

        @Override
	public void cancelCellEditing() {

	}

        @Override
	public Object getCellEditorValue() {
                if(lastEditedCell!=null) {
                        return lastEditedCell.getLabel();
                } else {
                        return "";
                }
	}

        @Override
	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) anEvent;
			if (me.getClickCount() >= 2) {
                                return true;
                        }
		}
		return false;
	}

        @Override
	public void removeCellEditorListener(CellEditorListener l) {

	}

        @Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return false;
	}

        @Override
	public boolean stopCellEditing() {
                if(lastEditedCell!=null) {
                        if(lastEditedCell.getValue() instanceof ILayer) {
                                ILayer l = (ILayer)lastEditedCell.getValue();
                                //A layer with a data source can be set with an empty name
                            return !(lastEditedCell.getLabel().isEmpty() && l.getTableReference() == null);
                        } else if(lastEditedCell.getValue() instanceof Style) {
                                return !lastEditedCell.getLabel().isEmpty();
                        } else {
                                //Unkown type
                                LOGGER.debug("Unknown tree editor value");
                                return false;
                        }
                        
                } else {
                        return false;
                }
	}
}
