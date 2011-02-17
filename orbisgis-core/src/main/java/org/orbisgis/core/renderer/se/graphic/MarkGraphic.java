/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */

package org.orbisgis.core.renderer.se.graphic;


import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.persistance.se.MarkGraphicType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;

import org.orbisgis.core.Services;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.OnlineResource;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.transform.Transform;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.ViewBoxNode;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.utils.I18N;

public final class MarkGraphic extends Graphic implements FillNode, StrokeNode, ViewBoxNode {

    public static final double defaultSize = 3;
    private MarkGraphicSource source;
    private ViewBox viewBox;
    private RealParameter pOffset;
    private Halo halo;
    private Fill fill;
    private Stroke stroke;
    private RealParameter markIndex;
    // cached shape : only available with shape that doesn't depends on features
    private Shape shape;
    private String mimeType;

    public MarkGraphic() {
    }

    public void setTo3mmCircle() {
        this.setUom(Uom.MM);
        this.setSource(WellKnownName.CIRCLE);
        this.setViewBox(new ViewBox(new RealLiteral(defaultSize)));
        this.setFill(new SolidFill());
        ((RealLiteral) ((SolidFill) this.getFill()).getOpacity()).setValue(100.0);
        this.setStroke(new PenStroke());
    }

    MarkGraphic(JAXBElement<MarkGraphicType> markG) throws IOException, InvalidStyle {
        MarkGraphicType t = markG.getValue();

        if (t.getUnitOfMeasure() != null) {
            this.setUom(Uom.fromOgcURN(t.getUnitOfMeasure()));
        }

        if (t.getViewBox() != null) {
            this.setViewBox(new ViewBox(t.getViewBox()));
        }

        if (t.getPerpendicularOffset() != null) {
            this.setPerpendicularOffset(SeParameterFactory.createRealParameter(t.getPerpendicularOffset()));
        }

        if (t.getTransform() != null) {
            this.setTransform(new Transform(t.getTransform()));
        }

        if (t.getHalo() != null) {
            this.setHalo(new Halo(t.getHalo()));
        }

        if (t.getFill() != null) {
            this.setFill(Fill.createFromJAXBElement(t.getFill()));
        }

        if (t.getStroke() != null) {
            this.setStroke(Stroke.createFromJAXBElement(t.getStroke()));
        }


        // Source
        if (t.getWellKnownName() != null) {
            this.setSource(WellKnownName.fromString(t.getWellKnownName()));
        } else {
            if (t.getOnlineResource() != null) {
                this.setSource((MarkGraphicSource) new OnlineResource(t.getOnlineResource()));
            } else if (t.getInlineContent() != null) {
                // TODO Not yer implemented
            }

            if (t.getMarkIndex() != null) {
                this.setMarkIndex(SeParameterFactory.createRealParameter(t.getMarkIndex()));
            }

            this.mimeType = t.getFormat();
        }
    }

    @Override
    public Fill getFill() {
        return fill;
    }

    @Override
    public void setFill(Fill fill) {
        this.fill = fill;
        if (fill != null) {
            fill.setParent(this);
        }
    }

    public Halo getHalo() {
        return halo;
    }

    public void setHalo(Halo halo) {
        this.halo = halo;
        if (halo != null) {
            halo.setParent(this);
        }
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
        if (stroke != null) {
            stroke.setParent(this);
        }
    }

    @Override
    public ViewBox getViewBox() {
        return viewBox;
    }

    @Override
    public void setViewBox(ViewBox viewBox) {

        if (viewBox == null) {
            viewBox = new ViewBox();
        }

        this.viewBox = viewBox;

        viewBox.setParent(this);
        //updateGraphic();
    }

    public MarkGraphicSource getSource() {
        return source;
    }

    public RealParameter getpOffset() {
        return pOffset;
    }

    public void setPerpendicularOffset(RealParameter pOffset) {
        this.pOffset = pOffset;
        if (this.pOffset != null) {
            this.pOffset.setContext(RealParameterContext.realContext);
        }
    }

    private void setMarkIndex(RealParameter mIndex) {
        this.markIndex = mIndex;
        this.markIndex.setContext(RealParameterContext.nonNegativeContext);
    }

    /*
     * This method must be called after each modification of uom, viewbox, source
     *
     */
    @Override
    public void updateGraphic() {
        try {
            shape = source.getShape(viewBox, null, -1, null, null, markIndex, mimeType);
        } catch (Exception e) {
            Services.getErrorManager().error("Could not update graphic", e);
            shape = null;
        }
    }

    public void setSource(MarkGraphicSource source) {
        this.source = source;

        if (source instanceof OnlineResource) {
            // Add listener which update markIndex context!
        }
        //updateGraphic();
    }

