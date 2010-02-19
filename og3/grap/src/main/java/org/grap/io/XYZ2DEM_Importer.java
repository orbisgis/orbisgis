/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.io;

/**
 * XYZ2DEM_Importer Version 1.00, 2005-02-18, by Martin Schlueter, i3mainz,
 * Mainz University of Applied Sciences, Germany Barry Joe, ZCS Inc., Calgary,
 * AB, Canada (contributor of Geompack)
 *
 * This ImageJ plugin imports X,Y,Z coordinates of (usually irregularly
 * distributed) points from the first 3 columns of a plain text file and
 * interpolates a Digital Elevation Model (DEM) image or Digital Terrain Model
 * (DTM) image.
 *
 * The given points are projected to the X,Y-plane and are meshed by a
 * 2D-Delaunay triangulation. For each image pixel position, a signed 32-bit
 * floating-point pixel value Z=Z(X,Y) is calculated by linear interpolation
 * within the corresponding triangle. Pixel positions outside the convex hull
 * get a user chosen background value. The detection of occluded surface areas
 * is not supported. It is recommended to use the TIFF file format for 32-bit
 * images.
 *
 * To learn more please refer to the related web pages with examples and
 * applications.
 *
 * For consultance and practical applications concerning Mobile 3D Coordinate
 * Measuring Techniques, 3D Digitizing, Deformation Measurement and Analysis
 * please contact Martin Schlueter: xyz2dem@geoinform.fh-mainz.de or visit
 * http://www.i3mainz.fh-mainz.de/institut/personal/schlueter/e_index.html
 *
 * For more information on Geompack++, a comprehensive object-oriented C++
 * software package for finite element mesh generation (triangular,
 * quadrilateral, surface, tetrahedral, hexahedral-dominant), please see
 * www.allstream.net/~bjoe/index.htm
 *
 * Copyright (c) [2004-09-30] by Prof. Dr.-Ing. Martin Schlueter, i3mainz, Mainz
 * University of Applied Sciences, Germany with exception to the meshing methods
 * labeled below with Copyright (c) by Dr. Barry Joe, ZCS Inc., Calgary, AB,
 * Canada
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions: The above copyright
 * notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * OSI Certified Open Source Software
 */

import ij.IJ;
import ij.process.FloatProcessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.text.DecimalFormat;
import java.util.Vector;

import org.grap.model.RasterMetadata;

/**
 * description
 *
 * @author bocher
 *
 * Adapted for GRAP
 */
public class XYZ2DEM_Importer {

	String directory, filename, path;

	private double myBackground = 0.0;
	private double myMaximumEdgeLength = 0.0;
	private double myPixelSize = 1.0;
	private boolean myShowLimitsDecision = false;

	private int numberOfPoints = 0;
	private int numberOfTriangles = 0;
	private int numberOfPointDoublets = 0;
	private int numberOfEliminatedPoints = 0;

	private double[] boundaryOfXYZCoordSysXYZ = new double[6]; // Actual
	// boundary
	// values in X,
	// Y and Z
	// (coordinate
	// system: XYZ)
	private double[] boundaryOfImageCoordSysXYZ = new double[4]; // Boundary
	// of target
	// image
	// (centers
	// of lower
	// left and
	// upper
	// right
	// pixel,
	// coordinate
	// system:
	// XYZ)
	private double[] boundaryOfXYZCoordSysImage = new double[4]; // Actual
	// boundary
	// (coordinate
	// system:
	// image
	// pixels)
	private int[] boundaryOfImageCoordSysImage = new int[4]; // Boundary of
	// target image
	// (coordinate
	// system: image
	// pixels)

	FloatProcessor ip;

	RasterMetadata rastermetadata;

	public XYZ2DEM_Importer(String path) {
		this.path = path;
		read(path);
	}

	public XYZ2DEM_Importer(String path, float myPixelSize,
			boolean myShowLimitsDecision) {
		this.path = path;
		this.myPixelSize = myPixelSize;
		this.myShowLimitsDecision = myShowLimitsDecision;
		read(path);
	}

	public void read(String path) {

		if (IJ.versionLessThan("1.31a"))
			return;

		double tol = 1.e-20;
		tol = initializeTolerance(tol); // Approximate machine-epsilon

		// Read XYZ coordinates from text file
		Vector coordinateList = new Vector();

		try {
			Reader r = new BufferedReader(new InputStreamReader(
					new FileInputStream(path)));
			parseCoordinatesFromTextfile(r, coordinateList);
			IJ.showStatus("XYZ_Import " + Integer.toString(numberOfPoints)
					+ " points");

			r.close();
			// IJ.showStatus("XYZ_Import input: " + path);
		} catch (IOException e) {
			String msg = e.getMessage();
			if (msg == null || msg.equals(""))
				msg = "" + e;
			IJ.showMessage("XYZ_Import", msg);
		}

		// Initialize coordinate arrays:
		double[] xVcl = new double[(numberOfPoints + 4)]; // X coordinates
		// (input) (xVcl[0]
		// is not used)
		double[] yVcl = new double[(numberOfPoints + 4)]; // Y coordinates
		// (input) (yVcl[0]
		// is not used)
		double[] zVcl = new double[(numberOfPoints + 4)]; // Z coordinates
		// (input) (zVcl[0]
		// is not used)
		copyCoordinatesToArrays(coordinateList, xVcl, yVcl, zVcl);
		coordinateList.removeAllElements();

		// Initialize arrays for the triangulation:
		int[] ind = new int[numberOfPoints + 4]; // Index of sorted X/Y
		// coordinates (ind[0] is
		// not used)
		int[][] til = new int[3][2 * (numberOfPoints + 4) + 2]; // List of
		// triangles
		// (til[][0] is
		// not used)
		int[][] tnbr = new int[3][2 * (numberOfPoints + 4) + 2]; // List of
		// neighbour
		// triangles
		// (tnbr[][0]
		// is not
		// used)
		int[] stack = new int[2 * (numberOfPoints + 4) + 2]; // Stack of
		// triangles for
		// which
		// circumcircle
		// test must be
		// made
		// (stack[0] is
		// not used)
		// Later used to mark triangles to be disregarded (myMaximumEdgeLength)

		// Initialize index, sort points, eliminate point doublets (in X and Y):
		sortAndCheckCoordinates(xVcl, yVcl, zVcl, ind, stack, tol);
		if (numberOfPoints < 3) {
			IJ.showMessage("XYZ_Import",
					"ERROR: Number of points is less than three!");
			IJ.showStatus("");
			return;
		}

		userinput();

		// Transform coordinates and perform Delaunay-Triangulation:
		IJ.showStatus("XYZ_Import builds up mesh");
		IJ.showProgress(0.0);
		setImageBoundary(tol);
		transformCoordinates(xVcl, yVcl);
		if (myShowLimitsDecision)
			userShowLimits(); // Write boundary information to text file

		int ierr = 0;
		ierr = delaunayTriangulation(xVcl, yVcl, ind, til, tnbr, stack, tol);
		if (ierr == 8) {
			IJ
					.showMessage(
							"XYZ_Import",
							"ERROR: Not enough space in 'stack' array.\n \n"
									+ "(This error should not occur. Please give feedback to the author.)");
			return;
		} else if (ierr == 225) {
			IJ
					.showMessage("XYZ_Import",
							"ERROR: All points are collinear (in floating point arithmetic).");
			return;
		}

		// Initialize resulting image:
		IJ.showStatus("XYZ_Import generates 32-bit image");
		IJ.showProgress(0.0);

		int za = boundaryOfImageCoordSysImage[2] + 1;
		int sa = boundaryOfImageCoordSysImage[1] + 1;

		rastermetadata = new RasterMetadata(boundaryOfXYZCoordSysXYZ[0],
				boundaryOfXYZCoordSysXYZ[3], (float) myPixelSize,
				(float) -myPixelSize, boundaryOfImageCoordSysImage[1],
				boundaryOfImageCoordSysImage[2], (float) myBackground);

		System.out.println(rastermetadata.getEnvelope());

		ip = new FloatProcessor(sa, za);
		float[] myPixelValues = (float[]) ip.getPixels();

		// Perform image interpolation (linear interpolation within a triangle):
		interpolateImageLinear(til, tnbr, xVcl, yVcl, zVcl, stack,
				myPixelValues, tol);

		// Adjust brightness and contrast:
		ip.resetMinAndMax();

	}

