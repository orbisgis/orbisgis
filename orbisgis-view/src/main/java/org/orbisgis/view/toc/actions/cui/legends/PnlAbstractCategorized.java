package org.orbisgis.view.toc.actions.cui.legends;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelInterval;
import org.orbisgis.view.toc.actions.cui.legends.panels.ColorConfigurationPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionListener;
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
    /**
     * The default number of classes in a classification.
     */
    public static final Integer DEFAULT_CLASS_NUMBER = 5;
    private JTextField jtf;

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

    /**
     * Gets the minimum and maximum of the associated DataSource in the column whose name is {@code fieldName}.
     * @param fieldName The field name
     * @return The min and max in a Set. If something went wrong, the Set will contain {@link Double#POSITIVE_INFINITY}
     * and {@link Double#NEGATIVE_INFINITY}.
     */
    public List<Double> computeExtrema(String fieldName){
        DataSource ds = getDataSource();
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        try {
            int fieldIndex = ds.getMetadata().getFieldIndex(fieldName);
            long rowCount = ds.getRowCount();
            for(long i=0; i<rowCount; i++){
                Value val = ds.getFieldValue(i, fieldIndex);
                Double d = val.getAsDouble();
                if(d<min){
                    min = d;
                }
                if(d>max){
                    max =d;
                }
            }
        } catch (DriverException e) {
            LOGGER.warn(I18N.tr("The application has ended unexpectedly"));
        }
        List<Double> ret = new ArrayList<Double>();
        ret.add(min);
        ret.add(max);
        return ret;
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
        JButton createCl = new JButton(I18N.tr("Create Classification:"));
        if(classNumber == null){
            classNumber = DEFAULT_CLASS_NUMBER;
        }
        if(colorConfig == null){
            colorConfig = new ColorConfigurationPanel();
        }
        jtf = new JTextField(classNumber.toString(),3);
        ActionListener textListener = EventHandler.create(ActionListener.class, this, "updateFieldContent");
        jtf.addActionListener(textListener);
        JComboBox<String> methods = new JComboBox<String>(new String[]{"Equal Intervals"});
        JPanel btnPanel = new JPanel();
        createCl.setActionCommand("click");
        btnPanel.add(createCl);
        ActionListener btn = EventHandler.create(ActionListener.class, this, "onComputeClassification");
        createCl.addActionListener(btn);

        sec.add(numbLab);
        sec.add(jtf);
        sec.add(clLab);
        sec.add(methods);
        sec.add(btnPanel);
        ret.add(colorConfig);
        ret.add(sec);
        return ret;
    }

    /**
     * This method is called by EventHandler when clicking on the button dedicated to classification creation.
     */
    public void onComputeClassification(){
        List<Double> doubles = computeExtrema(getFieldName());
        TreeSet<Double> thresholds = getThresholds(doubles);
        if(!thresholds.isEmpty()){
            MappedLegend<Double,U> cl = createColouredClassification(
                    thresholds,
                    new NullProgressMonitor(),
                    colorConfig.getStartColor(),
                    colorConfig.getEndCol());
            cl.setLookupFieldName(((MappedLegend)getLegend()).getLookupFieldName());
            cl.setName(getLegend().getName());
            setLegend(cl);
        }
    }

    /**
     * Gets the thresholds. Simple method, will be replaced when stats will be used.
     * @param extrema
     * @return
     */
    private TreeSet<Double> getThresholds(List<Double> extrema){
        Double d1 = extrema.get(0);
        Double d2 = extrema.get(1);
        Double min = d1 < d2 ? d1 : d2;
        Double max = d1 > d2 ? d1 : d2;
        TreeSet<Double> ret = new TreeSet<Double>();
        if(min < Double.POSITIVE_INFINITY && max > Double.NEGATIVE_INFINITY){
            Double step = (max - min) / classNumber;
            for(int i = 0; i<classNumber;i++){
                ret.add(min+step*i);
            }
        }
        return ret;
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
}
