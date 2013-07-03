package org.orbisgis.view.toc.actions.cui.legends;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sif.components.WideComboBox;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelInterval;
import org.orbisgis.view.toc.actions.cui.legends.panels.ColorConfigurationPanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.ColorScheme;
import org.orbisgis.view.toc.actions.cui.legends.stats.Thresholds;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;

import static org.orbisgis.core.renderer.se.parameter.Categorize.CategorizeMethod;

/**
 * Common base for all the panels used to configure the interval classifications.
 * @author Alexis Guéganno
 */
public abstract class PnlAbstractCategorized<U extends LineParameters> extends PnlAbstractTableAnalysis<Double,U> {
    public static final Logger LOGGER = Logger.getLogger(PnlAbstractCategorized.class);
    private static final I18n I18N = I18nFactory.getI18n(PnlAbstractCategorized.class);
    private ColorConfigurationPanel colorConfig;
    private Thresholds thresholds;
    public final static Integer[] THRESHOLDS_NUMBER = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    public final Integer[] THRESHOLDS_SQUARE = new Integer[]{2,4,8,16};
    protected CanvasSE fallbackPreview;
    protected WideComboBox fieldCombo;
    protected static final String ENABLE_BORDER = I18N.tr("Enable border");

    /**
     * The default number of classes in a classification.
     */
    public static final Integer DEFAULT_CLASS_NUMBER = 5;
    private JComboBox numberCombo;
    private JButton createCl;
    private JComboBox methodCombo;
    private DefaultComboBoxModel comboModel;

    @Override
    public CanvasSE getPreview() {
        return fallbackPreview;
    }

    /**
     * Builds the panel used to display and configure the fallback symbol
     *
     * @return The Panel where the fallback configuration is displayed.
     */
    protected CanvasSE getFallback() {
        initPreview();
        return fallbackPreview;
    }

    @Override
    public String getFieldName() {
        return fieldCombo.getSelectedItem().toString();
    }

    /**
     * Initialize a {@code JComboBo} whose values are set according to the
     * not spatial fields of {@code ds}.
     * @param ds The associated DataSource
     * @return The combo box used to manage the studied field.
     */
    @Override
    public WideComboBox getFieldCombo(DataSource ds){
        WideComboBox combo = new WideComboBox();
        if(ds != null){
            try {
                Metadata md = ds.getMetadata();
                int fc = md.getFieldCount();
                for (int i = 0; i < fc; i++) {
                    if(TypeFactory.isNumerical(md.getFieldType(i).getTypeCode())){
                        combo.addItem(md.getFieldName(i));
                    }
                }
            } catch (DriverException ex) {
                LOGGER.error(ex);
            }
        }
        return combo;
    }

    @Override
    public int getPreviewColumn(){
        return TableModelInterval.PREVIEW_COLUMN;
    }

    @Override
    public int getKeyColumn(){
        return TableModelInterval.KEY_COLUMN;
    }

    @Override
    public Double getNotUsedKey(){
        AbstractCategorizedLegend leg = (AbstractCategorizedLegend) getLegend();
        return leg.getNotUsedKey((Double)leg.keySet().last());
    }

    @Override
    public Class getPreviewClass() {
        return Double.class;
    }

    @Override
    public String getTitleBorder(){
        return I18N.tr("Interval classification");
    }

    private Thresholds computeStats(String fieldName){
        DescriptiveStatistics stats = new DescriptiveStatistics();
        DataSource ds = getDataSource();
        try {
            int fieldIndex = ds.getMetadata().getFieldIndex(fieldName);
            long rowCount = ds.getRowCount();
            for(long i=0; i<rowCount; i++){
                Value val = ds.getFieldValue(i,fieldIndex);
                stats.addValue(val.getAsDouble());
            }
        } catch (DriverException e) {
            LOGGER.warn(I18N.tr("The application has ended unexpectedly"));
        }
        return new Thresholds(stats,fieldName);
    }

