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
package org.orbisgis.core.renderer.se.graphic;

import net.opengis.se._2_0.core.MarkGraphicType;
import net.opengis.se._2_0.core.ObjectFactory;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.*;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.common.VariableOnlineResource;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.transform.Transform;

import javax.xml.bind.JAXBElement;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * @author Maxence Laurent, Alexis Guéganno
 */
public final class MarkGraphic extends Graphic implements FillNode, StrokeNode,
        ViewBoxNode, UomNode, TransformNode {

        /**
         * The default size used to build {@code MarkGraphic} instances.
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
     * @param markG The JAXB representation of a MarkGraphic
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
            return uom;
        } else if(getParent() instanceof UomNode){
            return ((UomNode)getParent()).getUom();
        } else {
            return Uom.PX;
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
     * @return The Halo drawn around the symbol
     */
    public Halo getHalo() {
        return halo;
    }

    /**
     * Set the {@link Halo} defined around this {@code MarkGraphic}.
     * @param halo The new halo to draw around the symbol.
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
     * @return The perpendicular offset
     */
    public RealParameter getPerpendicularOffset() {
        return pOffset;
    }

    /**
     * Set the perpendicular offset applied to this {@code MarkGraphic} before rendering.
     * @param pOffset The perpendicular offset
     */
    public void setPerpendicularOffset(RealParameter pOffset) {
        this.pOffset = pOffset;
        if (this.pOffset != null) {
            this.pOffset.setContext(RealParameterContext.REAL_CONTEXT);
            this.pOffset.setParent(this);
        }
    }

    /**
     * Gets the index where to retrieve the mark in the collection associated to
     * this {@code MarkGraphic}.
     * @param mIndex The index of the mark
     */
    private void setMarkIndex(RealParameter mIndex) {
        this.markIndex = mIndex;
        this.markIndex.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
        this.markIndex.setParent(this);
    }

    /*
     * This method must be called after each modification of uom, view box, source
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
     * DataSet, at the given index.
     * @param map The map of input values
     * @return The source that defines this MarkGraphic
     * @throws ParameterException 
     */
    private MarkGraphicSource getSource(Map<String,Object> map) throws ParameterException {
        if (wkn != null) {
            return WellKnownName.fromString(wkn.getValue(map));
        } else if (onlineResource != null) {
            return onlineResource;
        }
        return null;
    }

    private Shape getShape(Map<String,Object> map, MapTransform mt) throws ParameterException, IOException {

        Double dpi = null;
        Double scaleDenom = null;

        if (mt != null) {
            dpi = mt.getDpi();
            scaleDenom = mt.getScaleDenominator();
        }

        MarkGraphicSource source = getSource(map);

        if (source != null) {
            return source.getShape(viewBox, map, scaleDenom, dpi, markIndex, mimeType);
        } else {
            return null;
        }
    }

    @Override
    public Rectangle2D getBounds(Map<String,Object> map,
            MapTransform mt) throws ParameterException, IOException {
        Shape shp;

        
        // If the shape doesn't depends on feature (i.e. not null), we used the cached one
        if (shape == null) {
            shp = getShape(map, mt);
        } else {
            shp = shape;
        }

        if (shp == null) {
            shp = WellKnownName.CIRCLE.getShape(viewBox, map, mt.getScaleDenominator(), mt.getDpi(), markIndex, mimeType);
        }

        /*if (transform != null) {
            return this.transform.getGraphicalAffineTransform(false, map, mt, shp.getBounds().getWidth(),
                    shp.getBounds().getHeight()).createTransformedShape(shp).getBounds2D();
        } else {*/
            return shp.getBounds2D();/*
        }*/
    }
    
    @Override
    public void draw(Graphics2D g2, Map<String,Object> map,
            boolean selected, MapTransform mt, AffineTransform fat) throws ParameterException, IOException {
        Shape shp;

        AffineTransform at = new AffineTransform(fat);

        // If the shape doesn't depends on feature (i.e. not null), we used the cached one
        if (shape == null) {
            shp = getShape(map, mt);
        } else {
            shp = shape;
        }

        if (shp == null) {
            shp = WellKnownName.CIRCLE.getShape(viewBox, map, mt.getScaleDenominator(), mt.getDpi(), markIndex, mimeType);
        }


        if (transform != null) {
            at.concatenate(this.transform.getGraphicalAffineTransform(false, map, mt, shp.getBounds().getWidth(), shp.getBounds().getHeight()));
        }

        Shape atShp = at.createTransformedShape(shp);

        //If both of the fill and stroke are null, we must apply a default
        //behaviour (as we can't draw a mark graphic without both of them...).
        if (this.getFill() == null && this.getStroke() == null) {
            this.setFill(new SolidFill());
            this.setStroke(new PenStroke());
        }

        //We give the raw shape to the drawHalo method in order not to lose the 
        //type of the original Shape - It will be easier to compute the halo.
        //We give the transformed shape too... This way we are sure we won't
        //compute it twice, as it is a complicated operation.
        if (halo != null) {
            drawHalo(g2, map, selected, shp, atShp, mt, at);
        }

        if (fill != null) {
            fill.draw(g2, map, atShp, selected, mt);
        }

        if (stroke != null) {
            double offset = 0.0;
            if (pOffset != null) {
                offset = Uom.toPixel(pOffset.getValue(map), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
            }
            stroke.draw(g2, map, atShp, selected, mt, offset);
        }
    }

    private void drawHalo(Graphics2D g2, Map<String,Object> map,
            boolean selected, Shape shp,Shape atShp, MapTransform mt, 
            AffineTransform fat) throws ParameterException, IOException {
        //If we are dealing with a WKN, and if it is a Circle or a half-circle, 
        //we must be a little more clever...
        if(shp instanceof Arc2D){
            halo.drawCircle(g2, map, selected, (Arc2D)shp, atShp, mt, true, viewBox, fat);
        } else {
            halo.draw(g2, map, selected, atShp, mt, true);
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
    /*private double getMargin(DataSet sds, long fid, MapTransform mt) throws ParameterException, IOException {
    double sWidth = 0.0;
    double haloR = 0.0;
    double offset = 0.0;

    if (stroke != null) {
    sWidth += stroke.getMaxWidth(map, mt);
    }

    if (this.halo != null) {
    haloR = halo.getHaloRadius(map, mt);
    }

    if (this.pOffset != null) {
    offset = Uom.toPixel(pOffset.getValue(map), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
    }

    double max = Math.max(sWidth, haloR);
    return Math.max(max, 2 * offset);
    }*/

    /*
    @Override
    public double getMaxWidth(DataSet sds, long fid, MapTransform mt) throws ParameterException, IOException {
    double delta = 0.0;

    if (viewBox != null && viewBox.usable()) {
    Point2D dim = viewBox.getDimensionInPixel(map, DEFAULT_SIZE, DEFAULT_SIZE, mt.getScaleDenominator(), mt.getDpi());
    delta = Math.max(dim.getX(), dim.getY());
    } else {
    MarkGraphicSource source = getSource(map);
    if (source != null) {
    delta = source.getDefaultMaxWidth(map, delta, delta, markIndex, mimeType);
    } else {
    delta = WellKnownName.CIRCLE.getDefaultMaxWidth(map, delta, delta, markIndex, mimeType);
    }
    }

    delta += this.getMargin(map, mt);

    return delta;
    }*/
    /**
     * Get the online resource that defines this {@code MarkGraphic}.
     * @return The online resource that defines this {@code MarkGraphic}.
     */
    public VariableOnlineResource getOnlineResource() {
        return onlineResource;
    }

    /**
     * Set the online resource that defines this {@code MarkGraphic}.
     * @param onlineResource the online resource that defines this {@code MarkGraphic}.
     */
    public void setOnlineResource(VariableOnlineResource onlineResource) {
        this.onlineResource = onlineResource;
        if (onlineResource != null) {
            wkn = null;
            onlineResource.setParent(this);
        }
    }

    /**
     * Gets the WellKnownName defining this {@code MarkGraphic}.
     * @return the well-known name currently used, as a StringParameter.
     */
    public StringParameter getWkn() {
        return wkn;
    }

    /**
     * Sets the WellKnownName defining this {@code MarkGraphic}.
     * @param wkn The new well-known name to use, as a StringParameter.
     */
    public void setWkn(StringParameter wkn) {
        this.wkn = wkn;
        if (this.wkn != null) {
            this.wkn.setRestrictionTo(WellKnownName.getValues());
            this.onlineResource = null;
            this.wkn.setParent(this);
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
    public List<SymbolizerNode> getChildren() {
        List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
        if (wkn != null) {
            ls .add(wkn);
        }
        if (viewBox != null) {
            ls .add(viewBox);
        }
        if (pOffset != null) {
            ls .add(pOffset);
        }
        if (halo != null) {
            ls .add(halo);
        }
        if (fill != null) {
            ls .add(fill);
        }
        if (stroke != null) {
            ls .add(stroke);
        }
        if (transform != null) {
            ls .add(transform);
        }
        if (markIndex != null) {
            ls .add(markIndex);
        }
        if(onlineResource != null){
            ls.add(onlineResource);
        }
        return ls;
    }
}
