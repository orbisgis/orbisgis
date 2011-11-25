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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.Symbolizer;

/**
 * The controller makes a copy of the style and mount UI to edit the copy
 *
 * @author maxence
 */
public final class LegendUIController {

	private int geometryType;
	private LegendUIMainPanel mainPanel;
	private Style style;
	// First index is for rules, second for symbolizer
	private ArrayList<ArrayList<LegendUIComponent>> rootPanels;
	private ArrayList<LegendUIRulePanel> rulePanels;
	private ArrayList<String> availableSymbolizers;
	private Dimension maxAllowedSize;

	/**
	 * @param fts the style to edit.
	 */
	public LegendUIController(Style fts) throws InvalidStyle {

		this.style = new Style(fts.getJAXBElement(), fts.getLayer());

		rootPanels = new ArrayList<ArrayList<LegendUIComponent>>();
		rulePanels = new ArrayList<LegendUIRulePanel>();

		availableSymbolizers = new ArrayList<String>();

		maxAllowedSize = new Dimension(1000, 750);

                Type type = null;

		try {
			ILayer layer = style.getLayer();
			type = layer.getDataSource().getMetadata().getFieldType(layer.getDataSource().getSpatialFieldIndex());
		} catch (DriverException ex) {
			Logger.getLogger(LegendUIController.class.getName()).log(Level.SEVERE, null, ex);
		}

		if (type == null) {
			geometryType = Type.GEOMETRY;
		} else {
			geometryType = type.getTypeCode();
		}

		switch (geometryType) {
			default:
			case Type.GEOMETRY:
			case Type.GEOMETRYCOLLECTION:
			case Type.POLYGON:
			case Type.MULTIPOLYGON:
				availableSymbolizers.add("Area Symbolizer");
			case Type.LINESTRING:
			case Type.MULTILINESTRING:
				availableSymbolizers.add("Line Symbolizer");
			case Type.POINT:
			case Type.MULTIPOINT:
				availableSymbolizers.add("Point Symbolizer");
		}

		availableSymbolizers.add("Text Symbolizer");


		int i;
		for (i = 0; i < style.getRules().size(); i++) {

			Rule rule = style.getRules().get(i);
			ArrayList<LegendUIComponent> sList = new ArrayList<LegendUIComponent>();
			rootPanels.add(sList);

			LegendUIRulePanel ruleP = new LegendUIRulePanel(rule);
			rulePanels.add(ruleP);

			for (Symbolizer s : rule.getCompositeSymbolizer().getSymbolizerList()) {
				// Create a node for each symbolizer
				LegendUISymbolizerPanel symbPanel = new LegendUISymbolizerPanel(this, null, s);
				sList.add(symbPanel);
				structureChanged(symbPanel, false);
			}
		}
		mainPanel = new LegendUIMainPanel(this, style);
	}

	public int getGeometryType() {
		return geometryType;
	}

