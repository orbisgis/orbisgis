/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.views.outputView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.orbisgis.Services;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.images.IconLoader;
import org.orbisgis.outputManager.OutputManager;
import org.orbisgis.view.ViewManager;

public class OutputPanel extends JPanel implements OutputManager {

	private static final int MAX_CHARACTERS = 2048;

	private JTextPane jTextArea;

	public OutputPanel() {
		this.setLayout(new BorderLayout());
		jTextArea = new JTextPane();

		this.add(getButtonPanel(), BorderLayout.NORTH);
		this.add(new JScrollPane(jTextArea), BorderLayout.CENTER);

	}

	public JPanel getButtonPanel() {

		JPanel buttonsPanel = new JPanel();
		final FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		buttonsPanel.setLayout(flowLayout);
		JButton deleteBt = new JButton();
		deleteBt.setIcon(IconLoader.getIcon("edit-clear.png"));
		deleteBt.setToolTipText("Clear the text");
		deleteBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				jTextArea.setText(null);
			}
		});
		buttonsPanel.add(deleteBt);
		return buttonsPanel;

	}

	@Override
	public void println(String out) {
		print(out + "\n");
	}

	@Override
	public void println(String text, Color color) {
		print(text + "\n", color);
	}

	@Override
	public void print(String out) {
		print(out, Color.black);
	}

	@Override
	public void print(String text, Color color) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				StyleConstants.Foreground, color);

		int len = jTextArea.getDocument().getLength();
		try {
			jTextArea.setCaretPosition(len);
			jTextArea.setCharacterAttributes(aset, false);
			jTextArea.getDocument().insertString(len, text, aset);
			len = jTextArea.getDocument().getLength();
			if (len > MAX_CHARACTERS) {
				jTextArea.getDocument().remove(0, len - MAX_CHARACTERS);
			}
		} catch (BadLocationException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot add error message", e);
		}
		jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
		
		ViewManager vm = (ViewManager) Services.getService(ViewManager.class);
		vm.showView("org.orbisgis.views.Output");
	}
}
