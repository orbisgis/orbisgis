package org.orbisgis.map.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.gdms.driver.DriverException;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

public interface MapExportManager {

	/**
	 * Exports the specified mapContext to svg format. The mapContext must be
	 * open
	 * 
	 * @param mapContext
	 *            MapContext used to draw. Invisible layers won't be drawn
	 * @param outStream
	 *            Output stream to export to
	 * @param width
	 *            width of the map image to export
	 * @param height
	 *            height of the map image to export
	 * @param extent
	 *            extent of the map to export
	 * @throws UnsupportedEncodingException
	 *             If the UTF-8 encoding is not supported
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             If the specified mapContext is not open
	 * @throws DriverException
	 *             If there is an error accessing any of the layers in the map
	 */
	void exportSVG(MapContext mapContext, OutputStream outStream, int width,
			int height, Envelope extent) throws UnsupportedEncodingException,
			IOException, IllegalArgumentException, DriverException;

	/**
	 * @see #exportSVG(MapContext, OutputStream, int, int, Envelope)
	 */
	void exportSVG(MapContext mapContext, OutputStream outStream, int width,
			int height, Envelope extent, IProgressMonitor pm)
			throws UnsupportedEncodingException, IOException,
			IllegalArgumentException, DriverException;

	/**
	 * Adds a new scale to the export manager
	 * 
	 * @param scaleClass
	 */
	void registerScale(Class<? extends Scale> scaleClass);

	/**
	 * Get the names of all registered scales
	 * 
	 * @return
	 */
	String[] getScaleNames();

	/**
	 * Get an instance of the specified instance
	 * 
	 * @param name
	 * @return The scale instance or null if there is no such scale
	 */
	Scale getScale(String name);
}
