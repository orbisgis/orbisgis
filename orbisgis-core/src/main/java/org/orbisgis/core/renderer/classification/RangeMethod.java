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
package org.orbisgis.core.renderer.classification;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.legend.carto.Interval;

public class RangeMethod {

	private DataSource ds;

	private int nbCl;

	private Range[] ranges;

	private int rowCount;

	private String fieldName;

	public RangeMethod(DataSource ds, String fieldName, int nbCl)
			throws DriverException {
		this.ds = ds;
		// Number of ranges
		this.nbCl = nbCl;
		this.fieldName = fieldName;
		ranges = new Range[nbCl];
		rowCount = (int) ds.getRowCount();
	}

	/**
	 * Quantiles intervalls
	 *
	 * Adpated from SCAP3 : http://w3.geoprdc.univ-tlse2.fr/scap/java/
	 *
	 * @throws DriverException
	 *
	 */
	public void disecQuantiles() throws DriverException {

		int i = 0;

		// Nombre d'individus par classes
		int nipc = rowCount / nbCl;
		int reste = rowCount % nbCl;
		// Calcul du nombre d'individus égal par classe
		for (i = 0; i < nbCl; i++) {
			// Répartition des individus dans les classes
			ranges[i] = new Range();
			ranges[i].setNumberOfItems(nipc);
			ranges[i].setPartOfItems(nipc * 100 / rowCount);
		}
		for (i = 0; i < reste; i++) {
			// Répartition du reste éventuel
			ranges[i].setNumberOfItems(ranges[i].getNumberOfItems() + 1);
			ranges[i].setPartOfItems((nipc + 1) * 100 / rowCount);
		}
		// Calcul bornes
		int compteur = 0;
		double[] valeurs = ClassificationUtils.getSortedValues(ds, fieldName);
		for (i = 0; i < nbCl; i++) {
			ranges[i].setMinRange(valeurs[compteur]);
			compteur += ranges[i].getNumberOfItems();
			if (compteur > (rowCount - 1))
				compteur = rowCount - 1;
			ranges[i].setMaxRange(valeurs[compteur]);
		}
	}

	/**
	 * Equal intervalls method (equivalence)
	 *
	 * Adpated from SCAP3 : http://w3.geoprdc.univ-tlse2.fr/scap/java/
	 *
	 * @throws DriverException
	 *
	 */
	public void disecEquivalences() throws DriverException {
		int i = 0;
		double[] valeurs = ClassificationUtils.getSortedValues(ds, fieldName);
		double min = valeurs[0];
		double max = valeurs[rowCount - 1];
		double largeur = (max - min) / nbCl;
		int compteur = 0;
		int clec = 0;
		double debClec = 0;
		int dernier = 0;
		// Calcul bornes pour des classes de méme largeur
		ranges[0] = new Range();
		ranges[0].setMinRange(valeurs[0]);
		debClec = valeurs[0];
		for (i = 0; i < rowCount; i++) {
			compteur += 1;
			dernier += 1;

			if (valeurs[i] > (debClec + largeur)) {
				ranges[clec].setMaxRange(valeurs[i]);
				ranges[clec].setNumberOfItems(compteur - 1);
				ranges[clec].setPartOfItems((compteur - 1) * 100 / rowCount);
				compteur = 0;
				debClec = valeurs[i];
				if (clec < (nbCl - 1))
					clec += 1;
				else
					break;
				ranges[clec] = new Range();
				ranges[clec].setMinRange(valeurs[i]);
			}
		}
		if ((clec - 1) < (nbCl - 2)) {
			int diff = (nbCl - 2) - (clec - 1);
			for (i = 0; i < diff; i++) {
				ranges[clec + i] = new Range();
				ranges[clec + i].setMinRange(valeurs[rowCount - 1]);
				ranges[clec + i].setMaxRange(valeurs[rowCount - 1]);
				ranges[clec + i].setNumberOfItems(0);
				ranges[clec + i].setPartOfItems(0);
			}
		}
		ranges[nbCl - 1] = new Range();
		ranges[nbCl - 1].setMinRange(ranges[nbCl - 2].getMaxRange());
		ranges[nbCl - 1].setMaxRange(valeurs[rowCount - 1]);
		ranges[nbCl - 1].setNumberOfItems(rowCount - dernier + 1);
		ranges[nbCl - 1].setPartOfItems((rowCount - dernier + 1) * 100
				/ rowCount);
	}

