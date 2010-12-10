/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.components;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.io.Opener;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

/////////////////////////////////////////////////////////////////////
//
//		Yawi2D (Yet Another Wand for ImageJ 2D) - 2.1.0-SVN
//				http://yawi3d.sourceforge.net
//
// This is the selection tool (magic wand) used on 2D slices
// to select ROIs. It uses an adaptive algorithm based on cromatic
// composition of areas that segments regions with cromatic
// similarities.
// The pluging provides a powerful implementation of the Wand
// selection tool and it can be applied, with respect to ImageJ
// wand, to a wider spectrum of problems.
//
// This software is released under GPL license, you can find a
// copy of this license at http://www.gnu.org/copyleft/gpl.html
//
//
// Start date:
// 	2004-05-05
// Last update date:
// 	2007-10-01
//
// Authors:
//	Davide Coppola - dav_mc@users.sourceforge.net
//	Mario Rosario Guarracino - marioguarracino@users.sourceforge.net
//
/////////////////////////////////////////////////////////////////////
public class MagicWand {

	private ImagePlus imp;
	private byte[] img_pixels;
	/// image dimension
	private Dimension img_dim = new Dimension();
	/// lower threshold limit
	private int lower_threshold;
	/// upper threshold limit
	private int upper_threshold;
	/// radius of threshold rectangle
	private int rad_tresh;
	/// edge point
	private Point edge_p = new Point();
	/// initial direction of edge
	private int start_dir;
	/// directions
	static final int UP = 0, DOWN = 1, UP_OR_DOWN = 2, LEFT = 3, RIGHT = 4, LEFT_OR_RIGHT = 5, NA = 6;
	/// max number of points of a ROI. it is increased if necessary
	private int max_points = 1000;
	/// starting point, the point clicked by the user
	private Point start_p = new Point();
	/// X coordinate of the points in the border of the Roi
	private int[] xpoints = new int[max_points];
	/// Y coordinate of the points in the border of the Roi
	private int[] ypoints = new int[max_points];
	/// backup arrays - X coordinate
	private int[] xpoints_b;
	/// backup arrays - Y coordinate
	private int[] ypoints_b;
	/// number of points in the generated outline => dimension of xpoints and ypoints
	private int npoints = 0;
	/// generated ROI
	private Roi roi = null;
	// default values for settings
	private static int RAD_DEF = 2;
	private static float PERC_DEF = 0.6f;
	private static int SIDE_DEF = 5;
	/// Inside - radius threshold
	private int _rad_ts = RAD_DEF;
	/// Inside - minimum percentage
	private float _min_perc = PERC_DEF;
	/// SetThreshold - side
	private int _side = SIDE_DEF;
	/// screen dimension
	Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
	/// histogram left padding
	static final int HIST_XPAD = 4;
	/// histogram top padding
	static final int HIST_TOP_YPAD = 2;
	/// histogram bottom padding
	static final int HIST_YPAD = 132;
	/// histogram height limit for normal values
	static final int HIST_LIMIT_Y = 120;
	/// histogram height limit for mode value
	static final int HIST_MAX_Y = 129;
	/// number of colors of the image
	static final int HIST_COLORS = 256;
	private ImageProcessor ip;

	//private ConversionDialog d = null;
	public MagicWand() {
		imp = new ImagePlus();

		roi = null;

		Opener opener = new Opener();
		// load the image
		imp = opener.openImage("/home/maxence/mercator.jpg");

		// update the ImageCanvas with the new Image
		// store the ImageProcessor
		ip = imp.getProcessor();

		// save img dimension
		img_dim.setSize(ip.getWidth(), ip.getHeight());
		// save img pixels
		img_pixels = (byte[]) ip.getPixels();
	}

	/// generate the ROI
	public void MakeROI(int x, int y) {
		start_p.setLocation(x, y);

		SetThreshold(x, y);
		AutoOutline(x, y);

		//there's a selection
		if (TraceEdge()) {
			Roi previousRoi = imp.getRoi();
			roi = new PolygonRoi(xpoints, ypoints, npoints, Roi.TRACED_ROI);
			imp.killRoi();
			imp.setRoi(roi);

			if (previousRoi != null) {
				roi.update(IJ.shiftKeyDown(), IJ.altKeyDown());
			}

		} else //no selection
		{
			imp.killRoi();
		}
	}

