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
package org.orbisgis.view.toc.actions.cui.legends.ui;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.legend.thematic.recode.AbstractRecodedLegend;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.ComponentUtil;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.background.*;
import org.orbisgis.view.joblist.JobListItem;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.legends.components.ColorConfigurationPanel;
import org.orbisgis.view.toc.actions.cui.legends.components.ColorScheme;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelUniqueValue;
import org.orbisgis.view.toc.actions.cui.legends.panels.AbsPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Root class for Value Classifications.
 *
 * <p>This panel proposes a way to build a classification from scratch. This
 * feature comes fortunately with a ProgressMonitor that can be used to cancel
 * the building. This way, if accidentally trying to build a classification on
 * a field with a lot of different values, the user can still cancel the
 * operation. The feeding of the underlying recoded analysis becomes in fact
 * really inefficient when it manages a lot of elements.</p>
 *
 * @author alexis
 */
public abstract class PnlAbstractUniqueValue<U extends LineParameters> extends PnlAbstractTableAnalysis<String,U> {

    private static final I18n I18N = I18nFactory.getI18n(PnlAbstractUniqueValue.class);
    public static final Logger LOGGER = Logger.getLogger(PnlAbstractUniqueValue.class);

    private ColorConfigurationPanel colorConfigPanel;
    private JPanel classifPanel;
    private BackgroundListener background;

    private static final String COMPUTED = "Computed";
    protected final static String JOB_NAME = "recodeSelectDistinct";

    /**
     * Contructor
     *
     * @param lc     LegendContext
     * @param legend Legend
     */
    public PnlAbstractUniqueValue(LegendContext lc,
                                  AbstractRecodedLegend<U> legend) {
        super(lc, legend);
    }

    /**
     * We take the fallback configuration and copy it for each key.
     * @param set A set of keys we use as a basis.
     * @param pm The progress monitor that can be used to stop the process.
     * @return A fresh unique value analysis.
     */
    public AbstractRecodedLegend<U> createConstantClassification(TreeSet<String> set, ProgressMonitor pm) {
        U lp = getLegend().getFallbackParameters();
        AbstractRecodedLegend<U> newRL = (AbstractRecodedLegend<U>) getEmptyAnalysis();
        newRL.setComparator(getComparator());
        newRL.setFallbackParameters(lp);
        int size = set.size();
        double m = size == 0 ? 0 : 90.0/(double)size;
        int i = 0;
        int n = 0;
        pm.progressTo(50);
        pm.startTask(CREATE_CLASSIF, 100);
        for(String s : set){
            newRL.put(s, lp);
            if(i*m>n){
                n++;
                pm.progressTo(50*i/size);
            }
            if(pm.isCancelled()){
                pm.endTask();
                return null;
            }
            i++;
        }
        pm.progressTo(100);
        pm.endTask();
        pm.progressTo(100);
        postProcess(newRL);
        return newRL;
    }

    /**
     * Get the comparator to be used to retrieve the values sorted the best way.
     * @return A comparator that can be used with the keys of the associated mapping.
     */
    public Comparator<String> getComparator(){
        String fieldName = getFieldName();
        DataSource ds = getDataSource();
        try {
            Metadata metadata = ds.getMetadata();
            Type type = metadata.getFieldType(metadata.getFieldIndex(fieldName));
            return AbstractRecodedLegend.getComparator(type);
        } catch (DriverException e) {
            LOGGER.warn(I18N.tr("Can't build the analysis with an accurate comparator."),e);
        }
        return null;
    }

    /**
     * Disables the colour configuration.
     */
    public void onFromFallback(){
        ComponentUtil.setFieldState(false, colorConfigPanel);
    }

    /**
     * Enables the colour configuration.
     */
    public void onComputed(){
        ComponentUtil.setFieldState(true, colorConfigPanel);
    }

