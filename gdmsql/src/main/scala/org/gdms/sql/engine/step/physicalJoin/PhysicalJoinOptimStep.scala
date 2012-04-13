/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * 
 * Team leader : Erwan BOCHER, scientific researcher,
 * 
 * User support leader : Gwendall Petit, geomatic engineer.
 * 
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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

package org.gdms.sql.engine.step.physicalJoin

import java.util.Properties
import org.gdms.data.SQLDataSourceFactory
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.sql.engine.AbstractEngineStep
import org.gdms.sql.engine.operations._
import org.gdms.sql.evaluator._

/**
 * Step P1: Join ptimisations that do require access to the DataSourceFactory.
 * 
 * - Joins tagged as spatials are looked at and a table is chosen for index scan.
 * - Equi-joins are found, and a table is chosen for index scan.
 */
case object PhysicalJoinOptimStep extends AbstractEngineStep[(Operation, SQLDataSourceFactory), Operation]("DSF-aware join optimisations") {

  def doOperation(op: (Operation, SQLDataSourceFactory))(implicit p: Properties): Operation = {
    // optimize joins
    if (!isPropertyTurnedOff(Flags.OPTIMIZEJOINS)) {
      if (isPropertyTurnedOn(Flags.EXPLAIN)) {
        LOG.info("Optimizing joins with DSF")
        LOG.info(op)
      }
      optimizeJoins(op._2, op._1)
    }
    op._1
  }
  
  private def optimizeJoins(dsf: SQLDataSourceFactory ,op: Operation) {
    op.allChildren foreach ( _ match {
        
        // optimize spatial joins
        case j @ Join(Inner(_, true, _), a @ Scan(t, al, _), b @ Scan(t2, al2, _)) => {
            if (t == t2) {
              j.children = IndexQueryScan(t, al) :: b :: Nil
            } else {
              // gets the sizes of the tables
              val sizes = Seq(t, t2) map { table =>
                val d = dsf.getDataSource(table)
                d.open
                val count = d.getRowCount
                d.close
                (count, table)
              }
            
              // gets the best candidate for index scan
              // in this case the table with the most rows
              val best = sizes.reduceLeft {(a, b) => 
                if (a._1 >= b._1) a else b
              }
              
              if (best._2 == t) {
                j.children = IndexQueryScan(t, al) :: b :: Nil
              } else {
                j.children = a :: IndexQueryScan(t2, al2) :: Nil
              }
            }
          }
          
          // optimize basic equi-joins
        case j @ Join(jt @ Inner(field(fn1,ft1) === field(fn2,ft2), false, _), a @ Scan(t, al, _), b @ Scan(t2, al2, _)) => {
            if (t == t2) {
              j.children = IndexQueryScan(t, al) :: b :: Nil
            } else {
              // gets the sizes of the tables
              val sizes = Seq(t, t2) map { table =>
                val d = dsf.getDataSource(table)
                d.open
                val m = d.getMetadata
                val count = d.getRowCount
                d.close
                (count, table, m)
              }
              
            
              // gets the best candidate for index scan
              // in this case the table with the most rows
              val best = sizes.reduceLeft {(a, b) => 
                if (a._1 >= b._1) a else b
              }
              
              if (best._2 == t) {
                if (ft1.map(_ == al.getOrElse(t)).getOrElse(best._3.getFieldIndex(fn1) != -1)) {
                  jt.withIndexOn = Some((fn1, Field(fn2, al2.getOrElse(t2)), true))
                  j.children = IndexQueryScan(t, al) :: b :: Nil
                }
              } else {
                if (ft2.map(_ == al2.getOrElse(t2)).getOrElse(best._3.getFieldIndex(fn2) != -1)) {
                  jt.withIndexOn = Some((fn2, Field(fn1, al.getOrElse(t)), true))
                  j.children = a :: IndexQueryScan(t2, al2) :: Nil
                }
              }
            }
          }
          // optimize joins ANDed to anything else
        case j @ Join(jt @ Inner(exp & exp2, false, _), a @ Scan(t, al, _), b @ Scan(t2, al2, _)) => {
            
            def doMatch(ex: Expression, comp: Expression) {
              ex match {
                // equi-joins
                case field(fn1,ft1) === field(fn2,ft2) => {
                    if (t == t2) {
                      j.children = IndexQueryScan(t, al) :: b :: Nil
                    } else {
                      // gets the sizes of the tables
                      val sizes = Seq(t, t2) map { table =>
                        val d = dsf.getDataSource(table)
                        d.open
                        val m = d.getMetadata
                        val count = d.getRowCount
                        d.close
                        (count, table, m)
                      }
              
            
                      // gets the best candidate for index scan
                      // in this case the table with the most rows
                      val best = sizes.reduceLeft {(a, b) => 
                        if (a._1 >= b._1) a else b
                      }
              
                      if (best._2 == t) {
                        if (ft1.map(_ == al.getOrElse(t)).getOrElse(best._3.getFieldIndex(fn1) != -1)) {
                          jt.withIndexOn = Some((fn1, Field(fn2, t2), false))
                          j.children = IndexQueryScan(t, al) :: b :: Nil
                        }
                      } else {
                        if (ft2.map(_ == al2.getOrElse(t2)).getOrElse(best._3.getFieldIndex(fn2) != -1)) {
                          jt.withIndexOn = Some((fn2, Field(fn1, t), false))
                          j.children = a :: IndexQueryScan(t2, al2) :: Nil
                        }
                      }
                      jt.cond = comp
                    }
                  }
                case a & b => {
                    doMatch(a, b)
                    doMatch(b ,a)
                  }
                case _ =>
              }
            }
            
            doMatch(exp, exp2)
            doMatch(exp2, exp)
          }
        case _ =>
      })
  }
}
