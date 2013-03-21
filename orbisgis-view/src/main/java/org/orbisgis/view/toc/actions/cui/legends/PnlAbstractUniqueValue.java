/**
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
import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.se.CompositeSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.recode.AbstractRecodedLegend;
import org.orbisgis.legend.thematic.recode.RecodedLine;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.background.*;
import org.orbisgis.view.joblist.JobListItem;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelUniqueValue;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URL;
import java.util.HashSet;

/**
 * Methods shared by unique value analysis.
 * @author alexis
 */
public abstract class PnlAbstractUniqueValue<U extends LineParameters> extends AbstractFieldPanel implements ILegendPanel, ActionListener {
    public final static String CREATE_CLASSIF = "Create classification";
    public final static Logger LOGGER = Logger.getLogger(PnlAbstractUniqueValue.class);
    private static I18n I18N = I18nFactory.getI18n(PnlAbstractUniqueValue.class);
    private AbstractRecodedLegend<U> legend;
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private DataSource ds;
    private JPanel colorConfig;
    private JLabel endCol;
    private JLabel startCol;
    public final static int CELL_PREVIEW_WIDTH = CanvasSE.WIDTH/2;
    public final static int CELL_PREVIEW_HEIGHT = CanvasSE.HEIGHT/2;
    protected final static String JOB_NAME = "recodeSelectDistinct";


