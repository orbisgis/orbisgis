/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.editors.map.actions.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.editors.map.MapEditor;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.map.export.MapExportManager;
import org.orbisgis.map.export.Scale;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.progress.IProgressMonitor;
import org.sif.UIFactory;
import org.sif.UIPanel;

import com.vividsolutions.jts.geom.Envelope;

/**
 * 
 * Export a map in a pdf. Currently only vector data are taking into into
 * account.
 * 
 */
public class ExportMapAsSVG implements IEditorAction {

	public void actionPerformed(IEditor editor) {
		MapEditor mapEditor = (MapEditor) editor;
		MapContext mc = (MapContext) editor.getElement().getObject();

		Envelope envelope = mapEditor.getMapTransform().getAdjustedExtent();
		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.editors.map.actions.ExportMapAsPDF",
				"Choose a file format");
		outfilePanel.addFilter("svg", "Scalable Vector Graphics (*.svg)");

		ScaleEditor scaleEditor = new ScaleEditor(mapEditor.getMapTransform()
				.getScaleDenominator());
		UIPanel[] wizards = new UIPanel[] { scaleEditor, outfilePanel };
		if (UIFactory.showDialog(wizards)) {
			BackgroundManager bm = Services.getService(BackgroundManager.class);
			bm.backgroundOperation(new ExportJob(mc, envelope, outfilePanel
					.getSelectedFile(), scaleEditor.getScale()));
		}
	}

	public boolean isEnabled(IEditor editor) {
		MapContext mc = (MapContext) editor.getElement().getObject();
		return mc.getLayerModel().getLayerCount() >= 1;
	}

	public boolean isVisible(IEditor editor) {
		return true;
	}

	private class ExportJob implements BackgroundJob {

		private Envelope envelope;
		private MapContext mc;
		private File outputFile;
		private Scale scale;

		public ExportJob(MapContext mc, Envelope envelope, File outputFile, Scale scale) {
			this.mc = mc;
			this.envelope = envelope;
			this.outputFile = outputFile;
			this.scale = scale;
		}

		@Override
		public String getTaskName() {
			return "Exporting map";
		}

		@Override
		public void run(IProgressMonitor pm) {
			try {
				MapExportManager mem = Services
						.getService(MapExportManager.class);
				mem.exportSVG(mc, new FileOutputStream(outputFile), 500, 500,
						envelope, scale, pm);
			} catch (UnsupportedEncodingException e) {
				Services.getErrorManager().error("Cannot export", e);
			} catch (IllegalArgumentException e) {
				Services.getErrorManager().error("Cannot export", e);
			} catch (IOException e) {
				Services.getErrorManager().error(
						"Cannot write to the selected file", e);
			} catch (DriverException e) {
				Services.getErrorManager().error("Cannot access the map", e);
			}
		}

	}
}
