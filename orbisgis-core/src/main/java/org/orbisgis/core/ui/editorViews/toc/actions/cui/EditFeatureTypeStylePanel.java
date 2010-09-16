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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.core.images.OrbisGISIcon;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.FeatureTypeStyle;
import org.orbisgis.core.renderer.se.PanelableNode;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;

/**
 *
 * @author maxence
 */
public final class EditFeatureTypeStylePanel extends JPanel implements UIPanel {

	private GeometryConstraint gc;
	private final int geometryType;
	private final FeatureTypeStyle typeStyle;
	private JPanel left;
	private JLabel overviewLbl;
	private BufferedImage overview;
	private JPanel rulesPanel;

	private JPanel symbolizersPanel;

	private JPanel editor;

	private JList rList;
	
	private JPanel rulesTools;

	private JButton ruleUp = new JButton(OrbisGISIcon.GO_UP);
	private JButton ruleDown = new JButton(OrbisGISIcon.GO_DOWN);
	private JButton ruleAdd = new JButton(OrbisGISIcon.ADD);
	private JButton ruleRm = new JButton(OrbisGISIcon.REMOVE);

	public EditFeatureTypeStylePanel(MapTransform mt, GeometryConstraint cons, FeatureTypeStyle fts) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.gc = cons;
		this.typeStyle = new FeatureTypeStyle(fts.getJAXBElement(), fts.getLayer());

		if (gc == null) {
			this.geometryType = ILegendPanel.ALL;
		} else {
			switch (gc.getGeometryType()) {
				case GeometryConstraint.POINT:
				case GeometryConstraint.MULTI_POINT:
					geometryType = ILegendPanel.POINT;
					break;
				case GeometryConstraint.LINESTRING:
				case GeometryConstraint.MULTI_LINESTRING:
					geometryType = ILegendPanel.LINE;
					break;
				case GeometryConstraint.POLYGON:
				case GeometryConstraint.MULTI_POLYGON:
					geometryType = ILegendPanel.POLYGON;
					break;
				default:
					geometryType = ILegendPanel.ALL;
					break;
			}
		}

		left = new JPanel(new BorderLayout());
		overview = null;
		overviewLbl = new JLabel("Overview");

		rulesPanel = new JPanel(new BorderLayout());

		DefaultListModel model = new DefaultListModel();
		for (Rule r : typeStyle.getRules()) {
			model.addElement(r);
		}