	/**
	 * File Parser
	 */

	private void parseCoordinatesFromTextfile(Reader r, Vector coordinateList)
			throws IOException {
		StreamTokenizer tok = new StreamTokenizer(r);
		int coordinatesPerLine = 0;
		double xcoord = 0.0;
		double ycoord = 0.0;
		double zcoord = 0.0;

		// Prepare StreamTokenizer:
		// http://java.sun.com/j2se/1.4.2/docs/api/java/io/StreamTokenizer.html
		tok.resetSyntax();
		tok.wordChars('0', '9'); // java.io.StreamTokenizer fails to parse
		tok.wordChars('e', 'e'); // scientific notation like "1.09E-008".
		tok.wordChars('E', 'E'); // The solution is to read and parse
		tok.wordChars('.', '.'); // coordinates as "words".
		tok.wordChars('+', '+'); // You run into trouble if the input file
		tok.wordChars('-', '-'); // contains text with "e" or "E" which is
		tok.whitespaceChars(0, ' '); // not declared to be a comment.
		tok.whitespaceChars(',', ',');
		tok.whitespaceChars(';', ';');
		tok.commentChar(35); // Character '#' starts a single line comment
		tok.slashSlashComments(true); // Character '//' start a comment
		tok.slashStarComments(true); // Character '/*' starts, '*/' ends a
		// comment
		tok.eolIsSignificant(true);

		while (tok.nextToken() != StreamTokenizer.TT_EOF) {
			switch (tok.ttype) {
			case StreamTokenizer.TT_EOL:
				if (coordinatesPerLine >= 3) {
					coordinateList.addElement(new Double(xcoord)); // Collect
					// coordinates
					// in
					// Vector.
					coordinateList.addElement(new Double(ycoord)); // Since
					// Vector
					// only
					// accepts
					// Objects
					coordinateList.addElement(new Double(zcoord)); // a wrapper
					// class is
					// used.
					numberOfPoints++;

					if (numberOfPoints % 1000 == 0)
						IJ.showStatus("XYZ_Import "
								+ Integer.toString(numberOfPoints) + " points");
				}
				coordinatesPerLine = 0;
				break;
			case StreamTokenizer.TT_WORD:
				String coordinateString = tok.sval;
				coordinatesPerLine++;
				if (coordinateString != null) {
					switch (coordinatesPerLine) {
					// Double.valueOf(String s) translates even "2.0e-3" to
					// 0.002:
					case 1:
						xcoord = Double.valueOf(coordinateString).doubleValue();
						break;
					case 2:
						ycoord = Double.valueOf(coordinateString).doubleValue();
						break;
					case 3:
						zcoord = Double.valueOf(coordinateString).doubleValue();
						break;
					}
				}
				break;
			}
		}
	}

	/**
	 * User interaction, ask for: myPixelSize, myMaximumEdgeLength,
	 * myBackground, myShowLimitsDecision
	 */

	private int userinput() {
		// myPixelSize = Math
		// .max(
		// (boundaryOfXYZCoordSysXYZ[1] - boundaryOfXYZCoordSysXYZ[0]) / 1000.0,
		// (boundaryOfXYZCoordSysXYZ[3] - boundaryOfXYZCoordSysXYZ[2]) /
		// 1000.0); // ...
		// just
		// a
		// guess
		// ...
		myBackground = boundaryOfXYZCoordSysXYZ[4]; // The minimum of Z might be
		// a good choice for
		// myBackground ...

		// GenericDialog gd = new GenericDialog("Import " + filename
		// + " to DEM image");
		// gd
		// .addMessage("The size of a pixel determines the resolution of the
		// resulting image. \n"
		// + "The length of one pixel has to be given in units of your XYZ-data.
		// \n");
		// gd.addNumericField("Length of one pixel:", myPixelSize, 2);
		// gd
		// .addMessage("XYZ_Import builds up a triangular mesh for the
		// resampling. If your XYZ-data contains \n"
		// + "holes or a not-convex boundary you may want to suppress the
		// visualization of very large \n"
		// + "triangles by giving a maximum edge length between two points (in
		// units of your XYZ-data). \n"
		// + "The default is 0.0 - which means NO maximum length is assumed -
		// ('show all!)'.");
		// gd.addNumericField("Maximum edge length:", myMaximumEdgeLength, 2);
		// gd.addMessage("Areas without data are filled with the \n");
		// gd.addNumericField("Background value:", myBackground, 2);
		// gd.addCheckbox("Show protocol and coordinate limits in X and Y.",
		// myShowLimitsDecision);
		// gd.addMessage("Number of accepted points: " + numberOfPoints + "\n"
		// + "Number of eliminated points (doublets in X/Y): "
		// + numberOfEliminatedPoints);
		//
		// gd.showDialog();
		// myPixelSize = gd.getNextNumber();
		// myMaximumEdgeLength = gd.getNextNumber();
		// myBackground = gd.getNextNumber();
		// myShowLimitsDecision = gd.getNextBoolean();
		//
		// if (gd.wasCanceled()) {
		// return 0;
		// } else if (gd.invalidNumber()) {
		// IJ.showMessage("XYZ_Import",
		// "Input Error - Please enter valid numbers!");
		// return -1;
		// } else {
		return 1;
		// }
	}

	/**
	 * Output (text) of boundary values
	 */

