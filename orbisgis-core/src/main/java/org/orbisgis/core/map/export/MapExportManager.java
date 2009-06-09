package org.orbisgis.core.map.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

public interface MapExportManager {

	/**
	 * Exports the specified mapContext to svg format. The mapContext must be
	 * open. It creates an image of the map and vectorial representations of the
	 * legends in each of the layers, along with a graphical representation of
	 * the specified scale
	 * 
	 * @param mapContext
	 *            MapContext used to draw. Invisible layers won't be drawn
	 * @param outStream
	 *            Output stream to export to
	 * @param width
	 *            width of the map image to export in centimeters
	 * @param height
	 *            height of the map image to export in centimeters
	 * @param extent
	 *            extent of the map to export
	 * @param scale
	 *            Scale to draw
	 * @param mapDpi
	 *            Dots per inch of the map image
	 * @throws UnsupportedEncodingException
	 *             If the UTF-8 encoding is not supported
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             If the specified mapContext is not open
	 * @throws DriverException
	 *             If there is an error accessing any of the layers in the map
	 */
	void exportSVG(MapContext mapContext, OutputStream outStream, double width,
			double height, Envelope extent, Scale scale, int mapDpi)
			throws UnsupportedEncodingException, IOException,
			IllegalArgumentException, DriverException;

	/**
	 * @see #exportSVG(MapContext, OutputStream, int, int, Envelope)
	 */
	void exportSVG(MapContext mapContext, OutputStream outStream, double width,
			double height, Envelope extent, Scale scale, int mapDpi,
			IProgressMonitor pm) throws UnsupportedEncodingException,
			IOException, IllegalArgumentException, DriverException;

	/**
	 * Adds a new scale to the export manager
	 * 
	 * @param scaleClass
	 */
	void registerScale(Class<? extends Scale> scaleClass);

	/**
	 * Get an array of the instances of all registered Scales
	 * 
	 * @return
	 */
	Scale[] getScales();

}