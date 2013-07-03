package org.orbisgis.view.toc.actions.cui.legends;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.orbisgis.core.renderer.se.CompositeSymbolizer;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.PointParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;
import org.orbisgis.legend.thematic.categorize.CategorizedPoint;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.model.KeyEditorCategorizedPoint;
import org.orbisgis.view.toc.actions.cui.legends.model.ParametersEditorCategorizedPoint;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelCatPoint;
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
 * UI for "Interval classification - Point".
 *
 * @author Alexis Guéganno
 */
public class PnlCategorizedPoint extends PnlAbstractCategorized<PointParameters>{
    public static final Logger LOGGER = Logger.getLogger(PnlCategorizedPoint.class);
    private static final I18n I18N = I18nFactory.getI18n(PnlCategorizedPoint.class);
    private JCheckBox strokeBox;
    private ContainerItemProperties[] uoms;

    /**
     * This methods is called by EventHandler when the user clicks on the fall back's preview. It opens an UI that lets
     * the user edit the parameters of the fall back configuration and that apply it if the user clicks OK.
     * @param me The MouseEvent that caused the call to this method.
     */
    public void onEditFallback(MouseEvent me){
        ((CategorizedPoint)getLegend()).setFallbackParameters(editCanvas(fallbackPreview));
    }