	private void userShowLimits() {

		DecimalFormat df = new DecimalFormat("0.0000000000");

		IJ.log("________________________________________________ " + "\n"
				+ "File                                             "
				+ path
				+ "\n"
				+ "Chosen length of one pixel:                      "
				+ myPixelSize
				+ "\n"
				+ "Chosen maximum edge length:                      "
				+ myMaximumEdgeLength
				+ "\n"
				+ "Chosen background value:                         "
				+ myBackground
				+ "\n"
				+ "Number of accepted points:                       "
				+ numberOfPoints
				+ "\n"
				+ "Number of eliminated points (doublets in X/Y):   "
				+ numberOfEliminatedPoints
				+ "\n"
				+ "________________________________________________ "
				+ "\n"
				+ "Boundary of input data                           "
				+ "\n"
				+ "in XYZ coordinates:                              "
				+ "\n"
				+
				// "Xmin (input XYZ) = " +
				// df.format(boundaryOfXYZCoordSysXYZ[0]) + "\n" +
				"Xmin (input XYZ)                               = "
				+ (boundaryOfXYZCoordSysXYZ[0])
				+ "\n"
				+ "Xmax (input XYZ)                               = "
				+ (boundaryOfXYZCoordSysXYZ[1])
				+ "\n"
				+ "Ymin (input XYZ)                               = "
				+ (boundaryOfXYZCoordSysXYZ[2])
				+ "\n"
				+ "Ymax (input XYZ)                               = "
				+ (boundaryOfXYZCoordSysXYZ[3])
				+ "\n"
				+ "                                                 "
				+ "\n"
				+ "Boundary of input data                           "
				+ "\n"
				+ "in image pixel coordinates:                      "
				+ "\n"
				+ "Xmin (input XYZ) corresponds to column         = "
				+ (boundaryOfXYZCoordSysImage[0])
				+ "\n"
				+ "Xmax (input XYZ) corresponds to column         = "
				+ (boundaryOfXYZCoordSysImage[1])
				+ "\n"
				+ "Ymin (input XYZ) corresponds to row            = "
				+ (boundaryOfXYZCoordSysImage[2])
				+ "\n"
				+ "Ymax (input XYZ) corresponds to row            = "
				+ (boundaryOfXYZCoordSysImage[3])
				+ "\n"
				+ "________________________________________________ "
				+ "\n"
				+ "Boundary of target image (coordinates of         "
				+ "\n"
				+ "pixel centers) in XYZ coordinates:               "
				+ "\n"
				+ "Xmin (left)                                    = "
				+ (boundaryOfImageCoordSysXYZ[0])
				+ "\n"
				+ "Xmax (right)                                   = "
				+ (boundaryOfImageCoordSysXYZ[1])
				+ "\n"
				+ "Ymin (low)                                     = "
				+ (boundaryOfImageCoordSysXYZ[2])
				+ "\n"
				+ "Ymax (up)                                      = "
				+ (boundaryOfImageCoordSysXYZ[3])
				+ "\n"
				+ "                                                 "
				+ "\n"
				+ "Boundary of target image (coordinates of         "
				+ "\n"
				+ "pixel centers) in image pixel coordinates:       "
				+ "\n"
				+ "Xmin (left)  corresponds to column             = "
				+ (boundaryOfImageCoordSysImage[0])
				+ "\n"
				+ "Xmax (right) corresponds to column             = "
				+ (boundaryOfImageCoordSysImage[1])
				+ "\n"
				+ "Ymin (low)   corresponds to row                = "
				+ (boundaryOfImageCoordSysImage[2])
				+ "\n"
				+ "Ymax (up)    corresponds to row                = "
				+ (boundaryOfImageCoordSysImage[3]));

		return;
	}

	/**
	 * Copy coordinates from vector to array
	 */

	private void copyCoordinatesToArrays(Vector coordinateList, double[] xVcl,
			double[] yVcl, double[] zVcl) {
		int k = 0;
		Double coordinateTmp;
		for (int i = 0; i < numberOfPoints; i++) {
			// index of *vcl runs 1, ..., numberOfPoints!!!
			coordinateTmp = (Double) coordinateList.elementAt(k);
			xVcl[i + 1] = coordinateTmp.doubleValue();
			k++;
			coordinateTmp = (Double) coordinateList.elementAt(k);
			yVcl[i + 1] = coordinateTmp.doubleValue();
			k++;
			coordinateTmp = (Double) coordinateList.elementAt(k);
			zVcl[i + 1] = coordinateTmp.doubleValue();
			k++;
		}
	}

	/**
	 * Initialize variables, sort points, find minima and maxima, check for
	 * identical points
	 */

	private void sortAndCheckCoordinates(double[] xVcl, double[] yVcl,
			double[] zVcl, int[] ind, int[] stack, double tol) {

		IJ.showStatus("XYZ_Import " + Integer.toString(numberOfPoints)
				+ " points - sorting");

		// Initialize variables for triangulation:
		for (int i = 1; i <= numberOfPoints; i++) {
			ind[i] = i;
			stack[i] = 0;
		}

		// Sort all points in lexicographically increasing (x,y) order:
		dHeapSort(numberOfPoints, xVcl, yVcl, ind, tol);
		boundaryOfXYZCoordSysXYZ[0] = xVcl[ind[1]]; // Minimum value in
		// direction of X
		boundaryOfXYZCoordSysXYZ[1] = xVcl[ind[numberOfPoints]]; // Maximum
		// value in
		// direction
		// of X

		// Check for identical points; find minimum and maximum value in
		// direction of y:
		IJ.showStatus("XYZ_Import " + Integer.toString(numberOfPoints)
				+ " points - checking");
		double cmax = 0.0;
		int m = 0;
		int m1 = ind[1];
		boundaryOfXYZCoordSysXYZ[2] = yVcl[1];
		boundaryOfXYZCoordSysXYZ[3] = yVcl[1];
		boundaryOfXYZCoordSysXYZ[4] = zVcl[1];
		boundaryOfXYZCoordSysXYZ[5] = zVcl[1];
		for (int i = 2; i <= numberOfPoints; i++) {
			if (yVcl[i] < boundaryOfXYZCoordSysXYZ[2])
				boundaryOfXYZCoordSysXYZ[2] = yVcl[i]; // Minimum value in
			// direction of Y
			if (yVcl[i] > boundaryOfXYZCoordSysXYZ[3])
				boundaryOfXYZCoordSysXYZ[3] = yVcl[i]; // Maximum value in
			// direction of Y
			if (zVcl[i] < boundaryOfXYZCoordSysXYZ[4])
				boundaryOfXYZCoordSysXYZ[4] = zVcl[i]; // Minimum value in
			// direction of Y
			if (zVcl[i] > boundaryOfXYZCoordSysXYZ[5])
				boundaryOfXYZCoordSysXYZ[5] = zVcl[i]; // Maximum value in
			// direction of Y
			m = m1;
			m1 = ind[i];
			cmax = Math.max(Math.abs(xVcl[m]), Math.abs(xVcl[m1]));
			if (Math.abs(xVcl[m] - xVcl[m1]) > tol * cmax && cmax > tol) {
			} else {
				cmax = Math.max(Math.abs(yVcl[m]), Math.abs(yVcl[m1]));
				if (Math.abs(yVcl[m] - yVcl[m1]) > tol * cmax && cmax > tol) {
				} else {
					// Count points to be deleted:
					numberOfPointDoublets++;
					// Set a flag for each point to be deleted:
					stack[m1] = 1;
				}
			}
		}

		// Eliminate coordinates with identical coordinates (in x and y):
		if (numberOfPointDoublets > 0)
			eliminateDoublets(xVcl, yVcl, zVcl, ind, stack, tol);
		return;
	}

	/**
	 * Delete vertices with identical coordinates in X and Y
	 */

	private void eliminateDoublets(double[] xVcl, double[] yVcl, double[] zVcl,
			int[] ind, int[] stack, double tol) {

		IJ.showStatus("XYZ_Import " + Integer.toString(numberOfPoints)
				+ " points - cleaning");
		int i = 0;
		int j = 0;

		// Update list of coordinates:
		for (i = 1; i <= numberOfPoints; i++) {
			if (stack[i] == 1) {
				j++;
				stack[i] = -1;
			} else {
				xVcl[i - j] = xVcl[i];
				yVcl[i - j] = yVcl[i];
				zVcl[i - j] = zVcl[i];
				stack[i] = j;
			}
		}
		// Update sort index of coordinates:
		j = 0;
		for (i = 1; i <= numberOfPoints; i++) {
			if (stack[(ind[i])] >= 0) {
				ind[i - j] = ind[i] - stack[(ind[i])];
			} else {
				j++;
			}
		}
		numberOfPoints = numberOfPoints - numberOfPointDoublets;
		numberOfPointDoublets = 0;
		numberOfEliminatedPoints = j;
		IJ.showStatus("XYZ_Import " + Integer.toString(numberOfPoints)
				+ " points; " + numberOfEliminatedPoints
				+ " eliminated doublets");

		return;
	}

	/**
	 * Transform X and Y coordinates from XY coordinate system to pixel
	 * coordinate system
	 */

	private void transformCoordinates(double[] xVcl, double[] yVcl) {

		for (int i = 1; i <= numberOfPoints; i++) {
			xVcl[i] = (xVcl[i] - boundaryOfImageCoordSysXYZ[0]) / myPixelSize;
			yVcl[i] = (boundaryOfImageCoordSysXYZ[3] - yVcl[i]) / myPixelSize;
		}
	}

