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
package org.gdms.sql.engine.operations

import org.gdms.sql.function.table.TableFunction
import util.control.Breaks._
import org.gdms.data.indexes.IndexQuery
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.evaluator.Expression
import org.gdms.sql.evaluator.field

/**
 * Abstract query operation.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
abstract sealed class Operation {
  
  def children: List[Operation]
  
  def children_=(o: List[Operation]) {}
  
  /**
   * Returns all children and children of children, etc.
   */
  def allChildren: List[Operation] = children flatMap (ch => ch :: (ch allChildren))

  def validate(): Unit = {
    children foreach (_.validate())
    doValidate()
  }
  
  def doValidate(): Unit = {}
  
  def duplicate: Operation

  /**
   * Better toString method for debugging purposes
   */
  override def toString = super.toString ++ " " ++ children.toString

}

object Operation {
  def unapply(op: Operation) = {
    op.children match {
      case Nil => None
      case a => Some(a)
    }
  }
}

trait ExpressionOperation extends Operation {
  def expr: Seq[Expression]
}

object ExpressionOperation {
  def unapply(o: Operation) = {
    o match {
      case t: ExpressionOperation => Some(t.expr)
      case _ => None
    }
  }
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
  val children = Nil
  override def toString = "Scan of(" + table + ") " + alias + (if (edition) " in edition" else "")
  def duplicate: Scan = copy()
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
                           alias: Option[String] = None) extends Operation with ExpressionOperation {
  val children = tables.flatMap(_.right.toOption).toList
  
  override def toString = "TableFunction  called(" + customQuery + ") params(" + exp + tables + ")" + alias
  var function: TableFunction = null
  override def doValidate() = {
    exp foreach (_ preValidate)
    
    def check(e: Expression) {
      e match {
        case field(n,_) => throw new SemanticException("No field is allowed in a table function: found '" + n + "'.")
        case _ => e.children map (check)
      }
    }
    
    exp map (check)
  }
  def expr = exp
  def duplicate: CustomQueryScan = {
    val c = CustomQueryScan(customQuery, exp map (_.duplicate), tables map { 
        case Right(a) => Right(a.duplicate)
        case b => b
      }, alias)
    if (function != null) {
      c.function = function
    }
    c
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
  val children = Nil
  override def toString = "IndexQueryScan of(" + table + ") " + alias + {if (query != null) {
      " on " + query.getFieldNames + (if (query.isStrict) " strict" else "")
    } else ""}
  def duplicate: IndexQueryScan = copy()
}

/**
 * Represents a 'constant table' on a list of expressions.
 * 
 * @param exp a list of rows (seq of expressions that do not reference fields)
 * @param alias an optional alias for this table
 * @author Antoine Gourlay
 * @since 0.3
 */
case class ValuesScan(exp: Seq[Seq[Expression]], alias: Option[String] = None, internal: Boolean = true) 
extends Operation with ExpressionOperation {
  override def toString = "ValuesScan" + (if (internal) "(internal)" else "") + " of (" + exp + ") " + (if (alias.isDefined) alias else "")
  val children = Nil
  override def doValidate() {
    // check for constant values
    def check(e: Expression): Unit = e match {
      case field(name,_) => throw new SemanticException("the expression cannot contain the field '" + name +
                                                        "'. The expression must be constant.")
      case _ => e.children map (check)
    }
      
    exp foreach (_ foreach (check))
      
    // check for number of elements in rows
    val s = exp.head.size
    exp.tail foreach {e =>
      if (e.size != s) {
        throw new SemanticException("Rows must all have the same number of elements.")
      }
    }
  }
  def expr = exp flatten
  def duplicate: ValuesScan = ValuesScan(exp map (_ map (_.duplicate)), alias, internal)
}
  

/**
 * Represents the output or end node of the Operation tree.
 * 
 * This operation is the top operation of any instruction.
 *
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Output(var child: Operation) extends Operation {
  
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  
  override def doValidate() = {
    val aliases = allChildren flatMap {
      case Scan(_, a, _) => a
      case CustomQueryScan(_, _, _, a) => a
      case SubQuery(a, _) => a :: Nil
      case ValuesScan(_, a, _) => a
      case _ => Nil
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
  def duplicate: Output = Output(child.duplicate)
}

/**
 * Represents the filtering of input row to remove duplicates.
 *
 * @author Antoine Gourlay
 * @since 0.3
 */
case class Distinct(var child: Operation) extends Operation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  
  override def toString = "Distinct on(" + children + ')'
  def duplicate: Distinct = Distinct(child.duplicate)
}

