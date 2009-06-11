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
package org.gdms.data;

import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.FieldEditionEvent;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.edition.MultipleEditionEvent;

public class ListenerCounter implements EditionListener,
		MetadataEditionListener, DataSourceListener {

	public int total;

	public int deletions;

	public int insertions;

	public int modifications;

	public int fieldDeletions;

	public int fieldInsertions;

	public int fieldModifications;

	public int undoRedo;

	public int open;

	public int cancel;

	public int commit;

	public int resync;

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
		case EditionEvent.RESYNC:
			resync++;
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

	public void cancel(DataSource ds) {
		cancel++;
		total++;
	}

	public void commit(DataSource ds) {
		commit++;
		total++;
	}

	public void open(DataSource ds) {
		open++;
		total++;
	}
}