    /**
     * Initialize the panels.
     */
    @Override
    public void initializeLegendFields() {
        this.removeAll();

        JPanel glob = new JPanel(new MigLayout());

        //Fallback symbol
        glob.add(getSettingsPanel(), "cell 0 0");

        //Classification generator
        glob.add(getCreateClassificationPanel(), "cell 1 0");

        //Table for the recoded configurations
        glob.add(getTablePanel(), "cell 0 1, span 2 1, growx");
        this.add(glob);
        this.revalidate();
    }

    /**
     * Initialize and return the settings panel.
     *
     * @return Settings panel
     */
    protected JPanel getSettingsPanel() {
        JPanel jp = new JPanel(new MigLayout("wrap 2", "[align r][align l]"));
        jp.setBorder(BorderFactory.createTitledBorder(I18N.tr("General settings")));

        //Field chooser
        jp.add(new JLabel("<html><b>" + I18N.tr("Field") + "</b></html>"));
        fieldCombo = getFieldComboBox();
        // Set the field combo box to a max width of 90 pixels
        // and grow the others.
        jp.add(fieldCombo, "width ::90");

        //UOM
        jp.add(new JLabel(I18N.tr("Border width unit")));
        jp.add(getUOMComboBox(), "growx");

        beforeFallbackSymbol(jp);

        // Fallback symbol
        jp.add(getFallback(), "span 2, align center");
        jp.add(new JLabel(I18N.tr("Fallback symbol")), "span 2, align center");

        return jp;
    }

    /**
     * Add any necessary components in the general settings panel
     * before the fallback symbol.
     *
     * @param genSettings The general settings panel
     */
    protected abstract void beforeFallbackSymbol(JPanel genSettings);

    /**
     * Create and return a combobox for the border width unit,
     * adding an appropriate action listener to update the preview.
     *
     * @return A combobox for the border width unit
     */
    protected abstract JComboBox getUOMComboBox();

