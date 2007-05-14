package org.orbisgis.plugin.view.ui;

import javax.swing.JFrame;

import junit.framework.TestCase;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.MeshLayer;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.TINLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view.ui.TOC;

public class OurTreeModelTest extends TestCase {

	public void testTreeExploring() throws Exception {
		CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
		
		LayerCollection root = new LayerCollection("my root");
		VectorLayer vl1 = new VectorLayer("my 1st shapefile",crs);
		RasterLayer rl1 = new RasterLayer("my 1st tiff",crs);
		LayerCollection lc1 = new LayerCollection("my data");
		TINLayer tl1 = new TINLayer("my 1st TIN",crs);
		MeshLayer ml1 = new MeshLayer("my 1st Mesh",crs);
		lc1.put(tl1);
		lc1.put(ml1);
		root.put(vl1);
		root.put(rl1);
		root.put(lc1);

		TOC myTOC = new TOC(root);
		myTOC.expandRow(2);

		JFrame frame = new JFrame("OurTreeModelTest");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(myTOC);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		new OurTreeModelTest().testTreeExploring();
	}
}