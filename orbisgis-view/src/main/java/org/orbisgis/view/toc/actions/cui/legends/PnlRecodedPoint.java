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
import org.orbisgis.core.renderer.se.CompositeSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.PointParameters;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.legend.thematic.recode.AbstractRecodedLegend;
import org.orbisgis.legend.thematic.recode.RecodedPoint;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.model.KeyEditorRecodedPoint;
import org.orbisgis.view.toc.actions.cui.legends.model.KeyEditorUniqueValue;
import org.orbisgis.view.toc.actions.cui.legends.model.ParametersEditorRecodedPoint;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelRecodedPoint;
import org.orbisgis.view.toc.actions.cui.legends.panels.UomCombo;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.util.Map;
import java.util.Set;

/**
 * "Value classification - Point" UI.
 *
 * @author alexis
 */
public class PnlRecodedPoint extends PnlAbstractUniqueValue<PointParameters> {
    public static final Logger LOGGER = Logger.getLogger(PnlRecodedLine.class);
    private static final I18n I18N = I18nFactory.getI18n(PnlRecodedLine.class);
    private String id;
    private ContainerItemProperties[] uoms;

    @Override
    public RecodedPoint getEmptyAnalysis() {
        return new RecodedPoint();
    }

    @Override
    public void initPreview() {
        System.out.println("    Called from initPreview RP");
        fallbackPreview = new CanvasSE(getFallbackSymbolizer());
        MouseListener l = EventHandler.create(MouseListener.class, this, "onEditFallback", "", "mouseClicked");
        fallbackPreview.addMouseListener(l);
    }

    public void onEditFallback(MouseEvent me){
        (getLegend()).setFallbackParameters(editCanvas(fallbackPreview));
    }

    /**
     * Builds a SIF dialog used to edit the given LineParameters.
     * @param cse The canvas we want to edit
     * @return The LineParameters that must be used at the end of the edition.
     */
    private PointParameters editCanvas(CanvasSE cse){
        RecodedPoint leg = (RecodedPoint) getLegend();
        PointParameters lps = leg.getFallbackParameters();
        UniqueSymbolPoint usa = getFallBackLegend();
        PnlUniquePointSE pls = new PnlUniquePointSE(false, leg.isStrokeEnabled());
        pls.setLegend(usa);
        if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
            usa = (UniqueSymbolPoint) pls.getLegend();
            PointParameters nlp = usa.getPointParameters();
            cse.setSymbol(usa.getSymbolizer());
            return nlp;
        } else {
            return lps;
        }
    }

    @Override
    public PointParameters getColouredParameters(PointParameters f, Color c) {
        return new PointParameters(f.getLineColor(), f.getLineOpacity(),f.getLineWidth(),f.getLineDash(),
                    c,f.getFillOpacity(),
                    f.getWidth(), f.getHeight(), f.getWkn());
    }

    private UniqueSymbolPoint getFallBackLegend(){
        RecodedPoint leg = (RecodedPoint)getLegend();
        UniqueSymbolPoint usl = new UniqueSymbolPoint(leg.getFallbackParameters());
        usl.setStrokeUom(leg.getStrokeUom());
        usl.setSymbolUom(leg.getSymbolUom());
        if(leg.isOnVertex()){
            usl.setOnVertex();
        } else {
            usl.setOnCentroid();
        }
        return usl;
    }

    @Override
    public AbstractTableModel getTableModel() {
        return new TableModelRecodedPoint((AbstractRecodedLegend<PointParameters>) getLegend());
    }

    @Override
    public TableCellEditor getPreviewCellEditor() {
        return new ParametersEditorRecodedPoint();
    }

    @Override
    public KeyEditorUniqueValue<PointParameters> getKeyCellEditor() {
        return new KeyEditorRecodedPoint();
    }

    @Override
    public void setLegend(Legend legend) {
        if (legend instanceof RecodedPoint) {
            if(getLegend() != null){
                Rule rule = getLegend().getSymbolizer().getRule();
                if(rule != null){
                    CompositeSymbolizer compositeSymbolizer = rule.getCompositeSymbolizer();
                    int i = compositeSymbolizer.getSymbolizerList().indexOf(this.getLegend().getSymbolizer());
                    compositeSymbolizer.setSymbolizer(i, legend.getSymbolizer());
                }
            }
            setLegendImpl((RecodedPoint)legend);
            this.initializeLegendFields();
        } else {
            throw new IllegalArgumentException(I18N.tr("You must use recognized RecodedArea instances in"
                        + "this panel."));
        }
    }

    @Override
    public void setGeometryType(int type) {
    }

    @Override
    public boolean acceptsGeometryType(int geometryType) {
        return (geometryType & SimpleGeometryType.ALL) != 0;
    }

    @Override
    public Legend copyLegend() {
        RecodedPoint rl = new RecodedPoint();
        RecodedPoint leg = (RecodedPoint) getLegend();
        leg.setStrokeEnabled(rl.isStrokeEnabled());
        Set<Map.Entry<String,PointParameters>> entries = leg.entrySet();
        for(Map.Entry<String,PointParameters> entry : entries){
            rl.put(entry.getKey(),entry.getValue());
        }
        rl.setFallbackParameters(leg.getFallbackParameters());
        rl.setLookupFieldName(leg.getLookupFieldName());
        return rl;
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
}
