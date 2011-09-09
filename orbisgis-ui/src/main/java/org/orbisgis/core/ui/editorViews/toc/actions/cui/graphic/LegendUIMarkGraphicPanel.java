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
import javax.swing.Icon;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIHaloPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.UomInput;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.fill.LegendUIMetaFillPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.stroke.LegendUIMetaStrokePanel;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 *
 * @author maxence
 */
public class LegendUIMarkGraphicPanel extends LegendUIComponent implements LegendUIGraphicComponent {

	private MarkGraphic mark;
	private LegendUIMetaFillPanel mFill;
	private LegendUIMetaStrokePanel mStroke;
	private LegendUIViewBoxPanel vBox;
    private LegendUITransformPanel transform;
	private LegendUIMetaRealPanel pOffset;
	private LegendUIMetaMarkSource mSource;
	private LegendUIHaloPanel halo;
	private UomInput uomInput;

	public LegendUIMarkGraphicPanel(LegendUIController controller, LegendUIComponent parent, MarkGraphic m) {
		super("Mark graphic", controller, parent, 0, false);
		this.mark = m;

		this.uomInput = new UomInput(m);

		this.mFill = new LegendUIMetaFillPanel(controller, this, (FillNode) mark, true);
		this.mFill.init();

		this.mStroke = new LegendUIMetaStrokePanel(controller, this, (StrokeNode) mark, true);
		this.mStroke.init();

        this.transform = new LegendUITransformPanel(controller, this, m);

		this.pOffset = new LegendUIMetaRealPanel("POffset", controller, this, mark.getpOffset(), true) {

			@Override
			public void realChanged(RealParameter newReal) {
				mark.setPerpendicularOffset(newReal);
			}
		};
		this.pOffset.init();

		this.halo = new LegendUIHaloPanel(controller, this, mark.getHalo()) {

			@Override
			protected void haloChanged(Halo halo) {
				mark.setHalo(halo);
				controller.structureChanged(this);
			}
		};

		//mark.getHalo(); TODO

		this.vBox = new LegendUIViewBoxPanel(controller, this, mark.getViewBox(), true) {

			@Override
			public void viewBoxChanged(ViewBox newViewBox) {
				mark.setViewBox(viewbox);
				controller.structureChanged(this);
			}

		};

		this.mSource = new LegendUIMetaMarkSource(controller, this, mark);
		mSource.init();

	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.PALETTE;
	}

	@Override
	protected void mountComponent() {
		LegendUIAbstractPanel content1 = new LegendUIAbstractPanel(controller);
		LegendUIAbstractPanel content2 = new LegendUIAbstractPanel(controller);
		LegendUIAbstractPanel content3 = new LegendUIAbstractPanel(controller);
		LegendUIAbstractPanel content3a = new LegendUIAbstractPanel(controller);
		LegendUIAbstractPanel content3b = new LegendUIAbstractPanel(controller);

		content1.add(mSource, BorderLayout.WEST);
		content1.add(uomInput, BorderLayout.EAST);

		content2.add(vBox, BorderLayout.NORTH);
        content2.add(transform, BorderLayout.SOUTH);

		content3a.add(pOffset, BorderLayout.WEST);
		content3a.add(halo, BorderLayout.EAST);

		content3b.add(mStroke, BorderLayout.WEST);
		content3b.add(mFill, BorderLayout.EAST);

        content3.add(content3a, BorderLayout.NORTH);
        content3.add(content3b, BorderLayout.SOUTH);

		editor.add(content1, BorderLayout.NORTH);
		editor.add(content2, BorderLayout.CENTER);
		editor.add(content3, BorderLayout.SOUTH);
	}

	@Override
	public Graphic getGraphic() {
		return mark;
	}

	@Override
	public Class getEditedClass() {
		return MarkGraphic.class;
	}

	@Override
	protected void turnOff() {
		throw new UnsupportedOperationException("Unreachable code.");
	}

	@Override
	protected void turnOn() {
		throw new UnsupportedOperationException("Unreachable code.");
	}
}
