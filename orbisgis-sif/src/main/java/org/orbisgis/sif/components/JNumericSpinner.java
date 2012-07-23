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
package org.orbisgis.sif.components;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.icons.SifIcon;

/**
 * {@code JNumericSpinner} is a text field that can contain only numeric values.
 * It is built with a {@code JTextField} that will let the user change the
 * value directly, and with two buttons that can be used respectively to
 * increase or decrease the numeric value.</p>
 * <p>This panel can be configured with a maximum and minimum authorized values.
 * It is also possible to set the value to be added or removed when using one of
 * the two buttons.
 *
 * @author Fernando Gonzales Cortes, Alexis Guéganno
 */
public class JNumericSpinner extends JPanel {

	private JTextField txt;
	private JButton up;
	private JButton down;
	private NumberFormat numberFormat;
	private Timer incTimer;
	private IncActionListener incActionListener;
	private ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();

        /**
         * Build a new {@code JNumericSpinner}. The {@code int} value given in
         * argument can be used to set the number of characters the inner {@code
         * JTextField} can contain.
         * @param columns
         */
	public JNumericSpinner(int columns) {
		numberFormat = NumberFormat.getInstance();
		txt = new JTextField(columns);
		txt.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				fireChange();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				fireChange();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				fireChange();
			}
		});
		this.setLayout(new BorderLayout());
		this.add(txt, BorderLayout.CENTER);
		JPanel pnlButtons = new JPanel();
		CRFlowLayout layout = new CRFlowLayout();
		layout.setVgap(0);
		layout.setHgap(0);
		pnlButtons.setLayout(layout);
		incActionListener = new IncActionListener();
		incTimer = new Timer(10, incActionListener);
		incTimer.setCoalesce(true);
		incTimer.setRepeats(true);
		incTimer.setInitialDelay(100);
		up = createButton(SifIcon.getIcon("spinner_up"), 1);
		pnlButtons.add(up);
		pnlButtons.add(new CarriageReturn());
		down = createButton(SifIcon.getIcon("spinner_down"), -1);
		pnlButtons.add(down);
		this.add(pnlButtons, BorderLayout.EAST);
	}

        /**
         * Build a new {@code JNumericSpinner}. The text field has width equals
         * to {@code column}, the minimum value is {@code min}, the maximum
         * value is {@code max} and the increment threshold is {@code inc}.
         * @param columns
         * @param min
         * @param max
         * @param inc
         */
        public JNumericSpinner(int columns, int min, int max, double inc){
                this(columns);
                setMax(max);
                setMin(min);
                setInc(inc);
        }

	private JButton createButton(Icon icon, final int sign) {
		JButton button = new JButton(icon);
		Insets buttonMargin = new Insets(button.getMargin().top, 0, button
				.getMargin().bottom, 0);
		button.setMargin(buttonMargin);
		button.setFocusable(false);
		button.getInsets().right = 0;
		button.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				incActionListener.init(sign);
				incActionListener.actionPerformed(null);
				incTimer.start();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				incTimer.stop();
			}
		});
		return button;
	}

	/**
	 * Sets the value of the spinner
	 *
	 * @param value
	 */
	public void setValue(double value) {
		if (value > incActionListener.max) {
			value = incActionListener.max;
		}
		if (value < incActionListener.min) {
			value = incActionListener.min;
		}
		txt.setText(numberFormat.format(value));
		fireChange();
	}

	private void fireChange() {
		for (ChangeListener listener : listeners) {
			listener.stateChanged(new ChangeEvent(this));
		}
	}

	/**
	 * Get the value of the spinner
	 *
	 * @return
	 */
	public double getValue() {
		try {
                        return numberFormat.parse(txt.getText()).doubleValue();
		} catch (ParseException e) {
			return 0;
		}
	}

	/**
	 * Set the primary increment the spinner buttons use
	 *
	 * @param inc
	 */
	public final void setInc(double inc) {
		this.incActionListener.startingInc = inc;
	}

	private final class IncActionListener implements ActionListener {

		private double startingInc = 0.01;
		private double currentInc;
		private int min = Integer.MIN_VALUE;
		private int max = Integer.MAX_VALUE;

		@Override
		public void actionPerformed(ActionEvent e) {
			incrementValue(currentInc);
			currentInc = currentInc * 1.0;
		}

		public void init(int sign) {
			currentInc = sign * startingInc;
		}

		private void incrementValue(double increment) {
			try {
				double value = numberFormat.parse(txt.getText()).doubleValue();
				value += increment;
				setValue(value);
			} catch (ParseException e1) {
				// ignore
			}
		}
	}

	public void addChangeListener(ChangeListener changeListener) {
		this.listeners.add(changeListener);
	}

	public void removeChangeListener(ChangeListener changeListener) {
		this.listeners.remove(changeListener);
	}

	/**
	 * Sets a number format in this spinner.
	 *
	 * @param numberFormat
	 */
	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

	/**
	 * Sets the minimum value the buttons can take the value
	 *
	 * @param min
	 */
	public final void setMin(int min) {
		incActionListener.min = min;
	}

	/**
	 * Sets the minimum value the buttons can take the value
	 *
	 * @param min
	 */
	public final void setMax(int max) {
		incActionListener.max = max;
	}
}
