package org.gdms.data;

import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.FieldEditionEvent;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.edition.MultipleEditionEvent;

public class EditionListenerCounter implements EditionListener,
		MetadataEditionListener {

	public int total;

	public int deletions;

	public int insertions;

	public int modifications;

	public int fieldDeletions;

	public int fieldInsertions;

	public int fieldModifications;

	public int undoRedo;

	public void singleModification(EditionEvent e) {
		switch (e.getType()) {
		case EditionEvent.DELETE:
			deletions++;
			break;
		case EditionEvent.INSERT:
			insertions++;
			break;
		case EditionEvent.MODIFY:
			modifications++;
			break;
		}

		if (e.isUndoRedo()) {
			undoRedo++;
		}

		total++;
	}

	public void multipleModification(MultipleEditionEvent e) {
		EditionEvent[] events = e.getEvents();
		for (int i = 0; i < events.length; i++) {
			singleModification(events[i]);
		}
	}

	public void fieldAdded(FieldEditionEvent event) {
		fieldDeletions++;
		total++;
	}

	public void fieldRemoved(FieldEditionEvent event) {
		fieldInsertions++;
		total++;
	}

	public void fieldModified(FieldEditionEvent event) {
		fieldModifications++;
		total++;
	}
}