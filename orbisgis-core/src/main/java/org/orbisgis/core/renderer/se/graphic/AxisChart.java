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
package org.orbisgis.core.renderer.se.graphic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.thematic.AxisChartSubtypeType;
import net.opengis.se._2_0.thematic.AxisChartType;
import net.opengis.se._2_0.thematic.CategoryType;
import net.opengis.se._2_0.thematic.ObjectFactory;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 * {@code AxisChart} references all the supported types of chart that uses axis
 * in their representation (by opposition to {@link PieChart}, for instance,
 * that don't need any). Three types of axis charts are supported, as defined in
 * the {@code AxisChartSubType} enumeration : ortho, polar and stacked.
 * It's defined using :
 * <ul>
 * <li>A unit of measure, as every {@link UomNode}.</li>
 * <li>A (compulsory) {@link AxisScale}.</li>
 * <li>A width, used to render the categories</li>
 * <li>An optional value to specify the gap between two {@link Category}s.</li>
 * <li>A {@link Fill} used to render the background.</li>
 * <li>A {@code Transform}</li>
 * <li>A set of {@link Category} instances.</li>
 * <li>And as said upper, a particular type. It is optional, and defaulted to
 * ortho if not set.</li>
 * </ul>
 * @author Maxence Laurent
 * @todo Implements drawGraphic
 */
public final class AxisChart extends Graphic implements UomNode, FillNode,
        StrokeNode, TransformNode {

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
        /**
         * The default gap between two bars.
         */
        public static final double DEFAULT_GAP_PX = 5; //px
        /**
         * The default gap before the first bar.
         */
        public static final double INITIAL_GAP_PX = 5; //px
        /**
         * The default width of each bar.
         */
        public static final double DEFAULT_WIDTH_PX = 15; //px

        //private Categories stakc;
        /**
         * The three supported types of {@code AxisChart}.
         */
        public static enum AxisChartSubType {

                ORTHO, POLAR, STACKED;
        };

        /**
         * Build a new; default, {@code AxisChart}. It's an ortho one, built with
         * an empty list of categories and with a default {@link
         * AxisScale#AxisScale() AxisScale}.
         */
        public AxisChart() {
                subtype = AxisChartSubType.ORTHO;
                categories = new ArrayList<Category>();
                listeners = new ArrayList<CategoryListener>();
                this.setAxisScale(new AxisScale());
        }

        /**
         * Build a new {@code AxisChart} from a {@code JAXBElement} instance
         * that embeds an {@link AxisChartType}.
         * @param chartE
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
         */
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

        /**
         * Get the number of {@link Category} registered in this {@code
         * AxisChart}.
         * @return
         * The number of registered {@link Category} instances.
         */
        public int getNumCategories() {
                return categories.size();
        }

        /**
         * Add a {@link Category} to this {@code AxisChart}.
         * @param c
         * The {@link Category} that must be added.
         */
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

        /**
         * Get the {@link AxisScale} used in this {@code AxisChart}.
         * @return
         * An {@link AxisScale} instance.
         */
        public AxisScale getAxisScale() {
                return axisScale;
        }

        /**
         * Set the {@link AxisScale} used in this {@code AxisChart}.
         * @param axisScale
         */
        public void setAxisScale(AxisScale axisScale) {
                this.axisScale = axisScale;
        }

        /**
         * Get the gap that must be set between two categories.
         * @return
         * The gap as a {@link RealParameter}. This object is placed in a
         * {@link RealParameterContext#NON_NEGATIVE_CONTEXT non-negative context}.
         */
        public RealParameter getCategoryGap() {
                return categoryGap;
        }

        /**
         * Set the gap that must be set between two categories.
         * @param categoryGap
         * A {@link RealParameter}, that will be placed in a
         * {@link RealParameterContext#NON_NEGATIVE_CONTEXT non-negative context}.
         */
        public void setCategoryGap(RealParameter categoryGap) {
                this.categoryGap = categoryGap;
                if (this.categoryGap != null) {
                        this.categoryGap.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
                }
        }

        /**
         * Get the width of each category.
         * @return
         * The wifth as a {@link RealParameter}. This object is placed in a
         * {@link RealParameterContext#NON_NEGATIVE_CONTEXT non-negative context}.
         */
        public RealParameter getCategoryWidth() {
                return categoryWidth;
        }

        /**
         * Set the width of each category.
         * @param categoryWidth
         * A {@link RealParameter}, that will be placed in a
         * {@link RealParameterContext#NON_NEGATIVE_CONTEXT non-negative context}.
         * Consequently, if given a negative number here, the categories won't
         * be displayed, as their width will be set to 0...
         */
        public void setCategoryWidth(RealParameter categoryWidth) {
                this.categoryWidth = categoryWidth;
                if (categoryWidth != null) {
                        categoryWidth.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
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
                        normalizeTo.setContext(RealParameterContext.REAL_CONTEXT);
                }
        }

        /**
         * Get the subtype that accurately define this {@code AxisChart} type.
         * @return
         */
        public AxisChartSubType getSubtype() {
                return subtype;
        }

        /**
         * Set the subtype that accurately define this {@code AxisChart} type.
         * @param subtype
         */
        public void setSubtype(AxisChartSubType subtype) {
                this.subtype = subtype;
        }

        /**
         * Get the categories that must be represented in this {@code AxisChart}.
         * @return
         * A list of {@link Category} instances. Their oreder is important, as
         * it determines their rendering order.
         */
        public List<Category> getCategories() {
                return categories;
        }

        /**
         * Get the ith category registered in this {@code AxisChart}.
         * @param i
         * @return
         * The ith {@code Category}, if i is a valid index.
         * @throws ParameterException
         */
        public Category getCategory(int i) throws ParameterException {
                if (i >= 0 && i < categories.size()) {
                        return categories.get(i);
                }
                throw new ParameterException("Category index out of bounds!");
        }

        @Override
        public void updateGraphic() {
        }

        private double[] getMeasuresInPixel(Map<String,Value> map, MapTransform mt) throws ParameterException {
                double rLength = Uom.toPixel(axisScale.getAxisLength().getValue(map),
                        getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                double rMesure = axisScale.getMeasureValue().getValue(map);

                double[] heights = new double[categories.size()];

                int i = 0;
                for (Category c : categories) {
                        heights[i] = c.getMeasure().getValue(map) * rLength / rMesure;
                        i++;
                }

                return heights;
        }

        /**
         * Move the ith {@code Category} down, ie swap the ith and (i-1)th elements.
         * @param i
         */
        public void moveCategoryDown(int i) {
                if (i >= 0 && i < categories.size() - 1) {
                        Category tmp = categories.get(i);
                        categories.set(i, categories.get(i + 1));
                        categories.set(i + 1, tmp);
                        fireCatDown(i);
                }
        }

        /**
         * Move the ith {@code Category} up, ie swap the ith and (i+1)th elements.
         * @param i
         */
        public void moveCategoryUp(int i) {
                if (i > 0 && i < categories.size()) {
                        Category tmp = categories.get(i);
                        categories.set(i, categories.get(i - 1));
                        categories.set(i - 1, tmp);
                        fireCatUp(i);
                }
        }

        /**
         * Remove the ith {@code Category}.
         * @param i
         */
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
         * @param map
         * @param selected
         * @param mt
         * @param at
         * @throws ParameterException
         * @throws IOException
         */
        private void drawOrthoChart(Graphics2D g2, Map<String,Value> map,
                boolean selected, MapTransform mt, AffineTransform at)
                throws ParameterException, IOException {

                int nCat = categories.size();
                double heights[] = getMeasuresInPixel(map, mt);

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
                        cGap = Uom.toPixel(categoryGap.getValue(map), getUom(), mt.getDpi(),
                                mt.getScaleDenominator(), null);
                }

                double cWidth = DEFAULT_WIDTH_PX;
                if (categoryWidth != null) {
                        cWidth = Uom.toPixel(categoryWidth.getValue(map), getUom(), mt.getDpi(),
                                mt.getScaleDenominator(), null);
                }

                // compute chart width, according to number of categories
                double width = (nCat - 1) * cGap + nCat * cWidth + INITIAL_GAP_PX;

                // chart bounds
                Rectangle2D bounds = new Rectangle2D.Double(-width / 2, -maxHeight, width, maxHeight + -1 * minHeight);

                //AffineTransform at = null;
                if (transform != null) {
                        at.concatenate(transform.getGraphicalAffineTransform(false, map, mt, minHeight, minHeight));
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
                                        c.getFill().draw(g2, map, shp, selected, mt);
                                }
                                if (c.getStroke() != null) {
                                        c.getStroke().draw(g2, map, shp, selected, mt, 0.0);
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
                        areaFill.draw(g2, map, shp, selected, mt);
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
                        lineStroke.draw(g2, map, shp, selected, mt, 0.0);
                }

                // and finally, points
                for (i = 0; i < nCat; i++) {
                        Category c = categories.get(i);
                        if (c.getGraphicCollection() != null) {
                                AffineTransform at2 = AffineTransform.getTranslateInstance(xOffset[i] + cWidth / 2, -heights[i]);
                                if (at != null) {
                                        at2.concatenate(at);
                                }

                                c.getGraphicCollection().draw(g2, map, selected, mt, at2);
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
                RenderableGraphics rArrow = arrow.getRenderableGraphics(map, selected, mt);
                g2.drawRenderableImage(rArrow, AffineTransform.getTranslateInstance(0, bounds.getMinY()));
                 */
        }

        private void drawStackedChart(Graphics2D g2, Map<String,Value> map,
                boolean selected, MapTransform mt, AffineTransform at) throws ParameterException, IOException {
        }

        /**
         *
         * Create polar chart
         *
         * @param map
         * @param selected
         * @param mt
         * @return
         * @throws ParameterException
         * @throws IOException
         */
        private void drawPolarChart(Graphics2D g2, Map<String,Value> map,
                boolean selected, MapTransform mt, AffineTransform at) throws ParameterException, IOException {
                int nCat = categories.size();
                double heights[] = getMeasuresInPixel(map, mt);

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
                        at.concatenate(transform.getGraphicalAffineTransform(false, map, mt, 2 * radius, 2 * radius));
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
                                areaFill.draw(g2, map, shp, selected, mt);
                        }
                        if (this.lineStroke != null) {
                                lineStroke.draw(g2, map, shp, selected, mt, 0.0);
                        }
                }

                for (i = 0; i < nCat; i++) {
                        Category cat = this.categories.get(i);
                        if (cat.getGraphicCollection() != null) {

                                AffineTransform at2 = AffineTransform.getTranslateInstance(xpos[i], ypos[i]);
                                if (at != null) {
                                        at2.concatenate(at);
                                }

                                cat.getGraphicCollection().draw(g2, map, selected, mt, at2);
                        }
                        Shape shp;

                        // Draw specific categorie fill & stroke
                        // this is a polar bar chart
                        if (cat.getFill() != null || cat.getStroke() != null) {
                                Arc2D.Double slice = new Arc2D.Double(-heights[i], -heights[i],
                                        2 * heights[i], 2 * heights[i],
                                        (-alphas[i] - beta / 2) / ShapeHelper.ONE_DEG_IN_RAD, beta / ShapeHelper.ONE_DEG_IN_RAD, Arc2D.PIE);

                                shp = slice;
                                if (at != null) {
                                        shp = at.createTransformedShape(slice);
                                }
                                if (cat.getFill() != null) {
                                        cat.getFill().draw(g2, map, shp, selected, mt);
                                }
                                if (cat.getStroke() != null) {
                                        cat.getStroke().draw(g2, map, shp, selected, mt, 0.0);
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
        public void draw(Graphics2D g2, Map<String,Value> map,
                boolean selected, MapTransform mt, AffineTransform fat) throws ParameterException, IOException {

                AffineTransform at = new AffineTransform(fat);

                switch (subtype) {
                        case POLAR:
                                drawPolarChart(g2, map, selected, mt, at);
                                break;
                        case STACKED:
                                drawStackedChart(g2, map, selected, mt, at);
                                break;
                        case ORTHO:
                        default:
                                drawOrthoChart(g2, map, selected, mt, at);
                                break;
                }
        }

        private Rectangle2D getPolarBounds(Map<String,Value> map, MapTransform mt) throws ParameterException, IOException {
                double[] measuresInPixel = getMeasuresInPixel(map, mt);
                double max = 0.0;
                for (double m : measuresInPixel) {
                        max = Math.max(max, m);
                }
                Rectangle2D bounds = new Rectangle2D.Double(-max, -max, 2 * max, 2 * max);
                if (transform != null) {
                        AffineTransform at = transform.getGraphicalAffineTransform(false, map, mt, 2 * max, 2 * max);
                        return at.createTransformedShape(bounds).getBounds2D();
                } else {
                        return bounds;
                }
        }

        private Rectangle2D getStackedBounds(Map<String,Value> map, MapTransform mt) throws ParameterException, IOException {
                double[] measuresInPixel = getMeasuresInPixel(map, mt);
                double sum = 0.0;
                for (double m : measuresInPixel) {
                        if (m < 0) {
                                throw new ParameterException("Negative values not allowed with Stacked charts");
                        }
                        sum += m;
                }
                double width = AxisChart.DEFAULT_WIDTH_PX;

                if (categoryWidth != null) {
                        width = Uom.toPixel(categoryWidth.getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                }
                width += AxisChart.INITIAL_GAP_PX;

                Rectangle2D bounds = new Rectangle2D.Double(0, -sum, width, sum);
                if (transform != null) {
                        AffineTransform at = transform.getGraphicalAffineTransform(false, map, mt, width, sum);
                        return at.createTransformedShape(bounds).getBounds2D();
                } else {
                        return bounds;
                }
        }

        private Rectangle2D getOrthoBounds(Map<String,Value> map, MapTransform mt) throws ParameterException, IOException {
                double[] measuresInPixel = getMeasuresInPixel(map, mt);
                double max = 0.0;
                for (double m : measuresInPixel) {
                        max += Math.abs(m);
                }
                double width = AxisChart.DEFAULT_WIDTH_PX;

                if (categoryWidth != null) {
                        width = Uom.toPixel(categoryWidth.getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                }
                width *= categories.size();
                width += AxisChart.INITIAL_GAP_PX;

                Rectangle2D bounds = new Rectangle2D.Double(0, -max, width, 2 * max);
                if (transform != null) {
                        AffineTransform at = transform.getGraphicalAffineTransform(false, map, mt, width, 2 * max);
                        return at.createTransformedShape(bounds).getBounds2D();
                } else {
                        return bounds;
                }
        }

        @Override
        public Rectangle2D getBounds(Map<String,Value> map, MapTransform mt) throws ParameterException, IOException {

                switch (subtype) {
                        case POLAR:
                                return getPolarBounds(map, mt);
                        case STACKED:
                                return getStackedBounds(map, mt);
                        case ORTHO:
                        default:
                                return getOrthoBounds(map, mt);
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
                        category.add(c.getJAXBType());
                }

                ObjectFactory of = new ObjectFactory();
                return of.createAxisChart(a);

        }

        @Override
        public HashSet<String> dependsOnFeature() {
                HashSet<String> ret = new HashSet<String>();
                if (areaFill != null) {
                        ret.addAll(areaFill.dependsOnFeature());
                }
                if (lineStroke != null) {
                        ret.addAll(lineStroke.dependsOnFeature());
                }
                if (this.categoryGap != null) {
                        ret.addAll(categoryGap.dependsOnFeature());
                }
                if (categoryWidth != null) {
                        ret.addAll(categoryWidth.dependsOnFeature());
                }
                if (axisScale != null) {
                        ret.addAll(axisScale.dependsOnFeature());
                }
                for (Category c : categories) {
                        ret.addAll(c.dependsOnFeature());
                }
                return ret;
        }

        @Override
        public UsedAnalysis getUsedAnalysis() {
                UsedAnalysis ret = new UsedAnalysis();
                if (areaFill != null) {
                        ret.merge(areaFill.getUsedAnalysis());
                }
                if (lineStroke != null) {
                        ret.merge(lineStroke.getUsedAnalysis());
                }
                if (this.categoryGap != null) {
                        ret.include(categoryGap);
                }
                if (categoryWidth != null) {
                        ret.include(categoryWidth);
                }
                if (axisScale != null) {
                        ret.merge(axisScale.getUsedAnalysis());
                }
                for (Category c : categories) {
                        ret.merge(c.getUsedAnalysis());
                }
                return ret;
        }
}
