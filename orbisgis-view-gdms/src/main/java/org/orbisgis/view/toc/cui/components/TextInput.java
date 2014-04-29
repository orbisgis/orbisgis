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
package org.orbisgis.view.toc.actions.cui.components;


import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This class provide an auto-save text field for real values
 * @author Maxence Laurent
 */
public abstract class TextInput extends JPanel {

	private JTextField input;
	private String initial;
	private boolean allowBlank;

	/**
	 *
	 * @param min
	 * @param max
	 */
	public TextInput(String name, String initialValue, int size, boolean allowBlank) {
		super();
		this.initial = initialValue;
		this.allowBlank = allowBlank;

		this.add(new JLabel(name + ":"));


		if (initial != null) {
			input = new JTextField(initialValue.toString());
		} else {
			input = new JTextField();
		}
		input.setColumns(size);

		/*
		 * This listener will be fired each time the text field content will be changed
		 */
		input.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateValue(false);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateValue(false);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});


		/*
		 * this listener will be fired when the field loose the focus
		 */
		input.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				updateValue(true);
			}
		});

		this.add(input);
	}

    public void setValue(String s){
        input.setText(s);
        valueChanged(s);
    }

	private void updateValue(boolean resetOnError) {
		String inputText = input.getText();
		if (resetOnError && (inputText == null || 
				(!allowBlank && inputText.equalsIgnoreCase("")))){
			inputText = this.initial;
			this.input.setText(initial);
		}
		valueChanged(inputText);
	}

	@Override
	public void setEnabled(boolean enabled){
		this.input.setEnabled(enabled);
	}

    protected JTextField getInputField(){
        return input;
    }

	protected abstract void valueChanged(String s);
}