/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 * 
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 * 
 * This file is part of Gdms.
 * 
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://www.orbisgis.org/>
 * 
 * or contact directly:
 * info@orbisgis.org
 */

package org.gdms.driver.geojson

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.vividsolutions.jts.geom.GeometryFactory
import java.io.File
import org.gdms.data.DataSourceFactory
import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.schema.DefaultSchema
import org.gdms.driver.DriverException
import org.gdms.driver.driverManager.DriverManager.{DEFAULT_SINGLE_TABLE_NAME => MainTable}
import org.gdms.driver.io.FileImporter
import org.gdms.driver.io.RowWriter
import org.gdms.source.SourceManager

/**
 * A geo-json importer for Gdms.
 * 
 * @author Antoine Gourlay
 */
class GeoJsonImporter extends FileImporter with Parser {
   
  // internal usefull stuff
  private var file: File = _
  private val metadata = new DefaultMetadata()
  private val gf = new GeometryFactory
  private lazy val jsonFactory = new JsonFactory()
  private implicit var jp: JsonParser = _
  
  // constant values for Importer
  val getType = SourceManager.FILE | SourceManager.VECTORIAL
  val getSupportedType = getType
  val getTypeName = "GeoJSON"
  val getTypeDescription = "Geo-JSON file format"
  val getImporterId = "geojson"
  val getFileExtensions = Array("js", "json")
  
  
  // initialization at object creation
  getSchema.addTable(MainTable, metadata)
  
  def open {
    // parse metadata
    jp = jsonFactory.createJsonParser(file)
    metadata.clear
    metadata.addAll(parseMetadata)
  }
  
  def close {
    jp.close
    jp = null
  }
  
  def setFile(f: File) {file = f}
  
  val getSchema = new DefaultSchema("json")
    
  def setDataSourceFactory(dsf: DataSourceFactory) {}
    
  def convertTable(name: String, rw: RowWriter) = {
    if (jp == null) {
      throw new DriverException("This driver is closed.")
    }
    
    if (name != MainTable) {
      throw new DriverException("There is no table '" + name + "' in this driver.")
    }
    
    // reload parser (was used parsing metadata)
    jp = jsonFactory.createJsonParser(file)
    
    parse(rw.addValues, metadata)
  }
}
