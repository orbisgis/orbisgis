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
package org.orbisgis.coremap.renderer.se.fill;

import net.opengis.se._2_0.core.GraphicFillType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.TileGapType;

import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.coremap.renderer.se.graphic.GraphicCollection;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameterContext;

import javax.xml.bind.JAXBElement;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A "GraphicFill" defines repeated-graphic filling (stippling) pattern for an area geometry.
 * It is defined with a GraphicCollection (that will be used to draw the filling symbol), a Uom,
 * and a gap vector. The gap vector is represented by its two (X and Y) coordinates,
 * stored as <code>RealParameter</code> instances.
 * @author Alexis Guéganno, Maxence Laurent
 */
public final class GraphicFill extends Fill {

    private GraphicCollection graphic;
    /**
     * Distance between two graphics in the fill, in X direction.
     */
    private RealParameter gapX;
    /**
     * Distance between two graphics in the fill, in Y direction.
     */
    private RealParameter gapY;

    /**
     * Creates a new GraphicFill, with the gap's measures set to null.
     */
    public GraphicFill() {
        this.setGapX(null);
        this.setGapY(null);
    }

    /**
     * Creates a new GraphicFill directly from the Jaxb representation of the style.
     * @param gft
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public GraphicFill(GraphicFillType gft) throws InvalidStyle {
        if (gft.getGraphic() != null) {
            this.setGraphic(new GraphicCollection(gft.getGraphic(), this));
        }
        TileGapType gap = gft.getTileGap();
        if (gap != null) {
            if (gap.getX() != null) {
                this.setGapX(SeParameterFactory.createRealParameter(gap.getX()));
            }
            if (gap.getY() != null) {
                this.setGapY(SeParameterFactory.createRealParameter(gap.getY()));
            }
        }

        if (gft.getUom() != null) {
            this.setUom(Uom.fromOgcURN(gft.getUom()));
        }
    }

    /**
     * Creates a new GraphicFill directly from the Jaxb representation of the style.
     * @param f
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    GraphicFill(JAXBElement<GraphicFillType> f) throws InvalidStyle {
        this(f.getValue());
    }

    /**
     * Set the GraphicCollection embedded in this GraphicFill. This is set as the parent of <code>graphic</code>
     * @return 
     */
    public void setGraphic(GraphicCollection graphic) {
        this.graphic = graphic;
        graphic.setParent(this);
    }

    /**
     * Get the GraphicCollection embedded in this GraphicFill.
     * @return 
     */
    public GraphicCollection getGraphic() {
        return graphic;
    }

    /**
     * Set the gap, upon X direction, between two symbols.
     * @param gap 
     */
    public void setGapX(RealParameter gap) {
        gapX = gap;
        if (gap != null) {
            gap.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
            gap.setParent(this);
        }
    }

    /**
     * Set the gap, upon Y direction, between two symbols.
     * @param gap 
     */
    public void setGapY(RealParameter gap) {
        gapY = gap;
        if (gap != null) {
            gap.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
            gap.setParent(this);
        }
    }


    /**
     * Get the gap, upon X direction, between two symbols.
     * @param gap 
     */
    public RealParameter getGapX() {
        return gapX;
    }

    /**
     * Get the gap, upon Y direction, between two symbols.
     * @param gap 
     */
    public RealParameter getGapY() {
        return gapY;
    }

    /**
     * see Fill
     */
    @Override
    public void draw(Graphics2D g2, Map<String,Object> map, Shape shp, boolean selected, MapTransform mt) throws ParameterException, IOException {
        Paint stipple = this.getPaint(map, selected, mt);

        // TODO handle selected ! 
        if (stipple != null) {
            g2.setPaint(stipple);
            g2.fill(shp);
        }
    }

    /**
     * Create a new TexturePaint according to this GraphicFill
     * 
     * @param ds DataSet
     * @param fid feature id
     * @return a TexturePain ready to be used
     * @throws ParameterException
     * @throws IOException
     */
    @Override
    public Paint getPaint(Map<String,Object> map, boolean selected, MapTransform mt) throws ParameterException, IOException {
        double gX = 0.0;
        double gY = 0.0;

        if (gapX != null) {
            gX = gapX.getValue(map);
            if (gX < 0.0) {
                gX = 0.0;
            }
        }

        if (gapY != null) {
            gY = gapY.getValue(map);
            if (gY < 0.0) {
                gY = 0.0;
            }
        }

        Rectangle2D bounds = graphic.getBounds(map, selected, mt);
        gX = Uom.toPixel(gX, getUom(), mt.getDpi(), mt.getScaleDenominator(), bounds.getWidth());
        gY = Uom.toPixel(gY, getUom(), mt.getDpi(), mt.getScaleDenominator(), bounds.getHeight());

        return getPaint(map, selected, mt, graphic, gX, gY, bounds);
    }

    public static Paint getPaint(Map<String,Object> map, boolean selected,
            MapTransform mt, GraphicCollection graphic, double gX, double gY, Rectangle2D bounds)
            throws ParameterException, IOException {

        if (bounds != null) {

            Point2D.Double geoRef = new Point2D.Double(0, 0);
            Point2D ref = mt.getAffineTransform().transform(geoRef, null);

            int tWidth = (int) (bounds.getWidth() + gX);
            int tHeight = (int) (bounds.getHeight() + gY);

            int deltaX = (int) (ref.getX() - Math.ceil(ref.getX() / tWidth) * tWidth);
            int deltaY = (int) (ref.getY() - Math.ceil(ref.getY() / tHeight) * tHeight);


            BufferedImage i = new BufferedImage(tWidth, tHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tile = i.createGraphics();
            tile.setRenderingHints(mt.getRenderingHints());

            int ix;
            int iy;
            for (ix = 0; ix < 2; ix++) {
                for (iy = 0; iy < 2; iy++) {
                    graphic.draw(tile, map, selected, mt,
                            AffineTransform.getTranslateInstance(
                            -bounds.getMinX() + gX / 2.0 + deltaX + tWidth * ix,
                            -bounds.getMinY() + gY / 2.0 + deltaY + tHeight * iy));
                }
            }

            return new TexturePaint(i, new Rectangle2D.Double(0, 0, i.getWidth(), i.getHeight()));
        } else {
            return null;
        }

    }

    @Override
    public GraphicFillType getJAXBType() {
        GraphicFillType f = new GraphicFillType();

        if (getOwnUom() != null) {
            f.setUom(getOwnUom().toURN());
        }

        if (graphic != null) {
            f.setGraphic(graphic.getJAXBElement());
        }

        if (gapX != null || gapY != null) {
            TileGapType tile = new TileGapType();
            if (gapX != null) {
                tile.setX(gapX.getJAXBParameterValueType());
            }
            if (gapY != null) {
                tile.setY(gapY.getJAXBParameterValueType());
            }
            f.setTileGap(tile);
        }

        return f;
    }

    @Override
    public List<SymbolizerNode> getChildren() {
        List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
        if (graphic != null) {
            ls.add(graphic);
        }
        if (gapX != null) {
            ls.add(gapX);
        }
        if (gapY != null) {
            ls.add(gapY);
        }
        return ls;
    }

    @Override
    public JAXBElement<GraphicFillType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createGraphicFill(this.getJAXBType());
    }
}
