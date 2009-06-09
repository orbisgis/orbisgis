package org.orbisgis.core.ui.components.preview;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.orbisgis.images.IconLoader;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;

/**
 * Numeric spinner
 * 
 * @author fergonco
 */
public class JNumericSpinner extends JPanel {

	private JTextField txt;
	private JButton up;
	private JButton down;
	private NumberFormat numberFormat;
	private Timer incTimer;
	private IncActionListener incActionListener;
	private ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();

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
		up = createButton(IconLoader.getIcon("spinner_up.png"), 1);
		pnlButtons.add(up);
		pnlButtons.add(new CarriageReturn());
		down = createButton(IconLoader.getIcon("spinner_down.png"), -1);
		pnlButtons.add(down);
		this.add(pnlButtons, BorderLayout.EAST);
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
	public void setInc(double inc) {
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
			currentInc = currentInc * 1.01;
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
	public void setMin(int min) {
		incActionListener.min = min;
	}

	/**
	 * Sets the minimum value the buttons can take the value
	 * 
	 * @param min
	 */
	public void setMax(int max) {
		incActionListener.max = max;
	}
}
