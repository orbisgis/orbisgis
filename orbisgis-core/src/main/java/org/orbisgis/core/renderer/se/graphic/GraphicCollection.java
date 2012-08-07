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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.*;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.CompositeGraphicType;
import net.opengis.se._2_0.core.GraphicType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.apache.log4j.Logger;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This class doesn't exists within XSD. Actually, it the CompositeGraphic element which has been move up
 * It is a set of graphic symbols, as defined in SE.
 * @author Maxence Laurent
 */
public final class GraphicCollection implements SymbolizerNode {

    private static final Logger LOGGER = Logger.getLogger(GraphicCollection.class);
    private static final I18n I18N = I18nFactory.getI18n(GraphicCollection.class);
    
    private ArrayList<Graphic> graphics;
    private SymbolizerNode parent;
    
    /**
     * Create a new, orphan and empty GraphicCollection.
     */
    public GraphicCollection() {
        graphics = new ArrayList<Graphic>();
    }

    /**
     * Create a new GraphicCollection, with parent node <code>parent</code>. The collection
     * is filled using graphic elements found in <code>g</code>
     * @param g
     * @param parent
     *          The parent node of this GraphicCollection
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
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

    /**
     * Get the number of inner graphic symbols.
     * @return 
     */
    public int getNumGraphics() {
        return graphics.size();
    }

    /**
     * Get the ith Graphic element of this Collection.
     * @param i
     * @return 
     *  The Graphic at index i.
     * @throws IndexOutOfBoundsException if <code>i &gt;= getNumGraphics() &amp;&amp; i &lt; 0 </code>
     */
    public Graphic getGraphic(int i) {
        return graphics.get(i);
    }

    /**
     * Add a graphic in this collection, at index i if<code>i &lt;= getNumGraphics()-1 &amp;&amp; i &gt;= 0 </code>,
     * or in the end of the collection (ie at index n+1, if the collection contains
     * n elements before the insertion) if this condition is no satisfied.
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

    /**
     * Add a graphic at the end of the collection
     * @param graphic 
     */
    public void addGraphic(Graphic graphic) {
        if (graphic != null) {
            graphics.add(graphic);
            graphic.setParent(this);
            //graphic.updateGraphic();
        }
    }

    /**
     * Move the graphic at index i (if any) down in the collection, ie at position
     * i+1. If <code> i >= n-1 </code>, where n is the size of the collection, or 
     * if <code> 0 > i </code>, nothing is done.
     * 
     * @param index
     * @return 
     */
    public boolean moveGraphicDown(int index) {
        if (index >= 0 && index < graphics.size() - 1) {
            Graphic g = graphics.get(index);
            graphics.set(index, graphics.get(index+1));
            graphics.set(index + 1, g);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Move the graphic at index i (if any) up in the collection, ie at position i-1.
     * If <code>0 >= i</code> or <code>i > n-1 </code>, where n is the size of the collection,
     * nothing is done.
     * @param index
     * @return 
     */
    public boolean moveGraphicUp(int index) {
        if (index > 0 && index < graphics.size()) {
            Graphic g = graphics.get(index);
            graphics.set(index, graphics.get(index-1));
            graphics.set(index - 1, g);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Delete graphic at index i in the collection.
     * @param i
     * @return 
     * true if a Graphic has been removed, false otherwise.
     */
    public boolean delGraphic(int i) {
        try {
            graphics.remove(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Delete the <code>Graphic</code> instance <code>graphic</code> of this collection.
     * @param graphic
     * @return 
     */
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
     * Get the minimum horizontal rectangle that contains this GraphicCollection.
     * @param map
     * @param selected
     * @param mt
     * @return
     * @throws ParameterException
     * @throws IOException 
     */
    public Rectangle2D getBounds(Map<String,Value> map, boolean selected, MapTransform mt)
            throws ParameterException, IOException {

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
                Rectangle2D bounds = g.getBounds(map, mt);
                if (bounds != null) {
                    double mX = bounds.getMinX();
                    double w = bounds.getWidth();
                    double mY = bounds.getMinY();
                    double h = bounds.getHeight();

                    if (mX < xmin) {
                        xmin = mX;
                    }
                    if (mY < ymin) {
                        ymin = mY;
                    }
                    if (mX + w > xmax) {
                        xmax = mX + w;
                    }
                    if (mY + h > ymax) {
                        ymax = mY + h;
                    }
                }
            } catch (ParameterException ex) {
                LOGGER.error(I18N.tr("Error during graphic composition"),ex);
            }
        }

        double width = xmax - xmin;
        double height = ymax - ymin;

        if (width > 0 && height > 0) {
            return new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin);
        } else {
            return null;
        }
    }

    /**
     *
     * @param g2
     * @param sds
     * @param fid
     * @param selected
     * @param mt
     * @throws ParameterException
     * @throws IOException
     */
    public void draw(Graphics2D g2, Map<String,Value> map, boolean selected, MapTransform mt, AffineTransform at)
            throws ParameterException, IOException {
        for (Graphic g : graphics) {
            try {
                g.draw(g2, map, selected, mt, at);
            } catch (ParameterException ex) {
                LOGGER.error(I18N.tr("Could not render graphic"),ex);
            }
        }
    }

    @Override
    public HashSet<String> dependsOnFeature() {
        HashSet<String> result = new HashSet<String>();
        for (Graphic g : this.graphics) {
            result.addAll(g.dependsOnFeature());
        }
        return result;
    }

    @Override
    public UsedAnalysis getUsedAnalysis() {
        UsedAnalysis result = new UsedAnalysis();
        for (Graphic g : this.graphics) {
            result.merge(g.getUsedAnalysis());
        }
        return result;
    }

    /**
     * Get a JAXB representation of this {@code GraphicCollection}, that will be
     * useable for XML serialization.
     * @return
     * A {@code JAXBElement} that contains a {@code GraphicType} instance.
     */
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

}
