package org.orbisgis.view.toc.actions.cui.legends.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.SortedSet;

/**
 * TableModel for categorized analysis.
 * @author Alexis Guéganno
 */
public class TableModelInterval<U extends LineParameters> extends AbstractLegendTableModel<Double,U> {
    /**
     * The column for intervals
     */
    public static final int INTERVAL_COLUMN = 2;
    /**
     * The number of digits we want to display
     */
    public static final int DIGITS_NUMBER = 4;
    private static final int TABLE_COLUMN_COUNT = 3;
    private AbstractCategorizedLegend<U> cat;
    private final static I18n I18N = I18nFactory.getI18n(TableModelInterval.class);
    private NumberFormat formatter;

    /**
     * Builds a new TableModelInterval model linked to the given legend
     * @param rl The input interval classification
     */
    public TableModelInterval(AbstractCategorizedLegend<U > rl){
        cat = rl;
        formatter = NumberFormat.getInstance(Locale.getDefault());
        formatter.setGroupingUsed(false);
        formatter.setMaximumFractionDigits(DIGITS_NUMBER);
    }

    @Override
    public int getColumnCount() {
        return TABLE_COLUMN_COUNT;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        return columnIndex < 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == KEY_COLUMN) {
            return getKeyAt(rowIndex);
        } else if(columnIndex == PREVIEW_COLUMN){
            return getPreviewAt(rowIndex);
        } else if(columnIndex == INTERVAL_COLUMN){
            SortedSet<Double> ts = getMappedLegend().keySet();
            Double d = getKeyAt(rowIndex);
            SortedSet<Double> tail = ts.tailSet(d);
            StringBuilder sb = new StringBuilder();
            sb.append("  ");
            if(Double.isInfinite(d)){
                sb.append("]");
                sb.append("-").append(Character.toString('\u221e')) ;
            }else {
                sb.append("[");
                String numS = Math.abs(d) < 1.0 ?
                        Double.toString(KeyCellRenderer.getRounded(d,DIGITS_NUMBER)):
                        formatter.format(d);
                sb.append(numS);
            }
            sb.append("; ");
            if(tail.size() == 1){
                sb.append("+").append(Character.toString('\u221e'));
            } else {
                Iterator<Double> it = tail.iterator();
                //next is d, we want the one next to d.
                it.next();
                Double nd = it.next();
                String numS = Math.abs(nd) < 1.0 ?
                        Double.toString(KeyCellRenderer.getRounded(nd,DIGITS_NUMBER)):
                        formatter.format(nd);
                sb.append(numS);
            }
            sb.append("[  ");
            return sb.toString();
        }
        throw new IndexOutOfBoundsException("We did not found a column at index "+columnIndex+" !");
    }

    @Override
    public String getColumnName(int col){
        if(col == KEY_COLUMN){
            return I18N.tr("Threshold");
        } else if(col == PREVIEW_COLUMN){
            return I18N.tr("Preview");
        } else if(col == INTERVAL_COLUMN){
            return I18N.tr("Label");
        }
        throw new IndexOutOfBoundsException("We did not found a column at index "+col+" !");
    }

    @Override
    public MappedLegend<Double,U> getMappedLegend() {
        return cat;
    }
}
