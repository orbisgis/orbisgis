package org.orbisgis.view.toc.actions.cui.legends;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import static org.orbisgis.core.renderer.se.parameter.Categorize.CategorizeMethod;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelInterval;
import org.orbisgis.view.toc.actions.cui.legends.panels.ColorConfigurationPanel;
import org.orbisgis.view.toc.actions.cui.legends.stats.Thresholds;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.beans.EventHandler;
import java.util.*;

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
    /**
     * The default number of classes in a classification.
     */
    public static final Integer DEFAULT_CLASS_NUMBER = 5;
    private JTextField jtf;
    private JButton createCl;
    private JComboBox<ContainerItemProperties> methodCombo;

    /**
     * Initialize a {@code JComboBo} whose values are set according to the
     * not spatial fields of {@code ds}.
     * @param ds
     * @return
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
        createCl = new JButton(I18N.tr("Create Classification"));
        if(classNumber == null){
            classNumber = DEFAULT_CLASS_NUMBER;
        }
        if(colorConfig == null){
            colorConfig = new ColorConfigurationPanel();
        }
        jtf = new JTextField(classNumber.toString(),3);
        ActionListener textListener = EventHandler.create(ActionListener.class, this, "updateFieldContent");
        FocusListener focusListener = EventHandler.create(FocusListener.class, this, "updateFieldContent");
        jtf.addActionListener(textListener);
        jtf.addFocusListener(focusListener);
        JPanel btnPanel = new JPanel();
        createCl.setActionCommand("click");
        btnPanel.add(createCl);
        ActionListener btn = EventHandler.create(ActionListener.class, this, "onComputeClassification");
        createCl.addActionListener(btn);

        sec.add(numbLab);
        sec.add(jtf);
        sec.add(clLab);
        sec.add(getMethodCombo());
        sec.add(btnPanel);
        ret.add(colorConfig);
        ret.add(sec);
        return ret;
    }

    /**
     * Gets the JComboBox used to select the classification method.
     * @return The JComboBox.
     */
    public JComboBox<ContainerItemProperties> getMethodCombo(){
        if(methodCombo == null){
            ContainerItemProperties[] categorizeMethods = getCategorizeMethods();
            methodCombo = new JComboBox<ContainerItemProperties>(categorizeMethods);
            ActionListener acl = EventHandler.create(ActionListener.class, this, "methodChanged");
            methodCombo.addActionListener(acl);
            methodCombo.setSelectedItem(CategorizeMethod.MANUAL.toString());
            methodChanged();
        }
        return methodCombo;
    }

    /**
     * The selected classification has changed. Called by EventHandler.
     */
    public void methodChanged(){
        ContainerItemProperties selectedItem = (ContainerItemProperties) methodCombo.getSelectedItem();
        boolean b = CategorizeMethod.valueOf(selectedItem.getKey()).equals(CategorizeMethod.MANUAL);
        createCl.setEnabled(!b);
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
        SortedSet<Double> set = thresholds.getThresholds(cm,classNumber);
        if(!set.isEmpty()){
            MappedLegend<Double,U> cl = createColouredClassification(
                    set,
                    new NullProgressMonitor(),
                    colorConfig.getStartColor(),
                    colorConfig.getEndCol());
            cl.setLookupFieldName(((MappedLegend)getLegend()).getLookupFieldName());
            cl.setName(getLegend().getName());
            setLegend(cl);
        }
    }

    /**
     * Updates the content of the numeric field where the user can put the number of classes that must be put in the
     * classification.
     */
    public void updateFieldContent(){
        try{
            classNumber = Integer.valueOf(jtf.getText());
        } catch(NumberFormatException nfe){
            jtf.setText(classNumber.toString());
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

    private boolean isSupported(CategorizeMethod cm){
        switch(cm){
            case EQUAL_INTERVAL : return true;
            case MANUAL : return true;
            case STANDARD_DEVIATION: return true;
            default : return false;
        }
    }
}
