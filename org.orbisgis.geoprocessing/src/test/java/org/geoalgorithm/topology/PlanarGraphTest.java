package org.geoalgorithm.topology;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.geoalgorithm.orbisgis.topology.PlanarGraph;
import org.orbisgis.progress.NullProgressMonitor;

public class PlanarGraphTest {

	public static DataSourceFactory dsf = new DataSourceFactory();

	public static String path = "data/polygonfortopology.shp";

	/**
	 * @param args
	 * @throws DriverException
	 * @throws DataSourceCreationException
	 * @throws DriverLoadException
	 * @throws NonEditableDataSourceException
	 */

	public static void main(String[] args) throws DriverLoadException,
			DataSourceCreationException, DriverException, NonEditableDataSourceException {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);

		PlanarGraph planarGraph = new PlanarGraph();

		ObjectMemoryDriver edges = planarGraph.createEdges(sds, new NullProgressMonitor());

		ObjectMemoryDriver nodes = planarGraph.createNodes(edges);

		ObjectMemoryDriver faces = planarGraph.createFaces(edges);


		File gdmsFile = new File("/tmp/edges.shp");
		gdmsFile.delete();
		dsf.getSourceManager().register("edges", gdmsFile);

		DataSource ds = dsf.getDataSource(edges);
		ds.open();
		dsf.saveContents("edges", ds);
		ds.close();

		gdmsFile = new File("/tmp/nodes.shp");
		gdmsFile.delete();
		dsf.getSourceManager().register("nodes", gdmsFile);

		 ds = dsf.getDataSource(nodes);
		ds.open();
		dsf.saveContents("nodes", ds);
		ds.close();

		gdmsFile = new File("/tmp/faces.shp");
		gdmsFile.delete();
		dsf.getSourceManager().register("faces", gdmsFile);

		ds = dsf.getDataSource(faces);
		ds.open();
		dsf.saveContents("faces", ds);
		ds.close();

	}

}