    @Override
    public void initialize(LegendContext lc) {
        if (getLegend() == null) {
            setLegend(new RecodedLine());
            initPreview();
        }
        setGeometryType(lc.getGeometryType());
        ILayer layer = lc.getLayer();
        if (layer != null && layer.getDataSource() != null) {
            setDataSource(layer.getDataSource());
        }
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
            this.legend = (AbstractRecodedLegend<U>) legend;
            this.initializeLegendFields();
        } else {
            throw new IllegalArgumentException(I18N.tr("You must use recognized RecodedLine instances in"
                        + "this panel."));
        }
    }
    /**
     * Creates and fill the combo box that will be used to compute the
     * analysis.
     *
     * @return  A ComboBox linked to the underlying MappedLegend that configures the analysis field.
     */
    public JComboBox getFieldComboBox() {
        if (ds != null) {
            JComboBox jcc = getFieldCombo(ds);
            ActionListener acl2 = EventHandler.create(ActionListener.class,
                        this, "updateField", "source.selectedItem");
            String field = legend.getLookupFieldName();
            if (field != null && !field.isEmpty()) {
                jcc.setSelectedItem(field);
            }
            jcc.addActionListener(acl2);
            updateField((String) jcc.getSelectedItem());
            return jcc;
        } else {
            return new JComboBox();
        }
    }

    /**
     * Sets the associated data source
     * @param newDS the new {@link DataSource}.
     */
    protected void setDataSource(DataSource newDS){
        ds = newDS;
    }

    /**
     * Used when the field against which the analysis is made changes.
     *
     * @param obj The new field.
     */
    public void updateField(String obj) {
        legend.setLookupFieldName(obj);
    }

    @Override
    public Legend getLegend() {
        return legend;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ADD)){
            String key = legend.getNotUsedKey("newValue");
            U lp = legend.getFallbackParameters();
            legend.put(key, lp);
            TableModelUniqueValue model = (TableModelUniqueValue) getJTable().getModel();
            model.fireTableDataChanged();
        } else if (e.getActionCommand().equals(REMOVE)){
            TableModelUniqueValue model = (TableModelUniqueValue) getJTable().getModel();
            int col = getJTable().getSelectedColumn();
            int row = getJTable().getSelectedRow();
            if(col>=0 && row >= 0){
                String key = (String)getJTable().getValueAt(row, col);
                legend.remove(key);
                model.fireTableDataChanged();
            }
        }
    }

    /**
     * Creates the two buttons add and remove, links them to this through actions and put them in a JPanel.
     * @return the two buttons in a JPanel.
     */
    public JPanel getButtonsPanel(){
        JPanel jp = new JPanel();
        JButton jb1 = new JButton(I18N.tr("Add"));
        jb1.setActionCommand(ADD);
        jb1.addActionListener(this);
        jp.add(jb1);
        jp.setAlignmentX((float) .5);
        JButton remove = new JButton(I18N.tr("Remove"));
        remove.setActionCommand(REMOVE);
        remove.addActionListener(this);
        jp.add(jb1);
        jp.add(remove);
        jp.setAlignmentX((float) .5);
        return jp;
    }

    /**
     * Gets the panel that can be used to produce the colours of a generated classification
     * @return The configuration panel.
     */
    public JPanel getColorConfig(){
        colorConfig = new JPanel();
        BoxLayout classLayout = new BoxLayout(colorConfig, BoxLayout.Y_AXIS);
        colorConfig.setLayout(classLayout);
        //The start colour
        JPanel start = new JPanel();
        start.add(new JLabel(I18N.tr("Start colour :")));
        startCol = getFilledLabel(Color.BLUE);
        start.add(startCol);
        start.setAlignmentX((float).5);
        colorConfig.add(start);
        //The end colour
        JPanel end = new JPanel();
        end.add(new JLabel(I18N.tr("End colour :")));
        endCol = getFilledLabel(Color.RED);
        end.setAlignmentX((float).5);
        end.add(endCol);
        colorConfig.add(end);
        //We add colorConfig to the global panel
        colorConfig.setAlignmentX((float).5);
        return colorConfig;
    }

    /**
     * Initialize the panels.
     */
    public abstract void initializeLegendFields();

    /**
     * Gets an empty analysis that can be used ot build a panel equivalent to the caller.
     * @return an empty analysis
     */
    public abstract AbstractRecodedLegend<U> getEmptyAnalysis();

    /**
     * Init the preview of the fallback symbol.
     */
    public abstract void initPreview();

    /**
     * Gets a unique symbol configuration whose only difference with {@code fallback} is one of its color set to {@code
     * c}.
     * @param fallback The original configuration
     * @param c The new colour
     * @return A new configuration.
     */
    public abstract U getColouredParameters(U fallback, Color c);

    /**
     * We take the fallback configuration and copy it for each key.
     * @param set A set of keys we use as a basis.
     * @param pm The progress monitor that can be used to stop the process.
     * @return A fresh unique value analysis.
     */
    public AbstractRecodedLegend<U> createConstantClassification(HashSet<String> set, ProgressMonitor pm) {
        U lp = legend.getFallbackParameters();
        AbstractRecodedLegend newRL = getEmptyAnalysis();
        newRL.setFallbackParameters(lp);
        int size = set.size();
        double m = size == 0 ? 0 : 90.0/(double)size;
        int i = 0;
        int n = 0;
        pm.startTask(CREATE_CLASSIF, 100);
        for(String s : set){
            newRL.put(s, lp);
            if(i*m>n){
                n++;
                pm.progressTo(n+10);
            }
            if(pm.isCancelled()){
                pm.endTask();
                return null;
            }
            i++;
        }
        pm.progressTo(100);
        pm.endTask();
        return newRL;
    }


    /**
     * We take the fallback configuration and copy it for each key, changing the colour. The colour management is
     * made thanks to {@link #getColouredParameters(org.orbisgis.legend.thematic.LineParameters, java.awt.Color)}.
     * @param set A set of keys we use as a basis.
     * @param pm The progress monitor that can be used to stop the process.
     * @param start the starting color for the gradient
     * @param end the ending color for the gradient
     * @return A fresh unique value analysis.
     */
    public final AbstractRecodedLegend<U> createColouredClassification(HashSet<String> set, ProgressMonitor pm,
                                                                       Color start, Color end) {
        U lp = legend.getFallbackParameters();
        AbstractRecodedLegend<U> newRL = getEmptyAnalysis();
        newRL.setFallbackParameters(lp);
        int size = set.size();
        int redStart = start.getRed();
        int greenStart = start.getGreen();
        int blueStart = start.getBlue();
        int alphaStart = start.getAlpha();
        int redThreshold;
        int greenThreshold;
        int blueThreshold;
        int alphaThreshold;
        if(size <= 1){
            redThreshold = 0;
            greenThreshold = 0;
            blueThreshold = 0;
            alphaThreshold = 0;
        } else {
            redThreshold = (redStart-end.getRed())/(size-1);
            greenThreshold = (greenStart-end.getGreen())/(size-1);
            blueThreshold = (blueStart-end.getBlue())/(size-1);
            alphaThreshold = (alphaStart-end.getAlpha())/(size-1);
        }
        double m = size == 0 ? 0 : 90.0/(double)size;
        int i=0;
        int n = 0;
        pm.startTask(CREATE_CLASSIF , 100);
        for(String s : set){
            Color newCol = new Color(redStart-redThreshold*i,
                        greenStart-i*greenThreshold,
                        blueStart-i*blueThreshold,
                        alphaStart-i*alphaThreshold);
            U value = getColouredParameters(lp, newCol);
            newRL.put(s, value);
            if(i*m>n){
                n++;
                pm.progressTo(n+10);
            }
            if(pm.isCancelled()){
                pm.endTask();
                return null;
            }
            i++;
        }
        pm.endTask();
        return newRL;
    }

    /**
     * Disables the colour configuration.
     */
    public void onFromFallback(){
        setFieldState(false,colorConfig);
    }

    /**
     * Enables the colour configuration.
     */
    public void onComputed(){
        setFieldState(true, colorConfig);
    }

    /**
     * Gets the JTable used to draw the mapping
     * @return the JTable
     */
    public abstract JTable getJTable();

    /**
     * Gets the constant Symbolizer obtained when using all the constant and fallback values of the original Symbolizer.
     * @return The fallback Symbolizer.
     */
    public abstract Symbolizer getFallbackSymbolizer();

    /**
     * Update the inner CanvasSE. It updates its symbolizer and forced the image to be redrawn.
     */
    public final void updatePreview(Object source){
        JComboBox jcb = (JComboBox) source;
        updateLUComboBox(jcb.getSelectedIndex());
        CanvasSE prev = getPreview();
        prev.setSymbol(getFallbackSymbolizer());
        prev.imageChanged();
        AbstractTableModel model = (AbstractTableModel) getJTable().getModel();
        model.fireTableDataChanged();
    }

    /**
     * This Job can be used as a background operation to retrieve a set containing the distinct data of a specific
     * field in a DataSource.
     */
    public class SelectDistinctJob implements BackgroundJob {

        private final String fieldName;
        private HashSet<String> result = null;

        /**
         * Builds the BackgroundJob.
         * @param f The name of the field we want the data from.
         */
        public SelectDistinctJob(String f){
            fieldName = f;
        }

        @Override
        public void run(ProgressMonitor pm) {
            result = getValues(pm);
            if(result != null){
                AbstractRecodedLegend<U> rl;
                if(colorConfig.isEnabled() && result.size() > 0){
                    Color start = startCol.getBackground();
                    Color end = endCol.getBackground();
                    rl = createColouredClassification(result, pm, start, end);
                } else {
                    rl = createConstantClassification(result, pm);
                }
                if(rl != null){
                    rl.setLookupFieldName(legend.getLookupFieldName());
                    setLegend(rl);
                }
            } else {
                pm.startTask(PnlRecodedLine.CREATE_CLASSIF, 100);
                pm.endTask();
            }
        }

        /**
         * Gathers all the distinct values of the input DataSource in a {@link HashSet}.
         * @param pm Used to be able to cancel the job.
         * @return The distinct values as String instances in a {@link HashSet} or null if the job has been cancelled.
         */
        public HashSet<String> getValues(final ProgressMonitor pm){
            HashSet<String> ret = new HashSet<String>();
            try {
                long rowCount=ds.getRowCount();
                pm.startTask(I18N.tr("Retrieving classes"), 10);
                pm.progressTo(0);
                double m = rowCount>0 ?(double)10/rowCount : 0;
                int n =0;
                int fieldIndex = ds.getFieldIndexByName(fieldName);
                final int warn = 100;
                for(long i=0; i<rowCount; i++){
                    Value val = ds.getFieldValue(i, fieldIndex);
                    ret.add(val.toString());
                    if(ret.size() == warn){
                        final UIPanel cancel = new CancelPanel(warn);
                        try{
                            SwingUtilities.invokeAndWait(new Runnable() {
                                @Override
                                public void run() {
                                    if(!UIFactory.showDialog(cancel,true, true)){
                                        pm.setCancelled(true);
                                    }
                                }
                            });
                        } catch (Exception ie){
                            LOGGER.warn(I18N.tr("The application has ended unexpectedly"));
                        }
                    }
                    if(i*m>n*10){
                        n++;
                        pm.progressTo(n);
                    }
                    if(pm.isCancelled()){
                        pm.endTask();
                        return null;
                    }
                }
            } catch (DriverException e) {
                LOGGER.error("IO error while handling the input data source : " + e.getMessage());
            }
            pm.endTask();
            return ret;
        }

        /**
         * Gets the generated DataSource.
         * @return The gathered information in a DataSource.
         */
        public HashSet<String> getResult() {
            return result;
        }

        @Override
        public String getTaskName() {
            return "select distinct";
        }
    }


    /**
     * This panel is used to let the user cancel classifications on more values than a given threshold.
     */
    private static class CancelPanel implements UIPanel {

        private int threshold;

        /**
         * Builds a new CancelPanel
         * @param thres The expected limit, displayed in the inner JLable.
         */
        public CancelPanel(int thres){
            super();
            threshold = thres;
        }

        @Override
        public URL getIconURL() {
            return UIFactory.getDefaultIcon();
        }

        @Override
        public String getTitle() {
            return I18N.tr("Continue ?");
        }

        @Override
        public String validateInput() {
            return null;
        }

        @Override
        public Component getComponent() {
            JPanel pan = new JPanel();
            JLabel lab = new JLabel();
            StringBuilder sb = new StringBuilder();
            sb.append(I18N.tr("<html><p>The analysis seems to generate more than "));
            sb.append(threshold);
            sb.append(I18N.tr(" different values...</p><p>Are you sure you want to continue ?</p></html>"));
            lab.setText(sb.toString());
            pan.add(lab);
            return pan;
        }
    }

    /**
     * This progress listener listens to the progression of the background operation that retrieves data from
     * the analysed source and builds a simple unique value classification with it.
     */
    public class DistinctListener implements ProgressListener {

        private JDialog window;
        private final JobListItem jli;
        private int count = 0;

        /**
         * Builds the listener from the given {@code JobListItem}. Not that the construction ends by displaying
         * a {@link JDialog} in modal mode that stays always on top of the application.
         * @param jli The item that will provide the progress bar.
         */
        public DistinctListener(JobListItem jli){
            this.jli = jli;
            JDialog root = (JDialog) SwingUtilities.getRoot(PnlAbstractUniqueValue.this);
            root.setEnabled(false);
            this.window = new JDialog(root,I18N.tr("Operation in progress..."));
            window.setLayout(new BorderLayout());
            window.setAlwaysOnTop(true);
            window.setVisible(true);
            window.setModal(true);
            window.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            window.add(jli.getItemPanel());
            window.setLocationRelativeTo(root);
            window.setMinimumSize(jli.getItemPanel().getPreferredSize());
        }

        @Override
        public void progressChanged(Job job) {
            window.pack();
        }

        @Override
        public void subTaskStarted(Job job) {
        }

        @Override
        public void subTaskFinished(Job job) {
            count ++;
            //I know I have two subtasks... This is unfortunate, but I don't find really efficient way to hide my
            //dialog from the listener without that..
            if(count >= 2){
                window.setVisible(false);
                JDialog root = (JDialog) SwingUtilities.getRoot(PnlAbstractUniqueValue.this);
                root.setEnabled(true);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        jli.dispose();
                    }
                });
            }

        }
    }

    /**
     * This backgroundListener waits for operation on {@code Job} with {@link PnlRecodedLine#JOB_NAME} as its name.
     * When such a {@link Job} is added, it adds a DistinctListener to the associated Job. When it is removed, it
     * retrieves the gathered information and build a new classification from it.
     */
    public class OperationListener implements BackgroundListener {

        @Override
        public  void jobAdded(Job job) {
            if(job.getId().is(new DefaultJobId(JOB_NAME))){
                JobListItem jli = new JobListItem(job).listenToJob(true);
                DistinctListener listener = new DistinctListener(jli);
                job.addProgressListener(listener);
            }
        }

        @Override
        public void jobRemoved(Job job) {
            if(job.getId().is(new DefaultJobId(JOB_NAME))){
                ((AbstractTableModel) getJTable().getModel()).fireTableDataChanged();
                getJTable().invalidate();
            }
        }


        @Override
        public void jobReplaced(Job job) {
        }
    }
}