	/**
	 * Determine image boundary coordinates depending on the minimum and maximum
	 * values of the input data (in X and Y):
	 *
	 * Please note that in accordance to the concept of ImageJ - the center of
	 * the upper left pixel is (0.0;0.0) in pixel coordinates - the center of
	 * the lower right pixel is (cols-1;rows-1) in pixel coordinates
	 */

	private void setImageBoundary(double tol) {

		double cmax = 0.0;
		cmax = Math.max(cmax, tol);
		cmax = Math.max(cmax, myPixelSize);
		for (int i = 0; i < 4; i++) {
			cmax = Math.max(cmax, boundaryOfXYZCoordSysXYZ[i]);
			cmax = Math.max(cmax, boundaryOfXYZCoordSysXYZ[i] / myPixelSize);
		}

		// Boundary coordinates of target image in XYZ coordinate system:
		boundaryOfImageCoordSysXYZ[0] = ((double) Math
				.floor(boundaryOfXYZCoordSysXYZ[0] / myPixelSize + cmax * tol))
				* myPixelSize;
		boundaryOfImageCoordSysXYZ[1] = ((double) Math
				.ceil(boundaryOfXYZCoordSysXYZ[1] / myPixelSize - cmax * tol))
				* myPixelSize;
		boundaryOfImageCoordSysXYZ[2] = ((double) Math
				.floor(boundaryOfXYZCoordSysXYZ[2] / myPixelSize + cmax * tol))
				* myPixelSize;
		boundaryOfImageCoordSysXYZ[3] = ((double) Math
				.ceil(boundaryOfXYZCoordSysXYZ[3] / myPixelSize - cmax * tol))
				* myPixelSize;

		// Manual interaction to change boundaryOfImageCoordSysXYZ is not yet
		// implemented.
		// If you need to define the image area on your own feel free to
		// implement it.
		// You might add code to manually change boundaryOfImageCoordSysXYZ
		// right HERE.
		// XYZ_Importer is designed to keep on working.

		// Boundary coordinates of target image in pixel coordinate system:
		boundaryOfImageCoordSysImage[0] = 0;
		boundaryOfImageCoordSysImage[1] = (int) (((boundaryOfImageCoordSysXYZ[1] - boundaryOfImageCoordSysXYZ[0]) / myPixelSize) + 0.5); // number
		// of
		// cols
		boundaryOfImageCoordSysImage[2] = (int) (((boundaryOfImageCoordSysXYZ[3] - boundaryOfImageCoordSysXYZ[2]) / myPixelSize) + 0.5); // number
		// of
		// rows
		boundaryOfImageCoordSysImage[3] = 0;

		// Actual coordinates (from XYZ) in pixel coordinate system:
		boundaryOfXYZCoordSysImage[0] = (boundaryOfXYZCoordSysXYZ[0] - boundaryOfImageCoordSysXYZ[0])
				/ myPixelSize;
		boundaryOfXYZCoordSysImage[1] = (boundaryOfXYZCoordSysXYZ[1] - boundaryOfImageCoordSysXYZ[0])
				/ myPixelSize;
		boundaryOfXYZCoordSysImage[2] = (boundaryOfImageCoordSysXYZ[3] - boundaryOfXYZCoordSysXYZ[2])
				/ myPixelSize;
		boundaryOfXYZCoordSysImage[3] = (boundaryOfImageCoordSysXYZ[3] - boundaryOfXYZCoordSysXYZ[3])
				/ myPixelSize;

		return;
	}

	/**
	 * Purpose: Initialize relative tolerance
	 *
	 * Input parameters: tolin - relative tolerance used to determine tol
	 *
	 * Output parameters: initializeTolerance - relative tolerance max(tolin,
	 * 100.0e0*eps) where eps is approximation to machine epsilon
	 *
	 * This method is based on code originally written and copyrighted by Dr.
	 * Barry Joe, Canada. Inclusion by courtesy of Barry Joe.
	 */

	private double initializeTolerance(double tolin) {
		double initializeTolerance = 0.0;

		double eps = 1.0e0;
		double epsp1 = 0.0;

		do {
			eps = eps / 2.0e0;
			epsp1 = 1.0e0 + eps;
		} while (epsp1 > 1.0e0);

		initializeTolerance = Math.max(tolin, 100.0e0 * eps);
		return initializeTolerance;
	}

	/**
	 * Purpose: Construct Delaunay triangulation of 2-D vertices using
	 * incremental approach and diagonal edge swaps. Vertices first have been
	 * sorted in lexicographically increasing (x,y) order, and now are inserted
	 * one at a time from outside the convex hull.
	 *
	 * Input parameters: numberOfPoints - number of 2-D points (vertices)
	 * stack.length - maximum size available for stack array; should be about
	 * numberOfPoints to be safe, but max(10,2*LOG2(numberOfPoints)) usually
	 * enough xVcl[], yVcl[] - coordinates of 2-D vertices ind[1:numberOfPoints] -
	 * indices in xVcl, yVcl of vertices to be triangulated
	 *
	 * Updated parameters: ind[1:numberOfPoints] - permuted due to sort
	 *
	 * Output parameters: numberOfTriangles - number of triangles in
	 * triangulation; equal to (2*numberOfPoints - nb - 2) where nb = number of
	 * boundary vertices til[1:3],[1:numberOfTriangles] - triangle incidence
	 * list; elements are indices of xVcl, yVcl; vertices of triangles are in
	 * CCW order tnbr[1:3][1:numberOfTriangles] - triangle neighbour list;
	 * negative values are used for links of CCW linked list of boundary edges;
	 * link = -(3*i + j-1) where i, j = triangle, edge index tnbr[J][I] refers
	 * to the neighbour along edge from vertex j to j+1 (mod 3)
	 *
	 * Working parameters: stack[] - used for stack of triangles for which
	 * circumcircle test must be made
	 *
	 * Abnormal return: ierr is set to 8 or 225
	 *
	 * Routines called: dHeapSort, leftOrRightOfLine, swapEdge,
	 * visibleBoundaryEdge
	 *
	 * This method is based on code originally written and copyrighted by Dr.
	 * Barry Joe, Canada. Inclusion by courtesy of Barry Joe.
	 */

