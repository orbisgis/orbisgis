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

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.WarningListener;
import org.gdms.data.db.DBSource;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.PrecisionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 *
 * This class demonstrates how to use GDMS with differents kinds of sources.
 *
 *
 */

public class DocumentationExamples extends TestCase {

	// Input flat file sources

	private File incsvFile = new File("src/test/resources/test.csv");

	private File inshpFile = new File("src/test/resources/ile_de_nantes.shp");

	// Output flat file sources
	private File outdbfFile = new File("/tmp/mydbf.dbf");

	private File outcsvFile = new File("/tmp/mycsv.csv");

	private File outshpFile = new File("/tmp/myshp.shp");

	// Create the factory for accessing to datasources
	private DataSourceFactory dsf = new DataSourceFactory();;

	/**
	 * This example demonstrates how to read a csv file and how to display the
	 * value for the first row for the field named gis.
	 *
	 * @throws Exception
	 */
	public void testReadCSVAndShowOneValue() throws Exception {

		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(incsvFile);

		// Open the file
		ds.open();

		// Show the value for the field "gis" in the first row
		System.out.println(ds.getString(0, "gis"));

		// Close without saving the changes. Indeed we did no changes!
		ds.close();
	}

	/**
	 * This example shows how to create a csv file.
	 *
	 * @throws Exception
	 */

