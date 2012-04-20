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

package org.gdms.sql.engine.step.builder

import java.util.Properties
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.data.SQLDataSourceFactory
import org.gdms.sql.engine.AbstractEngineStep
import org.gdms.sql.engine.commands._
import org.gdms.sql.engine.commands.ddl._
import org.gdms.sql.engine.commands.join._
import org.gdms.sql.engine.commands.scan._
import org.gdms.sql.engine.operations._
import org.gdms.sql.engine.step.aggregate.AggregateStep
import org.gdms.sql.engine.step.physicalJoin.PhysicalJoinOptimStep
import org.gdms.sql.evaluator.ExistsEvaluator
import org.gdms.sql.evaluator.Expression
import org.gdms.sql.evaluator.InEvaluator
import org.gdms.sql.function.table.TableFunction

/**
 * Step P2: Building of the command tree
 * 
 * The final Operation tree is converted into the actual commands that will execute the query.
 */
case object BuilderStep extends AbstractEngineStep[(Operation, SQLDataSourceFactory), OutputCommand]("Command Tree Building") {
  
  def doOperation(op: (Operation, SQLDataSourceFactory))(implicit p: Properties): OutputCommand = {
    if (isPropertyTurnedOn(Flags.EXPLAIN)) {
      LOG.info("Building command tree.")
    }
    
    buildCommandTree(op._1)(op._2, p).asInstanceOf[OutputCommand]
  }
  
  /**
   * Builds a command tree from a operation tree.
   * This method chooses between the different available joins methods
   * index/full scans, etc.
   */
  private def buildCommandTree(op: Operation)(implicit dsf: SQLDataSourceFactory, p: Properties): Command = {
    // for readability
    implicit val opToCo = buildCommandTree _
    
    op match {
      case Output(ch) => new QueryOutputCommand withChild(ch)
      case LimitOffset(l, o, ch) => new LimitOffsetCommand(l, o) withChild(ch)
      case SubQuery(s, Output(ch)) => {
          // jumping over Output in the subquery
          if (s != "") {
            // renaming if there is an alias
            new RenamingCommand(s) withChild(ch)
          } else {
            // unnamed call, in a customQuery for example
            buildCommandTree(ch)
          }
        }
      case Scan(table, alias, edit) => new ScanCommand(table, alias, edit)
      case IndexQueryScan(table, alias, query) => new IndexQueryScanCommand(table, alias, query)
      case Join(jType, l, r) => (jType match {
            case Cross() => {
                new ExpressionBasedLoopJoinCommand(None)
              }
            case Inner(ex, false, None) => {
                processExp(ex)
                new ExpressionBasedLoopJoinCommand(Some(ex))
              }
            case Inner(ex, false, Some((field, expr, strict))) => {
                processExp(ex)
                new IndexedJoinCommand(expr, ex, field, strict)
              }
            case Inner(ex, true, _) => {
                processExp(ex)
                new SpatialIndexedJoinCommand(ex)
              }
            case Natural() => {
                new ExpressionBasedLoopJoinCommand(None, true)
              }
            case OuterLeft(ex) => {
                new ExpressionBasedLoopJoinCommand(ex, false, true)
              }
            case _ => throw new IllegalStateException("Internal error: problem building PQP for joins.")
          })  withChildren(Seq(l, r))
      case ValuesScan(ex, alias, internal) => {
          ex foreach (_ foreach processExp)
          new ValuesScanCommand(ex, alias, internal)
        }
      case Projection(exp, ch) => {
          exp foreach (a => processExp(a._1))
          new ProjectionCommand(exp toArray) withChild(ch)
        }
      case Aggregate(exp, Grouping(e, ch)) => {
          new AggregateCommand(exp, e) withChild(ch)
        }
      case Aggregate(exp, ch) => {
          new AggregateCommand(exp, Nil) withChild(ch)
        }
      case Distinct(ch) => new MemoryDistinctCommand() withChild(ch)
      case Grouping(e, ch) =>  new AggregateCommand(Nil, e) withChild(ch)
      case Filter(exp, ch) => {
          processExp(exp)
          new ExpressionFilterCommand(exp) withChild(ch)
        }
      case Sort(exprs, ch) => new MergeSortCommand(exprs) withChild(ch)
      case Union(a,b) => new UnionCommand() withChildren(Seq(a,b))
      case Update(e, ch) => new UpdateCommand(e) withChild(ch)
      case Insert(t, f, ch) => new InsertCommand(t, f) withChild(ch)
      case Delete(ch) => new DeleteCommand() withChild(ch)
      case a @ CustomQueryScan(_,e, t, alias) => {
          var tables = t map { 
            case Left(s) => Left(s)
            case Right(o) => { 
                val ot = buildCommandTree(o)
                ot match {
                  case out: OutputCommand => Right(out)
                  case other => {
                      val out = new QueryOutputCommand
                      out withChild(other)
                      Right(out)
                    }
                }
              }
          }
          new CustomQueryScanCommand(e,tables.reverse, a.function.asInstanceOf[TableFunction], alias)
        }
      case CreateTableAs(n, ch) => new CreateTableCommand(n) withChild(ch)
      case a @ CreateView(n, o, ch) => new CreateViewCommand(n, ch, o)
      case CreateTable(n, cols) => new TableCreationCommand(n, cols)
      case DropTables(n, i ,p) => new DropTablesCommand(n, i, p)
      case DropSchemas(n, i, p) => new DropSchemasCommand(n, i, p)
      case DropViews(n, i) => new DropViewsCommand(n, i)
      case AlterTable(n, elems) => new AlterTableCommand(n, elems)
      case RenameTable(n, nn) => new RenameTableCommand(n, nn)
      case CreateIndex(t, c) => new CreateIndexCommand(t, c)
      case DropIndex(t, c) => new DropIndexCommand(t, c)
      case ExecutorCall(name, l) => new ExecutorCommand(name, l)
      case Set(p, v) => new SetParamCommand(p, v)
      case Show(p) => new ShowParamCommand(p)
      case CreateFunction(n, a, l, r) => new CreateFunctionCommand(n, a, l, r)
      case DropFunction(n, i) => new DropFunctionCommand(n, i)
      case a => throw new IllegalStateException("Internal error: problem with the logic query plan. Found :" + a)
    }
  }
  
  private def processExp(f: Expression)(implicit dsf: SQLDataSourceFactory, p: Properties) {
    (f :: f.allChildren) foreach { e => e.evaluator match {
        case e @ ExistsEvaluator(op) => e.command = processOp(op)
        case e @ InEvaluator(_, op) => e.command = processOp(op)
        case _ =>
      }
    }
  }
  
  private def processOp(o: Operation)(implicit dsf: SQLDataSourceFactory, p: Properties) = {
    val c = (o, dsf) >=: 
    PhysicalJoinOptimStep >=: 
    BuilderStep
    
    // jump over Output (no materialization)
    c.children.head
  }
}
