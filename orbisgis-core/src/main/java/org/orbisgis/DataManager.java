package org.orbisgis;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.IndexManager;
import org.gdms.source.SourceManager;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.persistence.LayerType;

public interface DataManager {

	/**
	 * Gets the DataSourceFactory of the system
	 *
	 * @return
	 */
	DataSourceFactory getDSF();

	/**
	 * Gets the manager of all the registered data sources
	 *
	 * @return
	 */
	SourceManager getSourceManager();

	/**
	 * Gets the index manager of the system
	 *
	 * @return
	 */
	IndexManager getIndexManager();

	/**
	 * Registers the specified definition in the system source manager. If the
	 * name already exists a different one is derived
	 *
	 * @param name
	 *            base name to register the source definition
	 * @param dsd
	 *            source definition
	 * @return The name actually used to register the definition
	 */
	String registerWithUniqueName(String name, DataSourceDefinition dsd);

	/**
	 * Creates a layer on the source which name is equal to the specified name
	 *
	 * @param sourceName
	 * @return
	 * @throws LayerException
	 *             if the layer could not be created
	 */
	ILayer createLayer(String sourceName) throws LayerException;

	/**
	 * Creates a layer that accesses the specified DataSource. The DataSource
	 * must have been obtained from the DataSourceFactory accessible by this
	 * interface
	 *
	 * @param dataSource
	 * @return
	 */
	ILayer createLayer(DataSource dataSource);

	/**
	 * Creates a layer collection with the specified name
	 *
	 * @param string
	 * @return
	 */
	ILayer createLayerCollection(String layerName);

	/**
	 * Creates a layer from the information obtained in the specified XML mapped
	 * object
	 *
	 * @param layer
	 * @return
	 * @throws LayerException
	 *             If the layer could not be created
	 */
	ILayer createLayer(LayerType layer) throws LayerException;

	/**
	 * Creates a layer on the specified file with the specified name. The file
	 * is added as a source in the source manager
	 *
	 * @param name
	 * @param file
	 * @return
	 * @throws LayerException
	 *             If the layer could not be created
	 */
	ILayer createLayer(String name, File file) throws LayerException;

	/**
	 * Creates a layer on the specified file with a random name. The file is
	 * added as a source in the source manager
	 *
	 * @param file
	 * @return
	 * @throws LayerException
	 *             If the layer could not be created
	 */
	ILayer createLayer(File file) throws LayerException;

}
