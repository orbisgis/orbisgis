package org.orbisgis.core.renderer.se.label;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.io.IOException;
import java.util.ArrayList;
import javax.media.jai.RenderableGraphics;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.persistance.se.FontType;
import org.orbisgis.core.renderer.persistance.se.StyledLabelType;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.Graphic;
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

public final class StyledLabel implements SymbolizerNode, FillNode, StrokeNode, UomNode {

    private SymbolizerNode parent;
    private StringParameter labelText;
    private StringParameter fontFamily;
    private StringParameter fontWeight;
    private StringParameter fontStyle;
    private RealParameter fontSize;
    private Stroke stroke;
    private Fill fill;
    private Halo halo;
    private Uom uom;

    public StyledLabel() {
        this.labelText = new StringLiteral("Label");
        this.fontFamily = new StringLiteral("Arial");
        this.fontWeight = new StringLiteral("Normal");
        this.fontStyle = new StringLiteral("Normal");
        this.fontSize = new RealLiteral(12);

        setFill(new SolidFill(Color.BLACK, 100.0));
        setStroke(null);
    }

    public StyledLabel(String label) {
        this.labelText = new StringLiteral(label);
        this.fontFamily = new StringLiteral("Arial");
        this.fontWeight = new StringLiteral("Normal");
        this.fontStyle = new StringLiteral("Normal");
        this.fontSize = new RealLiteral(12);

        SolidFill f = new SolidFill();
        f.setOpacity(new RealLiteral(100.0));
        f.setColor(new ColorLiteral(Color.black));

        this.setFill(f);
    }

    public StyledLabel(StyledLabelType sl) throws InvalidStyle {
        if (sl.getFill() != null) {
            this.setFill(Fill.createFromJAXBElement(sl.getFill()));
        }

        if (sl.getStroke() != null) {
            this.setStroke(Stroke.createFromJAXBElement(sl.getStroke()));
        }

        if (sl.getFont() != null) {
            FontType font = sl.getFont();

            if (font.getUnitOfMeasure() != null) {
                this.setUom(Uom.fromOgcURN(font.getUnitOfMeasure()));
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

        if (sl.getLabelText() != null) {
            this.setLabelText(SeParameterFactory.createStringParameter(sl.getLabelText()));
        }
    }

    @Override
    public Uom getUom() {
        if (this.uom != null) {
            return this.uom;
        } else {
            return parent.getUom();
        }
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

    public StringParameter getLabelText() {
        return labelText;
    }

    public void setLabelText(StringParameter labelText) {
        this.labelText = labelText;
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
        this.fontFamily = fontFamily;
    }

    public RealParameter getFontSize() {
        return fontSize;
    }

    public void setFontSize(RealParameter fontSize) {
        this.fontSize = fontSize;
        if (this.fontSize != null) {
            this.fontSize.setContext(RealParameterContext.percentageContext);
        }
    }

    public StringParameter getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(StringParameter fontStyle) {
        this.fontStyle = fontStyle;
    }

    public StringParameter getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(StringParameter fontWeight) {
        this.fontWeight = fontWeight;
    }

    private RenderableGraphics getTextImage(String text, SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {
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
            size = Uom.toPixel(fontSize.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
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


        Font font = new Font(family, st, (int) size);
        FontMetrics metrics = new FontMetrics(font) {
        };

        Rectangle2D bounds = metrics.getStringBounds(text, null);

        RenderableGraphics rg = new RenderableGraphics(bounds);

        TextLayout tl = new TextLayout(text, font, rg.getFontRenderContext());

        double ty;

        ty = -bounds.getMaxY() + bounds.getHeight() / 2.0;

        Shape outline = tl.getOutline(AffineTransform.getTranslateInstance(-bounds.getCenterX(), ty));

        double margin = 0.0;

        if (stroke != null) {
            margin = stroke.getMaxWidth(sds, fid, mt);
        }
        if (halo != null) {
            margin += halo.getHaloRadius(sds, fid, mt);
        }

        rg = Graphic.getNewRenderableGraphics(outline.getBounds2D(), margin, mt);

        if (halo != null) {
            halo.draw(rg, sds, fid, selected, outline.getBounds(), mt, false);
            //halo.draw(rg, sds, fid, selected, outline, mt, true);
        }

        /**
         * No fill and no stroke : apply default solidfill !
         */
        if (fill == null && stroke == null) {
            SolidFill sf = new SolidFill(Color.BLACK, 100.0);
            sf.draw(rg, sds, fid, outline, selected, mt);
            sf.setParent(this);
        }

        if (fill != null) {
            fill.draw(rg, sds, fid, outline, selected, mt);
        }

        if (stroke != null) {
            stroke.draw(rg, sds, fid, outline, selected, mt, 0.0);
        }

        // HALO, FILL, STROKE
        return rg;

    }

    public double getEmInPixel(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException {
        double size = Uom.toPixel(12, Uom.PT, mt.getDpi(), mt.getScaleDenominator(), null);
        if (fontSize != null) {
            size = Uom.toPixel(fontSize.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }
        return size / 2.0;
    }

    /**
     * Return one graphic for each glyph and null for space ( this.getEm() give effective width of spaces)
     * @param sds
     * @param fid
     * @param selected
     * @param mt
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    public ArrayList<RenderableGraphics> getGlyphs(SpatialDataSourceDecorator sds,
            long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {

        ArrayList<RenderableGraphics> rGlyphs = new ArrayList<RenderableGraphics>();

        String text = labelText.getValue(sds, fid);
        String[] glyphs = text.split("");

        for (String glyph : glyphs) {
            if (!glyph.trim().isEmpty()) {
                RenderableGraphics textImage = getTextImage(glyph, sds, fid, selected, mt);
                rGlyphs.add(textImage);
            } else {
                rGlyphs.add(null);
            }
        }

        return rGlyphs;
    }

    public RenderableGraphics getImage(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {
        String text = labelText.getValue(sds, fid);
        return getTextImage(text, sds, fid, selected, mt);
    }

    public StyledLabelType getJAXBType() {
        StyledLabelType l = new StyledLabelType();

        if (labelText != null) {
            l.setLabelText(labelText.getJAXBParameterValueType());
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
        if (labelText != null) {
            result += " " + labelText.dependsOnFeature();
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
