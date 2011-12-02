/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
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
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */

package org.gdms.sql.engine.operations

import util.control.Breaks._
import org.gdms.data.indexes.IndexQuery
import org.gdms.data.types.IncompatibleTypesException
import org.gdms.data.types.Type
import org.gdms.data.types.TypeFactory
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.evaluator.Expression
import org.gdms.sql.evaluator.FieldEvaluator
import org.gdms.sql.function.FunctionException
import org.gdms.sql.function.FunctionManager

/**
 * Abstract query operation.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
class Operation {
  def addChild(c: Operation) = {
    children = c :: children
    this
  }

  var children: List[Operation] = Nil

  /**
   * Returns all children and children of children, etc.
   */
  def allChildren: List[Operation] = children flatMap (ch => ch :: (ch allChildren))

  def validate: Unit = {
    children foreach (_.validate)
    doValidate
  }
  
  def doValidate: Unit = {}

  /**
   * Better toString method for debugging purposes
   */
  override def toString = super.toString ++ " " ++ children.toString

}

/**
 * Represents a table scan.
 * 
 * @param table the name of a registered table
 * @param alias an optional alias for this table
 * @param edition true if the underlying datasource has to be opened with edition capabilities, for
 *    use with instructions that edit while scanning
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Scan(table: String, alias: Option[String] = None, var edition: Boolean = false) extends Operation {
  override def toString = "Scan of(" + table + ") " + alias + (if (edition) " in edition" else "")
}

/**
 * Represents a CustomQuery call.
 * 
 * @param customQuery the name of a registered custom query
 * @param exp the expressions given as arguments to the custom query call
 * @param tables the table names or query operation of the input tables of the custom query
 * @param alias an optional alias for the resulting table
 * @author Antoine Gourlay
 * @since 0.1
 */
case class CustomQueryScan(customQuery: String, exp: Seq[Expression],
                           tables: Seq[Either[String, Operation]],
                           alias: Option[String] = None) extends Operation {
  override def toString = "TableFunction  called(" + customQuery + ") params(" + exp + tables + ")" + alias
  val function = FunctionManager.getFunction(customQuery)
  override def doValidate = {
    if (function == null) throw new FunctionException("The function " + customQuery + " does not exist.")
    if (!function.isTable) throw new FunctionException("The function " + customQuery + " does not return a table.")
    exp foreach (_ preValidate)
  }
}


/**
 * Represents a index query scan.
 * 
 * @param table the name of a registered table
 * @param alias an optional alias for this table
 * @param query the query to use
 * @author Antoine Gourlay
 * @since 0.3
 */
case class IndexQueryScan(table: String, alias: Option[String] = None, query: IndexQuery = null) extends Operation {
  override def toString = "IndexQueryScan of(" + table + ") " + alias + {if (query != null) {
      " on " + query.getFieldName + (if (query.isStrict) " strict" else "")
    } else ""}
}

/**
 * Represents a 'constant table' on a list of expressions.
 * 
 * @param exp a list of rows (seq of expressions that do not reference fields)
 * @param alias an optional alias for this table
 * @author Antoine Gourlay
 * @since 0.3
 */
case class ValuesScan(exp: Seq[Seq[Expression]], alias: Option[String] = None) extends Operation {
  override def toString = "ValuesScan of (" + exp + ") " + (if (alias.isDefined) alias else "")
}
  

/**
 * Represents the output or end node of the Operation tree.
 * 
 * This operation is the top operation of any instruction.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Output() extends Operation {
  
  override def doValidate = {
    val aliases = allChildren flatMap { _ match {
        case Scan(_, a, _) => a
        case CustomQueryScan(_, _, _, a) => a
        case SubQuery(a) => a :: Nil
        case _ => Nil
      }
    }
    if (hasDuplicates(aliases)) {
      throw new SemanticException("Two tables cannot have the same alias!")
    }
  }

  def hasDuplicates(seq: Seq[String]): Boolean = {
    val h = collection.mutable.HashSet[String]()
    var ret = false
    breakable {
      for (x <- seq) {
        if (h(x)) {
          ret = true; break
        } else {
          h+=x
        }
      }
    }
    ret
  }
  
  override def toString = "Output(" + children +")"
}

/**
 * Represents the filtering of input row to remove duplicates.
 *
 * @author Antoine Gourlay
 * @since 0.3
 */
case class Distinct() extends Operation {
  override def toString = "Distinct on(" + children + ')'
}

/**
 * Reprensents the limiting of some input row to a specific limit and/or offset.
 *
 * @param limit an optional number of rows to limit the input
 * @param offset an optional offset of rows to skip before returning any
 * @author Antoine Gourlay
 * @since 0.1
 */
