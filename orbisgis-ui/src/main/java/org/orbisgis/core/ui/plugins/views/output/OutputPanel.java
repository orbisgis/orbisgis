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
package org.orbisgis.core.ui.plugins.views.output;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class OutputPanel extends JPanel implements OutputManager {

	private static final int MAX_CHARACTERS = 2048;

	private JTextPane jTextArea;
	private String viewId;

	public OutputPanel(String viewId) {
		this.setLayout(new BorderLayout());
		jTextArea = new JTextPane();
		this.viewId = viewId;
		this.add(getButtonToolBar(), BorderLayout.NORTH);
		this.add(new JScrollPane(jTextArea), BorderLayout.CENTER);

	}

	public JToolBar getButtonToolBar() {
		JToolBar buttonsToolBar = new JToolBar();
                buttonsToolBar.setBorderPainted(false);
                buttonsToolBar.setFloatable(false);
                buttonsToolBar.setOpaque(false);
		JButton deleteBt = new JButton();
		deleteBt.setIcon(OrbisGISIcon.EDIT_CLEAR);
		deleteBt.setToolTipText(I18N.getString("orbisgis.org.orbisgis.Clear"));
		deleteBt.setBorderPainted(false);
		deleteBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				jTextArea.setText(null);
			}
		});
		buttonsToolBar.add(deleteBt);
		return buttonsToolBar;

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
			ErrorMessages.error(ErrorMessages.CannotAddErrorMessage, e);
		}
		jTextArea.setCaretPosition(jTextArea.getDocument().getLength());

		WorkbenchContext wbContext = Services
				.getService(WorkbenchContext.class);
		wbContext.getWorkbench().getFrame().showView(viewId);
	}
}