	private int delaunayTriangulation(double[] xVcl, double[] yVcl, int[] ind,
			int[][] til, int[][] tnbr, int[] stack, double tol) {

		int delaunayTriangulation = 0;
		int ierr = 0;

		int e = 0;
		int i = 0;
		int j = 0;
		int l = 0;
		int[] ledg = { 0 };
		int lr = 0;
		int leftOrRightOfLine = 0;
		int[] ltri = { 0 };
		int m = 0;
		int m1 = 0;
		int m2 = 0;
		int n = 0;
		int[] redg = { 0 };
		int[] rtri = { 0 };
		int t = 0;
		double cmax = 0.0;
		int ProgressOld = 0;
		int ProgressNew = 0;
		double ProgressCounter = 0.0;

		// Sorting in lexicographically increasing (x,y) order has to been done
		// before!
		// Check for identical points has to been done before!

		// Check whether all points are collinear:
		m1 = ind[1];
		m2 = ind[2];
		j = 2;
		do {
			j = j + 1;
			if (j > numberOfPoints) {
				ierr = 225;
				delaunayTriangulation = ierr;
				return delaunayTriangulation;
			}
			m = ind[j];
			lr = leftOrRightOfLine(xVcl[m], yVcl[m], xVcl[m1], yVcl[m1],
					xVcl[m2], yVcl[m2], 0.0e0, tol);
		} while (lr == 0);

		// Initialize Delaunay triangulation:
		numberOfTriangles = j - 2;
		if (lr == -1) {
			til[0][1] = m1;
			til[1][1] = m2;
			til[2][1] = m;
			tnbr[2][1] = -3;
			for (i = 2; i <= numberOfTriangles; i++) {
				m1 = m2;
				m2 = ind[i + 1];
				til[0][i] = m1;
				til[1][i] = m2;
				til[2][i] = m;
				tnbr[0][i - 1] = -(3 * i);
				tnbr[1][i - 1] = i;
				tnbr[2][i] = i - 1;
			}
			tnbr[0][numberOfTriangles] = -(3 * numberOfTriangles) - 1;
			tnbr[1][numberOfTriangles] = -5;
			ledg[0] = 2;
			ltri[0] = numberOfTriangles;
		} else {
			til[0][1] = m2;
			til[1][1] = m1;
			til[2][1] = m;
			tnbr[0][1] = -4;
			for (i = 2; i <= numberOfTriangles; i++) {
				m1 = m2;
				m2 = ind[i + 1];
				til[0][i] = m2;
				til[1][i] = m1;
				til[2][i] = m;
				tnbr[2][i - 1] = i;
				tnbr[0][i] = -(3 * i) - 3;
				tnbr[1][i] = i - 1;
			}
			tnbr[2][numberOfTriangles] = -(3 * numberOfTriangles);
			tnbr[1][1] = -(3 * numberOfTriangles) - 2;
			ledg[0] = 2;
			ltri[0] = 1;
		}

		// Insert vertices one at a time from outside convex hull, determine
		// visible boundary edges, and apply diagonal edge swaps until
		// Delaunay triangulation of vertices (so far) is obtained.
		ProgressOld = 0;
		for (i = j + 1; i <= numberOfPoints; i++) {
			int top = 0;

			ProgressCounter = ((float) (i)) / ((float) (numberOfPoints));
			ProgressNew = (int) (ProgressCounter * 100.0);
			if (ProgressNew != ProgressOld) {
				ProgressOld = ProgressNew;
				IJ.showStatus("XYZ_Import builds up mesh: "
						+ Integer.toString(i) + " of "
						+ Integer.toString(numberOfPoints) + " points");
				IJ.showProgress(ProgressCounter);
			}

			m = ind[i];
			m1 = til[(ledg[0] - 1)][ltri[0]];
			if (ledg[0] <= 2) {
				m2 = til[(ledg[0])][ltri[0]];
			} else {
				m2 = til[0][ltri[0]];
			}
			lr = leftOrRightOfLine(xVcl[m], yVcl[m], xVcl[m1], yVcl[m1],
					xVcl[m2], yVcl[m2], 0.0e0, tol);
			if (lr > 0) {
				rtri[0] = ltri[0];
				redg[0] = ledg[0];
				ltri[0] = 0;
			} else {
				l = -(tnbr[(ledg[0] - 1)][ltri[0]]);
				rtri[0] = l / 3;
				redg[0] = (l) % (3) + 1;
			}

			visibleBoundaryEdge(xVcl[m], yVcl[m], xVcl, yVcl, til, tnbr, ltri,
					ledg, rtri, redg, tol);

			n = numberOfTriangles + 1;
			l = -(tnbr[(ledg[0] - 1)][ltri[0]]);

			do {
				t = l / 3;
				e = (l) % (3) + 1;
				l = -(tnbr[(e - 1)][t]);
				m2 = til[e - 1][t];
				if (e <= 2) {
					m1 = til[e][t];
				} else {
					m1 = til[0][t];
				}
				numberOfTriangles = numberOfTriangles + 1;
				tnbr[(e - 1)][t] = numberOfTriangles;
				til[0][numberOfTriangles] = m1;
				til[1][numberOfTriangles] = m2;
				til[2][numberOfTriangles] = m;
				tnbr[0][numberOfTriangles] = t;
				tnbr[1][numberOfTriangles] = numberOfTriangles - 1;
				tnbr[2][numberOfTriangles] = numberOfTriangles + 1;
				top = top + 1;
				if (top >= (stack.length - 1)) {
					ierr = 8;
					delaunayTriangulation = ierr;
					return delaunayTriangulation;
				}
				stack[top] = numberOfTriangles;
			} while (t != rtri[0] || e != redg[0]);

			tnbr[(ledg[0] - 1)][ltri[0]] = -(3 * n) - 1;
			tnbr[1][n] = -(3 * numberOfTriangles) - 2;
			tnbr[2][numberOfTriangles] = -(l);
			ltri[0] = n;
			ledg[0] = 2;

			ierr = swapEdge(m, top, ltri, ledg, xVcl, yVcl, til, tnbr, stack,
					tol);

			if (ierr != 0) {
				delaunayTriangulation = ierr;
				return delaunayTriangulation;
			}
		}
		delaunayTriangulation = ierr;
		return delaunayTriangulation;
	}

	/**
	 * Purpose: Use dHeapSort to obtain the permutation of n 2-dimensional
	 * double precision points so that the points are in lexicographic
	 * increasing order.
	 *
	 * Input parameters: numberOfPoints, xVcl[], yVcl[], ind[] - see above
	 *
	 * Updated parameters: ind[n] - elements are permuted so that xVcl(ind[1]) <=
	 * xVcl(ind[2]) <= ... <= xVcl(ind[n])
	 *
	 * Methods called: dShiftDownHeap
	 *
	 * This method is based on code originally written and copyrighted by Dr.
	 * Barry Joe, Canada. Inclusion by courtesy of Barry Joe.
	 */

	private void dHeapSort(int numberOfPoints, double[] xVcl, double[] yVcl,
			int[] ind, double tol) {

		int i = 0;
		int t = 0;
		for (i = numberOfPoints / 2; i >= 1; i += -1) {
			dShiftDownHeap(i, numberOfPoints, xVcl, yVcl, ind, tol);
		}
		for (i = numberOfPoints; i >= 2; i += -1) {
			t = ind[1];
			ind[1] = ind[i];
			ind[i] = t;
			dShiftDownHeap(1, i - 1, xVcl, yVcl, ind, tol);
		}
	}

	/**
	 * Purpose: Shift xVcl[ind[lowerindex]] and yVcl[ind[lowerindex]] down a
	 * heap of size upperindex.
	 *
	 * Input parameters: lowerindex, upperindex - lower and upper index of part
	 * of heap xVcl[], yVcl[], ind[], tol - see above
	 *
	 * Updated parameters: ind[] - see above
	 *
	 * Methods called: dLess
	 *
	 * This method is based on code originally written and copyrighted by Dr.
	 * Barry Joe, Canada. Inclusion by courtesy of Barry Joe.
	 */

	private void dShiftDownHeap(int lowerindex, int upperindex, double[] xVcl,
			double[] yVcl, int[] ind, double tol) {

		int i = 0;
		int j = 0;
		int t = 0;
		boolean dLess = false;
		i = lowerindex;
		j = 2 * i;
		t = ind[i];

		while (j <= upperindex) {
			if (j < upperindex) {
				if (dLess(xVcl[(ind[j])], yVcl[(ind[j])], xVcl[(ind[j + 1])],
						yVcl[(ind[j + 1])], tol))
					j = j + 1;
			}
			if (dLess(xVcl[(ind[j])], yVcl[(ind[j])], xVcl[t], yVcl[t], tol))
				break;
			ind[i] = ind[j];
			i = j;
			j = 2 * i;
		}
		ind[i] = t;
	}

