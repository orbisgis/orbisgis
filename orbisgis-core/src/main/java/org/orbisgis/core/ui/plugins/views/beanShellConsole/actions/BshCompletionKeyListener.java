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
package org.orbisgis.core.ui.plugins.views.beanShellConsole.actions;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.autocompletion.Completion;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.autocompletion.Option;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.ui.CompletionPopUp;

public class BshCompletionKeyListener extends KeyAdapter {

	private final static Logger logger = Logger
			.getLogger(BshCompletionKeyListener.class);

	private final boolean script;
	private JTextComponent txt;
	private CompletionPopUp pop;
	private Completion completion;

	public BshCompletionKeyListener(boolean script, JTextComponent txt) {
		this.txt = txt;
		this.script = script;
		try {
			completion = new Completion();
		} catch (LinkageError e) {
			Services.getService(ErrorManager.class).error(
					"Completion system cannot be initialized", e);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (completion == null) {
		}

		String originalText = txt.getText();
		if ((e.getKeyCode() == KeyEvent.VK_SPACE) && e.isControlDown()) {
			Point p = txt.getCaret().getMagicCaretPosition();
			try {
				Option[] list = completion.getOptions(originalText, txt
						.getCaretPosition(), script);
				showList(list, p);
			} catch (Exception e1) {
				logger.debug("Bug autocompleting", e1);
			}
		} else if ((e.getKeyCode() == KeyEvent.VK_S) && e.isControlDown()
				&& e.isShiftDown()) {
			try {
				SaveFilePanel sfp = new SaveFilePanel(null,
						"Save code completion test case");
				sfp.setCurrentDirectory(new File("/home/fergonco/"
						+ "ogworkspace/geocognition/orbisgis-core/"
						+ "src/test/resources/" + "org/orbisgis/javaManager/"));
				sfp.addFilter("compl", "completion file");
				if (UIFactory.showDialog(sfp)) {
					Option[] list = completion.getOptions(originalText, txt
							.getCaretPosition(), script);
					DataOutputStream dos = new DataOutputStream(
							new FileOutputStream(sfp.getSelectedFile()));
					StringBuffer sb = new StringBuffer();
					sb.append(txt.getCaretPosition());
					for (Option option : list) {
						sb.append(";").append(option.getAsString());
					}
					dos.write(sb.append("\n").toString().getBytes());
					String content = originalText;
					dos.write(content.getBytes());
					dos.close();
				}
			} catch (IOException e1) {
				Services.getErrorManager().error(
						"Cannot save code completion test case", e1);
			}
		}
	}

	private void showList(final Option[] list, Point p) {
		if (list.length > 0) {
			WorkbenchContext wbContext = Services
					.getService(WorkbenchContext.class);
			JFrame mainFrame = wbContext.getWorkbench().getFrame();
			pop = new CompletionPopUp(txt, list);
			pop.pack();

			// Place the pop up inside the frame so that it's a lightweight
			// component
			Point txtPoint = txt.getLocationOnScreen();
			Point frmPoint = mainFrame.getLocationOnScreen();
			int x1 = (txtPoint.x - frmPoint.x) + p.x;
			int y1 = (txtPoint.y - frmPoint.y) + p.y + 15;
			Dimension popSize = pop.getPreferredSize();
			int popWidth = popSize.width;
			int popHeight = popSize.height;
			if (txtPoint.y + p.y + popHeight + 15 > frmPoint.y
					+ mainFrame.getHeight()) {
				y1 = y1 - popHeight - 15;
			}
			if (txtPoint.x + p.x + popWidth > frmPoint.x + mainFrame.getWidth()) {
				x1 = x1 - popWidth;
			}
			// if (x1 < frmPoint.x) {
			// x1 = 0;
			// Dimension newDimension = new Dimension(
			// 3 * mainFrame.getWidth() / 4, popHeight);
			// pop.setPreferredSize(newDimension);
			// }
			// if (y1 < frmPoint.y) {
			// y1 = 0;
			// Dimension newDimension = new Dimension(popWidth, 3 * mainFrame
			// .getHeight() / 4);
			// pop.setPreferredSize(newDimension);
			// }
			//
			pop.show(mainFrame, x1, y1);
		}
	}
}