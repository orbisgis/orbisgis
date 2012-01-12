package org.orbisgis.core.renderer.se.label;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.gdms.data.DataSource;

import org.orbisgis.core.map.MapTransform;

import net.opengis.se._2_0.core.FontType;
import net.opengis.se._2_0.core.StyledTextType;
import org.orbisgis.core.renderer.RenderContext;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

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
 * @author maxence, alexis
 */
public final class StyledText implements SymbolizerNode, FillNode, StrokeNode, UomNode {

    private SymbolizerNode parent;
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
     * @param label 
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
     * Build a <code>StyledText</code> from the JAXB element given in argument.
     * @param sl
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
     * @return
     */
    public Uom getFontUom() {
        if (this.uom != null) {
            return this.uom;
        } else {
            return parent.getUom();
        }
    }

    @Override
    public Uom getUom() {
        // Note: this.uom only affect font size
        return parent.getUom();
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
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
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
     * @param halo 
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
     * @param text 
     */
    public void setText(StringParameter text) {
        if (text != null) {
            this.text = text;
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
     * @param fontFamily 
     */
    public void setFontFamily(StringParameter fontFamily) {
        if (fontFamily != null) {
            this.fontFamily = fontFamily;
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
     * @param fontFamily 
     */
    public void setFontSize(RealParameter fontSize) {
        this.fontSize = fontSize;
        if (this.fontSize != null) {
            this.fontSize.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
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
     * @param fontFamily 
     */
    public void setFontStyle(StringParameter fontStyle) {
        if (fontStyle != null) {
            this.fontStyle = fontStyle;
            this.fontStyle.setRestrictionTo(styles);
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
     * @param fontFamily 
     */
    public void setFontWeight(StringParameter fontWeight) {
        if (fontWeight != null) {
            this.fontWeight = fontWeight;
            this.fontWeight.setRestrictionTo(weights);
        }
    }

    private Font getFont(DataSource sds, long fid,
            MapTransform mt) throws ParameterException, IOException {
        String family = "Arial";
        if (fontFamily != null) {
            family = fontFamily.getValue(sds, fid);
        }

        // TODO Family is comma delimeted list of fonts family. Choose the first available

        String weight = "normal";
        if (fontWeight != null) {
            weight = fontWeight.getValue(sds, fid);
        }

        String style = "normal";
        if (fontStyle != null) {
            style = fontStyle.getValue(sds, fid);
        }

        //double size = Uom.toPixel(12, Uom.PT, mt.getDpi(), mt.getScaleDenominator(), null);
        double size = 12.0;
        if (fontSize != null) {
            size = Uom.toPixel(fontSize.getValue(sds, fid), getFontUom(), mt.getDpi(), mt.getScaleDenominator(), null);
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
     * @param g2
     * @param sds
     * @param fid
     * @param mt
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    public Rectangle2D getBounds(Graphics2D g2, DataSource sds, long fid,
            MapTransform mt) throws ParameterException, IOException {
        String txt = this.text.getValue(sds, fid);
        return getBounds(g2, txt, sds, fid, mt);
    }

    /**
     * Get the minimal {@code Rectangle2D} that contains this {@code StyledText}.
     * @param g2
     * @param text
     * @param sds
     * @param fid
     * @param mt
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    public Rectangle2D getBounds(Graphics2D g2, String text, DataSource sds, long fid,
            MapTransform mt) throws ParameterException, IOException {

        Font font = getFont(sds, fid, mt);
        FontMetrics metrics = new FontMetrics(font) {
        };
        Rectangle2D bounds = metrics.getStringBounds(text, null);
        return bounds;
    }

    /**
     * Draw this {@code StyledText} in the {@code Graphics2D g2}.
     * @param g2
     * @param sds
     * @param fid
     * @param selected
     * @param mt
     * @param at
     * @param perm
     * @throws ParameterException
     * @throws IOException
     */
    public void draw(Graphics2D g2, DataSource sds, long fid,
            boolean selected, MapTransform mt, AffineTransform at, RenderContext perm) throws ParameterException, IOException {
        String txt = this.text.getValue(sds, fid);
        draw(g2, txt, sds, fid, selected, mt, at, perm);
    }

    public Shape getOutline(Graphics2D g2, String text, DataSource sds, long fid,
            boolean selected, MapTransform mt, AffineTransform at, RenderContext perm) throws ParameterException, IOException {

        Font font = getFont(sds, fid, mt);
        TextLayout tl = new TextLayout(text, font, g2.getFontRenderContext());

        FontMetrics metrics = new FontMetrics(font) {
        };
        Rectangle2D bounds = metrics.getStringBounds(text, null);

        double ty = -bounds.getMaxY() + bounds.getHeight() / 2.0;

        AffineTransform rat;
        if (at != null) {
            rat = new AffineTransform(at);
        } else {
            rat = new AffineTransform();
        }
        rat.concatenate(AffineTransform.getTranslateInstance(-bounds.getCenterX(), ty));

        Shape outline = tl.getOutline(rat);

        return outline;
    }

    public void drawOutlines(Graphics2D g2, ArrayList<Shape> outlines, DataSource sds, long fid,
            boolean selected, MapTransform mt) throws ParameterException, IOException {

        if (halo != null) {
            for (Shape outline : outlines) {
                //halo.draw(rg, sds, fid, selected, outline.getBounds(), mt, false);
                //System.out.println ("Draw halo");
                halo.draw(g2, sds, fid, selected, outline, mt, true);
            }
        }

        for (Shape outline : outlines) {
            /**
             * No fill and no stroke : apply default solidfill !
             */
            if (fill == null && stroke == null) {
                SolidFill sf = new SolidFill(Color.BLACK, 1.0);
                sf.setParent(this);
                sf.draw(g2, sds, fid, outline, selected, mt);
            }

            if (fill != null) {
                fill.draw(g2, sds, fid, outline, selected, mt);
            }

            if (stroke != null) {
                stroke.draw(g2, sds, fid, outline, selected, mt, 0.0);
            }
        }
    }

    /**
     * Draw this {@code StyledText} in the {@code Graphics2D g2}.
     * @param g2
     * @param text
     * @param sds
     * @param fid
     * @param selected
     * @param mt
     * @param at
     * @param perm
     * @throws ParameterException
     * @throws IOException
     */
    public void draw(Graphics2D g2, String text, DataSource sds, long fid,
            boolean selected, MapTransform mt, AffineTransform at, RenderContext perm) throws ParameterException, IOException {

        ArrayList<Shape> outlines = new ArrayList<Shape>();
        outlines.add(getOutline(g2, text, sds, fid, selected, mt, at, perm));
        drawOutlines(g2, outlines, sds, fid, selected, mt);
    }

    /**
     *
     * @param sds
     * @param fid
     * @param mt
     * @return
     * @throws ParameterException
     */
    public double getEmInPixel(DataSource sds, long fid, MapTransform mt) throws ParameterException {
        double size = Uom.toPixel(12, Uom.PT, mt.getDpi(), mt.getScaleDenominator(), null);
        if (fontSize != null) {
            size = Uom.toPixel(fontSize.getValue(sds, fid), getFontUom(), mt.getDpi(), mt.getScaleDenominator(), null);
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
    public HashSet<String> dependsOnFeature() {

        HashSet<String> result = new HashSet<String>();
        if (text != null) {
            result.addAll(text.dependsOnFeature());
        }
        if (fontFamily != null) {
            result.addAll(fontFamily.dependsOnFeature());
        }
        if (fontWeight != null) {
            result.addAll(fontWeight.dependsOnFeature());
        }
        if (fontStyle != null) {
            result.addAll(fontStyle.dependsOnFeature());
        }
        if (fontSize != null) {
            result.addAll(fontSize.dependsOnFeature());
        }

        return result;
    }
}
