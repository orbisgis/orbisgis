package org.orbisgis.view.toc.actions.cui.legends;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.orbisgis.core.renderer.se.CompositeSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.AreaParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;
import org.orbisgis.legend.thematic.categorize.CategorizedArea;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.model.KeyEditorCategorizedArea;
import org.orbisgis.view.toc.actions.cui.legends.model.ParametersEditorCategorizedArea;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelCatArea;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelInterval;
import org.orbisgis.view.toc.actions.cui.legends.panels.UomCombo;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.util.Map;
import java.util.Set;

/**
 * Panel for "Interval classification - Area".
 *
 * @author Alexis Guéganno
 */
public class PnlCategorizedArea extends PnlAbstractCategorized<AreaParameters>{
    public static final Logger LOGGER = Logger.getLogger(PnlCategorizedArea.class);
    private static final I18n I18N = I18nFactory.getI18n(PnlCategorizedArea.class);
    private JCheckBox strokeBox;

    /**
     * This methods is called by EventHandler when the user clicks on the fall back's preview. It opens an UI that lets
     * the user edit the parameters of the fall back configuration and that apply it if the user clicks OK.
     * @param me The MouseEvent that caused the call to this method.
     */
    public void onEditFallback(MouseEvent me){
        ((CategorizedArea)getLegend()).setFallbackParameters(editCanvas(fallbackPreview));
    }

    /**
     * Builds a SIF dialog used to edit the given AreaParameters.
     * @param cse The canvas we want to edit
     * @return The AreaParameters that must be used at the end of the edition.
     */
    private AreaParameters editCanvas(CanvasSE cse){
        CategorizedArea leg = (CategorizedArea) getLegend();
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

    @Override
    public void initPreview() {
        fallbackPreview = new CanvasSE(getFallbackSymbolizer());
        MouseListener l = EventHandler.create(MouseListener.class, this, "onEditFallback", "", "mouseClicked");
        fallbackPreview.addMouseListener(l);
    }

    @Override
    public AreaParameters getColouredParameters(AreaParameters f, Color c) {
        return new AreaParameters(f.getLineColor(), f.getLineOpacity(),f.getLineWidth(),f.getLineDash(),c,f.getFillOpacity());
    }

    @Override
    protected void beforeFallbackSymbol(JPanel genSettings) {
        // Enable stroke?
        genSettings.add(getEnableStrokeCheckBox(), "span 2, align center");
    }

    /**
     * Gets the checkbox used to set if the border will be drawn.
     *
     * @return The enable border checkbox.
     */
    private JCheckBox getEnableStrokeCheckBox(){
        strokeBox = new JCheckBox(I18n.marktr(ENABLE_BORDER));
        strokeBox.setSelected(((CategorizedArea) getLegend()).isStrokeEnabled());
        strokeBox.addActionListener(
                EventHandler.create(ActionListener.class, this, "onEnableStroke"));
        return strokeBox;
    }

    @Override
    public MappedLegend<Double,AreaParameters> getEmptyAnalysis() {
        return new CategorizedArea();
    }

    @Override
    public AbstractTableModel getTableModel() {
        return new TableModelCatArea((AbstractCategorizedLegend<AreaParameters>)getLegend());
    }

    @Override
    public TableCellEditor getParametersCellEditor() {
        return new ParametersEditorCategorizedArea();
    }

    @Override
    public TableCellEditor getKeyCellEditor() {
        return new KeyEditorCategorizedArea();
    }

    @Override
    public void setLegend(Legend legend) {
        if (legend instanceof CategorizedArea) {
            if(getLegend() != null){
                Rule rule = getLegend().getSymbolizer().getRule();
                if(rule != null){
                    CompositeSymbolizer compositeSymbolizer = rule.getCompositeSymbolizer();
                    int i = compositeSymbolizer.getSymbolizerList().indexOf(this.getLegend().getSymbolizer());
                    compositeSymbolizer.setSymbolizer(i, legend.getSymbolizer());
                }
            }
            setLegendImpl((CategorizedArea)legend);
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
        return geometryType == SimpleGeometryType.POLYGON||
                geometryType == SimpleGeometryType.ALL;
    }

    /**
     * Gets the Symbolizer that is associated to the unique symbol matching the fallback configuration of this
     * interval classification.
     * @return A Symbolizer.
     */
    @Override
    public Symbolizer getFallbackSymbolizer(){
        UniqueSymbolArea usl = new UniqueSymbolArea(((CategorizedArea)getLegend()).getFallbackParameters());
        usl.setStrokeUom(((CategorizedArea) getLegend()).getStrokeUom());
        return usl.getSymbolizer();
    }

    @Override
    public Legend copyLegend() {
        CategorizedArea cl = (CategorizedArea) getLegend();
        Set<Map.Entry<Double,AreaParameters>> entries = cl.entrySet();
        CategorizedArea ret = new CategorizedArea();
        for(Map.Entry<Double,AreaParameters> en : entries){
            ret.put(en.getKey(),en.getValue());
        }
        ret.setStrokeUom(cl.getStrokeUom());
        ret.setFallbackParameters(cl.getFallbackParameters());
        ret.setLookupFieldName(cl.getLookupFieldName());
        return ret;
    }

    @Override
    public Component getComponent() {
        initializeLegendFields();
        return this;
    }

    @Override
    public ISELegendPanel newInstance() {
        return new PnlCategorizedArea();
    }

    @Override
    public String validateInput() {
        return "";
    }

    /**
     * Action done when the checkbox used to activate the stroke is pressed.
     */
    public void onEnableStroke(){
        CategorizedArea ra = (CategorizedArea) getLegend();
        ra.setStrokeEnabled(strokeBox.isSelected());
        getPreview().setSymbol(new UniqueSymbolArea(ra.getFallbackParameters()).getSymbolizer());
        TableModelInterval model = (TableModelInterval) getJTable().getModel();
        model.fireTableDataChanged();
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

    @Override
    protected JComboBox getUOMComboBox(){
        UomCombo jcb = getLineUomCombo(((CategorizedArea) getLegend()));
        jcb.addActionListener(
                EventHandler.create(ActionListener.class, this, "updatePreview", "source"));
        return jcb.getCombo();
    }
}
