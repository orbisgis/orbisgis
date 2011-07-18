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
package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.io.IOException;

import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import net.opengis.se._2_0.thematic.DensityFillType;

import net.opengis.se._2_0.thematic.ObjectFactory;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import java.awt.geom.AffineTransform;

import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.stroke.PenStroke;

public final class DensityFill extends Fill implements GraphicNode {

    private boolean isHatched;
    private PenStroke hatches;
    private RealParameter orientation;
    private GraphicCollection mark;
    private RealParameter percentageCovered;


    public DensityFill() {
        this.setHatches(new PenStroke());
        this.setHatchesOrientation(new RealLiteral(45));
        this.setPercentageCovered(new RealLiteral(0.2));
    }

    DensityFill(JAXBElement<DensityFillType> f) throws InvalidStyle {

        DensityFillType t = f.getValue();

        if (t.getPenStroke() != null) {
            this.setHatches(new PenStroke(t.getPenStroke()));

            if (t.getOrientation() != null) {
                this.setHatchesOrientation(SeParameterFactory.createRealParameter(t.getOrientation()));
            }
        } else if (t.getGraphic() != null) {
            this.setGraphicCollection(new GraphicCollection(t.getGraphic(), this));
        }

        if (t.getPercentage() != null) {
            this.setPercentageCovered(SeParameterFactory.createRealParameter(t.getPercentage()));
        }
    }

    public void setHatches(PenStroke hatches) {
        this.hatches = hatches;
        if (hatches != null) {
            this.isHatched = true;
            this.setGraphicCollection(null);
            hatches.setParent(this);
        }
    }

    public PenStroke getHatches() {
        return hatches;
    }

    /**
     *
     * @param orientation angle in degree
     */
    public void setHatchesOrientation(RealParameter orientation) {
        this.orientation = orientation;
        if (this.orientation != null) {
            this.orientation.setContext(RealParameterContext.realContext);
        }
    }

    public RealParameter getHatchesOrientation() {
        return orientation;
    }

    @Override
    public void setGraphicCollection(GraphicCollection mark) {
        this.mark = mark;
        if (mark != null) {
            this.isHatched = false;
            mark.setParent(this);
            setHatches(null);
        }
    }

    @Override
    public GraphicCollection getGraphicCollection() {
        return mark;
    }

    public void useMarks() {
        isHatched = false;
    }

    public boolean useHatches() {
        return isHatched;
    }

    /**
     *
     * @param percent percentage covered by the marks/hatches [0;100]
     */
    public void setPercentageCovered(RealParameter percent) {
        this.percentageCovered = percent;
        if (this.percentageCovered != null) {
            this.percentageCovered.setContext(RealParameterContext.percentageContext);
        }
    }

