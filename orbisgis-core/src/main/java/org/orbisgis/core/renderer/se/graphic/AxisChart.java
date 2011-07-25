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
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;

import net.opengis.se._2_0.thematic.AxisChartSubtypeType;
import net.opengis.se._2_0.thematic.AxisChartType;
import net.opengis.se._2_0.thematic.CategoryType;
import net.opengis.se._2_0.thematic.ObjectFactory;
import org.orbisgis.core.renderer.se.FillNode;

import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 *
 * @author maxence
 * @todo Implements drawGraphic
 */
public final class AxisChart extends Graphic implements UomNode, FillNode, StrokeNode, TransformNode {

    private List<CategoryListener> listeners;
    private Uom uom;
    private RealParameter normalizeTo;
    //private boolean isPolarChart;
    private AxisScale axisScale;
    private RealParameter categoryWidth;
    private RealParameter categoryGap;
    private Fill areaFill;
    private Transform transform;
    private Stroke lineStroke;
    private List<Category> categories;
    private AxisChartSubType subtype;
    public static final double DEFAULT_GAP_PX = 5; //px
    public static final double INITIAL_GAP_PX = 5; //px
    public static final double DEFAULT_WIDTH_PX = 15; //px

    //private Categories stakc;
    public static enum AxisChartSubType {

        ORTHO, POLAR, STACKED;
    };

    public AxisChart() {
        subtype = AxisChartSubType.ORTHO;
        categories = new ArrayList<Category>();
        listeners = new ArrayList<CategoryListener>();
        this.setAxisScale(new AxisScale());
    }

