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
package org.orbisgis.core.renderer.se.label;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import net.opengis.se._2_0.core.FontType;
import net.opengis.se._2_0.core.StyledTextType;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;

/**
 * This class embed all the informations needed to represent text of any kind on a map.
 * A <code>StyledText</code> is defined with several values :
 * <ul><li>A text value</li>
 * <li>A font</li>
 * <li>A weight (Normal or Bold)</li>
 * <li>A style (Normal, Italic or Oblique)</li>
 * <li>A size</li>
 * <li>A stroke</li></ul>
 * Color and opacity of the text are defined using a <code>Fill</code> instance
 * @author Maxence Laurent, Alexis Guéganno
 */
public final class StyledText extends AbstractSymbolizerNode implements UomNode, FillNode, StrokeNode {
    private StringParameter text;
    private StringParameter fontFamily;
    private StringParameter fontWeight;
    private StringParameter fontStyle;
    private RealParameter fontSize;
    private Stroke stroke;
    private Fill fill;
    private Halo halo;
    private Uom uom;
    private String[] weights = {"Normal", "Bold"};
    private String[] styles = {"Normal", "Italic", "Oblique"};

    /**
     * Fill a new StyledText with default values. Inner text is <code>Label</code>,
     * it will be displayed in Arial, with a normal weight, a normal style, a 12 font size.
     * It is displayed in black, and completely opaque.
     */
    public StyledText() {
        this("Label");
    }

    
    /**
     * Fill a new <code>StyledText</code> with the given text value and default values. The text 
     * will be displayed in Arial, with a normal weight, a normal style, a 12 font size.
     * It is displayed in black, and completely opaque.
     * @param label The label we want to display.
     */
    public StyledText(String label) {
        setText(new StringLiteral(label));
        setFontFamily(new StringLiteral("Arial"));
        setFontWeight(new StringLiteral("Normal"));
        setFontStyle(new StringLiteral("Normal"));
        setFontSize(new RealLiteral(12));
        setUom(Uom.PT);

        SolidFill f = new SolidFill();
        f.setOpacity(new RealLiteral(1.0));
        f.setColor(new ColorLiteral(Color.black));

        this.setFill(f);
    }

    /**
     * Build a <code>StyledText</code> from the JaXB element given in argument.
     * @param sl The original JaXB object.
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public StyledText(StyledTextType sl) throws InvalidStyle {
        if (sl.getFill() != null) {
            this.setFill(Fill.createFromJAXBElement(sl.getFill()));
        }

        if (sl.getStroke() != null) {
            this.setStroke(Stroke.createFromJAXBElement(sl.getStroke()));
        }

        if (sl.getFont() != null) {
            FontType font = sl.getFont();

            if (font.getUom() != null) {
                this.setUom(Uom.fromOgcURN(font.getUom()));
            }

            if (font.getFontSize() != null) {
                this.setFontSize(SeParameterFactory.createRealParameter(font.getFontSize()));
            }

            if (font.getFontFamily() != null) {
                this.setFontFamily(SeParameterFactory.createStringParameter(font.getFontFamily()));
            }

            if (font.getFontStyle() != null) {
                this.setFontStyle(SeParameterFactory.createStringParameter(font.getFontStyle()));
            }

            if (font.getFontWeight() != null) {
                this.setFontWeight(SeParameterFactory.createStringParameter(font.getFontWeight()));
            }
        }

        if (sl.getHalo() != null) {
            this.setHalo(new Halo(sl.getHalo()));
        }

        if (sl.getText() != null) {
            this.setText(SeParameterFactory.createStringParameter(sl.getText()));
        }
    }

    /**
     * Tries to retrieve the UOM of the font if any. If non can be found, return the UOM
     * of the parent node.
     * @return The unit of measure used to compute the text's size.
     */
    public Uom getFontUom() {
        if (uom != null) {
            return uom;
        } else if(getParent() instanceof UomNode){
            return ((UomNode)getParent()).getUom();
        } else {
                return Uom.PX;
        }
    }

    @Override
    public Uom getUom() {
        // Note: this.uom only affect font size
        return ((UomNode)getParent()).getUom();
    }

    @Override
    public void setUom(Uom u) {
        this.uom = u;
    }

    @Override
    public Uom getOwnUom() {
        return this.uom;
    }

    @Override
    public Fill getFill() {
        return fill;
    }

    @Override
    public void setFill(Fill fill) {
        this.fill = fill;
        if (fill != null) {
            fill.setParent(this);
        }
    }

    /**
     * Return the halo associated to this <code>StyledText</code>.
     * @return 
     * A <code>Halo</code> instance, or null if it has not been set.
     */
    public Halo getHalo() {
        return halo;
    }

