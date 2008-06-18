package org.orbisgis;

import java.awt.image.BufferedImage;

import junit.framework.TestCase;

import org.orbisgis.map.MapTransform;

import com.vividsolutions.jts.geom.Envelope;

public class MapTransformTest extends TestCase {

	private BufferedImage img;
	private Envelope extent;
	private MapTransform mt;

	@Override
	protected void setUp() throws Exception {
		img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		mt = new MapTransform();
		extent = new Envelope(0, 100, 0, 100);
	}

	public void testExtentAndImage() throws Exception {
		mt.setExtent(extent);
		mt.setImage(img);
		assertTrue(mt.getAdjustedExtent().equals(extent));
	}

	public void testImageAndExtent() throws Exception {
		mt.setImage(img);
		mt.setExtent(extent);
		assertTrue(mt.getAdjustedExtent().equals(extent));
	}

}
