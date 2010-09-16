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
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;

/**
 *
 * @author maxence
 */
public abstract class MetaColorParameterPanel extends JPanel {

	JButton changeType;
	JPanel currentPanel;
	JPanel editor;
	EditColorLiteralPanel literalPanel;
	//EditColorAttributePanel attributePanel;
	EditCategorizePanel categorizePanel;
	private static String[] availablePanels = {"Constant", "Classification"};

	public MetaColorParameterPanel(ColorParameter p) {
		super(new FlowLayout());
		editor = new JPanel();
		if (p == null) {
			currentPanel = null;
			editor.add(new EmptyPanel("no color"));

		} else {
			currentPanel = p.getEditionPanel(null);

			if (currentPanel instanceof EditColorLiteralPanel) {
				literalPanel = (EditColorLiteralPanel) currentPanel;
			} else if (currentPanel instanceof EditCategorizePanel) {
				categorizePanel = (EditCategorizePanel) currentPanel;
			}

			editor.add((JPanel) currentPanel);
		}
		this.add(editor);

		changeType = new JButton("ct");
		changeType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String s = (String) JOptionPane.showInputDialog(null,
						"Choose a new type", "Choose a new type",
						JOptionPane.PLAIN_MESSAGE, null,
						availablePanels, availablePanels[0]);

				switchTo(s);
			}
		});
		this.add(changeType);
	}

	private void switchTo(String type) {

		if (type != null) {
			ColorParameter colorP = null;
			/*if (type.equals(availablePanels[0])) {
				currentPanel = new EmptyPanel("no color");
			} else */
			if (type.equals(availablePanels[0])) {
				if (this.literalPanel == null) {
					colorP = new ColorLiteral();
					literalPanel = new EditColorLiteralPanel((ColorLiteral) colorP);
				} else {
					colorP = literalPanel.getColorParameter();
				}
				currentPanel = literalPanel;
			} else if (type.equals(availablePanels[1])) {
				if (categorizePanel == null) {
					colorP = new Categorize2Color(new ColorLiteral(), new ColorLiteral(), new RealAttribute());
					categorizePanel = new EditCategorizePanel((Categorize2Color) colorP);
				} else {
					colorP = categorizePanel.getColorParameter();
				}
				currentPanel = categorizePanel;
			}

			colorChanged(colorP);

			editor.removeAll();
			editor.add(currentPanel);
			editor.revalidate();

			JDialog dlg = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this);
			if (dlg != null) {
				dlg.pack();
				dlg.setLocation(0, 0);
			}
		}
	}

	public abstract void colorChanged(ColorParameter newColor);
}
