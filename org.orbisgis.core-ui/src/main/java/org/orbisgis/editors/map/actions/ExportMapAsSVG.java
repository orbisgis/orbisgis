package org.orbisgis.editors.map.actions;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.JOptionPane;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
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
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.renderer.Renderer;
import org.orbisgis.views.documentCatalog.documents.MapDocument;
import org.sif.UIFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Envelope;

public class ExportMapAsSVG implements IEditorAction {

	ProgressMonitor pm = new ProgressMonitor("SVG export");

	public void actionPerformed(IEditor editor) {
		MapEditor mapEditor = (MapEditor) editor;
		MapDocument mapDocument = (MapDocument) editor.getDocument();
		MapContext mc = mapDocument.getMapContext();

		DataManager dataManager = (DataManager) Services
				.getService("org.orbisgis.DataManager");
		ILayer root = dataManager.createLayerCollection("root");

		ILayer[] allSelectedLayers = mc.getSelectedLayers();
		Envelope envelope = new Envelope();

		try {

			pm.startTask("Checking layers...");

			for (int i = 0; i < allSelectedLayers.length; i++) {
				Envelope env = allSelectedLayers[i].getEnvelope();
				if (env.intersects(mapEditor.getMapTransform().getExtent())) {
					envelope.expandToInclude(env);
				}
				
					root.addLayer(allSelectedLayers[i]);

					allSelectedLayers[i].open();
				

				
				pm.progressTo(100 - (100 * i) / allSelectedLayers.length);

			}
		} catch (LayerException e) {
			Services.getErrorManager().error("Cannot open the layer", e);
		}

		pm.endTask();
		

		Envelope intersectEnv = envelope.intersection(mapEditor
				.getMapTransform().getExtent());

		Envelope layerPixelEnvelope = mapEditor.getMapTransform().toPixel(
				intersectEnv);

		save(layerPixelEnvelope, envelope, root);

	}

	public boolean isEnabled(IEditor editor) {
		MapDocument map = (MapDocument) editor.getDocument();
		return map.getMapContext().getLayerModel().getLayerCount() > 0;
	}

	public boolean isVisible(IEditor editor) {
		return true;
	}

	public void save(Envelope layerPixelEnvelope, Envelope envelope,
			ILayer layer) {
		DOMImplementation domImpl = GenericDOMImplementation
				.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);

		// Create an instance of the SVG Generator.
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

		Renderer r = new Renderer();

		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.editors.map.actions.ExportMapAsSVG",
				"Choose a file format");
		outfilePanel.addFilter("svg", "Scalable Vector Graphics (*.svg)");

		// Finally, stream out SVG to the standard output using
		// UTF-8 encoding.
		boolean useCSS = true; // we want to use CSS style attributes

		try {
			if (UIFactory.showDialog(outfilePanel)) {
				final File savedFile = new File(outfilePanel.getSelectedFile()
						.getAbsolutePath());

				if (savedFile.getName().toLowerCase().endsWith("svg")) {

					r.draw(svgGenerator, (int) layerPixelEnvelope.getWidth(),
							(int) layerPixelEnvelope.getHeight(), envelope,
							layer, new NullProgressMonitor());
					
					
					FileOutputStream fos = new FileOutputStream(savedFile,
							false);
					Writer out = new OutputStreamWriter(fos, "UTF-8");
					svgGenerator.stream(out, useCSS);
					out.close();
					JOptionPane.showMessageDialog(null,
							"The file has been saved.");
				}

			}

		} catch (Exception e) {
			Services.getErrorManager().error("Cannot export in SVG", e);
		}
	}

}