    /**
     * Builds a SIF dialog used to edit the given PointParameters.
     * @param cse The canvas we want to edit
     * @return The PointParameters that must be used at the end of the edition.
     */
    private PointParameters editCanvas(CanvasSE cse){
        CategorizedPoint leg = (CategorizedPoint) getLegend();
        PointParameters lps = leg.getFallbackParameters();
        UniqueSymbolPoint usa = new UniqueSymbolPoint(lps);
        if(leg.isStrokeEnabled()){
            usa.setStrokeUom(leg.getStrokeUom());
        }
        PnlUniquePointSE pls = new PnlUniquePointSE(false, leg.isStrokeEnabled(), false);
        pls.setLegend(usa);
        if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
            usa = (UniqueSymbolPoint) pls.getLegend();
            PointParameters nlp = usa.getPointParameters();
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
    public PointParameters getColouredParameters(PointParameters f, Color c) {
        return new PointParameters(f.getLineColor(), f.getLineOpacity(),f.getLineWidth(),f.getLineDash(),
                c,f.getFillOpacity(),
                f.getWidth(), f.getHeight(), f.getWkn());
    }

    @Override
    protected void beforeFallbackSymbol(JPanel genSettings) {
        //UOM - symbol size
        genSettings.add(buildText(I18N.tr("Size unit")));
        genSettings.add(getSymbolUOMComboBox());

        // On vertex? On centroid?
        genSettings.add(pnlOnVertex(), "span 2, align center");

        // Enable stroke?
        genSettings.add(getEnableStrokeCheckBox(), "span 2, align center");
    }

    /**
     * Gets the checkbox used to set if the border will be drawn.
     *
     * @return The enable border checkbox.
     */
    private JCheckBox getEnableStrokeCheckBox(){
        strokeBox = new JCheckBox(ENABLE_BORDER);
        strokeBox.setSelected(((CategorizedPoint) getLegend()).isStrokeEnabled());
        strokeBox.addActionListener(
                EventHandler.create(ActionListener.class, this, "onEnableStroke"));
        return strokeBox;
    }

    @Override
    public MappedLegend<Double,PointParameters> getEmptyAnalysis() {
        return new CategorizedPoint();
    }

    @Override
    public AbstractTableModel getTableModel() {
        return new TableModelCatPoint((AbstractCategorizedLegend<PointParameters>)getLegend());
    }

    @Override
    public TableCellEditor getParametersCellEditor() {
        return new ParametersEditorCategorizedPoint();
    }

    @Override
    public TableCellEditor getKeyCellEditor() {
        return new KeyEditorCategorizedPoint();
    }

    @Override
    public void setLegend(Legend legend) {
        if (legend instanceof CategorizedPoint) {
            if(getLegend() != null){
                Rule rule = getLegend().getSymbolizer().getRule();
                if(rule != null){
                    CompositeSymbolizer compositeSymbolizer = rule.getCompositeSymbolizer();
                    int i = compositeSymbolizer.getSymbolizerList().indexOf(this.getLegend().getSymbolizer());
                    compositeSymbolizer.setSymbolizer(i, legend.getSymbolizer());
                }
            }
            setLegendImpl((CategorizedPoint)legend);
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
                geometryType == SimpleGeometryType.LINE||
                geometryType == SimpleGeometryType.POINT||
                geometryType == SimpleGeometryType.ALL;
    }

    /**
     * Gets the Symbolizer that is associated to the unique symbol matching the fallback configuration of this
     * interval classification.
     * @return A Symbolizer.
     */
    @Override
    public Symbolizer getFallbackSymbolizer(){
        UniqueSymbolPoint usl = new UniqueSymbolPoint(((CategorizedPoint)getLegend()).getFallbackParameters());
        usl.setStrokeUom(((CategorizedPoint) getLegend()).getStrokeUom());
        return usl.getSymbolizer();
    }

    @Override
    public Legend copyLegend() {
        CategorizedPoint cl = (CategorizedPoint) getLegend();
        Set<Map.Entry<Double,PointParameters>> entries = cl.entrySet();
        CategorizedPoint ret = new CategorizedPoint();
        for(Map.Entry<Double,PointParameters> en : entries){
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
        return new PnlCategorizedPoint();
    }

    @Override
    public String validateInput() {
        return "";
    }

    /**
     * Action done when the checkbox used to activate the stroke is pressed.
     */
    public void onEnableStroke(){
        CategorizedPoint ra = (CategorizedPoint) getLegend();
        ra.setStrokeEnabled(strokeBox.isSelected());
        getPreview().setSymbol(new UniqueSymbolPoint(ra.getFallbackParameters()).getSymbolizer());
        TableModelInterval model = (TableModelInterval) getJTable().getModel();
        model.fireTableDataChanged();
    }

    @Override
    protected JComboBox getUOMComboBox(){
        UomCombo jcb = getLineUomCombo(((CategorizedPoint) getLegend()));
        jcb.addActionListener(
                EventHandler.create(ActionListener.class, this, "updatePreview", "source"));
        return jcb.getCombo();
    }

    /**
     * A JPanel containing the combo returned bu getPointUomCombo
     * @return The JComboBox with a JLabel in a JPanel.
     */
    private JComboBox getSymbolUOMComboBox(){
        uoms = getUomProperties();
        UomCombo puc = new UomCombo(((CategorizedPoint)getLegend()).getSymbolUom(),
                uoms,
                I18N.tr("Unit of measure - size :"));
        puc.addActionListener(
                EventHandler.create(ActionListener.class, this, "updateSUComboBox", "source.selectedIndex"));
        return puc.getCombo();
    }

    /**
     * Sets the underlying graphic to use the ith element of the combo box
     * as its uom. Used when changing the combo box selection.
     * @param index The index of the selected unit of measure.
     */
    public void updateSUComboBox(int index){
        CategorizedPoint leg = (CategorizedPoint)getLegend();
        leg.setSymbolUom(Uom.fromString(uoms[index].getKey()));
        CanvasSE prev = getPreview();
        prev.setSymbol(getFallbackSymbolizer());
        updateTable();
    }

    /**
     * Returns the panel used to configure if the symbol must be drawn on vertex or on centroid.
     * @return The panel with the radio buttons.
     */
    private JPanel pnlOnVertex(){
        JPanel jp = new JPanel();
        CategorizedPoint point = (CategorizedPoint) getLegend();
        JRadioButton bVertex = new JRadioButton(I18N.tr("On vertex"));
        JRadioButton bCentroid = new JRadioButton(I18N.tr("On centroid"));
        ButtonGroup bg = new ButtonGroup();
        bg.add(bVertex);
        bg.add(bCentroid);
        ActionListener actionV = EventHandler.create(ActionListener.class, point, "setOnVertex");
        ActionListener actionC = EventHandler.create(ActionListener.class, point, "setOnCentroid");
        ActionListener actionRefV = EventHandler.create(ActionListener.class, this, "onClickVertex");
        ActionListener actionRefC= EventHandler.create(ActionListener.class, this, "onClickCentroid");
        bVertex.addActionListener(actionV);
        bVertex.addActionListener(actionRefV);
        bCentroid.addActionListener(actionC);
        bCentroid.addActionListener(actionRefC);
        bVertex.setSelected(((PointSymbolizer)point.getSymbolizer()).isOnVertex());
        bCentroid.setSelected(!((PointSymbolizer)point.getSymbolizer()).isOnVertex());
        jp.add(bVertex);
        jp.add(bCentroid);
        return jp;
    }

    /**
     * called when the user wants to put the points on the vertices of the geometry.
     */
    public void onClickVertex(){
        changeOnVertex(true);
    }

    /**
     * called when the user wants to put the points on the centroid of the geometry.
     */
    public void onClickCentroid(){
        changeOnVertex(false);
    }

    /**
     * called when the user wants to put the points on the vertices or ont the centroid of the geometry.
     * @param b If true, the points are set on the vertices.
     */
    private void changeOnVertex(boolean b){
        CanvasSE prev = getPreview();
        ((PointSymbolizer)prev.getSymbol()).setOnVertex(b);
        prev.imageChanged();
        updateTable();
    }
}