case class LimitOffset(limit: Int = -1, offset:Int = 0) extends Operation {
  override def toString = "LimitOffset lim=" + limit + " offset=" + offset + "of(" + children + ")"
}

/**
 * Reprensents a subquery. Its only child has to be an Output operation.
 *
 * @param alias reprensents a mandatory alias for the resulting 'table'
 * @author Antoine Gourlay
 * @since 0.1
 */
case class SubQuery(alias: String) extends Operation {
  "SubQuery called(" + alias + ") of(" + children + ")"
}

/**
 * Represents a projection with expression evaluation.
 * 
 * Then given expressions are evaluated for each input rows, with an optional alias.
 * 
 * @param exp list of expressions, with an optional alias name for the resulting 'column'
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Projection(exp: List[(Expression, Option[String])]) extends Operation {
  override def doValidate = exp foreach (_._1 preValidate)
  override def toString = "Projection of(" + exp + ") on(" + children + ")"
}

/**
 * Represents an aggregation of rows.
 * 
 * The AggregateEvaluator objects are evaluated for every input row. Then they are replaced with their aggregate result
 * and the expressions are evaluated and returned as a single row.
 * 
 * @param exp list of expressions, with an optional alias name for the resulting 'column'
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Aggregate(exp: List[(Expression, Option[String])]) extends Operation {
  override def doValidate = exp foreach (_._1 preValidate)
  override def toString = "Aggregate exp(" + exp + ") on(" + children + ")"
}

/**
 * Represents a filter operation.
 * 
 * For every input row, the given expression is evaluated and acts as the condition for keeping the current row.
 * 
 * @param e an expression whose result must be a SQL tri-state Boolean value.
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Filter(e: Expression) extends Operation {
  override def doValidate = {
    e.preValidate
    if (e.evaluator.sqlType != Type.BOOLEAN) {
      throw new IncompatibleTypesException("The WHERE expression must return a BOOLEAN. Its return type is: "
                                           + TypeFactory.getTypeName(e.evaluator.sqlType))
    }
  }
  override def toString = "Filter of(" + e + ") on(" + children + ")"
}

/**
 * Represents a sorting operation.
 * 
 * The rows are sorted according to the parameter sequence of expressions, in descending order if the boolean parameter is
 * set to true.
 * No guaranty is made on the specific ordering algorithm nor on it being stable. The method is not guaranteed to be pure,
 * although it does not have any side-effect on the dataset itself.
 * 
 * @param names a list of expressions and orders (false = asc, true = desc) to be sorted against
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Sort(names: Seq[(Expression, Boolean)]) extends Operation {
  override def doValidate = names foreach (_._1 preValidate)
  override def toString = "Sort fields(" + names + ") on(" + children + ")"
}

/**
 * Represents a grouping operation.
 * 
 * The input rows are grouped using the result of the evaluation of the parameter sequence of expression.
 * No guaranty is made on the specific grouping method, nor on the ordering within the groups. It is guaranteed however
 * that the groups will always be the same and will use the ordering defined in Gdms for {@link Value} objects.
 * 
 * @param exp a list of expressions whose result are used to group the rows, with an optional alias for each expression
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Grouping(exp: List[(Expression, Option[String])]) extends Operation {
  override def doValidate = exp foreach (_._1 preValidate)
  override def toString = "Group over(" + exp + ") on(" + children + ")"
}

/**
 * Represents a join operation.
 * 
 * @param joinType the type of the join on the child operations.
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Join(var joinType: JoinType) extends Operation {
  override def toString = "Join type(" + joinType + ") on(" + children + ")"
}


/**
 * Represents an Union of two Select instructions.
 * 
 * @author Antoine Gourlay
 * @since 0.3
 */
case class Union() extends Operation {
  override def toString = "Union of (" + children + ")"
}

/**
 * Represents an Update instruction.
 * 
 * For every input row, the parameter expressions are evaluated and the results are attributed to the given column names.
 * 
 * @param exp a list of columns and expressions which results set the new values of the columns.
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Update(exp: Seq[(String, Expression)]) extends Operation {
  override def doValidate = exp foreach (_._2 preValidate)
  override def toString = "Update exp(" + exp + ") on (" + children + ")"
}

/**
 * Represents a insert of a list of "static" rows, i.e. insertion of a list of constant rows.
 * 
 * The expressions in the rows must be constant, i.e. be only constants or expressions containing only constants. The only
 * type of function that can be used is scalar functions.
 * 
 * @param table the table that has to be updated
 * @param exps list of rows to be inserted
 * @author Antoine Gourlay
 * @since 0.1
 */
