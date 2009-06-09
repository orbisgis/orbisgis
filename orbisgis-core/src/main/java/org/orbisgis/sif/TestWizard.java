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
package org.orbisgis.sif;

import javax.swing.JDialog;

public class TestWizard {

	public static void main(String[] args) {
		DynamicUIPanel[] panel = new DynamicUIPanel[3];
		panel[0] = UIFactory.getDynamicUIPanel("Connection", null,
				new String[] { "host", "port" }, new int[] { SQLUIPanel.STRING,
						SQLUIPanel.INT }, null, null);
		panel[1] = UIFactory.getDynamicUIPanel("Database", null, new String[] {
				"database", "user", "password" }, new int[] {
				SQLUIPanel.STRING, SQLUIPanel.STRING, SQLUIPanel.INT },
				new String[] { "database is not null" },
				new String[] { "Ey men! You have to specify a database!" });
		panel[2] = UIFactory.getDynamicUIPanel("Congratulations!", null,
				new String[] { "any float" }, new int[] { SQLUIPanel.DOUBLE },
				null, null);

		JDialog dlg = UIFactory.getWizard(panel);
		dlg.setModal(true);
		dlg.pack();
		dlg.setVisible(true);

		System.out.println(panel[0].getValue("host"));
		System.out.println(panel[0].getValue("port"));
		System.out.println(panel[1].getValue("database"));
		System.out.println(panel[1].getValue("user"));
		System.out.println(panel[1].getValue("password"));
		System.out.println(panel[2].getValue("any float"));

	}
}
