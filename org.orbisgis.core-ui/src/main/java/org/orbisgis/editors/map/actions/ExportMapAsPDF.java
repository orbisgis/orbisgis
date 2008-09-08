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
package org.orbisgis.editors.map.actions;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JOptionPane;

import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.editors.map.MapEditor;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.renderer.Renderer;
import org.orbisgis.renderer.legend.Legend;
import org.sif.UIFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.vividsolutions.jts.geom.Envelope;

/**
 * 
 * Export a map in a pdf. Currently only vector data are taking into into
 * account.
 * 
 */
public class ExportMapAsPDF implements IEditorAction {

	public void actionPerformed(IEditor editor) {
		MapEditor mapEditor = (MapEditor) editor;
		MapContext mc = (MapContext) editor.getElement().getObject();

		ILayer root = mc.getLayerModel();
		Envelope envelope = mapEditor.getMapTransform().getAdjustedExtent();

		BufferedImage img = mapEditor.getMapTransform().getImage();
		save(img, envelope, root);

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
	public void save(BufferedImage img, Envelope envelope, ILayer layer) {
		int width = img.getWidth();
		int height = img.getHeight();
		Document document = new Document(PageSize.A4.rotate());

		try {

			Renderer r = new Renderer();

			final SaveFilePanel outfilePanel = new SaveFilePanel(
					"org.orbisgis.editors.map.actions.ExportMapAsPDF",
					"Choose a file format");
			outfilePanel.addFilter("pdf", "Portable Document Format (*.pdf)");

			if (UIFactory.showDialog(outfilePanel)) {
				final File savedFile = new File(outfilePanel.getSelectedFile()
						.getAbsolutePath());

				if (savedFile.getName().toLowerCase().endsWith("pdf")) {

					FileOutputStream fos = new FileOutputStream(savedFile);

					PdfWriter writer = PdfWriter.getInstance(document, fos);

					document.open();

					PdfContentByte cb = writer.getDirectContent();

					PdfTemplate tp = cb.createTemplate(document.getPageSize()
							.getWidth(), document.getPageSize().getHeight());

					Graphics2D g2d = tp.createGraphicsShapes(width, height);

					r.draw(g2d, width, height, envelope, layer,
							new NullProgressMonitor());
					g2d.dispose();

					cb.addTemplate(tp, 0, height);
					PdfTemplate vectorTP = cb.createTemplate(width, height);

					Graphics2D vectorg2d = vectorTP.createGraphicsShapes(width,
							height);

					ILayer[] layers = layer.getLayersRecursively();

					for (int i = 0; i < layers.length; i++) {
						if (layers[i].isVisible()) {
							if (layers[i].isVectorial()) {
								Legend[] vectorLegends = layers[i]
										.getVectorLegend();
								java.awt.Font font = new java.awt.Font("arial",
										0, 12);
								vectorg2d.setFont(font);
								vectorg2d.drawString(layers[i].getName(), 0, 0);
								for (int j = 0; j < vectorLegends.length; j++) {
									Legend vectorLegend = vectorLegends[j];
									vectorLegend.drawImage(vectorg2d);
									int[] size = vectorLegend
											.getImageSize(vectorg2d);
									vectorg2d.translate(0, size[1]);

								}

							} else if (layers[i].isRaster()) {

								// TODO generate a image for LUT with min and
								// max
								/*
								 * RasterLegend[] rastersLegend =
								 * layers[i].getRasterLegend();
								 * 
								 * for (int j = 0; j < rastersLegend.length;
								 * j++) {
								 * 
								 * final LutDisplay lutDisplay = new
								 * LutDisplay(rastersLegend[j].getColorModel());
								 * 
								 * vectorg2d.drawImage(lutDisplay.getImagePlus().getImage(),0,0,
								 * null); }
								 */

							}
						}
					}
					vectorg2d.dispose();

					cb.addTemplate(vectorTP, 0, height);

					JOptionPane.showMessageDialog(null,
							"The file has been saved.");
				}

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
