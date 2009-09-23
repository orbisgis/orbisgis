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
package org.orbisgis.core.ui.editors.map.actions.export;

import java.awt.BasicStroke;
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
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editor.action.IEditorAction;
import org.orbisgis.core.ui.editors.map.MapEditor;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.export.Scale;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;

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
 * Export map as pdf
 *
 */
public class ExportMapAsPDF implements IEditorAction {

	private MapContext mapContext;

	public void actionPerformed(IEditor editor) {

		MapEditor mapEditor = (MapEditor) editor;
		mapContext = (MapContext) editor.getElement().getObject();

		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.core.ui.editors.map.actions.ExportMapAsPDF",
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
			img = null;
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

				PdfContentByte cb = writer.getDirectContent();

				PdfTemplate templateMap = cb.createTemplate(pageWidth,
						pageHeight);

				PdfTemplate templateLegend = cb.createTemplate(150, pageHeight);

				PdfTemplate templateScale = cb.createTemplate(pageWidth, 50);


				Graphics2D g2dLegend = templateLegend.createGraphicsShapes(150,
						pageHeight);

				Graphics2D g2dMap = templateMap.createGraphicsShapes(pageWidth,
						pageHeight);


				Graphics2D g2dScale = templateScale.createGraphicsShapes(pageWidth,
						50);

				g2dMap.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

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
				g2dLegend.drawString("Legend", 0, 10);

				for (int i = 0; i < layers.length; i++) {
					g2dLegend.translate(0, maxHeight +10);
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
				g2dScale.drawRect(0, 0, (int)pageWidth, 50);

				g2dScale.setColor(Color.white);
				g2dScale.fillRect(0, 0, (int)pageWidth,  50);

				g2dScale.setColor(Color.BLACK);

				g2dScale.translate(30, 10);
				// draw scale
				if (scale != null) {
					scale.drawScale(g2dScale, 90);
				}

				BufferedImage image = ImageIO.read(url);


				AffineTransform tx = new AffineTransform();
			    tx.scale(0.5, 0.5);

			    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			    image = op.filter(image, null);

			    g2dScale.drawImage(image, 200, 0, null);

				g2dMap.dispose();
				g2dLegend.dispose();
				g2dScale.dispose();

				cb.addTemplate(templateMap, 0, 0);
				cb.addTemplate(templateLegend, 0, 0);
				cb.addTemplate(templateScale, 0, 0);

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