	public void testCreateCSV() throws Exception {

		// Define the schema of the dbf to be created
		DefaultMetadata metadata = new DefaultMetadata();

		// Add a String field
		metadata.addField("name", Type.STRING);

		// Add a second string field
		metadata.addField("surname", Type.STRING);

		// Delete it just in case...
		outcsvFile.delete();

		// GO!
		FileSourceCreation fileSourceCreation = new FileSourceCreation(
				outcsvFile, metadata);
		dsf.createDataSource(fileSourceCreation);

		// Read it!
		DataSource ds = dsf.getDataSource(outcsvFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.close();
	}

	/**
	 * This example demonstrates how to add some values in a csv file.
	 *
	 * @throws Exception
	 */
	public void testAddValuesInCSV() throws Exception {
		// Create an empty csv
		testCreateCSV();

		// Get it
		DataSource ds = dsf.getDataSource(outcsvFile);
		ds.open();

		// Insert a row
		ds.insertEmptyRow();

		// populate it
		ds.setString(0, "name", "micky");
		//ds.setString(0, "surname", "mouse");

		// SAVE MY RESULTS PLEASE!!
		ds.commit();
		ds.close();

		// Read it!
		ds = dsf.getDataSource(outcsvFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.close();
	}

	/**
	 * This example shows how to modify a csf file.
	 *
	 * @throws Exception
	 */
	public void testModifyCSV() throws Exception {
		testAddValuesInCSV();

		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(outcsvFile);

		// Open the file
		ds.open();

		// Modify second field in first row
		ds.setString(0, 1, "new string");

		// If you want to close without saving the changes, use this :
		// ds.cancel();

		ds.commit();
		ds.close();

		ds.open();
		System.out.println(ds.getAsString());
		ds.close();

	}

	/**
	 * This example demonstrates how to create a dbf file.
	 *
	 * @throws Exception
	 */
	public void testCreateDBF() throws Exception {

		// Define the schema of the dbf to be created
		DefaultMetadata metadata = new DefaultMetadata();

		// Create an integer field
		{// bracket to indent for documentation purposes only

			// DBF numeric fields need some constraints
			Constraint lengthConstraint = new LengthConstraint(5);
			Constraint precisionConstraint = new PrecisionConstraint(0);

			// Add the integer field
			metadata.addField("id", Type.INT, new Constraint[] {
					lengthConstraint, precisionConstraint });
		}

		// Create a String field
		{// bracket to indent for documentation purposes only

			// Define the length constraint
			Constraint stringLengthConstraint = new LengthConstraint(15);

			// Add the string field
			metadata.addField("name", Type.STRING,
					new Constraint[] { stringLengthConstraint });
		}

		// Delete it just in case...
		outdbfFile.delete();

		// GO!
		FileSourceCreation fileSourceCreation = new FileSourceCreation(
				outdbfFile, metadata);
		dsf.createDataSource(fileSourceCreation);

		// Read it!
		DataSource ds = dsf.getDataSource(outdbfFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.close();
	}

	/**
	 * This example demontrates how to read a shapefile and show the geometries.
	 * Geometry is a JTS geometry.
	 *
	 * @throws Exception
	 */
	public void testReadGeometriesInShapefile() throws Exception {
		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(inshpFile);

		// Obtain the spatial datasource
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();

		for (int i = 0; i < sds.getRowCount(); i++) {
			System.out.println("Geometry : " + sds.getGeometry(i));
		}

		sds.close();

	}

	/**
	 * This example demontrates how to create and write a shapefile.
	 *
	 * @throws Exception
	 */

	public void testCreateAndWriteShapefile() throws Exception {

		// Define the schema of the dbf to be created
		DefaultMetadata metadata = new DefaultMetadata();

		// Create an integer field
		{// bracket to indent for documentation purposes only

			// DBF numeric fields need some constraints
			Constraint lengthConstraint = new LengthConstraint(5);
			Constraint precisionConstraint = new PrecisionConstraint(0);

			// Add the integer field
			metadata.addField("id", Type.INT, new Constraint[] {
					lengthConstraint, precisionConstraint });
		}

		// Create a String field
		{// bracket to indent for documentation purposes only

			// Define the length constraint
			Constraint stringLengthConstraint = new LengthConstraint(15);

			// Add the string field
			metadata.addField("name", Type.STRING,
					new Constraint[] { stringLengthConstraint });
		}

		// Create a spatial field
		{
			// Define the length constraint
			Constraint geometryTypeConstraint = new GeometryConstraint(
					GeometryConstraint.MULTI_POINT);

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

		// Obtain spatial capabilities on the DataSource
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		ds.insertEmptyRow();
		ds.insertEmptyRow();
		ds.insertEmptyRow();
		sds.setGeometry(0, gf.createPoint(new Coordinate(34, 645)));
		sds.setGeometry(1, gf.createPoint(new Coordinate(14, 5)));
		sds.setGeometry(2, gf.createPoint(new Coordinate(344, 365)));
		sds.setInt(0, "id", 2785);
		sds.setInt(1, "id", 34897);
		sds.setInt(2, "id", 854);
		sds.setString(0, "name", "gdms");
		sds.setString(1, "name", "is");
		sds.setString(2, "name", "super!");
		ds.commit();
		ds.close();

		// Read it!!
		ds = dsf.getDataSource(shpFile);
		sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		System.out.println(sds.getFullExtent());
		System.out.println(sds.getGeometry(0));
		System.out.println(ds.getAsString());
		sds.close();

	}

	/**
	 * This example demontrates how to duplicate a shapefile.
	 *
	 *
	 * @throws Exception
	 */
	public void testDuplicateShapefile() throws Exception {

		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(inshpFile);

		// Read it

		ds.open();
		System.out.println(ds.getAsString());
		ds.close();

		// Create the new shapefile

		outshpFile.delete();
		DataSourceDefinition dsd = new FileSourceDefinition(outshpFile);

		// Register it
		dsf.registerDataSource("newShape", dsd);

		// Save it
		dsf.saveContents("newShape", ds);

		// Read it
		DataSource outds = dsf.getDataSource(outshpFile);
		outds.open();
		System.out.println(outds.getAsString());
		outds.close();

	}

	/**
	 * This example demonstrates how duplicate a new shapefile and add into it a
	 * new field populate with geometry propreties.
	 *
	 * @throws Exception
	 */
	public void testAddFieldValuesInShapefile() throws Exception {

		testDuplicateShapefile();

		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(outshpFile);

		// Obtain the spatial datasource
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);

		// Open the file
		sds.open();

		// Add a field
		sds.addField("area", TypeFactory.createType(Type.DOUBLE));

		for (int i = 0; i < sds.getRowCount(); i++) {

			sds.setDouble(i, 2, sds.getGeometry(i).getArea());
		}
		// Save changes
		sds.commit();
		sds.close();

		// Read it
		sds.open();
		System.out.println(sds.getAsString());
		sds.close();
	}

	/**
	 * This example demontrates how to connect to a postgresql database.
	 *
	 * @throws Exception
	 */
	public void testConnectPostgreSQL() throws Exception {

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
		ds.close();
	}

	/**
	 * This example demonstrates how to add a field into an existing dbf file.
	 *
	 * @throws Exception
	 */
	public void testSchemaEdition() throws Exception {
		// Create the dbf
		testCreateDBF();

		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(outdbfFile);

		// Open the file
		ds.open();

		// Add a field
		ds.addField("newField", TypeFactory.createType(Type.BOOLEAN));

		// Save changes
		ds.commit();
		ds.close();

		// Read it!
		ds = dsf.getDataSource(outdbfFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.close();
	}

	/**
	 * This example shows how to use undo method to cancel a modification.
	 *
	 * @throws Exception
	 */
	public void testUndo() throws Exception {

		// Obtain a DataSource to interact with the source
		DataSource ds = dsf.getDataSource(incsvFile);

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

		// Ok. Let's save no change
		ds.commit();
		ds.close();

	}

	/**
	 * This examples demonstrates how to convert a dbf file onto a csv file.
	 *
	 * @throws Exception
	 */
	public void testExportTool() throws Exception {
		// Create the dbf
		testCreateDBF();

		// Populate dbf
		DataSource dbfDataSource = dsf.getDataSource(outdbfFile);
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
			dbfDataSource.close();
		}

		// Export it to csv. Note independent code inside
		// brackets. We could export to postgreSQL as well.
		File csvFile = new File("/tmp/myexported.csv");
		{
			// Register the csv with the name "mycsv". Notice it's not created
			// yet
			FileSourceDefinition def = new FileSourceDefinition(csvFile);
			dsf.getSourceManager().register("mycsv", def);

			// Save the contents in the registered csv. Very easy!
			dsf.saveContents("mycsv", dbfDataSource);
		}

		// Read it!
		DataSource ds = dsf.getDataSource(csvFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.close();
	}

	public void testBadModification() throws Exception {
		// Create a dbf
		testCreateDBF();

		// Get it
		DataSource ds = dsf.getDataSource(outdbfFile);
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
		ds.close();

		// Read it!
		ds = dsf.getDataSource(outdbfFile);
		ds.open();
		System.out.println(ds.getAsString());
		ds.close();
	}

	public void testCommingSoon() throws Exception {
		DataSource ds = dsf
				.getDataSourceFromSQL("select buffer(the_geom, 20) from myshapefile where id = 4;");
		ds.open();
		System.out.println(ds.getAsString());
		ds.close();
	}

	public static void main(String[] args) throws Exception {
		DocumentationExamples de = new DocumentationExamples();
		de.testConnectPostgreSQL();
		de.testReadCSVAndShowOneValue();
		de.testCreateDBF();
		de.testAddValuesInCSV();
		de.testAddFieldValuesInShapefile();
		de.testDuplicateShapefile();
		de.testBadModification();
		de.testSchemaEdition();
		de.testUndo();
		de.testExportTool();
		de.testCommingSoon();
	}
}
