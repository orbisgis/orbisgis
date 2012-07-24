/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.thematic.DotMapFillType;
import net.opengis.se._2_0.thematic.ObjectFactory;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Descriptor for dot maps. Each point represents a given quantity. Points are randomly placed
 * in the polygon that contains them.<br/>
 * A DotMapFill is defined with three things : <br/>
 *   * The quantity represented by a single dot<br/>
 *   * The total quantity to represent<br/>
 *   * The symbol associated to each single dot.
 * @author Alexis Guéganno
 */
public final class DotMapFill extends Fill implements GraphicNode {

    private static final Logger LOGGER = Logger.getLogger(DotMapFill.class);
    private static final I18n I18N = I18nFactory.getI18n(DotMapFill.class);
    
    static final int MAX_ATTEMPT = 100;

    private GraphicCollection mark;
    private RealParameter quantityPerMark;
    private RealParameter totalQuantity;
    private Random rand;

    /**
     * Creates a new DotMapFill, with uninstanciated values.
     */
    public DotMapFill() {
        rand = new Random();
    }

    /**
     * Creates a new DotMapFill using directly the values stored in the Jaxb tree.
     * @param f
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public DotMapFill(JAXBElement<DotMapFillType> f) throws InvalidStyle {
        this();
        DotMapFillType dmf = f.getValue();

        if (dmf.getGraphic() != null) {
            this.setGraphicCollection(new GraphicCollection(dmf.getGraphic(), this));
        }

        if (dmf.getValuePerMark() != null) {
            this.setQuantityPerMark(SeParameterFactory.createRealParameter(dmf.getValuePerMark()));
        }

        if (dmf.getValueToRepresent() != null) {
            this.setTotalQuantity(SeParameterFactory.createRealParameter(dmf.getValueToRepresent()));
        }
    }

    @Override
    public void setGraphicCollection(GraphicCollection mark) {
        if (mark != null) {
            this.mark = mark;
            mark.setParent(this);
        }
    }

    @Override
    public GraphicCollection getGraphicCollection() {
        return mark;
    }

    /**
     * Set the quantity represented by a single dot.
     * @param quantityPerMark 
     */
    public void setQuantityPerMark(RealParameter quantityPerMark) {
        if (quantityPerMark != null) {
            this.quantityPerMark = quantityPerMark;
            this.quantityPerMark.setContext(RealParameterContext.REAL_CONTEXT);
        }
    }

    /**
     * Get the quantity represented by a single dot.
     * @return The quantity represented by a single dot
     */
    public RealParameter getQantityPerMark() {
        return quantityPerMark;
    }

    /**
     * Get the total quantity to be represented for this symbolizer.
     * @param totalQuantity 
     */
    public void setTotalQuantity(RealParameter totalQuantity) {
        if (totalQuantity != null) {
            this.totalQuantity = totalQuantity;
            this.totalQuantity.setContext(RealParameterContext.REAL_CONTEXT);
        }
    }

    /**
     * Set the total quantity to be represented for this symbolizer.
     * @return 
     */
    public RealParameter getTotalQantity() {
        return totalQuantity;
    }

    /**
     * Return null since an hatched fill cannot be converted into a native java fill
     * @param fid
     * @param sds
     * @param selected
     * @param mt
     * @return null
     * @throws ParameterException
     */
    @Override
    public Paint getPaint(Map<String,Value> map,
            boolean selected, MapTransform mt) throws ParameterException {
        return null;
    }

    @Override
    public void draw(Graphics2D g2, Map<String,Value> map, Shape shp, boolean selected, MapTransform mt)
            throws ParameterException, IOException {

        //RenderedImage m = this.mark.getGraphic(map, selected, mt).createRendering(mt.getCurrentRenderContext());


        Double perMark = null;
        if (quantityPerMark != null) {
            perMark = this.quantityPerMark.getValue(map);
        }

        Double total = null;
        if (totalQuantity != null) {
            total = this.totalQuantity.getValue(map);
        }

        if (perMark == null || total == null) {
            throw new ParameterException("Dot Map Fill: missing parameters !!!");
        }

        int nb = (int) Math.round(total / perMark);

        //Area area = new Area(shapes.get(0));
        Area area = new Area(shp);

        // setting the seed to the scale denom will ensure that mark will not move when panning
        rand.setSeed((long) mt.getScaleDenominator());
        for (int i = 0; i < nb; i++) {
            Point2D.Double pos = findMarkPosition(area);
            if (pos != null) {
                mark.draw(g2, map, selected, mt, AffineTransform.getTranslateInstance(pos.x, pos.y));
            } else {
                LOGGER.error(I18N.tr("Could not find position for mark within area"));
            }
        }
    }

    /**
     * Ugly version to find a random point which stand within the area
     * @param area
     * @return
     */
    private Point2D.Double findMarkPosition(Area area) {
        Rectangle2D bounds2D = area.getBounds2D();

        for (int i = 0; i < MAX_ATTEMPT; i++) {
            double x = rand.nextDouble() * bounds2D.getWidth() + bounds2D.getMinX();
            double y = rand.nextDouble() * bounds2D.getHeight() + bounds2D.getMinY();

            if (area.contains(x, y)) {
                return new Point2D.Double(x, y);
            }
        }
        return null;
    }

    @Override
    public HashSet<String> dependsOnFeature() {
        HashSet<String> ret = new HashSet<String>();

        if (mark != null) {
            ret.addAll(mark.dependsOnFeature());
        }
        if (this.quantityPerMark != null) {
            ret.addAll(quantityPerMark.dependsOnFeature());
        }
        if (this.totalQuantity != null) {
            ret.addAll(totalQuantity.dependsOnFeature());
        }

        return ret;
    }

    @Override
    public UsedAnalysis getUsedAnalysis() {
        UsedAnalysis ua = new UsedAnalysis();
        if(mark != null){
            ua.merge(mark.getUsedAnalysis());
        }
        ua.include(totalQuantity);
        ua.include(quantityPerMark);
        return ua;
    }

    @Override
    public DotMapFillType getJAXBType() {
        DotMapFillType f = new DotMapFillType();

        if (mark != null) {
            f.setGraphic(mark.getJAXBElement());
        }

        if (quantityPerMark != null) {
            f.setValuePerMark(quantityPerMark.getJAXBParameterValueType());
        }

        if (totalQuantity != null) {
            f.setValueToRepresent(totalQuantity.getJAXBParameterValueType());
        }

        return f;
    }

    @Override
    public JAXBElement<DotMapFillType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createDotMapFill(this.getJAXBType());
    }
}
