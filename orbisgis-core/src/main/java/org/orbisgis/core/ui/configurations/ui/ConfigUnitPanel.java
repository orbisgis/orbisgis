package org.orbisgis.core.ui.configurations.ui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.multiInputPanel.InputType;

public class ConfigUnitPanel extends JPanel {
	public ConfigUnitPanel(String title, JCheckBox checkbox, String enableText,
			String[] labels, final InputType[] inputs) {
		setLayout(new CRFlowLayout());
		setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), title));

		if (checkbox != null) {
			JPanel checkPanel = new JPanel();
			checkPanel.add(checkbox);
			checkPanel.add(new JLabel(enableText));
			add(checkPanel);
			add(new CarriageReturn());
		}

		CRFlowLayout layout = new CRFlowLayout();
		layout.setAlignment(CRFlowLayout.LEFT);
		JPanel labelPanel = new JPanel(layout);
		for (int i = 0; i < labels.length; i++) {
			JLabel l = new JLabel(labels[i]);
			Dimension size = l.getPreferredSize();
			size.height = inputs[i].getComponent().getPreferredSize().height;
			l.setPreferredSize(size);
			labelPanel.add(l);
			labelPanel.add(new CarriageReturn());
		}

		JPanel compPanel = new JPanel(layout);
		for (InputType input : inputs) {
			compPanel.add(input.getComponent());
			compPanel.add(new CarriageReturn());
		}

		add(labelPanel);
		add(compPanel);
		Dimension min = labelPanel.getPreferredSize();
		min.width += compPanel.getPreferredSize().width + 35;
		setMinimumSize(min);
	}
}
