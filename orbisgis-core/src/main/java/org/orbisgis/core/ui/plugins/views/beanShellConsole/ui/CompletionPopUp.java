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
package org.orbisgis.core.ui.plugins.views.beanShellConsole.ui;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.autocompletion.ImportOption;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.autocompletion.Option;

public class CompletionPopUp extends JPopupMenu {

	private JTextComponent text;
	private Option[] optionList;
	private JList lst;
	private boolean completed;
	private int startingPoint;
	private String prefix;

	public CompletionPopUp(JTextComponent text, Option[] list) {
		this.startingPoint = text.getCaretPosition();
		this.text = text;
		this.optionList = list;
		lst = new JList(optionList);
		completed = false;
		if (optionList.length == 1) {
			completed = true;
			lst.setSelectedIndex(0);
			completeSelected();
		} else if (optionList.length > 0) {
			prefix = list[0].getPrefix();
			lst.setSelectedIndex(0);
			SelectionListener listener = new SelectionListener();
			lst.addMouseListener(listener);
			lst.addKeyListener(listener);
			this.add(new JScrollPane(lst));
			this.setBorderPainted(false);

			lst.setCellRenderer(new CompletionRenderer());
		}
	}

	@Override
	public void show(Component invoker, int x, int y) {
		if (!completed) {
			super.show(invoker, x, y);
			lst.requestFocusInWindow();
		}
	}

	private class SelectionListener extends MouseAdapter implements KeyListener {

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				completeSelected();
			} else {
				try {
					boolean updateList = false;
					if ((e.getKeyCode() > KeyEvent.VK_0)
							&& (e.getKeyCode() < KeyEvent.VK_Z)) {
						String addedText = e.getKeyChar() + "";
						text.getDocument().insertString(
								text.getCaretPosition(), addedText, null);
						updateList = true;
					} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
						text.getDocument().remove(text.getCaretPosition() - 1,
								1);
						updateList = true;
					} else {
						switch (e.getKeyCode()) {
						case KeyEvent.VK_UP:
						case KeyEvent.VK_DOWN:
						case KeyEvent.VK_PAGE_DOWN:
						case KeyEvent.VK_PAGE_UP:
						case KeyEvent.VK_SHIFT:
							break;
						default:
							setVisible(false);
						}
					}
					if (text.getCaretPosition() < startingPoint) {
						setVisible(false);
					} else if (updateList) {
						ArrayList<Option> filtered = new ArrayList<Option>();
						for (Option option : optionList) {
							String newPrefix = prefix
									+ text.getText().substring(startingPoint,
											text.getCaretPosition());
							if (option.setPrefix(newPrefix)) {
								filtered.add(option);
							}
							lst.removeAll();
							lst.setListData(filtered.toArray(new Option[0]));
							if (filtered.size() > 0) {
								lst.setSelectedIndex(0);
							}
						}
					}
				} catch (BadLocationException e1) {
					// ignore
				}
			}
		}

		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				completeSelected();
			}
		}

		public void keyReleased(KeyEvent e) {
		}
	}

	private void completeSelected() {
		completed = true;
		setVisible(false);
		Option op = (Option) lst.getSelectedValue();
		if (op != null) {
			op.setCompletionCase(text.getText(), text.getCaretPosition());
			text.setText(op.getTransformedText());
			text.setCaretPosition(op.getCursorPosition());
			ImportOption[] imports = op.getImports();
			for (ImportOption importOption : imports) {
				importOption.setCompletionCase(text.getText(), text
						.getCaretPosition());
				text.setText(importOption.getTransformedText());
				text.setCaretPosition(importOption.getCursorPosition());
			}
		}
	}

}
