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
package org.gdms.manual;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;

public class Writers {

	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.getSourceManager().register(
				"shape",
				new FileSourceDefinition(new File(
						"../../datas2tests/shp/bigshape2D/communes.shp")));

		DataSource sql = dsf.getDataSourceFromSQL("select * from shape");

		DataSourceDefinition target;
		boolean shape = false;
		if (shape) {
			target = new FileSourceDefinition(new File("output.shp"));
		} else {
			target = new DBTableSourceDefinition(new DBSource(null, 0,
					"/tmp/erwan/h2_1", null, null, "communes", "jdbc:h2"));
		}
		dsf.getSourceManager().register("output", target);
		dsf.saveContents("output", sql);
		DataSource ds1 = dsf
				.getDataSourceFromSQL("select the_geom from output");
		DataSource ds2 = dsf.getDataSourceFromSQL("select the_geom from shape");
		ds1.open();
		ds2.open();
		System.out.println(ds1.getAsString().equals(ds2.getAsString()));
		ds1.close();
		ds2.close();
	}
}