	/**
	 * Mean data classification.
	 *
	 * Adpated from SCAP3 : http://w3.geoprdc.univ-tlse2.fr/scap/java/
	 *
	 * @throws DriverException
	 *             The ranges are available only for 2, 4 and 8.
	 *
	 */
	public void disecMean() throws DriverException {

		if ((nbCl != 2) && ((nbCl != 4) && (nbCl != 8))) {
			throw new IllegalArgumentException(
					"Only 2,4 or 8 intervals allowed");
		}

		double[] valeurs = ClassificationUtils.getSortedValues(ds, fieldName);
		double min = valeurs[0];
		double max = valeurs[rowCount - 1];
		double M = 0;
		double Ma = 0, Ma1 = 0, Ma2 = 0;
		double Mb = 0, Mb1 = 0, Mb2 = 0;
		int Mi = 0;
		int Mai = 0, Ma1i = 0, Ma2i = 0;
		int Mbi = 0, Mb1i = 0, Mb2i = 0;
		// Modification si besoin est du nombre de classes
		if (nbCl != 4 && nbCl != 8) {
			if (Math.abs((nbCl - 4)) < Math.abs((nbCl - 8)))
				nbCl = 4;
			else
				nbCl = 8;
			// todo add a message dialog
		} else {

		}
		M = getMean(valeurs, 0, rowCount);
		Mi = getIndice(valeurs, M);
		Ma = getMean(valeurs, 0, Mi);
		Mai = getIndice(valeurs, Ma);
		Ma1 = getMean(valeurs, 0, Mai);
		Ma1i = getIndice(valeurs, Ma1);
		Ma2 = getMean(valeurs, Mai, Mi);
		Ma2i = getIndice(valeurs, Ma2);
		Mb = getMean(valeurs, Mi, rowCount);
		Mbi = getIndice(valeurs, Mb);
		Mb1 = getMean(valeurs, Mi, Mbi);
		Mb1i = getIndice(valeurs, Mb1);
		Mb2 = getMean(valeurs, Mbi, rowCount);
		Mb2i = getIndice(valeurs, Mb2);
		if (nbCl == 4) {
			ranges[0] = new Range();
			ranges[1] = new Range();
			ranges[2] = new Range();
			ranges[3] = new Range();

			ranges[0].setMinRange(min);
			ranges[0].setMaxRange(valeurs[Mai]);
			ranges[0].setNumberOfItems(Mai - 1);
			ranges[0].setPartOfItems((Mai - 1) * 100 / rowCount);

			ranges[1].setMinRange(valeurs[Mai]);
			ranges[1].setMaxRange(valeurs[Mi]);
			ranges[1].setNumberOfItems((Mi - 1) - (Mai - 1));
			ranges[1].setPartOfItems(((Mi - 1) - (Mai - 1)) * 100 / rowCount);

			ranges[2].setMinRange(valeurs[Mi]);
			ranges[2].setMaxRange(valeurs[Mbi]);
			ranges[2].setNumberOfItems((Mbi - 1) - (Mi - 1));
			ranges[2].setPartOfItems(((Mbi - 1) - (Mi - 1)) * 100 / rowCount);

			ranges[3].setMinRange(valeurs[Mbi]);
			ranges[3].setMaxRange(max);
			ranges[3].setNumberOfItems(rowCount - (Mbi - 1));
			ranges[3].setPartOfItems((rowCount - (Mbi - 1)) * 100 / rowCount);

		}
		if (nbCl == 8) {

			ranges[0] = new Range();
			ranges[1] = new Range();
			ranges[2] = new Range();
			ranges[3] = new Range();
			ranges[4] = new Range();
			ranges[5] = new Range();
			ranges[6] = new Range();
			ranges[7] = new Range();

			ranges[0].setMinRange(min);
			ranges[0].setMaxRange(valeurs[Ma1i]);
			ranges[0].setNumberOfItems(Ma1i - 1);
			ranges[0].setPartOfItems((Ma1i - 1) * 100 / rowCount);

			ranges[1].setMinRange(valeurs[Ma1i]);
			ranges[1].setMaxRange(valeurs[Mai]);
			ranges[1].setNumberOfItems((Mai - 1) - (Ma1i - 1));
			ranges[1].setPartOfItems(((Mai - 1) - (Ma1i - 1)) * 100 / rowCount);

			ranges[2].setMinRange(valeurs[Mai]);
			ranges[2].setMaxRange(valeurs[Ma2i]);
			ranges[2].setNumberOfItems((Ma2i - 1) - (Mai - 1));
			ranges[2].setPartOfItems(((Ma2i - 1) - (Mai - 1)) * 100 / rowCount);

			ranges[3].setMinRange(valeurs[Ma2i]);
			ranges[3].setMaxRange(valeurs[Mi]);
			ranges[3].setNumberOfItems((Mi - 1) - (Ma2i - 1));
			ranges[3].setPartOfItems(((Mi - 1) - (Ma2i - 1)) * 100 / rowCount);

			ranges[4].setMinRange(valeurs[Mi]);
			ranges[4].setMaxRange(valeurs[Mb1i]);
			ranges[4].setNumberOfItems((Mb1i - 1) - (Mi - 1));
			ranges[4].setPartOfItems(((Mb1i - 1) - (Mi - 1)) * 100 / rowCount);

			ranges[5].setMinRange(valeurs[Mb1i]);
			ranges[5].setMaxRange(valeurs[Mbi]);
			ranges[5].setNumberOfItems((Mbi - 1) - (Mb1i - 1));
			ranges[5].setPartOfItems(((Mbi - 1) - (Mb1i - 1)) * 100 / rowCount);

			ranges[6].setMinRange(valeurs[Mbi]);
			ranges[6].setMaxRange(valeurs[Mb2i]);
			ranges[6].setNumberOfItems((Mb2i - 1) - (Mbi - 1));
			ranges[6].setPartOfItems(((Mb2i - 1) - (Mbi - 1)) * 100 / rowCount);

			ranges[7].setMinRange(valeurs[Mb2i]);
			ranges[7].setMaxRange(max);
			ranges[7].setNumberOfItems(rowCount - (Mb2i - 1));
			ranges[7].setPartOfItems((rowCount - (Mb2i - 1)) * 100 / rowCount);

		}
	}

