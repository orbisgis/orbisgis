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
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.CompositeSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.AreaParameters;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;
import org.orbisgis.legend.thematic.recode.AbstractRecodedLegend;
import org.orbisgis.legend.thematic.recode.RecodedArea;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.background.BackgroundListener;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.DefaultJobId;
import org.orbisgis.view.background.JobId;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.model.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.util.Map;
import java.util.Set;

/**
* <p></p>This panel must be used to manage all the parameters of an area symbolizer
* which is configured thanks to a "simple" recoded PenStroke and a "simple" recoded SolidFill. All the parameters
* of these symbolizer nodes must be configured either with a Recode or a Literal, all
* the Recode must be done with the same analysis field.</p>
* <p>This panel proposes a way to build a classification from scratch. This feature comes fortunately with a
* ProgressMonitor that can be used to cancel the building. This way, if accidentally trying to build a classification
* on a field with a lot of different values, the user can still cancel the operation. The feeding of the underlying
* recoded analysis becomes in fact really inefficient when it manages a lot of elements.</p>
*
* @author Alexis Guéganno
*/
public class PnlRecodedArea extends PnlAbstractUniqueValue<AreaParameters>{
    public static final Logger LOGGER = Logger.getLogger(PnlRecodedLine.class);
    private static final I18n I18N = I18nFactory.getI18n(PnlRecodedLine.class);
    private String id;
    private CanvasSE fallbackPreview;
    private JComboBox fieldCombo;
    private final static String JOB_NAME = "recodeSelectDistinct";
    private SelectDistinctJob selectDistinct;
    private BackgroundListener background;
    private JCheckBox strokeBox;

    @Override
    public Component getComponent() {
        initializeLegendFields();
        return this;
    }

    @Override
    public ISELegendPanel newInstance() {
        return new PnlRecodedArea();
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
        ((RecodedArea)getLegend()).setFallbackParameters(editCanvas(fallbackPreview));
    }

    /**
     * Builds a SIF dialog used to edit the given LineParameters.
     * @param cse The canvas we want to edit
     * @return The LineParameters that must be used at the end of the edition.
     */
    private AreaParameters editCanvas(CanvasSE cse){
        RecodedArea leg = (RecodedArea) getLegend();
        AreaParameters lps = leg.getFallbackParameters();
        UniqueSymbolArea usa = new UniqueSymbolArea(lps);
        if(leg.isStrokeEnabled()){
            usa.setStrokeUom(leg.getStrokeUom());
        }
        PnlUniqueAreaSE pls = new PnlUniqueAreaSE(false, leg.isStrokeEnabled(), false);
        pls.setLegend(usa);
        if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
            usa = (UniqueSymbolArea) pls.getLegend();
            AreaParameters nlp = usa.getAreaParameters();
            cse.setSymbol(usa.getSymbolizer());
            return nlp;
        } else {
            return lps;
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
        fieldCombo =getFieldComboBox();
        jp.add(fieldCombo);
        return jp;
    }

    /**
     * Gets the Symbolizer that is associated to the unique symbol matching the fallback configuration of this
     * unique value classification.
     * @return A Symbolizer.
     */
    @Override
    public Symbolizer getFallbackSymbolizer(){
        UniqueSymbolArea usl = new UniqueSymbolArea(((RecodedArea)getLegend()).getFallbackParameters());
        usl.setStrokeUom(((RecodedArea)getLegend()).getStrokeUom());
        return usl.getSymbolizer();
    }

    /**
     * Initializes the preview for the fallback configuration
     */
    public void initPreview() {
        fallbackPreview = new CanvasSE(getFallbackSymbolizer());
        MouseListener l = EventHandler.create(MouseListener.class, this, "onEditFallback", "", "mouseClicked");
        fallbackPreview.addMouseListener(l);
    }

