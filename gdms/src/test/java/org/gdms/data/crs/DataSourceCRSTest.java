package org.gdms.data.crs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.annotation.processing.FilerException;

import junit.framework.TestCase;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class DataSourceCRSTest extends TestCase {

	private DataSourceFactory dsf;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
	}

	public void testSHPCRSWithPRJ() throws Exception {

		String crsName = "NTF_Lambert_II_étendu";
		File file = new File(BaseTest.internalData + "landcover2000.shp");
		DataSource ds = dsf.getDataSource(file);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		assertTrue(sds.getCRS().getName().equals(crsName));
		sds.close();

	}
	
	
	public void testASCIICRSWithPRJ() throws Exception {

		File file = new File(BaseTest.internalData + "sample.asc");
		DataSource ds = dsf.getDataSource(file);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		System.out.println(sds.getCRS().getName());
		sds.close();

	}
	
	public void testSHPCRSWithoutPRJ() throws Exception {

		File file = new File(BaseTest.internalData + "hedgerow.shp");
		DataSource ds = dsf.getDataSource(file);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		assertTrue(sds.getCRS().getName().equals("Unknow CRS"));
		sds.close();

	}
	
	/**
	 * Test CTS Transformation : CSV file give original and results Point 
	 * @throws Exception : File errors
	 */
	public void testSHPCRSTransform() throws Exception {
		
		final String dataFileName = "clc00r53"; //Shape to convert
		final String targetCode = "4326"; //target code correspond to CRS target
		//number ligne of datasource to convert. (to limit transformation when shape file is too big much) 
		final int NB_ROWS = 2; 
		
		final String prjFolder = "prj/";
		final String originalPrefix = "original";
		final String backupPrefix = "backup";		
		final String originalFolder = prjFolder + originalPrefix + "/";
		final String backupFolder= prjFolder + backupPrefix + "/";
		
		//Create CSV metadata
		DefaultMetadata metadata = new DefaultMetadata();		
		metadata.addField("X", Type.DOUBLE);
		metadata.addField("Y", Type.DOUBLE);
		metadata.addField("Z", Type.DOUBLE);		
		metadata.addField("ctsX", Type.DOUBLE);
		metadata.addField("ctsY", Type.DOUBLE);
		metadata.addField("ctsZ", Type.DOUBLE);		
		
		File file = new File(BaseTest.internalData + originalFolder+ dataFileName + ".shp");
		if(!file.exists()) throw new FilerException("Shape to convert not found");
		File workingFile = new File(BaseTest.internalData + backupFolder + dataFileName + ".shp");
		
		//Backup File
		ArrayList<String> extensions = new ArrayList<String>();
		extensions.add(".prj");
		extensions.add(".shx");
		extensions.add(".dbf");
		extensions.add(".shp");		
		FilenameFilter filters = new FilterShapeExtensions(extensions);		
		String[] files = getShapeFile(BaseTest.internalData + originalFolder, filters);	
		boolean find ;
		for(String ext : extensions ){
			find = false;
			for(int i=0; i< files.length; i++) {
				if(files[i].endsWith(ext)) find = true;
				if (i==files.length -1 &&  !find)
					throw new FileNotFoundException("Extension "+ext+" not found.");
				
			}								
		}		
		for(String path : files){
			File oneFile = new File(BaseTest.internalData + originalFolder + path);
			if(!oneFile.exists()) throw new FileNotFoundException("File "+path+" not found.");
			if(!(backupFile(oneFile, new File( oneFile.getAbsolutePath().replace(originalPrefix, backupPrefix)))))
				throw new FileNotFoundException("File "+path+" don't created.");
		}
		
		
		//SHP & before transform dataSource			
		DataSource dsBefore = dsf.getDataSource(file);
		SpatialDataSourceDecorator sdsBefore = new SpatialDataSourceDecorator(dsBefore);		
		sdsBefore.open();		
		ArrayList<Coordinate[]>pointsBefore = new ArrayList<Coordinate[]>();
		for(int i=0; i < NB_ROWS ; i++) {
			Geometry g = sdsBefore.getGeometry(i);
			pointsBefore.add(new CoordinateArraySequence(g.getCoordinates()).toCoordinateArray());
		}				
		sdsBefore.close();	
				
		//transform datasource
		dsf.getSourceManager().register(dataFileName, workingFile);		
		DataSource dsTransform = dsf.getDataSourceFromSQL("select st_transform(the_geom, '" + targetCode + "') from " + dataFileName + ";");		
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(dsTransform);		
		sds.open();			
		
		//Create CSV File 
		File outcsvFile = new File(BaseTest.internalData + backupFolder + dataFileName + /* "_" +
								 sds.getCRS().getCode() + */ "to" + targetCode + ".csv");
		outcsvFile.delete();
		FileSourceCreation fileSourceCreation = new FileSourceCreation(
				outcsvFile, metadata);
		dsf.createDataSource(fileSourceCreation);
		DataSource dsCSV = dsf.getDataSource(outcsvFile);
		dsCSV.open();
		
		//Write CSV
		long row=0;
		CoordinateArraySequence pointsAfter = null;
		for (int i = 0, j=0; i < NB_ROWS; i++,j=0) {
			Geometry g = sds.getGeometry(i);
			pointsAfter = new CoordinateArraySequence(g.getCoordinates());
			for(Coordinate c : pointsAfter.toCoordinateArray()) {
				dsCSV.insertEmptyRow();
				row = dsCSV.getRowCount() - 1;
				dsCSV.setDouble(row, "X", pointsBefore.get(i)[j].x);
				dsCSV.setDouble(row, "Y", pointsBefore.get(i)[j].y);
				dsCSV.setDouble(row, "Z", pointsBefore.get(i)[j].z);

				dsCSV.setDouble(row, "ctsX", c.x);
				dsCSV.setDouble(row, "ctsY", c.y);	
				dsCSV.setDouble(row, "ctsZ", c.z);	
				j++;
			}	
			dsCSV.commit();
		}			
		dsCSV.close();
		sds.close();		
	}

	private boolean backupFile(File source, File destination) {
		boolean resultat = false;

		// Declaration des flux
		java.io.FileInputStream sourceFile=null;
		java.io.FileOutputStream destinationFile=null;
		
		// Création du fichier :
		if(!destination.exists()) {
			try {
				destination.createNewFile();
				// Ouverture des flux
				sourceFile = new java.io.FileInputStream(source);
				destinationFile = new java.io.FileOutputStream(destination);
				// Lecture par segment de 0.5Mo
				byte buffer[]=new byte[512*1024];
				int nbLecture;
				while( (nbLecture = sourceFile.read(buffer)) != -1 ) {
					destinationFile.write(buffer, 0, nbLecture);
				}	
				// Copie réussie
				resultat = true;
			} catch( java.io.FileNotFoundException f ) {
				resultat=false;
				f.printStackTrace();
			} catch( java.io.IOException e ) {
				resultat=false;
				e.printStackTrace();
			} finally {
				// Quoi qu'il arrive, on ferme les flux
				try {
				sourceFile.close();
				} catch(Exception e) { }
				try {
				destinationFile.close();
				} catch(Exception e) { }
			}
		}
		else 
			resultat = true;
		return( resultat );
	}	
	
	public String[] getShapeFile(String url, FilenameFilter filters){
		File f = new File(url);
		return f.list(filters);
	}
	
	public class FilterShapeExtensions implements FilenameFilter {
		ArrayList<String> exts;

		public FilterShapeExtensions(ArrayList<String> exts) {
			this.exts = exts;
		}

		public boolean accept(File dir, String name) {
			for(String ext : exts){
				if(name.endsWith(ext))
					return true;
			}
			return false;
		}
	}
	

		
}
