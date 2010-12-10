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

import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.stroke.LegendUIMetaStrokePanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.fill.LegendUIMetaFillPanel;
import javax.swing.BoxLayout;

import javax.swing.Icon;

import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.RasterSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.TextSymbolizer;
import org.orbisgis.core.renderer.se.VectorSymbolizer;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.TextInput;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.UomInput;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.graphic.LegendUICompositeGraphicPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;

/**
 *
 * @author maxence
 */
public class LegendUISymbolizerPanel extends LegendUIComponent {

	private final Symbolizer symbolizer;

	private LegendUIMetaFillPanel mFill;

	private LegendUIMetaStrokePanel mStroke;

	private LegendUIMetaRealPanel pOffset;

	private LegendUICompositeGraphicPanel gCollection;

	private TextInput nameInput;
	private UomInput uomInput;

	public LegendUISymbolizerPanel(LegendUIController controller, LegendUIComponent parent,
			final Symbolizer symb) {
		super(symb.getName(), controller, parent, 0, false);

		// A symbolizer UI always starts in a new panel !
		this.extractFromParent();

		this.symbolizer = symb;

		nameInput = new TextInput("Name", symbolizer.getName(), 30, false) {

			@Override
			protected void valueChanged(String s) {
				symbolizer.setName(s);
				LegendUISymbolizerPanel.this.setName(s);
				fireNameChanged();
			}
		};

		if (symb instanceof VectorSymbolizer) {
			uomInput = new UomInput((VectorSymbolizer) symbolizer);
			// Transform @todo
		}

		if (symb instanceof AreaSymbolizer) {
			mFill = new LegendUIMetaFillPanel(controller, this, (FillNode) symbolizer, true);
			mFill.init();

			mStroke = new LegendUIMetaStrokePanel(controller, this, (StrokeNode) symbolizer, true);
			mStroke.init();


			pOffset = new LegendUIMetaRealPanel("POffset", controller, this, ((AreaSymbolizer)symbolizer).getPerpendicularOffset(), true) {

				@Override
				public void realChanged(RealParameter newReal) {
					((AreaSymbolizer)symbolizer).setPerpendicularOffset(newReal);
				}
			};
			pOffset.init();

			//mStroke = new LegendUIMetaStrokePanel(controller, parent, (StrokeNode)symbolizer);
		} else if (symb instanceof LineSymbolizer) {
			mStroke = new LegendUIMetaStrokePanel(controller, this, (StrokeNode) symbolizer, false);
			mStroke.init();


			pOffset = new LegendUIMetaRealPanel("POffset", controller, this, ((LineSymbolizer)symbolizer).getPerpendicularOffset(), true) {

				@Override
				public void realChanged(RealParameter newReal) {
					((LineSymbolizer)symbolizer).setPerpendicularOffset(newReal);
				}
			};
			pOffset.init();

			//mStroke = new LegendUIMetaStrokePanel(controller, parent, (StrokeNode)symbolizer);
		} else if (symb instanceof PointSymbolizer) {
			//graphics = new LegendUIGraphicCollectionPanel(controller, parent, ((PointSymbolizer)symbolizer).getGraphicCollection());
			gCollection = new LegendUICompositeGraphicPanel(controller, this, ((PointSymbolizer) symb).getGraphicCollection());
		} else if (symb instanceof TextSymbolizer) {
		} else if (symb instanceof RasterSymbolizer) { // ??
		}
	}

	public Symbolizer getSymbolizer() {
		return symbolizer;
	}

	@Override
	public Icon getIcon() {
		Class cl = symbolizer.getClass();

		if (cl == AreaSymbolizer.class) {
			return OrbisGISIcon.LAYER_POLYGON;
		} else if (cl == LineSymbolizer.class) {
			return OrbisGISIcon.LAYER_LINE;
		} else if (cl == PointSymbolizer.class) {
			return OrbisGISIcon.LAYER_POINT;
		} else if (cl == TextSymbolizer.class) {
			return OrbisGISIcon.PENCIL;
		} else if (cl == RasterSymbolizer.class) {
			return OrbisGISIcon.LAYER_RGB;
		}

		return OrbisGISIcon.PENCIL;
	}

	@Override
	protected void mountComponent() {
		editor.removeAll();

		LegendUIAbstractPanel topBar = new LegendUIAbstractPanel(controller);
		topBar.setLayout(new BoxLayout(topBar, BoxLayout.X_AXIS));

		topBar.add(nameInput);


		LegendUIAbstractPanel symbEditor = new LegendUIAbstractPanel(controller);
		symbEditor.setLayout(new BoxLayout(symbEditor, BoxLayout.Y_AXIS));


		if (uomInput != null) {
			topBar.add(uomInput);
		}

		symbEditor.add(topBar);

		if (pOffset != null){
			symbEditor.add(pOffset);
		}

		if (mStroke != null) {
			symbEditor.add(mStroke);
		}

		if (mFill != null) {
			symbEditor.add(mFill);
		}

		if (gCollection != null) {
			symbEditor.add(gCollection);
		}

		editor.add(symbEditor);
	}

	@Override
	protected void turnOff() {
	}

	@Override
	protected void turnOn() {
	}

	@Override
	public Class getEditedClass() {
		return symbolizer.getClass();
	}

}
