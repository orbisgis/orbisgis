/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
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