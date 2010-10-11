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

import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIType;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

/**
 *
 * @author maxence
 */
public abstract class LegendUIAbstractMetaPanel extends LegendUIComponent {

	private JButton changeType;
	private LegendUIType[] types;
	private LegendUIComponent[] comps;
	private LegendUIType currentType;
	private LegendUIComponent currentComp;

	public LegendUIAbstractMetaPanel(String name, LegendUIController controller, LegendUIComponent parent,
			float weight) {
		super(name, controller, parent, weight);

	}

	/**
	 *
	 * @param availableTypes
	 * @param initialType
	 * @param initialPanel
	 */
	protected void init(LegendUIType[] availableTypes, LegendUIType initialType, LegendUIComponent initialPanel) {
		types = availableTypes;
		comps = new LegendUIComponent[availableTypes.length];

		this.currentType = initialType;
		this.currentComp = initialPanel;

		this.setBorder(BorderFactory.createTitledBorder(""));

		int typeIndex = getTypeIndex(initialType);

		comps[typeIndex] = currentComp;

		switchTo(initialType, currentComp);
	}

	/**
	 * call right after create new instances !
	 */
	public abstract void init();

	/**
	 *
	 * @param type the new type of sub panel as selected by the user
	 * @param newActiveComp the corresponding UI component
	 */
	protected abstract void switchTo(LegendUIType type, LegendUIComponent newActiveComp);

	//protected abstract void changeType(Object newType);
	private void setTitle() {
		TitledBorder border = (TitledBorder) this.getBorder();
		border.setTitle(currentComp.getName());
	}

	private int getTypeIndex(LegendUIType type) {
		int i;
		for (i = 0; i < types.length; i++) {
			if (types[i].equals(type)) {
				return i;
			}
		}
		return -1;
	}

	private class ChangeTypeListener implements ActionListener {

		private final LegendUIComponent metaPanel;

		public ChangeTypeListener(LegendUIComponent metaPanel) {
			super();
			this.metaPanel = metaPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LegendUIType newType = (LegendUIType) JOptionPane.showInputDialog(null,
					"Choose a new type", "Choose a new type",
					JOptionPane.PLAIN_MESSAGE, null,
					types, currentType);

			int index = getTypeIndex(newType);

			if (index >= 0) {
				if (comps[index] == null) {
					comps[index] = newType.getUIComponent(LegendUIAbstractMetaPanel.this);
				}

				currentType = newType;

				// Update tree
				currentComp.makeOrphan();
				currentComp = comps[index];
				metaPanel.addChild(currentComp);

				switchTo(currentType, currentComp);
				controller.structureChanged(currentComp);
			}
		}
	}

	@Override
	protected void mountComponent() {
		//this.removeAll();
		//this.mountComponentForChildren();

		changeType = new JButton("ct");
		changeType.addActionListener(new ChangeTypeListener(this));
		this.add(changeType, BorderLayout.EAST);

		if (currentComp != null) {
			/*if (currentComp.isNested()) {
				LegendUILinkToComplexPanel link = new LegendUILinkToComplexPanel(controller, currentComp);
				this.add(link, BorderLayout.WEST);
			} else {
				this.add(currentComp, BorderLayout.WEST);
			}*/
			this.add(currentComp);

			setTitle();
		}else{
			this.add(new JLabel("Empty!"));
		}

	}

	public LegendUIComponent getCurrentComponent(){
		return this.currentComp;
	}
}
