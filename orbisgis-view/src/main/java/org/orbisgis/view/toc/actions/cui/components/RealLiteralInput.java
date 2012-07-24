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
package org.orbisgis.view.toc.actions.cui.components;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This class provide an auto-save text field for real values
 * @author Maxence Laurent
 */
public abstract class RealLiteralInput extends JPanel {

	private JTextField input;
	private JSlider slider;
	private JLabel sliderValue;
	private Double min;
	private Double max;
	private Double initial;

	private static final int nbColumns = 10;

	/**
	 *
	 * @param min
	 * @param max
	 */
	public RealLiteralInput(String name, Double initialValue, Double min, Double max) {
		super();
		this.min = min;
		this.max = max;
		this.initial = initialValue;

		boolean useSlider;
		String label = "";
		if (name != null)
			label = name;

		useSlider = (min != null && max != null);

		if (useSlider) {
			if (min > max)
				throw new IndexOutOfBoundsException("Bounds are invalid (min > max)");
		} else {
			if (min != null) {
				label += " ( > " + min + ")";
			} else if (max != null) {
				label += " ( < " + max + ")";
			}
		}

		this.add(new JLabel(label + ":"));


		if (useSlider) {
			slider = new JSlider(min.intValue(), max.intValue(), initialValue.intValue());
			sliderValue = new JLabel();
			sliderValue.setSize(40,15); // Todo compute size of the bigger text with user font !

			updateSliderValue();

			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(5);

			slider.setPaintLabels(true);
			slider.setPaintTicks(true);
			slider.setPaintTrack(true);

			slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					valueChanged((double)slider.getValue());
					updateSliderValue();
				}
			});
			this.add(slider);
			this.add(sliderValue);
		} else {
			if (initial != null) {
				input = new JTextField(initialValue.toString());
			} else {
				input = new JTextField();
			}
			input.setColumns(RealLiteralInput.nbColumns);

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
	}

	private void updateSliderValue(){
		sliderValue.setText(Integer.toString(slider.getValue()) + " %");
	}

	private void updateValue(boolean resetOnError) {
		String inputText = input.getText();
		if (inputText != null && !inputText.equalsIgnoreCase("")) {
			Double inputd = null;

			try {
				inputd = Double.parseDouble(inputText);
				if ((min != null && inputd < min) || (max != null && inputd > max)) {
					inputd = null;
				}
			} catch (Exception ex) {
				inputd = null;
			}

			if (inputd != null) {
				valueChanged(inputd);
			} else if (resetOnError) {
				if (initial != null) {
					input.setText(this.initial.toString());
				} else {
					input.setText("");
				}
				valueChanged(null);
			}

		} else {
			valueChanged(null);
		}
	}

	protected abstract void valueChanged(Double v);
}