/**
 * Reprensents the limiting of some input row to a specific limit and/or offset.
 *
 * @param limit an optional number of rows to limit the input
 * @param offset an optional offset of rows to skip before returning any
 * @author Antoine Gourlay
 * @since 0.1
 */
case class LimitOffset(limit: Int = -1, offset:Int = 0,var child: Operation) extends Operation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
    
  override def toString = "LimitOffset lim=" + limit + " offset=" + offset + "of(" + children + ")"
  def duplicate: LimitOffset = LimitOffset(limit, offset, child.duplicate)
}

/**
 * Reprensents a subquery. Its only child has to be an Output operation.
 *
 * @param alias reprensents a mandatory alias for the resulting 'table'
 * @author Antoine Gourlay
 * @since 0.1
 */
case class SubQuery(alias: String,var child: Operation) extends Operation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  "SubQuery called(" + alias + ") of(" + children + ")"
  def duplicate: SubQuery = SubQuery(alias, child.duplicate)
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
case class Projection(exp: List[(Expression, Option[String])],var child: Operation) 
extends Operation with ExpressionOperation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  override def doValidate() = exp foreach (_._1 preValidate)
  override def toString = "Projection of(" + exp + ") on(" + children + ")"
  def expr = exp map (_._1)
  def duplicate: Projection = Projection(exp map (a => (a._1.duplicate, a._2)), child.duplicate)
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
case class Aggregate(var exp: List[(Expression, Option[String])],var child: Operation) 
extends Operation with ExpressionOperation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  override def doValidate() = exp foreach (_._1 preValidate)
  override def toString = "Aggregate exp(" + exp + ") on(" + children + ")"
  def expr = exp map (_._1)
  def duplicate: Aggregate = Aggregate(exp map (a => (a._1.duplicate, a._2)), child.duplicate)
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
case class Filter(e: Expression,var child: Operation, having: Boolean = false) extends Operation with ExpressionOperation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  override def doValidate() = e.preValidate
  override def toString = "Filter of(" + e + ") on(" + children + ")"
  def expr = Seq(e)
  def duplicate: Filter = Filter(e.duplicate, child.duplicate, having)
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
case class Sort(names: Seq[(Expression, Boolean)],var child: Operation) extends Operation with ExpressionOperation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  override def doValidate() = names foreach (_._1 preValidate)
  override def toString = "Sort fields(" + names + ") on(" + children + ")"
  def expr = names map (_._1)
  def duplicate: Sort = Sort(names map (a => (a._1.duplicate, a._2)), child.duplicate)
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
case class Grouping(var exp: List[(Expression, Option[String])],var child: Operation) extends Operation with ExpressionOperation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  override def doValidate() = exp foreach (_._1 preValidate)
  override def toString = "Group over(" + exp + ") on(" + children + ")"
  def expr = exp map (_._1)
  def duplicate: Grouping = Grouping(exp map (a => (a._1.duplicate, a._2)), child.duplicate)
}

/**
 * Represents a join operation.
 * 
 * @param joinType the type of the join on the child operations.
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Join(var joinType: JoinType, var left: Operation,var right: Operation) extends Operation
                                                                                     with ExpressionOperation {
  def children = List(left, right)
  override def children_=(o: List[Operation]) = {o match {
      case a :: b :: Nil => left = a; right = b;
      case _ => throw new IllegalArgumentException("Needs two children extactly.")
    }}
  override def toString = "Join type(" + joinType + ") on(" + children + ")"
  def expr = {
    joinType match {
      case OuterLeft(c) => c.toSeq
      case OuterFull(c) => c.toSeq
      case Inner(c,_,_) => Seq(c)
      case _ => Nil
    }
  }
  def duplicate: Join = Join(joinType.duplicate, left.duplicate, right.duplicate)
}


/**
 * Represents an Union of two Select instructions.
 * 
 * @author Antoine Gourlay
 * @since 0.3
 */
