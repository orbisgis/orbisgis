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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.export.Scale;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.editors.map.actions.export.ScaleEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;

public class ExportMapAsPDFPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		MapEditorPlugIn mapEditor = (MapEditorPlugIn) getPlugInContext()
				.getActiveEditor();
		MapContext mapContext = (MapContext) mapEditor.getElement().getObject();

		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.core.ui.editors.map.actions.ExportMapAsPDF", I18N //$NON-NLS-1$
						.getString("orbisgis.core.file.chooseFileFormat")); //$NON-NLS-1$
		outfilePanel.addFilter("pdf", "Portable Document Format (*.pdf)"); //$NON-NLS-1$ //$NON-NLS-2$

		ScaleEditor scaleEditor = new ScaleEditor(mapEditor.getMapTransform()
				.getScaleDenominator());
		UIPanel[] wizards = new UIPanel[] { scaleEditor, outfilePanel };
		if (UIFactory.showDialog(wizards)) {

			File outPutFile = outfilePanel.getSelectedFile();
			Scale scale = scaleEditor.getScale();
			ILayer root = mapContext.getLayerModel();
			Envelope envelope = mapEditor.getMapTransform().getAdjustedExtent();

			BufferedImage img = mapEditor.getMapTransform().getImage();

			save(outPutFile, scale, img, envelope, root);
			img = null;
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getMapEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_MAP_EXPORT_PDF },
				Names.POPUP_MAP_EXPORT_PDF_GROUP, false, null, wbContext);
	}

	public void save(File outputFile, Scale scale, BufferedImage img,
			Envelope envelope, ILayer layer) {
		int width = img.getWidth();
		int height = img.getHeight();
		Document document = new Document(PageSize.A4.rotate());
		EditorManager em = Services.getService(EditorManager.class);
		MapEditorPlugIn mapEditor = (MapEditorPlugIn) em.getActiveEditor();
		MapContext mapContext = (MapContext) mapEditor.getElement().getObject();

		try {

			Renderer r = new Renderer();

			if (outputFile.getName().toLowerCase().endsWith("pdf")) { //$NON-NLS-1$

				FileOutputStream fos = new FileOutputStream(outputFile);

				PdfWriter writer = PdfWriter.getInstance(document, fos);

				document.open();

				float pageWidth = document.getPageSize().getWidth();
				float pageHeight = document.getPageSize().getHeight();

				// Add the north
				final java.net.URL url = IconLoader
						.getIconUrl("simplenorth.png"); //$NON-NLS-1$

				PdfContentByte cb = writer.getDirectContent();

				PdfTemplate templateMap = cb.createTemplate(pageWidth,
						pageHeight);

				PdfTemplate templateLegend = cb.createTemplate(150, pageHeight);

				PdfTemplate templateScale = cb.createTemplate(pageWidth, 50);

				Graphics2D g2dLegend = templateLegend.createGraphicsShapes(150,
						pageHeight);

				Graphics2D g2dMap = templateMap.createGraphicsShapes(pageWidth,
						pageHeight);

				Graphics2D g2dScale = templateScale.createGraphicsShapes(
						pageWidth, 50);

				g2dMap.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

				r.draw(g2dMap, width, height, envelope, layer,
						new NullProgressMonitor());

				ILayer[] layers = mapContext.getLayerModel()
						.getLayersRecursively();

				g2dLegend.setColor(Color.BLACK);
				g2dLegend.drawRect(0, 0, 150, (int) pageHeight);

				g2dLegend.setColor(Color.white);
				g2dLegend.fillRect(0, 0, 150, (int) pageHeight);

				g2dLegend.setColor(Color.BLACK);
				int maxHeight = 30;

				g2dLegend.translate(10, 10);
				g2dLegend
						.drawString(
								I18N
										.getString("orbisgis.org.orbisgis.ui.map.exportMapAsPDFPlugIn.legend"), 0, 10); //$NON-NLS-1$

				for (int i = 0; i < layers.length; i++) {
					g2dLegend.translate(0, maxHeight + 10);
					maxHeight = 0;
					if (layers[i].isVisible()) {
						Legend[] legends = layers[i].getRenderingLegend();
						g2dLegend.drawString(layers[i].getName(), 0, 0);
						for (int j = 0; j < legends.length; j++) {
							Legend vectorLegend = legends[j];
							vectorLegend.drawImage(g2dLegend);
							int[] size = vectorLegend.getImageSize(g2dLegend);
							if (size[1] > maxHeight) {
								maxHeight = size[1];
							}
							g2dLegend.translate(0, 20);
						}
					}
				}

				g2dScale.translate(150, 0);
				g2dScale.setColor(Color.BLACK);
				g2dScale.drawRect(0, 0, (int) pageWidth, 50);

				g2dScale.setColor(Color.white);
				g2dScale.fillRect(0, 0, (int) pageWidth, 50);

				g2dScale.setColor(Color.BLACK);

				g2dScale.translate(30, 10);
				// draw scale
				if (scale != null) {
					scale.drawScale(g2dScale, 90);
				}

				BufferedImage image = ImageIO.read(url);

				AffineTransform tx = new AffineTransform();
				tx.scale(0.5, 0.5);

				AffineTransformOp op = new AffineTransformOp(tx,
						AffineTransformOp.TYPE_BILINEAR);
				image = op.filter(image, null);

				g2dScale.drawImage(image, 200, 0, null);

				g2dMap.dispose();
				g2dLegend.dispose();
				g2dScale.dispose();

				cb.addTemplate(templateMap, 0, 0);
				cb.addTemplate(templateLegend, 0, 0);
				cb.addTemplate(templateScale, 0, 0);

				JOptionPane.showMessageDialog(null, I18N
						.getString("orbisgis.core.file.fileSaved")); //$NON-NLS-1$
			}

		} catch (FileNotFoundException e) {
			ErrorMessages.error(ErrorMessages.CannotWriteOnDisk, e);
		} catch (DocumentException e) {
			ErrorMessages.error(ErrorMessages.CannotWritePDF, e);
		} catch (Exception e) {
			ErrorMessages.error(ErrorMessages.CannotWritePDF, e);
		}

		document.close();

	}

	public boolean isEnabled() {
		MapEditorPlugIn mapEditor = null;
		if ((mapEditor = getPlugInContext().getMapEditor()) != null) {
			MapContext mc = (MapContext) mapEditor.getElement().getObject();
			return mc.getLayerModel().getLayerCount() >= 1;
		}
		return false;
	}
}