    /**
     * Gets the JPanel that gathers all the buttons and labels to create a classification from scratch;
     * @return The JPanel used to create a classification from scratch.
     */
    @Override
    public JPanel getCreateClassificationPanel() {
        if(classifPanel == null){
            classifPanel = new JPanel(new MigLayout("wrap 1", "[" + AbsPanel.FIXED_WIDTH + "]"));
            classifPanel.setBorder(BorderFactory.createTitledBorder(
                    I18N.tr(CLASSIFICATION_SETTINGS)));

            // Choose between fallback color and a color scheme
            JRadioButton fromFallback = new JRadioButton(I18N.tr("Use the fallback color"));
            fromFallback.setActionCommand(FALLBACK);
            fromFallback.addActionListener(
                    EventHandler.create(ActionListener.class, this, "onFromFallback"));
            JRadioButton computed = new JRadioButton(I18N.tr("Use a color scheme:"));
            computed.setActionCommand(COMPUTED);
            computed.addActionListener(
                    EventHandler.create(ActionListener.class, this, "onComputed"));
            ButtonGroup bg = new ButtonGroup();
            bg.add(fromFallback);
            bg.add(computed);
            bg.setSelected(computed.getModel(), true);
            classifPanel.add(fromFallback);
            classifPanel.add(computed);
            // Color scheme panel
            ArrayList<String> names = new ArrayList<String>(ColorScheme.discreteColorSchemeNames());
            names.addAll(ColorScheme.rangeColorSchemeNames());
            colorConfigPanel = new ColorConfigurationPanel(names);
            classifPanel.add(colorConfigPanel, "align c, growx");
            // Create button
            JButton createButton = new JButton(I18N.tr("Create"));
            createButton.setActionCommand("click");
            createButton.addActionListener(
                    EventHandler.create(ActionListener.class, this, "onCreateClassification", ""));
            classifPanel.add(createButton, "align c");

            // Enable the color config by default
            onComputed();
        }
        return classifPanel;
    }

    @Override
    public int getPreviewColumn(){
        return TableModelUniqueValue.PREVIEW_COLUMN;
    }

    @Override
    public int getKeyColumn(){
        return TableModelUniqueValue.KEY_COLUMN;
    }

    @Override
    public Class getPreviewClass() {
        return String.class;
    }

    @Override
    public String getTitleBorder(){
        return I18N.tr("Unique value classification");
    }

    /**
     * Called to build a classification from the given data source and field. Makes a SELECT DISTINCT field FROM ds;
     * and feeds the legend that has been cleared prior to that.
     */
    public void onCreateClassification(ActionEvent e){
        if(e.getActionCommand().equals("click")){
            String fieldName = getFieldName();
            SelectDistinctJob selectDistinct = new SelectDistinctJob(fieldName);
            BackgroundManager bm = Services.getService(BackgroundManager.class);
            JobId jid = new DefaultJobId(JOB_NAME);
            if(background == null){
                background = new OperationListener();
                bm.addBackgroundListener(background);
            }
            bm.nonBlockingBackgroundOperation(jid, selectDistinct);
        }
    }

    /**
     * This Job can be used as a background operation to retrieve a set containing the distinct data of a specific
     * field in a DataSource.
     */
    public class SelectDistinctJob implements BackgroundJob {

        private final String fieldName;
        private TreeSet<String> result = null;

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
                if(colorConfigPanel.isEnabled() && result.size() > 0){
                    ColorScheme sc = colorConfigPanel.getColorScheme();
                    rl = (AbstractRecodedLegend<U>) createColouredClassification(result, pm, sc);
                    rl.setComparator(getComparator());
                } else {
                    rl = createConstantClassification(result, pm);
                }
                if(rl != null){
                    MappedLegend<String,U> legend = getLegend();
                    rl.setLookupFieldName(legend.getLookupFieldName());
                    rl.setName(legend.getName());
                    setLegend(rl);
                }
            } else {
                pm.startTask(CREATE_CLASSIF, 100);
                pm.endTask();
            }
        }

        /**
         * Gathers all the distinct values of the input DataSource in a {@link HashSet}.
         * @param pm Used to be able to cancel the job.
         * @return The distinct values as String instances in a {@link HashSet} or null if the job has been cancelled.
         */
        public TreeSet<String> getValues(final ProgressMonitor pm){
            Comparator<String> comparator = getComparator();
            TreeSet<String> ret = comparator != null ? new TreeSet<String>(comparator) : new TreeSet<String>();
            try {
                DataSource ds = getDataSource();
                long rowCount=ds.getRowCount();
                pm.startTask(I18N.tr("Retrieving classes"), 50);
                pm.progressTo(0);
                double m = rowCount>0 ?(double)50/rowCount : 0;
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
                    if(i*m>n && rowCount > 0){
                        n++;
                        pm.progressTo(50*i/rowCount);
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
        public TreeSet<String> getResult() {
            return result;
        }

        @Override
        public String getTaskName() {
            return "Creating classification...";
        }
    }


    /**
     * This panel is used to let the user cancel classifications on more values than a given threshold.
     */
    private static class CancelPanel implements UIPanel {

        private int threshold;

        /**
         * Builds a new CancelPanel
         * @param thres The expected limit, displayed in the inner JLabel.
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
                JTable table = tablePanel.getJTable();
                ((AbstractTableModel) table.getModel()).fireTableDataChanged();
                table.invalidate();
            }
        }


        @Override
        public void jobReplaced(Job job) {
        }
    }
}
