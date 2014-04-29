/**
 * TANATO is a library dedicated to the modelling of water pathways based on
 * triangulate irregular network. TANATO takes into account anthropogenic and
 * natural artifacts to evaluate their impacts on the watershed response.
 * It ables to compute watershed, main slope directions and water flow pathways.
 *
 * This library has been originally created by Erwan Bocher during his thesis
 * “Impacts des activités humaines sur le parcours des écoulements de surface dans
 * un bassin versant bocager : essai de modélisation spatiale. Application au
 * Bassin versant du Jaudy-Guindy-Bizien (France)”. It has been funded by the
 * Bassin versant du Jaudy-Guindy-Bizien and Syndicat d’Eau du Trégor.
 *
 * The new version is developed at French IRSTV institut as part of the
 * AvuPur project, funded by the French Agence Nationale de la Recherche
 * (ANR) under contract ANR-07-VULN-01.
 *
 * TANATO is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2010-2014 IRSTV FR CNRS 2488
 *
 * TANATO is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * TANATO is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * TANATO. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.gdms.sql.function.spatial.tin.model;

/**
 * This class contains only some constants that describe the names of the columns,
 * in the GDMS tables, that will be produced by the tanato GDMS functions and
 * custom queries.
 *
 * @author Erwan Bocher
 */
public final class TINSchema {

        /**
         * Field names used by TIN datasources
         */
        public static final String PROPERTY_FIELD = "property";
        public static final String WEIGHT_FIELD = "weight";
        public static final String HEIGHT_FIELD = "height";
        public static final String STARTPOINT_NODE_FIELD = "startp_n";
        public static final String ENDPOINT_NODE_FIELD = "endp_n";
        public static final String LEFT_TRIANGLE_FIELD = "left_t";
        public static final String RIGHT_TRIANGLE_FIELD = "right_t";
        public static final String GID_SOURCE_FIELD = "gid_source";
        public static final String EDGE_0_GID_FIELD = "edge0_gid";
        public static final String EDGE_1_GID_FIELD = "edge1_gid";
        public static final String EDGE_2_GID_FIELD = "edge2_gid";
        public static final String GID = "gid";
        public static final String GEOM_FIELD = "the_geom";

        /**
         * We don't want anyone to instantiate a TINSchema.
         */
        private TINSchema() {
        }
}