    /**
     * Set the halo associated to this <code>StyledText</code>
     * @param halo The halo.
     */
    public void setHalo(Halo halo) {
        this.halo = halo;
        if (halo != null) {
            halo.setParent(this);
        }
    }

    /**
     * Get the text contained in this <code>StyledText</code>
     * @return the text contained in this <code>StyledText</code> as a <code>StringParameter</code> instance.
     */
    public StringParameter getText() {
        return text;
    }

    /**
     * Set the text contained in this <code>StyledText</code>
     * @param text The text to be displayed
     */
    public void setText(StringParameter text) {
        if (text != null) {
            this.text = text;
            this.text.setParent(this);
        }
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
        if (stroke != null) {
            stroke.setParent(this);
        }
    }

    /**
     * Get the font family used to represent this <code>StyledText</code>
     * @return 
     * The fontFamily as a <code>StringParameter</code>
     */
    public StringParameter getFontFamily() {
        return fontFamily;
    }

    /**
     * Set the font family used to represent this <code>StyledText</code>
     * @param fontFamily The new font's family
     */
    public void setFontFamily(StringParameter fontFamily) {
        if (fontFamily != null) {
            this.fontFamily = fontFamily;
            this.fontFamily.setParent(this);
        }
    }

    /**
     * Get the font size used to represent this <code>StyledText</code>
     * @return 
     * The font size as a <code>RealParameter</code>
     */
    public RealParameter getFontSize() {
        return fontSize;
    }

    /**
     * Set the font size used to represent this <code>StyledText</code>
     * @param fontSize The new font's size
     */
    public void setFontSize(RealParameter fontSize) {
        this.fontSize = fontSize;
        if (this.fontSize != null) {
            this.fontSize.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
            this.fontSize.setParent(this);
        }
    }

    /**
     * Get the font style used to represent this <code>StyledText</code>
     * @return 
     * The font style as a <code>StringParameter</code>
     */
    public StringParameter getFontStyle() {
        return fontStyle;
    }

    /**
     * Set the font style used to represent this <code>StyledText</code>
     * @param fontStyle The new font's style
     */
    public void setFontStyle(StringParameter fontStyle) {
        if (fontStyle != null) {
            this.fontStyle = fontStyle;
            this.fontStyle.setRestrictionTo(styles);
            this.fontStyle.setParent(this);
        }
    }

    /**
     * Get the font weight used to represent this <code>StyledText</code>
     * @return 
     * The font weight as a <code>StringParameter</code>
     */
    public StringParameter getFontWeight() {
        return fontWeight;
    }

    /**
     * Set the font weight used to represent this <code>StyledText</code>
     * @param fontWeight The new font's weight
     */
    public void setFontWeight(StringParameter fontWeight) {
        if (fontWeight != null) {
            this.fontWeight = fontWeight;
            this.fontWeight.setRestrictionTo(weights);
            this.fontWeight.setParent(this);
        }
    }

