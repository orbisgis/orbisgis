package org.orbisgis.view.toc.actions.cui.legends;

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
    private Integer classNumber;
    private ColorConfigurationPanel colorConfig;
    private Thresholds thresholds;
    public final static Integer[] THRESHOLDS_NUMBER = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    public final Integer[] THRESHOLDS_SQUARE = new Integer[]{2,4,8,16};
    /**
     * The default number of classes in a classification.
     */
    public static final Integer DEFAULT_CLASS_NUMBER = 5;
    private JComboBox numberCombo;
    private JButton createCl;
    private JComboBox methodCombo;
    private DefaultComboBoxModel comboModel;

    /**
     * Initialize a {@code JComboBo} whose values are set according to the
     * not spatial fields of {@code ds}.
     * @param ds The associated DataSource
     * @return The combo box used to manage the studied field.
     */
    @Override
    public JComboBox getFieldCombo(DataSource ds){
        JComboBox combo = new JComboBox();
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
     * Retrieve the panel that gathers all the components needed to create the classification.
     * @return The panel gathering the graphic elements that can be used to create the classification.
     */
    public JPanel getCreateClassificationPanel(){
        JPanel ret = new JPanel();
        BoxLayout bl = new BoxLayout(ret, BoxLayout.PAGE_AXIS);
        ret.setLayout(bl);
        JPanel sec = new JPanel();
        JLabel numbLab = new JLabel(I18N.tr("Classes:"));
        JLabel clLab = new JLabel(I18N.tr("Method:"));
        createCl = new JButton(I18N.tr("Create"));
        if(classNumber == null){
            classNumber = DEFAULT_CLASS_NUMBER;
        }
        if(colorConfig == null){
            colorConfig = new ColorConfigurationPanel();
        }
        if(numberCombo == null){
            numberCombo = new JComboBox(getThresholdsNumber());
        }
        comboModel = (DefaultComboBoxModel) numberCombo.getModel();
        JPanel btnPanel = new JPanel();
        createCl.setActionCommand("click");
        btnPanel.add(createCl);
        ActionListener btn = EventHandler.create(ActionListener.class, this, "onComputeClassification");
        createCl.addActionListener(btn);
        sec.add(numbLab);
        sec.add(numberCombo);
        sec.add(clLab);
        sec.add(getMethodCombo());
        sec.add(btnPanel);
        ret.add(colorConfig);
        ret.add(sec);
        return ret;
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
            methodCombo = new JComboBox(categorizeMethods);
            ActionListener acl = EventHandler.create(ActionListener.class, this, "methodChanged");
            methodCombo.addActionListener(acl);
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
