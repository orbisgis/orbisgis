package org.orbisgis.view.toc.actions.cui.legends.model;

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