case class StaticInsert(table: String, exps: Seq[Array[Expression]], fields: Option[Seq[String]]) extends Operation {
  override def doValidate = {
    exps foreach (_ foreach (_ preValidate))
    if (fields.isDefined) {
      val l = fields.get.length
      exps foreach(a => if (a.length != l) {
          throw new SemanticException("There is a different number of fields and values to insert.")
        })
    }
    exps foreach (_ foreach ( noFields(_) ))
  }
  
  private def noFields(e: Expression): Unit = {
    e.evaluator match {
      case f: FieldEvaluator => throw new SemanticException("the expression cannot contain the field '" + f.name +
                                                            "'. The expression must be constant.")
      case _ =>
    }
    e.foreach ( noFields(_) )
  }
  
  override def toString = "Insert into(" + table + ") of(" + exps + ")"
}

/**
 * Represents a deletion operation.
 * 
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Delete() extends Operation {
  override def toString = "Deletes on(" + children + ")"
}


/**
 * Represents the creation of a table from the results of a query.
 * 
 * @param name the name of the resulting table
 * @author Antoine Gourlay
 * @since 0.1
 */
case class CreateTableAs(name: String) extends Operation {
  override def toString = "CreateTableAs name(" + name + ") as(" + children + ")"
}

/**
 * Represents the creation of a view from a query.
 * 
 * @param name the name of the resulting view
 * @author Antoine Gourlay
 * @since 0.1
 */
case class CreateView(name: String, orReplace: Boolean) extends Operation {
  override def toString = "CreateViewAs name(" + name + ") as(" + children + ")" +
  (if (orReplace) " replace" else "")
}


/**
 * Represents the creation of an empty table from some metadata.
 * 
 * @param name the name of the resulting table
 * @param cols a list of column names and types
 * @author Antoine Gourlay
 * @since 0.1
 */
case class CreateTable(name: String, cols: Seq[(String, String)]) extends Operation {
  override def toString = "CreateTableAs name(" + name + ") as(" + cols + ")"
}

/**
 * Represents an alter instruction.
 * 
 * @param name the name of the table to alter.
 * @param actions the list of actions to make on the table
 * @author Antoine Gourlay
 * @since 0.1
 */
case class AlterTable(name: String, actions: Seq[AlterElement]) extends Operation {
  override def toString = "AlterTable name(" + name + ") do(" + actions + ")"
}


/**
 * Represents an ALTER TABLE .. RENAME TO .. instruction.
 * 
 * @param name the name of the table to alter.
 * @param newname the new name of the table
 * @author Antoine Gourlay
 * @since 0.1
 */
case class RenameTable(name: String, newname: String) extends Operation {
  override def toString = "RenameTable name(" + name + ") newname(" + newname + ")"
}

/**
 * Represents a drop instruction, i.e. unregisters tables
 * 
 * @param names a list of names of tables to drop
 * @param ifExists true if no error should be thrown in the case of a non-existent table
 * @param purge true if the tables are to be physically deleted, not just unregistered
 * @author Antoine Gourlay
 * @since 0.1
 */
case class DropTables(names: Seq[String], ifExists: Boolean, purge: Boolean) extends Operation {
  override def toString = "DropTables names(" + names + ") ifExists=" + ifExists + " purge=" + purge
}

/**
 * Represents a drop view instruction, i.e. unregisters views
 * 
 * @param names a list of names of views to drop
 * @param ifExists true if no error should be thrown in the case of a non-existent table
 * @author Antoine Gourlay
 * @since 0.1
 */
case class DropViews(names: Seq[String], ifExists: Boolean) extends Operation {
  override def toString = "DropViews names(" + names + ") ifExists=" + ifExists
}

/**
 * Represents an index-creation instruction.
 * 
 * @param table the name of the table to index
 * @param column the name of the field to index
 * @author Antoine Gourlay
 * @since 0.1
 */
case class CreateIndex(table: String, column: String) extends Operation {
  override def toString = "CreateIndex on(" + table + ", " + column + ")"
}

/**
 * Represents an index-deletion instruction.
 * 
 * @param table the name of the table with an index
 * @param column the name of the field whose index is to be removed
 * @author Antoine Gourlay
 * @since 0.1
 */
case class DropIndex(table: String, column: String) extends Operation {
  override def toString = "DropIndex on(" + table + ", " + column + ")"
}

/**
 * Represents a call to a non procedure or ExecutorFunction, i.e. a function that does not return anything.
 * 
 * Parameter expressions can only be constant expressions or operators applied on constant expressions and scalar
 * function calls.
 * 
 * @param name the name of the procedure to call
 * @param params the list of parameter expressions.
 * @author Antoine Gourlay
 * @since 0.1
 */
case class ExecutorCall(name: String, params: List[Expression]) extends Operation {
  override def doValidate = params foreach (_ preValidate)
  override def toString = "ExecutorFunction name(" + name + ") " + " params(" + params + ")"
}