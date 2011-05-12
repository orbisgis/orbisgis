/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.sql.customQuery.spatial.geometry.topology;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.DefaultAlphaQuery;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;

/**
 *
 * @author ebocher
 */
public class GraphModel implements DirectedGraph<Integer, Integer> {


        public static String EDGES_PATH="/tmp/edges.gdms";
        public static String NODES_PATH="/tmp/nodes.gdms";
        
        private final String sourceEdgesName;
        private String START_NODE = "start_node";
        private String END_NODE = "end_node";
        private String GID = "id";
        private final DataSourceFactory dsf;
        private final String sourceNodesName;
        private final IProgressMonitor pm;
        private DataSource edgesSDS = null;
        private DataSource nodesSDS = null;

        public GraphModel(String sourceNodesName, String sourceEdgesName, DataSourceFactory dsf, IProgressMonitor pm) throws NoSuchTableException, IndexException, DriverLoadException, DataSourceCreationException {
                this.sourceEdgesName = sourceEdgesName;
                this.sourceNodesName = sourceNodesName;
                this.pm = pm;
                this.dsf = dsf;
                init();
        }

        /**
         * Create all necessary indexes to improve queries on node and edge index
         */
        private void init() throws NoSuchTableException, IndexException, DriverLoadException, DataSourceCreationException {

                IndexManager indexManager = dsf.getIndexManager();

                if (!indexManager.isIndexed(sourceEdgesName, START_NODE)) {
                        indexManager.buildIndex(sourceEdgesName, START_NODE, pm);
                }
                if (!indexManager.isIndexed(sourceEdgesName, END_NODE)) {
                        indexManager.buildIndex(sourceEdgesName, END_NODE, pm);
                }

                if (!indexManager.isIndexed(sourceEdgesName, GID)) {
                        indexManager.buildIndex(sourceEdgesName, GID, pm);
                }

                if (!indexManager.isIndexed(sourceNodesName, GID)) {
                        indexManager.buildIndex(sourceNodesName, GID, pm);
                }

                edgesSDS=  dsf.getDataSource(sourceEdgesName);
                nodesSDS=  dsf.getDataSource(sourceEdgesName);
        }

        @Override
        public int inDegreeOf(Integer v) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Integer> incomingEdgesOf(Integer v) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int outDegreeOf(Integer v) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Integer> outgoingEdgesOf(Integer v) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Integer> getAllEdges(Integer v, Integer v1) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer getEdge(Integer v, Integer v1) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public EdgeFactory<Integer, Integer> getEdgeFactory() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer addEdge(Integer v, Integer v1) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addEdge(Integer v, Integer v1, Integer e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addVertex(Integer v) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean containsEdge(Integer v, Integer v1) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean containsEdge(Integer e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean containsVertex(Integer v) {
                try {
                        if (getElementIndex(nodesSDS, v) != -1) {
                                return true;
                        }
                        return false;
                } catch (DriverException ex) {
                        Logger.getLogger(GraphModel.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
                
        }

        @Override
        public Set<Integer> edgeSet() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Integer> edgesOf(Integer v) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeAllEdges(Collection<? extends Integer> clctn) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Integer> removeAllEdges(Integer v, Integer v1) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeAllVertices(Collection<? extends Integer> clctn) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer removeEdge(Integer v, Integer v1) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeEdge(Integer e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeVertex(Integer v) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Integer> vertexSet() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer getEdgeSource(Integer e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer getEdgeTarget(Integer e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public double getEdgeWeight(Integer e) {
                throw new UnsupportedOperationException("Not supported yet.");
        }


             // ----------------------------------------------------------------
        // PRIVATE METHODS
        // ----------------------------------------------------------------
        /**
         * Get an ilement from its GID
         * @param sds
         * @param theGID
         * @return
         * @throws DriverException
         */
        private long getElementIndex(DataSource ds, int theGID) throws DriverException {
                if (ds == null) {
                        // sds not set
                        return -1;
                } else {
                        DefaultAlphaQuery defaultAlphaQuery = new DefaultAlphaQuery(GID, ValueFactory.createValue(theGID));
                        Iterator<Integer> queryResult = ds.queryIndex(defaultAlphaQuery);
                        if (queryResult == null) {
                                return -1;
                        } else if (queryResult.hasNext()) {
                                return queryResult.next();
                        } else {
                                return -1;
                        }
                }
        }



        public static void main(String[] args) throws Exception {
                DataSourceFactory dsf = new DataSourceFactory();

                dsf.getSourceManager().register("graphEdges", new File(EDGES_PATH));
                dsf.getSourceManager().register("graphNodes", new File(NODES_PATH));

                Graph d_graph = new GraphModel("graphNodes", "graphEdges", dsf, new NullProgressMonitor());
                List chemin = DijkstraShortestPath.findPathBetween(d_graph, new Integer(0), new Integer(10));



        }
}
