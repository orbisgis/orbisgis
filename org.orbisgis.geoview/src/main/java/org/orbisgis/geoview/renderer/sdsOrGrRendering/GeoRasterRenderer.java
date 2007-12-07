package org.orbisgis.geoview.renderer.sdsOrGrRendering;

//import ij.LookUpTable;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.orbisgis.geoview.renderer.style.Style;

import com.vividsolutions.jts.geom.Envelope;

public class GeoRasterRenderer {

	public GeoRasterRenderer() {
	}

	public void paint(final Graphics2D graphics, final GeoRaster geoRaster,
			final Envelope layerPixelEnvelope, final Style style)
			throws IOException, GeoreferencingException {
		graphics.setComposite(AlphaComposite.SrcOver);
		graphics.drawImage(geoRaster.getGrapImagePlus().getImage(),
				(int) layerPixelEnvelope.getMinX(), (int) layerPixelEnvelope
						.getMinY(), (int) layerPixelEnvelope.getWidth(),
				(int) layerPixelEnvelope.getHeight(), null);
	}
}