    AxisChart(JAXBElement<AxisChartType> chartE) throws InvalidStyle {
        this();
        AxisChartType t = chartE.getValue();

        if (t.getUom() != null) {
            this.setUom(Uom.fromOgcURN(t.getUom()));
        }

        if (t.getTransform() != null) {
            this.setTransform(new Transform(t.getTransform()));
        }

        if (t.getNormalization() != null) {
            this.setNormalizeTo(SeParameterFactory.createRealParameter(t.getNormalization()));
        }

        if (t.getCategoryWidth() != null) {
            this.setCategoryWidth(SeParameterFactory.createRealParameter(t.getCategoryWidth()));
        }

        if (t.getCategoryGap() != null) {
            this.setCategoryGap(SeParameterFactory.createRealParameter(t.getCategoryGap()));
        }

        if (t.getAxisChartSubtype() != null) {
            String type = t.getAxisChartSubtype().value();
            if (type.equalsIgnoreCase("polar")) {
                subtype = AxisChartSubType.POLAR;
            } else if (type.equalsIgnoreCase("stacked")) {
                subtype = AxisChartSubType.STACKED;
            } else {
                subtype = AxisChartSubType.ORTHO;
            }
        }

        if (t.getFill() != null) {
            this.setFill(Fill.createFromJAXBElement(t.getFill()));
        }

        if (t.getStroke() != null) {
            this.setStroke(Stroke.createFromJAXBElement(t.getStroke()));
        }

        if (t.getAxisScale() != null) {
            this.setAxisScale(new AxisScale(t.getAxisScale()));
        }

        for (CategoryType ct : t.getCategory()) {
            addCategory(new Category(ct));
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

    public void registerListerner(CategoryListener lsner) {
        if (lsner != null) {
            listeners.add(lsner);
        }
    }

    public int getNumCategories() {
        return categories.size();
    }

    public void addCategory(Category c) {
        categories.add(c);
        c.setParent(this);
    }

    @Override
    public Fill getFill() {
        return areaFill;
    }

    @Override
    public void setFill(Fill areaFill) {
        this.areaFill = areaFill;
        areaFill.setParent(this);
    }

    public AxisScale getAxisScale() {
        return axisScale;
    }

    public void setAxisScale(AxisScale axisScale) {
        this.axisScale = axisScale;
    }

    public RealParameter getCategoryGap() {
        return categoryGap;
    }

    public void setCategoryGap(RealParameter categoryGap) {
        this.categoryGap = categoryGap;
        if (this.categoryGap != null) {
            this.categoryGap.setContext(RealParameterContext.nonNegativeContext);
        }
    }

    public RealParameter getCategoryWidth() {
        return categoryWidth;
    }

    public void setCategoryWidth(RealParameter categoryWidth) {
        this.categoryWidth = categoryWidth;
        if (categoryWidth != null) {
            categoryWidth.setContext(RealParameterContext.nonNegativeContext);
        }
    }

    @Override
    public Stroke getStroke() {
        return lineStroke;
    }

    @Override
    public void setStroke(Stroke lineStroke) {
        this.lineStroke = lineStroke;
        lineStroke.setParent(this);
    }

    public RealParameter getNormalizeTo() {
        return normalizeTo;
    }

    public void setNormalizeTo(RealParameter normalizeTo) {
        this.normalizeTo = normalizeTo;
        if (normalizeTo != null) {
            normalizeTo.setContext(RealParameterContext.realContext);
        }
    }

    public AxisChartSubType getSubtype() {
        return subtype;
    }

    public void setSubtype(AxisChartSubType subtype) {
        this.subtype = subtype;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Category getCategory(int i) throws ParameterException {
        if (i >= 0 && i < categories.size()) {
            return categories.get(i);
        }
        throw new ParameterException("Category index out of bounds!");
    }

    @Override
    public void updateGraphic() {
    }

    private double[] getMeasuresInPixel(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException {
        double rLength = Uom.toPixel(axisScale.getAxisLength().getValue(sds, fid),
                getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        double rMesure = axisScale.getMeasureValue().getValue(sds, fid);

        double[] heights = new double[categories.size()];

        int i = 0;
        for (Category c : categories) {
            heights[i] = c.getMeasure().getValue(sds, fid) * rLength / rMesure;
            i++;
        }

        return heights;
    }

    public void moveCategoryDown(int i) {
        if (i >= 0 && i < categories.size() - 1) {
            Category tmp = categories.get(i);
            categories.set(i, categories.get(i + 1));
            categories.set(i + 1, tmp);
            fireCatDown(i);
        }
    }

    public void moveCategoryUp(int i) {
        if (i > 0 && i < categories.size()) {
            Category tmp = categories.get(i);
            categories.set(i, categories.get(i - 1));
            categories.set(i - 1, tmp);
            fireCatUp(i);
        }
    }

    public void removeCategory(int i) {
        if (i >= 0 && i < categories.size()) {
            categories.remove(i);
            fireCatRm(i);
        }
    }

    private void fireCatDown(int i) {
        for (CategoryListener l : listeners) {
            l.categoryMoveDown(i);
        }
    }

    private void fireCatUp(int i) {
        for (CategoryListener l : listeners) {
            l.categoryMoveUp(i);
        }
    }

    private void fireCatRm(int i) {
        for (CategoryListener l : listeners) {
            l.categoryRemoved(i);
        }
    }

    /**
     * Draw method for polar chart
     * @param g2
     * @param sds
     * @param fid
     * @param selected
     * @param mt
     * @param at
     * @throws ParameterException
     * @throws IOException
     */
    private void drawOrthoChart(Graphics2D g2, SpatialDataSourceDecorator sds, long fid,
            boolean selected, MapTransform mt, AffineTransform at)
            throws ParameterException, IOException {

        int nCat = categories.size();
        double heights[] = getMeasuresInPixel(sds, fid, mt);

        double maxHeight = 0;
        double minHeight = 0;

        // Determine min and max heights
        for (double h : heights) {
            if (h > maxHeight) {
                maxHeight = h;
            }
            if (h < minHeight) {
                minHeight = h;
            }
        }
        
        double cGap = DEFAULT_GAP_PX;
        if (categoryGap != null) {
            cGap = Uom.toPixel(categoryGap.getValue(sds, fid), getUom(), mt.getDpi(),
                    mt.getScaleDenominator(), null);
        }

        double cWidth = DEFAULT_WIDTH_PX;
        if (categoryWidth != null) {
            cWidth = Uom.toPixel(categoryWidth.getValue(sds, fid), getUom(), mt.getDpi(),
                    mt.getScaleDenominator(), null);
        }

        // compute chart width, according to number of categories
        double width = (nCat - 1) * cGap + nCat * cWidth + INITIAL_GAP_PX;

        // chart bounds
        Rectangle2D bounds = new Rectangle2D.Double(-width / 2, -maxHeight, width, maxHeight + -1 * minHeight);

        //AffineTransform at = null;
        if (transform != null) {
            at.concatenate(transform.getGraphicalAffineTransform(false, sds, fid, mt, minHeight, minHeight));
            Shape shp = at.createTransformedShape(bounds);
            bounds.setRect(shp.getBounds2D());
        }

        double currentX = -width / 2 + INITIAL_GAP_PX;

        double xOffset[] = new double[nCat];

        int i;
        for (i = 0; i < nCat; i++) {
            //Category c = categories.get(i);
            xOffset[i] = currentX;
            currentX += cGap + cWidth;
        }


        // First, draw bar chart
        for (i = 0; i < nCat; i++) {
            Category c = categories.get(i);
            if (c.getFill() != null || c.getStroke() != null) {
                Path2D.Double bar = new Path2D.Double();
                bar.moveTo(xOffset[i], 0);
                bar.lineTo(xOffset[i], -heights[i]);
                bar.lineTo(xOffset[i] + cWidth, -heights[i]);
                bar.lineTo(xOffset[i] + cWidth, 0);
                bar.closePath();
                Shape shp = bar;
                if (at != null) {
                    shp = at.createTransformedShape(bar);
                }
                if (c.getFill() != null) {
                    c.getFill().draw(g2, sds, fid, shp, selected, mt);
                }
                if (c.getStroke() != null) {
                    c.getStroke().draw(g2, sds, fid, shp, selected, mt, 0.0);
                }
            }
        }

        // then draw main area (if required) 
        if (areaFill != null) {
            Path2D area = new Path2D.Double();

            area.moveTo(xOffset[0] + cWidth / 2, 0);
            for (i = 0; i < nCat; i++) {
                area.lineTo(xOffset[i] + cWidth / 2, -heights[i]);
            }
            area.lineTo(xOffset[nCat - 1] + cWidth / 2, 0);
            area.closePath();

            Shape shp = area;
            if (at != null) {
                shp = at.createTransformedShape(area);
            }
            areaFill.draw(g2, sds, fid, shp, selected, mt);
        }

        // then the line chart
        if (lineStroke != null) {
            Path2D line = new Path2D.Double();
            line.moveTo(xOffset[0] + cWidth / 2, -heights[0]);
            for (i = 0; i < nCat; i++) {
                line.lineTo(xOffset[i] + cWidth / 2, -heights[i]);
            }
            //area.lineTo(xOffset[nCat-1]+cWidth/2, 0);
            //area.closePath();
            Shape shp = line;
            if (at != null) {
                shp = at.createTransformedShape(line);
            }
            lineStroke.draw(g2, sds, fid, shp, selected, mt, 0.0);
        }

        // and finally, points
        for (i = 0; i < nCat; i++) {
            Category c = categories.get(i);
            if (c.getGraphicCollection() != null) {
                AffineTransform at2 = AffineTransform.getTranslateInstance(xOffset[i] + cWidth / 2, -heights[i]);
                if (at != null) {
                    at2.concatenate(at);
                }

                c.getGraphicCollection().draw(g2, sds, fid, selected, mt, at2);
            }
        }

        
        /*  Following code try to draw x&y axis, TODO tyke into account AT
                g2.setPaint(Color.black);

        Point2D origin = at.transform(new Point2D.Double(0, 0), null);
        Point2D maxX_y0 = at.transform(new Point2D.Double(0, 0), null);

        g2.drawLine((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getMinX(), (int) bounds.getMaxY());
        g2.drawLine((int) bounds.getMinX(), (int) origin.getY(), (int) bounds.getMaxX(), (int) maxX_y0.getY());

         *
         */

        /*
        MarkGraphic arrow = new MarkGraphic();

        arrow.setSource(WellKnownName.TRIANGLE);
        arrow.setUom(Uom.MM);
        arrow.setFill(new SolidFill(Color.black, 100.0));
        arrow.setViewBox(new ViewBox(new RealLiteral(20)));
        RenderableGraphics rArrow = arrow.getRenderableGraphics(sds, fid, selected, mt);
        g2.drawRenderableImage(rArrow, AffineTransform.getTranslateInstance(0, bounds.getMinY()));
         */
    }

    private void drawStackedChart(Graphics2D g2, SpatialDataSourceDecorator sds, long fid,
            boolean selected, MapTransform mt, AffineTransform at) throws ParameterException, IOException {
    }

    /**
     *
     * Create polar chart
     *
     * @param sds
     * @param fid
     * @param selected
     * @param mt
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    private void drawPolarChart(Graphics2D g2, SpatialDataSourceDecorator sds, long fid,
            boolean selected, MapTransform mt, AffineTransform at) throws ParameterException, IOException {
        int nCat = categories.size();
        double heights[] = getMeasuresInPixel(sds, fid, mt);

        double maxHeight = 0;
        double minHeight = 0;

        /* compute min & max height */
        for (double h : heights) {
            if (h > maxHeight) {
                maxHeight = h;
            }
            if (h < minHeight) {
                minHeight = h;
            }
        }

        // make sure min value > 0
        if (minHeight < 0.0) {
            throw new ParameterException("Negative measures are not allowed for polar charts!");
        }

        //double width = (nCat - 1) * cGap + nCat * cWidth + INITIAL_GAP_PX;
        double radius = maxHeight;

        if (transform != null) {
            at.concatenate(transform.getGraphicalAffineTransform(false, sds, fid, mt, 2 * radius, 2 * radius));
        }


        double alphas[] = new double[nCat];
        double beta = 2 * Math.PI / nCat;

        double alpha = Math.PI / 2.0; // The first is vertical !

        double xpos[] = new double[nCat];
        double ypos[] = new double[nCat];

        int i;
        // Compute effective position for each category
        for (i = 0; i < nCat; i++) {
            ypos[i] = Math.sin(alpha) * heights[i];
            xpos[i] = Math.cos(alpha) * heights[i];
            alphas[i] = alpha;
            alpha += beta;
        }

        // First fill the area, with general fill & stroke  
        // This is a net-chart
        if (this.areaFill != null || this.lineStroke != null) {
            Path2D.Double area = new Path2D.Double();
            area.moveTo(xpos[0], ypos[0]);
            for (i = 1; i < nCat; i++) {
                area.lineTo(xpos[i], ypos[i]);
            }
            area.closePath();
            Shape shp = area;
            if (at != null) {
                shp = at.createTransformedShape(area);
            }

            if (this.areaFill != null) {
                areaFill.draw(g2, sds, fid, shp, selected, mt);
            }
            if (this.lineStroke != null) {
                lineStroke.draw(g2, sds, fid, shp, selected, mt, 0.0);
            }
        }

        for (i = 0; i < nCat; i++) {
            Category cat = this.categories.get(i);
            if (cat.getGraphicCollection() != null) {

                AffineTransform at2 = AffineTransform.getTranslateInstance(xpos[i], ypos[i]);
                if (at != null) {
                    at2.concatenate(at);
                }

                cat.getGraphicCollection().draw(g2, sds, fid, selected, mt, at2);
            }
            Shape shp;

            // Draw specific categorie fill & stroke 
            // this is a polar bar chart
            if (cat.getFill() != null || cat.getStroke() != null) {
                Arc2D.Double slice = new Arc2D.Double(-heights[i], -heights[i],
                        2 * heights[i], 2 * heights[i],
                        (-alphas[i] - beta / 2) / ShapeHelper._0_0175, beta / ShapeHelper._0_0175, Arc2D.PIE);

                shp = slice;
                if (at != null) {
                    shp = at.createTransformedShape(slice);
                }
                if (cat.getFill() != null) {
                    cat.getFill().draw(g2, sds, fid, shp, selected, mt);
                }
                if (cat.getStroke() != null) {
                    cat.getStroke().draw(g2, sds, fid, shp, selected, mt, 0.0);
                }
            }


            // DRAW measure:
            Path2D.Double stick = new Path2D.Double();
            stick.moveTo(0, 0);
            stick.lineTo(xpos[i], ypos[i]);
            shp = stick;
            if (at != null) {
                shp = at.createTransformedShape(stick);
            }
            g2.setStroke(new BasicStroke(1));
            //g2.setColor(Color.black);
            g2.setPaint(Color.GRAY);
            g2.draw(shp);

        }
    }

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid,
            boolean selected, MapTransform mt, AffineTransform fat) throws ParameterException, IOException {

        AffineTransform at = new AffineTransform(fat);

        switch (subtype) {
            case POLAR:
                drawPolarChart(g2, sds, fid, selected, mt, at);
                break;
            case STACKED:
                drawStackedChart(g2, sds, fid, selected, mt, at);
                break;
            case ORTHO:
            default:
                drawOrthoChart(g2, sds, fid, selected, mt, at);
                break;
        }
    }

    private Rectangle2D getPolarBounds(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        double[] measuresInPixel = getMeasuresInPixel(sds, fid, mt);
        double max = 0.0;
        for (double m : measuresInPixel) {
            max = Math.max(max, m);
        }
        Rectangle2D bounds = new Rectangle2D.Double(-max, -max, 2 * max, 2 * max);
        if (transform != null) {
            AffineTransform at = transform.getGraphicalAffineTransform(false, sds, fid, mt, 2 * max, 2 * max);
            return at.createTransformedShape(bounds).getBounds2D();
        } else {
            return bounds;
        }
    }

    private Rectangle2D getStackedBounds(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        double[] measuresInPixel = getMeasuresInPixel(sds, fid, mt);
        double sum = 0.0;
        for (double m : measuresInPixel) {
            if (m < 0) {
                throw new ParameterException("Negative values not allowed with Stacked charts");
            }
            sum += m;
        }
        double width = AxisChart.DEFAULT_WIDTH_PX;

        if (categoryWidth != null) {
            width = Uom.toPixel(categoryWidth.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }
        width += AxisChart.INITIAL_GAP_PX;

        Rectangle2D bounds = new Rectangle2D.Double(0, -sum, width, sum);
        if (transform != null) {
            AffineTransform at = transform.getGraphicalAffineTransform(false, sds, fid, mt, width, sum);
            return at.createTransformedShape(bounds).getBounds2D();
        } else {
            return bounds;
        }
    }

    private Rectangle2D getOrthoBounds(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        double[] measuresInPixel = getMeasuresInPixel(sds, fid, mt);
        double max = 0.0;
        for (double m : measuresInPixel) {
            max += Math.abs(m);
        }
        double width = AxisChart.DEFAULT_WIDTH_PX;

        if (categoryWidth != null) {
            width = Uom.toPixel(categoryWidth.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }
        width *= categories.size();
        width += AxisChart.INITIAL_GAP_PX;

        Rectangle2D bounds = new Rectangle2D.Double(0, -max, width, 2 * max);
        if (transform != null) {
            AffineTransform at = transform.getGraphicalAffineTransform(false, sds, fid, mt, width, 2 * max);
            return at.createTransformedShape(bounds).getBounds2D();
        } else {
            return bounds;
        }
    }

    @Override
    public Rectangle2D getBounds(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {

        switch (subtype) {
            case POLAR:
                return getPolarBounds(sds, fid, mt);
            case STACKED:
                return getStackedBounds(sds, fid, mt);
            case ORTHO:
            default:
                return getOrthoBounds(sds, fid, mt);
        }
    }

    @Override
    public JAXBElement<AxisChartType> getJAXBElement() {

        AxisChartType a = new AxisChartType();

        if (axisScale != null) {
            a.setAxisScale(axisScale.getJAXBType());
        }

        if (categoryGap != null) {
            a.setCategoryGap(categoryGap.getJAXBParameterValueType());
        }

        if (categoryWidth != null) {
            a.setCategoryWidth(categoryWidth.getJAXBParameterValueType());
        }

        if (areaFill != null) {
            a.setFill(areaFill.getJAXBElement());
        }

        if (normalizeTo != null) {
            a.setNormalization(normalizeTo.getJAXBParameterValueType());
        }


        if (lineStroke != null) {
            a.setStroke(lineStroke.getJAXBElement());
        }

        if (transform != null) {
            a.setTransform(transform.getJAXBType());
        }

        if (uom != null) {
            a.setUom(uom.toString());
        }

        switch (subtype) {
            case ORTHO:
                a.setAxisChartSubtype(AxisChartSubtypeType.ORTHO);
                break;
            case POLAR:
                a.setAxisChartSubtype(AxisChartSubtypeType.POLAR);
                break;
            case STACKED:
                a.setAxisChartSubtype(AxisChartSubtypeType.STACKED);
                break;
        }
        List<CategoryType> category = a.getCategory();
        for (Category c : categories) {
            category.add(c.getJaxbType());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createAxisChart(a);

    }

    @Override
    public String dependsOnFeature() {
        StringBuffer buf = new StringBuffer();

        if (areaFill != null) {
            buf.append(" "); 
            buf.append( this.areaFill.dependsOnFeature());
        }

        if (lineStroke != null) {
            buf.append(" "); 
            buf.append( this.lineStroke.dependsOnFeature());
        }

        if (this.categoryGap != null) {
            buf.append(" "); 
            buf.append( categoryGap.dependsOnFeature());
        }

        if (categoryWidth != null) {
            buf.append(" "); 
            buf.append( categoryWidth.dependsOnFeature());
        }

        if (axisScale != null) {
            if (axisScale.getAxisLength() != null) {
                buf.append(" "); 
                buf.append( axisScale.getAxisLength().dependsOnFeature());
            }
            if (axisScale.getMeasureValue() != null) {
                buf.append(" "); 
                buf.append( axisScale.getMeasureValue().dependsOnFeature());
            }
        }

        for (Category c : categories) {
            if (c.getFill() != null) {
                buf.append(" "); 
                buf.append( c.getFill().dependsOnFeature());
            }
            if (c.getStroke() != null) {
                buf.append(" ");
                buf.append( c.getStroke().dependsOnFeature());
            }
            if (c.getGraphicCollection() != null) {
                buf.append(" ");
                buf.append(c.getGraphicCollection().dependsOnFeature());
            }
            if (c.getMeasure() != null) {
                buf.append(" ");
                buf.append(c.getMeasure().dependsOnFeature());
            }
        }

        String s = buf.toString().trim();
        return s;
    }
}
