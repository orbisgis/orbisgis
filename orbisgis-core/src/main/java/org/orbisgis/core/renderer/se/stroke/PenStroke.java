package org.orbisgis.core.renderer.se.stroke;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.PenStrokeType;

import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.GraphicFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.color.ColorHelper;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

/**
 * Basic stroke for linear features
 * @todo implement dasharray/dashoffset
 * @author maxence
 */
public final class PenStroke extends Stroke {

    @Override
    public boolean dependsOnFeature() {
        if (useColor) {
            if (color != null && color.dependsOnFeature()) {
                return true;
            }
        } else {
            if (stipple != null && stipple.dependsOnFeature()) {
                return true;
            }
        }

        return (this.dashOffset != null && dashOffset.dependsOnFeature())
                || (this.opacity != null && opacity.dependsOnFeature())
                || (this.width != null && width.dependsOnFeature());
    }

    public enum LineCap {

        BUTT, ROUND, SQUARE;

        public ParameterValueType getParameterValueType() {
            return SeParameterFactory.createParameterValueType(this.name().toLowerCase());
        }
    }

    public enum LineJoin {

        MITRE, ROUND, BEVEL;

        public ParameterValueType getParameterValueType() {
            return SeParameterFactory.createParameterValueType(this.name().toLowerCase());
        }
    }

    /**
     * Create a standard undashed 0.1mm-wide opaque black stroke
     */
    public PenStroke() {
        setColor(new ColorLiteral(Color.BLACK));
        setWidth(new RealLiteral(0.1));
        setUom(Uom.MM);
        setOpacity(new RealLiteral(100.0));
        dashArray = new StringLiteral("");
        updateBasicStroke();
    }

    public PenStroke(PenStrokeType t) {
        this();

        if (t.getColor() != null) {
            this.setColor(SeParameterFactory.createColorParameter(t.getColor()));

        } else if (t.getStipple() != null) {
            this.setStipple(new GraphicFill(t.getStipple()));
        } else {
            // TODO  Neither color nor stipple
        }

        if (t.getDashArray() != null) {
			System.out.println ("Read DASHARRAY from XML");
			this.setDashArray(SeParameterFactory.createStringParameter(t.getDashArray()));
			System.out.println ("This.dashArray => " + this.dashArray);
		}

        if (t.getDashOffset() != null) {
			System.out.println ("Read dash Offset !");
            this.setDashOffset(SeParameterFactory.createRealParameter(t.getDashOffset()));
        }

        if (t.getWidth() != null) {
            this.setWidth(SeParameterFactory.createRealParameter(t.getWidth()));
        }

        if (t.getLineCap() != null) {
        }

        if (t.getLineJoin() != null) {
            // TODO
        }

        if (t.getOpacity() != null) {
            this.setOpacity(SeParameterFactory.createRealParameter(t.getOpacity()));
        }

        if (t.getUnitOfMeasure() != null) {
            this.setUom(Uom.fromOgcURN(t.getUnitOfMeasure()));
        }
		else{
			this.uom = null;
		}

        this.updateBasicStroke();
    }

    public PenStroke(JAXBElement<PenStrokeType> s) {
        this(s.getValue());
    }

    /**
     * default painter is either a solid color or a stipple
     * @return true when default is color, false when it's the stipple
     */
    public boolean useColor() {
        return useColor;
    }

    /**
     * Indicates which painter to use
     * If default painter is undefined, a random solid color is used
     * @param useColor true => use color; false => use stipple
     */
    public void setUseColor(boolean useColor) {
        this.useColor = useColor;
        updateBasicStroke();
    }

    public void setColor(ColorParameter color) {
        this.color = color;
        useColor = true;
        updateBasicStroke();
    }

    public ColorParameter getColor() {
        return color;
    }

    public void setStipple(GraphicFill stipple) {
        this.stipple = stipple;
        stipple.setParent(this);
        useColor = false;
        updateBasicStroke();
    }

    public GraphicFill getStipple() {
        return stipple;
    }

    public void setLineCap(LineCap cap) {
        lineCap = cap;
        updateBasicStroke();
    }

    public LineCap getLineCap() {
        return lineCap;
    }

    public void setLineJoin(LineJoin join) {
        lineJoin = join;
        updateBasicStroke();
    }

    public LineJoin getLineJoin() {
        return lineJoin;
    }

    public void setOpacity(RealParameter opacity) {
        this.opacity = opacity;
        updateBasicStroke();
    }

    public RealParameter getOpacity() {
        return this.opacity;
    }

    public void setWidth(RealParameter width) {
        this.width = width;
        updateBasicStroke();
    }

    public RealParameter getWidth() {
        return this.width;
    }

    public RealParameter getDashOffset() {
        return dashOffset;
    }

    public void setDashOffset(RealParameter dashOffset) {
        this.dashOffset = dashOffset;
        updateBasicStroke();
    }

	public StringParameter getDashArray() {
		return dashArray;
	}

	public void setDashArray(StringParameter dashArray) {
		this.dashArray = dashArray;
	}

    private void updateBasicStroke() {
        try {
            bStroke = createBasicStroke(null);
        } catch (Exception e) {
            this.bStroke = null;
        }
    }

