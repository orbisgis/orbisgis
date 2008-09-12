package org.orbisgis.views.geocognition.sync;

import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.views.geocognition.sync.tree.TreeElement;

public class TreeUpdateTest extends AbstractSyncTest {
	public void testUpdateAddedLeaf() {
		IdPath path;

		path = createPath("root:a:1:1:2");
		sm.update(path);

		path = createPath("root:d:2");
		sm.update(path);

		checkAddedLeaf(sm);
	}

	public void testUpdateAddedContainer() {
		IdPath path = createPath("root:a:1:1:2");
		IdPath path2 = createPath("root:a:1:1");

		sm.update(path);
		sm.update(path2);

		checkAddedContainer(sm);
	}

	public void testUpdateModifiedLeaf() {
		IdPath path;

		path = createPath("root:b");
		sm.update(path);

		path = createPath("root:d:1");
		sm.update(path);

		try {
			checkLeafModification(sm);
		} catch (GeocognitionException e) {
			fail();
		}
	}

	public void testUpdateConflictLeaf() {
		IdPath path;

		path = createPath("root:e:1:1");
		sm.update(path);

		path = createPath("root:d:4:1");
		sm.update(path);

		checkConflict(sm);
	}

	public void testUpdateConflictContainer() {
		IdPath path;

		path = createPath("root:e:1");
		sm.update(path);

		path = createPath("root:d:4");
		sm.update(path);

		checkConflict(sm);
	}

	public void testUpdateDeletedLeaf() {
		IdPath path;

		path = createPath("root:c:1");
		sm.update(path);

		path = createPath("root:d:3");
		sm.update(path);

		checkDeletedLeaf(sm);
	}

	public void testUpdateDeletedContainer() {
		IdPath path;

		// Create path
		path = createPath("root:c");
		sm.update(path);

		checkDeletedContainer(sm);
	}

	public void testDuplicateId() {
		IdPath path;

		path = createPath("root:e");
		path.addLast("root.a");
		sm.update(path);

		// Check boolean methods
		assertTrue(sm.isAdded(createPath("root:a")));
		assertFalse(sm.isAdded(createPath("root:e:root.a")));
		assertTrue(sm.isModified(createPath("root:b")));
		assertTrue(sm.isModified(createPath("root:d:1")));

		// Check local tree
		GeocognitionElement local = sm.getLocalRoot().getElement("root");
		GeocognitionElement aux = local.getElement("root.a");
		assertNotNull(aux);
		aux = local.getElement("root.e");
		assertNotNull(aux);
		aux = aux.getElement("root.a");
		assertNull(aux);
	}

	private void checkAddedLeaf(SyncManager sm) {
		// Check boolean methods
		assertTrue(sm.isAdded(createPath("root:a")));
		assertTrue(sm.isAdded(createPath("root:a:1")));
		assertTrue(sm.isAdded(createPath("root:a:1:1")));
		assertTrue(sm.isAdded(createPath("root:a:1:1:1")));
		assertFalse(sm.isAdded(createPath("root:a:1:1:2")));
		assertTrue(sm.isAdded(createPath("root:a:1:1:3")));

		// Check local tree
		GeocognitionElement local = sm.getLocalRoot().getElement("root");
		GeocognitionElement aux = local.getElement("root.a");
		assertNotNull(aux);
		aux = aux.getElement("root.a.1");
		assertNotNull(aux);
		aux = aux.getElement("root.a.1.1");
		assertNotNull(aux);
		GeocognitionElement aux2 = aux.getElement("root.a.1.1.2");
		assertNull(aux2);
		aux2 = aux.getElement("root.a.1.1.1");
		assertNotNull(aux2);
		aux2 = aux.getElement("root.a.1.1.3");
		assertNotNull(aux2);

		// Check merged tree
		TreeElement merged = sm.getDifferenceTree().getElement("root");
		TreeElement tAux = merged.getElement("root.a");
		assertNotNull(tAux);
		tAux = tAux.getElement("root.a.1");
		assertNotNull(tAux);
		tAux = tAux.getElement("root.a.1.1");
		assertNotNull(tAux);
		TreeElement tAux2 = tAux.getElement("root.a.1.1.2");
		assertNull(tAux2);
		tAux2 = tAux.getElement("root.a.1.1.1");
		assertNotNull(tAux2);
		tAux2 = tAux.getElement("root.a.1.1.3");
		assertNotNull(tAux2);

		// Check boolean methods
		assertFalse(sm.isAdded(createPath("root:d")));
		assertFalse(sm.isAdded(createPath("root:d:2")));

		// Check local tree
		aux = local.getElement("root.d");
		assertNotNull(aux);
		aux = aux.getElement("root.d.2");
		assertNull(aux);

		// Check merged tree
		tAux = merged.getElement("root.d");
		assertNotNull(tAux);
		tAux = tAux.getElement("root.d.2");
		assertNull(tAux);
	}

