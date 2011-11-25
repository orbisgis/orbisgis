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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import net.opengis.se._2_0.core.MarkGraphicType;
import net.opengis.se._2_0.core.ObjectFactory;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Halo;
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
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.ViewBoxNode;
import org.orbisgis.core.renderer.se.common.VariableOnlineResource;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

/**
 * A {@code MarkGraphic} is created by stroking and filling a geometry line or shape.
 * It is built using the following parameters :
 * <ul><li>A definition of the contained graphic, that can be exclusively of one of these types :
 *      <ul><li> WellKnownText : as defined in {@link WellKnownName}.</li>
 *      <li>An online resource, defined with an URI (to a TrueType font, for instance,
 *          particularly if associated with a markindex value</li>
 *      <li>An inlineContent, ie a nested mark description</li>
 *      </ul></li>
 * <li>A Format, describing the MIME-type. This parameter is compulsory when
 * using an online or inline mark, as it is essential to handle efficiently
 * the desired objects.</li>
 * <li>A MarkIndex, used to retrieve the desired mark in a remote collection (a glyph
 * within a font, for instance).</li>
 * <li>A unit of measure</li>
 * <li>A viewbox, as described in {@link ViewBox}</li>
 * <li>A {@link Transform}, that describes an affine transformation that must be applied on the mark.</li>
 * <li>A {@link Halo}</li>
 * <li>A {@link Fill}</li>
 * <li>A {@link Stroke}</li>
 * <li>A perpendicular offset</li>
 * </ul>
 * @author maxence, alexis
 */