    private BasicStroke createBasicStroke(Feature feat) throws ParameterException {

        int cap;
        if (this.lineCap == null) {
            cap = BasicStroke.CAP_BUTT;
        } else {
            switch (this.lineCap) {
                case BUTT:
                default:
                    cap = BasicStroke.CAP_BUTT;
                    break;
                case ROUND:
                    cap = BasicStroke.CAP_ROUND;
                    break;
                case SQUARE:
                    cap = BasicStroke.CAP_SQUARE;
                    break;
            }
        }

        int join;
        if (this.lineJoin == null) {
            join = BasicStroke.JOIN_ROUND;
        } else {
            switch (this.lineJoin) {
                case MITRE:
                    join = BasicStroke.JOIN_MITER;
                    break;
                case ROUND:
                default:
                    join = BasicStroke.JOIN_ROUND;
                    break;
                case BEVEL:
                    join = BasicStroke.JOIN_BEVEL;
                    break;
            }
        }

        double w = 1.0;

        if (width != null) {
            w = width.getValue(feat);
            // TODO add scale and dpi
            w = Uom.toPixel(w, getUom(), MapEnv.getScaleDenominator());
        }


		if (this.dashArray != null && ! this.dashArray.getValue(feat).isEmpty()){
			System.out.println ("DASH!!!!");
			float dashO = 0.0f;
			float[] dashA;

			String sDash = this.dashArray.getValue(feat);
			System.out.println ("The string version : " + sDash);
			String[] splitedDash = sDash.split(" ");
			dashA = new float[splitedDash.length];
			for (int i = 0;i<splitedDash.length;i++){
            	dashA[i] = (float) Uom.toPixel(Double.parseDouble(splitedDash[i]), getUom(), MapEnv.getScaleDenominator());
				System.out.println ("This is my new dash element " + dashA[i]);
			}

			if (this.dashOffset != null){
				System.out.println ("Offset: " + this.dashOffset);
            	dashO = (float) Uom.toPixel(this.dashOffset.getValue(feat), getUom(), MapEnv.getScaleDenominator());
				System.out.println ("Offset double: " + dashO);
			}
        	return new BasicStroke((float) w, cap, join, 10.0f, dashA, dashO);
		}
		else{
			return new BasicStroke((float) w, cap, join);
		}
    }

    public BasicStroke getBasicStroke(Feature feat) throws ParameterException {
        if (bStroke != null) {
            return bStroke;
        } else {
            return this.createBasicStroke(feat);
        }
    }

    @Override
    public void draw(Graphics2D g2, Shape shp, Feature feat, boolean selected) throws ParameterException, IOException {

        Paint paint = null;
        // remove preGap, postGap from the line
        Shape shape = this.getPreparedShape(shp);

        BasicStroke stroke = null;

        if (this.bStroke == null) {
            stroke = this.createBasicStroke(feat);
        } else {
            stroke = this.bStroke;
        }

        g2.setStroke(stroke);

        if (this.useColor == false) {
            if (stipple != null) {
                paint = stipple.getStipplePainter(feat, selected);
            } else {
                // TOOD Warn Stiple has to be used, but is undefined
            }
        } else {
            Color c;

            if (this.color != null) {
                c = color.getColor(feat);
                if (selected) {
                    c = ColorHelper.invert(c);
                }
            } else {
                // TOOD Warn Color has to be used, but is undefined (Using a random one)
                c = new ColorLiteral().getColor(feat);
            }

            Color ac = c;
            if (this.opacity != null) {
                paint = ColorHelper.getColorWithAlpha(c, this.opacity.getValue(feat));
            }

        }

        if (paint != null) {
            g2.setPaint(paint);
            g2.draw(shape);
        }
    }

    @Override
    public double getMaxWidth(Feature feat) throws ParameterException {
        if (this.width != null) {
            return Uom.toPixel(width.getValue(feat), this.getUom(), MapEnv.getScaleDenominator());
        } else {
            return 0.0;
        }
    }

    @Override
    public JAXBElement<PenStrokeType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createPenStroke(this.getJAXBType());
    }

    public PenStrokeType getJAXBType() {
        PenStrokeType s = new PenStrokeType();

        this.setJAXBProperties(s);

        if (useColor) {
            if (color != null) {
                s.setColor(color.getJAXBParameterValueType());
            }
        } else if (stipple != null) {
            s.setStipple(stipple.getJAXBType());
        }

        if (this.uom != null) {
            s.setUnitOfMeasure(this.uom.toURN());
        }

        if (this.dashArray != null) {
            //s.setDashArray(null);
			s.setDashArray(dashArray.getJAXBParameterValueType());
        }

        if (this.dashOffset != null) {
            s.setDashOffset(this.dashOffset.getJAXBParameterValueType());
        }

        if (this.lineCap != null) {
            s.setLineCap(this.lineCap.getParameterValueType());
        }

        if (this.lineJoin != null) {
            s.setLineJoin(this.lineJoin.getParameterValueType());
        }

        if (this.opacity != null) {
			try {
				if (this.opacity.getValue(null) != 100.0) {
					s.setOpacity(this.opacity.getJAXBParameterValueType());
				}
			} catch (ParameterException ex) {
				s.setOpacity(this.opacity.getJAXBParameterValueType());
			}
        }

        if (this.preGap != null) {
            s.setPreGap(this.preGap.getJAXBParameterValueType());
        }

        if (this.postGap != null) {
            s.setPostGap(this.postGap.getJAXBParameterValueType());
        }

        if (this.width != null) {
            s.setWidth(this.width.getJAXBParameterValueType());
        }

        return s;
    }
    private ColorParameter color;
    private GraphicFill stipple;
    private boolean useColor;
    private RealParameter opacity;
    private RealParameter width;
    private LineJoin lineJoin;
    private LineCap lineCap;
	private StringParameter dashArray;
    private RealParameter dashOffset;
    private BasicStroke bStroke;
}
