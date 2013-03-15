/*
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
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.se.CompositeSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.legend.thematic.recode.RecodedLine;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.background.*;
import org.orbisgis.view.joblist.JobListItem;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.model.KeyEditorRecodedLine;
import org.orbisgis.view.toc.actions.cui.legends.model.ParametersEditorRecodedLine;
import org.orbisgis.view.toc.actions.cui.legends.model.PreviewCellRenderer;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelRecodedLine;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p></p>This panel must be used to manage all the parameters of a line symbolizer
 * which is configured thanks to a "simple" recoded PenStroke. All the parameters
 * of the PenStroke must be configured either with a Recode or a Literal, all
 * the Recode must be done with the same analysis field.</p>
 * <p>This panel proposes a way to build a classification from scratch. This feature comes fortunately with a
 * ProgressMonitor that can be used to cancel the building. This way, if accidentally trying to build a classification
 * on a field with a lot of different values, the user can still cancel the operation. The feeding of the underlying
 * recoded analysis becomes in fact really inefficient when it manages a lot of elements.</p>
 *
 * @author Alexis Guéganno
 */
public class PnlRecodedLine extends AbstractFieldPanel implements ILegendPanel, ActionListener {
    public final static Logger LOGGER = Logger.getLogger(PnlRecodedLine.class);
    private static I18n I18N = I18nFactory.getI18n(PnlRecodedLine.class);
    private static final String FALLBACK = "Fallback";
    private static final String COMPUTED = "Computed";
    private String id;
    private RecodedLine legend;
    private DataSource ds;
    private CanvasSE fallbackPreview;
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private JTable table;
    private JPanel colorConfig;
    private JComboBox fieldCombo;
    private JLabel endCol;
    private JLabel startCol;
    private final static String JOB_NAME = "recodeSelectDistinct";
    public final static String CREATE_CLASSIF = "Create classification";
    private SelectDistinctJob selectDistinct;
    private BackgroundListener background;
    public final static int CELL_PREVIEW_WIDTH = CanvasSE.WIDTH/2;
    public final static int CELL_PREVIEW_HEIGHT = CanvasSE.HEIGHT/2;

    @Override
    public Component getComponent() {
        initializeLegendFields();
        return this;
    }

    @Override
    public void initialize(LegendContext lc) {
        if (legend == null) {
            setLegend(new RecodedLine());
            initPreview();
        }
        setGeometryType(lc.getGeometryType());
        ILayer layer = lc.getLayer();
        if (layer != null && layer.getDataSource() != null) {
            ds = layer.getDataSource();
        }
    }

    @Override
    public ISELegendPanel newInstance() {
        return new PnlRecodedLine();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String newId) {
        id = newId;
    }

    @Override
    public String validateInput() {
        return "";
    }

    /**
     * This methods is called by EventHandler when the user clicks on the fall back's preview. It opens an UI that lets
     * the user edit the parameters of the fall back configuration and that apply it if the user clicks OK.
     * @param me The MouseEvent that caused the call to this method.
     */
    public void onEditFallback(MouseEvent me){
        legend.setFallbackParameters(editCanvas(fallbackPreview, legend.getFallbackParameters()));
    }

