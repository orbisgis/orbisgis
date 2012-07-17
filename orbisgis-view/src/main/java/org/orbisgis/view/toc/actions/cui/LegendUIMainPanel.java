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



package org.orbisgis.view.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;

/**
 *
 * @author maxence
 */
public final class LegendUIMainPanel extends JPanel  implements UIPanel  {
	private LegendUIController controller;

	private JPanel leftMenu;
	private JPanel editor;
	private JPanel footer;

	private JPanel overview;
	private LegendUIRuleListPanel rules;
	private LegendUITOCPanel tocPanel;

	private JButton apply;

	public LegendUIMainPanel (LegendUIController controller, Style fts){
		super(new BorderLayout());
		this.controller = controller;

		leftMenu = new JPanel(new BorderLayout());
		editor = new JPanel();
		//editor.setBorder(BorderFactory.createTitledBorder("Editor"));


		overview = new JPanel();
		overview.setBorder(BorderFactory.createTitledBorder("OverView"));
		overview.setSize(200, 200);

		rules = new LegendUIRuleListPanel(this.controller, this.controller.getRulePanels());

		tocPanel = new LegendUITOCPanel(this.controller);
		tocPanel.setBorder(BorderFactory.createTitledBorder("Symbolizers"));

		leftMenu.add(overview, BorderLayout.NORTH);
		leftMenu.add(rules, BorderLayout.CENTER);
		leftMenu.add(tocPanel, BorderLayout.SOUTH);

		this.add(leftMenu, BorderLayout.WEST);
		this.add(editor, BorderLayout.EAST);

		footer = new JPanel();
		apply = new JButton("apply");
		apply.addActionListener(new ActionListenerImpl());

		footer.add(apply);
		this.add(footer, BorderLayout.SOUTH);
		/*
		 * TODO: Switch to global style editor (i.e. symbolizer level editor)
		 */
		editRule(-1);
		//editor.add(new JLabel("Symbolizer order !"));
	}

	void refreshTOC(){
		tocPanel.refresh();
	}

	void editRule(int ruleID){
		tocPanel.refresh(ruleID);

		this.editor.removeAll();

		//rules.updateSelection(ruleID);
		if (ruleID >= 0){
			LegendUIRulePanel rPanel = controller.getRulePanel(ruleID);
			this.editor.add(rPanel);
		}else{
			this.editor.add(new LegendUISymbolizerLevelPanel(controller));
		}

		this.pack();
	}

	public void pack(){
		this.revalidate();

		JDialog dlg = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this);

		if (dlg != null){
			dlg.pack();
		}

		this.updateUI();
	}

	public void editComponent(LegendUIComponent comp){
		editor.removeAll();
		if (comp != null){
			editor.add(comp);
		}
		this.pack();
	}

	void setEditorTitle(String title){
		editor.setBorder(BorderFactory.createTitledBorder(title));
	}

	/**
	 * ?????????????????????
	 * @param i
	 */
	void selectRule(int i) {
		throw new UnsupportedOperationException("Not yet implemented");
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
	public Component getComponent() {
		return this;
	}

	@Override
	public String getInfoText() {
		return UIFactory.getDefaultOkMessage();
	}

      

	private class ActionListenerImpl implements ActionListener {

		public ActionListenerImpl() {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Style eFts = controller.getEditedFeatureTypeStyle();
				Style fts = new Style(eFts.getJAXBElement(), eFts.getLayer());
                                List<Style> styles = new ArrayList<Style>();
                                styles.add(fts);
				eFts.getLayer().setStyles(styles);
			} catch (InvalidStyle ex) {
				Logger.getLogger(LegendUIMainPanel.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
