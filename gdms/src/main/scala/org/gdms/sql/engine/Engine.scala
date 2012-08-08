/**
 * The GDMS library (Generic Datasource Management System)
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
package org.gdms.sql.engine

import java.io.{InputStream, ObjectInputStream, IOException, File, FileInputStream}
import java.util.Properties
import scala.io.Source
import org.gdms.data.DataSourceFactory
import org.gdms.sql.engine.operations.Operation
import org.gdms.sql.engine.step.filters.FiltersStep
import org.gdms.sql.engine.step.logicalJoin.LogicalJoinOptimStep
import org.gdms.sql.engine.step.parsing.ParsingStep
import org.gdms.sql.engine.step.treeparsing.TreeParsingStep
import org.gdms.sql.engine.step.validate.ValidationStep

/**
 * The entry point of the SQL Engine.
 * 
 * @author Antoine Gourlay
 * @since 2.0
 */
object Engine {
  
  /**
   * Parses a SQL script file.
   * 
   * @param sqlFile a valid sql file
   * @throws ParseException if there is an error while parsing
   */
  @throws(classOf[ParseException])
  def parseScript(sqlFile: File): SQLScript = parseScript(sqlFile, DataSourceFactory.getDefaultProperties)
  
  /**
   * Parses a SQL script file.
   * 
   * @param sqlFile a valid sql file
   * @param p some properties to control the engine
   * @throws ParseException if there is an error while parsing
   */
  @throws(classOf[ParseException])
  def parseScript(sqlFile: File, p: Properties): SQLScript = {
    parseScript(Source.fromFile(sqlFile).mkString, p)
  }
  
  /**
   * Parses a SQL script from an input stream.
   * 
   * @param sql a stream to a valid sql script
   * @throws ParseException if there is an error while parsing
   */
  @throws(classOf[ParseException])
  def parseScript(sql: InputStream): SQLScript = {
    parseScript(Source.fromInputStream(sql).mkString, DataSourceFactory.getDefaultProperties)
  }
  
  /**
   * Parses a SQL script from an input stream.
   * 
   * @param sql a stream to a valid sql script
   * @param p some properties to control the engine
   * @throws ParseException if there is an error while parsing
   */
  @throws(classOf[ParseException])
  def parseScript(sql: InputStream, p: Properties): SQLScript = {
    parseScript(Source.fromInputStream(sql).mkString, p)
  }
  
  /**
   * Parses a SQL script from a string.
   * 
   * @param sql a SQL script as a String
   * @param p some properties to control the engine
   * @throws ParseException if there is an error while parsing
   */
  @throws(classOf[ParseException])
  def parseScript(sql: String): SQLScript = parseScript(sql, DataSourceFactory.getDefaultProperties)
  
  /**
   * Parses a SQL script from a string.
   * 
   * @param sql a SQL script as a String
   * @param p some properties to control the engine
   * @throws ParseException if there is an error while parsing
   */
  @throws(classOf[ParseException])
  def parseScript(sql: String, p: Properties): SQLScript = {
    implicit val pp = p
    
    new SQLScript({
        sql          >=: // original string
        ParsingStep  >=: // parsing into AST
        TreeParsingStep  // parsing into Seq[Operation]
      } map { c =>
        (c._1                    >=:
         LogicalJoinOptimStep >=: // joins
         FiltersStep          >=: // filters
         ValidationStep           // validation
         , c._2)
      } map (c => new SQLStatement(c._2, c._1)))
  }
  
  /**
   * Parses a single SQL statement from a string.
   * 
   * @param sql a SQL statement as a String
   * @throws ParseException if there is an error while parsing
   */
  @throws(classOf[ParseException])
  def parse(sql: String): SQLStatement = parse(sql, DataSourceFactory.getDefaultProperties)
  
  /**
   * Parses a single SQL statement from a string.
   * 
   * @param sql a SQL statement as a String
   * @param p some properties to control the engine
   * @throws ParseException if there is an error while parsing
   */
  @throws(classOf[ParseException])
  def parse(sql: String, p: Properties): SQLStatement = {
    implicit val pp = p
    
    val c = {
      sql          >=: // original string
      ParsingStep  >=: // parsing into AST
      TreeParsingStep  // parsing into Seq[Operation]
    }
    
    if (!c.tail.isEmpty) {
      throw new ParseException("There shoudn't be more than one statement. Found " + c.size + ".")
    }
    
    // SQLStatement is always built with a complete (ending in ';') sql string
    // but the parsing above removes it
    new SQLStatement(c.head._2 + ';', 
                     c.head._1                 >=:
                     LogicalJoinOptimStep >=: // joins
                     FiltersStep          >=: // filters
                     ValidationStep           // validation)
    )
  }
  