    /**
     * Builds a SIF dialog used to edit the given LineParameters.
     * @param cse The canvas we want to edit
     * @param lps The LineParameters to feed the canvas
     * @return The LineParameters that must be used at the end of the edition.
     */
    private LineParameters editCanvas(CanvasSE cse, LineParameters lps){
        UniqueSymbolLine usl = new UniqueSymbolLine(lps);
        PnlUniqueLineSE pls = new PnlUniqueLineSE(false);
        pls.setLegend(usl);
        if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
            usl = (UniqueSymbolLine) pls.getLegend();
            LineParameters nlp = usl.getLineParameters();
            cse.setSymbol(usl.getSymbolizer());
            cse.imageChanged();
            return nlp;
        } else {
            return lps;
        }
    }

    /**
     * Creates and fill the combo box that will be used to compute the
     * analysis.
     *
     * @return  A ComboBox linked to the underlying MappedLegend that configures the analysis field.
     */
    private JComboBox getFieldComboBox() {
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
     * Initializes the preview for the fallback configuration
     */
    private void initPreview() {
        UniqueSymbolLine usl = new UniqueSymbolLine(legend.getFallbackParameters());
        fallbackPreview = new CanvasSE(usl.getSymbolizer());
        MouseListener l = EventHandler.create(MouseListener.class, this, "onEditFallback", "", "mouseClicked");
        fallbackPreview.addMouseListener(l);
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

    /**
     * Build the panel that contains the JTable where the map is displayed.
     * @return The panel that contains the JTable where the map is displayed.
     */
    private JPanel getTablePanel() {
        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        jp.setBorder(BorderFactory.createTitledBorder(I18N.tr("Unique value classification")));
        //we build the table here
        TableModelRecodedLine model = new TableModelRecodedLine(legend);
        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        table.setRowHeight(CELL_PREVIEW_HEIGHT);
        final int previewWidth = CELL_PREVIEW_WIDTH;
        TableColumn previews = table.getColumnModel().getColumn(TableModelRecodedLine.PREVIEW_COLUMN);
        previews.setWidth(previewWidth);
        previews.setMinWidth(previewWidth);
        previews.setMaxWidth(previewWidth);
        previews.setCellRenderer(new PreviewCellRenderer(table, String.class, legend));
        previews.setCellEditor(new ParametersEditorRecodedLine());
        //We put a default editor on the keys.
        TableColumn keys = table.getColumnModel().getColumn(TableModelRecodedLine.KEY_COLUMN);
        KeyEditorRecodedLine ker = new KeyEditorRecodedLine();
        CellEditorListener  cel = EventHandler.create(CellEditorListener.class, model, "fireTableDataChanged", null, "editingStopped");
        ker.addCellEditorListener(cel);
        keys.setCellEditor(ker);
        JScrollPane jsp = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(400,200));
        jsp.setAlignmentX((float).5);
        jp.add(jsp, BorderLayout.CENTER);
        table.doLayout();
        jp.add(getButtonsPanel());
        return jp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ADD)){
            String key = legend.getNotUsedKey("newValue");
            LineParameters lp = legend.getFallbackParameters();
            legend.put(key, lp);
            TableModelRecodedLine model = (TableModelRecodedLine) table.getModel();
            model.fireTableDataChanged();
        } else if (e.getActionCommand().equals(REMOVE)){
            TableModelRecodedLine model = (TableModelRecodedLine) table.getModel();
            int col = table.getSelectedColumn();
            int row = table.getSelectedRow();
            if(col>=0 && row >= 0){
                String key = (String)table.getValueAt(row, col);
                legend.remove(key);
                model.fireTableDataChanged();
            }
        }
    }

    /**
     * Creates the two buttons add and remove, links them to this through actions and put them in a JPanel.
     * @return the two buttons in a JPanel.
     */
    private JPanel getButtonsPanel(){
        JPanel jp = new JPanel();
        JButton jb1 = new JButton(I18N.tr("Add"));
        jb1.setActionCommand(ADD);
        jb1.addActionListener(this);
        jp.add(jb1);
        jp.setAlignmentX((float).5);
        JButton remove = new JButton(I18N.tr("Remove"));
        remove.setActionCommand(REMOVE);
        remove.addActionListener(this);
        jp.add(jb1);
        jp.add(remove);
        jp.setAlignmentX((float) .5);
        return jp;
    }

    /**
     * Here are made all the initializations. Look at the specialized methods to have more details.
     */
    private void initializeLegendFields() {
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
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        glob.add(getCreateClassificationPanel(), gbc);
        //Table for the recoded configurations
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        glob.add(getTablePanel(), gbc);
        this.add(glob);
        this.revalidate();
    }

    private JPanel getUOMCombo(){
        JPanel pan = new JPanel();
        JComboBox jcb = getLineUomCombo(legend);
        pan.add(new JLabel(I18N.tr("Unit of measure :")));
        pan.add(jcb);
        return pan;
    }

    /**
     * Gets the JPanel that gathers all the buttons and labels to create a classification from scratch;
     * @return The JPanel used to create a classification from scratch.
     */
    private JPanel getCreateClassificationPanel() {
        JPanel ret = new JPanel();
        BoxLayout bl = new BoxLayout(ret, BoxLayout.Y_AXIS);
        ret.setLayout(bl);
        ret.setBorder(BorderFactory.createTitledBorder(I18N.tr("Generate")));
        //The button Group
        JPanel btnPnl = new JPanel();
        BoxLayout btnLayout = new BoxLayout(btnPnl, BoxLayout.Y_AXIS);
        btnPnl.setLayout(btnLayout);
        ButtonGroup bg = new ButtonGroup();
        JRadioButton fromFallback = new JRadioButton(I18N.tr("Colour from the fallback symbol"));
        fromFallback.setActionCommand(FALLBACK);
        JRadioButton computed = new JRadioButton(I18N.tr("Computed colour"));
        computed.setActionCommand(COMPUTED);
        bg.add(fromFallback);
        bg.add(computed);
        bg.setSelected(fromFallback.getModel(), true);
        fromFallback.setAlignmentX((float) 0);
        computed.setAlignmentX((float)0);
        btnPnl.add(fromFallback);
        btnPnl.add(computed);
        btnPnl.setAlignmentX((float).5);
        ret.add(btnPnl);
        //We build the panel used to configure the color before creating the classification.
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
        ret.add(colorConfig);
        //We still need a button to configure all of that
        JPanel btnPanel = new JPanel();
        JButton createButton = new JButton(I18N.tr("Create Classification"));
        createButton.setActionCommand("click");
        btnPanel.add(createButton);
        btnPanel.setAlignmentX((float).5);
        ret.add(btnPanel);
        //We still must feed all of that with listeners...
        //Colours
        ActionListener al1 = EventHandler.create(ActionListener.class, this, "onFromFallback");
        ActionListener al2 = EventHandler.create(ActionListener.class, this, "onComputed");
        fromFallback.addActionListener(al1);
        computed.addActionListener(al2);
        //Creation
        ActionListener btn = EventHandler.create(ActionListener.class, this, "onCreateClassification","");
        createButton.addActionListener(btn);
        //We disable the color config by default
        onFromFallback();
        return ret;
    }

    /**
     * Called to build a classification from the given data source and field. Makes a SELECT DISTINCT field FROM ds;
     * and feeds the legend that has been cleared prior to that.
     */
    public void onCreateClassification(ActionEvent e){
        if(e.getActionCommand().equals("click")){
            String fieldName = fieldCombo.getSelectedItem().toString();
            selectDistinct = new SelectDistinctJob(fieldName);
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
     * We take the fallback configuration and copy it for each key.
     * @param set A set of keys we use as a basis.
     */
    private RecodedLine createConstantClassification(HashSet<String> set, ProgressMonitor pm) {
        LineParameters lp = legend.getFallbackParameters();
        RecodedLine newRL = new RecodedLine();
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
     * We take the fallback configuration and copy it for each key, changing the colour.
     * @param set A set of keys we use as a basis.
     */
    private RecodedLine createColouredClassification(HashSet<String> set, ProgressMonitor pm) {
        LineParameters lp = legend.getFallbackParameters();
        RecodedLine newRL = new RecodedLine();
        newRL.setFallbackParameters(lp);
        int size = set.size();
        Color start = startCol.getBackground();
        Color end = endCol.getBackground();
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
            LineParameters value = new LineParameters(newCol, lp.getLineOpacity(), lp.getLineWidth(), lp.getLineDash());
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
    public void setLegend(Legend legend) {
        if (legend instanceof RecodedLine) {
            if(this.legend != null){
                Rule rule = this.legend.getSymbolizer().getRule();
                if(rule != null){
                    CompositeSymbolizer compositeSymbolizer = rule.getCompositeSymbolizer();
                    int i = compositeSymbolizer.getSymbolizerList().indexOf(this.getLegend().getSymbolizer());
                    compositeSymbolizer.setSymbolizer(i, legend.getSymbolizer());
                }
            }
            this.legend = (RecodedLine) legend;
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
                return geometryType == SimpleGeometryType.LINE ||
                        geometryType == SimpleGeometryType.POLYGON||
                        geometryType == SimpleGeometryType.ALL;
    }

    @Override
    public Legend copyLegend() {
        RecodedLine rl = new RecodedLine();
        Set<Map.Entry<String,LineParameters>> entries = legend.entrySet();
        for(Map.Entry<String,LineParameters> entry : entries){
            rl.put(entry.getKey(),entry.getValue());
        }
        rl.setFallbackParameters(legend.getFallbackParameters());
        rl.setLookupFieldName(legend.getLookupFieldName());
        return rl;
    }

    @Override
    public CanvasSE getPreview() {
        return fallbackPreview;
    }

    /**
     * This Job can be used as a background operation to retrieve a set containing the distinct data of a specific
     * field in a DataSource.
     */
    private class SelectDistinctJob implements BackgroundJob {

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
                RecodedLine rl;
                if(colorConfig.isEnabled() && result.size() > 0){
                    rl = createColouredClassification(result, pm);
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
                boolean moveOn = false;
                final int warn = 100;
                for(long i=0; i<rowCount; i++){
                    Value val = ds.getFieldValue(i, fieldIndex);
                    ret.add(val.toString());
                    if(ret.size() == warn && !moveOn){
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
            JDialog root = (JDialog) SwingUtilities.getRoot(PnlRecodedLine.this);
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
                JDialog root = (JDialog) SwingUtilities.getRoot(PnlRecodedLine.this);
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
                ((TableModelRecodedLine) table.getModel()).fireTableDataChanged();
                table.invalidate();
            }
        }


        @Override
        public void jobReplaced(Job job) {
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
}