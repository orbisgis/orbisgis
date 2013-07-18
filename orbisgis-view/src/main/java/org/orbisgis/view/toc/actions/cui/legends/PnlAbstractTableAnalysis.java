package org.orbisgis.view.toc.actions.cui.legends;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.sif.components.WideComboBox;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.model.KeyCellRenderer;
import org.orbisgis.view.toc.actions.cui.legends.model.PreviewCellRenderer;
import org.orbisgis.view.toc.actions.cui.legends.panels.ColorScheme;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.*;
import java.util.List;

/**
 * Base class for the analysis that relies on a "table", i.e., Value and
 * Interval Classifications. Provides some useful methods related to color
 * generation and JTable management, and a framework used by its inheritors.
 *
 * @author Alexis Guéganno
 */
public abstract class PnlAbstractTableAnalysis<K, U extends LineParameters>
        extends AbstractFieldPanel implements ILegendPanel, ActionListener {
    public static final String FALLBACK = "Fallback";
    public static final String CREATE_CLASSIF = "Create classification";
    public static final Logger LOGGER = Logger.getLogger(PnlAbstractTableAnalysis.class);
    public final static int CELL_PREVIEW_WIDTH = CanvasSE.WIDTH/2;
    public final static int CELL_PREVIEW_HEIGHT = CanvasSE.HEIGHT/2;
    private static final I18n I18N = I18nFactory.getI18n(PnlAbstractTableAnalysis.class);
    private MappedLegend<K,U> legend;
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private DataSource ds;
    private JTable table;
    private String id;
    protected CanvasSE fallbackPreview;
    protected WideComboBox fieldCombo;
    protected static final String ENABLE_BORDER = I18n.marktr("Enable border");
    protected static final String CLASSIFICATION_SETTINGS = I18n.marktr("Classification settings");

    @Override
    public void initialize(LegendContext lc) {
        if (getLegend() == null) {
            setLegend(getEmptyAnalysis());
            initPreview();
        }
        setGeometryType(lc.getGeometryType());
        ILayer layer = lc.getLayer();
        if (layer != null && layer.getDataSource() != null) {
            setDataSource(layer.getDataSource());
        }
    }

    protected void setLegendImpl(MappedLegend<K,U> leg){
        this.legend =  leg;
    }

    /**
     * Create and return a combobox for the border width unit,
     * adding an appropriate action listener to update the preview.
     *
     * @return A combobox for the border width unit
     */
    protected abstract JComboBox getUOMComboBox();

    /**
     * Creates and fill the combo box that will be used to compute the
     * analysis.
     *
     * @return  A ComboBox linked to the underlying MappedLegend that configures the analysis field.
     */
    public WideComboBox getFieldComboBox() {
        if (ds != null) {
            WideComboBox jcc = getFieldCombo(ds);
            ActionListener acl2 = EventHandler.create(ActionListener.class,
                    this, "updateField", "source.selectedItem");
            String field = legend.getLookupFieldName();
            if (field != null && !field.isEmpty()) {
                jcc.setSelectedItem(field);
            }
            jcc.addActionListener(acl2);
            updateField((String) jcc.getSelectedItem());
            ((JLabel)jcc.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            return jcc;
        } else {
            return new WideComboBox();
        }
    }

    /**
     * Sets the associated data source
     * @param newDS the new {@link org.gdms.data.DataSource}.
     */
    protected void setDataSource(DataSource newDS){
        ds = newDS;
    }

    /**
     * Gets the associated DataSource
     * @return The inner DataSource.
     */
    public DataSource getDataSource(){
        return ds;
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
            K key = getNotUsedKey();
            U lp = legend.getFallbackParameters();
            legend.put(key, lp);
            updateTable();
        } else if (e.getActionCommand().equals(REMOVE)){
            int col = getKeyColumn();
            int row = getJTable().getSelectedRow();
            if(col>=0 && row >= 0){
                K key = (K)getJTable().getValueAt(row, col);
                legend.remove(key);
                updateTable();
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
     * Gets the JTable used to draw the mapping
     * @return the JTable
     */
    public JTable getJTable(){
        return table;
    }
    /**
     * Build the panel that contains the JTable where the map is displayed.
     * @return The panel that contains the JTable where the map is displayed.
     */
    public JPanel getTablePanel() {
        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        jp.setBorder(BorderFactory.createTitledBorder(getTitleBorder()));
        //we build the table here
        AbstractTableModel model = getTableModel();
        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        table.setRowHeight(CELL_PREVIEW_HEIGHT);
        final int previewWidth = CELL_PREVIEW_WIDTH;
        TableColumn previews = table.getColumnModel().getColumn(getPreviewColumn());
        previews.setWidth(previewWidth);
        previews.setMinWidth(previewWidth);
        previews.setMaxWidth(previewWidth);
        previews.setCellRenderer(new PreviewCellRenderer(table, getPreviewClass(), (MappedLegend<K, U>) getLegend()));
        previews.setCellEditor(getParametersCellEditor());
        //We put a default editor on the keys.
        TableColumn keys = table.getColumnModel().getColumn(getKeyColumn());
        TableCellEditor ker = getKeyCellEditor();
        CellEditorListener cel = EventHandler.create(CellEditorListener.class, model, "fireTableDataChanged", null, "editingStopped");
        ker.addCellEditorListener(cel);
        keys.setCellEditor(ker);
        keys.setCellRenderer(new KeyCellRenderer(table, getPreviewClass(), (MappedLegend<K,U>)getLegend()));
        JScrollPane jsp = new JScrollPane(table);
        // Set the viewport to view 6 rows with a width of 400 pixels.
        int rowHeight = table.getRowHeight();
        int tableWidth = 400;
        int tableHeight = rowHeight*6;
        table.setPreferredScrollableViewportSize(
                new Dimension(tableWidth, tableHeight));
        table.setDoubleBuffered(true);
        jsp.setAlignmentX((float).5);
        jp.add(jsp, BorderLayout.CENTER);
        table.doLayout();
        jp.add(getButtonsPanel());
        // Set the unit (click once on down arrow: scroll down one row)
        // and block (one mouse scroll wheel: scroll down one half page)
        // increments.
        JScrollBar verticalScrollBar = jsp.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(rowHeight);
        verticalScrollBar.setBlockIncrement(tableHeight);
        // Set the scroll mode.
        jsp.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        return jp;
    }

    protected abstract String getTitleBorder();

    /**
     * Replaces the inner JTable.
     * @param t The new JTable.
     */
    protected void setJTable(JTable t){
        table = t;
    }

    /**
     * Update the table if it is not null.
     */
    public void updateTable(){
        JTable table = getJTable();
        if(table != null){
            AbstractTableModel model = (AbstractTableModel) table.getModel();
            model.fireTableDataChanged();
        }
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Update the inner CanvasSE. It updates its symbolizer and forces the image to be redrawn.
     */
    public final void updatePreview(Object source){
        JComboBox jcb = (JComboBox) source;
        updateLUComboBox(jcb.getSelectedIndex());
        CanvasSE prev = getPreview();
        prev.setSymbol(getFallbackSymbolizer());
        updateTable();
    }


    /**
     * We take the fallback configuration and copy it for each key, changing the colour. The colour management is
     * made thanks to {@link #getColouredParameters(org.orbisgis.legend.thematic.LineParameters, java.awt.Color)}.
     * The base colours are found in the given color scheme. We will put the colors from it. If there are more
     * elements in the input set than in the ColorScheme, we compute colours from the one in the colour scheme.
     * To achieve this goal, we make linear interpolations in the RGB space.
     * @param set A set of keys we use as a basis.
     * @param pm The progress monitor that can be used to stop the process.
     * @param scheme the input palette
     * @return A fresh unique value analysis.
     */
    public final MappedLegend<K,U> createColouredClassification(SortedSet<K> set,
                                                                org.orbisgis.progress.ProgressMonitor pm,
                                                                       ColorScheme scheme) {

        int expected = set.size();
        int included = scheme.getColors().size();
        List<Color> colors;
        Comparator<K> comp = getComparator();
        if(expected <= included ){
            colors = scheme.getSubset(expected);
        } else {
            List<Color> known = scheme.getColors();
            colors = new ArrayList<Color>();
            int others = expected - included;
            int intervals = included - 1;
            int div = others / intervals;
            int remain = others  - intervals * div;
            for(int i = 0; i<intervals; i++){
                int ins = remain > 0 ? div +1 : div;
                remain --;
                Color start = known.get(i);
                Color end = known.get(i+1);
                List<Color> temp = getColorList(ins,start, end);
                colors.add(known.get(i));
                colors.addAll(temp);
            }
            colors.add(known.get(known.size()-1));
        }
        int size = set.size();
        double m = size == 0 ? 0 : 50.0/(double)size;
        int i=0;
        int n = 0;
        pm.progressTo(50);
        pm.startTask(CREATE_CLASSIF , 100);
        U lp = ((MappedLegend<K,U>)getLegend()).getFallbackParameters();
        MappedLegend<K,U> newRL = getEmptyAnalysis();
        newRL.setComparator(comp);
        newRL.setFallbackParameters(lp);
        if(set.size() != colors.size()){
            throw new IllegalStateException("Wrong state");
        }
        Iterator<Color> it = colors.iterator();
        for(K s : set){
            U value = getColouredParameters(lp, it.next());
            newRL.put(s, value);
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
        pm.endTask();
        pm.progressTo(100);
        return newRL;
    }

    /**
     * Retrieve a list of colours that are on the start-end line in the RGB space. Note that the input colours
     * are not included in the returned list.
     * @param num The number of colours we want
     * @param start The starting colour
     * @param end The ending colour
     * @return The list of computed colours
     */
    public List<Color> getColorList(int num, Color start, Color end){
        int actual = num+2;
        List<Color> ret = new ArrayList<Color>();
        int redStart = start.getRed();
        int greenStart = start.getGreen();
        int blueStart = start.getBlue();
        int alphaStart = start.getAlpha();
        double redThreshold;
        double greenThreshold;
        double blueThreshold;
        double alphaThreshold;
        if(actual <= 1){
            redThreshold = 0;
            greenThreshold = 0;
            blueThreshold = 0;
            alphaThreshold = 0;
        } else {
            redThreshold = ((double)(redStart-end.getRed()))/(actual+1);
            greenThreshold = ((double)(greenStart-end.getGreen()))/(actual+1);
            blueThreshold = ((double)(blueStart-end.getBlue()))/(actual+1);
            alphaThreshold = ((double)(alphaStart-end.getAlpha()))/(actual+1);
        }
        for(int j=0; j<actual; j++){
            Color newCol = new Color(redStart-(int)(redThreshold*j),
                    greenStart-(int)(j*greenThreshold),
                    blueStart-(int)(j*blueThreshold),
                    alphaStart-(int)(j*alphaThreshold));
            ret.add(newCol);
        }
        ret.remove(0);
        ret.remove(ret.size()-1);
        return ret;
    }

    /**
     * Gets a unique symbol configuration whose only difference with {@code fallback} is one of its color set to {@code
     * c}.
     * @param fallback The original configuration
     * @param c The new colour
     * @return A new configuration.
     */
    public abstract U getColouredParameters(U fallback, Color c);

    @Override
    public void setId(String newId) {
        id = newId;
    }

    @Override
    public CanvasSE getPreview() {
        if (fallbackPreview == null) {
            initPreview();
        }
        return fallbackPreview;
    }

    /**
     * Init the preview of the fallback symbol.
     */
    public abstract void initPreview();

    /**
     * Initialize the panels.
     */
    public void initializeLegendFields() {
        this.removeAll();

        JPanel glob = new JPanel(new MigLayout("wrap 2"));

        //Fallback symbol
        glob.add(getSettingsPanel());

        //Classification generator
        glob.add(getCreateClassificationPanel());

        //Table for the recoded configurations
        glob.add(getTablePanel(), "span 2, growx");
        this.add(glob);
        this.revalidate();
    }

    /**
     * Initialize and return the settings panel.
     *
     * @return Settings panel
     */
    protected JPanel getSettingsPanel() {
        JPanel jp = new JPanel(new MigLayout("wrap 2", COLUMN_CONSTRAINTS));
        jp.setBorder(BorderFactory.createTitledBorder(I18N.tr("General settings")));

        // Field chooser
        jp.add(new JLabel(FIELD));
        fieldCombo = getFieldComboBox();
        jp.add(fieldCombo, COMBO_BOX_CONSTRAINTS);

        // Unit of measure - line width
        jp.add(new JLabel(I18N.tr(LINE_WIDTH_UNIT)));
        jp.add(getUOMComboBox(), COMBO_BOX_CONSTRAINTS);

        beforeFallbackSymbol(jp);

        // Fallback symbol
        jp.add(getPreview(), "span 2, align center");
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
     * Gets the JPanel that gathers all the buttons and labels to create a classification from scratch;
     * @return The JPanel used to create a classification from scratch.
     */
    public abstract JPanel getCreateClassificationPanel();

    /**
     * Gets an empty analysis that can be used ot build a panel equivalent to the caller.
     * @return an empty analysis
     */
    public abstract MappedLegend<K,U> getEmptyAnalysis();

    /**
     * Get a key that is not already used in the inner MappedLegend
     * @return A not already used key.
     */
    public abstract K getNotUsedKey();

    /**
     * Gets the model used to build the JTable.
     * @return The table model.
     */
    public abstract AbstractTableModel getTableModel();

    /**
     * Gets the editor used to configure a cell with a preview.
     * @return A cell editor.
     */
    public abstract TableCellEditor getParametersCellEditor();

    /**
     * Gets the editor used to configure a key of the table
     * @return A cell editor.
     */
    public abstract TableCellEditor getKeyCellEditor();

    /**
     * Gets the index of the column used to display previews in the table.
     * @return the index of the preview column.
     */
    public abstract int getPreviewColumn();

    /**
     * Gets the index of the column used to display keys in the table.
     * @return the index of the key column.
     */
    public abstract int getKeyColumn();

    /**
     * Gets the constant Symbolizer obtained when using all the constant and fallback values of the original Symbolizer.
     * @return The fallback Symbolizer.
     */
    public abstract Symbolizer getFallbackSymbolizer();

    /**
     * Gets the Class of the keys used in the map.
     * @return The Class of the type used for the map's keys.
     */
    public abstract Class getPreviewClass();

    /**
     * Gets the name of the field on which we will perform the analysis
     * @return The name of the field.
     */
    public String getFieldName() {
        return fieldCombo.getSelectedItem().toString();
    }

    /**
     * Get the comparator to be used to retrieve the values sorted the best way.
     * @return A comparator that can be used with the keys of the associated mapping.
     */
    public  Comparator<K> getComparator(){
        return null;
    }
}
