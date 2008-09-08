package org.orbisgis.views.geocognition.sync;

import java.util.ArrayList;

import org.orbisgis.views.geocognition.sync.tree.TreeElement;

public class TreeCompareTest extends AbstractSyncTest {

	public void testAdded() throws Exception {
		assertFalse(sm.isAdded(createPath("root")));
		assertTrue(sm.isAdded(createPath("root:a")));
		assertTrue(sm.isAdded(createPath("root:a:1")));
		assertTrue(sm.isAdded(createPath("root:a:1:1")));
		assertTrue(sm.isAdded(createPath("root:a:1:1:1")));
		assertTrue(sm.isAdded(createPath("root:a:1:1:2")));
		assertTrue(sm.isAdded(createPath("root:a:1:1:3")));
		assertFalse(sm.isAdded(createPath("root:b")));
		assertFalse(sm.isAdded(createPath("root:c")));
		assertFalse(sm.isAdded(createPath("root:c:1")));
		assertFalse(sm.isAdded(createPath("root:d")));
		assertFalse(sm.isAdded(createPath("root:d:1")));
		assertTrue(sm.isAdded(createPath("root:d:2")));
		assertFalse(sm.isAdded(createPath("root:d:3")));
		assertFalse(sm.isAdded(createPath("root:d:4")));
		assertFalse(sm.isAdded(createPath("root:d:4.1")));
		assertFalse(sm.isAdded(createPath("root:e")));
		assertFalse(sm.isAdded(createPath("root:e:1")));
		assertFalse(sm.isAdded(createPath("root:e:1:1")));
		ArrayList<String> path = createPath("root:e");
		path.add("root.a");
		assertTrue(sm.isAdded(path));
		assertFalse(sm.isAdded(createPath("root:f")));
	}

	public void testDeleted() throws Exception {
		assertFalse(sm.isDeleted(createPath("root")));
		assertFalse(sm.isDeleted(createPath("root:a")));
		assertFalse(sm.isDeleted(createPath("root:a:1")));
		assertFalse(sm.isDeleted(createPath("root:a:1:1")));
		assertFalse(sm.isDeleted(createPath("root:a:1:1:1")));
		assertFalse(sm.isDeleted(createPath("root:a:1:1:2")));
		assertFalse(sm.isDeleted(createPath("root:a:1:1.3")));
		assertFalse(sm.isDeleted(createPath("root:b")));
		assertTrue(sm.isDeleted(createPath("root:c")));
		assertTrue(sm.isDeleted(createPath("root:c:1")));
		assertFalse(sm.isDeleted(createPath("root:d")));
		assertFalse(sm.isDeleted(createPath("root:d:1")));
		assertFalse(sm.isDeleted(createPath("root:d:2")));
		assertTrue(sm.isDeleted(createPath("root:d:3")));
		assertFalse(sm.isDeleted(createPath("root:d:4")));
		assertFalse(sm.isDeleted(createPath("root:e")));
		assertFalse(sm.isDeleted(createPath("root:e:1")));
		assertFalse(sm.isDeleted(createPath("root:e:1:1")));
		ArrayList<String> path = createPath("root:e");
		path.add("root.a");
		assertFalse(sm.isDeleted(path));
		assertFalse(sm.isDeleted(createPath("root:f")));
	}

	public void testContentModified() throws Exception {
		assertFalse(sm.isModified(createPath("root")));
		assertFalse(sm.isModified(createPath("root:a")));
		assertFalse(sm.isModified(createPath("root:a:1")));
		assertFalse(sm.isModified(createPath("root:a:1:1")));
		assertFalse(sm.isModified(createPath("root:a:1:1:1")));
		assertFalse(sm.isModified(createPath("root:a:1:1:2")));
		assertFalse(sm.isModified(createPath("root:a:1:1.3")));
		assertTrue(sm.isModified(createPath("root:b")));
		assertFalse(sm.isModified(createPath("root:c")));
		assertFalse(sm.isModified(createPath("root:c:1")));
		assertFalse(sm.isModified(createPath("root:d")));
		assertTrue(sm.isModified(createPath("root:d:1")));
		assertFalse(sm.isModified(createPath("root:d:2")));
		assertFalse(sm.isModified(createPath("root:d:3")));
		assertFalse(sm.isModified(createPath("root:d:4")));
		assertFalse(sm.isModified(createPath("root:e")));
		assertFalse(sm.isModified(createPath("root:e:1")));
		assertFalse(sm.isModified(createPath("root:e:1:1")));
		ArrayList<String> path = createPath("root:e");
		path.add("root.a");
		assertFalse(sm.isModified(path));
		assertFalse(sm.isModified(createPath("root:f")));
	}

