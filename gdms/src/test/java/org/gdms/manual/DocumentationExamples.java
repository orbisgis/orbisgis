/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.manual;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.WarningListener;
import org.gdms.data.db.DBSource;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.spatial.SpatialDataSourceDecorator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class DocumentationExamples extends TestCase {

	private File dbfFile = new File("/tmp/mydbf.dbf");
	private File csvFile = new File("/tmp/mycsv.csv");
	private DataSourceFactory dsf;

	public void testConnectPostgreSQL() throws Exception {
		// Create the factory
		dsf = new DataSourceFactory();

		// Create the information of the source we want to connect to
		DBSource dbSource = new DBSource("127.0.0.1", 5432, "gdms", "postgres",
				"postgres", "gisapps", "jdbc:postgresql");

		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(dbSource);

		// Connect to the database
		ds.open();

		// Show the field "id" in the first row
		System.out.println(ds.getInt(0, "id"));

		// Close without saving the changes. Indeed we did no changes!
		ds.cancel();
	}

	public void testCreateCSV() throws Exception {
		// Create the factory
		dsf = new DataSourceFactory();

		// Define the schema of the dbf to be created
		DefaultMetadata metadata = new DefaultMetadata();

		// Add a String field
		metadata.addField("name", Type.STRING);

		// Add a second string field
		metadata.addField("surname", Type.STRING);

		// Specify the location. Delete it just in case...
		csvFile = new File("/tmp/mycsv.csv");
		csvFile.delete();

		// GO!
		FileSourceCreation fileSourceCreation = new FileSourceCreation(csvFile,
				metadata);
		dsf.createDataSource(fileSourceCreation);

		// Read it!
		DataSource ds = dsf.getDataSource(csvFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();
	}

	public void testLoadCSV() throws Exception {
		// Create the factory
		dsf = new DataSourceFactory();

		// Create the information of the source we want to read
		File file = new File("src/test/resources/test.csv");

		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(file);

		// Open the file
		ds.open();

		// Show the field "id" in the first row
		System.out.println(ds.getString(0, "id"));

		// Close without saving the changes. Indeed we did no changes!
		ds.cancel();
	}

	public void testAddValues() throws Exception {
		// Create an empty csv
		testCreateCSV();

		// Get it
		DataSource ds = dsf.getDataSource(csvFile);
		ds.open();

		// Insert a row
		ds.insertEmptyRow();

		// populate it
		ds.setString(0, "name", "micky");
		ds.setString(0, "surname", "mouse");

		// SAVE MY RESULTS PLEASE!!
		ds.commit();

		// Read it!
		ds = dsf.getDataSource(dbfFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();
	}

	public void testModifyCSV() throws Exception {
		testAddValues();

		// Create the information of the source we want to read
		File file = csvFile;

		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(file);

		// Open the file
		ds.open();

		// Modify second field in first row
		ds.setString(0, 1, "new string");

		// Close without saving the changes. Indeed we did no changes!
		ds.cancel();
	}

	public void testCreateDBF() throws Exception {
		// Create the factory
		dsf = new DataSourceFactory();

		// Define the schema of the dbf to be created
		DefaultMetadata metadata = new DefaultMetadata();

		// Create an integer field
		{// bracket to indent for documentation purposes only

			// DBF numeric fields need some constraints
			Constraint lengthConstraint = ConstraintFactory.createConstraint(
					ConstraintNames.LENGTH, "5");
			Constraint precisionConstraint = ConstraintFactory
					.createConstraint(ConstraintNames.PRECISION, "0");

			// Add the integer field
			metadata.addField("id", Type.INT, new Constraint[] {
					lengthConstraint, precisionConstraint });
		}

		// Create a String field
		{// bracket to indent for documentation purposes only

			// Define the length constraint
			Constraint stringLengthConstraint = ConstraintFactory
					.createConstraint(ConstraintNames.LENGTH, "15");

			// Add the string field
			metadata.addField("name", Type.STRING,
					new Constraint[] { stringLengthConstraint });
		}

		// Specify the location. Delete it just in case...
		dbfFile = new File("/tmp/mydbf.dbf");
		dbfFile.delete();

		// GO!
		FileSourceCreation fileSourceCreation = new FileSourceCreation(dbfFile,
				metadata);
		dsf.createDataSource(fileSourceCreation);

		// Read it!
		DataSource ds = dsf.getDataSource(dbfFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();
	}

	public void testCreateAndWriteShapefile() throws Exception {
		// Create the factory
		dsf = new DataSourceFactory();

		// Define the schema of the dbf to be created
		DefaultMetadata metadata = new DefaultMetadata();

		// Create an integer field
		{// bracket to indent for documentation purposes only

			// DBF numeric fields need some constraints
			Constraint lengthConstraint = ConstraintFactory.createConstraint(
					ConstraintNames.LENGTH, "5");
			Constraint precisionConstraint = ConstraintFactory
					.createConstraint(ConstraintNames.PRECISION, "0");

			// Add the integer field
			metadata.addField("id", Type.INT, new Constraint[] {
					lengthConstraint, precisionConstraint });
		}

		// Create a String field
		{// bracket to indent for documentation purposes only

			// Define the length constraint
			Constraint stringLengthConstraint = ConstraintFactory
					.createConstraint(ConstraintNames.LENGTH, "15");

			// Add the string field
			metadata.addField("name", Type.STRING,
					new Constraint[] { stringLengthConstraint });
		}

		// Create a spatial field
		{
			// Define the length constraint
			Constraint geometryTypeConstraint = new GeometryConstraint(GeometryConstraint.MULTI_POINT_2D);

			// Add the geometry. name is ignored in shapefiles
			metadata.addField("the_geom", Type.GEOMETRY,
					new Constraint[] { geometryTypeConstraint });
		}

		// Specify the location. Delete it just in case...
		File shpFile = new File("/tmp/myshp.shp");
		shpFile.delete();

		// GO!
		FileSourceCreation fileSourceCreation = new FileSourceCreation(shpFile,
				metadata);
		dsf.createDataSource(fileSourceCreation);

		// Populate it!
		GeometryFactory gf = new GeometryFactory();
		DataSource ds = dsf.getDataSource(shpFile);

		//Obtain spatial capabilities on the DataSource
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		ds.insertEmptyRow();
		ds.insertEmptyRow();
		ds.insertEmptyRow();
		sds.setGeometry(0, gf.createPoint(new Coordinate(34, 645)));
		sds.setGeometry(1, gf.createPoint(new Coordinate(14, 5)));
		sds.setGeometry(2, gf.createPoint(new Coordinate(344,365)));
		sds.setInt(0, "id", 2785);
		sds.setInt(1, "id", 34897);
		sds.setInt(2, "id", 854);
		sds.setString(0, "name", "gdms");
		sds.setString(1, "name", "is");
		sds.setString(2, "name", "super!");
		ds.commit();

		//Read it!!
		ds = dsf.getDataSource(shpFile);
		sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		System.out.println(sds.getFullExtent());
		System.out.println(sds.getGeometry(0));
		System.out.println(ds.getAsString());
		sds.cancel();

	}

	public void testBadModification() throws Exception {
		// Create a dbf
		testCreateDBF();

		// Get it
		DataSource ds = dsf.getDataSource(dbfFile);
		ds.open();

		// Insert a row
		ds.insertEmptyRow();

		// populate it
		ds.setInt(0, "id", 4);
		ds.setString(0, "name", "Super long string");

		// We listen the warnings
		dsf.setWarninglistener(new WarningListener() {

			public void throwWarning(String msg) {
				// Just show it at the error output
				System.err.println(msg);
			}

			public void throwWarning(String msg, Throwable t, Object source) {
				// Just show it at the error output
				System.err.println(msg);
			}

		});

		// SAVE MY RESULTS PLEASE!!
		ds.commit();

		// Read it!
		ds = dsf.getDataSource(dbfFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();
	}

	public void testSchemaEdition() throws Exception {
		// Create the dbf
		testCreateDBF();

		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(dbfFile);

		// Open the file
		ds.open();

		// Add a field
		ds.addField("newField", TypeFactory.createType(Type.BOOLEAN));

		// Save changes
		ds.commit();

		// Read it!
		ds = dsf.getDataSource(dbfFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();
	}

	public void testUndo() throws Exception {
		// Create the factory
		dsf = new DataSourceFactory();

		// Create the information of the source we want to connect to
		File file = new File("src/test/resources/test.csv");

		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(file);

		// Open the file
		ds.open();

		// Show the number of rows
		System.out.println("number of rows: " + ds.getRowCount());

		// Delete the first one
		ds.deleteRow(0);

		// show again
		System.out.println("number of rows: " + ds.getRowCount());

		// undo the deletion!!
		ds.undo();

		// Everything is as before now
		System.out.println("number of rows: " + ds.getRowCount());

		// Ok. Lest save no change
		ds.commit();
	}

	public void testExportTool() throws Exception {
		// Create the dbf
		testCreateDBF();

		// Populate dbf
		DataSource dbfDataSource = dsf.getDataSource(dbfFile);
		{
			// open datasource
			dbfDataSource.open();
			// Populate 100 rows
			for (int i = 0; i < 100; i++) {
				dbfDataSource.insertEmptyRow();
				dbfDataSource.setInt(i, 0, i);
				dbfDataSource.setString(i, 1, "I'm everywhere!");
			}
			// Save population on disk
			dbfDataSource.commit();
		}

		// Export it to csv. Note independent code inside
		// brackets. We could export to postgreSQL as well.
		File csvFile = new File("/tmp/myexported.csv");
		{
			// Register the csv with the name "mycsv". Notice it's not created
			// yet
			FileSourceDefinition def = new FileSourceDefinition(csvFile);
			dsf.registerDataSource("mycsv", def);

			// Save the contents in the registered csv. Very easy!
			dsf.saveContents("mycsv", dbfDataSource);
		}

		// Read it!
		DataSource ds = dsf.getDataSource(csvFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();
	}

	public void testCommingSoon() throws Exception {
		DataSource ds = dsf.executeSQL("select buffer(the_geom, 20) from myshapefile where id = 4;");
		ds.open();
		System.out.println(ds.getAsString());
		ds.cancel();
	}

	public static void main(String[] args) throws Exception {
		DocumentationExamples de = new DocumentationExamples();
		de.testConnectPostgreSQL();
		de.testLoadCSV();
		de.testCreateDBF();
		de.testAddValues();
		de.testBadModification();
		de.testSchemaEdition();
		de.testUndo();
		de.testExportTool();
		de.testCommingSoon();
	}
}
