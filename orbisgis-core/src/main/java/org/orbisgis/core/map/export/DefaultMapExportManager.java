package org.orbisgis.core.map.export;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Envelope;

public class DefaultMapExportManager implements MapExportManager {

	private ArrayList<Class<? extends Scale>> scales = new ArrayList<Class<? extends Scale>>();

	@Override
	public void exportSVG(MapContext mapContext, OutputStream outStream,
			double width, double height, Envelope extent, Scale scale, int dpi,
			IProgressMonitor pm) throws UnsupportedEncodingException,
			IOException, DriverException {

		// Calculate the number of pixels to have the desired dpi when the image
		// has the specified size
		double dpcm = 0.01 * dpi / 0.0254;
		int pixelWidth = (int) (width * dpcm);
		int pixelHeight = (int) (height * dpcm);

		if (!mapContext.isOpen()) {
			throw new IllegalArgumentException(
					"The map must be open to call this method");
		}

		pm.startTask("Drawing map");
		Renderer r = new Renderer();
		DOMImplementation domImpl = GenericDOMImplementation
				.getDOMImplementation();

		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);
		SVGGraphics2D svgg = new SVGGraphics2D(document);

		// Write map image
		MapTransform mt = new MapTransform();
		mt.setExtent(extent);
		BufferedImage bi = new BufferedImage(pixelWidth, pixelHeight,
				BufferedImage.TYPE_INT_ARGB);
		mt.setImage(bi);
		r.draw(bi.createGraphics(), pixelWidth, pixelHeight, mt
				.getAdjustedExtent(), mapContext.getLayerModel(), pm);

		double batikDefaultDPcm = 0.01 * 90 / 0.0254;
		int svgWidth = (int) (width * batikDefaultDPcm);
		int svgHeight = (int) (height * batikDefaultDPcm);
		svgg.drawImage(bi, 0, 0, svgWidth, svgHeight, null);

		// write legends
		svgg.setClip(null);
		ILayer[] layers = mapContext.getLayerModel().getLayersRecursively();

		svgg.translate(0, svgHeight + 50);
		int maxHeight = 0;
		for (int i = 0; i < layers.length; i++) {
			svgg.translate(0, maxHeight + 30);
			maxHeight = 0;
			AffineTransform original = svgg.getTransform();
			if (layers[i].isVisible()) {
				Legend[] legends = layers[i].getRenderingLegend();
				java.awt.Font font = new java.awt.Font("arial", 0, 12);
				svgg.setFont(font);
				svgg.drawString(layers[i].getName(), 0, 0);
				for (int j = 0; j < legends.length; j++) {
					Legend vectorLegend = legends[j];
					vectorLegend.drawImage(svgg);
					int[] size = vectorLegend.getImageSize(svgg);
					if (size[1] > maxHeight) {
						maxHeight = size[1];
					}
					svgg.translate(size[0] + 30, 0);
				}
			}
			svgg.setTransform(original);
		}

		// draw scale
		if (scale != null) {
			scale.drawScale(svgg, 90);
		}
		pm.endTask();

		pm.startTask("writting result");
		Writer out = new OutputStreamWriter(outStream, "UTF-8");
		svgg.stream(out);
		pm.endTask();
	}

	@Override
	public void exportSVG(MapContext mc, OutputStream outputStream,
			double width, double height, Envelope envelope, Scale scale, int dpi)
			throws UnsupportedEncodingException, IOException,
			IllegalArgumentException, DriverException {
		exportSVG(mc, outputStream, width, height, envelope, scale, dpi,
				new NullProgressMonitor());
	}

	@Override
	public void registerScale(Class<? extends Scale> scaleClass) {
		try {
			scaleClass.newInstance();
			scales.add(scaleClass);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(
					"Cannot obtain a scale instance", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(
					"Cannot obtain a scale instance", e);
		}
	}

	@Override
	public Scale[] getScales() {
		ArrayList<Scale> ret = new ArrayList<Scale>();
		Iterator<Class<? extends Scale>> it = scales.iterator();
		while (it.hasNext()) {
			Class<? extends Scale> scaleClass = it.next();
			try {
				ret.add(scaleClass.newInstance());
			} catch (InstantiationException e) {
				throw new IllegalArgumentException(
						"Cannot obtain a scale instance", e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(
						"Cannot obtain a scale instance", e);
			}
		}

		return ret.toArray(new Scale[0]);
	}
}