	/**
	 *
	 * @return the new edited style
	 */
	public Style getEditedFeatureTypeStyle() {
		return style;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public ArrayList<LegendUIComponent> getSymbolizerPanels(int ruleID) {
		return rootPanels.get(ruleID);
	}

	public Rule createNewRule() {
		Rule r = new Rule(style.getLayer());
		style.addRule(r);

		ArrayList<LegendUIComponent> root = new ArrayList<LegendUIComponent>();
		rootPanels.add(root);

		LegendUIRulePanel rP = new LegendUIRulePanel(r);
		rulePanels.add(rP);

		for (Symbolizer s : r.getCompositeSymbolizer().getSymbolizerList()) {
			LegendUISymbolizerPanel newSymbPanel = new LegendUISymbolizerPanel(this, null, s);
			root.add(newSymbPanel);
			structureChanged(newSymbPanel, false);
		}

		return r;
	}

	public boolean deleteRule(int i) {
		boolean deleted = style.deleteRule(i);
		if (deleted) {
			rootPanels.remove(i);
			rulePanels.remove(i);
		}
		return deleted;
	}

	public void editRule(int i) {
		if (i >= 0 && i < rootPanels.size()) {

			for (LegendUIComponent c : rootPanels.get(i)) {
				structureChanged(c, false);
			}
			//Rule r = style.getRules().get(i);
			mainPanel.editRule(i);
		}else{
			mainPanel.editRule(-1);
		}
	}

	/*
	 * return true if the was moved, false otherwise
	 */
	boolean moveRuleUp(int i) {
		boolean moved = style.moveRuleUp(i);

		if (moved) {
			ArrayList<LegendUIComponent> comp = rootPanels.remove(i);
			rootPanels.add(i - 1, comp);

			LegendUIRulePanel rp = rulePanels.remove(i);
			rulePanels.add(i - 1, rp);
		}

		return moved;
	}

	boolean moveRuleDown(int i) {
		boolean moved = style.moveRuleDown(i);

		if (moved) {
			ArrayList<LegendUIComponent> comp = rootPanels.remove(i);
			rootPanels.add(i + 1, comp);

			LegendUIRulePanel rp = rulePanels.remove(i);
			rulePanels.add(i + 1, rp);
		}

		return moved;
	}

	private String getName() {
		return style.getName();
	}

	public LegendUIRulePanel getRulePanel(int ruleID) {
		return rulePanels.get(ruleID);
	}

	public ArrayList<LegendUIRulePanel> getRulePanels() {
		return rulePanels;
	}

	void editComponent(LegendUIComponent comp) {
		if (mainPanel != null) {
			mainPanel.editComponent(comp);
		}
	}

	void addSymbolizerToRule(int ruleID, Symbolizer s) {
		Rule r = style.getRules().get(ruleID);

		r.getCompositeSymbolizer().addSymbolizer(s);
		ArrayList<LegendUIComponent> rootPanel = rootPanels.get(ruleID);

		LegendUISymbolizerPanel newPanel = new LegendUISymbolizerPanel(this, null, s);

		rootPanel.add(newPanel);

		editRule(ruleID);
		structureChanged(newPanel, true);
		//editComponent(newPanel);
	}

	public ArrayList<String> getAvailableSymbolizerTypes() {
		return availableSymbolizers;
	}


	public void structureChanged(LegendUIComponent focudOn){
		this.structureChanged(focudOn, true);
	}

	private void structureChanged(LegendUIComponent focusOn, boolean updateUI) {
		//System.out.println("Structure Changed !");
		//Thread.dumpStack();



		LegendUIComponent topPanel = focusOn.getTopParent();
        if (topPanel != null) {
			//this.splitPanel(topPanel);
            topPanel.mountComponentForChildren();

		} else {
			System.out.println("Not able to found a root panel !");
		}

		if (mainPanel != null) {

			mainPanel.refreshTOC();
		}

		if (updateUI) {
		    LegendUIComponent panelToFocusOn = focusOn.getScopeParent();
			editComponent(panelToFocusOn);
		}
	}

	private LegendUIComponent findChildToDetach(LegendUIComponent parent) {
		ArrayList<LegendUIComponent> childrenQueue = new ArrayList<LegendUIComponent>();

		Iterator<LegendUIComponent> it = parent.getChildrenIterator();
		while (it.hasNext()) {
			LegendUIComponent next = it.next();
			if (next.isInlinedAndNotNull()) {
				System.out.println ("   queue child" + next);
				childrenQueue.add(next);
			}
		}

		LegendUIComponent toNest = null;
		Dimension size = parent.getSize();
		double totalArea = size.height * size.width;

		double bestRatio = 0;

		while (!childrenQueue.isEmpty()) {

			LegendUIComponent child = childrenQueue.remove(0);

			System.out.println ("   Process" + child);

			child.extractFromParent();

			parent.mountComponentForChildren();

			parent.pack();
			Dimension newParentSize = parent.getSize();

			child.mountComponentForChildren();

			Dimension childSize = child.getSize();
			child.pack();

			double ratio = totalArea
					/ (newParentSize.height * newParentSize.width
					+ childSize.height * childSize.width);


			if (ratio > bestRatio) {
				toNest = child;
				bestRatio = ratio;
			}

			// Queue child children
			it = child.getChildrenIterator();
			while (it.hasNext()) {
				LegendUIComponent next = it.next();
				if (next.isInlinedAndNotNull()) {
					childrenQueue.add(next);
					System.out.println ("     Queue.." + next);
				}
			}

			child.unnest();
			parent.mountComponentForChildren();
		}

		return toNest;
	}

	private void splitPanel(LegendUIComponent comp) {
		if (mainPanel != null) {
			System.out.println("SPLIT PANEL START");
			// Inline all children
			comp.unnestChildren();

			ArrayList<LegendUIComponent> mainQueue = new ArrayList<LegendUIComponent>();
			mainQueue.add(comp);


			mainPanel.setVisible(false);
			while (mainQueue.size() > 0) {
				LegendUIComponent current = mainQueue.remove(0);
				System.out.println ("Process : " + current);
				// Mount the panel
				current.mountComponentForChildren();
				current.pack();
				editComponent(current);

				Dimension totalSize = current.getSize();
				if (totalSize.height > maxAllowedSize.height /*|| totalSize.width > maxAllowedSize.width*/) {
					LegendUIComponent child = findChildToDetach(current);

					if (child != null) {
						System.out.println (" Nest: " + child);
						// Now queue the two panel
						child.extractFromParent();
						mainQueue.add(current);
						mainQueue.add(child);
					}
				}
			}
			mainPanel.setVisible(true);
		}
		System.out.println("SPLIT PANEL END");
	}

	void removeSymbolizerFromRule(LegendUISymbolizerPanel sPanel, int ruleID) {
		Rule rule = this.style.getRules().get(ruleID);
		Symbolizer symbolizer = sPanel.getSymbolizer();

		//sPanel.makeOrphan();
		sPanel.clear();

		for (LegendUIComponent root : rootPanels.get(ruleID)){
			if (root instanceof LegendUISymbolizerPanel){
				if (((LegendUISymbolizerPanel)root).getSymbolizer().equals(symbolizer)){
					rootPanels.get(ruleID).remove(root);
					break;
				}
			}
		}

		rule.getCompositeSymbolizer().removeSymbolizer(symbolizer);

		this.editRule(ruleID);
	}


	/**
	 *
	 */
	public void packMainPanel(){
		mainPanel.revalidate();
		JDialog dlg = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, mainPanel);
		if (dlg != null){
			dlg.pack();
			mainPanel.updateUI();
		}
	}


}
