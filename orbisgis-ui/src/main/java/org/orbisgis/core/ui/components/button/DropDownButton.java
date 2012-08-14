/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.components.button;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public abstract class DropDownButton extends JToggleButton implements
		ChangeListener, PopupMenuListener, ActionListener {
	private final DropDownButton mainButton = this;
	private boolean popupVisible = false;
	private ImageIcon iconFile;
	private final static ImageIcon dropIconFile = OrbisGISIcon.BTN_DROPDOWN;

	public DropDownButton(ImageIcon iconFile) {
		this.iconFile = iconFile;
		setIcon(calculIcon());
		mainButton.getModel().addChangeListener(this);
		mainButton.addActionListener(this);
	}

	private ImageIcon calculIcon() {
		BufferedImage resultImage = null;
		Image firstImage = null;
		Image secondImage = null;
		firstImage = iconFile.getImage();
		secondImage = dropIconFile.getImage();

		int w = firstImage.getWidth(null) + secondImage.getWidth(null);
		int h = Math.max(firstImage.getHeight(null), secondImage
				.getHeight(null));
		resultImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resultImage.createGraphics();
		g2.drawImage(firstImage, 0, 0, null);
		g2.drawImage(secondImage, firstImage.getWidth(null), 0, null);
		return new ImageIcon(resultImage);
	}

	public void setIconFile(ImageIcon iconFile) {
		this.iconFile = iconFile;
		setIcon(calculIcon());
	}

	/*------------------------------[ ChangeListener ]---------------------------------------------------*/

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == mainButton.getModel()) {
			if (popupVisible && !mainButton.getModel().isRollover()) {
				mainButton.getModel().setRollover(true);
				return;
			}
		} else {
		}
	}

	/*------------------------------[ ActionListener ]---------------------------------------------------*/

	public void actionPerformed(ActionEvent ae) {
		JPopupMenu popup = getPopupMenu();
		popup.addPopupMenuListener(this);
		popup.show(mainButton, 0, mainButton.getHeight());
	}

	/*------------------------------[ PopupMenuListener ]---------------------------------------------------*/

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		popupVisible = true;
		mainButton.getModel().setRollover(true);
		mainButton.getModel().setSelected(true);
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		popupVisible = false;

		mainButton.getModel().setRollover(false);
		mainButton.getModel().setSelected(false);
		((JPopupMenu) e.getSource()).removePopupMenuListener(this);
	}

	public void popupMenuCanceled(PopupMenuEvent e) {
		popupVisible = false;
	}

	/*------------------------------[ Other Methods ]---------------------------------------------------*/

	protected abstract JPopupMenu getPopupMenu();

	public JToggleButton addToToolBar(JToolBar toolbar) {
		toolbar.add(mainButton);
		return mainButton;
	}
}