	/// set the threshold of the ROI
	private void SetThreshold(int x, int y) {
		int dist = _side / 2;
		int color;

		int i, k;

		lower_threshold = 255;
		upper_threshold = 0;

		for (i = (y - dist); i <= (y + dist); i++) {
			for (k = (x - dist); k <= (x + dist); k++) {
				color = GetColor(k, i);

				if (color > upper_threshold) {
					upper_threshold = color;
				} else if (color < lower_threshold) {
					lower_threshold = color;
				}
			}
		}
	}

	/// return the color of a pixel located at (x,y)
	private int GetColor(int x, int y) {
		if (x >= 0 && y >= 0 && x < img_dim.width && y < img_dim.height) {
			return img_pixels[(img_dim.width * y) + x] & 0xff;
		} else {
			return 0;
		}
	}

	/// find ROI border starting from (start_x,start_y) point inside the area
	private void AutoOutline(int start_x, int start_y) {
		edge_p.setLocation(start_x, start_y);

		int direction = 0;

		if (Inside(edge_p.x, edge_p.y, RIGHT)) {
			// if DELTAthreshold is very small we use the ImageJ inside
			if ((upper_threshold - lower_threshold) < 5) {
				do {
					edge_p.x++;
				} while (Inside(edge_p.x, edge_p.y) && edge_p.x < img_dim.width);
			} else {
				do {
					edge_p.x++;
				} while (Inside(edge_p.x, edge_p.y, RIGHT) && edge_p.x < img_dim.width);
				// we are still into the threshold area
				if (Inside(edge_p.x, edge_p.y)) {
					do {
						edge_p.x++;
					} while (Inside(edge_p.x, edge_p.y) && edge_p.x < img_dim.width);
				} // we are out the threshold area more than 1 pixel
				else if (!Inside(edge_p.x - 1, edge_p.y)) {
					do {
						edge_p.x--;
					} while (!Inside(edge_p.x, edge_p.y, LEFT) && edge_p.x > 0);
				}
			}

			// initial direction
			if (!Inside(edge_p.x - 1, edge_p.y - 1)) {
				direction = RIGHT;
			} else if (Inside(edge_p.x, edge_p.y - 1)) {
				direction = LEFT;
			} else {
				direction = DOWN;
			}
		} else {
			// this case is not managed
		}

		// start direction is set for traceEdge
		start_dir = direction;
	}

	/// ImageJ inside, checks just 1 pixel
	/// check if the pixel color is inside the threshold or not
	private boolean Inside(int x, int y) {
		int value = -1;

		if (x >= 0 && y >= 0 && x < img_dim.width && y < img_dim.height) {
			value = img_pixels[(img_dim.width * y) + x] & 0xff;
		}

		return (value >= lower_threshold && value <= upper_threshold);
	}

	/// Yawi2D inside, checks a square area
	/// check if most of the pixels are inside the threshold or not
	private boolean Inside(int x, int y, int direction) {
		int x_a, x_b;
		int y_a, y_b;

		x_a = x_b = y_a = y_b = 0;


		// moving UP
		if (direction == UP) {
			if (x - _rad_ts > 0) {
				x_a = x - _rad_ts;
			} else {
				x_a = 0;
			}

			if (x + rad_tresh < img_dim.width) {
				x_b = x + _rad_ts;
			} else {
				x_b = img_dim.width - 1;
			}

			if (y - (_rad_ts * 2) > 0) {
				y_a = y - (_rad_ts * 2);
			} else {
				y_a = 0;
			}

			y_b = y;
		}

		// moving DOWN
		if (direction == DOWN) {
			if (x - _rad_ts > 0) {
				x_a = x - _rad_ts;
			} else {
				x_a = 0;
			}

			if (x + _rad_ts < img_dim.width) {
				x_b = x + _rad_ts;
			} else {
				x_b = img_dim.width - 1;
			}

			y_a = y;

			if (y + (_rad_ts * 2) < img_dim.height) {
				y_b = y + (_rad_ts * 2);
			} else {
				y_b = img_dim.height - 1;
			}
		}

		// moving LEFT
		if (direction == LEFT) {
			if (x - (2 * _rad_ts) > 0) {
				x_a = x - (2 * _rad_ts);
			} else {
				x_a = 0;
			}

			x_b = x;

			if (y - _rad_ts > 0) {
				y_a = y - _rad_ts;
			} else {
				y_a = 0;
			}

			if (y + _rad_ts < img_dim.height) {
				y_b = y + _rad_ts;
			} else {
				y_b = img_dim.height - 1;
			}
		}

		// moving RIGHT
		if (direction == RIGHT) {
			x_a = x;

			if (x + (2 * _rad_ts) < img_dim.width) {
				x_b = x + (2 * _rad_ts);
			} else {
				x_b = img_dim.width - 1;
			}

			if (y - _rad_ts > 0) {
				y_a = y - _rad_ts;
			} else {
				y_a = 0;
			}

			if (y + _rad_ts < img_dim.height) {
				y_b = y + _rad_ts;
			} else {
				y_b = img_dim.height - 1;
			}
		}

		int area = ((_rad_ts * 2) + 1) * ((_rad_ts * 2) + 1);
		int inside_count = 0;
		int xp, yp;

		for (xp = x_a; xp <= x_b; xp++) {
			for (yp = y_a; yp <= y_b; yp++) {
				if (Inside(xp, yp)) {
					inside_count++;
				}
			}
		}

		return (((float) inside_count) / area >= _min_perc);
	}

