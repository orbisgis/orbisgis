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

package org.orbisgis.core.ui.plugins.editors.mapEditor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.editors.map.tool.Rectangle2DDouble;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;

public class ExportMapAsImagePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		MapEditorPlugIn mapEditor = (MapEditorPlugIn) getPlugInContext()
				.getActiveEditor();
		BufferedImage image = mapEditor.getMapTransform().getImage();
		MapContext mc = (MapContext) mapEditor.getElement().getObject();

		ILayer[] allSelectedLayers = mc.getLayers();
		Envelope envelope = new Envelope();

		for (int i = 0; i < allSelectedLayers.length; i++) {
			Envelope env = allSelectedLayers[i].getEnvelope();
			if (env.intersects(mapEditor.getMapTransform().getExtent())) {
				envelope.expandToInclude(env);
			}
		}

		Envelope intersectEnv = envelope.intersection(mapEditor
				.getMapTransform().getAdjustedExtent());

		Rectangle2DDouble layerPixelEnvelope = mapEditor.getMapTransform()
				.toPixel(intersectEnv);

		BufferedImage subImg = image.getSubimage((int) layerPixelEnvelope
				.getMinX(), (int) layerPixelEnvelope.getMinY(),
				(int) layerPixelEnvelope.getWidth(), (int) layerPixelEnvelope
						.getHeight());

		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.core.ui.editors.map.actions.ExportMapAsImage",
				I18N.getString("orbisgis.core.file.chooseFileFormat"));
		outfilePanel.addFilter("png", "Portable Network Graphics (*.png)");

		if (UIFactory.showDialog(outfilePanel)) {
			final File savedFile = new File(outfilePanel.getSelectedFile()
					.getAbsolutePath());

			try {
				ImageIO.write(subImg, "png", savedFile);

				JOptionPane.showMessageDialog(null, I18N
						.getString("orbisgis.core.file.fileSaved"));

			} catch (IOException e) {
				ErrorMessages.error(ErrorMessages.CannotWriteImage, e);
			}

		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		// TODO (pyf): trouver MapEditorPlugIn.getMapCOntrol.getToolManager ->
		// contient MenbuTree
		// & addPopupMenuItem ajoute à ce MenuTree le menu
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getMapEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_MAP_EXPORT_IMG },
				Names.POPUP_MAP_EXPORT_IMG_GROUP, false, null, wbContext);
	}

	public boolean isEnabled() {
		MapEditorPlugIn mapEditor = null;
		if ((mapEditor = getPlugInContext().getMapEditor()) != null) {
			MapContext map = (MapContext) mapEditor.getElement().getObject();
			return map.getLayerModel().getLayerCount() > 0;
		}
		return false;
	}
}