case class Union(var left: Operation,var right: Operation) extends Operation {
  def children = List(left, right)
  override def children_=(o: List[Operation]) = {o match {
      case a :: b :: Nil => left = a; right = b;
      case _ => throw new IllegalArgumentException("Needs two children extactly.")
    }}
  override def toString = "Union of (" + children + ")"
  def duplicate: Union = Union(left.duplicate, right.duplicate)
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
case class Update(exp: Seq[(String, Expression)],var child: Operation) extends Operation with ExpressionOperation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  override def doValidate() = exp foreach (_._2 preValidate)
  override def toString = "Update exp(" + exp + ") on (" + children + ")"
  def expr = exp map (_._2)
  def duplicate: Update = Update(exp map (a => (a._1, a._2.duplicate)), child.duplicate)
}

/**
 * Represents a insert into a table. Values to insert are taken from the child command.
 * 
 * @param table the table that has to be updated
 * @author Antoine Gourlay
 * @since 0.3
 */
case class Insert(table: String, fields: Option[Seq[String]], var child: Operation) extends Operation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  override def toString = "Insert into(" + table + ") from (" + child + ")"
  def duplicate: Insert = Insert(table, fields, child.duplicate)
}

/**
 * Represents a deletion operation.
 * 
 * @author Antoine Gourlay
 * @since 0.1
 */
case class Delete(var child: Operation) extends Operation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  override def toString = "Deletes on(" + children + ")"
  def duplicate: Delete = Delete(child.duplicate)
}


/**
 * Represents the creation of a table from the results of a query.
 * 
 * @param name the name of the resulting table
 * @author Antoine Gourlay
 * @since 0.1
 */
case class CreateTableAs(name: String,var child: Operation) extends Operation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  override def toString = "CreateTableAs name(" + name + ") as(" + children + ")"
  def duplicate: CreateTableAs = CreateTableAs(name, child.duplicate)
}

/**
 * Represents the creation of a view from a query.
 * 
 * @param name the name of the resulting view
 * @author Antoine Gourlay
 * @since 0.1
 */
case class CreateView(name: String, sql: String, orReplace: Boolean,var child: Operation) extends Operation {
  def children = List(child)
  override def children_=(o: List[Operation]) = {o.headOption.map(child = _)}
  override def toString = "CreateViewAs name(" + name + ", '" + sql + "') as(" + children + ")" +
  (if (orReplace) " replace" else "")
  def duplicate: CreateView = CreateView(name, sql, orReplace, child.duplicate)
}


/**
 * Represents the creation of an empty table from some metadata.
 * 
 * @param name the name of the resulting table
 * @param cols a list of column names and types
 * @author Antoine Gourlay
 * @since 0.1
 */
case class CreateTable(name: String, cols: Seq[(String, String, Seq[ConstraintType])]) extends Operation {
  def children = Nil
  override def toString = "CreateTableAs name(" + name + ") as(" + cols + ")"
  def duplicate: CreateTable = CreateTable(name, cols)
}

/**
 * Represents an alter instruction.
 * 
 * @param name the name of the table to alter.
 * @param actions the list of actions to make on the table
 * @author Antoine Gourlay
 * @since 0.1
 */
case class AlterTable(name: String, actions: Seq[AlterElement]) extends Operation with ExpressionOperation {
  def children = Nil
  override def toString = "AlterTable name(" + name + ") do(" + actions + ")"
  def duplicate: AlterTable = AlterTable(name, actions)
  val expr: Seq[Expression] = actions.flatMap {
    case AlterTypeOfColumn(_,_,ex) => ex
    case _ => Nil
  }
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
  def children = Nil
  override def toString = "RenameTable name(" + name + ") newname(" + newname + ")"
  def duplicate: RenameTable = RenameTable(name, newname)
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
  def children = Nil
  override def toString = "DropTables names(" + names + ") ifExists=" + ifExists + " purge=" + purge
  def duplicate: DropTables = DropTables(names, ifExists, purge)
}
  
/**
 * Represents a drop instruction, i.e. unregisters whole schemas (with sub-tables)
 * 
 * @param names a list of names of tables to drop
 * @param ifExists true if no error should be thrown in the case of a non-existent table
 * @param purge true if the tables are to be physically deleted, not just unregistered
 * @author Antoine Gourlay
 * @since 0.1
 */