	/// traces an object defined by lower and upper threshold values.
	/// The boundary points are stored in the public xpoints and ypoints fields
	private boolean TraceEdge() {
		int secure = 0;

		int[] table = {
			// 1234 1=upper left pixel,  2=upper right, 3=lower left, 4=lower right
			NA, // 0000 should never happen
			RIGHT, // 000X
			DOWN, // 00X0
			RIGHT, // 00XX
			UP, // 0X00
			UP, // 0X0X
			UP_OR_DOWN, // 0XX0 Go up or down depending on current direction
			UP, // 0XXX
			LEFT, // X000
			LEFT_OR_RIGHT, // X00X Go left or right depending on current direction
			DOWN, // X0X0
			RIGHT, // X0XX
			LEFT, // XX00
			LEFT, // XX0X
			DOWN, // XXX0
			NA, // XXXX Should never happen
		};

		int index;
		int new_direction;
		int x = edge_p.x;
		int y = edge_p.y;
		int direction = start_dir;

		// upper left
		boolean UL = Inside(x - 1, y - 1);
		// upper right
		boolean UR = Inside(x, y - 1);
		// lower left
		boolean LL = Inside(x - 1, y);
		// lower right
		boolean LR = Inside(x, y);

		int count = 0;

		do {
			index = 0;

			if (LR) {
				index |= 1;
			}
			if (LL) {
				index |= 2;
			}
			if (UR) {
				index |= 4;
			}
			if (UL) {
				index |= 8;
			}

			new_direction = table[index];

			// uncertainty, up or down
			if (new_direction == UP_OR_DOWN) {
				if (direction == RIGHT) {
					new_direction = UP;
				} else {
					new_direction = DOWN;
				}
			}

			// uncertainty, left or right
			if (new_direction == LEFT_OR_RIGHT) {
				if (direction == UP) {
					new_direction = LEFT;
				} else {
					new_direction = RIGHT;
				}
			}

			// error
			if (new_direction == NA) {
				return false;
			}

			// a new direction means a new selection's point
			if (new_direction != direction) {
				xpoints[count] = x;
				ypoints[count] = y;
				count++;

				// xpoints and ypoints need more memory
				if (count == xpoints.length) {
					int[] xtemp = new int[max_points * 2];
					int[] ytemp = new int[max_points * 2];

					System.arraycopy(xpoints, 0, xtemp, 0, max_points);
					System.arraycopy(ypoints, 0, ytemp, 0, max_points);

					xpoints = xtemp;
					ypoints = ytemp;

					max_points *= 2;
				}
			}

			// moving along the selected direction
			switch (new_direction) {
				case UP:
					y = y - 1;
					LL = UL;
					LR = UR;
					UL = Inside(x - 1, y - 1);
					UR = Inside(x, y - 1);
					break;

				case DOWN:
					y = y + 1;
					UL = LL;
					UR = LR;
					LL = Inside(x - 1, y);
					LR = Inside(x, y);
					break;

				case LEFT:
					x = x - 1;
					UR = UL;
					LR = LL;
					UL = Inside(x - 1, y - 1);
					LL = Inside(x - 1, y);
					break;

				case RIGHT:
					x = x + 1;
					UL = UR;
					LL = LR;
					UR = Inside(x, y - 1);
					LR = Inside(x, y);
					break;
			}

			direction = new_direction;

			if (secure < 10000) {
				secure++;
			} else // traceEdge OVERFLOW!!!
			{
				return false;
			}

		} while ((x != edge_p.x || y != edge_p.y || direction != start_dir));

		// number of ROI points
		npoints = count;

		// backup ROI point
		xpoints_b = new int[npoints];
		ypoints_b = new int[npoints];

		for (int i = 0; i < npoints; i++) {
			xpoints_b[i] = xpoints[i];
			ypoints_b[i] = ypoints[i];
		}

		return true;
	}
}