	public void testConflict() throws Exception {
		assertFalse(sm.isConflict(createPath("root")));
		assertFalse(sm.isConflict(createPath("root:a")));
		assertFalse(sm.isConflict(createPath("root:a:1")));
		assertFalse(sm.isConflict(createPath("root:a:1:1")));
		assertFalse(sm.isConflict(createPath("root:a:1:1:1")));
		assertFalse(sm.isConflict(createPath("root:a:1:1:2")));
		assertFalse(sm.isConflict(createPath("root:a:1:1.3")));
		assertFalse(sm.isConflict(createPath("root:b")));
		assertFalse(sm.isConflict(createPath("root:c")));
		assertFalse(sm.isConflict(createPath("root:c:1")));
		assertFalse(sm.isConflict(createPath("root:d")));
		assertFalse(sm.isConflict(createPath("root:d:1")));
		assertFalse(sm.isConflict(createPath("root:d:2")));
		assertFalse(sm.isConflict(createPath("root:d:3")));
		assertTrue(sm.isConflict(createPath("root:d:4")));
		assertFalse(sm.isConflict(createPath("root:e")));
		assertTrue(sm.isConflict(createPath("root:e:1")));
		assertTrue(sm.isConflict(createPath("root:e:1:1")));
		ArrayList<String> path = createPath("root:e");
		path.add("root.a");
		assertFalse(sm.isConflict(path));
		assertFalse(sm.isConflict(createPath("root:f")));
	}

	public void testMerged() throws Exception {
		// root
		TreeElement merged = sm.getDifferenceTree().getElement("root");
		assertTrue(merged.getElementCount() == 5);

		// a node
		TreeElement a = merged.getElement("root.a");
		assertNotNull(a);
		a = a.getElement("root.a.1");
		assertNotNull(a);
		a = a.getElement("root.a.1.1");
		assertNotNull(a);
		assertTrue(a.getElementCount() == 3);
		TreeElement a2 = a.getElement("root.a.1.1.1");
		assertNotNull(a2);
		a2 = a.getElement("root.a.1.1.2");
		assertNotNull(a2);
		a2 = a.getElement("root.a.1.1.3");
		assertNotNull(a2);

		// b node
		TreeElement b = merged.getElement("root.b");
		assertNotNull(b);

		// c node
		TreeElement c = merged.getElement("root.c");
		assertNotNull(c);
		assertNotNull(c.getElement("root.c.1"));
		assertTrue(c.getElementCount() == 1);

		// d node
		TreeElement d = merged.getElement("root.d");
		assertNotNull(d);
		assertTrue(d.getElementCount() == 4);
		TreeElement d2 = d.getElement("root.d.1");
		assertNotNull(d2);
		d2 = d.getElement("root.d.2");
		assertNotNull(d2);
		d2 = d.getElement("root.d.3");
		assertNotNull(d2);
		d2 = d.getElement("root.d.4");
		assertNotNull(d2);

		// e node
		TreeElement e = merged.getElement("root.e");
		assertNotNull(e);
		assertTrue(e.getElementCount() == 2);
		TreeElement e2 = e.getElement("root.e.1");
		assertNotNull(e2);
		e2 = e.getElement("root.a");
		assertNotNull(e2);

		// f node
		TreeElement f = merged.getElement("root.f");
		assertNull(f);
	}
}