case class DropSchemas(names: Seq[String], ifExists: Boolean, purge: Boolean) extends Operation {
  def children = Nil
  override def toString = "DropSchemas names(" + names + ") ifExists=" + ifExists + " purge=" + purge
  def duplicate: DropSchemas = DropSchemas(names, ifExists, purge)
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
  def children = Nil
  override def toString = "DropViews names(" + names + ") ifExists=" + ifExists
  def duplicate: DropViews = DropViews(names, ifExists)
}

/**
 * Represents an index-creation instruction.
 * 
 * @param table the name of the table to index
 * @param column the name of the field to index
 * @author Antoine Gourlay
 * @since 0.1
 */
case class CreateIndex(table: String, columns: Seq[String]) extends Operation {
  def children = Nil
  override def toString = "CreateIndex on(" + table + ", " + columns + ")"
  def duplicate: CreateIndex = CreateIndex(table, columns)
}

/**
 * Represents an index-deletion instruction.
 * 
 * @param table the name of the table with an index
 * @param column the name of the field whose index is to be removed
 * @author Antoine Gourlay
 * @since 0.1
 */
case class DropIndex(table: String, columns: Seq[String]) extends Operation {
  def children = Nil
  override def toString = "DropIndex on(" + table + ", " + columns + ")"
  def duplicate: DropIndex = DropIndex(table, columns)
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
case class ExecutorCall(name: String, tables: Seq[Either[String, Operation]], params: Seq[Expression]) extends Operation with ExpressionOperation {
  def children = tables.flatMap(_.right.toOption).toList
  override def doValidate() = {
    params foreach (_ preValidate)
    
    def check(e: Expression) {
      e match {
        case field(n,_) => throw new SemanticException("No field is allowed in an executor function: found '" + n + "'.")
        case _ => e.children map (check)
      }
    }
    
    params map (check)
  }
  override def toString = "ExecutorFunction name(" + name + ") " + " params(" + params + ")"
  def expr = params
  def duplicate: ExecutorCall = ExecutorCall(name, tables map { 
      case Right(a) => Right(a.duplicate)
      case b => b
    }, params)
}

/**
 * Represents setting a runtime parameter of the Engine.
 * 
 * @param parameter name of the runtime parameter, or None for all values
 * @param value the value, or None if to use the default value
 * @author Antoine Gourlay
 * @since 0.3
 */
case class Set(parameter: Option[String], value: Option[String]) extends Operation {
  def children = Nil
  override def toString = "Set '" + parameter + "' TO " + value
  def duplicate: Set = Set(parameter, value)
}

/**
 * Represents showing a runtime parameter of the Engine.
 * 
 * @param parameter name of the runtime parameter, or None if all must be shown
 * @author Antoine Gourlay
 * @since 0.3
 */
case class Show(parameter: Option[String]) extends Operation {
  def children = Nil
  override def toString = "Show '" + parameter + "'"
  def duplicate: Show = Show(parameter)
}

/**
 * Adds a function to the Gdms funtion manager.
 * 
 * @param name name of the function
 * @param as language specific string describing the function
 * @param language language name
 * @param replace true if any existing function must be silently replaced
 * @author Antoine Gourlay
 * @since 0.3
 */
case class CreateFunction(name: String, as: String, language: String, replace: Boolean) extends Operation {
  def children = Nil
  override def doValidate() = {
    if (language != "java") {
      throw new SemanticException("Unknown language: '" + language + "'. The only supported language is 'java'.")
    }
  }
  override def toString = "CreateFunction (" + name + ", " + as + ", " + language + ", replace=" + replace + ")"
  def duplicate: CreateFunction = CreateFunction(name, as, language, replace)
}

/**
 * Removes a function from the Gdms function manager.
 * 
 * @param name name of the function
 * @param ifExists true if no error should be thrown when there is no function with that name
 * @author Antoine Gourlay
 * @since 0.3
 */
case class DropFunction(name: String, ifExists: Boolean) extends Operation {
  def children = Nil
  override def toString = "DropFunction (" + name + ", ifExists=" + ifExists + ")"
  def duplicate: DropFunction = DropFunction(name, ifExists)
}