    /**
     * @param ds
     * @param fid
     * @throws ParameterException
     * @throws IOException
     */
    @Override
    public RenderableGraphics getRenderableGraphics(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {
        Shape shp;

        // If the shape doesn't depends on feature (i.e. not null), we used the cached one
        if (shape == null) {
            if (source != null) {
                shp = source.getShape(viewBox, sds, fid, mt.getScaleDenominator(), mt.getDpi(), markIndex, mimeType);
            } else {
                shp = WellKnownName.CIRCLE.getShape(viewBox, sds, fid, mt.getScaleDenominator(), mt.getDpi(), markIndex, mimeType);
            }
        } else {
            shp = shape;
        }

        // Apply AT
        Shape atShp = shp;

        if (transform != null) {
            atShp = this.transform.getGraphicalAffineTransform(false, sds, fid, mt, shp.getBounds().getWidth(),
                    shp.getBounds().getHeight()).createTransformedShape(shp);
        }

        Rectangle2D bounds = atShp.getBounds2D();

        double margin = this.getMargin(sds, fid, mt);

        RenderableGraphics rg = Graphic.getNewRenderableGraphics(bounds, margin);

        if (halo != null) {
            halo.draw(rg, sds, fid, atShp, mt);
        }

        if (fill != null) {
            fill.draw(rg, sds, fid, atShp, selected, mt);
        }

        if (stroke != null) {
            if (pOffset != null) {
                double offset = Uom.toPixel(pOffset.getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                for (Shape offShp : ShapeHelper.perpendicularOffset(atShp, offset)){
                  if (offShp == null) {
                      Services.getErrorManager().error(I18N.getString("orbisgis.org.orbisgis.renderer.cannotCreatePerpendicularOffset"));
                  }else{
                      stroke.draw(rg, sds, fid, offShp, selected, mt);
                  }
                }
            } else{
                stroke.draw(rg, sds, fid, atShp, selected, mt);
            }
        }

        return rg;

    }

    /**
     * compute required extra space. This extra space equals the max bw stroke width and halo radius
     * @param ds
     * @param fid
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    private double getMargin(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        double sWidth = 0.0;
        double haloR = 0.0;
        double offset = 0.0;

        if (stroke != null) {
            sWidth += stroke.getMaxWidth(sds, fid, mt);
        }

        if (this.halo != null) {
            haloR = halo.getHaloRadius(sds, fid, mt);
        }

        if (this.pOffset != null) {
            offset = Uom.toPixel(pOffset.getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }

        double max = Math.max(sWidth, haloR);
        return Math.max(max, 2*offset);
    }

    @Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        double delta = 0.0;

        if (viewBox != null && viewBox.usable()) {
            Point2D dim = viewBox.getDimensionInPixel(sds, fid, defaultSize, defaultSize, mt.getScaleDenominator(), mt.getDpi());
            delta = Math.max(dim.getX(), dim.getY());
        } else {
            if (source != null){
                delta = source.getDefaultMaxWidth(sds, fid, delta, delta, markIndex, mimeType);
            } else {
                delta = WellKnownName.CIRCLE.getDefaultMaxWidth(sds, fid, delta, delta, markIndex, mimeType);
            }
        }

        delta += this.getMargin(sds, fid, mt);

        return delta;
    }

    @Override
    public JAXBElement<MarkGraphicType> getJAXBElement() {
        MarkGraphicType m = new MarkGraphicType();

        source.setJAXBSource(m);

        if (uom != null) {
            m.setUnitOfMeasure(uom.toURN());
        }

        if (markIndex != null) {
            m.setMarkIndex(markIndex.getJAXBParameterValueType());
        }

        if (mimeType != null) {
            m.setFormat(mimeType);
        }

        if (transform != null) {
            m.setTransform(transform.getJAXBType());
        }

        if (pOffset != null) {
            m.setPerpendicularOffset(pOffset.getJAXBParameterValueType());
        }

        if (halo != null) {
            m.setHalo(halo.getJAXBType());
        }

        if (viewBox != null) {
            m.setViewBox(viewBox.getJAXBType());
        }

        if (fill != null) {
            m.setFill(fill.getJAXBElement());
        }

        if (stroke != null) {
            m.setStroke(stroke.getJAXBElement());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createMarkGraphic(m);
    }

    @Override
    public String dependsOnFeature() {

        String result = "";

        if (viewBox != null){
            result += " " + viewBox.dependsOnFeature();
        }
        if (pOffset != null){
            result += " " + pOffset.dependsOnFeature();
        }
        if (halo != null){
            result += " " + halo.dependsOnFeature();
        }
        if (fill != null){
            result += " " + fill.dependsOnFeature();
        }
        if (stroke != null){
            result += " " + stroke.dependsOnFeature();
        }
        if (transform != null){
            result += " " + transform.dependsOnFeature();
        }
        if (markIndex != null){
            result += " " + markIndex.dependsOnFeature();
        }

        return result.trim();
    }
}