		rList = new JList(model);
		rList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rulesPanel.add(rList, BorderLayout.NORTH);
		rulesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));


		rulesTools = new JPanel();

		ruleUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int i = rList.getSelectedIndex();
				typeStyle.moveRuleUp(i);
				// reflect the new order in the UI
				DefaultListModel model = (DefaultListModel) rList.getModel();
				if (i > 0) {
					Rule r = (Rule) model.remove(i);
					model.add(i - 1, r);
					rList.setSelectedIndex(i - 1);
				}
			}
		});

		ruleDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int i = rList.getSelectedIndex();
				typeStyle.moveRuleDown(i);
				DefaultListModel model = (DefaultListModel) rList.getModel();
				if (i < model.getSize() - 1) {
					Rule r = (Rule) model.remove(i);
					model.add(i + 1, r);
					rList.setSelectedIndex(i + 1);
				}
			}
		});

		ruleAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Rule r = new Rule(typeStyle.getLayer());
				typeStyle.addRule(r);
				DefaultListModel model = (DefaultListModel) rList.getModel();
				model.addElement(r);
				rList.setSelectedIndex(rList.getModel().getSize() - 1);
			}
		});

		ruleRm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int showConfirmDialog = JOptionPane.showConfirmDialog(rulesPanel, "Sur ?", "Sur ?", JOptionPane.YES_NO_OPTION);

				System.out.println("REsult: " + showConfirmDialog);

				if (showConfirmDialog == 0) {
					int i = rList.getSelectedIndex();
					typeStyle.deleteRule(i);
					DefaultListModel model = (DefaultListModel) rList.getModel();
					model.remove(i);
					rList.setSelectedIndex(-1);
				}
			}
		});

		ruleUp.setEnabled(false);
		ruleDown.setEnabled(false);
		ruleRm.setEnabled(false);

		rulesTools.add(ruleUp);
		rulesTools.add(ruleAdd);
		rulesTools.add(ruleRm);
		rulesTools.add(ruleDown);


		rList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				int index = rList.locationToIndex(e.getPoint());
				rList.setSelectedIndex(index);

				ruleUp.setEnabled(false);
				ruleDown.setEnabled(false);
				ruleRm.setEnabled(false);

				if (index >= 0) {
					// First, update button status
					if (rList.getModel().getSize() > 1){ //dont delete the last rule
						ruleRm.setEnabled(true);
					}

					if (index > 0) {
						// Turn the move down button on
						ruleUp.setEnabled(true);
					}
					if (index < rList.getModel().getSize() - 1) {
						ruleDown.setEnabled(true);
					}
					// Then update symbolizer tree
					Rule sRule = (Rule) rList.getModel().getElementAt(index);

					// and finally put the EditRulePane in the editor panel
					editNode(sRule);
					setSymbolizerList(sRule);
				}
			}
		});

		rulesPanel.add(rulesTools, BorderLayout.SOUTH);

		symbolizersPanel = new JPanel();
		symbolizersPanel.setBorder(BorderFactory.createTitledBorder("Symbolizers"));
		this.setSymbolizerList(null);

		left.add(overviewLbl, BorderLayout.NORTH);
		left.add(rulesPanel, BorderLayout.CENTER);
		left.add(symbolizersPanel, BorderLayout.SOUTH);

		this.add(left);

		editor = new JPanel();

		this.add(editor);

		// According to geometry type, create a list of available symbolizers
	}

	public void setSymbolizerList(Rule r) {
		symbolizersPanel.removeAll();
		symbolizersPanel.add(new EditSymbolizerListPanel(this, r));
		symbolizersPanel.revalidate();
	}

	public void editNode(PanelableNode node){
		JPanel panel;
		try {
			panel = node.getEditionPanel(this);
		}
		catch (Exception ex){
			panel = new JPanel();
			panel.add(new JLabel("Could not load editor for " + node + "(" + node.getClass().getSimpleName() + ")"));
			ex.printStackTrace();
		}

		editor.removeAll();
		editor.add(panel);
		editor.revalidate();

		JDialog dlg = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this);
		if (dlg != null){
			dlg.pack();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

			if (screenSize.height < dlg.getHeight() || screenSize.width < dlg.getWidth()){
				// TODO Not enough to place the new panel !
			}

			//dlg.setLocation((int) ((screenSize.width - dlg.getWidth()) / 2.0), (int) ((screenSize.height - dlg.getHeight()) / 2.0));
			dlg.setLocation(0, 0);
		}
	}

	public FeatureTypeStyle getFeatureTypeStyle() {
		return typeStyle;
	}


	@Override
	public String validateInput() {
		return null;
	}

	@Override
	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

	@Override
	public String getTitle() {
		return "Legend Edition";
	}

	@Override
	public String initialize() {
		return null;
	}

	@Override
	public String postProcess() {
		return null;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getInfoText() {
		return UIFactory.getDefaultOkMessage();
	}

	public Object[] getAvailableSymbolizerList() {
		ArrayList<String> list = new ArrayList<String>();

		if (this.geometryType >= 1){
			list.add("Point Symbolizer");
		}

	    if (this.geometryType >= 2){
			list.add("Line Symbolizer");
		}

	    if (this.geometryType >= 4){
			list.add("Area Symbolizer");
		}

		list.add("Label");

		return list.toArray();
	}

	public String getDefaultType() {
		switch (this.geometryType){
			case 2:
				return "Line Symbolizer";
			case 4:
				return "Area Symbolizer";
			default:
				return "Point Symbolizer";
		}
	}

	void setEditorTitle(String name) {
		editor.setBorder(BorderFactory.createTitledBorder(name));
	}
}
