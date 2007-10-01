package org.orbisgis.plugin.view.ui.workbench;

import ij.process.ImageProcessor;

import org.gdms.data.DataSource;
import org.orbisgis.plugin.renderer.style.Style;

import com.vividsolutions.jts.geom.Envelope;

public class LayerStackEntry {
	private DataSource dataSource;

	private ImageProcessor imageProcessor;

	private Style style;

	private Envelope mapEnvelope;

	public LayerStackEntry(final DataSource dataSource, final Style style) {
		this.dataSource = dataSource;
		this.style = style;
	}

	public LayerStackEntry(final ImageProcessor imageProcessor,
			final Style style, final Envelope mapEnvelope) {
		this.imageProcessor = imageProcessor;
		this.style = style;
		this.mapEnvelope = mapEnvelope;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public ImageProcessor getImageProcessor() {
		return imageProcessor;
	}

	public Style getStyle() {
		return style;
	}

	public Envelope getMapEnvelope() {
		return mapEnvelope;
	}
}