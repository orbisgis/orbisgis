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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import java.io.IOException;

import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.persistance.se.DensityFillType;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import java.awt.geom.AffineTransform;
import javax.media.jai.RenderableGraphics;

import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.stroke.PenStroke;

public final class DensityFill extends Fill implements GraphicNode {

    public DensityFill() {
        this.setHatches(new PenStroke());
        this.setHatchesOrientation(new RealLiteral(45));
        this.setPercentageCovered(new RealLiteral(20.0));
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
        this.isHatched = true;
        hatches.setParent(this);
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
        this.isHatched = false;
        mark.setParent(this);
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
        Paint painter = getPaint(fid, sds, selected, mt);

        if (painter != null) {
            g2.setPaint(painter);
            g2.fill(shp);
        }
    }

    @Override
    public Paint getPaint(long fid, SpatialDataSourceDecorator sds, boolean selected, MapTransform mt) throws ParameterException {
        double percentage = 0.0;

        if (percentageCovered != null) {
            percentage = percentageCovered.getValue(sds, fid);
        }

        if (percentage > 100) {
            percentage = 100;
        }

        if (percentage > 0.0) {// nothing to draw (TODO compare with an epsilon !!)
            Paint painter = null;

            if (isHatched && hatches != null) {
                double theta = -45.0;

                if (this.orientation != null) {
                    // SE ask for clockwise angle, Math.cos()/sin() want counterclockwise
                    theta = this.orientation.getValue(sds, fid);
                }

                theta *= Math.PI / 180.0;

                // Stroke width
                double sWidth = hatches.getMaxWidth(sds, fid, mt);

                // Perpendiculat dist bw two hatches
                double pDist = 100 * sWidth / percentage;


                double cosTheta = Math.cos(theta);
                double sinTheta = Math.sin(theta);

                double dx;
                double dy;

                int ix;
                int iy;

                ////
                // Compute tile size

                if (Math.abs(sinTheta) < 0.001) {
                    // Vertical
                    dx = 0;
                    ix = (int) pDist;
                } else {
                    dx = pDist / sinTheta;
                    ix = (int) dx;
                }

                if (Math.abs(cosTheta) < 0.001) {
                    // Horizontal
                    dy = 0;
                    iy = (int) pDist;
                } else {
                    dy = pDist / cosTheta;
                    iy = (int) dy;
                }

                // Hatch delta x & y
                int idx = (int) dx;
                int idy = (int) dy;

                // Tile size is always absolute
                ix = Math.abs(ix);
                iy = Math.abs(iy);


                BufferedImage img = new BufferedImage(ix, iy, BufferedImage.TYPE_INT_ARGB);
                Graphics2D tile = img.createGraphics();

                tile.setRenderingHints(mt.getCurrentRenderContext().getRenderingHints());

                if (hatches.getFill() != null) {
                    tile.setPaint(hatches.getFill().getPaint(fid, sds, selected, mt));
                } else {
                    tile.setColor(Color.black);
                }

                tile.setStroke(hatches.getBasicStroke(sds, fid, mt, null));

                int ipDist = (int) pDist;

                if (idx == 0) { // V-Hatches
                    tile.drawLine(0, -idy, 0, idy);
                    tile.drawLine(ipDist, -idy, ipDist, idy);
                } else if (idy == 0) { // H-Hatches
                    tile.drawLine(-idx, 0, idx, 0);
                    tile.drawLine(-idx, ipDist, idx, ipDist);
                } else {
                    tile.drawLine(-2 * idx, -2 * idy, 2 * idx, 2 * idy);
                    tile.drawLine(-idx, -2 * idy, 2 * idx, idy);
                    tile.drawLine(0, -2 * idy, 2 * idx, 0);
                    tile.drawLine(-2 * idx, -idy, idx, 2 * idy);
                    tile.drawLine(-2 * idx, 0, 0, 2 * idy);
                }

                painter = new TexturePaint(img, new Rectangle2D.Double(0, 0, ix, iy));
            } else if (mark != null) { // Marked

                RenderableGraphics g;
                try {
                    g = mark.getGraphic(sds, fid, selected, mt);
                } catch (IOException ex) {
                    throw new ParameterException(ex);
                }

                if (g != null) {
                    // Mark size:
                    double mWidth = g.getWidth();
                    double mHeight = g.getHeight();

                    //Final Texture square size
                    //double TextureSize = getTextureSize(mWidth, mHeight, percentage);
                    double tHeight = getTextureSize(mHeight, percentage);
                    double tWidth = getTextureSize(mWidth, percentage);

                    System.out.println("DensityFill: " + percentage + " / TextureSize:" + tWidth + "x" + tHeight);
                    RenderedImage rg = g.createRendering(mt.getCurrentRenderContext());

                    //Create image to which to paint the marks
                    BufferedImage i = new BufferedImage((int) tWidth, (int) tHeight, BufferedImage.TYPE_INT_ARGB);
                    //Create graphics from the image
                    Graphics2D tg = i.createGraphics();
                    //Draw the mark to the image
                    //Draw centered full mark if percentage is smaller or equal to 50

                    tg.drawRenderedImage(rg, AffineTransform.getTranslateInstance(tWidth / 2, tHeight / 2));
                    if (percentage <= 50) {
                        //Draw mark quarters
                        //Top left corner quarter mark
                        tg.drawRenderedImage(rg, AffineTransform.getTranslateInstance(0, 0));
                        //Top right corner quarter mark
                        tg.drawRenderedImage(rg, AffineTransform.getTranslateInstance(tWidth, 0));
                        //Bottom right corner quarter mark
                        tg.drawRenderedImage(rg, AffineTransform.getTranslateInstance(tWidth, tHeight));
                        //Bottom left corner quarter mark
                        tg.drawRenderedImage(rg, AffineTransform.getTranslateInstance(0, tHeight));
                    }
                    //finally set the painter
                    painter = new TexturePaint(i, new Rectangle2D.Double(0, 0, i.getWidth(), i.getHeight()));
                }
            } else {
                throw new ParameterException("Neither marks or hatches are defined");
            }
            return painter;
        }
        return null;
    }

    private double getTextureSize(double markSize, double percentage) {
        double size = (markSize * 100) / percentage;

        if (percentage > 50){
           size -= (size - markSize)/2.0;
        }
        return size + 0.5;
    }

    
    //private double getTextureSize(double markWidth, double markHeight, double percentage) {
        /* Square size depends on the percentage. Lower or equal to 50, the mark
         * will be drawn 2 times in total (1 full, 4/4 quarters).
         * Higher then 50, the mark will be drawn 1 time ( 4/4 quarters).
         */
        //double TextureSurface = (markWidth * markHeight * 100) / percentage;
        //if (percentage <= 50) {
            //TextureSurface = (markWidth * markHeight * 2 * 100) / percentage;
        //}
        //return Math.round(Math.sqrt(TextureSurface));
    //}


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
    private boolean isHatched;
    private PenStroke hatches;
    private RealParameter orientation;
    private GraphicCollection mark;
    private RealParameter percentageCovered;
}
