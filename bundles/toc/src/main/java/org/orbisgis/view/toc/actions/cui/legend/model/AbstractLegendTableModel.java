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
package org.orbisgis.view.toc.actions.cui.legend.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;

import javax.swing.table.AbstractTableModel;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * @author Alexis Guéganno
 */
public abstract class AbstractLegendTableModel<K ,U extends LineParameters>  extends AbstractTableModel {
    private final static int COLUMN_COUNT = 2;
    public final static int KEY_COLUMN = 1;
    public final static int PREVIEW_COLUMN = 0;

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == KEY_COLUMN) {
            return getKeyAt(rowIndex);
        } else if(columnIndex == PREVIEW_COLUMN){
            return getPreviewAt(rowIndex);
        }
        throw new IndexOutOfBoundsException("We did not found a column at index "+columnIndex+" !");
    }

    @Override
    public int getRowCount() {
        return getMappedLegend().size();
    }

    /**
     * Gets the key ar index {@code rowIndex}
     * @param rowIndex The row index.
     * @return the key stored at {@code rowIndex}.
     */
    protected K getKeyAt(int rowIndex){
        SortedSet<K> ts = getMappedLegend().keySet();
        Iterator<K> it = ts.iterator();
        int i=0;
        while(it.hasNext()){
            if(i==rowIndex){
                return it.next();
            } else {
                it.next();
                i++;
            }
        }
        throw new IndexOutOfBoundsException("We did not found a key at index "+rowIndex+" !");
    }

    /**
     * Gets the key associated to the preview at index {@code rowIndex}.
     * @param rowIndex The row index
     * @return The key associated to the preview we're interested in.
     */
    protected K getPreviewAt(int rowIndex) {
        return getKeyAt(rowIndex);
    }

    /**
     * Gets the inner MappedLegend.
     * @return The inner MappedLegend.
     */
    public abstract MappedLegend<K,U> getMappedLegend();
}