	/**
	 *
	 * Standard discretization
	 *
	 * Adpated from SCAP3 : http://w3.geoprdc.univ-tlse2.fr/scap/java/
	 *
	 * @throws DriverException
	 *             Only 3,5 or 7 intervals allowed.
	 *
	 */
	public void disecStandard() throws DriverException {

		// Discrétisation équivalences : calcul des bornes et des tailles
		double[] valeurs = ClassificationUtils.getSortedValues(ds, fieldName);
		double moyenne = getMean(valeurs, 0, valeurs.length);
		double ec = getEcType(valeurs);
		if ((moyenne - (ec / 2)) < valeurs[0])
			if (nbCl != 3 && nbCl != 5 && nbCl != 7) {
				int ac3 = Math.abs((nbCl - 3));
				int ac5 = Math.abs((nbCl - 5));
				int ac7 = Math.abs((nbCl - 7));
				if (ac3 == Math.min(ac3, Math.min(ac5, ac7)))
					nbCl = 3;
				if (ac5 == Math.min(ac3, Math.min(ac5, ac7)))
					nbCl = 5;
				if (ac7 == Math.min(ac3, Math.min(ac5, ac7)))
					nbCl = 7;
			}
		int compteur = 0;
		int compteurI = 0;

		switch (nbCl) {
		case 3:
			ranges[0] = new Range();
			ranges[1] = new Range();
			ranges[2] = new Range();

			ranges[0].setMinRange(valeurs[0]);
			while (valeurs[compteur] < (moyenne - (ec / 2))) {
				compteur += 1;
			}
			ranges[0].setMaxRange(valeurs[compteur]);
			ranges[0].setNumberOfItems(compteur);
			ranges[0].setPartOfItems(compteur * 100 / valeurs.length);
			ranges[1].setMinRange(valeurs[compteur]);
			while (valeurs[compteur] < (moyenne + (ec / 2))) {
				compteur += 1;
				compteurI += 1;
			}
			ranges[1].setMaxRange(valeurs[compteur]);
			ranges[1].setNumberOfItems(compteurI);
			ranges[1].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[2].setMinRange(valeurs[compteur]);
			ranges[2].setMaxRange(valeurs[valeurs.length - 1]);
			ranges[2].setNumberOfItems(valeurs.length - compteur);
			ranges[2].setPartOfItems((valeurs.length - compteur) * 100
					/ valeurs.length);
			break;
		case 5:
			ranges[0] = new Range();
			ranges[1] = new Range();
			ranges[2] = new Range();
			ranges[3] = new Range();
			ranges[4] = new Range();
			ranges[0].setMinRange(valeurs[0]);
			while (valeurs[compteur] < (moyenne - (ec * 1.5))) {
				compteur += 1;
			}
			ranges[0].setMaxRange(valeurs[compteur]);
			ranges[0].setNumberOfItems(compteur);
			ranges[0].setPartOfItems(compteur * 100 / valeurs.length);
			ranges[1].setMinRange(valeurs[compteur]);
			while (valeurs[compteur] < (moyenne - (ec / 2))) {
				compteur += 1;
				compteurI += 1;
			}
			ranges[1].setMaxRange(valeurs[compteur]);
			ranges[1].setNumberOfItems(compteurI);
			ranges[1].setPartOfItems(compteurI * 100 / valeurs.length);
			ranges[2].setMinRange(valeurs[compteur]);
			compteurI = 0;
			while (valeurs[compteur] < (moyenne + (ec / 2))) {
				compteur += 1;
				compteurI += 1;
			}
			ranges[2].setMaxRange(valeurs[compteur]);
			ranges[2].setNumberOfItems(compteurI);
			ranges[2].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[3].setMinRange(valeurs[compteur]);
			compteurI = 0;
			while (valeurs[compteur] < (moyenne + (ec * 1.5))) {
				compteur += 1;
				compteurI += 1;
			}

			ranges[3].setMaxRange(valeurs[compteur]);
			ranges[3].setNumberOfItems(compteurI);
			ranges[3].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[4].setMinRange(valeurs[compteur]);
			ranges[4].setMaxRange(valeurs[valeurs.length - 1]);
			ranges[4].setNumberOfItems(valeurs.length - compteur);
			ranges[4].setPartOfItems((valeurs.length - compteur) * 100
					/ valeurs.length);

			break;
		case 7:
			ranges[0] = new Range();
			ranges[1] = new Range();
			ranges[2] = new Range();
			ranges[3] = new Range();
			ranges[4] = new Range();
			ranges[5] = new Range();
			ranges[6] = new Range();
			ranges[0].setMinRange(valeurs[0]);
			while (valeurs[compteur] < (moyenne - (ec * 2.5))) {
				compteur += 1;
			}

			ranges[0].setMaxRange(valeurs[compteur]);
			ranges[0].setNumberOfItems(compteur);
			ranges[0].setPartOfItems(compteur * 100 / valeurs.length);

			ranges[1].setMinRange(valeurs[compteur]);
			while (valeurs[compteur] < (moyenne - (ec * 1.5))) {
				compteur += 1;
				compteurI += 1;
			}
			ranges[1].setMaxRange(valeurs[compteur]);
			ranges[1].setNumberOfItems(compteurI);
			ranges[1].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[2].setMinRange(valeurs[compteur]);

			compteurI = 0;
			while (valeurs[compteur] < (moyenne - (ec / 2))) {
				compteur += 1;
				compteurI += 1;
			}

			ranges[2].setMaxRange(valeurs[compteur]);
			ranges[2].setNumberOfItems(compteurI);
			ranges[2].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[3].setMinRange(valeurs[compteur]);

			compteurI = 0;
			while (valeurs[compteur] < (moyenne + (ec / 2))) {
				compteur += 1;
				compteurI += 1;
			}

			ranges[3].setMaxRange(valeurs[compteur]);
			ranges[3].setNumberOfItems(compteurI);
			ranges[3].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[4].setMinRange(valeurs[compteur]);

			compteurI = 0;
			while (valeurs[compteur] < (moyenne + (ec * 1.5))) {
				compteur += 1;
				compteurI += 1;
			}

			ranges[4].setMaxRange(valeurs[compteur]);
			ranges[4].setNumberOfItems(compteurI);
			ranges[4].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[5].setMinRange(valeurs[compteur]);

			compteurI = 0;
			while (valeurs[compteur] < (moyenne - (ec * 2.5))) {
				compteur += 1;
				compteurI += 1;
			}
			ranges[5].setMaxRange(valeurs[compteur]);
			ranges[5].setNumberOfItems(compteurI);
			ranges[5].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[6].setMinRange(valeurs[compteur]);
			ranges[6].setMaxRange(valeurs[valeurs.length - 1]);
			ranges[6].setNumberOfItems(valeurs.length - compteur);
			ranges[6].setPartOfItems((valeurs.length - compteur) * 100
					/ valeurs.length);
			break;

		default:
			throw new IllegalArgumentException(
					"Only 3, 5 and 7 intervalls allowed.");

		}

	}

