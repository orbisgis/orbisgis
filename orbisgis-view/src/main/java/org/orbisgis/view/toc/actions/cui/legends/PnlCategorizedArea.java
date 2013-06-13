package org.orbisgis.view.toc.actions.cui.legends;

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
 * @author Alexis Guéganno
 */
public class PnlCategorizedArea extends PnlAbstractCategorized<AreaParameters>{
    public static final Logger LOGGER = Logger.getLogger(PnlCategorizedArea.class);
    private static final I18n I18N = I18nFactory.getI18n(PnlCategorizedArea.class);
    private CanvasSE fallbackPreview;
    private JComboBox fieldCombo;
    private JCheckBox strokeBox;

    @Override
    public CanvasSE getPreview() {
        return fallbackPreview;
    }

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
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        glob.add(getEnableStrokeCheckBox(), gbc);
        //Table for the recoded configurations
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
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
        CategorizedArea ra = (CategorizedArea) getLegend();
        strokeBox.setSelected(ra.isStrokeEnabled());
        strokeBox.addActionListener(EventHandler.create(ActionListener.class, this, "onEnableStroke"));
        ret.add(strokeBox);
        return ret;
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

    private JPanel getUOMCombo(){
        JPanel pan = new JPanel();
        JComboBox jcb = getLineUomCombo((CategorizedArea)getLegend());
        ActionListener aclUom = EventHandler.create(ActionListener.class, this, "updatePreview", "source");
        jcb.addActionListener(aclUom);
        pan.add(new JLabel(I18N.tr("Unit of measure :")));
        pan.add(jcb);
        return pan;
    }
}
