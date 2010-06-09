package org.orbisgis.core.renderer.se.graphic;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.VolatileImage;
import java.awt.image.renderable.RenderContext;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.CompositeGraphicType;
import org.orbisgis.core.renderer.persistance.se.GraphicType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.color.ColorHelper;

/**
 * This class doesn't exists within XSD. Actually, it the CompositeGraphic element which has been move up
 *
 * @author maxence
 */
public final class GraphicCollection implements SymbolizerNode {

    public GraphicCollection() {
        graphics = new ArrayList<Graphic>();
    }

    public GraphicCollection(JAXBElement<? extends GraphicType> g, SymbolizerNode parent) {
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

    public void addGraphic(Graphic graphic) {
        if (graphic != null) {
            graphics.add(graphic);
            graphic.setParent(this);
            graphic.updateGraphic();
        }
    }

    public void delGraphic(Graphic graphic) {
        if (graphics.remove(graphic)) {
        } else {
            // TODO Throw error
        }

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
     * @param ds DataSource of the layer
     * @param fid id of the feature to draw
     * @return a specific image for this feature
     * @throws ParameterException
     * @throws IOException
     */
    public RenderableGraphics getGraphic(DataSource ds, long fid, boolean selected)
            throws ParameterException, IOException {
        /*
         * if the graphics collection doesn't depends on feature and the current
         * feature is not selected, use cached graphic
         */
        if (!selected && ! this.dependsOnFeature() && this.graphicCache != null){
            return graphicCache;
        }

        RenderContext ctc = MapEnv.getCurrentRenderContext();

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
            RenderableGraphics img = g.getRenderableGraphics(ds, fid, selected);
            if (img != null) {
                float mX = img.getMinX();
                float w = img.getWidth();
                float mY = img.getMinY();
                float h = img.getHeight();

                images.add(img);

                if (mX< xmin) {
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
        }

        double width = xmax - xmin;
        double height = ymax - ymin;

        if (width > 0 && height > 0) {


            RenderableGraphics rg = new RenderableGraphics(new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin));

            for (RenderableGraphics g : images) {
                rg.drawRenderedImage(g.createRendering(ctc), new AffineTransform());
            }

            
            if (!selected && ! this.dependsOnFeature()){
                graphicCache = rg;
            }
            
            return rg;
        } else {
            return null;
        }
    }

    public final boolean dependsOnFeature() {
        for (Graphic g : this.graphics){
            if (g.dependsOnFeature()){
                return true;
            }
        }
        return false;
    }

    public double getMaxWidth(DataSource ds, long fid) throws ParameterException, IOException {
        double maxWidth = 0.0;

        Iterator<Graphic> it = graphics.iterator();
        while (it.hasNext()) {
            Graphic g = it.next();
            maxWidth = Math.max(g.getMaxWidth(ds, fid), maxWidth);
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

	public RenderedImage getCache(DataSource ds, long fid, boolean selected)
            throws ParameterException, IOException {

        if (!selected && ! this.dependsOnFeature() && imageCache != null){
			return imageCache;
		}


		RenderableGraphics rGraphic = this.getGraphic(ds, fid, selected);
    	RenderedImage rImage = null;
		if (rGraphic != null){
			rImage = rGraphic.createRendering(MapEnv.getCurrentRenderContext());
		}

		/*
		 * Only cache if the graphic doesn't depends on features attribute
		 * and only if it's not a selected highlighted graphic
		 */
		if (!selected && !this.dependsOnFeature()){
			this.imageCache = rImage;
		}

		return rImage;
	}
    RenderableGraphics graphicCache = null;
    RenderedImage imageCache = null;

    private ArrayList<Graphic> graphics;
    private SymbolizerNode parent;
}