	/**
	 * TODO: Set items count for each ranges. calculates class limits using
	 * Jenks's Optimisation Method(Natural Break)
	 *
	 * @param data
	 * @param numberClasses
	 * @return break values for classes. E.g. for 4 ranges 3 breaks are
	 *         returned. Min and Max Values are not returned.
	 */
	public void disecNaturalBreaks() throws DriverException {

		double[] limits = new double[nbCl - 1];
		int[] itemsCount = new int[nbCl - 1];
		double[] valeurs = ClassificationUtils.getSortedValues(ds, fieldName);

		int numData = valeurs.length;

		double[][] mat1 = new double[numData + 1][nbCl + 1];
		double[][] mat2 = new double[numData + 1][nbCl + 1];

		for (int i = 1; i <= nbCl; i++) {
			mat1[1][i] = 1;
			mat2[1][i] = 0;
			for (int j = 2; j <= numData; j++)
				mat2[j][i] = Double.MAX_VALUE;
		}
		double v = 0;

		for (int l = 2; l <= numData; l++) {
			double s1 = 0;
			double s2 = 0;
			double w = 0;
			for (int m = 1; m <= l; m++) {
				int i3 = l - m + 1;
				double val = valeurs[i3 - 1];

				s2 += val * val;
				s1 += val;

				w++;
				v = s2 - (s1 * s1) / w;
				int i4 = i3 - 1;
				if (i4 != 0) {
					for (int j = 2; j <= nbCl; j++) {
						if (mat2[l][j] >= (v + mat2[i4][j - 1])) {
							mat1[l][j] = i3;
							mat2[l][j] = v + mat2[i4][j - 1];
						}
						;
					}
					;
				}
				;
			}
			;
			mat1[l][1] = 1;
			mat2[l][1] = v;
		}
		;

		int k = numData;
		int nextK = 0;

		int sum = 0;
		for (int j = nbCl; j >= 2; j--) {
			nextK = k;
			int id = (int) (mat1[k][j]) - 2;
			// -- [sstein] modified version from Hisaji,
			// otherwise breaks will be "on" one item
			// limits[j - 2] = orderedItems[id];
			// -- new
			double limit = valeurs[id + 1];
			limits[j - 2] = limit;
			k = (int) mat1[k][j] - 1;
			int nbItems = (nextK - k);
			sum = nbItems + sum;
			itemsCount[j - 2] = nbItems;

		}

		int index = -1;
		double min = valeurs[0];
		double max = valeurs[0];

		int nbItems = 0;
		for (int j = 0; j < nbCl - 1; j++) {
			min = max;
			index = index + 1;
			max = limits[index];
			ranges[j] = new Range();
			ranges[j].setMinRange(min);
			ranges[j].setMaxRange(max);
			nbItems = itemsCount[j];
			ranges[j].setNumberOfItems(nbItems);
			ranges[j].setPartOfItems(nbItems * 100 / valeurs.length);

		}
		ranges[nbCl - 1] = new Range();
		ranges[nbCl - 1].setMinRange(max);
		ranges[nbCl - 1].setMaxRange(valeurs[rowCount - 1]);
		nbItems = numData - sum;
		ranges[nbCl - 1].setNumberOfItems(nbItems);
		ranges[nbCl - 1].setPartOfItems(nbItems * 100 / valeurs.length);
	}

