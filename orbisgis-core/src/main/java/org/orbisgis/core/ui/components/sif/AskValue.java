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
package org.orbisgis.core.ui.components.sif;

import java.awt.Component;

import javax.swing.JTextField;

import org.orbisgis.sif.AbstractUIPanel;
import org.orbisgis.sif.SQLUIPanel;

public class AskValue extends AbstractUIPanel implements SQLUIPanel {

	private JTextField txtField;
	private String[] sql;
	private String title;
	private String[] error;
	private String initialValue;
	private int type;

	public AskValue(String title, String sql, String error) {
		this(title, sql, error, "");
	}

	public AskValue(String title, String sql, String error, String initialValue) {
		this.title = title;
		this.sql = (sql == null) ? null : new String[] { sql };
		this.error = (error == null) ? null : new String[] { error };
		this.initialValue = initialValue;
		this.type = STRING;
	}

	public Component getComponent() {
		txtField = new JTextField(initialValue);
		return txtField;
	}

	public String getTitle() {
		return title;
	}

	public String validateInput() {
		return null;
	}

	public String[] getErrorMessages() {
		return error;
	}

	public String[] getFieldNames() {
		return new String[] { "txt" };
	}

	public int[] getFieldTypes() {
		return new int[] { type };
	}

	public String[] getValidationExpressions() {
		return sql;
	}

	public String[] getValues() {
		return new String[] { txtField.getText() };
	}

	public String getValue() {
		return getValues()[0];
	}

	public String getId() {
		return null;
	}

	public void setValue(String fieldName, String fieldValue) {
		txtField.setText(fieldValue);
	}

	public void setType(int type) {
		this.type = type;
	}

}
