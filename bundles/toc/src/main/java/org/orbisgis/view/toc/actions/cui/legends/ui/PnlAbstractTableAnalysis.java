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
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.thematic.EnablesStroke;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.OnVertexOnCentroid;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.legend.thematic.uom.StrokeUom;
import org.orbisgis.legend.thematic.uom.SymbolUom;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.IClassificationLegend;
import org.orbisgis.view.toc.actions.cui.legends.components.ColorScheme;
import org.orbisgis.view.toc.actions.cui.legends.panels.SettingsPanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.TablePanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.Util;
import org.xnap.commons.i18n.I18n;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

/**
 * Root class for Value and Interval Classifications. Provides some methods for
 * color generation and JTable management.
 *
 * @author Alexis Guéganno
 */
public abstract class PnlAbstractTableAnalysis<K, U extends LineParameters>
        extends AbstractFieldPanel implements IClassificationLegend {

    public static final Logger LOGGER = Logger.getLogger(PnlAbstractTableAnalysis.class);

    private MappedLegend<K,U> legend;
    private DataSource ds;
    private SettingsPanel<K, U> settingsPanel;
    protected CanvasSE fallbackPreview;
    protected TablePanel<K, U> tablePanel;

    private String id;

    public static final String FALLBACK = "Fallback";
    public static final String CREATE_CLASSIF = "Create classification";
    public static final String ENABLE_BORDER = I18n.marktr("Enable border");
    protected static final String CLASSIFICATION_SETTINGS = I18n.marktr("Classification settings");

    /**
     * Sets the DataSource to the LegendContext's layer's DataSource and keeps
     * a reference to the given legend.
     *
     * @param lc     LegendContext
     * @param legend Legend
     */
    public PnlAbstractTableAnalysis(LegendContext lc,
                                    MappedLegend<K, U> legend) {
        this.ds = lc.getLayer().getDataSource();
        this.legend = legend;
    }

    protected void setLegendImpl(MappedLegend<K,U> leg){
        this.legend =  leg;
    }

    /**
     * Gets the associated DataSource
     * @return The inner DataSource.
     */
    public DataSource getDataSource() {
        return ds;
    }

    @Override
    public MappedLegend<K,U> getLegend() {
        return legend;
    }

    protected abstract String getTitleBorder();

    @Override
    public String getId() {
        return id;
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
        U lp = (getLegend()).getFallbackParameters();
        MappedLegend<K,U> newRL = getEmptyAnalysis();
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
        postProcess(newRL);
        return newRL;
    }

    /**
     * Makes a postProcess operation on {@code ml} using the inner
     * legend. We keep properties handled by the following interfaces :
     * <ul><li>{@link OnVertexOnCentroid},</li>
     * <li>{@link EnablesStroke}</li>
     * <li>{@link StrokeUom}</li>
     * <li>{@link SymbolUom}</li>
     * </ul>
     *
     * @param ml The legend we want to process.
     */
    protected void postProcess(MappedLegend<K, U> ml) {
        MappedLegend<K,U> inner = getLegend();
        if (inner instanceof OnVertexOnCentroid &&
                ml instanceof OnVertexOnCentroid) {
                if (((OnVertexOnCentroid) inner).isOnVertex()) {
                    ((OnVertexOnCentroid) ml).setOnVertex();
                } else {
                    ((OnVertexOnCentroid) ml).setOnCentroid();
                }
        }
        if (inner instanceof EnablesStroke &&
                ml instanceof EnablesStroke) {
                ((EnablesStroke) ml).setStrokeEnabled(
                        ((EnablesStroke) inner).isStrokeEnabled());
        }
        ml.setStrokeUom(inner.getStrokeUom());
        if (inner instanceof SymbolUom &&
                ml instanceof SymbolUom) {
            ((SymbolUom) ml).setSymbolUom(
                    ((SymbolUom) inner).getSymbolUom());

        }
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
        ret.remove(ret.size() - 1);
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

    @Override
    public final void buildUI() {
        // TODO: Without this call to removeAll(), if the Simple Style Editor
        // is reopened, then the Classification UI is replaced by a Unique
        // Symbol UI. Also, this call is necessary because the whole UI is
        // recreated every time the user clicks "Create" (create classification).
        this.removeAll();

        JPanel glob = new JPanel(new MigLayout("wrap 2"));

        tablePanel = new TablePanel<K, U>(legend,
                getTitleBorder(),
                getTableModel(),
                getKeyCellEditor(),
                getKeyColumn(),
                getPreviewCellEditor(),
                getPreviewColumn(),
                getPreviewClass());

        settingsPanel = new SettingsPanel<K, U>(legend,
                getDataSource(),
                getPreview(),
                tablePanel);
        glob.add(settingsPanel);

        // Classification generator
        glob.add(getCreateClassificationPanel());

        // Table
        glob.add(tablePanel, "span 2, growx");
        this.add(glob);
        this.revalidate();
    }

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
     * Gets the model used to build the JTable.
     * @return The table model.
     */
    public abstract AbstractTableModel getTableModel();

    /**
     * Gets the editor used to configure a cell with a preview.
     * @return A cell editor.
     */
    public abstract TableCellEditor getPreviewCellEditor();

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
    public Symbolizer getFallbackSymbolizer() {
        return Util.getFallbackSymbolizer(legend);
    }

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
        return settingsPanel.getSelectedField();
    }
}
