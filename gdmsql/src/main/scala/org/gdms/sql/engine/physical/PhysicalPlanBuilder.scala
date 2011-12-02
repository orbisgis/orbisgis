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

package org.gdms.sql.engine.physical

/**
 * This object contains all the logic to build a Physical Query Plan from a Logical Query Plan given by
 * {@link LogicPlanBuilder}.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
import java.util.Properties
import org.apache.log4j.Logger
import org.gdms.data.SQLDataSourceFactory
import org.gdms.sql.engine.commands._
import org.gdms.sql.engine.commands.scan._
import org.gdms.sql.engine.commands.ddl._
import org.gdms.sql.engine.commands.join._
import org.gdms.sql.engine.logical.LogicPlanOptimizer
import org.gdms.sql.engine.operations._
import org.gdms.sql.function.table.TableFunction

object PhysicalPlanBuilder {
  
  private val LOG: Logger = Logger.getLogger(PhysicalPlanBuilder.getClass)
  private val OPTIMIZEJOINS = "optimizer.optimiseJoins"
  private val EXPLAIN = "output.explain"

  /**
   * Builds the physical plan associated with the given operation.
   * 
   * @param dsf the datasourcefactory that will be used for this query
   * @param op the operation to build
   * @param p a set of properties (can be null)
   */
  def buildPhysicalPlan(dsf: SQLDataSourceFactory ,op: Operation, p: Properties): Command = {
    if (isPropertyTurnedOn(p, EXPLAIN)) {
      LOG.info("Building physical plan")
    }
    
    // optimize joins
    if (!isPropertyTurnedOff(p, OPTIMIZEJOINS)) {
      optimizeSpatialJoins(dsf, op)
      if (isPropertyTurnedOn(p, EXPLAIN)) {
        LOG.info("Optimized joins")
        LOG.info(op)
      }
    }
    
    // build the command tree
    buildCommandTree(op)
  }
  
  private def optimizeSpatialJoins(dsf: SQLDataSourceFactory ,op: Operation) {
    LogicPlanOptimizer.matchOperationFromBottom(op, {ch =>
        // gets Join(Inner(_))
        ch.isInstanceOf[Join] && (ch.asInstanceOf[Join].joinType match {
            // find actual spatial joins
            case Inner(_, true) => {
                ch.children.filter(_.isInstanceOf[ValuesScan]).isEmpty
              }
            case _ => false
          })
      }, {ch =>
        
        // will hold table names
        var tables: List[String] = Nil
        
        // gets the table names from the scans
        LogicPlanOptimizer.matchOperationFromBottom(ch, {c =>
            c.isInstanceOf[Scan]
          }, {c => tables = c.asInstanceOf[Scan].table :: tables
          })
        
        // gets the sizes of the tables
        val sizes = tables map { t =>
          val d = dsf.getDataSource(t)
          d.open
          val count = d.getRowCount
          d.close
          (count, t)
        }
        
        // gets the best candidate for index scan
        // in this case the table with the most rows
        val best = sizes.reduceLeft {(a, b) => 
          if (a._1 >= b._1) a else b
        }
        
        // replaces the Scan by an IndexQueryScan
        LogicPlanOptimizer.replaceOperationFromBottom(ch,{c =>
            c.isInstanceOf[Scan] && c.asInstanceOf[Scan].table == best._2
          }, {c => 
            val s = c.asInstanceOf[Scan]
            IndexQueryScan(s.table, s.alias, null)
          })
      })
  }

  /**
   * Builds a command tree from a operation tree.
   * This method chooses between the different available joins methods
   * index/full scans, etc.
   */
  private def buildCommandTree(op: Operation): Command = {
    val c = op match {
      case Output() => new QueryOutputCommand
      case LimitOffset(l, o) => new LimitOffsetCommand(l, o)
      case SubQuery(s) => {

          if (s != "") {
            // jumping over Output in the subquery
            op.children = op.children.head.children
            // renaming if there is an alias
            new RenamingCommand(s)
          } else {
            // unnamed call, in a customQuery for example
            val l = buildCommandTree(op.children.head.children.head)
            op.children = Nil
            l
          }
        }
      case Scan(table, alias, edit) => new ScanCommand(table, alias, edit)
      case IndexQueryScan(table, alias, query) => new IndexQueryScanCommand(table, alias, query)
      case Join(jType) => jType match {
          case Cross() => {
              new ExpressionBasedLoopJoinCommand(None)
            }
          case Inner(ex, false) => {
              new ExpressionBasedLoopJoinCommand(Some(ex))
            }
          case Inner(ex, true) => {
              new SpatialIndexedJoinCommand(ex)
            }
          case Natural() => {
              new ExpressionBasedLoopJoinCommand(None, true)
          }
          case OuterLeft(ex) => {
              new ExpressionBasedLoopJoinCommand(ex, false, true)
          }
        }
      case ValuesScan(ex, alias) => new ValuesScanCommand(ex, alias)
      case Projection(exp) => new ProjectionCommand(exp toArray)
      case a @ Aggregate(exp) => {
          val grouping = a.children.head match {
            case g @ Grouping(e) => {
                op.children = g.children
                e}
            case _ => Nil
          }
          new AggregateCommand(exp, grouping)
        }
      
      case Distinct() => new MemoryDistinctCommand()
      case Grouping(e) =>  new AggregateCommand(Nil, e)
      case Filter(exp) => new ExpressionFilterCommand(exp)
      case Sort(exprs) => new MergeSortCommand(exprs)
      case Union() => new UnionCommand()
      case Update(e) => new UpdateCommand(e)
      case StaticInsert(t, e, f) => new StaticInsertCommand(t, e, f)
      case Delete() => new DeleteCommand()
      case a @ CustomQueryScan(_,e, t, alias) => {
          var tables: Seq[Either[String, OutputCommand]] = Nil
          tables = t map { _ match {
              case Left(s) => Left(s)
              case Right(o) => { 
                  val ot = buildCommandTree(o)
                  if (ot.isInstanceOf[OutputCommand]) {
                    Right(ot.asInstanceOf[OutputCommand])
                  } else {
                    val out = new QueryOutputCommand()
                    out.children = ot :: Nil
                    Right(out)
                  }
                }
            }}
          op.children = Nil
          tables = tables.reverse
          new CustomQueryScanCommand(e,tables, a.function.asInstanceOf[TableFunction], alias)
        }
      case CreateTableAs(n) => new CreateTableCommand(n)
      case a @ CreateView(n, o) => {
          val s = a.children.head
          a.children = Nil
          new CreateViewCommand(n, s, o)}
      case CreateTable(n, cols) => new TableCreationCommand(n, cols)
      case DropTables(n, i ,p) => new DropTablesCommand(n, i, p)
      case DropViews(n, i) => new DropViewsCommand(n, i)
      case AlterTable(n, elems) => new AlterTableCommand(n, elems)
      case RenameTable(n, nn) => new RenameTableCommand(n, nn)
      case CreateIndex(t, c) => new CreateIndexCommand(t, c)
      case DropIndex(t, c) => new DropIndexCommand(t, c)
      case ExecutorCall(name, l) => new ExecutorCommand(name, l)
    }
    op.children foreach ( buildCommandTree(_) addAsChildrenOf c)
    c
  }

  private def isPropertyValue(p: Properties, name: String, value: String) = {
    p != null && (p.getProperty(name) match {
        case a if a == value => true
        case _ => false
      })
  }
  
  private def isPropertyTurnedOn(p: Properties, name: String) = {
    isPropertyValue(p, name, "true")
  }
  
  private def isPropertyTurnedOff(p: Properties, name: String) = {
    isPropertyValue(p, name, "false")
  }
}
