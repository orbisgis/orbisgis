/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.CompoundStrokeType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * A {@code CompoundStroke} allows to combine multiple strokes. This way, it becomes possible 
 * to render lines using complex strokes. It relies on the following parameters :
 * <ul><li>preGap is a {@link RealParameter} that defines how far to advance along the line before 
 * starting to plot content.</li>
 * <li>postGap is a {@link RealParameter} that defines how far from the end of the line to stop plotting</li>
 * <li>A list of {@link CompoundStrokeElement}s. They are used to compute the style of the line.</li>
 * <li>A list of {@link StrokeAnnotationGraphic} used to decorate the line</li></ul>
 * @author Maxence Laurent, Alexis Guéganno
 */
public final class CompoundStroke extends Stroke implements UomNode {

    private RealParameter preGap;
    private RealParameter postGap;
    private List<CompoundStrokeElement> elements;
    //private List<StrokeAnnotationGraphic> annotations;

    /**
     * Build a new {@code CompoundStroke}, with empty parameters. If used, it won't draw 
     * any line of any kind.
     */
    public CompoundStroke() {
        super();
        elements = new ArrayList<CompoundStrokeElement>();
        addElement(new StrokeElement());
        //annotations = new ArrayList<StrokeAnnotationGraphic>();
    }

    /**
     * Build a {@code CompoundStroke} using the JAXB type given in argument.
     * @param s
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public CompoundStroke(CompoundStrokeType s) throws InvalidStyle {
        super(s);

        if (s.getUom() != null) {
            setUom(Uom.fromOgcURN(s.getUom()));
        }

        if (s.getPreGap() != null) {
            setPreGap(SeParameterFactory.createRealParameter(s.getPreGap()));
        }

        if (s.getPostGap() != null) {
            setPostGap(SeParameterFactory.createRealParameter(s.getPostGap()));
        }

        elements = new ArrayList<CompoundStrokeElement>();
        //annotations = new ArrayList<StrokeAnnotationGraphic>();
        

        if (s.getStrokeElementOrAlternativeStrokeElements() != null) {
            for (Object o : s.getStrokeElementOrAlternativeStrokeElements()) {
                CompoundStrokeElement cse = CompoundStrokeElement.createCompoundStrokeElement(o);
                addCompoundStrokeElement(cse);
            }
        }

        /*if (s.getStrokeAnnotationGraphic() != null) {
            for (StrokeAnnotationGraphicType sagt : s.getStrokeAnnotationGraphic()) {
                StrokeAnnotationGraphic sag = new StrokeAnnotationGraphic(sagt);
                addStrokeAnnotationGraphic(sag);
            }
        }*/
    }

    /**
     * Build a {@code CompoundStroke} using the JAXBElement given in argument.
     * @param s
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public CompoundStroke(JAXBElement<CompoundStrokeType> s) throws InvalidStyle {
        this(s.getValue());
    }

    /**
     * Set the PreGap used in this {@code CompoundStroke}, as a {@code RealParameter} instance.
     * The PreGap is used to determine how far to advance along the line before starting to plot content.</p>
     * <p>Note that PreGap is considered to be a positive real number. If a negative value is given, 
     * it will be set to O.
     * @param preGap 
     */
    public void setPreGap(RealParameter preGap) {
        this.preGap = preGap;

        if (preGap != null) {
            this.preGap.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
        }
    }

    /**
     * Set the PostGap used in this {@code CompoundStroke}, as a {@code RealParameter} instance.
     * The PostGap is used to determine how far from the end of the line the plotting must be stopped.</p>
     * <p>Note that PostGap is considered to be a positive real number. If a negative value is given, 
     * it will be set to O.
     * @param preGap 
     */
    public void setPostGap(RealParameter postGap) {
        this.postGap = postGap;

        if (postGap != null) {
            this.postGap.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
        }
    }

    /**
     * Get the PreGap used in this {@code CompoundStroke}, as a {@code RealParameter} instance.
     * The PreGap is used to determine how far to advance along the line before starting to plot content.</p>
     * <p>The {@code PreGap} is set in a non-negative real context. That means that
     * the returned value shall be greater than or equal to 0.
     * @return 
     */
    public RealParameter getPreGap() {
        return preGap;
    }

    /**
     * Get the PostGap used in this {@code CompoundStroke}, as a {@code RealParameter} instance.
     * The PostGap is used to determine how far from the end of the line the plotting must be stopped.</p>
     * <p>The {@code PostGap} is set in a non-negative real context. That means that
     * the returned value shall be greater than or equal to 0.
     * @return 
     */
    public RealParameter getPostGap() {
        return postGap;
    }

    /**
     * Get the annotations embedded in this {@code CompoundStroke}
     * @return 
     *
    public List<StrokeAnnotationGraphic> getAnnotations() {
        return annotations;
    }
     * 

    /**
     * Gets the stroke elements embedded in this {@code CompoundStroke}.
     * @return 
     */
    public List<CompoundStrokeElement> getElements() {
        return elements;
    }

    @Override
    public Double getNaturalLength(Map<String,Value> map, Shape shp,
                MapTransform mt) throws ParameterException, IOException {
        return Double.POSITIVE_INFINITY; 
    }

    @Override
    public void draw(Graphics2D g2, Map<String,Value> map, Shape shape,
            boolean selected, MapTransform mt, double off) throws ParameterException, IOException {
        double offset = off;
        double initGap;
        double endGap;

        List<Shape> shapes;
        // if not using offset rapport, compute perpendiculat offset first
        if (!this.isOffsetRapport() && Math.abs(offset) > 0.0) {
            shapes = ShapeHelper.perpendicularOffset(shape, offset);
            // Setting offset to 0.0 let be sure the offset will never been applied twice!
            offset = 0.0;
        } else {
            shapes = new ArrayList<Shape>();
            shapes.add(shape);
        }

        //ShapeHelper.printvertices(shape);

        for (Shape shp : shapes) {

            if (preGap != null) {
                initGap = Uom.toPixel(preGap.getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                if (initGap > 0.0) {
                    
                    List<Shape> splitLine = ShapeHelper.splitLine(shp, initGap);
                    if (splitLine.size() == 2) {
                        shp = splitLine.get(1);
                    } else {
                        shp = null;
                    }
                }
            }

            if (shp != null) {
                if (postGap != null) {
                    endGap = Uom.toPixel(postGap.getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                    if (endGap > 0.0) {
                        double lineLength = ShapeHelper.getLineLength(shp);
                        shp = ShapeHelper.splitLine(shp, lineLength - endGap).get(0);
                    }
                }


                int nbElem = elements.size();

                //we will store the strokes and some informations about them in
                //the following arrays.
                double lengths[] = new double[nbElem];
                Stroke strokes[] = new Stroke[nbElem];
                Double preGaps[] = new Double[nbElem];
                Double postGaps[] = new Double[nbElem];

                double remainingLength = ShapeHelper.getLineLength(shp);
                double lineLength = remainingLength;
                int nbInfinite = 0;

                int i = 0;
                for (CompoundStrokeElement elem : elements) {
                    StrokeElement sElem = null;

                    if (elem instanceof StrokeElement) {
                        sElem = (StrokeElement) elem;
                    } else if (elem instanceof AlternativeStrokeElements) {
                        // do not retrieve the most suitable element, just take the first one...
                        AlternativeStrokeElements aElem = (AlternativeStrokeElements) elem;
                        sElem = aElem.getElements().get(0);
                    }
                    strokes[i] = sElem.getStroke();

                    if (sElem.getLength() != null) {
                        lengths[i] = Uom.toPixel(sElem.getLength().getValue(map),
                                getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                    } else {
                        lengths[i] = sElem.getStroke().getNaturalLengthForCompound(map, shp, mt);
                    }

                    if (sElem.getPreGap() != null) {
                        preGaps[i] = Uom.toPixel(sElem.getPreGap().getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                        remainingLength -= preGaps[i];
                    } else {
                        preGaps[i] = null;
                    }

                    if (sElem.getPostGap() != null) {
                        postGaps[i] = Uom.toPixel(sElem.getPostGap().getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                        remainingLength -= postGaps[i];
                    } else {
                        postGaps[i] = null;
                    }

                    if (Double.isInfinite(lengths[i])) {
                        nbInfinite++;
                    } else {
                        remainingLength -= lengths[i];
                    }
                    i++;
                }

                double patternLength = lineLength - remainingLength;
                if (nbInfinite > 0) {
                    double infiniteLength = remainingLength / nbInfinite;
                    for (i = 0; i < lengths.length; i++) {
                        if (Double.isInfinite(lengths[i])) {
                            lengths[i] = infiniteLength;
                        }
                    }
                } else { // fixed length pattern
                    if (this.isLengthRapport()) {
                        // Scale pattern to lineLength intergral fraction
                        int nbPattern = (int) ((lineLength / patternLength) + 0.5);
                        if (nbPattern < 1) {
                            // Make sure at least one pattern will be drawn
                            nbPattern = 1;
                        }
                        // Compute factor
                        double f = lineLength / (nbPattern * patternLength);
                        for (i = 0; i < nbElem; i++) {
                            lengths[i] *= f;
                            if (preGaps[i] != null) {
                                preGaps[i] *= f;
                            }
                            if (postGaps[i] != null) {
                                postGaps[i] *= f;
                            }
                        }
                    }
                }

                Shape scrap = shp;


                //while (ShapeHelper.getLineLength(chute) > 0) {
                i = 0; // stroke element iterator
                while (scrap != null) {

                    double scrapLength = ShapeHelper.getLineLength(scrap);
                    if (scrapLength < 1) {
                        break;
                    }

                    if (preGaps[i] != null && preGaps[i] > 0) {
                        List<Shape> splitLine = ShapeHelper.splitLine(scrap, preGaps[i]);
                        if (splitLine.size() > 1) {
                            scrap = splitLine.get(1);
                        } else {
                            break;
                        }
                    }

                    if (lengths[i] >= 0) {
                        // get two lines. first is the one we'll style with i'est element
                        List<Shape> splitLine = ShapeHelper.splitLine(scrap, lengths[i]);
                        Shape seg = splitLine.remove(0);
                        strokes[i].draw(g2, map, seg, selected, mt, offset);

                        if (splitLine.size() > 0) {
                            scrap = splitLine.remove(0);
                        } else {
                            break;
                        }
                    }

                    if (postGaps[i] != null && postGaps[i] > 0) {
                        List<Shape> splitLine = ShapeHelper.splitLine(scrap, postGaps[i]);
                        if (splitLine.size() > 1) {
                            scrap = splitLine.get(1);
                        } else {
                            break;
                        }
                    }

                    i = (i + 1) % nbElem;
                }


                /*
                 if (annotations.size() > 0) {
                    List<Shape> splitLineInSeg = ShapeHelper.splitLineInSeg(shp, patternLength);
                    //List<Shape> splitLineInSeg = new ArrayList<Shape>();
                    //splitLineInSeg.add(shp);
                    for (Shape seg : splitLineInSeg) {
                        for (StrokeAnnotationGraphic annotation : annotations) {
                            GraphicCollection graphic = annotation.getGraphic();
                            Rectangle2D bounds = graphic.getBounds(map, selected, mt);

                            RelativeOrientation rOrient = annotation.getRelativeOrientation();
                            if (rOrient == null) {
                                rOrient = RelativeOrientation.NORMAL;
                            }

                            double gWidth = bounds.getWidth();
                            double gHeight = bounds.getHeight();

                            double gLength;
                            switch (rOrient) {
                                case NORMAL:
                                case NORMAL_UP:
                                    gLength = gWidth;
                                    break;
                                case LINE:
                                    gLength = gHeight;
                                    break;
                                case PORTRAYAL:
                                default:
                                    gLength = Math.sqrt(gWidth * gWidth + gHeight * gHeight);
                                    break;
                            }

                            double pos = (ShapeHelper.getLineLength(seg) - gLength) * annotation.getRelativePosition().getValue(map) + gLength / 2.0;

                            Point2D.Double pt = ShapeHelper.getPointAt(seg, pos);

                            AffineTransform at = AffineTransform.getTranslateInstance(pt.x, pt.y);
                            if (rOrient != RelativeOrientation.PORTRAYAL) {

                                Point2D.Double ptA = ShapeHelper.getPointAt(seg, pos - gLength / 2.0);
                                Point2D.Double ptB = ShapeHelper.getPointAt(seg, pos + gLength / 2.0);

                                double theta = Math.atan2(ptB.y - ptA.y, ptB.x - ptA.x);

                                switch (rOrient) {
                                    case LINE:
                                        theta += 0.5 * Math.PI;
                                        break;
                                    case NORMAL_UP:
                                        if (theta < -Math.PI / 2 || theta > Math.PI / 2) {
                                            theta += Math.PI;
                                        }
                                        break;
                                }
                                at.concatenate(AffineTransform.getRotateInstance(theta));
                            }

                            graphic.draw(g2, map, selected, mt, at);
                        }
                    }
                }*/
            }
        }
    }

    @Override
    public List<SymbolizerNode> getChildren() {
        List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
        ls.addAll(elements);
        return ls;
    }

    private void addCompoundStrokeElement(CompoundStrokeElement cse) {
        elements.add(cse);
        cse.setParent(this);
    }

    /*private void addStrokeAnnotationGraphic(StrokeAnnotationGraphic sag) {
        annotations.add(sag);
        sag.setParent(this);
    }*/

    @Override
    public JAXBElement<CompoundStrokeType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createCompoundStroke(this.getJAXBType());
    }

    /**
     * Get a JAXB representation of this {@code CompoundStroke}.
     * @return 
     */
    public CompoundStrokeType getJAXBType() {
        CompoundStrokeType s = new CompoundStrokeType();

        this.setJAXBProperties(s);

        if (getOwnUom() != null) {
            s.setUom(getOwnUom().toURN());
        }

        if (this.preGap != null) {
            s.setPreGap(preGap.getJAXBParameterValueType());
        }

        if (this.postGap != null) {
            s.setPostGap(postGap.getJAXBParameterValueType());
        }


        List<Object> sElem = s.getStrokeElementOrAlternativeStrokeElements();
        //List<StrokeAnnotationGraphicType> sAnnot = s.getStrokeAnnotationGraphic();

        for (CompoundStrokeElement elem : this.elements) {
            sElem.add(elem.getJAXBType());
        }

        //for (StrokeAnnotationGraphic sag : annotations) {
        //    sAnnot.add(sag.getJaxbType());
        //}

        return s;
    }

    /**
     * Add an annotation to the set associated to this {@code CompoundStroke}.
     * @param annotation 
     *
    public void addAnnotation(StrokeAnnotationGraphic annotation) {
        if (annotation != null) {
            annotations.add(annotation);
            annotation.setParent(this);
        }
    }
     * 

    /**
     * Move the ith annotation up in the list of annotations.
     * @param i
     * @return 
     * {@code true} if the ith annotation existed and has been moved. {@code false}
     * if  i was negative, equal to 0 or greater than the list's size.
     *
    public boolean moveAnnotationUp(int i) {
        if (i > 0 && i < this.annotations.size()) {
            StrokeAnnotationGraphic anno = annotations.remove(i);
            annotations.add(i - 1, anno);
            return true;
        }
        return false;
    }*/

    /**
     * Move the ith annotation down in the list of annotations.
     * 
     * @param i
     * @return 
     * {@code true} if the ith annotation existed and has been moved. {@code false}
     * if  i was less than 1 or greater than the list's size -1.
     *
    public boolean moveAnnotationDown(int i) {
        if (i >= 0 && i < this.annotations.size() - 1) {
            StrokeAnnotationGraphic anno = annotations.remove(i);
            annotations.add(i + 1, anno);
            return true;
        }
        return false;
    }*/

    /**
     * Remove the ith annotation, if it exists
     * @param i
     * @return 
     * {@code true} if i was a valid range of the annotations list, and consequently
     * if something has been removed, false otherwise.
     *
    public boolean removeAnnotation(int i) {
        try {
            annotations.remove(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/

    /**
     * Add a stroke in the embedded list of stroke elements.
     * @param element 
     */
    public void addElement(CompoundStrokeElement element) {
        if (element != null) {
            elements.add(element);
            element.setParent(this);
        }
    }

    /**
     * Move the ith stroke up in the list of annotations.
     * @param i
     * @return 
     * {@code true} if the ith stroke existed and has been moved. {@code false}
     * if  i was negative, equal to 0 or greater than the list's size.
     */
    public boolean moveElementUp(int i) {
        if (i > 0 && i < this.elements.size()) {
            CompoundStrokeElement elem = elements.remove(i);
            elements.add(i - 1, elem);
            return true;
        }
        return false;
    }

    /**
     * Move the ith stroke down in the list of annotations.
     * 
     * @param i
     * @return 
     * {@code true} if the ith annotation existed and has been moved. {@code false}
     * if  i was less than 1 or greater than the list's size -1.
     */
    public boolean moveElementDown(int i) {
        if (i >= 0 && i < this.elements.size() - 1) {
            CompoundStrokeElement elem = elements.remove(i);
            elements.add(i + 1, elem);
            return true;
        }
        return false;
    }

    /**
     * Remove the ith stroke, if it exists
     * @param i
     * @return 
     * {@code true} if i was a valid range of the elements list, and consequently
     * if something has been removed, false otherwise.
     */
    public boolean removeElement(int i) {
        try {
            elements.remove(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
