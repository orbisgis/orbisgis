/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.sif;

import java.awt.Component;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


public class DynamicUIPanel implements SQLUIPanel {
        protected final static I18n i18n = I18nFactory.getI18n(DynamicUIPanel.class);
	private String[] errorMsgs;
	private String[] expressions;
	private int[] types;
	private String[] names;
	private String title;
	private URL icon;
	private JTextField[] txts;
	private String id;

	public DynamicUIPanel(String id, String title, URL icon, String[] names,
			int[] types, String[] expressions, String[] errorMsgs) {
		this.id = id;
		this.title = title;
		this.icon = icon;
		this.names = names;
		this.types = types;
		this.expressions = expressions;
		this.errorMsgs = errorMsgs;
	}

	public String[] getErrorMessages() {
		return errorMsgs;
	}

	public String[] getFieldNames() {
		return names;
	}

	public int[] getFieldTypes() {
		return types;
	}

	public String[] getValidationExpressions() {
		return expressions;
	}

	public String[] getValues() {
		String[] ret = new String[txts.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = txts[i].getText();
		}

		return ret;
	}

	public Component getComponent() {
		JPanel textPanel = new JPanel();
		JPanel labelPanel = new JPanel();
		textPanel.setLayout(new CRFlowLayout());
		labelPanel.setLayout(new CRFlowLayout());
		txts = new JTextField[names.length];
		for (int i = 0; i < names.length; i++) {
			txts[i] = new JTextField(10);
			textPanel.add(txts[i]);
			textPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel(names[i] + ": ")); //$NON-NLS-1$
			labelPanel.add(new CarriageReturn());
		}

		JPanel all = new JPanel();
		all.add(labelPanel);
		all.add(textPanel);

		return all;
	}

	public URL getIconURL() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

	public String initialize() {
		return null;
	}

	public String validateInput() {
		return null;
	}

	public String getValue(String fieldName) {
		int index = -1;
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(fieldName)) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			throw new IllegalArgumentException(
					i18n.tr("sif.dynamicUIPanel.thereIsNoFieldWithName") + fieldName); //$NON-NLS-1$
		}

		return txts[index].getText();
	}

	public void setValue(String fieldName, String fieldValue) {
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(fieldName)) {
				txts[i].setText(fieldValue);
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getInfoText() {
		return null;
	}

	public String postProcess() {
		return null;
	}

	public boolean isShowFavorites() {
		return true;
	}

}
