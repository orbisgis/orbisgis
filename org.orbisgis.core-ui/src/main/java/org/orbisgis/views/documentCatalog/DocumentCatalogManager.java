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
package org.orbisgis.views.documentCatalog;

/**
 * Service to manage the documents in the application
 *
 * @author Fernando Gonzalez Cortes
 */
public interface DocumentCatalogManager {

	/**
	 * Adds a listener to the events in the catalog, typically addition and
	 * removal of documents
	 *
	 * @param listener
	 */
	public void addDocumentCatalogListener(DocumentCatalogListener listener);

	/**
	 * Removes a listener to the events in the catalog
	 *
	 * @param listener
	 */
	public void removeDocumentCatalogListener(DocumentCatalogListener listener);

	/**
	 * Adds the document to the catalog and opens it in the editor by default
	 *
	 * @param document
	 */
	public void addDocument(IDocument document);

	/**
	 * Adds the specified document to the specified existing document in the
	 * catalog and opens it in the editor by default
	 *
	 * @param parent
	 *            Document where the document will be added
	 * @param document
	 *            Document to add
	 */
	public void addDocument(IDocument parent, IDocument document);

	/**
	 * Returns true if the document catalog is empty, false otherwise
	 *
	 * @return
	 */
	public boolean isEmpty();

	/**
	 * Opens the document using the default editor
	 *
	 * @param mapDocument
	 */
	public void openDocument(IDocument document);

}
