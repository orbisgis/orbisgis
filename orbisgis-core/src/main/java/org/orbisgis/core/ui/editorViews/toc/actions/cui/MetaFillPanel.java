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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.fill.DensityFill;
import org.orbisgis.core.renderer.se.fill.DotMapFill;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.GraphicFill;
import org.orbisgis.core.renderer.se.fill.SolidFill;

/**
 * Meta-Panel for fill edition
 * This panel will provide the ability to select fill type
 *
 * @author maxence
 */
public class MetaFillPanel extends JPanel {

	private FillNode fillNode;

	private AbstractEditFillPanel currentPanel;

	private EditSolidFillPanel sfPanel;

	public MetaFillPanel(FillNode fNode) {
		super();
		this.fillNode = fNode;
		this.setBorder(BorderFactory.createTitledBorder("Fill"));

		Fill f = fNode.getFill();

		if (f == null){
			this.add(new EmptyPanel("No fill"));
			currentPanel = null;
		} else if(f instanceof SolidFill) {
			sfPanel = new EditSolidFillPanel((SolidFill) f);
			currentPanel = sfPanel;
		} else if (f instanceof GraphicFill) {
		} else if (f instanceof DensityFill) {
		} else if (f instanceof DotMapFill) {
		} else {
		}

		this.add((JPanel)currentPanel);
	}


}
