package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.NullCRS;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.CRSException;
import org.orbisgis.plugin.view.layerModel.VectorLayer;

public class OurFileChooser {
	static class Utils {
		public final static String shp = "shp";

		/*
		 * Get the extension of a file.
		 */
		public static String getExtension(final File file) {
			String ext = null;
			final String fileName = file.getName();
			final int i = fileName.lastIndexOf('.');

			if ((i > 0) && (i < fileName.length() - 1)) {
				ext = fileName.substring(i + 1).toLowerCase();
			}
			return ext;
		}
	}

	public class ShpFilter extends FileFilter {
		// Accept all directories and all shp files.
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			final String extension = Utils.getExtension(f);
			if (extension != null) {
				if (extension.equals(Utils.shp)) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}

		// The description of this filter
		public String getDescription() {
			return "Just shapefiles";
		}
	}

	public OurFileChooser(final Component parent) {
		final JFileChooser fc = new JFileChooser(new File("/tmp"));
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(true);
		fc.addChoosableFileFilter(new ShpFilter());
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showOpenDialog(parent);
		DataSourceFactory dsf = new DataSourceFactory();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] files = fc.getSelectedFiles();
			for (File file : files) {
				System.out.println("=========> " + file.getName());
				VectorLayer vectorLayer = new VectorLayer(file.getName(),
						NullCRS.singleton);
				try {
					DataSource ds = dsf.getDataSource(file);
					SpatialDataSource sds = new SpatialDataSourceDecorator(ds);
					vectorLayer.setDataSource(sds);
					TempPluginServices.lc.put(vectorLayer);
				} catch (DataSourceCreationException ex) {
					ex.printStackTrace();
				} catch (DriverException ex) {
					ex.printStackTrace();
				} catch (CRSException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}