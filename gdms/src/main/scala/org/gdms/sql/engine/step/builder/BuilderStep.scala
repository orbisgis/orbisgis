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
package org.gdms.sql.engine.step.builder

import java.util.Properties
import org.gdms.sql.engine.GdmSQLPredef._
import org.gdms.data.DataSourceFactory
import org.gdms.sql.engine.AbstractEngineStep
import org.gdms.sql.engine.commands._
import org.gdms.sql.engine.commands.ddl._
import org.gdms.sql.engine.commands.join._
import org.gdms.sql.engine.commands.scan._
import org.gdms.sql.engine.operations._
import org.gdms.sql.engine.step.functions.FunctionsStep
import org.gdms.sql.engine.step.physicalJoin.PhysicalJoinOptimStep
import org.gdms.sql.evaluator.Expression
import org.gdms.sql.evaluator.QueryEvaluator

/**
 * Step P2: Building of the command tree
 * 
 * The final Operation tree is converted into the actual commands that will execute the query.
 */
case object BuilderStep extends AbstractEngineStep[(Operation, DataSourceFactory), OutputCommand]("Command Tree Building") {
  
  def doOperation(op: (Operation, DataSourceFactory))(implicit p: Properties): OutputCommand = {
    if (isPropertyTurnedOn(Flags.EXPLAIN)) {
      LOG.info("Building command tree.")
    }
    
    buildCommandTree(op._1)(op._2, p).asInstanceOf[OutputCommand]
  }
  
  /**
   * Builds a command tree from an operation tree.
   * This method chooses between the different available joins methods
   * index/full scans, etc.
   */
  private def buildCommandTree(op: Operation)(implicit dsf: DataSourceFactory, p: Properties): Command = {
    // for readability
    implicit val opToCo = buildCommandTree _
    
    op match {
      case Output(ch) => new QueryOutputCommand withChild ch
      case LimitOffset(l, o, ch) => new LimitOffsetCommand(l, o) withChild ch
      case SubQuery(s, Output(ch)) => 
        // jumping over Output in the subquery
        if (s != "") {
          // renaming if there is an alias
          new RenamingCommand(s) withChild ch
        } else {
          // unnamed call, in a customQuery for example
          buildCommandTree(ch)
        }
      case Scan(table, alias, edit) => new ScanCommand(table, alias, edit)
      case IndexQueryScan(table, alias, query) => new IndexQueryScanCommand(table, alias, query)
      case Join(jType, l, r) => (jType match {
            case Cross => new ExpressionBasedLoopJoinCommand(None)
            case Inner(ex, false, None) => 
              processExp(ex)
              new ExpressionBasedLoopJoinCommand(Some(ex))
            case Inner(ex, false, Some((field, expr, strict))) => 
              processExp(ex)
              new IndexedJoinCommand(expr, ex, field, strict)
            case Inner(ex, true, _) => 
              processExp(ex)
              new SpatialIndexedJoinCommand(ex)
            case Natural => new ExpressionBasedLoopJoinCommand(None, true)
            case OuterLeft(ex) => new ExpressionBasedLoopJoinCommand(ex, false, true)
            case _ => throw new IllegalStateException("Internal error: problem building PQP for joins.")
          })  withChildren(Seq(l, r))
      case ValuesScan(ex, alias, internal) => 
        ex foreach (_ foreach processExp)
        new ValuesScanCommand(ex, alias, internal)
      case Projection(exp, ch) => 
        exp foreach (a => processExp(a._1))
        new ProjectionCommand(exp toArray) withChild ch
      case Aggregate(exp, Grouping(e, ch)) => new AggregateCommand(exp, e) withChild ch
      case Aggregate(exp, ch) => new AggregateCommand(exp, Nil) withChild ch
      case Distinct(ch) => new MemoryDistinctCommand() withChild ch
      case Grouping(e, ch) =>  new AggregateCommand(Nil, e) withChild ch
      case Filter(exp, ch, _) => 
        processExp(exp)
        new ExpressionFilterCommand(exp) withChild ch
      case Sort(exprs, ch) => new MergeSortCommand(exprs) withChild ch
      case Union(a,b) => new UnionCommand() withChildren(Seq(a,b))
      case Update(e, ch) => new UpdateCommand(e) withChild ch
      case Insert(t, f, ch) => new InsertCommand(t, f) withChild ch
      case Delete(ch) => new DeleteCommand() withChild ch
      case a @ CustomQueryScan(_,e, t, alias) => 
        var tables = t map { 
          case Left(s) => Left(s)
          case Right(o) => buildCommandTree(o) match {
              case out: OutputCommand => Right(out)
              case other => Right(new QueryOutputCommand withChild other)
            }}
        new CustomQueryScanCommand(e,tables.reverse, a.function, alias)
      case CreateTableAs(n, ch) => new CreateTableCommand(n) withChild ch
      case CreateView(n, s, o, ch) => new CreateViewCommand(n, s, ch, o)
      case CreateTable(n, cols) => new TableCreationCommand(n, cols)
      case DropTables(n, i ,p) => new DropTablesCommand(n, i, p)
      case DropSchemas(n, i, p) => new DropSchemasCommand(n, i, p)
      case DropViews(n, i) => new DropViewsCommand(n, i)
      case AlterTable(n, elems) => new AlterTableCommand(n, elems)
      case RenameTable(n, nn) => new RenameTableCommand(n, nn)
      case CreateIndex(t, c) => new CreateIndexCommand(t, c)
      case DropIndex(t, c) => new DropIndexCommand(t, c)
      case ExecutorCall(name, t, l) => {
          var tables = t map { 
            case Left(s) => Left(s)
            case Right(o) => buildCommandTree(o) match {
                case out: OutputCommand => Right(out)
                case other => Right(new QueryOutputCommand withChild other)
              }}
          l map processExp
          new ExecutorCommand(name, tables, l)
        }
      case Set(p, v) => new SetParamCommand(p, v)
      case Show(p) => new ShowParamCommand(p)
      case CreateFunction(n, a, l, r) => new CreateFunctionCommand(n, a, l, r)
      case DropFunction(n, i) => new DropFunctionCommand(n, i)
      case a => throw new IllegalStateException("Internal error: problem with the logic query plan. Found :" + a)
    }
  }
  
  private def processExp(f: Expression)(implicit dsf: DataSourceFactory, p: Properties) {
    (f :: f.allChildren) foreach { _.evaluator match {
        case e : QueryEvaluator => e.command = processOp(e.op)
        case _ =>
      }
    }
  }
  
  private def processOp(o: Operation)(implicit dsf: DataSourceFactory, p: Properties) = {
    val c = (o, dsf) >=: 
    FunctionsStep >=:
    PhysicalJoinOptimStep >=: 
    BuilderStep
    
    // jump over Output (no materialization)
    c.children.head
  }
}
