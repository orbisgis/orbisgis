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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JOptionPane;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.editors.map.MapEditor;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.map.export.Scale;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.renderer.Renderer;
import org.orbisgis.renderer.legend.Legend;
import org.sif.UIFactory;
import org.sif.UIPanel;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.vividsolutions.jts.geom.Envelope;

/**
 * 
 * Export a map in a  pdf. 
 * 
 */
public class ExportMapAsPDF implements IEditorAction {

	private MapContext mapContext;

	public void actionPerformed(IEditor editor) {

		MapEditor mapEditor = (MapEditor) editor;
		mapContext = (MapContext) editor.getElement().getObject();

		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.editors.map.actions.ExportMapAsPDF",
				"Choose a file format");
		outfilePanel.addFilter("pdf", "Portable Document Format (*.pdf)");

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
		}

	}

	public boolean isEnabled(IEditor editor) {
		MapContext mc = (MapContext) editor.getElement().getObject();

		return mc.getLayerModel().getLayerCount() >= 1;
	}

	public boolean isVisible(IEditor editor) {
		return true;
	}

	/**
	 * TODO take into account raster with nice resolution
	 * 
	 * @param layerPixelEnvelope
	 * @param envelope
	 * @param layer
	 */
	public void save(File outputFile, Scale scale, BufferedImage img,
			Envelope envelope, ILayer layer) {
		int width = img.getWidth();
		int height = img.getHeight();
		Document document = new Document(PageSize.A4.rotate());

		try {

			Renderer r = new Renderer();

			if (outputFile.getName().toLowerCase().endsWith("pdf")) {

				FileOutputStream fos = new FileOutputStream(outputFile);

				PdfWriter writer = PdfWriter.getInstance(document, fos);

				document.open();

				float pageWidth = document.getPageSize().getWidth();
				float pageHeight = document.getPageSize().getHeight();

				// Add the north
				final java.net.URL url = this.getClass().getResource(
						"simplenorth.png");

				Image northImage = Image.getInstance(url.toString());

				
				
				northImage.setAbsolutePosition(pageWidth
						- (northImage.getWidth() + 1), pageHeight
						- (northImage.getHeight() + 1));
				document.add(northImage);

				PdfContentByte cb = writer.getDirectContent();

				PdfTemplate tp = cb.createTemplate(pageWidth, pageHeight);

				Graphics2D g2d = tp.createGraphicsShapes(pageWidth, pageHeight);

				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

				r.draw(g2d, width, height, envelope, layer,
						new NullProgressMonitor());

				g2d.dispose();
				// Determine la position en x et Y
				// g2d.translate(50, 100);

				// g2d.draw(new Rectangle(50, 50));

				// g2d.dispose();

				ILayer[] layers = mapContext.getLayerModel()
						.getLayersRecursively();

				Graphics2D g2dlegend = tp.createGraphicsShapes(pageWidth,
						pageHeight);

				// g2d.translate(0, 50);
				int maxHeight = 0;
				for (int i = 0; i < layers.length; i++) {
					g2dlegend.translate(0, maxHeight + 10);
					maxHeight = 0;
					if (layers[i].isVisible()) {
						Legend[] legends = layers[i].getRenderingLegend();

						g2dlegend.drawString(layers[i].getName(), 0, 0);

						for (int j = 0; j < legends.length; j++) {
							Legend vectorLegend = legends[j];
							vectorLegend.drawImage(g2dlegend);
							int[] size = vectorLegend.getImageSize(g2dlegend);
							if (size[1] > maxHeight) {
								maxHeight = size[1];
							}
							g2dlegend.translate(0, size[0]);
						}

					}
				}

				g2dlegend.translate(0, maxHeight);
				// draw scale
				if (scale != null) {
					scale.drawScale(g2dlegend, 90);
				}

				g2dlegend.dispose();
				cb.addTemplate(tp, 0, 0);

				JOptionPane.showMessageDialog(null, "The file has been saved.");
			}

		} catch (FileNotFoundException e) {
			Services.getErrorManager().error("Cannot write on the disk", e);
		} catch (DocumentException e) {
			Services.getErrorManager().error("Cannot write the PDF", e);
		} catch (Exception e) {
			Services.getErrorManager().error("Cannot export in PDF", e);
		}

		document.close();

	}
}
