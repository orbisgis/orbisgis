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
package org.orbisgis.view.toc.actions.cui.legends.panels;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.model.KeyCellRenderer;
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
 * The table panel used in Value and Interval Classifications.
 *
 * @see org.orbisgis.view.toc.actions.cui.legends.PnlAbstractTableAnalysis
 *
 * @author Adam Gouge
 * @author Alexis Guéganno
 */
public class TablePanel<K, U extends LineParameters> extends JPanel
        implements ActionListener {

    private static final I18n I18N = I18nFactory.getI18n(TablePanel.class);

    private static final String ADD = "add";
    private static final String REMOVE = "remove";

    public final static int CELL_PREVIEW_WIDTH = CanvasSE.WIDTH/2;
    public final static int CELL_PREVIEW_HEIGHT = CanvasSE.HEIGHT/2;

    private JTable table;

    private final MappedLegend<K,U> legend;
    private AbstractTableModel tableModel;
    private TableCellEditor keyCellEditor;
    private int keyColumnIndex;
    private TableCellEditor previewCellEditor;
    private int previewColumnIndex;
    private Class previewClass;

    /**
     * Constructs a {@code TablePanel} with the given parameters.
     *
     * @param legend                The legend being previewed
     * @param titleBorder           Title of the panel
     * @param tableModel            Table model
     * @param keyCellEditor         Editor for the key column
     * @param keyColumnIndex        Key column index
     * @param previewCellEditor     Editor for the preview column
     * @param previewColumnIndex    Preview column index
     * @param previewClass          Preview class (Double or String)
     */
    public TablePanel(MappedLegend<K, U> legend,
                      String titleBorder,
                      AbstractTableModel tableModel,
                      TableCellEditor keyCellEditor,
                      int keyColumnIndex,
                      TableCellEditor previewCellEditor,
                      int previewColumnIndex,
                      Class previewClass) {
        super(new MigLayout("wrap 1", "[align c, grow]"));
        setBorder(BorderFactory.createTitledBorder(titleBorder));
        this.legend = legend;
        this.tableModel = tableModel;
        this.keyCellEditor = keyCellEditor;
        this.previewCellEditor = previewCellEditor;
        this.keyColumnIndex = keyColumnIndex;
        this.previewColumnIndex = previewColumnIndex;
        this.previewClass = previewClass;
        init();
    }

    /**
     * Build the panel that contains the JTable where the map is displayed.
     *
     * @return The table panel
     */
    private void init() {
        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);
        table.setRowHeight(CELL_PREVIEW_HEIGHT);
        TableColumn previews = table.getColumnModel().getColumn(previewColumnIndex);
        previews.setWidth(CELL_PREVIEW_WIDTH);
        previews.setMinWidth(CELL_PREVIEW_WIDTH);
        previews.setMaxWidth(CELL_PREVIEW_WIDTH);
        previews.setCellRenderer(new PreviewCellRenderer(table, previewClass, legend));
        previews.setCellEditor(previewCellEditor);
        // We put a default editor on the keys.
        TableColumn keys = table.getColumnModel().getColumn(keyColumnIndex);
        TableCellEditor ker = keyCellEditor;
        CellEditorListener cel = EventHandler.create(
                CellEditorListener.class, tableModel, "fireTableDataChanged", null, "editingStopped");
        ker.addCellEditorListener(cel);
        keys.setCellEditor(ker);
        keys.setCellRenderer(new KeyCellRenderer(table, previewClass, legend));
        JScrollPane jsp = new JScrollPane(table);

        // Set the viewport to view 6 rows with a width of 400 pixels.
        int rowHeight = table.getRowHeight();
        int tableWidth = 400;
        int tableHeight = rowHeight*6;
        table.setPreferredScrollableViewportSize(
                new Dimension(tableWidth, tableHeight));
        table.setDoubleBuffered(true);
        jsp.setAlignmentX((float).5);
        table.doLayout();

        // Set the unit (click once on down arrow: scroll down one row)
        // and block (one mouse scroll wheel: scroll down one half page)
        // increments.
        JScrollBar verticalScrollBar = jsp.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(rowHeight);
        verticalScrollBar.setBlockIncrement(tableHeight);
        // Set the scroll mode.
        jsp.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

        // Add the elements.
        add(jsp, "growx");
        add(getButtonsPanel(), "growx");
    }

    /**
     * Creates the two buttons Add and Remove, links them to the
     * {@code TablePanel} through actions and put them in a JPanel.
     *
     * @return The two buttons in a JPanel
     */
    private JPanel getButtonsPanel() {
        JPanel jp = new JPanel();

        JButton jb1 = new JButton(I18N.tr("Add"));
        jb1.setActionCommand(ADD);
        jb1.addActionListener(this);
        jp.add(jb1);
        jp.setAlignmentX((float) .5);

        JButton remove = new JButton(I18N.tr("Remove"));
        remove.setActionCommand(REMOVE);
        remove.addActionListener(this);
        jp.add(remove);
        jp.setAlignmentX((float) .5);
        return jp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ADD)){
            legend.put(legend.getNotUsedKey(legend.keySet().last()),
                       legend.getFallbackParameters());
            updateTable();
        } else if (e.getActionCommand().equals(REMOVE)){
            int col = keyColumnIndex;
            int row = table.getSelectedRow();
            if(col>=0 && row >= 0){
                K key = (K) table.getValueAt(row, col);
                legend.remove(key);
                updateTable();
            }
        }
    }

    /**
     * Update the table if it is not null. Called by several event handler
     * methods.
     */
    public void updateTable(){
        if(table != null){
            tableModel.fireTableDataChanged();
        }
    }

    /**
     * Gets the JTable used to draw the mapping.
     *
     * @return the JTable
     */
    public JTable getJTable() {
        return table;
    }
}
