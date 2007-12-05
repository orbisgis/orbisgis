package org.orbisgis.toolsMenuPanel;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

/**
 * This class corresponds to the small bottom UrbSAT panel. The one dedicated to
 * the description of the selected menu item.
 */
public class DescriptionScrollPane extends JScrollPane {
	private JTextArea jTextArea;

	public DescriptionScrollPane() {
		jTextArea = new JTextArea();
		jTextArea.setLineWrap(true);
		jTextArea.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		jTextArea.setEditable(false);

		setViewportView(jTextArea);
	}

	public JTextArea getJTextArea() {
		return jTextArea;
	}
}