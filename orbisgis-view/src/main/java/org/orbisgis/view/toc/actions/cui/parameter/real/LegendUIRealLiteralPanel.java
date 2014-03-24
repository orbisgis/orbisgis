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
package org.orbisgis.view.toc.actions.cui.parameter.real;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * @author Maxence Laurent
 */
public abstract class LegendUIRealLiteralPanel extends LegendUIComponent implements LegendUIRealComponent {

	private final RealLiteral real;
	private JSlider slider;
	private JLabel label;

	private JTextField input;

	private Double min;
	private Double max;
	private Double initial;

	private static final int nbColumns = 10;

	public LegendUIRealLiteralPanel(String name, LegendUIController controller, LegendUIComponent parent, RealLiteral realLiteral, boolean isNullable) {
		super(name, controller, parent, 0, isNullable);
		this.real = realLiteral;
	}

	@Override
	public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette"); //TODO Change
	}

	@Override
	protected void mountComponent() {
		min = real.getContext().getMin();
		max = real.getContext().getMax();
		initial = real.getValue(null, -1);

		boolean useSlider = min != null && max != null;

		if (useSlider) {
			if (max < min) {
				throw new IndexOutOfBoundsException("Bounds are invalid (min > max)");
			}
			slider = new JSlider((int)(min*100), (int)(max*100), (int)(initial*100));

			label = new JLabel();
			updateSliderValue();

			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(5);

			slider.setPaintLabels(true);
			slider.setPaintTicks(true);
			slider.setPaintTrack(true);

			slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					valueChanged((double) slider.getValue() / 100.0);
					updateSliderValue();
				}
			});
			editor.add(slider, BorderLayout.WEST);
			editor.add(label, BorderLayout.EAST);

		} else {
			input = new JTextField(initial.toString());
			input.setColumns(LegendUIRealLiteralPanel.nbColumns);

			String text = "[";
			if (min != null){
				text += min;
			} else{
				text += "-\u221E";
			}
			text += ";";
			if (max != null){
				text += max;
			} else{
				text += "\u221E";
			}
			text += "]";


			label = new JLabel(text);

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

			editor.add(input, BorderLayout.WEST);
			editor.add(label, BorderLayout.EAST);
		}
	}

	@Override
	public RealParameter getRealParameter() {
		return real;
	}

	private void valueChanged(Double d) {
		if (d != null){
			this.real.setValue(d);
		}
	}

	private void updateSliderValue() {
		int digit = (int)(Math.log10(Math.max(Math.abs(min), Math.abs(max)))) + 2;
		label.setText(String.format("%" + digit + "d %%", slider.getValue()));
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

	@Override
	public Class getEditedClass(){
		return RealLiteral.class;
	}


	@Override
	protected void turnOff() {
		realChanged(null);
	}

	@Override
	protected void turnOn() {
		realChanged(this.real);
	}

	protected abstract void realChanged(RealLiteral real);
}