    @Override
    public AreaParameters getColouredParameters(AreaParameters f, Color c) {
        return new AreaParameters(f.getLineColor(), f.getLineOpacity(),f.getLineWidth(),f.getLineDash(),c,f.getFillOpacity());
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

    @Override
    public AbstractTableModel getTableModel(){
        return new TableModelRecodedArea((AbstractRecodedLegend<AreaParameters>) getLegend());
    }

    @Override
    public TableCellEditor getParametersCellEditor(){
        return new ParametersEditorRecodedArea();
    }

    @Override
    public KeyEditorUniqueValue<AreaParameters> getKeyCellEditor(){
        return new KeyEditorRecodedArea();
    }

    /**
     * Here are made all the initializations. Look at the specialized methods to have more details.
     */
    public void initializeLegendFields() {
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
        //UOM
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        glob.add(getUOMCombo(),gbc);
        //Classification generator
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        glob.add(getCreateClassificationPanel(), gbc);
        //Classification generator
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        glob.add(getEnableStrokeCheckBox(), gbc);
        //Table for the recoded configurations
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        glob.add(getTablePanel(), gbc);
        this.add(glob);
        this.revalidate();
    }

    /**
     * Gets the panel used to set if the stroke will be drawable or not.
     * @return The configuration panel for the stroke use.
     */
    public JPanel getEnableStrokeCheckBox(){
        JPanel ret = new JPanel();
        ret.add(new JLabel(I18N.tr("Enable Stroke:")));
        strokeBox = new JCheckBox(I18N.tr(""));
        RecodedArea ra = (RecodedArea) getLegend();
        strokeBox.setSelected(ra.isStrokeEnabled());
        strokeBox.addActionListener(EventHandler.create(ActionListener.class, this, "onEnableStroke"));
        ret.add(strokeBox);
        return ret;
    }

    /**
     * Action done when the checkbox used to activate the stroke is pressed.
     */
    public void onEnableStroke(){
        RecodedArea ra = (RecodedArea) getLegend();
        ra.setStrokeEnabled(strokeBox.isSelected());
        getPreview().setSymbol(new UniqueSymbolArea(ra.getFallbackParameters()).getSymbolizer());
        TableModelUniqueValue model = (TableModelUniqueValue) getJTable().getModel();
        model.fireTableDataChanged();
    }

    @Override
    public AbstractRecodedLegend<AreaParameters> getEmptyAnalysis() {
        RecodedArea ra = new RecodedArea();
        RecodedArea old = (RecodedArea)getLegend();
        if(old != null){
            ra.setStrokeEnabled(old.isStrokeEnabled());
        }
        return ra;
    }

    private JPanel getUOMCombo(){
        JPanel pan = new JPanel();
        JComboBox jcb = getLineUomCombo(((RecodedArea)getLegend()));
        ActionListener aclUom = EventHandler.create(ActionListener.class, this, "updatePreview", "source");
        jcb.addActionListener(aclUom);
        pan.add(new JLabel(I18N.tr("Unit of measure :")));
        pan.add(jcb);
        return pan;
    }

    /**
     * Called to build a classification from the given data source and field. Makes a SELECT DISTINCT field FROM ds;
     * and feeds the legend that has been cleared prior to that.
     */
    public void onCreateClassification(ActionEvent e){
        if(e.getActionCommand().equals("click")){
            String fieldName = fieldCombo.getSelectedItem().toString();
            selectDistinct = new SelectDistinctJob(fieldName);
            BackgroundManager bm = Services.getService(BackgroundManager.class);
            JobId jid = new DefaultJobId(JOB_NAME);
            if(background == null){
                background = new OperationListener();
                bm.addBackgroundListener(background);
            }
            bm.nonBlockingBackgroundOperation(jid, selectDistinct);
        }
    }

    /**
     * Used when the field against which the analysis is made changes.
     *
     * @param obj The new field.
     */
    public void updateField(String obj) {
        ((RecodedArea)getLegend()).setLookupFieldName(obj);
    }

    @Override
    public void setLegend(Legend legend) {
        if (legend instanceof RecodedArea) {
            if(getLegend() != null){
                Rule rule = getLegend().getSymbolizer().getRule();
                if(rule != null){
                    CompositeSymbolizer compositeSymbolizer = rule.getCompositeSymbolizer();
                    int i = compositeSymbolizer.getSymbolizerList().indexOf(this.getLegend().getSymbolizer());
                    compositeSymbolizer.setSymbolizer(i, legend.getSymbolizer());
                }
            }
            setLegendImpl((RecodedArea)legend);
            this.initializeLegendFields();
        } else {
            throw new IllegalArgumentException(I18N.tr("You must use recognized RecodedArea instances in"
                        + "this panel."));
        }
    }

    @Override
    public void setGeometryType(int type) {
    }

    @Override
    public boolean acceptsGeometryType(int geometryType) {
        return geometryType == SimpleGeometryType.POLYGON||
                    geometryType == SimpleGeometryType.ALL;
    }

    @Override
    public Legend copyLegend() {
        RecodedArea rl = new RecodedArea();
        rl.setStrokeEnabled(((RecodedArea)getLegend()).isStrokeEnabled());
        Set<Map.Entry<String,AreaParameters>> entries = ((RecodedArea)getLegend()).entrySet();
        for(Map.Entry<String,AreaParameters> entry : entries){
            rl.put(entry.getKey(),entry.getValue());
        }
        rl.setFallbackParameters(((RecodedArea)getLegend()).getFallbackParameters());
        rl.setLookupFieldName(((RecodedArea)getLegend()).getLookupFieldName());
        return rl;
    }

    @Override
    public CanvasSE getPreview() {
        return fallbackPreview;
    }

}