    public RealParameter getPercentageCovered() {
        return percentageCovered;
    }

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt) throws ParameterException, IOException {

        if (isHatched) {
            double alpha = 45.0;
            double pDist;

            if (this.orientation != null) {
                alpha = this.orientation.getValue(sds, fid);
            }

            // Stroke width
            double sWidth = hatches.getWidthInPixel(sds, fid, mt);

            double percentage = 0.0;

            if (percentageCovered != null) {
                percentage = percentageCovered.getValue(sds, fid) * 100;
            }

            if (percentage > 100) {
                percentage = 100;
            }


            // Perpendiculat dist bw two hatches
            pDist = 100 * sWidth / percentage;

            HatchedFill.drawHatch(g2, sds, fid, shp, selected, mt, alpha, pDist, hatches, 0.0);
        } else {

            Paint painter = getPaint(fid, sds, selected, mt);

            if (painter != null) {
                g2.setPaint(painter);
                g2.fill(shp);
            }
        }
    }

    @Override
    public Paint getPaint(long fid, SpatialDataSourceDecorator sds, boolean selected, MapTransform mt) throws ParameterException, IOException {
        double percentage = 0.0;

        if (percentageCovered != null) {
            percentage = percentageCovered.getValue(sds, fid) * 100;
        }

        if (percentage > 100) {
            percentage = 100;
        }

        if (percentage > 0.5) {
            Paint painter = null;

            if (isHatched && hatches != null) {
            } else if (mark != null) {
                Rectangle2D bounds = mark.getBounds(sds, fid, selected, mt);

                double ratio = Math.sqrt(100 / percentage);
                double gapX =  bounds.getWidth()*ratio - bounds.getWidth();
                double gapY =  bounds.getHeight()*ratio - bounds.getHeight();

                System.out.println ("New way : ...");
                painter = GraphicFill.getPaint(fid, sds, selected, mt, mark, gapX, gapY, bounds);

                /*// Marked
                //RenderableGraphics g;
                Rectangle2D bounds;
                try {
                    //g = mark.getGraphic(sds, fid, selected, mt);
                    bounds = mark.getBounds(sds, fid, selected, mt);
                } catch (IOException ex) {
                    throw new ParameterException(ex);
                }

                if (bounds != null) {
                    // Mark size:
                    double mWidth = bounds.getWidth();
                    double mHeight = bounds.getHeight();

                    //Final Texture square size
                    //double TextureSize = getTextureSize(mWidth, mHeight, percentage);
                    double tHeight = getTextureSize(mHeight, percentage);
                    double tWidth = getTextureSize(mWidth, percentage);

                    //RenderedImage rg = g.createRendering(mt.getCurrentRenderContext());

                    //Create image to which to paint the marks
                    BufferedImage i = new BufferedImage((int) tWidth, (int) tHeight, BufferedImage.TYPE_INT_ARGB);
                    //Create graphics from the image
                    Graphics2D tg = i.createGraphics();
                    tg.setRenderingHints(mt.getRenderingHints());
                    //Draw the mark to the image
                    //Draw centered full mark if percentage is smaller or equal to 50
                    try {
                        mark.draw(tg, sds, fid, selected, mt, AffineTransform.getTranslateInstance(tWidth / 2, tHeight / 2));

                        //tg.drawRenderedImage(rg, AffineTransform.getTranslateInstance(tWidth / 2, tHeight / 2));
                        if (percentage <= 50) {
                            //Draw mark quarters

                            //Top left corner quarter mark
                            mark.draw(tg, sds, fid, selected, mt, AffineTransform.getTranslateInstance(0, 0));
                            //Top right corner quarter mark
                            mark.draw(tg, sds, fid, selected, mt, AffineTransform.getTranslateInstance(tWidth, 0));
                            //Bottom right corner quarter mark
                            mark.draw(tg, sds, fid, selected, mt, AffineTransform.getTranslateInstance(tWidth, tHeight));
                            //Bottom left corner quarter mark
                            mark.draw(tg, sds, fid, selected, mt, AffineTransform.getTranslateInstance(0, tHeight));
                        }
                    } catch (IOException ex) {
                        throw new ParameterException("Couldn't generate tile !", ex);
                    }
                    //finally set the painter
                    painter = new TexturePaint(i, new Rectangle2D.Double(0, 0, i.getWidth(), i.getHeight()));
                }
             */

            } else {
                throw new ParameterException("Neither marks or hatches are defined");
            }
            return painter;
        }
        return null;
    }

    private double getTextureSize(double markSize, double percentage) {
        double size = 100 * (markSize) / percentage;

        if (percentage > 50) {
            size -= (size - markSize) / 2.0;
        }
        return size + 0.5;
    }

    @Override
    public String dependsOnFeature() {

        String pc = "";
        if (percentageCovered != null) {
            pc = percentageCovered.dependsOnFeature();
        }

        if (useHatches()) {
            String h = "";
            String o = "";
            if (hatches != null) {
                h = hatches.dependsOnFeature();
            }
            if (orientation != null) {
                o = orientation.dependsOnFeature();
            }
            return (pc + " " + o + " " + h).trim();
        } else if (mark != null) {
            return (pc + " " + mark.dependsOnFeature()).trim();
        }

        return "";
    }

    @Override
    public DensityFillType getJAXBType() {
        DensityFillType f = new DensityFillType();

        if (isHatched) {
            if (hatches != null) {
                f.setPenStroke(hatches.getJAXBType());
            }
            if (orientation != null) {
                f.setOrientation(orientation.getJAXBParameterValueType());
            }
        } else {
            if (mark != null) {
                f.setGraphic(mark.getJAXBElement());
            }
        }

        if (percentageCovered != null) {
            f.setPercentage(percentageCovered.getJAXBParameterValueType());
        }

        return f;


    }

    @Override
    public JAXBElement<DensityFillType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createDensityFill(this.getJAXBType());
    }
}
