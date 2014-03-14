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
package org.orbisgis.view.toc.actions.cui.legend.ui;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.Services;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.corejdbc.ReadTable;
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
import org.orbisgis.view.toc.actions.cui.legend.components.ColorConfigurationPanel;
import org.orbisgis.view.toc.actions.cui.legend.components.ColorScheme;
import org.orbisgis.view.toc.actions.cui.legend.model.TableModelUniqueValue;
import org.orbisgis.view.toc.actions.cui.legend.panels.AbsPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        try(Connection connection = getDataSource().getConnection()) {
            int type = MetaData.getFieldType(connection, getTable(), fieldName);
            return AbstractRecodedLegend.getComparator(type);
        } catch (SQLException e) {
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
     * Called to build a classification from the given data source and field. Makes a SELECT DISTINCT field FROM tableIdentifier;
     * and feeds the legend that has been cleared prior to that.
     */
    public void onCreateClassification(ActionEvent e){
        if(e.getActionCommand().equals("click")){
            String fieldName = getFieldName();
            SelectDistinctJob selectDistinct = new SelectDistinctJob(fieldName);
            BackgroundManager bm = Services.getService(BackgroundManager.class);
            bm.nonBlockingBackgroundOperation(selectDistinct);
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
         * @param progress Used to be able to cancel the job.
         * @return The distinct values as String instances in a {@link HashSet} or null if the job has been cancelled.
         */
        public TreeSet<String> getValues(ProgressMonitor progress){
            Comparator<String> comparator = getComparator();
            TreeSet<String> ret = comparator != null ? new TreeSet<>(comparator) : new TreeSet<String>();
            try(Connection connection = getDataSource().getConnection();
                Statement st = connection.createStatement()) {
                PropertyChangeListener cancelPm = EventHandler.create(PropertyChangeListener.class, st, "cancel");
                progress.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, cancelPm);
                try(ResultSet rs = st.executeQuery("SELECT DISTINCT "+ TableLocation.quoteIdentifier(fieldName)+" FROM "+ getTable())) {
                    final ProgressMonitor pm = progress.startTask(I18N.tr("Retrieving classes"),
                            ReadTable.getRowCount(connection, getTable()));
                    final int warn = 100;
                    int size = 0;
                    while(rs.next()) {
                        ret.add(rs.getString(1));
                        size++;
                        if(size == warn){
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
                        pm.endTask();
                        if(pm.isCancelled()) {
                            return null;
                        }
                    }
                } finally {
                    progress.removePropertyChangeListener(cancelPm);
                }
            } catch (SQLException e) {
                LOGGER.error("IO error while handling the input data source",e);
            }
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
            lab.setText(I18N.tr("<html><p>The analysis seems to generate more than ") + threshold +
                    I18N.tr(" different values...</p><p>Are you sure you want to continue ?</p></html>"));
            pan.add(lab);
            return pan;
        }
    }
}
