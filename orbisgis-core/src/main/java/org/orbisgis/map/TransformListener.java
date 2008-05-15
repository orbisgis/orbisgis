package org.orbisgis.map;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Interface to listen for changes in the MapTransform object
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface TransformListener {

	/**
	 * the extent of the data to see has changed
	 *
	 * @param oldExtent
	 * @param mapTransform
	 */
	void extentChanged(Envelope oldExtent, MapTransform mapTransform);

	/**
	 * The size of the image to visualize the data has changed
	 *
	 * @param oldWidth
	 * @param oldHeight
	 * @param mapTransform
	 */
	void imageSizeChanged(int oldWidth, int oldHeight, MapTransform mapTransform);

}
