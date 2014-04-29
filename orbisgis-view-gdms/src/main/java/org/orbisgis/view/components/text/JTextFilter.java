/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.components.text;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.orbisgis.view.components.button.JButtonTextField;
import org.orbisgis.view.icons.OrbisGISIcon;

public class JTextFilter extends JPanel {

	private JTextField txtFilter;
	private JButton btnClear;

	public JTextFilter() {
		txtFilter = new JButtonTextField(
				OrbisGISIcon.getIcon("small_search.png"), 8);
		txtFilter.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				enableButton();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				enableButton();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				enableButton();
			}

			private void enableButton() {
				btnClear.setVisible(txtFilter.getText().length() > 0);
			}
		});
		this.add(txtFilter);
		btnClear = new JButton(OrbisGISIcon.getIcon("remove"));
		btnClear.setBorderPainted(false);
		btnClear.setBackground(null);
		btnClear.setVisible(false);
		btnClear.setMargin(new Insets(0, 0, 0, 0));
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				txtFilter.setText("");
			}

		});
		this.add(btnClear);
	}

	public void addDocumentListener(DocumentListener listener) {
		txtFilter.getDocument().addDocumentListener(listener);
	}

	public void removeDocumentListener(DocumentListener listener) {
		txtFilter.getDocument().removeDocumentListener(listener);
	}

	public String getText() {
		return txtFilter.getText();
	}

}
