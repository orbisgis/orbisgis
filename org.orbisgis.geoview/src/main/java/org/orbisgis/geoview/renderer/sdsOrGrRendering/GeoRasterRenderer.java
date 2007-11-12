package org.orbisgis.geoview.renderer.sdsOrGrRendering;

//import ij.LookUpTable;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.Style;

import com.vividsolutions.jts.geom.Envelope;

public class GeoRasterRenderer {
	private static final double RATIO_VALUE = 0.02;

	private Image imageScaled;

	private double scaleCached;

	private MapControl mapControl;

	public GeoRasterRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	// public void paint(final Graphics2D graphics, final GeoRaster geoRaster,
	// final Style style) {
	// final Image image = geoRaster.getImagePlus().getImage();
	// final Envelope mapEnvelope = mapControl.fromGeographicToMap(geoRaster
	// .getMetadata().getEnvelope());
	//
	// Runtime.getRuntime().freeMemory();
	//
	// // LookupOp lookupOp = new LookupOp(new LookupTable(image),null);
	// // BufferedImageFilter bif = new BufferedImageFilter(lookupOp);
	//
	// graphics.setComposite(AlphaComposite.SrcOver);
	// graphics.drawImage(image, (int) mapEnvelope.getMinX(),
	// (int) mapEnvelope.getMinY(), (int) mapEnvelope.getWidth(),
	// (int) mapEnvelope.getHeight(), null);
	// }

	public void paint(final Graphics2D graphics, final GeoRaster geoRaster,
			final Envelope mapEnvelope, final Style style) throws IOException,
			GeoreferencingException {
		graphics.setComposite(AlphaComposite.SrcOver);
		graphics.drawImage(geoRaster.getGrapImagePlus().getImage(),
				(int) mapEnvelope.getMinX(), (int) mapEnvelope.getMinY(),
				(int) mapEnvelope.getWidth(), (int) mapEnvelope.getHeight(),
				null);
	}
}