	private void checkAddedContainer(SyncManager sm) {
		// Check boolean methods
		assertTrue(sm.isAdded(createPath("root:a")));
		assertTrue(sm.isAdded(createPath("root:a:1")));
		assertFalse(sm.isAdded(createPath("root:a:1:1")));
		assertFalse(sm.isAdded(createPath("root:a:1:1.1")));
		assertFalse(sm.isAdded(createPath("root:a:1:1:2")));
		assertFalse(sm.isAdded(createPath("root:a:1:1:3")));

		// Check local tree
		GeocognitionElement local = sm.getLocalRoot().getElement("root");
		GeocognitionElement aux = local.getElement("root.a");
		assertNotNull(aux);
		aux = aux.getElement("root.a.1");
		assertNotNull(aux);
		aux = aux.getElement("root.a.1.1");
		assertNull(aux);

		// Check merged tree
		TreeElement merged = sm.getDifferenceTree().getElement("root");
		TreeElement tAux = merged.getElement("root.a");
		assertNotNull(tAux);
		tAux = tAux.getElement("root.a.1");
		assertNotNull(tAux);
		tAux = tAux.getElement("root.a.1.1");
		assertNull(tAux);
	}

	private void checkLeafModification(SyncManager sm)
			throws GeocognitionException {
		GeocognitionElement local = sm.getLocalRoot().getElement("root");
		GeocognitionElement remote = sm.getRemoteRoot().getElement("root");
		TreeElement merged = sm.getDifferenceTree().getElement("root");
		// Check boolean methods
		assertFalse(sm.isModified(createPath("root:b")));

		// Check trees
		GeocognitionElement remoteB = remote.getElement("root.b");
		GeocognitionElement localB = local.getElement("root.b");
		TreeElement mergedB = merged.getElement("root.b");
		assertTrue(remoteB.getXMLContent().equalsIgnoreCase(
				localB.getXMLContent()));
		assertNull(mergedB);

		// Check boolean methods
		assertFalse(sm.isModified(createPath("root:d")));
		assertFalse(sm.isModified(createPath("root:d:1")));

		// Check trees
		GeocognitionElement remoteD = remote.getElement("root.d").getElement(
				"root.d.1");
		GeocognitionElement localD = local.getElement("root.d").getElement(
				"root.d.1");
		TreeElement mergedD = merged.getElement("root.d")
				.getElement("root.d.1");
		assertTrue(remoteD.getXMLContent().equalsIgnoreCase(
				localD.getXMLContent()));
		assertNull(mergedD);
	}

	private void checkConflict(SyncManager sm) {
		// Check boolean methods
		assertFalse(sm.isConflict(createPath("root:e:1")));
		assertFalse(sm.isConflict(createPath("root:e:1:1")));

		// Check local tree
		GeocognitionElement local = sm.getLocalRoot().getElement("root");
		GeocognitionElement aux = local.getElement("root.e");
		assertNotNull(aux);
		aux = aux.getElement("root.e.1");
		assertNotNull(aux);

		// Check merged tree
		TreeElement merged = sm.getDifferenceTree().getElement("root");
		TreeElement tAux = merged.getElement("root.e");
		assertNotNull(tAux);

		// Check boolean methods
		assertFalse(sm.isConflict(createPath("root:d:4")));
		assertFalse(sm.isConflict(createPath("root:d:4:1")));

		// Check local tree
		aux = local.getElement("root.d");
		assertNotNull(aux);
		aux = aux.getElement("root.d.4");
		assertNotNull(aux);

		// Check merged tree
		tAux = merged.getElement("root.d").getElement("root.d.4");
		assertNull(tAux);
	}

	private void checkDeletedLeaf(SyncManager sm) {
		// Check boolean methods
		assertFalse(sm.isDeleted(createPath("root:c")));
		assertFalse(sm.isDeleted(createPath("root:c:1")));
		assertFalse(sm.isAdded(createPath("root:c:1")));

		// Check local node
		GeocognitionElement local = sm.getLocalRoot().getElement("root");
		GeocognitionElement aux = local.getElement("root.c");
		assertNotNull(aux);
		aux = aux.getElement("root.c.1");
		assertNotNull(aux);

		// Check merged node
		TreeElement merged = sm.getDifferenceTree().getElement("root");
		TreeElement tAux = merged.getElement("root.c");
		assertNull(tAux);

		// Check boolean methods
		assertFalse(sm.isDeleted(createPath("root:d:3")));

		// Check local node
		aux = local.getElement("root.d");
		assertNotNull(aux);
		aux = aux.getElement("root.d.3");
		assertNotNull(aux);

		// Check merged node
		tAux = merged.getElement("root.d");
		assertNotNull(tAux);
		tAux = tAux.getElement("root.d.3");
		assertNull(tAux);
	}

	private void checkDeletedContainer(SyncManager sm) {
		// Check boolean methods
		assertFalse(sm.isDeleted(createPath("root:c")));
		assertFalse(sm.isDeleted(createPath("root:c:1")));

		// Check local tree
		GeocognitionElement local = sm.getLocalRoot().getElement("root");
		GeocognitionElement aux = local.getElement("root.c");
		assertNotNull(aux);
		aux = aux.getElement("root.c.1");
		assertNotNull(aux);

		// Check merged tree
		TreeElement merged = sm.getDifferenceTree();
		TreeElement tAux = merged.getElement("root.c");
		assertNull(tAux);
	}
}