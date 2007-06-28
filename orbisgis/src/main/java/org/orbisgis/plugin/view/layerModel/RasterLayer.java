package org.orbisgis.plugin.view.layerModel;

import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public class RasterLayer extends BasicLayer {
	private GridCoverage gridCoverage;

	public RasterLayer(String name,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);

		StyleBuilder styleBuilder = new StyleBuilder();
		setStyle(styleBuilder
				.createStyle(styleBuilder.createRasterSymbolizer()));
	}

	public void set(GridCoverage gridCoverage, Style style) throws Exception {
		setGridCoverage(gridCoverage);
		setStyle(style);
	}

	public GridCoverage getGridCoverage() {
		return gridCoverage;
	}

	public void setGridCoverage(GridCoverage gridCoverage) {
		this.gridCoverage = gridCoverage;
	}

	public Envelope getEnvelope() {
		if (null == gridCoverage) {
			return new Envelope();
		} else {
			final org.opengis.spatialschema.geometry.Envelope env = gridCoverage
					.getEnvelope();
			final double[] lowerCorner = env.getLowerCorner().getCoordinates();
			final double[] upperCorner = env.getUpperCorner().getCoordinates();
			return new Envelope(lowerCorner[0], upperCorner[0], lowerCorner[1],
					upperCorner[1]);
		}
	}
}