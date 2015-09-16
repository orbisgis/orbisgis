/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

import org.orbisgis.orbistoolboxapi.annotations.model.Process
import org.orbisgis.orbistoolboxapi.annotations.input.GeoDataInput
import org.orbisgis.orbistoolboxapi.annotations.output.GeoDataOutput

import groovy.sql.Sql
/**
 * This example script show how to use the GeoData input and output.
 *
 * @author Sylvain PALOMINOS
 */

@GeoDataInput(
        title = "input geoData",
        abstrac = "Input GeoData"
)
String inputGeoData

@GeoDataOutput(
        title = "output geoData",
        abstrac = "Output GeoData"
)
String outputGeoData


@Process(
        title = "Extract first geom",
        abstrac = "Extract the first geom of the input"
)
def processing() {
    sql = Sql.newInstance(grv_ds)

    sql.execute("DROP TABLE IF EXISTS " + outputGeoData + ";")
    sql.execute("CREATE TABLE " + outputGeoData + " as SELECT THE_GEOM  FROM  SHAPETABLE LIMIT 1;")
}