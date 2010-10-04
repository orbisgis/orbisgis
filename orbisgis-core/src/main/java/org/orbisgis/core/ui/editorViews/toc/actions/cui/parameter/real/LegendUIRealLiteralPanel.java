/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real;

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
import org.orbisgis.core.images.OrbisGISIcon;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;

/**
 *
 * @author maxence
 */
public class LegendUIRealLiteralPanel extends LegendUIComponent implements LegendUIRealComponent {

	private final RealLiteral real;
	private JSlider slider;
	private JLabel sliderValue;

	private JTextField input;

	private Double min;
	private Double max;
	private Double initial;

	private final static int nbColumns = 10;

	public LegendUIRealLiteralPanel(String name, LegendUIController controller, LegendUIComponent parent, RealLiteral realLiteral) {
		super(name, controller, parent, 0);
		this.real = realLiteral;
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.PALETTE; // TOOD Change
	}

	@Override
	protected void mountComponent() {
		min = real.getMinValue();
		max = real.getMaxValue();
		initial = real.getValue(null);

		boolean useSlider = min != null && max != null;

		if (useSlider) {
			if (max < min) {
				throw new IndexOutOfBoundsException("Bounds are invalid (min > max)");
			}
			slider = new JSlider(min.intValue(), max.intValue(), initial.intValue());
			sliderValue = new JLabel();
			updateSliderValue();

			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(5);

			slider.setPaintLabels(true);
			slider.setPaintTicks(true);
			slider.setPaintTrack(true);

			slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					valueChanged((double) slider.getValue());
					updateSliderValue();
				}
			});
			this.add(slider, BorderLayout.WEST);
			this.add(sliderValue, BorderLayout.EAST);

		} else {
			input = new JTextField(initial.toString());
			input.setColumns(LegendUIRealLiteralPanel.nbColumns);

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
		sliderValue.setText(String.format("%" + digit + "d %%", slider.getValue()));
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


}
