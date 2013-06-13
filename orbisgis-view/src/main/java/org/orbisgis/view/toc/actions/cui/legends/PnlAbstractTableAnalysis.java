package org.orbisgis.view.toc.actions.cui.legends;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.model.PreviewCellRenderer;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;

/**
 * @author Alexis Guéganno
 */
public abstract class PnlAbstractTableAnalysis<K, U extends LineParameters> extends AbstractFieldPanel implements ILegendPanel, ActionListener {
    public static final String FALLBACK = "Fallback";
    public static final String CREATE_CLASSIF = "Create classification";
    public static final Logger LOGGER = Logger.getLogger(PnlAbstractTableAnalysis.class);
    public final static int CELL_PREVIEW_WIDTH = CanvasSE.WIDTH/2;
    public final static int CELL_PREVIEW_HEIGHT = CanvasSE.HEIGHT/2;
    private static final I18n I18N = I18nFactory.getI18n(PnlAbstractTableAnalysis.class);
    private MappedLegend<K,U> legend;
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private DataSource ds;
    private JTable table;
    private String id;

    @Override
    public void initialize(LegendContext lc) {
        if (getLegend() == null) {
            setLegend(getEmptyAnalysis());
            initPreview();
        }
        setGeometryType(lc.getGeometryType());
        ILayer layer = lc.getLayer();
        if (layer != null && layer.getDataSource() != null) {
            setDataSource(layer.getDataSource());
        }
    }

    protected void setLegendImpl(MappedLegend<K,U> leg){
        this.legend =  leg;
    }

    /**
     * Creates and fill the combo box that will be used to compute the
     * analysis.
     *
     * @return  A ComboBox linked to the underlying MappedLegend that configures the analysis field.
     */
    public JComboBox getFieldComboBox() {
        if (ds != null) {
            JComboBox jcc = getFieldCombo(ds);
            ActionListener acl2 = EventHandler.create(ActionListener.class,
                    this, "updateField", "source.selectedItem");
            String field = legend.getLookupFieldName();
            if (field != null && !field.isEmpty()) {
                jcc.setSelectedItem(field);
            }
            jcc.addActionListener(acl2);
            updateField((String) jcc.getSelectedItem());
            return jcc;
        } else {
            return new JComboBox();
        }
    }

    /**
     * Sets the associated data source
     * @param newDS the new {@link org.gdms.data.DataSource}.
     */
    protected void setDataSource(DataSource newDS){
        ds = newDS;
    }

    /**
     * Gets the associated DataSource
     * @return The inner DataSource.
     */
    public DataSource getDataSource(){
        return ds;
    }

    /**
     * Used when the field against which the analysis is made changes.
     *
     * @param obj The new field.
     */
    public void updateField(String obj) {
        legend.setLookupFieldName(obj);
    }

