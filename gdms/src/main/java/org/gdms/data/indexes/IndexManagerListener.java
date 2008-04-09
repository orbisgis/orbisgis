package org.gdms.data.indexes;

import org.orbisgis.IProgressMonitor;

public interface IndexManagerListener {

	void indexCreated(String source, String field, String indexId,
			IndexManager im, IProgressMonitor pm) throws IndexException;

	void indexDeleted(String source, String field, String indexId,
			IndexManager im) throws IndexException;
}
