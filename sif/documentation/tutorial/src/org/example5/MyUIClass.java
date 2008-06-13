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
package org.example5;

import java.awt.Component;
import java.net.URL;

import org.sif.SQLUIPanel;

public class MyUIClass implements SQLUIPanel {
	// our UI is like a table with a single row (retrieve name, value and type
	// for each 'field')
	MyJPanel myJPanel;

	public Component getComponent() {
		if (null == myJPanel) {
			myJPanel = new MyJPanel();
		}
		return myJPanel;
	}

	public URL getIconURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		return "5th example - next example is persistance in UI";
	}

	public String initialize() {
		// TODO Auto-generated method stub
		return null;
	}

	public String validate() {
		if (null == myOwnGetSelectionMethod()) {
			return "enter a non null value in ComboBox !";
		}
		return null;
	}

	public String myOwnGetSelectionMethod() {
		return ((MyJPanel) getComponent()).getJComboBoxSelection();
	}

	public String myOwnGetTextFieldMethod() {
		return ((MyJPanel) getComponent()).getJTextFieldEntry();
	}

	public String[] getErrorMessages() {
		return new String[] { "the text field entry must be greater than 123" } ;
	}

	public String[] getFieldNames() {
		return new String[] { "aComboBox", "aTextField" };
	}

	public int[] getFieldTypes() {
		return new int[] { STRING, DOUBLE };
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getValidationExpressions() {
		return new String[] { "aTextField > 123"};
	}

	public String[] getValues() {
		return new String[] { myOwnGetSelectionMethod(),
				myOwnGetTextFieldMethod() };
	}

	public void setValue(String fieldName, String fieldValue) {
		if (fieldName.equals("aComboBox")) {
			fieldValue = myOwnGetSelectionMethod();
		} else if (fieldName.equals("aTextField")) {
			fieldValue = myOwnGetTextFieldMethod();
		} else {
			throw new Error();
		}
	}

	public String getInfoText() {
		// TODO Auto-generated method stub
		return null;
	}

	public String validateInput() {
		// TODO Auto-generated method stub
		return null;
	}
}
