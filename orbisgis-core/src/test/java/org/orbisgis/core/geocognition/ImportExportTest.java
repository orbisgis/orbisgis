package org.orbisgis.core.geocognition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.layerModel.DefaultMapContext;

public class ImportExportTest extends AbstractGeocognitionTest {

	public void testExportRootDoesNotCreateContainer() throws Exception {
		gc.addElement("A", new DefaultMapContext());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		gc.write(bos, "/");
		GeocognitionElement imported = gc.createTree(new ByteArrayInputStream(
				bos.toByteArray()));
		imported.setId(gc.getRoot().getId());
		equals(gc.getRoot(), imported);
	}

	private void equals(GeocognitionElement root, GeocognitionElement imported) {
		assertTrue(root.getId().equals(imported.getId()));
		if (root.isFolder()) {
			assertTrue(root.getElementCount() == imported.getElementCount());
			for (int i = 0; i < root.getElementCount(); i++) {
				equals(root.getElement(i), imported.getElement(i));
			}
		} else {
			assertTrue(!imported.isFolder());
		}
	}

	public void testExportFolderDoesNotCreateContainer() throws Exception {
		gc.addFolder("A");
		gc.addElement("/A/Map", new DefaultMapContext());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		gc.write(bos, "/A");
		GeocognitionElement imported = gc.createTree(new ByteArrayInputStream(
				bos.toByteArray()));
		GeocognitionElement compareNode = gc.getGeocognitionElement("/A");
		imported.setId(compareNode.getId());
		equals(compareNode, imported);
	}

	public void testExportLeaveCreateContainer() throws Exception {
		gc.addFolder("A");
		gc.addElement("/A/Map", new DefaultMapContext());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		gc.write(bos, "/A/Map");
		GeocognitionElement imported = gc.createTree(new ByteArrayInputStream(
				bos.toByteArray()));
		GeocognitionElement compareNode = gc.getGeocognitionElement("/A");
		imported.setId(compareNode.getId());
		equals(compareNode, imported);
	}

}
