package org.orbisgis.geoview.rasterProcessing.tin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.gdms.driver.DriverException;

public class PolyReader {
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

	private String nextThatIsNotAComment() throws DriverException {
		while (in.hasNext()) {
			final String tmp = in.next();
			if (tmp.startsWith("#")) {
				in.nextLine();
			} else {
				return tmp;
			}
		}
		throw new DriverException("NodeReader: format failure - i miss a token");
	}

	private int nextInteger() throws DriverException {
		return new Integer(nextThatIsNotAComment());
	}

	private double nextDouble() throws DriverException {
		return new Double(nextThatIsNotAComment());
	}

	void read() {

	}

	void close() {
		if (null != in) {
			in.close();
		}
	}
}