	/**
	 * Purpose: Determine whether point p is lexicographically less than point q
	 * in floating point arithmetic?
	 *
	 * Input parameters: px,py and qx,qy - two 2-dimensional double precision
	 * points
	 *
	 * Returned function value: dLess - true if p < q, false otherwise
	 *
	 * This method is based on code originally written and copyrighted by Dr.
	 * Barry Joe, Canada. Inclusion by courtesy of Barry Joe.
	 */

	private boolean dLess(double px, double py, double qx, double qy, double tol) {
		boolean dLess = false;
		double cmax = 0.0;

		cmax = Math.max(Math.abs(px), Math.abs(qx));
		if (Math.abs(px - qx) > tol * cmax && cmax > tol) {
			if (px < qx) {
				dLess = true;
			} else {
				dLess = false;
			}
		} else {
			cmax = Math.max(Math.abs(py), Math.abs(qy));
			if (Math.abs(py - qy) > tol * cmax && cmax > tol) {
				if (py < qy) {
					dLess = true;
				} else {
					dLess = false;
				}
			} else {
				dLess = false;
			}
		}
		return dLess;
	}

	/**
	 * Purpose: Swap diagonal edges in 2-D triangulation based on empty
	 * circumcircle criterion until all triangles are Delaunay, given that i is
	 * index of new vertex added to triangulation. Determine whether triangles
	 * in stack are Delaunay, and swap diagonal edge of convex quadrilateral if
	 * not.
	 *
	 * Input parameters: i - index in xVcl[], yVcl[] of new vertex top - index
	 * of top of stack, >= 0 btri,bedg - if positive, these are triangle and
	 * edge index of a boundary edge whose updated indices must be recorded
	 * xVcl[], yVcl[], til[][], tnbr[][] - see above stack[1:top] - index of
	 * initial triangles (involving vertex i) put in stack; the edges opposite i
	 * should be in interior
	 *
	 * Updated parameters: top - becomes 0, i.e. stack is empty btri,bedg - may
	 * be updated due to swap(s) til,tnbr - updated due to swaps
	 *
	 * Working parameters: stack[top+1:(stack.length-1)] - used as stack
	 *
	 * Abnormal return: swapEdge is set to 8
	 *
	 * Routines called: diagonalEdge
	 *
	 * This method is based on code originally written and copyrighted by Dr.
	 * Barry Joe, Canada. Inclusion by courtesy of Barry Joe.
	 */

	private int swapEdge(int i, int top, int[] btri, int[] bedg, double[] xVcl,
			double[] yVcl, int[][] til, int[][] tnbr, int[] stack, double tol) {

		int swapEdge = 0;

		int a = 0;
		int b = 0;
		int c = 0;
		int diagonalEdge = 0;
		int e = 0;
		int ee = 0;
		int em1 = 0;
		int ep1 = 0;
		int f = 0;
		int fm1 = 0;
		int fp1 = 0;
		int l = 0;
		int r = 0;
		int s = 0;
		int swap = 0;
		int t = 0;
		int tt = 0;
		int u = 0;
		double x = 0.0;
		double y = 0.0;

		x = xVcl[i];
		y = yVcl[i];

		while (top > 0) {
			t = stack[top];
			top = top - 1;
			if (til[0][t] == i) {
				e = 2;
				b = til[2][t];
			} else if (til[1][t] == i) {
				e = 3;
				b = til[0][t];
			} else {
				e = 1;
				b = til[1][t];
			}
			a = til[(e - 1)][t];
			u = tnbr[(e - 1)][t];
			if (tnbr[0][u] == t) {
				f = 1;
				c = til[2][u];
			} else if (tnbr[1][u] == t) {
				f = 2;
				c = til[0][u];
			} else {
				f = 3;
				c = til[1][u];
			}
			swap = diagonalEdge(x, y, xVcl[a], yVcl[a], xVcl[c], yVcl[c],
					xVcl[b], yVcl[b], tol);
			if (swap == 1) {
				em1 = e - 1;
				if (em1 == 0)
					em1 = 3;
				ep1 = e + 1;
				if (ep1 == 4)
					ep1 = 1;
				fm1 = f - 1;
				if (fm1 == 0)
					fm1 = 3;
				fp1 = f + 1;
				if (fp1 == 4)
					fp1 = 1;
				til[(ep1 - 1)][t] = c;
				til[(fp1 - 1)][u] = i;
				r = tnbr[(ep1 - 1)][t];
				s = tnbr[(fp1 - 1)][u];
				tnbr[(ep1 - 1)][t] = u;
				tnbr[(fp1 - 1)][u] = t;
				tnbr[(e - 1)][t] = s;
				tnbr[(f - 1)][u] = r;
				if (tnbr[(fm1 - 1)][u] > 0) {
					top = top + 1;
					stack[top] = u;
				}
				if (s > 0) {
					if (tnbr[0][s] == u) {
						tnbr[0][s] = t;
					} else if (tnbr[1][s] == u) {
						tnbr[1][s] = t;
					} else {
						tnbr[2][s] = t;
					}
					top = top + 1;
					if (top >= (stack.length - 1)) {
						swapEdge = 8;
						return swapEdge;
					}
					stack[top] = t;
				} else {
					if (u == btri[0] && fp1 == bedg[0]) {
						btri[0] = t;
						bedg[0] = e;
					}
					l = -((3 * t + e - 1));
					tt = t;
					ee = em1;
					while (tnbr[(ee - 1)][tt] > 0) {
						tt = tnbr[(ee - 1)][tt];
						if (til[0][tt] == a) {
							ee = 3;
						} else if (til[1][tt] == a) {
							ee = 1;
						} else {
							ee = 2;
						}
					}
					tnbr[(ee - 1)][tt] = l;
				}
				if (r > 0) {
					if (tnbr[0][r] == t) {
						tnbr[0][r] = u;
					} else if (tnbr[1][r] == t) {
						tnbr[1][r] = u;
					} else {
						tnbr[2][r] = u;
					}
				} else {
					if (t == btri[0] && ep1 == bedg[0]) {
						btri[0] = u;
						bedg[0] = f;
					}
					l = -((3 * u + f - 1));
					tt = u;
					ee = fm1;
					while (tnbr[(ee - 1)][tt] > 0) {
						tt = tnbr[(ee - 1)][tt];
						if (til[0][tt] == b) {
							ee = 3;
						} else if (til[1][tt] == b) {
							ee = 1;
						} else {
							ee = 2;
						}
					}
					tnbr[(ee - 1)][tt] = l;
				}
			}
		}
		return swapEdge;
	}

	/**
	 * Purpose: Determine whether 02 or 13 is the diagonal edge chosen based on
	 * the circumcircle criterion, where (x0,y0), (x1,y1), (x2,y2), (x3,y3) are
	 * the vertices of a simple quadrilateral in counterclockwise order.
	 *
	 * Input parameters: x0,y0, x1,y1, x2,y2, x3,y3 - vertex coordinates
	 *
	 * Returned function value: diagonalEdge - 1 if diagonal edge 02 is chosen,
	 * i.e. 02 is inside quadrilateral + vertex 3 is outside circumcircle 012 -1
	 * if diagonal edge 13 is chosen, i.e. 13 is inside quadrilateral + vertex 0
	 * is outside circumcircle 123 0 if four vertices are cocircular
	 *
	 * This method is based on code originally written and copyrighted by Dr.
	 * Barry Joe, Canada. Inclusion by courtesy of Barry Joe.
	 */

