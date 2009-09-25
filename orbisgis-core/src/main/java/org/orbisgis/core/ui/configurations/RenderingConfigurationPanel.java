package org.orbisgis.core.ui.configurations;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;

public class RenderingConfigurationPanel extends JPanel implements ItemListener {

	private JCheckBox compositeCheck;
	private JComboBox compositeCb;
	private JCheckBox antialiasingCheck;

	String alpha = "1.0";

	private ViewRenderingPanel view;
	private boolean antialiasing;
	private boolean composite;
	private String composite_value;

	public RenderingConfigurationPanel(boolean antialiasing, boolean composite,
			String composite_value) {
		this.antialiasing = antialiasing;
		this.composite = composite;
		this.composite_value = composite_value;
	}

	public void init() {

		this.setLayout(new BorderLayout());
		this.add(getCheckPanel(), BorderLayout.WEST);
		this.add(new CarriageReturn());
		view = new ViewRenderingPanel(composite_value);
		this.add(view, BorderLayout.CENTER);
	}

	public JPanel getCheckPanel() {
		CRFlowLayout crf = new CRFlowLayout();
		crf.setAlignment(CRFlowLayout.LEFT);
		JPanel checkJPanel = new JPanel(crf);
		setAntialiasingCheck(new JCheckBox());
		getAntialiasingCheck().setText("Activate antialiasing.");
		getAntialiasingCheck().setSelected(antialiasing);
		getAntialiasingCheck().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!getAntialiasingCheck().isSelected()) {
					getAntialiasingCheck().setSelected(false);
				} else {
					getAntialiasingCheck().setEnabled(true);
					view.changeAntialiasing(true);
				}
			}
		});

		Vector<String> items = new Vector<String>();
		items.add(RenderingConfiguration.items1);
		items.add(RenderingConfiguration.items2);
		items.add(RenderingConfiguration.items3);
		items.add(RenderingConfiguration.items4);

		setCompositeCb(new JComboBox(items));
		compositeCb.setSelectedItem(composite_value);
		getCompositeCb().setEnabled(composite);
		getCompositeCb().addItemListener(this);
		setCompositeCheck(new JCheckBox());
		getCompositeCheck().setText("Activate source-over alpha combination");
		getCompositeCheck().setSelected(composite);

		getCompositeCheck().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!getCompositeCheck().isSelected()) {
					getCompositeCheck().setSelected(false);
					getCompositeCb().setEnabled(false);
				} else {
					getCompositeCb().setEnabled(true);
				}
			}
		});

		checkJPanel.add(getAntialiasingCheck());
		checkJPanel.add(new CarriageReturn());
		checkJPanel.add(getCompositeCheck());
		checkJPanel.add(getCompositeCb());

		return checkJPanel;

	}

	public void itemStateChanged(ItemEvent e) {

		if (e.getStateChange() != ItemEvent.SELECTED) {
			return;
		}
		Object choice = e.getSource();
		if (choice == getCompositeCb()) {
			alpha = (String) getCompositeCb().getSelectedItem();
			view.changeRule(alpha);
		} else {

		}

	}

	public void setAntialiasingCheck(JCheckBox antialiasingCheck) {
		this.antialiasingCheck = antialiasingCheck;
	}

	public JCheckBox getAntialiasingCheck() {
		return antialiasingCheck;
	}

	public void setCompositeCheck(JCheckBox compositeCheck) {
		this.compositeCheck = compositeCheck;
	}

	public JCheckBox getCompositeCheck() {
		return compositeCheck;
	}

	public void setCompositeCb(JComboBox compositeCb) {
		this.compositeCb = compositeCb;
	}

	public JComboBox getCompositeCb() {
		return compositeCb;
	}

}