    @Override
    public Legend getLegend() {
        return legend;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ADD)){
            K key = getNotUsedKey();
            U lp = legend.getFallbackParameters();
            legend.put(key, lp);
            updateTable();
        } else if (e.getActionCommand().equals(REMOVE)){
            int col = getJTable().getSelectedColumn();
            int row = getJTable().getSelectedRow();
            if(col>=0 && row >= 0){
                K key = (K)getJTable().getValueAt(row, col);
                legend.remove(key);
                updateTable();
            }
        }
    }


    /**
     * Creates the two buttons add and remove, links them to this through actions and put them in a JPanel.
     * @return the two buttons in a JPanel.
     */
    public JPanel getButtonsPanel(){
        JPanel jp = new JPanel();
        JButton jb1 = new JButton(I18N.tr("Add"));
        jb1.setActionCommand(ADD);
        jb1.addActionListener(this);
        jp.add(jb1);
        jp.setAlignmentX((float) .5);
        JButton remove = new JButton(I18N.tr("Remove"));
        remove.setActionCommand(REMOVE);
        remove.addActionListener(this);
        jp.add(jb1);
        jp.add(remove);
        jp.setAlignmentX((float) .5);
        return jp;
    }

    /**
     * Gets the JTable used to draw the mapping
     * @return the JTable
     */
    public JTable getJTable(){
        return table;
    }
    /**
     * Build the panel that contains the JTable where the map is displayed.
     * @return The panel that contains the JTable where the map is displayed.
     */
    public JPanel getTablePanel() {
        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        jp.setBorder(BorderFactory.createTitledBorder(getTitleBorder()));
        //we build the table here
        AbstractTableModel model = getTableModel();
        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        table.setRowHeight(CELL_PREVIEW_HEIGHT);
        final int previewWidth = CELL_PREVIEW_WIDTH;
        TableColumn previews = table.getColumnModel().getColumn(getPreviewColumn());
        previews.setWidth(previewWidth);
        previews.setMinWidth(previewWidth);
        previews.setMaxWidth(previewWidth);
        previews.setCellRenderer(new PreviewCellRenderer(table, getPreviewClass(), ((MappedLegend<K, U>) getLegend())));
        previews.setCellEditor(getParametersCellEditor());
        //We put a default editor on the keys.
        TableColumn keys = table.getColumnModel().getColumn(getKeyColumn());
        TableCellEditor ker = getKeyCellEditor();
        CellEditorListener cel = EventHandler.create(CellEditorListener.class, model, "fireTableDataChanged", null, "editingStopped");
        ker.addCellEditorListener(cel);
        keys.setCellEditor(ker);
        JScrollPane jsp = new JScrollPane(table);
        // Set the viewport to view 6 rows with a width of 400 pixels.
        int rowHeight = table.getRowHeight();
        int tableWidth = 400;
        int tableHeight = rowHeight*6;
        table.setPreferredScrollableViewportSize(
                new Dimension(tableWidth, tableHeight));
        table.setDoubleBuffered(true);
        jsp.setAlignmentX((float).5);
        jp.add(jsp, BorderLayout.CENTER);
        table.doLayout();
        jp.add(getButtonsPanel());
        // Set the unit (click once on down arrow: scroll down one row)
        // and block (one mouse scroll wheel: scroll down one half page)
        // increments.
        JScrollBar verticalScrollBar = jsp.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(rowHeight);
        verticalScrollBar.setBlockIncrement(tableHeight);
        // Set the scroll mode.
        jsp.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        return jp;
    }

    protected abstract String getTitleBorder();

    /**
     * Replaces the inner JTable.
     * @param t The new JTable.
     */
    protected void setJTable(JTable t){
        table = t;
    }

    /**
     * Update the table if it is not null.
     */
    public void updateTable(){
        JTable table = getJTable();
        if(table != null){
            AbstractTableModel model = (AbstractTableModel) table.getModel();
            model.fireTableDataChanged();
        }
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Update the inner CanvasSE. It updates its symbolizer and forced the image to be redrawn.
     */
    public final void updatePreview(Object source){
        JComboBox jcb = (JComboBox) source;
        updateLUComboBox(jcb.getSelectedIndex());
        CanvasSE prev = getPreview();
        prev.setSymbol(getFallbackSymbolizer());
        updateTable();
    }

    @Override
    public void setId(String newId) {
        id = newId;
    }

    /**
     * Init the preview of the fallback symbol.
     */
    public abstract void initPreview();

    /**
     * Initialize the panels.
     */
    public abstract void initializeLegendFields();

    /**
     * Gets an empty analysis that can be used ot build a panel equivalent to the caller.
     * @return an empty analysis
     */
    public abstract MappedLegend<K,U> getEmptyAnalysis();

    /**
     * Get a key that is not already used in the inner MappedLegend
     * @return A not already used key.
     */
    public abstract K getNotUsedKey();

    /**
     * Gets the model used to build the JTable.
     * @return The table model.
     */
    public abstract AbstractTableModel getTableModel();

    /**
     * Gets the editor used to configure a cell with a preview.
     * @return A cell editor.
     */
    public abstract TableCellEditor getParametersCellEditor();

    /**
     * Gets the editor used to configure a key of the table
     * @return A cell editor.
     */
    public abstract TableCellEditor getKeyCellEditor();

    /**
     * Gets the index of the column used to display previews in the table.
     * @return the index of the preview column.
     */
    public abstract int getPreviewColumn();

    /**
     * Gets the index of the column used to display keys in the table.
     * @return the index of the key column.
     */
    public abstract int getKeyColumn();

    /**
     * Gets the constant Symbolizer obtained when using all the constant and fallback values of the original Symbolizer.
     * @return The fallback Symbolizer.
     */
    public abstract Symbolizer getFallbackSymbolizer();

    /**
     * Gets the Class of the keys used in the map.
     * @return The Class of the type used for the map's keys.
     */
    public abstract Class getPreviewClass();
}
