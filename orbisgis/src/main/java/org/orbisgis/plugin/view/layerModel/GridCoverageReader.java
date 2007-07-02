package org.orbisgis.plugin.view.layerModel;

// ImageJ dependencies

//J2SE dependencies
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.coverage.grid.AbstractGridCoverage2DReader;
import org.geotools.gce.arcgrid.ArcGridFormat;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.image.WorldImageFormat;
import org.geotools.gce.image.WorldImageReader;
import org.opengis.coverage.grid.GridCoverage;

public class GridCoverageReader {
	private GridCoverage gc;

	private static Map<String, String[]> worldFileExtensions;

	static {
		worldFileExtensions = new HashMap<String, String[]>();
		worldFileExtensions.put("tif", new String[] { "tfw" });
		worldFileExtensions.put("tiff", new String[] { "tfw", "tiffw" });
		worldFileExtensions.put("jpg", new String[] { "jpw", "jgw", "jpgw",
				"jpegw" });
		worldFileExtensions.put("jpeg", new String[] { "jpw", "jgw", "jpgw",
				"jpegw" });
		worldFileExtensions.put("gif", new String[] { "gfw", "gifw" });
		worldFileExtensions.put("bmp", new String[] { "bmw", "bmpw" });
		worldFileExtensions.put("png", new String[] { "pgw", "pngw" });
	}

	public GridCoverageReader(final File dataSource) throws IOException {
		this(dataSource.getAbsolutePath());
	}

	public GridCoverageReader(final String fileName) throws IOException {
		final int dotIndex = fileName.lastIndexOf('.');
		final String fileNamePrefix = fileName.substring(0, dotIndex);
		final String fileNameExtension = fileName.substring(dotIndex + 1);

		File file = new File(fileName);
		AbstractGridCoverage2DReader rdr;

		if (fileNameExtension.toLowerCase().endsWith("asc")) {
			rdr = (ArcGridReader) ((new ArcGridFormat()).getReader(file));
			gc = rdr.read(null);
		} else if ((fileNameExtension.toLowerCase().endsWith("tif") || fileNameExtension
				.toLowerCase().endsWith("tiff"))
				&& (!isThereAnyWorldFile(fileNamePrefix, fileNameExtension))) {
			rdr = (GeoTiffReader) ((new GeoTiffFormat()).getReader(file));
		} else {
			rdr = (WorldImageReader) ((new WorldImageFormat()).getReader(file));
		}
		gc = rdr.read(null);
	}

	private boolean isThereAnyWorldFile(final String fileNamePrefix,
			final String fileNameExtension) throws IOException {
		File worldFile = null;

		for (String extension : worldFileExtensions.get(fileNameExtension
				.toLowerCase())) {
			if (new File(fileNamePrefix + "." + extension).exists()) {
				worldFile = new File(fileNamePrefix + "." + extension);
			} else if (new File(fileNamePrefix + "." + extension.toUpperCase())
					.exists()) {
				worldFile = new File(fileNamePrefix + "."
						+ extension.toUpperCase());
			}
		}
		return (null == worldFile) ? false : true;
	}

	public GridCoverage getGc() {
		return gc;
	}
}