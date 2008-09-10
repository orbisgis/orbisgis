package org.orbisgis.map.export;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.gdms.driver.DriverException;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.map.MapTransform;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.renderer.Renderer;
import org.orbisgis.renderer.legend.Legend;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Envelope;

public class DefaultMapExportManager implements MapExportManager {

	@Override
	public void exportSVG(MapContext mapContext, OutputStream outStream,
			int width, int height, Envelope extent)
			throws UnsupportedEncodingException, IOException, DriverException {
		if (!mapContext.isOpen()) {
			throw new IllegalArgumentException(
					"The map must be open to call this method");
		}

		Renderer r = new Renderer();
		DOMImplementation domImpl = GenericDOMImplementation
				.getDOMImplementation();

		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);
		SVGGraphics2D svgg = new SVGGraphics2D(document);

		// Write map image
		MapTransform mt = new MapTransform();
		mt.setExtent(extent);
		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		mt.setImage(img);
		svgg.clipRect(0, 0, width, height);
		r.draw(svgg, width, height, mt.getAdjustedExtent(), mapContext
				.getLayerModel(), new NullProgressMonitor());

		// write legends
		svgg.setClip(null);
		ILayer[] layers = mapContext.getLayerModel().getLayersRecursively();

		svgg.translate(0, 11 * height / 10);
		svgg.drawString("Legends", 0, 0);
		for (int i = 0; i < layers.length; i++) {
			if (layers[i].isVisible()) {
				Legend[] legends = layers[i].getRenderingLegend();
				java.awt.Font font = new java.awt.Font("arial", 0, 12);
				svgg.setFont(font);
				svgg.drawString(layers[i].getName(), 0, 0);
				for (int j = 0; j < legends.length; j++) {
					Legend vectorLegend = legends[j];
					vectorLegend.drawImage(svgg);
					int[] size = vectorLegend.getImageSize(svgg);
					svgg.translate(size[0], 0);
				}
			}
		}

		Writer out = new OutputStreamWriter(outStream, "UTF-8");
		svgg.stream(out);
	}

}
