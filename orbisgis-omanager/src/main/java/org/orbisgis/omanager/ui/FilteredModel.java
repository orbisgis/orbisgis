package org.orbisgis.omanager.ui;

import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.apache.commons.collections.iterators.ListIteratorWrapper;

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

/**
 * Decorator to ListModel, enable filtering
 * @param <SubModel> ListModel implementation
 */
public class FilteredModel<SubModel extends ListModel> extends AbstractListModel {
    private SubModel subModel;
    private List<Integer> shownElements = null; // Filtered (visible) elements
    private List<Integer> subElementsToShown = null;
    private ItemFilter<SubModel> elementFilter;

    public FilteredModel(SubModel subModel) {
        this.subModel = subModel;
        subModel.addListDataListener(new SubListListener());
    }

    public int getSize() {
        return subModel.getSize();
    }
    public void setFilter(ItemFilter<SubModel> elementFilter) {

    }
    public Object getElementAt(int i) {
        return subModel.getElementAt(i);
    }

    /**
     * Convert sub model index to this model index.
     * @param i Sub element index
     * @return Shown index, null if filtered
     */
    private Integer subIndexToThisIndex(int i) {
        if(subElementsToShown==null) {
            return i;
        } else {
            return subElementsToShown.get(i);
        }
    }
    private class SubListListener implements ListDataListener {

        public void intervalAdded(ListDataEvent listDataEvent) {
            Integer deb = subIndexToThisIndex(listDataEvent.getIndex0());
            Integer end = subIndexToThisIndex(listDataEvent.getIndex1());
            fireIntervalAdded(this,deb,end);
        }

        public void intervalRemoved(ListDataEvent listDataEvent) {
            fireIntervalRemoved(this,subIndexToThisIndex(listDataEvent.getIndex0()),subIndexToThisIndex(listDataEvent.getIndex1()));
        }

        public void contentsChanged(ListDataEvent listDataEvent) {
            fireContentsChanged(this,subIndexToThisIndex(listDataEvent.getIndex0()),subIndexToThisIndex(listDataEvent.getIndex1()));
        }
    }
}
