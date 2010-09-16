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

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.TextSymbolizer;
import org.orbisgis.core.renderer.se.VectorSymbolizer;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.ComboBoxInput;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.RealLiteralInput;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.TextInput;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.UomInput;

/**
 *
 * Build a editor for a symbolizer
 * may create several new symbolizer children in the FtsPanel symbolizer tree !
 *
 * @author maxence
 */
public class EditSymbolizerPanel extends JPanel {

	private Symbolizer symbolizer;
	private EditFeatureTypeStylePanel ftsPanel;

	private VectorSymbolizer vSymb;
	private PointSymbolizer pSymb;
	private LineSymbolizer lSymb;
	private AreaSymbolizer aSymb;
	private TextSymbolizer tSymb;

	private TextInput nameInput;
	private ComboBoxInput uomInput;

	private RealLiteralInput pOffestInput;
	private MetaFillPanel fillPanel;
	private EditStrokePanel strokePanel;
	private EditGraphicCollectionPanel graphicPanel;

	public EditSymbolizerPanel(EditFeatureTypeStylePanel ftsPnl, Symbolizer s) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.symbolizer = s;
		this.ftsPanel = ftsPnl;
		updateBorder();



		// TODO symbolizer.getGeometry();
		// EDIT via all rules view : symbolizer.getLevel();
		nameInput = new TextInput("Name", symbolizer.getName(), 30) {

			@Override
			protected void valueChanged(String s) {
				symbolizer.setName(s);
				updateBorder();
			}
		};

		this.add(nameInput);
		vSymb = null;
		pSymb = null;
		lSymb = null;
		aSymb = null;
		tSymb = null;

		if (symbolizer instanceof VectorSymbolizer) {
			vSymb = (VectorSymbolizer) symbolizer;
			// TODO vSymb.setTransform();

			uomInput = new UomInput(vSymb);

			this.add(uomInput);


			if (vSymb instanceof PointSymbolizer){
				pSymb = (PointSymbolizer)vSymb;
				graphicPanel = new EditGraphicCollectionPanel(pSymb);
				this.add(graphicPanel);
				// Add EditGraphicPanel

			}else if (vSymb instanceof LineSymbolizer){
				lSymb = (LineSymbolizer)vSymb;

				strokePanel = new EditStrokePanel(lSymb);
				this.add(strokePanel);
				// Add stroke && pOffset
			}else if (vSymb instanceof AreaSymbolizer){
				aSymb = (AreaSymbolizer)vSymb;

				strokePanel = new EditStrokePanel(aSymb);
				this.add(strokePanel);

				fillPanel = new MetaFillPanel(aSymb);
				this.add(fillPanel);

				// Add EditStroke, EditFill && pOffest
			}else if (vSymb instanceof TextSymbolizer){
				tSymb = (TextSymbolizer)vSymb;
				// Add EditLabelPanel
			}else{

			}
		}
	}

	private void updateBorder() {
		this.ftsPanel.setEditorTitle("Symbolizer " + symbolizer.getName());
	}

}