  /**
   * Parses and executes a SQL Script.
   * 
   * @param sql a SQL statement as a String
   * @param dsf the current DataSourceFactory
   * @param p some properties to control the engine
   * @throws ParseException if there is an error while parsing
   */
  @throws(classOf[ParseException])
  def executeScript(sql: String, dsf: DataSourceFactory, p: Properties) {
    parseScript(sql, p).sts foreach { ss =>
      ss.setDataSourceFactory(dsf)
      ss.prepare
      ss.execute
      ss.cleanUp()
    }
  }
  
  /**
   * Parses and executes a SQL Script.
   * 
   * @param sql a SQL statement as a String
   * @param dsf the current DataSourceFactory
   * @throws ParseException if there is an error while parsing
   */
  @throws(classOf[ParseException])
  def executeScript(sql: String, dsf: DataSourceFactory) {
    executeScript(sql, dsf, dsf.getProperties)
  }
  
  /**
   * Loads a compiled sql statement from a file.
   * 
   * @param f a compiled sql statement
   * @param p some properties to control the engine
   * @throws IOException if there is an error while accessing the resource
   */
  @throws(classOf[IOException])
  def load(f: File): SQLStatement = load(new FileInputStream(f), DataSourceFactory.getDefaultProperties)
  
  /**
   * Loads a compiled sql statement from a file.
   * 
   * @param f a compiled sql statement
   * @param p some properties to control the engine
   * @throws IOException if there is an error while accessing the resource
   */
  @throws(classOf[IOException])
  def load(f: File, p: Properties): SQLStatement = load(new FileInputStream(f), p)
  
  /**
   * Loads a compiled sql statement from a stream.
   * 
   * @param i a stream over a compiled sql statement
   * @throws ParseException if there is an error while accessing the resource
   */
  @throws(classOf[IOException])
  def load(i: InputStream): SQLStatement = load(i, DataSourceFactory.getDefaultProperties)
  
  /**
   * Loads a compiled sql statement from a stream.
   * 
   * @param i a stream over a compiled sql statement
   * @param p some properties to control the engine
   * @throws ParseException if there is an error while accessing the resource
   */
  @throws(classOf[IOException])
  def load(i: InputStream, p: Properties): SQLStatement = {
    var o: ObjectInputStream = null
    var o2: ObjectInputStream = null
    
    try {
      o = new ObjectInputStream(i)
      val sql = o.readUTF
      o2 = new ObjectInputStream(i)
      val ope = o2.readObject.asInstanceOf[Operation]
      
      new SQLStatement(sql, ope)(p)
    } finally {
      if (o != null) o.close
      if (o2 != null) o2.close
      i.close // just to be sure
    }
  }
  
  /**
   * Loads a compiled sql script from a file.
   * 
   * @param f a compiled sql script file
   * @param p some properties to control the engine
   * @throws IOException if there is an error while accessing the resource
   */
  @throws(classOf[IOException])
  def loadScript(f: File): SQLScript = loadScript(new FileInputStream(f), DataSourceFactory.getDefaultProperties)
  
  /**
   * Loads a compiled sql script from a file.
   * 
   * @param f a compiled sql script file
   * @param p some properties to control the engine
   * @throws IOException if there is an error while accessing the resource
   */
  @throws(classOf[IOException])
  def loadScript(f: File, p: Properties): SQLScript =
    loadScript(new FileInputStream(f), p)
  
  /**
   * Loads a compiled sql script from a stream.
   * 
   * @param i a stream over a compiled sql script
   * @throws IOException if there is an error while accessing the resource
   */
  @throws(classOf[IOException])
  def loadScript(i: InputStream): SQLScript = loadScript(i, DataSourceFactory.getDefaultProperties)
  
  /**
   * Loads a compiled sql script from a stream.
   * 
   * @param i a stream over a compiled sql script
   * @param p some properties to control the engine
   * @throws IOException if there is an error while accessing the resource
   */
  @throws(classOf[IOException])
  def loadScript(i: InputStream, p: Properties): SQLScript = {
    var sts: List[SQLStatement] = Nil
    var objs: List[ObjectInputStream] = Nil
    
    try {
    val oNum = new ObjectInputStream(i)
    val num = oNum.readInt
    
    objs = List(oNum)
    
    new SQLScript((1 to num) map {_ =>
          // duplicating the ObjectInputStream is important here
          val o = new ObjectInputStream(i)
          val sql = o.readUTF
          val o2 = new ObjectInputStream(i)
          val ope = o2.readObject.asInstanceOf[Operation]
        
          new SQLStatement(sql, ope)(p)
      })
    } finally {
      objs map (_.close)
      i.close // just to be sure
    }
  }
}