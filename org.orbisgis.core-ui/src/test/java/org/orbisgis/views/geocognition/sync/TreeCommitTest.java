package org.orbisgis.views.geocognition.sync;

import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.views.geocognition.sync.tree.TreeElement;

public class TreeCommitTest extends AbstractSyncTest {
	public void testCommitAddedLeaf() {
		IdPath path;

		path = createPath("root:a:1:1:2");
		sm.commit(path);

		path = createPath("root:d:2");
		sm.commit(path);

		checkAddedLeaf();
	}

	public void testCommitAddedContainer() {
		IdPath path = createPath("root:a:1:1:2");
		IdPath path2 = createPath("root:a:1:1");

		sm.commit(path);
		sm.commit(path2);

		checkAddedContainer();
	}

	public void testCommitModifiedLeaf() {
		IdPath path;

		path = createPath("root:b");
		sm.commit(path);

		path = createPath("root:d:1");
		sm.commit(path);

		try {
			checkModifiedLeaf();
		} catch (GeocognitionException e) {
			fail();
		}
	}

	public void testCommitConflictLeaf() {
		IdPath path;

		path = createPath("root:e:1:1");
		sm.commit(path);

		path = createPath("root:d:4:1");
		sm.commit(path);

		checkConflict();
	}

	public void testCommitConflictContainer() {
		IdPath path;

		path = createPath("root:e:1");
		sm.commit(path);

		path = createPath("root:d:4");
		sm.commit(path);

		checkConflict();
	}

	public void testCommitDeletedLeaf() {
		IdPath path;

		path = createPath("root:c:1");
		sm.commit(path);

		path = createPath("root:d:3");
		sm.commit(path);

		checkDeletedLeaf();
	}

	public void testCommitDeletedContainer() {
		IdPath path;

		path = createPath("root:c");
		sm.commit(path);

		checkDeletedContainer();
	}

	public void testDeletedAfterAdded() {
		IdPath path;

		path = createPath("root:a");
		sm.commit(path);

		path = createPath("root:c");
		sm.commit(path);

		// Check boolean methods
		assertFalse(sm.isDeleted(createPath("root:c")));
		assertFalse(sm.isDeleted(createPath("root:c:1")));
		assertFalse(sm.isAdded(createPath("root:d")));
		assertFalse(sm.isAdded(createPath("root:d:1")));
		assertFalse(sm.isAdded(createPath("root:d:3")));
		assertFalse(sm.isAdded(createPath("root:d:4")));
		assertTrue(sm.isModified(createPath("root:d:1")));
		assertTrue(sm.isAdded(createPath("root:d:2")));
		assertTrue(sm.isDeleted(createPath("root:d:3")));
		assertTrue(sm.isConflict(createPath("root:d:4")));
		assertTrue(sm.isConflict(createPath("root:e:1")));

		// Check remote tree
		GeocognitionElement remote = sm.getRemoteRoot().getElement("root");
		GeocognitionElement aux = remote.getElement("root.c");
		assertNull(aux);
		aux = remote.getElement("root.d");
		assertNotNull(aux);
		GeocognitionElement aux2 = aux.getElement("root.d.1");
		assertNotNull(aux2);
		aux2 = aux.getElement("root.d.3");
		assertNotNull(aux2);
		aux2 = aux.getElement("root.d.4");
		assertNotNull(aux2);
		aux = remote.getElement("root.e");
		assertNotNull(aux);
		aux = remote.getElement("root.f");
		assertNotNull(aux);
	}

	public void testDuplicateId() {
		IdPath path;

		path = createPath("root:e");
		path.add("root.a");
		sm.commit(path);

		// Check boolean methods
		assertTrue(sm.isAdded(createPath("root:a")));
		assertFalse(sm.isAdded(createPath("root:e:root.a")));
		assertTrue(sm.isModified(createPath("root:b")));
		assertTrue(sm.isModified(createPath("root:d:1")));

		// Check remote tree
		GeocognitionElement remote = sm.getRemoteRoot().getElement("root");
		GeocognitionElement aux = remote.getElement("root.a");
		assertNull(aux);
		aux = remote.getElement("root.e");
		assertNotNull(aux);
		aux = aux.getElement("root.a");
		assertNotNull(aux);
	}

	private void checkAddedLeaf() {
		// Check boolean methods
		assertFalse(sm.isAdded(createPath("root:a")));
		assertFalse(sm.isAdded(createPath("root:a:1")));
		assertFalse(sm.isAdded(createPath("root:a:1:1")));
		assertFalse(sm.isAdded(createPath("root:a:1:1:2")));
		assertTrue(sm.isAdded(createPath("root:a:1:1:1")));
		assertTrue(sm.isAdded(createPath("root:a:1:1:3")));

		// Check remote tree
		GeocognitionElement remote = sm.getRemoteRoot().getElement("root");
		GeocognitionElement aux = remote.getElement("root.a");
		assertNotNull(aux);
		aux = aux.getElement("root.a.1");
		assertNotNull(aux);
		aux = aux.getElement("root.a.1.1");
		assertNotNull(aux);
		GeocognitionElement aux2 = aux.getElement("root.a.1.1.2");
		assertNotNull(aux2);
		aux2 = aux.getElement("root.a.1.1.1");
		assertNull(aux2);
		aux2 = aux.getElement("root.a.1.1.3");
		assertNull(aux2);

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

		// Check remote tree
		aux = remote.getElement("root.d");
		assertNotNull(aux);
		aux = aux.getElement("root.d.2");
		assertNotNull(aux);

		// Check merged tree
		tAux = merged.getElement("root.d");
		assertNotNull(tAux);
		tAux = tAux.getElement("root.d.2");
		assertNull(tAux);
	}

