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
package org.orbisgis.view.toc.actions.cui.legends.model;

import org.orbisgis.legend.thematic.recode.RecodedLine;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.table.AbstractTableModel;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Table model for recoded lines.
 * @author alexis
 */
public class TableModelRecodedLine extends AbstractTableModel{
    private final static I18n I18N = I18nFactory.getI18n(TableModelRecodedLine.class);

    private RecodedLine recodedLine;
    private final static int COLUMN_COUNT = 2;
    public final static int KEY_COLUMN = 1;
    public final static int PREVIEW_COLUMN = 0;

    /**
     * Builds a new {@code TableModelRecodedLine} linker to {@code rl}.
     * @param rl
     */
    public TableModelRecodedLine(RecodedLine rl){
        recodedLine = rl;
    }

    @Override
    public String getColumnName(int col){
        if(col == KEY_COLUMN){
            return I18N.tr("Value");
        } else if(col == PREVIEW_COLUMN){
            return I18N.tr("Preview");
        }
        throw new IndexOutOfBoundsException("We did not found a column at index "+col+" !");
    }

    @Override
    public int getRowCount() {
        return recodedLine.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
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

    /**
     * Gets the associated unique value.
     * @return   The associated unique value.
     */
    public RecodedLine getRecodedLine(){
        return recodedLine;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        return true;
    }

    private String getPreviewAt(int rowIndex) {
        return getKeyAt(rowIndex);
    }

    private String getKeyAt(int rowIndex){
        SortedSet<String> ts = recodedLine.keySet();
        Iterator<String> it = ts.iterator();
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
}
