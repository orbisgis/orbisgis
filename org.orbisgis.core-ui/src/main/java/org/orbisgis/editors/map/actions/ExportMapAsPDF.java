package org.orbisgis.editors.map.actions;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.JOptionPane;

import org.gdms.driver.DriverException;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.editors.map.MapEditor;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.renderer.Renderer;
import org.orbisgis.views.documentCatalog.documents.MapDocument;
import org.sif.UIFactory;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
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
		MapDocument mapDocument = (MapDocument) editor.getDocument();
		MapContext mc = mapDocument.getMapContext();

		DataManager dataManager = (DataManager) Services
				.getService("org.orbisgis.DataManager");
		ILayer root = dataManager.createLayerCollection("root");

		ILayer[] allLayers = mc.getLayers();
		Envelope envelope = new Envelope();

		try {

			for (int i = 0; i < allLayers.length; i++) {

				if (allLayers[i].isVisible() && allLayers[i].isVectorial()) {
					Envelope env = allLayers[i].getEnvelope();
					if (env.intersects(mapEditor.getMapTransform().getExtent())) {
						envelope.expandToInclude(env);
					}
					allLayers[i].setLegend(allLayers[i].getVectorLegend());
					root.addLayer(allLayers[i]);
					allLayers[i].open();
				}

			}
		} catch (LayerException e) {
			Services.getErrorManager().error("Cannot open the layer", e);
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot open the layer", e);
		}

		Envelope intersectEnv = envelope.intersection(mapEditor
				.getMapTransform().getExtent());

		Envelope layerPixelEnvelope = mapEditor.getMapTransform().toPixel(
				intersectEnv);

		save(layerPixelEnvelope, envelope, root);

	}

	public boolean isEnabled(IEditor editor) {
		MapDocument map = (MapDocument) editor.getDocument();
		ILayer[] layers = map.getMapContext().getLayers();

		boolean atLeatOneVectorLayer = false;
		for (int i = 0; i < layers.length; i++) {
			try {
				if (layers[i].isVectorial() && layers[i].isVisible()) {
					atLeatOneVectorLayer = true;
				}
			} catch (DriverException e) {
				Services.getErrorManager().error("Cannot open the layer", e);
			}
		}

		return atLeatOneVectorLayer;
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
	public void save(Envelope layerPixelEnvelope, Envelope envelope,
			ILayer layer) {
		int width = 1000;
		int height = 1000;
		Document document = new Document(new Rectangle(width, height));

		try {

			Renderer r = new Renderer();

			final SaveFilePanel outfilePanel = new SaveFilePanel(
					"org.orbisgis.editors.map.actions.ExportMapAsPDF",
					"Choose a file format");
			outfilePanel.addFilter("pdf", "Scalable Vector Graphics (*.pdf)");

			if (UIFactory.showDialog(outfilePanel)) {
				final File savedFile = new File(outfilePanel.getSelectedFile()
						.getAbsolutePath());

				if (savedFile.getName().toLowerCase().endsWith("pdf")) {

					FileOutputStream fos = new FileOutputStream(savedFile);

					PdfWriter writer = PdfWriter.getInstance(document, fos);

					document.open();

					PdfContentByte cb = writer.getDirectContent();

					Graphics2D g2d = cb.createGraphicsShapes(width, height);

					r.draw(g2d, width, height, envelope, layer,
							new NullProgressMonitor());
					g2d.dispose();

					JOptionPane.showMessageDialog(null,
							"The file has been saved.");
				}

			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (DocumentException e1) {
			e1.printStackTrace();

		} catch (Exception e) {
			Services.getErrorManager().error("Cannot export in SVG", e);
		}

		document.close();

	}
}
