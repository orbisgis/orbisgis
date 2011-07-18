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
import org.gdms.sql.engine.commands._
import org.gdms.sql.engine.commands.ddl._
import org.gdms.sql.engine.operations._
import org.gdms.sql.function.table.TableFunction

object PhysicalPlanBuilder {

  def buildPhysicalPlan(op: Operation): Command = {
    buildCommandTree(op)
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
            val l = buildCommandTree(op.children.head)
            op.children = Nil
            l
          }
        }
      case Scan(table, alias, edit) => new ScanCommand(table, alias, edit)
      case Join(jType) => jType match {
          case Cross() => {
              // we convert a multiple table Join Operation into a 2 table
              // LoopJoinCommand
              val l = buildJoinCommandTree(op.children)
              op.children = Nil
              l
            }
        }
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
      case Grouping(e) =>  new AggregateCommand(Nil, e)
      case Filter(exp) => new ExpressionFilterCommand(exp)
      case Sort(exprs) => new MergeSortCommand(exprs)
      case Update(e) => new UpdateCommand(e)
      case StaticInsert(t, e, f) => new StaticInsertCommand(t, e, f)
      case Delete() => new DeleteCommand()
      case a @ CustomQueryScan(_,e, t, alias) => {
          var tables: Seq[Either[String, OutputCommand]] = Nil
          tables = t map { _ match {
              case Left(s) => Left(s)
              case Right(o) => Right(buildCommandTree(o).asInstanceOf[OutputCommand])
            }}
          op.children = Nil
          tables = tables.reverse
          new CustomQueryScanCommand(e,tables, a.function.asInstanceOf[TableFunction], alias)
        }
      case CreateTableAs(n) => new CreateTableCommand(n)
      case CreateTable(n, cols) => new TableCreationCommand(n, cols)
      case DropTables(n, i ,p) => new DropTablesCommand(n, i, p)
      case AlterTable(n, elems) => new AlterTableCommand(n, elems)
      case RenameTable(n, nn) => new RenameTableCommand(n, nn)
      case CreateIndex(t, c) => new CreateIndexCommand(t, c)
      case DropIndex(t, c) => new DropIndexCommand(t, c)
      case ExecutorCall(name, l) => new ExecutorCommand(name, l)
    }
    op.children foreach ( buildCommandTree(_) addAsChildrenOf c)
    c
  }

  private def buildJoinCommandTree(ops: Seq[Operation]): Command = {
    ops match {
      case c0 :: Nil => buildCommandTree(c0)
      case a @ c0 :: c1 :: Nil =>
        val l = new LoopJoinCommand
        l.children = List(buildCommandTree(c0), buildCommandTree(c1))
        l
      case c0 :: c1 :: rest =>
        val l = new LoopJoinCommand
        l.children = List(c0, c1) map (buildCommandTree(_))

        val ltop = new LoopJoinCommand
        ltop.children = List(l, buildJoinCommandTree(rest))
        ltop
    }
  }
}