    /**
     * Retrieve the panel that gathers all the components needed to create the classification.
     * @return The panel gathering the graphic elements that can be used to create the classification.
     */
    public JPanel getCreateClassificationPanel(){
        if(numberCombo == null){
            numberCombo = new JComboBox(getThresholdsNumber());
            ((JLabel)numberCombo.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        }
        comboModel = (DefaultComboBoxModel) numberCombo.getModel();

        JPanel inner = new JPanel(
                new MigLayout("wrap 2", "[align r][align l]"));

        inner.add(new JLabel(I18N.tr("Method")));
        inner.add(getMethodCombo(), "width ::130");
        inner.add(new JLabel(I18N.tr("Classes")));
        inner.add(numberCombo, "split 2");
        createCl = new JButton(I18N.tr("Create"));
        createCl.setActionCommand("click");
        createCl.addActionListener(
                EventHandler.create(ActionListener.class, this, "onComputeClassification"));
        createCl.setEnabled(false);
        inner.add(createCl, "gapleft push");

        JPanel outside = new JPanel(new MigLayout("wrap 1", "[align c]"));
        outside.setBorder(BorderFactory.createTitledBorder(
                I18N.tr("Classification settings")));
        if(colorConfig == null){
            ArrayList<String> names = new ArrayList<String>(ColorScheme.rangeColorSchemeNames());
            names.addAll(ColorScheme.discreteColorSchemeNames());
            colorConfig = new ColorConfigurationPanel(names);
        }
        outside.add(new JLabel(I18N.tr("Color scheme")), "align l");
        outside.add(colorConfig, "growx");
        outside.add(inner, "growx");
        return outside;
    }

    /**
     * Change what is displayed by the combo box.
     */
    private void changeModelContent(){
        Integer selItem = (Integer)numberCombo.getSelectedItem();
        Integer[] vals = getThresholdsNumber();
        int index = Arrays.binarySearch(vals,selItem);
        int real = index < 0 ? -index-1 : index;
        comboModel.removeAllElements();
        for(int i=0;i<vals.length;i++){
            comboModel.addElement(vals[i]);
        }
        numberCombo.setSelectedIndex(real);
        numberCombo.invalidate();
    }

    /**
     * Gets the JComboBox used to select the classification method.
     * @return The JComboBox.
     */
    public JComboBox getMethodCombo(){
        if(methodCombo == null){
            ContainerItemProperties[] categorizeMethods = getCategorizeMethods();
            methodCombo = new WideComboBox(categorizeMethods);
            methodCombo.addActionListener(
                    EventHandler.create(ActionListener.class, this, "methodChanged"));
            ((JLabel)methodCombo.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            methodCombo.setSelectedItem(CategorizeMethod.MANUAL.toString());
        }
        methodChanged();
        return methodCombo;
    }

    /**
     * Gets the supported number of thresholds for the currently selected classification.
     * @return The number of thresholds.
     */
    private Integer[] getThresholdsNumber(){
        if(methodCombo == null){
            return THRESHOLDS_SQUARE;
        } else {
            ContainerItemProperties selectedItem = (ContainerItemProperties) methodCombo.getSelectedItem();
            CategorizeMethod cm = CategorizeMethod.valueOf(selectedItem.getKey());
            switch(cm){
                case BOXED_MEANS: return THRESHOLDS_SQUARE;
                default : return THRESHOLDS_NUMBER;
            }
        }
    }

    /**
     * The selected classification has changed. Called by EventHandler.
     */
    public void methodChanged(){
        ContainerItemProperties selectedItem = (ContainerItemProperties) methodCombo.getSelectedItem();
        changeModelContent();
        boolean b = CategorizeMethod.valueOf(selectedItem.getKey()).equals(CategorizeMethod.MANUAL);
        if(createCl != null){
            createCl.setEnabled(!b);
        }
    }

    /**
     * This method is called by EventHandler when clicking on the button dedicated to classification creation.
     */
    public void onComputeClassification(){
        String name = getFieldName();
        if(thresholds == null || !thresholds.getFieldName().equals(name)){
            thresholds = computeStats(getFieldName());
        }
        ContainerItemProperties selectedItem = (ContainerItemProperties) methodCombo.getSelectedItem();
        CategorizeMethod cm = CategorizeMethod.valueOf(selectedItem.getKey());
        Integer number = (Integer) numberCombo.getSelectedItem();
        SortedSet<Double> set = thresholds.getThresholds(cm,number);
        if(!set.isEmpty()){
            ColorScheme sc = colorConfig.getColorScheme();
            MappedLegend<Double,U> cl = createColouredClassification(
                    set,
                    new NullProgressMonitor(),
                    sc);
            cl.setLookupFieldName(((MappedLegend)getLegend()).getLookupFieldName());
            cl.setName(getLegend().getName());
            setLegend(cl);
        }
    }

    /**
     * Gets the value contained in the {@code Methods} enum with their
     * internationalized representation in a {@code
     * ContainerItemProperties} array.
     * @return {@link Categorize.CategorizeMethod} in an array of containers.
     */
    public ContainerItemProperties[] getCategorizeMethods(){
        CategorizeMethod[] us = CategorizeMethod.values();
        ArrayList<ContainerItemProperties> temp = new ArrayList<ContainerItemProperties>();
        for (CategorizeMethod u : us) {
            if (isSupported(u)) {
                ContainerItemProperties cip = new ContainerItemProperties(u.name(), u.toLocalizedString());
                temp.add(cip);
            }
        }
        return temp.toArray(new ContainerItemProperties[temp.size()]);
    }

    /**
     * Return if the given method is supported
     * @param cm The tested method
     * @return true if cm is supported
     */
    private boolean isSupported(CategorizeMethod cm){
        switch(cm){
            case EQUAL_INTERVAL : return true;
            case MANUAL : return true;
            case STANDARD_DEVIATION: return true;
            case QUANTILES: return true;
            default : return false;
        }
    }
}
