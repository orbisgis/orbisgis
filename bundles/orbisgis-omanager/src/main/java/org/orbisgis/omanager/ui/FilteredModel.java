/*
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
package org.orbisgis.omanager.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Decorator to ListModel, enable filtering
 * @param <SubModel> ListModel implementation
 */
public class FilteredModel<SubModel extends ListModel> extends AbstractListModel {
    private SubModel subModel;
    private List<Integer> shownElements = null;      // Filtered (visible) elements
    private ItemFilter<SubModel> elementFilter;

    public FilteredModel(SubModel subModel) {
        this.subModel = subModel;
        subModel.addListDataListener(new SubModelListener());
    }

    public int getSize() {
        if(elementFilter==null) {
            return subModel.getSize();
        } else {
            return shownElements.size();
        }
    }

    /**
     * Set the list filter and update the content.
     * @param elementFilter
     */
    public void setFilter(ItemFilter<SubModel> elementFilter) {
        this.elementFilter = elementFilter;
        shownElements = new ArrayList<Integer>();
        doFilter();
    }
    public Object getElementAt(int i) {
        if(elementFilter==null) {
            return subModel.getElementAt(i);
        } else {
            return subModel.getElementAt(shownElements.get(i));
        }
    }

    /**
     * Reapply the filter
     */
    public void doFilter() {
        if(elementFilter==null) {
            return;
        }
        if(getSize()>0) {
            fireIntervalRemoved(this, 0, getSize() - 1);
        }
        shownElements.clear();
        for(int i=0;i<subModel.getSize();i++)
        {
            if(elementFilter.include(subModel,i)) {
                shownElements.add(i);
            }
        }
        if(getSize()>0) {
            fireIntervalAdded(this,0,getSize()-1);
        }
    }

    /**
     * Propagate ListModel updates.
     */
    private class SubModelListener implements ListDataListener {
        public void intervalAdded(ListDataEvent listDataEvent) {
            if(elementFilter==null) {
                fireIntervalAdded(this,listDataEvent.getIndex0(),listDataEvent.getIndex1());
            } else {
                doFilter();
            }
        }

        public void intervalRemoved(ListDataEvent listDataEvent) {
            if(elementFilter==null) {
                fireIntervalRemoved(this, listDataEvent.getIndex0(), listDataEvent.getIndex1());
            } else {
                doFilter();
            }
        }

        public void contentsChanged(ListDataEvent listDataEvent) {
            if(elementFilter==null) {
                fireContentsChanged(this, listDataEvent.getIndex0(), listDataEvent.getIndex1());
            } else {
                doFilter();
            }
        }
    }
}
