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
package org.orbisgis.sif.multiInputPanel;

import org.orbisgis.sif.UIFactory;

public class TestMultiInputDialog {

	public static void main(String[] args) {
		MultiInputPanel mip = new MultiInputPanel("org.test",
				"Connect to database");
		mip.setInfoText("Introduce the connection parameters");
		mip.addInput("host", "Host:", "127.0.0.1", new StringType(10));
		mip.addInput("port", "Port:", "19", new IntType());
		mip.addText("Enter the name\n of the database");
		mip.addInput("database", "Database name:", null, new ComboBoxChoice("gdms",
				"template1", "template2"));
		mip.addInput("password", "Password:", "", new PasswordType(8));
		
		mip.addInput("check", "Check to validate", null, new CheckBoxChoice(true));

		mip.addValidationExpression("strlength(host) > 0",
				"you have to put some host");

//		mip.group("Host parameters", "host", "port", "database");
//		mip.group("Connection parameters", "password");

		if (UIFactory.showDialog(mip)) {
			System.out.println(mip.getInput("host"));
			System.out.println(mip.getInput("port"));
			System.out.println(mip.getInput("password"));
			System.out.println(mip.getInput("database"));
		}
	}
}
