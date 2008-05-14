package org.orbisgis.rasterProcessing.tin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class PolyReader extends AbstractReader {
	private Scanner in = null;
	private List<Vertex> listOfVertices;

	PolyReader(final File file, final List<Vertex> listOfVertices)
			throws FileNotFoundException {
		if (file.exists() && file.canRead()) {
			in = new Scanner(file);
			in.useLocale(Locale.US); // essential to read float values
		}
		this.listOfVertices = listOfVertices;
	}

	@Override
	Scanner getIn() {
		return in;
	}

	void read() {
		// TODO ?
	}
}