	private int diagonalEdge(double x0, double y0, double x1, double y1,
			double x2, double y2, double x3, double y3, double tol) {

		int diagonalEdge = 0;

		double ca = 0.0;
		double cb = 0.0;
		double dx10 = 0.0;
		double dx12 = 0.0;
		double dx30 = 0.0;
		double dx32 = 0.0;
		double dy10 = 0.0;
		double dy12 = 0.0;
		double dy30 = 0.0;
		double dy32 = 0.0;
		double s = 0.0;
		double tola = 0.0;
		double tolb = 0.0;

		dx10 = x1 - x0;
		dy10 = y1 - y0;
		dx12 = x1 - x2;
		dy12 = y1 - y2;
		dx30 = x3 - x0;
		dy30 = y3 - y0;
		dx32 = x3 - x2;
		dy32 = y3 - y2;

		tola = Math.max(Math.abs(dx10), Math.abs(dy10));
		tola = Math.max(tola, Math.abs(dx30));
		tola = Math.max(tola, Math.abs(dy30));
		tola = tol * tola;

		tolb = Math.max(Math.abs(dx12), Math.abs(dy12));
		tolb = Math.max(tolb, Math.abs(dx32));
		tolb = Math.max(tolb, Math.abs(dy32));
		tolb = tol * tolb;

		ca = dx10 * dx30 + dy10 * dy30;
		cb = dx12 * dx32 + dy12 * dy32;
		if (ca > tola && cb > tolb) {
			diagonalEdge = -1;
		} else if (ca < -(tola) && cb < -(tolb)) {
			diagonalEdge = 1;
		} else {
			tola = Math.max(tola, tolb);
			s = (dx10 * dy30 - dx30 * dy10) * cb + (dx32 * dy12 - dx12 * dy32)
					* ca;
			if (s > tola) {
				diagonalEdge = -1;
			} else if (s < -(tola)) {
				diagonalEdge = 1;
			} else {
				diagonalEdge = 0;
			}
		}
		return diagonalEdge;
	}

	/**
	 * Purpose: Determine boundary edges of 2-D triangulation which are visible
	 * from point (X,Y) outside convex hull. Find rightmost visible boundary
	 * edge using links, then possibly leftmost visible boundary edge using
	 * triangle neighbour info.
	 *
	 * Input parameters: x,y - 2-D point outside convex hull xVcl[], yVcl[],
	 * til[][], tnbr[][] - see above ltri,ledg - if ltri <> 0 then they are
	 * assumed to be as defined below and are not changed, else they are updated
	 * rtri - index of boundary triangle to begin search at redg - edge of
	 * triangle rtri that is visible from (x,y)
	 *
	 * Updated parameters: ltri - index of boundary triangle to left of leftmost
	 * boundary triangle visible from (x,y) ledg - boundary edge of triangle
	 * ltri to left of leftmost boundary edge visible from (x,y) rtri - index of
	 * rightmost boundary triangle visible from (x,y) redg - edge of triangle
	 * rtri that is visible from (x,y) [Note: 1 <= ledg, redg <= 3]
	 *
	 * Methods called: leftOrRightOfLine
	 *
	 * This method is based on code originally written and copyrighted by Dr.
	 * Barry Joe, Canada. Inclusion by courtesy of Barry Joe.
	 */

	private void visibleBoundaryEdge(double x, double y, double[] xVcl,
			double[] yVcl, int[][] til, int[][] tnbr, int[] ltri, int[] ledg,
			int[] rtri, int[] redg, double tol) {

		int a = 0;
		int b = 0;
		int e = 0;
		int l = 0;
		int lr = 0;
		int leftOrRightOfLine = 0;
		int t = 0;
		boolean ldone = false;

		if (ltri[0] == 0) {
			ldone = false;
			ltri[0] = rtri[0];
			ledg[0] = redg[0];
		} else {
			ldone = true;
		}

		do {
			l = -(tnbr[(redg[0] - 1)][rtri[0]]);
			t = l / 3;
			e = (l) % (3) + 1;
			a = til[e - 1][t];
			if (e <= 2) {
				b = til[e][t];
			} else {
				b = til[0][t];
			}
			lr = leftOrRightOfLine(x, y, xVcl[a], yVcl[a], xVcl[b], yVcl[b],
					0.0e0, tol);
			if (lr > 0) {
				rtri[0] = t;
				redg[0] = e;
			}
		} while (lr > 0);

		if (ldone) {
			return;
		}

		t = ltri[0];
		e = ledg[0];

		do {
			b = til[e - 1][t];
			if (e >= 2) {
				e = e - 1;
			} else {
				e = 3;
			}
			while (tnbr[(e - 1)][t] > 0) {
				t = tnbr[(e - 1)][t];
				if (til[0][t] == b) {
					e = 3;
				} else if (til[1][t] == b) {
					e = 1;
				} else {
					e = 2;
				}
			}
			a = til[e - 1][t];
			lr = leftOrRightOfLine(x, y, xVcl[a], yVcl[a], xVcl[b], yVcl[b],
					0.0e0, tol);
		} while (lr > 0);
		ltri[0] = t;
		ledg[0] = e;
		return;
	}

	/**
	 * Purpose: Determine whether a point is to the left of, right of, or on a
	 * directed line parallel to a line through given points.
	 *
	 * Input parameters: xu,yu, xv1,yv1, xv2,yv2 - vertex coordinates; the
	 * directed line is parallel to and at signed distance DV to the left of the
	 * directed line from (xv1,yv1) to (xv2,yv2); (xu,yu) is the vertex for
	 * which the position relative to the directed line is to be determined dv -
	 * signed distance (positive for left)
	 *
	 * Returned function value: leftOrRightOfLine - +1, 0, or -1 depending on
	 * whether (xu,yu) is to the right of, on, or left of the directed line (0
	 * if line degenerates to a point)
	 *
	 * This method is based on code originally written and copyrighted by Dr.
	 * Barry Joe, Canada. Inclusion by courtesy of Barry Joe.
	 */

	private int leftOrRightOfLine(double xu, double yu, double xv1, double yv1,
			double xv2, double yv2, double dv, double tol) {
		int leftOrRightOfLine = 0;
		double dx = 0.0;
		double dxu = 0.0;
		double dy = 0.0;
		double dyu = 0.0;
		double t = 0.0;
		double tolabs = 0.0;
		dx = xv2 - xv1;
		dy = yv2 - yv1;
		dxu = xu - xv1;
		dyu = yu - yv1;
		// maximum of dx,dy,dxu,dyu,dv:
		tolabs = Math.max(Math.abs(dx), Math.abs(dy));
		tolabs = Math.max(tolabs, Math.abs(dxu));
		tolabs = Math.max(tolabs, Math.abs(dyu));
		tolabs = Math.max(tolabs, Math.abs(dv));
		tolabs = tol * tolabs;
		t = dy * dxu - dx * dyu;
		if (dv != 0.0e0)
			t = t + dv * Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		if (t < 0.0) {
			leftOrRightOfLine = -1;
		} else {
			leftOrRightOfLine = 1;
		}
		if (Math.abs(t) <= tolabs)
			leftOrRightOfLine = 0;
		return leftOrRightOfLine;
	}

	/**
	 * Interpolate a depth image from given z-values: For each pixel position,
	 * find the triangle corresponding to the pixel position. This is done
	 * pretty fast by 'walking' through neighbouring triangles. Afterwards
	 * interpolate the depth value for the pixel position using barycentric
	 * coordinates (linear interpolation, tilted plane within three points). If
	 * the pixel position is outside the convex hull of the given points or is
	 * within a triangle which exceeds a specified maximum edge length, a
	 * background value is assigned.
	 *
	 * myMaximumEdgeLength=0 - use every triangle for interpolation
	 * myMaximumEdgeLength>0 - disregard triangles if the length of one edge >
	 * myMaximumEdgeLength
	 *
	 * Note: The triangulation offers very fast access to neighbouring
	 * triangles. It should be straightforward to implement complexer
	 * interpolation schemes which need access to a larger surrounding (not only
	 * to three points) of a given pixel position.
	 */