	private void checkAddedContainer() {
		// Check boolean methods
		assertFalse(sm.isAdded(createPath("root.a")));
		assertFalse(sm.isAdded(createPath("root.a.1")));
		assertFalse(sm.isAdded(createPath("root.a.1.1")));
		assertFalse(sm.isAdded(createPath("root.a.1.1.1")));
		assertFalse(sm.isAdded(createPath("root.a.1.1.2")));
		assertFalse(sm.isAdded(createPath("root.a.1.1.3")));

		// Check remote tree
		GeocognitionElement remote = sm.getRemoteRoot().getElement("root");
		GeocognitionElement aux = remote.getElement("root.a");
		assertNotNull(aux);
		aux = aux.getElement("root.a.1");
		assertNotNull(aux);
		aux = aux.getElement("root.a.1.1");
		assertNotNull(aux);
		GeocognitionElement aux2 = aux.getElement("root.a.1.1.2");
		assertNotNull(aux2);
		aux2 = aux.getElement("root.a.1.1.1");
		assertNotNull(aux2);
		aux2 = aux.getElement("root.a.1.1.3");
		assertNotNull(aux2);

		// Check merged tree
		TreeElement merged = sm.getDifferenceTree().getElement("root");
		TreeElement tAux = merged.getElement("root.a");
		assertNull(tAux);
	}

	private void checkModifiedLeaf() throws GeocognitionException {
		GeocognitionElement local = sm.getLocalRoot().getElement("root");
		GeocognitionElement remote = sm.getRemoteRoot().getElement("root");
		TreeElement merged = sm.getDifferenceTree().getElement("root");
		// Check boolean methods
		assertFalse(sm.isModified(createPath("root.b")));

		// Check trees
		GeocognitionElement remoteB = remote.getElement("root.b");
		GeocognitionElement localB = local.getElement("root.b");
		TreeElement mergedB = merged.getElement("root.b");
		assertTrue(remoteB.getXMLContent().equalsIgnoreCase(
				localB.getXMLContent()));
		assertNull(mergedB);

		// Check boolean methods
		assertFalse(sm.isModified(createPath("root.d")));
		assertFalse(sm.isModified(createPath("root.d.1")));

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

	private void checkConflict() {
		// Check boolean methods
		assertFalse(sm.isConflict(createPath("root.e.1")));
		assertFalse(sm.isConflict(createPath("root.e.1.1")));

		// Check remote tree
		GeocognitionElement remote = sm.getRemoteRoot().getElement("root");
		GeocognitionElement aux = remote.getElement("root.e");
		assertNotNull(aux);
		aux = aux.getElement("root.e.1");
		assertNotNull(aux);
		aux = aux.getElement("root.e.1.1");
		assertNotNull(aux);

		// Check merged tree
		TreeElement merged = sm.getDifferenceTree().getElement("root");
		TreeElement tAux = merged.getElement("root.e");
		assertNotNull(tAux);

		// Check boolean methods
		assertFalse(sm.isConflict(createPath("root.d.4")));
		assertFalse(sm.isConflict(createPath("root.d.4.1")));

		// Check remote tree
		aux = remote.getElement("root.d");
		assertNotNull(aux);
		aux = aux.getElement("root.d.4");
		assertNotNull(aux);
		aux = aux.getElement("root.d.4.1");
		assertNotNull(aux);

		// Check merged tree
		tAux = merged.getElement("root.d").getElement("root.d.4");
		assertNull(tAux);
	}

	private void checkDeletedLeaf() {
		// Check boolean methods
		assertTrue(sm.isDeleted(createPath("root:c")));
		assertFalse(sm.isDeleted(createPath("root:c:1")));

		// Check remote node
		GeocognitionElement remote = sm.getRemoteRoot().getElement("root");
		GeocognitionElement aux = remote.getElement("root.c");
		assertNotNull(aux);
		aux = aux.getElement("root.c.1");
		assertNull(aux);

		// Check merged node
		TreeElement merged = sm.getDifferenceTree().getElement("root");
		TreeElement tAux = merged.getElement("root.c");
		assertNotNull(tAux);
		tAux = tAux.getElement("root.c.1");
		assertNull(tAux);

		// Check boolean methods
		assertFalse(sm.isDeleted(createPath("root:d:3")));

		// Check remote node
		aux = remote.getElement("root.d");
		assertNotNull(aux);
		aux = aux.getElement("root.d.3");
		assertNull(aux);

		// Check merged node
		tAux = merged.getElement("root.d");
		assertNotNull(tAux);
		tAux = tAux.getElement("root.d.3");
		assertNull(tAux);
	}

	private void checkDeletedContainer() {
		// Check boolean methods
		assertFalse(sm.isDeleted(createPath("root:c")));
		assertFalse(sm.isDeleted(createPath("root:c:1")));

		// Check remote tree
		GeocognitionElement remote = sm.getRemoteRoot().getElement("root");
		GeocognitionElement aux = remote.getElement("root.c");
		assertNull(aux);

		// Check merged tree
		TreeElement merged = sm.getDifferenceTree().getElement("root");
		TreeElement tAux = merged.getElement("root.c");
		assertNull(tAux);
	}
}