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
 * Base class for Interval Classification UIs.
 *
 * @author Alexis Guéganno
 */
public abstract class PnlAbstractCategorized<U extends LineParameters> extends PnlAbstractTableAnalysis<Double,U> {
    public static final Logger LOGGER = Logger.getLogger(PnlAbstractCategorized.class);
    private static final I18n I18N = I18nFactory.getI18n(PnlAbstractCategorized.class);
    private ColorConfigurationPanel colorConfig;
    private Thresholds thresholds;
    public static final Integer[] THRESHOLDS_NUMBER =
            new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    public static final Integer[] THRESHOLDS_SQUARE =
            new Integer[]{2, 4, 8, 16};
    private WideComboBox numberCombo;
    private JButton createCl;
    private WideComboBox methodCombo;
    private DefaultComboBoxModel comboModel;

    @Override
    public int getPreviewColumn(){
        return TableModelInterval.PREVIEW_COLUMN;
    }

    @Override
    public int getKeyColumn(){
        return TableModelInterval.KEY_COLUMN;
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
     * Retrieve the panel that gathers all the components needed to create the classification.
     * @return The panel gathering the graphic elements that can be used to create the classification.
     */
    public JPanel getCreateClassificationPanel(){
        if(numberCombo == null){
            numberCombo = new WideComboBox(getThresholdsNumber());
        }
        comboModel = (DefaultComboBoxModel) numberCombo.getModel();
        createCl = new JButton(I18N.tr("Create"));
        createCl.setActionCommand("click");
        createCl.addActionListener(
                EventHandler.create(ActionListener.class, this, "onComputeClassification"));
        createCl.setEnabled(false);

        JPanel inner = new JPanel(
                new MigLayout("wrap 2", "[align r][align l]"));

        inner.add(new JLabel(I18N.tr("Method")));
        inner.add(getMethodCombo(), "width ::130");
        inner.add(new JLabel(I18N.tr("Classes")));
        inner.add(numberCombo, "split 2");
        inner.add(createCl, "gapleft push");

        JPanel outside = new JPanel(new MigLayout("wrap 1", "[" + FIXED_WIDTH + ", align c]"));
        outside.setBorder(BorderFactory.createTitledBorder(
                I18N.tr(CLASSIFICATION_SETTINGS)));
        if(colorConfig == null){
            ArrayList<String> names = new ArrayList<String>(ColorScheme.rangeColorSchemeNames());
            names.addAll(ColorScheme.discreteColorSchemeNames());
            colorConfig = new ColorConfigurationPanel(names);
        }
        outside.add(new JLabel(I18N.tr("Color scheme:")), "align l");
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
            thresholds = computeStats(name);
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
            case BOXED_MEANS: return true;
            default : return false;
        }
    }
}