	private void interpolateImageLinear(int[][] til, int[][] tnbr,
			double[] xVcl, double[] yVcl, double[] zVcl, int[] stack, // temporarily
			// used
			// to
			// mark
			// triangles
			// to be
			// disregarded
			float[] myPixelValues, // depth image
			double tol) {

		double x = 0.0;
		double y = 0.0;
		int i1 = 0;
		int i2 = 0;
		int triangleIndex = 1;
		int triangleIndexBeginningOfNextLine = 1;
		double[] bary = new double[3];
		double[] xse = new double[3];
		double[] yse = new double[3];
		double sk1 = 0.0;
		double sk2 = 0.0;
		double sk3 = 0.0;

		int za = boundaryOfImageCoordSysImage[2] + 1;
		int sa = boundaryOfImageCoordSysImage[1] + 1;

		int ProgressOld = 0;
		int ProgressNew = 0;
		double ProgressCounter = 0.0;

		// square maximum edge length to avoid calculation of square roots in
		// future:
		double myMaximumEdgeLengthSquared = Math.pow(
				(myMaximumEdgeLength / myPixelSize), 2);

		// mark triangles to be disregarded:
		for (i1 = 1; i1 <= numberOfTriangles; i1++) {
			xse[0] = xVcl[(til[0][i1])];
			xse[1] = xVcl[(til[1][i1])];
			xse[2] = xVcl[(til[2][i1])];
			yse[0] = yVcl[(til[0][i1])];
			yse[1] = yVcl[(til[1][i1])];
			yse[2] = yVcl[(til[2][i1])];

			// calculate squared edge lengths and do comparison:
			sk3 = (Math.pow((xse[1] - xse[0]), 2) + Math.pow((yse[1] - yse[0]),
					2));
			sk2 = (Math.pow((xse[2] - xse[0]), 2) + Math.pow((yse[2] - yse[0]),
					2));
			sk1 = (Math.pow((xse[2] - xse[1]), 2) + Math.pow((yse[2] - yse[1]),
					2));
			if (myMaximumEdgeLengthSquared <= tol) {
				stack[i1] = 1;
			} else if (sk1 > myMaximumEdgeLengthSquared
					|| sk2 > myMaximumEdgeLengthSquared
					|| sk3 > myMaximumEdgeLengthSquared) {
				stack[i1] = 0;
			} else {
				stack[i1] = 1;
			}
		}

		// go and get a value for each image pixel:
		int i0 = 0;
		for (i1 = 0; i1 < za; i1++) {
			ProgressCounter = ((float) (i1 + 1)) / ((float) (za));
			ProgressNew = (int) (ProgressCounter * 100.0);
			if (ProgressNew != ProgressOld) {
				ProgressOld = ProgressNew;
				IJ.showStatus("XYZ_Import generates 32-bit image: "
						+ Integer.toString(i1 + 1) + " of "
						+ Integer.toString(za + 1) + " lines");
				IJ.showProgress(ProgressCounter);
			}

			triangleIndex = triangleIndexBeginningOfNextLine;
			for (i2 = 0; i2 < sa; i2++) {
				y = (double) (i1);
				x = (double) (i2);

				// find the corresponding triangle by 'walking' through the
				// triangulation:
				triangleIndex = walkThroughTriangulation(x, y, bary, xVcl,
						yVcl, til, tnbr, triangleIndex, tol);

				// interpolate pixel value using barycentric coordinates:
				if ((triangleIndex >= 0) && (stack[(triangleIndex)] == 1)) {
					myPixelValues[i0++] = (float) (bary[0]
							* zVcl[(til[0][(triangleIndex)])] + bary[1]
							* zVcl[(til[1][(triangleIndex)])] + bary[2]
							* zVcl[(til[2][(triangleIndex)])]);
				} else {
					myPixelValues[i0++] = (float) myBackground;
					if (triangleIndex < 0)
						triangleIndex = triangleIndex * (-1); // reset if
					// point was
					// outside of
					// convex hull
				}

				if (i2 == 1)
					triangleIndexBeginningOfNextLine = triangleIndex;

			}
		}
		return;
	}

	/**
	 * Walk through neighbouring triangles of 2-D (Delaunay) triangulation until
	 * a triangle is found containing point (x,y) or (x,y) is found to be
	 * outside the convex hull. Search is guaranteed to terminate for a Delaunay
	 * triangulation, else a cycle may occur.
	 *
	 * This method is based on code originally written and copyrighted by Dr.
	 * Barry Joe, Canada. Inclusion by courtesy of Barry Joe.
	 */

	private int walkThroughTriangulation(double x, double y,
			double[] bary, // uses barycentric coordinates to determine where
			// (x,y) is located
			double[] xVcl, double[] yVcl, int[][] til, int[][] tnbr,
			int triangleIndex, // index of triangle to begin search at
			double tol) {
		int i = 0;
		for (int cnt = 1; cnt <= numberOfTriangles; cnt++) {
			int a = til[0][triangleIndex];
			int b = til[1][triangleIndex];
			int c = til[2][triangleIndex];
			double dxa = xVcl[a] - xVcl[c];
			double dya = yVcl[a] - yVcl[c];
			double dxb = xVcl[b] - xVcl[c];
			double dyb = yVcl[b] - yVcl[c];
			double dx = x - xVcl[c];
			double dy = y - yVcl[c];
			double det = dxa * dyb - dya * dxb;
			bary[0] = (dx * dyb - dy * dxb) / det;
			bary[1] = (dxa * dy - dya * dx) / det;
			bary[2] = 1.0e0 - bary[0] - bary[1];

			if ((bary[0] > tol) && (bary[1] > tol) && (bary[2] > tol)) {
				return triangleIndex; // (x,y) is in the interior of triangle
				// triangleIndex
			} else if (bary[0] < -(tol)) {
				i = tnbr[1][triangleIndex];
				if (i <= 0) {
					// iedg = -2; // (x,y) is outside convex hull due to walking
					// past edge 2 of triangle triangleIndex
					return triangleIndex * (-1); // point is outside convex
					// hull
				}
			} else if (bary[1] < -(tol)) {
				i = tnbr[2][triangleIndex];
				if (i <= 0) {
					// iedg = -3; // (x,y) is outside convex hull due to walking
					// past edge 3 of triangle triangleIndex
					return triangleIndex * (-1); // point is outside convex
					// hull
				}
			} else if (bary[2] < -(tol)) {
				i = tnbr[0][triangleIndex];
				if (i <= 0) {
					// iedg = -1; // (x,y) is outside convex hull due to walking
					// past edge 1 of triangle triangleIndex
					return triangleIndex * (-1); // (x,y) is outside convex
					// hull
				}
			} else if (bary[0] <= tol) {
				// if (bary[1] <= tol) {
				// iedg = 6; // (x,y) is (nearly) vertex 3 of triangleIndex;
				// }
				// else if (bary[2] <= tol) {
				// iedg = 5; // (x,y) is (nearly) vertex 2 of triangleIndex;
				// }
				// else {
				// iedg = 2; // (x,y) is on interior of edge 2 of triangleIndex;
				// }
				return triangleIndex;

			} else if (bary[1] <= tol) {
				// if (bary[2] <= tol) {
				// iedg = 4; // (x,y) is (nearly) vertex 1 of triangleIndex;
				// }
				// else {
				// iedg = 3; // (x,y) is on interior of edge 3 of triangleIndex;
				// }
				return triangleIndex;

			} else {
				// iedg = 1; // (x,y) is on interior of edge 1 of triangleIndex;
				return triangleIndex;
			}

			triangleIndex = i;
		}
		return -1; // abnormal return (cycle)
	}
}