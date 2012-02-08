/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author cleglaun
 */
public class OwsFileListModelImpl extends AbstractListModel implements OwsFileListModel {

    private final List<OwsFileBasic> files;

    public OwsFileListModelImpl() {
        this.files = new ArrayList<OwsFileBasic>();
    }

    public int getSize() {
        return this.files.size();
    }

    public Object getElementAt(int index) {
        return this.files.get(index);
    }

    public void updateAllItems(List<OwsFileBasic> newItems) {
        int oldSize = this.files.size();
        this.files.clear();
        super.fireIntervalRemoved(this, 0, oldSize);

        this.files.addAll(newItems);
        super.fireIntervalAdded(this, 0, this.files.size());

        orderByOwsTitleAsc();
    }

    public void orderByOwsTitleAsc() {
        Collections.sort(this.files, new Comparator<OwsFileBasic>() {

            public int compare(OwsFileBasic o1, OwsFileBasic o2) {
                return o1.getOwsTitle().compareTo(o2.getOwsTitle());
            }
        });
        super.fireContentsChanged(this, 0, this.files.size());
    }
}
