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
package org.orbisgis.core.ui.plugins.views;

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

import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class OutputPanel extends JPanel implements OutputManager {

	private static final int MAX_CHARACTERS = 2048;

	private JTextPane jTextArea;
	private String viewId;

	public OutputPanel(String viewId) {
		this.setLayout(new BorderLayout());
		jTextArea = new JTextPane();
		this.viewId = viewId;

		this.add(getButtonPanel(), BorderLayout.NORTH);
		this.add(new JScrollPane(jTextArea), BorderLayout.CENTER);

	}

	public JPanel getButtonPanel() {

		JPanel buttonsPanel = new JPanel();
		final FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		buttonsPanel.setLayout(flowLayout);
		JButton deleteBt = new JButton();
		deleteBt.setIcon(OrbisGISIcon.EDIT_CLEAR);
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

		WorkbenchContext wbContext = Services
				.getService(WorkbenchContext.class);
		wbContext.getWorkbench().getFrame().showView(viewId);
	}
}
