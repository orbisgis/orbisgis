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
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.legend.thematic.recode.AbstractRecodedLegend;
import org.orbisgis.legend.thematic.recode.RecodedLine;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.background.BackgroundListener;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.DefaultJobId;
import org.orbisgis.view.background.JobId;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.model.KeyEditorRecodedLine;
import org.orbisgis.view.toc.actions.cui.legends.model.KeyEditorUniqueValue;
import org.orbisgis.view.toc.actions.cui.legends.model.ParametersEditorRecodedLine;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelRecodedLine;
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
 * <p></p>This panel must be used to manage all the parameters of a line symbolizer
 * which is configured thanks to a "simple" recoded PenStroke. All the parameters
 * of the PenStroke must be configured either with a Recode or a Literal, all
 * the Recode must be done with the same analysis field.</p>
 * <p>This panel proposes a way to build a classification from scratch. This feature comes fortunately with a
 * ProgressMonitor that can be used to cancel the building. This way, if accidentally trying to build a classification
 * on a field with a lot of different values, the user can still cancel the operation. The feeding of the underlying
 * recoded analysis becomes in fact really inefficient when it manages a lot of elements.</p>
 *
 * @author Alexis Guéganno
 */
public class PnlRecodedLine extends PnlAbstractUniqueValue<LineParameters>{
    public final static Logger LOGGER = Logger.getLogger(PnlRecodedLine.class);
    private static I18n I18N = I18nFactory.getI18n(PnlRecodedLine.class);
    private static final String FALLBACK = "Fallback";
    private static final String COMPUTED = "Computed";
    private String id;
    private CanvasSE fallbackPreview;
    private JComboBox fieldCombo;
    private SelectDistinctJob selectDistinct;
    private BackgroundListener background;

    @Override
    public Component getComponent() {
        initializeLegendFields();
        return this;
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
        ((RecodedLine)getLegend()).setFallbackParameters(editCanvas(fallbackPreview));
    }

    /**
     * Builds a SIF dialog used to edit the given LineParameters.
     * @param cse The canvas we want to edit
     * @return The LineParameters that must be used at the end of the edition.
     */
    private LineParameters editCanvas(CanvasSE cse){
        LineParameters lps = ((RecodedLine)getLegend()).getFallbackParameters();
        UniqueSymbolLine usl = new UniqueSymbolLine(lps);
        usl.setStrokeUom(((RecodedLine)getLegend()).getStrokeUom());
        PnlUniqueLineSE pls = new PnlUniqueLineSE(false);
        pls.setLegend(usl);
        if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
            usl = (UniqueSymbolLine) pls.getLegend();
            LineParameters nlp = usl.getLineParameters();
            cse.setSymbol(usl.getSymbolizer());
            cse.imageChanged();
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
        UniqueSymbolLine usl = new UniqueSymbolLine(((RecodedLine)getLegend()).getFallbackParameters());
        usl.setStrokeUom(((RecodedLine) getLegend()).getStrokeUom());
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
    public RecodedLine getEmptyAnalysis(){
        return new RecodedLine();
    }

    @Override
    public AbstractTableModel getTableModel(){
        return new TableModelRecodedLine((AbstractRecodedLegend<LineParameters>) getLegend());
    }

    @Override
    public TableCellEditor getParametersCellEditor(){
        return new ParametersEditorRecodedLine();
    }

    @Override
    public KeyEditorUniqueValue<LineParameters> getKeyCellEditor(){
        return new KeyEditorRecodedLine();
    }

    /**
     * Here are made all the initializations. Look at the specialized methods to have more details.
     */
    @Override
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
        //Table for the recoded configurations
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        glob.add(getTablePanel(), gbc);
        this.add(glob);
        this.revalidate();
    }

    private JPanel getUOMCombo(){
        JPanel pan = new JPanel();
        JComboBox jcb = getLineUomCombo((RecodedLine)getLegend());
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

    @Override
    public LineParameters getColouredParameters(LineParameters lp, Color newCol){
        return new LineParameters(newCol, lp.getLineOpacity(), lp.getLineWidth(), lp.getLineDash());
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
        RecodedLine leg = (RecodedLine) getLegend();
        Set<Map.Entry<String,LineParameters>> entries = leg.entrySet();
        for(Map.Entry<String,LineParameters> entry : entries){
            rl.put(entry.getKey(),entry.getValue());
        }
        rl.setFallbackParameters(leg.getFallbackParameters());
        rl.setLookupFieldName(leg.getLookupFieldName());
        return rl;
    }

    @Override
    public CanvasSE getPreview() {
        return fallbackPreview;
    }

    @Override
    public void setLegend(Legend legend) {
        if (legend instanceof RecodedLine) {
            if(getLegend() != null){
                Rule rule = getLegend().getSymbolizer().getRule();
                if(rule != null){
                    CompositeSymbolizer compositeSymbolizer = rule.getCompositeSymbolizer();
                    int i = compositeSymbolizer.getSymbolizerList().indexOf(this.getLegend().getSymbolizer());
                    compositeSymbolizer.setSymbolizer(i, legend.getSymbolizer());
                }
            }
            setLegendImpl(legend);
            this.initializeLegendFields();
        } else {
            throw new IllegalArgumentException(I18N.tr("You must use recognized RecodedLine instances in"
                        + "this panel."));
        }
    }
}