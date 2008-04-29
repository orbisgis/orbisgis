package org.gdms.driver.geotif;

import java.io.File;

import org.gdms.data.types.TypeDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.source.SourceManager;

public class TifDriver extends AbstractRasterDriver implements FileReadWriteDriver {

	public String getName() {
		return "tif with world file driver";
	}

	public int getType() {
		return SourceManager.TFW;
	}

	public String completeFileName(String fileName) {
		if (!fileName.toLowerCase().endsWith(".tif")) {
			return fileName + ".tif";
		} else {
			return fileName;
		}
	}

	public boolean fileAccepted(File f) {
		String upperName = f.getName().toUpperCase();
		return upperName.endsWith(".TIF") || upperName.endsWith(".TIFF");
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		return null;
	}

	public boolean isCommitable() {
		return true;
	}

}
