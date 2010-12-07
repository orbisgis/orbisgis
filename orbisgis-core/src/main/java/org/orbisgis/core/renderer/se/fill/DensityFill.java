package org.orbisgis.core.renderer.se.fill;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.io.IOException;

import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.DensityFillType;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;

import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.color.ColorHelper;
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
		} else {
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
		if (this.orientation != null){
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
		if (this.percentageCovered != null){
			this.percentageCovered.setContext(RealParameterContext.percentageContext);
		}
	}

	public RealParameter getPercentageCovered() {
		return percentageCovered;
	}

	@Override
	public void draw(Graphics2D g2, Shape shp, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException {
		double percentage = 0.0;

		if (percentageCovered != null) {
			percentage = percentageCovered.getValue(feat);
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
					theta = this.orientation.getValue(feat);
				}

				theta *= Math.PI / 180.0;

				// Stroke width
				double sWidth = hatches.getMaxWidth(feat, mt);

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

				g2.setRenderingHints(mt.getCurrentRenderContext().getRenderingHints());

				Color c = hatches.getColor().getColor(feat);

				if (selected) {
					c = ColorHelper.invert(c);
				}

            	Color ac = c;
	            if (this.hatches.getOpacity() != null) {
    	            ac = ColorHelper.getColorWithAlpha(c, this.hatches.getOpacity().getValue(feat));
        	    }


				tile.setColor(ac);

				tile.setStroke(hatches.getBasicStroke(feat, mt, null));

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
				RenderableGraphics g = mark.getGraphic(feat, selected, mt);

				if (g != null) {
                }
			} else {
				throw new ParameterException("Neither marks or hatches are defined");
			}

			if (painter != null) {
				g2.setPaint(painter);
				g2.fill(shp);
			}
		}
	}

	private double getTextureSize(double markWidth, double markHeight, double percentage) {
		/* Square size depends on the percentage. Lower or equal to 50, the mark
		 * will be drawn 2 times in total (1 full, 4/4 quarters).
		 * Higher then 50, the mark will be drawn 1 time ( 4/4 quarters).
		 */
		double TextureSurface = (markWidth * markHeight * 100) / percentage;
		if (percentage < 50) {
			TextureSurface = (markWidth * markHeight * 2 * 100) / percentage;
		}
		return Math.round(Math.sqrt(TextureSurface));
	}

	@Override
	public boolean dependsOnFeature() {
		if (useHatches()) {
			if (hatches != null && this.hatches.dependsOnFeature()) {
				return true;
			}
			if (orientation != null && this.orientation.dependsOnFeature()) {
				return true;
			}
		} else if (mark != null && this.mark.dependsOnFeature()) {
			return true;
		}

		if (percentageCovered != null && this.percentageCovered.dependsOnFeature()) {
			return true;
		}

		return false;
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