	/**
	 * Compute the mean value between a set of individus.
	 *
	 * Adpated from SCAP3 : http://w3.geoprdc.univ-tlse2.fr/scap/java/
	 *
	 * @param values
	 * @param start
	 * @param end
	 * @return
	 */
	private double getMean(double[] values, int start, int end) {
		int i = 0;
		double somme = 0;
		double moyenne = 0;
		for (i = start; i < end; i++) {
			somme += values[i];
		}
		moyenne = somme / (end - start);
		return moyenne;
	}

	private int getIndice(double[] valeurs, double element) {
		int i = 0;
		for (i = 0; i < valeurs.length; i++) {
			if (valeurs[i] > element) {
				return i;
			}
		}
		return 0;
	}

	/**
	 *
	 * Calcul de l'ecart-type pour un jeu de valeurs numeriques.
	 *
	 * @param values
	 * @return
	 */
	private double getEcType(double[] values) {
		int i = 0;
		double somme = 0;
		double moyenne = getMean(values, 0, values.length);
		for (i = 0; i < values.length; i++) {
			somme += Math.pow((moyenne - values[i]), 2);
		}
		return Math.sqrt((somme / values.length));
	}

	/**
	 * Get the ranges.
	 *
	 * @return Range array
	 */
	public Range[] getRanges() {
		return ranges;

	}

	public Interval[] getIntervals() {
		Interval[] intervals = new Interval[ranges.length];

		for (int i = 0; i < ranges.length; i++) {
			Range ran = ranges[i];
			Value val1 = ValueFactory.createValue(ran.getMinRange());
			Value val2 = ValueFactory.createValue(ran.getMaxRange());
			boolean maxIncluded = (i == ranges.length - 1);

			Interval inter = new Interval(val1, true, val2, maxIncluded);
			intervals[i] = inter;
		}
		return intervals;
	}

}
