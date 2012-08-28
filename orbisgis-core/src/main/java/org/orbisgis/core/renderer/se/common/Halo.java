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
package org.orbisgis.core.renderer.se.common;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import net.opengis.se._2_0.core.HaloType;
import org.apache.log4j.Logger;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A {@code Halo} is a type of {@code Fill} that is applied to the background of font glyphs.
 * It is mainly used to improve the readability of text labels on the map.
 * @author Alexis Guéganno
 */
public final class Halo implements  UomNode, FillNode {

    private static final Logger LOGGER = Logger.getLogger(Halo.class);
    private static final I18n I18N = I18nFactory.getI18n(Halo.class);
        /**
         * The default radius for new {@code Halo} instances. Set to 5.0, and UOM dependant.
         */
    public static final double DEFAULT_RADIUS = 5.0;

    private Uom uom;
    private RealParameter radius;
    private Fill fill;
    private SymbolizerNode parent;
    
    /**
     * Build a new default {@code Halo}, with a solid fill and a radius set to {@code DEFAULT_RADIUS}
     */
    public Halo() {
        setFill(new SolidFill());
        setRadius(new RealLiteral(DEFAULT_RADIUS));
    }

    /**
     * Build a new {@code Halo} with the given {@code Fill} and a radius set to {@code radius}
     * @param fill
     * @param radius 
     */
    public Halo(Fill fill, RealParameter radius) {
        this.fill = fill;
        this.radius = radius;
    }

    /**
     * Build a new {@code Halo} from the given JAXB type element.
     * @param halo
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public Halo(HaloType halo) throws InvalidStyle {
        if (halo.getFill() != null) {
            this.setFill(Fill.createFromJAXBElement(halo.getFill()));
        }

        if (halo.getRadius() != null) {
            this.setRadius(SeParameterFactory.createRealParameter(halo.getRadius()));
        }

        if (halo.getUom() != null) {
            this.setUom(Uom.fromOgcURN(halo.getUom()));
        }
    }

    @Override
    public Uom getUom() {
        if (uom == null) {
            return ((UomNode)getParent()).getUom();
        } else {
            return uom;
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
    public void setFill(Fill fill) {
        this.fill = fill;
        fill.setParent(this);
    }

    @Override
    public Fill getFill() {
        return fill;
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
    }

    @Override
    public void update() {
            parent.update();
    }

    /**
     * Get the radius of this {@code Halo}.
     * @return 
     * The radius of this {@code Halo} as a {@code RealParameter}.
     */
    public RealParameter getRadius() {
        return radius;
    }

    /**
     * Set the radius of this {@code Halo}.
     * @param radius 
     */
    public void setRadius(RealParameter radius) {
        this.radius = radius;
        if (this.radius != null) {
            this.radius.setContext(RealParameterContext.REAL_CONTEXT);
            this.radius.setParent(this);
        }
    }

    /**
     * Return the halo radius in pixel
     * @param sds
     * @param fid
     * @param mt
     * @return
     * @throws ParameterException
     */
    public double getHaloRadius(Map<String,Value> map, MapTransform mt) throws ParameterException {
        return Uom.toPixel(radius.getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null); // TODO 100%
    }

    /**
     * Draw this {@code Halo} in {@code g2}. Basically compute an offseted shape
     * and fill the difference with the original one.
     * @param g2
     * @param sds
     * @param fid
     * @param selected
     * @param shp
     * @param mt
     * @param substract
     * @throws ParameterException
     * @throws IOException
     */
    public void draw(Graphics2D g2, Map<String,Value> map, boolean selected,
            Shape shp, MapTransform mt, boolean substract) throws ParameterException, IOException {
        if (radius != null && fill != null) {
            double r = this.getHaloRadius(map, mt);
            if (r > 0.0) {
                for (Shape halo : ShapeHelper.perpendicularOffset(shp, r)) {
                    fillHalo(halo, shp, g2, map, selected, mt, substract);
                }
            }
        }
    }
    
    /**
     * In order to improve performance when drawing a halo on a Circle, we use a
     * dedicated method, where we won't compute all the offseted point of the
     * original shape. It's faster to compute directly a new Shape.</p>
     * <p>To achieve this goal, we use the original shape, that is supposed to be
     * an {@code Arc2D}. We compute the new {@code Arc2D} by adding the wanted
     * radius, and finally apply the current AffineTransform obtained from the
     * MapTransform.
     * @param g2
     * The {@code Graphics} where we are going to draw.
     * @param sds
     * Our DataSet
     * @param fid
     * The index of the current feature in sds.
     * @param selected
     * @param shp
     * The original Shape
     * @param atShp
     * The shape obtained by applying mt to shp
     * @param mt
     * The current {@code MapTransform}.
     * @param substract
     * @param viewBox
     * @param at
     * @throws ParameterException
     * @throws IOException 
     */
    public void drawCircle(Graphics2D g2, Map<String,Value> map, boolean selected,
            Arc2D shp, Shape atShp, MapTransform mt, boolean substract, 
            ViewBox viewBox, AffineTransform at) throws ParameterException, IOException {
        //We want to make a halo around a WKN.CIRCLE instance. 
        if (radius != null && fill != null) {
            double r = this.getHaloRadius(map, mt);
            double x = shp.getX() - r/2;
            double y = shp.getY() - r/2;
            double height = shp.getHeight() + r;
            double width = shp.getWidth() + r;
            Shape origin = new Arc2D.Double(x, y, width, height, shp.getAngleStart(), shp.getAngleExtent(), shp.getArcType());
            Shape halo = at.createTransformedShape(origin);
            fillHalo(halo, atShp, g2, map, selected, mt, substract);
            
        }
    }
    
    private void fillHalo(Shape halo, Shape initialShp, Graphics2D g2, 
                Map<String,Value> map, boolean selected,MapTransform mt, boolean substract)
                throws ParameterException, IOException {
        if (halo != null && initialShp != null) {
            Area initialArea = new Area(initialShp);
            Area aHalo = new Area(halo);
            if (substract){
                aHalo.subtract(initialArea);
            }
            fill.draw(g2, map, aHalo, selected, mt);
        } else {
            LOGGER.error(
                    I18N.tr("Perpendicular offset failed"));
        }
    }

    @Override
    public HashSet<String> dependsOnFeature() {
        HashSet<String> ret = new HashSet<String>();
        ret.addAll(radius.dependsOnFeature());
        ret.addAll(fill.dependsOnFeature());
        return ret;
    }

    @Override
    public UsedAnalysis getUsedAnalysis() {
            UsedAnalysis ua = new UsedAnalysis();
            ua.merge(radius.getUsedAnalysis());
            ua.merge(fill.getUsedAnalysis());
            return ua;
    }

    /**
     * Get a JAXB rperesentation of this object.
     * @return 
     */
    public HaloType getJAXBType() {
        HaloType h = new HaloType();

        if (fill != null) {
            h.setFill(fill.getJAXBElement());
        }

        if (radius != null) {
            h.setRadius(radius.getJAXBParameterValueType());
        }

        if (uom != null) {
            h.setUom(uom.toURN());
        }

        return h;
    }
}
