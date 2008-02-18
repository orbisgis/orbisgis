/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.sif.multiInputPanel;

import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.sif.SQLUIPanel;

public class MultiInputPanel implements SQLUIPanel {

	private String id;
	private URL url;
	private String title;

	private ArrayList<String> expressions = new ArrayList<String>();
	private ArrayList<String> errors = new ArrayList<String>();
	private ArrayList<Input> inputs = new ArrayList<Input>();
	private HashMap<String, Input> nameInput = new HashMap<String, Input>();
	private InputPanel comp;
	private String infoText;
	private boolean showFavourites;

	public MultiInputPanel(String title) {
		this(null, title);
	}

	public MultiInputPanel(String id, String title) {
		this(id, title, true);
	}

	public MultiInputPanel(String id, String title, boolean showFavorites) {
		this.id = id;
		this.setTitle(title);
		this.showFavourites = showFavorites;
	}

	public void addInput(String name, String text, InputType type) {
		Input input = new Input(name, text, null, type);
		inputs.add(input);
		nameInput.put(name, input);
	}

	public void addInput(String name, String text, String initialValue,
			InputType type) {
		Input input = new Input(name, text, initialValue, type);
		inputs.add(input);
		nameInput.put(name, input);
	}

	public void addText(String text) {
		Input input = new Input(null, text, null, new NoInputType());
		inputs.add(input);
	}

	public void addValidationExpression(String sql, String errorMsg) {
		this.expressions.add(sql);
		this.errors.add(errorMsg);
	}

	public void setIcon(URL url) {
		this.url = url;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setInfoText(String infoText) {
		this.infoText = infoText;
	}

	public String getInfoText() {
		return infoText;
	}

	public String[] getErrorMessages() {
		return errors.toArray(new String[0]);
	}

	public String[] getFieldNames() {
		ArrayList<String> ret = new ArrayList<String>();
		for (Input input : inputs) {
			if (input.getType().isPersistent()) {
				ret.add(input.getName());
			}
		}

		return ret.toArray(new String[0]);
	}

	public int[] getFieldTypes() {
		String[] fieldNames = getFieldNames();
		int[] ret = new int[fieldNames.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = nameInput.get(fieldNames[i]).getType().getType();
		}

		return ret;
	}

	public String getId() {
		return id;
	}

	public String validateInput() {
		return null;
	}

	public String[] getValidationExpressions() {
		return expressions.toArray(new String[0]);
	}

	public String[] getValues() {
		String[] fieldNames = getFieldNames();
		String[] ret = new String[fieldNames.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = nameInput.get(fieldNames[i]).getType().getValue();
		}

		return ret;
	}

	public void setValue(String fieldName, String fieldValue) {
		Input input = nameInput.get(fieldName);
		if (input != null) {
			input.getType().setValue(fieldValue);
		}
	}

	public Component getComponent() {
		if (comp == null) {
			comp = new InputPanel(inputs);
		}

		return comp;
	}

	public URL getIconURL() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public String initialize() {
		return null;
	}

	public String getInput(String inputName) {
		Input input = nameInput.get(inputName);
		if (input != null) {
			return input.getType().getValue();
		} else {
			return null;
		}
	}

	public void group(String title, String... inputs) {
		for (String inputName : inputs) {
			nameInput.get(inputName).setGroup(title);
		}
	}

	private class NoInputType implements InputType {

		public Component getComponent() {
			return null;
		}

		public int getType() {
			return STRING;
		}

		public String getValue() {
			return null;
		}

		public boolean isPersistent() {
			return false;
		}

		public void setValue(String value) {
		}

	}

	public String postProcess() {
		return null;
	}

	public boolean showFavorites() {
		return showFavourites;
	}

}
