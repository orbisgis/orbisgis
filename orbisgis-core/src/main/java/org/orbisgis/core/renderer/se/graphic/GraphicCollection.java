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

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.persistance.se.CompositeGraphicType;
import org.orbisgis.core.renderer.persistance.se.GraphicType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * This class doesn't exists within XSD. Actually, it the CompositeGraphic element which has been move up
 *
 * @author maxence
 */
public final class GraphicCollection implements SymbolizerNode {

    public GraphicCollection() {
        graphics = new ArrayList<Graphic>();
    }

    public GraphicCollection(JAXBElement<? extends GraphicType> g, SymbolizerNode parent) throws InvalidStyle {
        this();
        this.setParent(parent);
        if (g.getDeclaredType() == CompositeGraphicType.class) {
            CompositeGraphicType cg = (CompositeGraphicType) g.getValue();
            for (JAXBElement<? extends GraphicType> gte : cg.getGraphic()) {

                if (gte.getDeclaredType() == CompositeGraphicType.class) {
                    GraphicCollection collec2 = new GraphicCollection(gte, parent);
                    for (Graphic newG : collec2.graphics) {
                        this.addGraphic(newG);
                    }
                } else {
                    this.addGraphic(Graphic.createFromJAXBElement(gte));
                }
            }
        } else {
            this.addGraphic(Graphic.createFromJAXBElement(g));
        }
    }

    public int getNumGraphics() {
        return graphics.size();
    }

    public Graphic getGraphic(int i) {
        return graphics.get(i);
    }

    /**
     *
     * @param graphic
     * @param index
     */
    public void addGraphic(Graphic graphic, int index) {
        if (graphic != null) {
            if (index >= 0 && index < graphics.size()) {
                graphics.add(index, graphic);
            } else {
                graphics.add(graphic);
            }
            graphic.setParent(this);
        }
    }

    public void addGraphic(Graphic graphic) {
        if (graphic != null) {
            graphics.add(graphic);
            graphic.setParent(this);
            //graphic.updateGraphic();
        }
    }

    public boolean moveGraphicDown(int index) {
        if (index >= 0 && index < graphics.size() - 1) {
            Graphic g = graphics.remove(index);
            graphics.add(index + 1, g);
            return true;
        } else {
            return false;
        }
    }

    public boolean moveGraphicUp(int index) {
        if (index > 0 && index < graphics.size()) {
            Graphic g = graphics.remove(index);
            graphics.add(index - 1, g);
            return true;
        } else {
            return false;
        }
    }

    public boolean delGraphic(int i) {
        try {
            graphics.remove(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delGraphic(Graphic graphic) {
        return (graphics.remove(graphic));
    }

    @Override
    public Uom getUom() {
        return parent.getUom();
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
    }

    /**
     * Convert a graphic collection to a drawable Graphics2D.
     * If this collection doesn't depends on features, this g2 
     * can be used for all features.
     *
     * Callers to this method are (will be...) :
     *    - GraphicStroke
     *    - GraphicFill
     *    - DensityFill
     *    - DotMapFill
     *    - AxisChart
     *    - PointSymbolizer
     *
     *
     * @param feat The feature the graphic is for
     * @param selected is the feature selected ?
     * @param mt the current map transform
     * @return a specific image for this feature
     * @throws ParameterException
     * @throws IOException
     */
    public RenderableGraphics getGraphic(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt)
            throws ParameterException, IOException {
        /*
         * if the graphics collection doesn't depends on feature and the current
         * feature is not selected, use cached graphic
         * Note that the graphic is regenerated every time the scale change (to
         * support gm ant gft units)
         */
        if (!selected
                && this.dependsOnFeature().isEmpty()
                && this.graphicCache != null
                && mt.getScaleDenominator() == this.scaleCache) {
            return graphicCache;
        }

        ArrayList<RenderableGraphics> images = new ArrayList<RenderableGraphics>();

        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;

        // First, retrieve all graphics composing the collection
        // and fetch the min/max x, y values

        Iterator<Graphic> it = graphics.iterator();
        while (it.hasNext()) {
            Graphic g = it.next();
            try {
                System.out.println (" Go for graphic !");
                RenderableGraphics img = g.getRenderableGraphics(sds, fid, selected, mt);
                if (img != null) {
                    float mX = img.getMinX();
                    float w = img.getWidth();
                    float mY = img.getMinY();
                    float h = img.getHeight();

                    images.add(img);

                    if (mX < xmin) {
                        xmin = mX;
                    }
                    if (mY < ymin) {
                        ymin = mY;
                    }
                    if (mX + w > xmax) {
                        xmax = mX + w;
                    }
                    if (img.getMinY() + img.getHeight() > ymax) {
                        ymax = mY + h;
                    }
                }
            } catch (ParameterException ex) {
                Services.getErrorManager().error("Erroi in graphic composition : " + ex.getMessage());
            }
        }

        double width = xmax - xmin;
        double height = ymax - ymin;

        if (width > 0 && height > 0) {
            RenderableGraphics rg = Graphic.getNewRenderableGraphics(new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin), 0.0, mt);

            for (RenderableGraphics g : images) {
                //rg.drawRenderableImage(g, new AffineTransform());
                rg.drawRenderedImage(g.createRendering(mt.getCurrentRenderContext()), new AffineTransform());
            }

            if (!selected && this.dependsOnFeature().isEmpty()) {
                scaleCache = mt.getScaleDenominator();
                graphicCache = rg;
            }
            return rg;
        } else {
            return null;
        }
    }

    public final String dependsOnFeature() {
        String result = "";
        for (Graphic g : this.graphics) {
            result += " " + g.dependsOnFeature();
        }
        return result.trim();
    }

    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        double maxWidth = 0.0;

        Iterator<Graphic> it = graphics.iterator();
        while (it.hasNext()) {
            Graphic g = it.next();
            maxWidth = Math.max(g.getMaxWidth(sds, fid, mt), maxWidth);
        }
        return maxWidth;
    }

    public JAXBElement<? extends GraphicType> getJAXBElement() {
        if (graphics.size() > 0) {
            if (graphics.size() == 1) {
                return graphics.get(0).getJAXBElement();
            } else {
                CompositeGraphicType gc = new CompositeGraphicType();
                List<JAXBElement<? extends GraphicType>> elems = gc.getGraphic();

                for (Graphic g : graphics) {
                    elems.add(g.getJAXBElement());
                }
                ObjectFactory of = new ObjectFactory();
                return of.createCompositeGraphic(gc);
            }
        } else {
            return null;
        }
    }

    public RenderedImage getCache(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt)
            throws ParameterException, IOException {

        /*if (!selected && ! this.dependsOnFeature() && imageCache != null){
        return imageCache;
        }*/


        RenderableGraphics rGraphic = this.getGraphic(sds, fid, selected, mt);
        RenderedImage rImage = null;
        if (rGraphic != null) {
            rImage = rGraphic.createRendering(mt.getCurrentRenderContext());
        }

        /*
         * Only cache if the graphic doesn't depends on features attribute
         * and only if it's not a selected highlighted graphic
         */
        if (!selected && this.dependsOnFeature().isEmpty()) {
            this.imageCache = rImage;
        }

        return rImage;
    }
    RenderableGraphics graphicCache = null;
    RenderedImage imageCache = null;
    double scaleCache = -1;
    private ArrayList<Graphic> graphics;
    private SymbolizerNode parent;
}