public final class MarkGraphic extends Graphic implements FillNode, StrokeNode,
        ViewBoxNode, UomNode, TransformNode {

        /**
         * The defautl size used to build {@code MarkGraphic} instances.
         */
    public static final double DEFAULT_SIZE = 3;
    //private MarkGraphicSource source;
    private Uom uom;
    private Transform transform;
    private StringParameter wkn;
    private VariableOnlineResource onlineResource;
    private ViewBox viewBox;
    private RealParameter pOffset;
    private Halo halo;
    private Fill fill;
    private Stroke stroke;
    private RealParameter markIndex;
    // cached shape : only available with shape that doesn't depends on features
    private Shape shape;
    private String mimeType;

    /**
     * Build a default {@code MarkGraphic}. It is built using the {@link WellKnownName#CIRCLE}
     * value. The mark will be rendered using default solid fill and pen stroke. The 
     * associated unit of measure is {@link Uom#MM}, and it has the {@link #DEFAULT_SIZE}.
     */
    public MarkGraphic() {
        this.setTo3mmCircle();
    }

    /**
     * Transform this {@code MarkGraphic} in default one, as described in the default constructor.
     */
    public void setTo3mmCircle() {
        this.setUom(Uom.MM);
        this.setWkn(new StringLiteral("circle"));

        this.setViewBox(new ViewBox(new RealLiteral(DEFAULT_SIZE)));
        this.setFill(new SolidFill());
        ((RealLiteral) ((SolidFill) this.getFill()).getOpacity()).setValue(100.0);
        this.setStroke(new PenStroke());
    }

    /**
     * Build a new {@code MarkGraphic} from the given {@code JAXBElement}.
     * @param markG
     * @throws IOException
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    MarkGraphic(JAXBElement<MarkGraphicType> markG) throws IOException, InvalidStyle, URISyntaxException {
        MarkGraphicType t = markG.getValue();

        if (t.getUom() != null) {
            this.setUom(Uom.fromOgcURN(t.getUom()));
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
            Stroke s = Stroke.createFromJAXBElement(t.getStroke());
            this.setStroke(s);
        }

        // Source
        if (t.getWellKnownName() != null) {
            this.setWkn(SeParameterFactory.createStringParameter(t.getWellKnownName()));
        } else {
            if (t.getOnlineResource() != null) {

                this.setOnlineResource(new VariableOnlineResource(t.getOnlineResource()));

            } else if (t.getInlineContent() != null) {
                // TODO Not yet implemented
            }

            if (t.getMarkIndex() != null) {
                this.setMarkIndex(SeParameterFactory.createRealParameter(t.getMarkIndex()));
            }

            this.mimeType = t.getFormat();
        }
    }

    @Override
    public Uom getUom() {
        if (uom != null) {
            return this.uom;
        } else {
            return parent.getUom();
        }
    }

    @Override
    public Uom getOwnUom() {
        return uom;
    }

    @Override
    public void setUom(Uom uom) {
        this.uom = uom;
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    @Override
    public void setTransform(Transform transform) {
        this.transform = transform;
        if (transform != null) {
            transform.setParent(this);
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

    /**
     * Get the {@link Halo} defined around this {@code MarkGraphic}.
     * @return 
     */
    public Halo getHalo() {
        return halo;
    }

    /**
     * Set the {@link Halo} defined around this {@code MarkGraphic}.
     * @param halo 
     */
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

    /*
    public MarkGraphicSource getSource() {
    return source;
    }*/
    /**
     * Get the perpendicular offset applied to this {@code MarkGraphic} before rendering.
     * @return 
     */
    public RealParameter getPerpendicularOffset() {
        return pOffset;
    }

    /**
     * Set the perpendicular offset applied to this {@code MarkGraphic} before rendering.
     * @param pOffset 
     */
    public void setPerpendicularOffset(RealParameter pOffset) {
        this.pOffset = pOffset;
        if (this.pOffset != null) {
            this.pOffset.setContext(RealParameterContext.REAL_CONTEXT);
        }
    }

    /**
     * Gets the index where to retrieve the mark in the collection associated to
     * this {@code MarkGraphic}.
     * @param mIndex 
     */
    private void setMarkIndex(RealParameter mIndex) {
        this.markIndex = mIndex;
        this.markIndex.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
    }

    /*
     * This method must be called after each modification of uom, viewbox, source
     *
     */
    @Override
    public void updateGraphic() {
        /*try {
        shape = getShape(null, -1, null);
        } catch (Exception e) {
        Services.getErrorManager().error("Could not update graphic", e);
        shape = null;
        }*/
        shape = null;
    }

    /**
     * Tries to retrieve the source that defines this {@code MarkGraphic} in the 
     * DataSource, at the diven index.
     * @param sds
     * @param fid
     * @return
     * @throws ParameterException 
     */
    private MarkGraphicSource getSource(DataSource sds, long fid) throws ParameterException {
        if (wkn != null) {
            return WellKnownName.fromString(wkn.getValue(sds, fid));
        } else if (onlineResource != null) {
            return onlineResource;
        }
        return null;
    }

    private Shape getShape(DataSource sds, long fid, MapTransform mt) throws ParameterException, IOException {

        Double dpi = null;
        Double scaleDenom = null;

        if (mt != null) {
            dpi = mt.getDpi();
            scaleDenom = mt.getScaleDenominator();
        }

        MarkGraphicSource source = getSource(sds, fid);

        if (source != null) {
            return source.getShape(viewBox, sds, fid, scaleDenom, dpi, markIndex, mimeType);
        } else {
            return null;
        }
    }

    @Override
    public Rectangle2D getBounds(DataSource sds, long fid,
            MapTransform mt) throws ParameterException, IOException {
        Shape shp = null;

        
        // If the shape doesn't depends on feature (i.e. not null), we used the cached one
        if (shape == null) {
            shp = getShape(sds, fid, mt);
        } else {
            shp = shape;
        }

        if (shp == null) {
            shp = WellKnownName.CIRCLE.getShape(viewBox, sds, fid, mt.getScaleDenominator(), mt.getDpi(), markIndex, mimeType);
        }

        /*if (transform != null) {
            return this.transform.getGraphicalAffineTransform(false, sds, fid, mt, shp.getBounds().getWidth(),
                    shp.getBounds().getHeight()).createTransformedShape(shp).getBounds2D();
        } else {*/
            return shp.getBounds2D();/*
        }*/
    }
    
    @Override
    public void draw(Graphics2D g2, DataSource sds, long fid,
            boolean selected, MapTransform mt, AffineTransform fat) throws ParameterException, IOException {
        Shape shp = null;

        AffineTransform at = new AffineTransform(fat);

        // If the shape doesn't depends on feature (i.e. not null), we used the cached one
        if (shape == null) {
            shp = getShape(sds, fid, mt);
        } else {
            shp = shape;
        }

        if (shp == null) {
            shp = WellKnownName.CIRCLE.getShape(viewBox, sds, fid, mt.getScaleDenominator(), mt.getDpi(), markIndex, mimeType);
        }


        if (transform != null) {
            at.concatenate(this.transform.getGraphicalAffineTransform(false, sds, fid, mt, shp.getBounds().getWidth(), shp.getBounds().getHeight()));
        }

        Shape atShp = at.createTransformedShape(shp);

        //If both of the fill and stroke are null, we must apply a default
        //behaviour (as we can't draw a mark graphic without both of them...).
        if (this.getFill() == null && this.getStroke() == null) {
            this.setFill(new SolidFill());
            this.setStroke(new PenStroke());
        }

        if (halo != null) {
            halo.draw(g2, sds, fid, selected, atShp, mt, true);
        }

        if (fill != null) {
            fill.draw(g2, sds, fid, atShp, selected, mt);
        }

        if (stroke != null) {
            double offset = 0.0;
            if (pOffset != null) {
                offset = Uom.toPixel(pOffset.getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
            }
            stroke.draw(g2, sds, fid, atShp, selected, mt, offset);
        }
    }

    /**
     * compute required extra space. This extra space equals the max bw stroke width and halo radius
     * @param ds
     * @param fid
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    /*private double getMargin(DataSource sds, long fid, MapTransform mt) throws ParameterException, IOException {
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
    return Math.max(max, 2 * offset);
    }*/

    /*
    @Override
    public double getMaxWidth(DataSource sds, long fid, MapTransform mt) throws ParameterException, IOException {
    double delta = 0.0;

    if (viewBox != null && viewBox.usable()) {
    Point2D dim = viewBox.getDimensionInPixel(sds, fid, DEFAULT_SIZE, DEFAULT_SIZE, mt.getScaleDenominator(), mt.getDpi());
    delta = Math.max(dim.getX(), dim.getY());
    } else {
    MarkGraphicSource source = getSource(sds, fid);
    if (source != null) {
    delta = source.getDefaultMaxWidth(sds, fid, delta, delta, markIndex, mimeType);
    } else {
    delta = WellKnownName.CIRCLE.getDefaultMaxWidth(sds, fid, delta, delta, markIndex, mimeType);
    }
    }

    delta += this.getMargin(sds, fid, mt);

    return delta;
    }*/
    /**
     * Get the online resource that defines this {@code MarkGraphic}.
     * @return 
     */
    public VariableOnlineResource getOnlineResource() {
        return onlineResource;
    }

    /**
     * Set the online resource that defines this {@code MarkGraphic}.
     * @param onlineResource 
     */
    public void setOnlineResource(VariableOnlineResource onlineResource) {
        this.onlineResource = onlineResource;
        if (onlineResource != null) {
            wkn = null;
        }
    }

    /**
     * Gets the WellKnownName defining this {@code MarkGraphic}.
     * @return 
     */
    public StringParameter getWkn() {
        return wkn;
    }

    /**
     * Sets the WellKnownName defining this {@code MarkGraphic}.
     * @param wkn 
     */
    public void setWkn(StringParameter wkn) {
        this.wkn = wkn;
        if (this.wkn != null) {
            this.wkn.setRestrictionTo(WellKnownName.getValues());
            this.onlineResource = null;
        }
    }

    @Override
    public JAXBElement<MarkGraphicType> getJAXBElement() {
        MarkGraphicType m = new MarkGraphicType();

        if (wkn != null) {
            m.setWellKnownName(wkn.getJAXBParameterValueType());
        } else if (onlineResource != null) {
            onlineResource.setJAXBSource(m);
        }

        if (uom != null) {
            m.setUom(uom.toURN());
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

        if (wkn != null) {
            result += " " + wkn.dependsOnFeature();
        }
        if (viewBox != null) {
            result += " " + viewBox.dependsOnFeature();
        }
        if (pOffset != null) {
            result += " " + pOffset.dependsOnFeature();
        }
        if (halo != null) {
            result += " " + halo.dependsOnFeature();
        }
        if (fill != null) {
            result += " " + fill.dependsOnFeature();
        }
        if (stroke != null) {
            result += " " + stroke.dependsOnFeature();
        }
        if (transform != null) {
            result += " " + transform.dependsOnFeature();
        }
        if (markIndex != null) {
            result += " " + markIndex.dependsOnFeature();
        }

        return result.trim();
    }
}
