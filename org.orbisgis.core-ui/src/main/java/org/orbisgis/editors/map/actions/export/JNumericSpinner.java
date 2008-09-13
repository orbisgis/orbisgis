package org.orbisgis.editors.map.actions.export;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.orbisgis.images.IconLoader;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;

public class JNumericSpinner extends JPanel {

	private JTextField txt;
	private JButton up;
	private JButton down;
	private NumberFormat numberFormat;
	private double inc = 0.01;

	public JNumericSpinner(int columns) {
		numberFormat = NumberFormat.getInstance();
		txt = new JTextField(columns);
		this.setLayout(new BorderLayout());
		this.add(txt, BorderLayout.CENTER);
		JPanel pnlButtons = new JPanel();
		CRFlowLayout layout = new CRFlowLayout();
		layout.setVgap(0);
		layout.setHgap(0);
		pnlButtons.setLayout(layout);
		up = new JButton(IconLoader.getIcon("spinner_up.png"));
		Insets buttonMargin = new Insets(up.getMargin().top, 0,
				up.getMargin().bottom, 0);
		up.setMargin(buttonMargin);
		up.getInsets().right = 0;
		up.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				incrementValue(inc);
			}
		});
		pnlButtons.add(up);
		pnlButtons.add(new CarriageReturn());
		down = new JButton(IconLoader.getIcon("spinner_down.png"));
		down.setMargin(buttonMargin);
		down.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				incrementValue(-inc);
			}
		});
		pnlButtons.add(down);
		this.add(pnlButtons, BorderLayout.EAST);
	}

	private void incrementValue(double increment) {
		try {
			double value = numberFormat.parse(txt.getText()).doubleValue();
			value += increment;
			txt.setText(numberFormat.format(value));
		} catch (ParseException e1) {
			// ignore
		}
	}

	public void setValue(double value) {
		txt.setText(numberFormat.format(value));
	}

	public double getValue() {
		try {
			return numberFormat.parse(txt.getText()).doubleValue();
		} catch (ParseException e) {
			return 0;
		}
	}

	public void setInc(int inc) {
		this.inc = inc;
	}

}
