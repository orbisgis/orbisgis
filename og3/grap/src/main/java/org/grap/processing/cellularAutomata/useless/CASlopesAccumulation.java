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
package org.grap.processing.cellularAutomata.useless;

import org.grap.processing.cellularAutomata.cam.ICAFloat;

public class CASlopesAccumulation implements ICAFloat {
	private int nrows;

	private int ncols;

	private short[] slopesDirections;

	private int[] neighboursIndices;

	private short[] neighboursDirection;

	public CASlopesAccumulation(final short[] slopesDirections,
			final int nrows, final int ncols) {
		this.nrows = nrows;
		this.ncols = ncols;
		this.slopesDirections = slopesDirections;

		neighboursIndices = new int[] { 1, ncols + 1, ncols, ncols - 1, -1,
				-ncols - 1, -ncols, -ncols + 1 };
		neighboursDirection = new short[] { 16, 32, 64, 128, 1, 2, 4, 8 };
	}

	public int getNCols() {
		return ncols;
	}

	public int getNRows() {
		return nrows;
	}

	public float init(int r, int c, int i) {
		return 1;
	}

	public float localTransition(float[] rac, int r, int c, int i) {
		int result = 1;
		for (int k = 0; k < neighboursIndices.length; k++) {
			final int ii = i + neighboursIndices[k];
			final int rr = ii / ncols;
			final int cc = ii % ncols;

			result += (getSlopesDirectionsValue(slopesDirections, rr, cc, ii) == neighboursDirection[k]) ? getRacValue(
					rac, rr, cc, ii)
					: 0;
		}
		return result;
	}

	private short getSlopesDirectionsValue(short[] v, final int r, final int c,
			final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? -1 : v[i];
	}

	private float getRacValue(final float[] rac, final int r, final int c,
			final int i) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? Float.NaN
				: rac[i];
	}
}