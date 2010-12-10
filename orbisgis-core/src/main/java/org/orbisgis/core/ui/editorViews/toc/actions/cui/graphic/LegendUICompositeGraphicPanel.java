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



package org.orbisgis.core.ui.editorViews.toc.actions.cui.graphic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;

/**
 *
 * @author maxence
 */
public class LegendUICompositeGraphicPanel extends LegendUIComponent {

	private GraphicCollection gc;
	private LegendUIAbstractPanel left; // menu
	private LegendUIAbstractPanel right; // editor
	private LegendUIAbstractPanel tools;


	private final JList list;
    private DefaultListModel model;

	private JButton btnUp;
	private JButton btnDown;
	private JButton btnAdd;
	private JButton btnRm;


	private ArrayList<LegendUIMetaGraphicPanelImpl> graphics;

	private int currentGraphic;


	public LegendUICompositeGraphicPanel(LegendUIController ctrl, LegendUIComponent parent, GraphicCollection graphicCollection) {
		super("Graphic collection", ctrl, parent, 0, false);
		this.gc = graphicCollection;

		graphics = new ArrayList<LegendUIMetaGraphicPanelImpl>();

		model = new DefaultListModel();

		currentGraphic = -1;

		int i;
		for (i=0;i<gc.getNumGraphics();i++){
			Graphic graphic = gc.getGraphic(i);
			LegendUIMetaGraphicPanelImpl mPanel = new LegendUIMetaGraphicPanelImpl("Graphic", controller, this, graphic, i);
			graphics.add(mPanel);
			model.addElement(graphic);
		}

		list = new JList(model);
		list.setCellRenderer(new CellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		btnRm = new JButton(OrbisGISIcon.REMOVE);
		btnAdd = new JButton(OrbisGISIcon.ADD);
		btnDown = new JButton(OrbisGISIcon.GO_DOWN);
		btnUp = new JButton(OrbisGISIcon.GO_UP);


		btnRm.setMargin(new Insets(0, 0, 0, 0));
		btnAdd.setMargin(new Insets(0, 0, 0, 0));
		btnUp.setMargin(new Insets(0, 0, 0, 0));
		btnDown.setMargin(new Insets(0, 0, 0, 0));

		btnUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int i = list.getSelectedIndex();
				if (gc.moveGraphicUp(i)){
					// reflect the new order in the UI
					DefaultListModel model = (DefaultListModel) list.getModel();
					Object g = model.remove(i);
					model.add(i - 1, g);
					list.setSelectedIndex(i - 1);

					LegendUIMetaGraphicPanelImpl remove = graphics.remove(i);
					remove.setIndex(i-1);
					graphics.add(i-1, remove);
					currentGraphic = i-1;

					//displayGraphic(i-1);
				}
			}
		});

		btnDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int i = list.getSelectedIndex();
				if (gc.moveGraphicDown(i)){
					// reflect the new order in the UI
					DefaultListModel model = (DefaultListModel) list.getModel();
					Object g = model.remove(i);
					model.add(i + 1, g);
					list.setSelectedIndex(i + 1);

					LegendUIMetaGraphicPanelImpl remove = graphics.remove(i);
					remove.setIndex(i+1);
					graphics.add(i+1, remove);

					//displayGraphic(i+1);
					currentGraphic = i+1;
				}
			}
		});

		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MarkGraphic g = new MarkGraphic();
				g.setTo3mmCircle();

				gc.addGraphic(g);
				DefaultListModel model = (DefaultListModel) list.getModel();
				model.addElement(g);
				currentGraphic = list.getModel().getSize() -1;
				list.setSelectedIndex(currentGraphic);

				LegendUIMetaGraphicPanelImpl mPanel = new LegendUIMetaGraphicPanelImpl("Graphic",
						LegendUICompositeGraphicPanel.this.controller,
						LegendUICompositeGraphicPanel.this, g, currentGraphic);
				graphics.add(mPanel);

				displayGraphic(currentGraphic);
			}
		});

		btnRm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int showConfirmDialog = JOptionPane.showConfirmDialog(LegendUICompositeGraphicPanel.this.controller.getMainPanel(), "Sur ?", "Sur ?", JOptionPane.YES_NO_OPTION);

				if (showConfirmDialog == 0) {
					int i = list.getSelectedIndex();

					if (gc.delGraphic(i)){
						DefaultListModel model = (DefaultListModel) list.getModel();
						model.remove(i);
						list.setSelectedIndex(-1);
						graphics.remove(i);
						for (;i<graphics.size();i++){
							graphics.get(i).setIndex(i);
						}

						displayGraphic(-1);
					}
				}
			}
		});

		btnUp.setEnabled(false);
		btnDown.setEnabled(false);
		btnRm.setEnabled(false);


		left = new LegendUIAbstractPanel(controller);
		right = new LegendUIAbstractPanel(controller);
		tools = new LegendUIAbstractPanel(controller);
		tools.setLayout(new FlowLayout(FlowLayout.TRAILING));


		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				int index = list.locationToIndex(e.getPoint());
				list.setSelectedIndex(index);

				btnUp.setEnabled(false);
				btnDown.setEnabled(false);
				btnRm.setEnabled(false);

				if (index >= 0) {
					// First, update button status
					if (list.getModel().getSize() > 1){ //dont delete the last graphic
						btnRm.setEnabled(true);
					}

					if (index > 0) {
						// Turn the move down button on
						btnUp.setEnabled(true);
					}
					if (index < list.getModel().getSize() - 1) {
						btnDown.setEnabled(true);
					}

					displayGraphic(index);
				}
			}
		});
	}

	/**
	 *
	 */
	private void displayGraphic(int index){
		int i;
		this.currentGraphic = index;

		for (i=0;i<graphics.size();i++){
			graphics.get(i).makeOrphan();
		}

		try{
			LegendUIMetaGraphicPanelImpl g = graphics.get(currentGraphic);
			LegendUICompositeGraphicPanel.this.addChild(g);
			controller.structureChanged(g.getCurrentComponent());
		}
		catch (Exception e){
			controller.structureChanged(this);
		}
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.PALETTE;
	}

	@Override
	protected void mountComponent() {
		left.removeAll();
		left.add(list, BorderLayout.NORTH);

		tools.removeAll();

		tools.add(btnUp);
		tools.add(btnAdd);
		tools.add(btnRm);
		tools.add(btnDown);

		left.add(tools, BorderLayout.SOUTH);


		right.removeAll();
		if (currentGraphic >= 0){
			right.add(graphics.get(currentGraphic));
		}else{
			right.add(new JLabel("Please select one!"));
		}

		editor.add(left, BorderLayout.WEST);
		editor.add(right, BorderLayout.EAST);
	}

	@Override
	protected void turnOff() {
	}

	@Override
	protected void turnOn() {
	}

	private class LegendUIMetaGraphicPanelImpl extends LegendUIMetaGraphicPanel {

		private int index;

		public LegendUIMetaGraphicPanelImpl(String name, LegendUIController controller, LegendUIComponent parent, Graphic g, int i) {
			super(name, controller, parent, g, false);
			this.index = i;
			this.init();
		}

		@Override
		public void graphicChanged(Graphic newGraphic) {
			if (gc.delGraphic(index)){
				gc.addGraphic(newGraphic, index);
			}
		}

		public void setIndex(int index){
			this.index = index;
		}
	}

	private class CellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			Graphic g = (Graphic)value;

			this.setIcon(OrbisGISIcon.IMAGE);

			return this;
		}
	}

	@Override
	public Class getEditedClass() {
		return GraphicCollection.class;
	}


}
