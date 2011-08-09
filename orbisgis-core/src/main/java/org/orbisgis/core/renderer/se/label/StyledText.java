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
import org.gdms.data.SpatialDataSourceDecorator;

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

    public StyledText() {
        this("Label");
    }

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
     * For font, use this.uom, if no -> une parent's one
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

    public Halo getHalo() {
        return halo;
    }

    public void setHalo(Halo halo) {
        this.halo = halo;
        if (halo != null) {
            halo.setParent(this);
        }
    }

    public StringParameter getText() {
        return text;
    }

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

    public StringParameter getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(StringParameter fontFamily) {
        if (fontFamily != null) {
            this.fontFamily = fontFamily;
        }
    }

    public RealParameter getFontSize() {
        return fontSize;
    }

    public void setFontSize(RealParameter fontSize) {
        this.fontSize = fontSize;
        if (this.fontSize != null) {
            this.fontSize.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
        }
    }

    public StringParameter getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(StringParameter fontStyle) {
        if (fontStyle != null) {
            this.fontStyle = fontStyle;
            this.fontStyle.setRestrictionTo(styles);
        }
    }

    public StringParameter getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(StringParameter fontWeight) {
        if (fontWeight != null) {
            this.fontWeight = fontWeight;
            this.fontWeight.setRestrictionTo(weights);
        }
    }

    private Font getFont(SpatialDataSourceDecorator sds, long fid,
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
            //size = (size * mt.getDpi()) / 72.0;
            size = size * 72.0 / mt.getDpi();
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

    public Rectangle2D getBounds(Graphics2D g2, SpatialDataSourceDecorator sds, long fid,
            MapTransform mt) throws ParameterException, IOException {
        String text = this.text.getValue(sds, fid);
        return getBounds(g2, text, sds, fid, mt);
    }

    public Rectangle2D getBounds(Graphics2D g2, String text, SpatialDataSourceDecorator sds, long fid,
            MapTransform mt) throws ParameterException, IOException {

        Font font = getFont(sds, fid, mt);
        FontMetrics metrics = new FontMetrics(font) {
        };
        Rectangle2D bounds = metrics.getStringBounds(text, null);
        return bounds;
    }

    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid,
            boolean selected, MapTransform mt, AffineTransform at, RenderContext perm) throws ParameterException, IOException {
        String text = this.text.getValue(sds, fid);
        draw(g2, text, sds, fid, selected, mt, at, perm);
    }

    public Shape getOutline(Graphics2D g2, String text, SpatialDataSourceDecorator sds, long fid,
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

    public void drawOutlines(Graphics2D g2, ArrayList<Shape> outlines, SpatialDataSourceDecorator sds, long fid,
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

    public void draw(Graphics2D g2, String text, SpatialDataSourceDecorator sds, long fid,
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
    public double getEmInPixel(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException {
        double size = Uom.toPixel(12, Uom.PT, mt.getDpi(), mt.getScaleDenominator(), null);
        if (fontSize != null) {
            size = Uom.toPixel(fontSize.getValue(sds, fid), getFontUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }
        return size / 2.0;
    }

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

    public String dependsOnFeature() {

        String result = "";
        if (text != null) {
            result += " " + text.dependsOnFeature();
        }
        if (fontFamily != null) {
            result += " " + fontFamily.dependsOnFeature();
        }
        if (fontWeight != null) {
            result += " " + fontWeight.dependsOnFeature();
        }
        if (fontStyle != null) {
            result += " " + fontStyle.dependsOnFeature();
        }
        if (fontSize != null) {
            result += " " + fontSize.dependsOnFeature();
        }

        return result.trim();
    }
}
