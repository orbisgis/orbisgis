package org.orbisgis.geocatalog.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.file.FileSourceDefinition;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.INewResource;
import org.orbisgis.geocatalog.resources.AbstractGdmsSource;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.ResourceFactory;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;

public class ConvertResources implements INewResource {

	public IResource[] getResources() {

		ArrayList<IResource> resources = new ArrayList<IResource>();

		ConvertXYZDEMWizard convertXYZDEMWizard = new ConvertXYZDEMWizard();
		boolean ok = UIFactory.showDialog(convertXYZDEMWizard
				.getWizardPanels());

		if (ok) {

			File infile = convertXYZDEMWizard.getSelectedInFiles();

			File outfile = convertXYZDEMWizard.getSelectedOutFiles();

			GeoRaster geoRaster;
			try {
				geoRaster = GeoRasterFactory.createGeoRaster(infile
						.getAbsolutePath());
				geoRaster.open();
				geoRaster.save(outfile.getAbsolutePath());

				String name = OrbisgisCore.registerInDSF(outfile.getName(),
						new FileSourceDefinition(outfile));
				resources.add(ResourceFactory.createResource(name,
						new AbstractGdmsSource()));

			} catch (FileNotFoundException e) {
				PluginManager.error("File not found", e);
			} catch (IOException e) {
				PluginManager.error("Cannot convert the file", e);
				e.printStackTrace();
			} catch (GeoreferencingException e) {
				PluginManager.error("Invalid spatial metadata", e);

			}

		}

		return resources.toArray(new IResource[0]);
	}

	public String getName() {

		return "Convert XYZ DEM ";
	}

}