    private Font getFont(Map<String, Value> map, MapTransform mt) throws ParameterException, IOException {
        String family = "Arial";
        if (fontFamily != null) {
            family = fontFamily.getValue(map);
        }

        // TODO Family is comma delimeted list of fonts family. Choose the first available

        String weight = "normal";
        if (fontWeight != null) {
            weight = fontWeight.getValue(map);
        }

        String style = "normal";
        if (fontStyle != null) {
            style = fontStyle.getValue(map);
        }

        //double size = Uom.toPixel(12, Uom.PT, mt.getDpi(), mt.getScaleDenominator(), null);
        double size = 12.0;
        if (fontSize != null) {
            size = Uom.toPixel(fontSize.getValue(map), getFontUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }

        int st = Font.PLAIN;

        if (weight.equalsIgnoreCase("bold")) {
            st = Font.BOLD;
        }

        if (style.equalsIgnoreCase("italic")) {
            if (st == Font.PLAIN) {
                st |= Font.ITALIC;
            } else {
                st = Font.ITALIC;
            }
        }

        return new Font(family, st, (int) size);
    }

    /**
     * Get the minimal {@code Rectangle2D} that contains this {@code StyledText}.
     * @param g2 The graphics we draw with
     * @param map The map of input values
     * @param mt The current MapTransform
     * @throws ParameterException If we can't get the input parameters
     * @throws IOException If something goes wrong while handling fonts
     */
    public Rectangle2D getBounds(Graphics2D g2, Map<String, Value> map,
            MapTransform mt) throws ParameterException, IOException {
        String txt = this.text.getValue(map);
        return getBounds(g2, txt, map, mt);
    }

    /**
     * Get the minimal {@code Rectangle2D} that contains this {@code StyledText}.
     * @param g2 The graphics we draw with
     * @param text The text for which we need the bounds.
     * @param map The map of input values
     * @param mt The current MapTransform
     * @return The bounds of the text
     * @throws ParameterException
     * @throws IOException
     */
    public Rectangle2D getBounds(Graphics2D g2, String text, Map<String, Value> map,
            MapTransform mt) throws ParameterException, IOException {

        Font font = getFont(map, mt);
        FontMetrics metrics = g2.getFontMetrics(font);
        return metrics.getStringBounds(text, null);
    }

    /**
     * Draw this {@code StyledText} in the {@code Graphics2D g2}.
     * @param g2 The graphics we draw with
     * @param map The map of input values
     * @param selected If true, the input geometry has been selected
     * @param mt The current MapTransform
     * @param at The configured affine transformation
     * @param perm The rendering permissions
     * @throws ParameterException
     * @throws IOException
     */
    public void draw(Graphics2D g2, Map<String, Value> map,
            boolean selected, MapTransform mt, AffineTransform at, RenderContext perm) throws ParameterException, IOException {
        String txt = this.text.getValue(map);
        draw(g2, txt, map, selected, mt, at, perm, Label.VerticalAlignment.TOP);
    }

    /**
     * Draw this {@code StyledText} in the {@code Graphics2D g2}.
     * @param g2 The graphics we draw with
     * @param map The map of input values
     * @param selected If true, the input geometry has been selected
     * @param mt The current MapTransform
     * @param at The configured affine transformation
     * @param perm The rendering permissions
     * @param va The needed vertical alignment
     * @throws ParameterException
     * @throws IOException
     */
    public void draw(Graphics2D g2, Map<String, Value> map,
            boolean selected, MapTransform mt, AffineTransform at, RenderContext perm,
            Label.VerticalAlignment va) throws ParameterException, IOException {
        String txt = this.text.getValue(map);
        draw(g2, txt, map, selected, mt, at, perm, va);
    }

    /**
     * Gets the outline of the given {@code String} as a shape. This shapes is
     * made of the boundary(ies) of the text, that will have to be stroked and
     * fill, with the default vertical alignment
     * ({@code VerticalAlignment.TOP}).
     * @param g2
     * The {@code Graphics2D} instance used to render the map we are drawing.
     * @param text
     * The text we want to compute the outline of.
     * @param map     The map of input values
     * @param mt
     * Used to compute the font's size.
     * @param at
     * The AffineTransform that we must apply to the shape before returning it.
     * @param perm The rendering permissions
     * @return The needed Shape
     * @throws ParameterException
     * @throws IOException
     */
    public Shape getOutline(Graphics2D g2, String text, Map<String, Value> map,
            MapTransform mt, AffineTransform at, RenderContext perm)
            throws ParameterException, IOException {
        return getOutline(g2, text, map, mt, at, perm, Label.VerticalAlignment.TOP);
    }

    /**
     * Gets the outline of the given {@code String} as a shape. This shapes is
     * made of the boundary(ies) of the text, that will have to be stroked and
     * fill.
     * @param g2
     * The {@code Graphics2D} instance used to render the map we are drawing.
     * @param text
     * The text we want to compute the outline of.
     * @param map     The map of input values
     * @param mt
     * Used to compute the font's size.
     * @param at
     * The AffineTransform that we must apply to the shape before returning it.
     * @param perm The rendering permissions
     * @param va
     * The {@code Label.VerticalAlignment} we must use to determine where to put
     * the baseline of {@code text}.
     * @return The needed Shape
     * @throws ParameterException
     * If we fail to retrieve a parameter used to configure this {@code
     * StyledText}.
     * @throws IOException
     * If an error occurred while retrieving the {@code Font}.
     */
    public Shape getOutline(Graphics2D g2, String text, Map<String, Value> map,
            MapTransform mt, AffineTransform at, RenderContext perm, Label.VerticalAlignment va)
            throws ParameterException, IOException {
        Font font = getFont(map, mt);
        TextLayout tl = new TextLayout(text, font, g2.getFontRenderContext());
        FontMetrics metrics = g2.getFontMetrics(font);
        double dy=0;
        switch(va){
            case BASELINE:
                break;
            case BOTTOM:
                dy = metrics.getAscent();
                break;
            case TOP:
                dy = -metrics.getDescent();
                break;
            case MIDDLE:
            default:
                dy = (metrics.getAscent() - metrics.getDescent()) / 2.0;
        }
        AffineTransform rat;
        if (at != null) {
            rat = new AffineTransform(at);
        } else {
            rat = new AffineTransform();
        }
        //We apply the translation used to manage the height of the text on the
        //line BEFORE to apply at : we use concatenate.
        rat.concatenate(AffineTransform.getTranslateInstance(0, dy));
        return tl.getOutline(rat);
    }

    /**
     * Draw the list of given "outlines", that is the list of characters already
     * transformed to {@code Shape} instances. We'll use for that, of course,
     * the inner {@code Fill}, {@code Halo} and {@code Stroke} instances. If
     * they are not set, a simple default {@code SolidFill} will be used.
     * @param g2 The graphics we draw with
     * @param map The map of input values
     * @param selected If true, the input geometry has been selected
     *
     * @param outlines  The list of needed outlines
     * @param mt
     * Used to compute the font's size.
     * @throws ParameterException
     * @throws IOException
     */
    public void drawOutlines(Graphics2D g2, ArrayList<Shape> outlines, Map<String, Value> map,
            boolean selected, MapTransform mt) throws ParameterException, IOException {
        if (halo != null) {
            for (Shape outline : outlines) {
                //halo.draw(rg, map, selected, outline.getBounds(), mt, false);
                halo.draw(g2, map, selected, outline, mt, true);
            }
        }
        for (Shape outline : outlines) {
            /**
             * No fill and no stroke : apply default SolidFill !
             */
            if (fill == null && stroke == null) {
                SolidFill sf = new SolidFill(Color.BLACK, 1.0);
                sf.setParent(this);
                sf.draw(g2, map, outline, selected, mt);
            }
            if (fill != null) {
                fill.draw(g2, map, outline, selected, mt);
            }
            if (stroke != null) {
                stroke.draw(g2, map, outline, selected, mt, 0.0);
            }
        }
    }

    /**
     * Draw this {@code StyledText} in the {@code Graphics2D g2}.
     * @param g2 The graphics we draw with
     * @param text The text we want to draw
     * @param map The map of input values
     * @param selected If true, the input geometry has been selected
     * @param mt The current MapTransform
     * @param at The configured affine transformation
     * @param perm The rendering permissions
     * @param va The needed vertical alignment
     * @throws ParameterException
     * @throws IOException
     */
    public void draw(Graphics2D g2, String text, Map<String, Value> map,
            boolean selected, MapTransform mt, AffineTransform at, RenderContext perm,
            Label.VerticalAlignment va) throws ParameterException, IOException {

        ArrayList<Shape> outlines = new ArrayList<Shape>();
        outlines.add(getOutline(g2, text, map, mt, at, perm, va));
        drawOutlines(g2, outlines, map, selected, mt);
    }

    /**
     *
     * @param map
     * @param mt
     * @return
     * @throws ParameterException
     */
    public double getEmInPixel(Map<String, Value> map, MapTransform mt) throws ParameterException {
        double size = Uom.toPixel(12, Uom.PT, mt.getDpi(), mt.getScaleDenominator(), null);
        if (fontSize != null) {
            size = Uom.toPixel(fontSize.getValue(map), getFontUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }
        return size / 2.0;
    }

    /**
     * Get a new JAXB representation of this {@code StyledText}.
     * @return
     * A {@code StyledTextType} representing this {@code StyledText}.
     */
    public StyledTextType getJAXBType() {
        StyledTextType l = new StyledTextType();
        if (text != null) {
            l.setText(text.getJAXBParameterValueType());
        }
        if (halo != null) {
            l.setHalo(halo.getJAXBType());
        }
        if (fill != null) {
            l.setFill(fill.getJAXBElement());
        }
        if (stroke != null) {
            l.setStroke(stroke.getJAXBElement());
        }
        FontType font = new FontType();
        if (this.getOwnUom() != null) {
            font.setUom(getOwnUom().toURN());
        }
        if (fontFamily != null) {
            font.setFontFamily(fontFamily.getJAXBParameterValueType());
        }
        if (fontWeight != null) {
            font.setFontWeight(fontWeight.getJAXBParameterValueType());
        }
        if (fontSize != null) {
            font.setFontSize(fontSize.getJAXBParameterValueType());
        }
        if (fontStyle != null) {
            font.setFontStyle(fontStyle.getJAXBParameterValueType());
        }
        l.setFont(font);
        return l;
    }

    @Override
    public java.util.List<SymbolizerNode> getChildren() {
        java.util.List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
        if (text != null) {
            ls.add(text);
        }
        if (fontFamily != null) {
            ls.add(fontFamily);
        }
        if (fontWeight != null) {
            ls.add(fontWeight);
        }
        if (fontStyle != null) {
            ls.add(fontStyle);
        }
        if (fontSize != null) {
            ls.add(fontSize);
        }
        if (stroke != null) {
            ls.add(stroke);
        }
        if (fill != null) {
            ls.add(fill);
        }
        if (halo != null) {
            ls.add(halo);
        }
        return ls;
    }
}
