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
package org.orbisgis.view.toc.actions.cui.legends;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.legend.thematic.recode.RecodedLine;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.model.KeyEditorRecodedLine;
import org.orbisgis.view.toc.actions.cui.legends.model.ParametersEditorRecodedLine;
import org.orbisgis.view.toc.actions.cui.legends.model.PreviewCellRenderer;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelRecodedLine;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.util.Map;
import java.util.Set;

/**
 * This panel must be used to manage all the parameters of a line symbolizer
 * which is configured thanks to a "simple" recoded PenStroke. All the parameters
 * of the PenStroke must be configured either with a Recode or a Literal, all
 * the Recode must be done with the same analysis field.
 *
 * @author Alexis Guéganno
 */
public class PnlRecodedLine extends AbstractFieldPanel implements ILegendPanel {
    private static I18n I18N = I18nFactory.getI18n(PnlRecodedLine.class);
    private String id;
    private RecodedLine legend;
    private DataSource ds;
    private CanvasSE fallbackPreview;

    @Override
    public Component getComponent() {
        initializeLegendFields();
        return this;
    }

    @Override
    public void initialize(LegendContext lc) {
        if (legend == null) {
            setLegend(new RecodedLine());
        }
        setGeometryType(lc.getGeometryType());
        ILayer layer = lc.getLayer();
        if (layer != null && layer.getDataSource() != null) {
            ds = layer.getDataSource();
        }
    }

    @Override
    public ISELegendPanel newInstance() {
        return new PnlRecodedLine();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String newId) {
        id = newId;
    }

    @Override
    public String validateInput() {
        return "";
    }

    /**
     * This methods is called by EventHandler when the user clicks on the fall back's preview. It opens an UI that lets
     * the user edit the parameters of the fall back configuration and that apply it if the user clicks OK.
     * @param me The MouseEvent that caused the call to this method.
     */
    public void onEditFallback(MouseEvent me){
        legend.setFallbackParameters(editCanvas(fallbackPreview, legend.getFallbackParameters()));
    }

    private LineParameters editCanvas(CanvasSE cse, LineParameters lps){
        UniqueSymbolLine usl = new UniqueSymbolLine(lps);
        PnlUniqueLineSE pls = new PnlUniqueLineSE();
        pls.setLegend(usl);
        if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
            usl = (UniqueSymbolLine) pls.getLegend();
            LineParameters nlp = usl.getLineParameters();
            cse.setSymbol(usl.getSymbolizer());
            cse.invalidate();
            return nlp;
        } else {
            return lps;
        }
    }

    /**
     * Creates and fill the combo box that will be used to compute the
     * analysis.
     *
     * @return  A ComboBox linked to the underlying MappedLegend that configures the analysis field.
     */
    private JComboBox getFieldComboBox() {
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
     * Build the panel used to select the classification field.
     *
     * @return The JPanel where the user will choose the classification field.
     */
    private JPanel getFieldLine() {
        JPanel jp = new JPanel();
        jp.add(new JLabel(I18N.tr("Classification field : ")));
        jp.add(getFieldComboBox());
        return jp;
    }

    /**
     * Get a preview for the fallback configuration
     * @return The preview as a CanvasSE.
     */
    private void initPreview() {
        UniqueSymbolLine usl = new UniqueSymbolLine(legend.getFallbackParameters());
        fallbackPreview = new CanvasSE(usl.getSymbolizer());
        MouseListener l = EventHandler.create(MouseListener.class, this, "onEditFallback", "", "mouseClicked");
        fallbackPreview.addMouseListener(l);
    }

    /**
     * Builds the panel used to display and configure the fallback symbol
     *
     * @return The Panel where the fallback configuration is displayed.
     */
    private JPanel getFallback() {
        JPanel jp = new JPanel();
        jp.add(new JLabel(I18N.tr("Fallback Symbol")));
        initPreview();
        jp.add(fallbackPreview);
        return jp;
    }

    /**
     * Build the panel that contains the JTable where the map is displayed.
     * @return The panel that contains the JTable where the map is displayed.
     */
    private JPanel getTablePanel() {
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createTitledBorder(I18N.tr("Unique value classification")));
        //we build the table here
        final TableModelRecodedLine model = new TableModelRecodedLine(legend);
        final JTable table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        table.setRowHeight(CanvasSE.HEIGHT);
        final int previewWidth = CanvasSE.WIDTH;
        TableColumn previews = table.getColumnModel().getColumn(TableModelRecodedLine.PREVIEW_COLUMN);
        previews.setWidth(previewWidth);
        previews.setMinWidth(previewWidth);
        previews.setMaxWidth(previewWidth);
        previews.setCellRenderer(new PreviewCellRenderer(table, String.class, legend));
        previews.setCellEditor(new ParametersEditorRecodedLine());
        //We put a default editor on the keys.
        TableColumn keys = table.getColumnModel().getColumn(TableModelRecodedLine.KEY_COLUMN);
        KeyEditorRecodedLine ker = new KeyEditorRecodedLine();
        CellEditorListener  cel = EventHandler.create(CellEditorListener.class, model, "fireTableDataChanged", null, "editingStopped");
        ker.addCellEditorListener(cel);
        keys.setCellEditor(ker);
        JScrollPane jsp = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(400,200));
        jp.add(jsp, BorderLayout.CENTER);
        table.doLayout();
        return jp;
    }

    private void initializeLegendFields() {
        this.removeAll();
        JPanel glob = new JPanel();
        GridBagLayout grid = new GridBagLayout();
        glob.setLayout(grid);
        //Field chooser
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        glob.add(getFieldLine(), gbc);
        //Fallback symbol
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        glob.add(getFallback(), gbc);
        //Table for the recoded configurations
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        glob.add(getTablePanel(), gbc);
        this.add(glob);
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
    public void setLegend(Legend legend) {
        if (legend instanceof RecodedLine) {
            this.legend = (RecodedLine) legend;
            this.initializeLegendFields();
        } else {
            throw new IllegalArgumentException(I18N.tr("You must use recognized RecodedLine instances in"
                        + "this panel."));
        }
    }

    @Override
    public void setGeometryType(int type) {
    }

    @Override
    public boolean acceptsGeometryType(int geometryType) {
                return geometryType == SimpleGeometryType.LINE ||
                        geometryType == SimpleGeometryType.POLYGON||
                        geometryType == SimpleGeometryType.ALL;
    }

    @Override
    public Legend copyLegend() {
        RecodedLine rl = new RecodedLine();
        Set<Map.Entry<String,LineParameters>> entries = legend.entrySet();
        for(Map.Entry<String,LineParameters> entry : entries){
            rl.put(entry.getKey(),entry.getValue());
        }
        rl.setFallbackParameters(legend.getFallbackParameters());
        rl.setLookupFieldName(legend.getLookupFieldName());
        return rl;
    }
}