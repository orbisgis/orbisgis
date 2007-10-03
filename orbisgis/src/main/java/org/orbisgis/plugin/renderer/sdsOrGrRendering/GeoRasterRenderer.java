package org.orbisgis.plugin.renderer.sdsOrGrRendering;

//import ij.LookUpTable;

import ij.process.ImageProcessor;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;

import org.orbisgis.plugin.renderer.style.Style;
import org.orbisgis.plugin.view.ui.workbench.MapControl;

import com.vividsolutions.jts.geom.Envelope;

public class GeoRasterRenderer {
	private static final double RATIO_VALUE = 0.02;

	private Image imageScaled;

	private double scaleCached;

	private MapControl mapControl;

	public GeoRasterRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public void paint(final Graphics2D graphics,
			final ImageProcessor rescaledImageProcessor,
			final Envelope mapEnvelope, final Style style) {
		graphics.setComposite(AlphaComposite.SrcOver);
		graphics.drawImage(rescaledImageProcessor.createImage(),
				(int) mapEnvelope.getMinX(), (int) mapEnvelope.getMinY(), null);
	}
}