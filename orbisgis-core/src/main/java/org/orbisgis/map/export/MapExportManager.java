package org.orbisgis.map.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.gdms.driver.DriverException;
import org.orbisgis.layerModel.MapContext;

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

}
