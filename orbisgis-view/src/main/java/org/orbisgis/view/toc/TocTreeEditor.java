/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.view.toc;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.renderer.se.Style;

public class TocTreeEditor implements TreeCellEditor {
	private JTree tree;
        private TocTreeEditorPanel lastEditedCell;
        private final static Logger LOGGER = Logger.getLogger("gui." + TocTreeEditor.class);
        
	public TocTreeEditor(JTree toc) {
		this.tree = toc;
	}

        @Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
                if(value instanceof ILayer) {
                        TocTreeEditorLayerPanel editedCell = new TocTreeEditorLayerPanel(tree,(ILayer) value);
                        lastEditedCell = editedCell;
                        return editedCell;
                } else if(value instanceof Style) {
                        TocTreeEditorStylePanel editedCell = new TocTreeEditorStylePanel(tree,(Style) value);
                        lastEditedCell = editedCell;
                        return editedCell;                        
                } else {
                        return null;
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
		return true;
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
                        if (lastEditedCell.getLabel().isEmpty()) {
                                return false;
                        } else {
                                if(lastEditedCell.getValue() instanceof ILayer) {
                                        ILayer l = (ILayer)lastEditedCell.getValue();
                                        try {
                                                l.setName(lastEditedCell.getLabel());
                                        } catch (LayerException e) {
                                                return false;
                                        }
                                        return true;
                                } else if(lastEditedCell.getValue() instanceof Style) {
                                        ((Style)lastEditedCell.getValue()).setName(lastEditedCell.getLabel());
                                        return true;
                                } else {
                                        //Unkown type
                                        LOGGER.debug("Unknown tree editor value");
                                        return false;
                                }
                        }
                } else {
                        return false;
                }
	}
}