/*
 * Bundle sql-parser is part of the OrbisGIS platform
 *
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * sql-parser is distributed under LGPL 3 license.
 *
 * Copyright (C) 2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * sql-parser is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * sql-parser is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * sql-parser. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.sql;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.filter.FunctionFinder;
import org.opengis.filter.expression.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


/**
 * SQLParser allows to convert a select or a where expression to geotools expression of filter model
 *
 * @author Erwan Bocher (CNRS 2020)
 */
public class SQLParser extends ExpressionDeParser {

    Stack<Expression> stack = new Stack<Expression>();
    Stack<Boolean> stackCondition = new Stack<Boolean>();
    private FilterFactoryImpl ff;
    FunctionFinder functionFinder;

    public SQLParser() {

    }

    /**
     * Convert a plain list of sql expressions to ECQL expression
     * @param selectExpression list of sql expression
     * @return
     */
    public Map<String, Expression> toExpressions(String... selectExpression)  {
        HashMap<String, Expression> expressions = new HashMap<String, Expression>();
        String alias = "exp_";
        for(int i = 0; i < selectExpression.length; i++) {
            Expression exp = toExpression(selectExpression[i]);
            if(exp!=null){
                expressions.put(alias+i, exp);
            }
        }
        return expressions;
    }


        /**
         * Convert plain SQL select expression to ECQL Expression
         * @param selectExpression
         * @return
         */
    public Expression toExpression(String selectExpression)  {
        try {
            if (selectExpression != null && !selectExpression.isEmpty()) {
                ff = new FilterFactoryImpl();
                functionFinder = new FunctionFinder(null);
                net.sf.jsqlparser.expression.Expression parseExpression = CCJSqlParserUtil.parseExpression(selectExpression, false);
                StringBuilder b = new StringBuilder();
                setBuffer(b);
                parseExpression.accept(this);
                Expression expression = stack.pop();
                stack.clear();
                stackCondition.clear();
                return expression;
            }
        } catch (JSQLParserException ex) {
            return null;
        }
        return null;
    }

    /**
     * Convert litteral SQL select expression to an Expression
     * @param selectExpression
     * @return
     */
    public Expression toFilter(String selectExpression)  {
        try {
            if (selectExpression != null && !selectExpression.isEmpty()) {
                ff = new FilterFactoryImpl();
                functionFinder = new FunctionFinder(null);
                net.sf.jsqlparser.expression.Expression parseExpression = CCJSqlParserUtil.parseCondExpression(selectExpression, false);
                StringBuilder b = new StringBuilder();
                setBuffer(b);
                parseExpression.accept(this);
                Expression expression = stack.pop();
                stack.clear();
                stackCondition.clear();
                return expression;
            }
        } catch (JSQLParserException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    @Override
    public void visit(SubSelect subSelect) {
       throw new RuntimeException("Sub selection is not supported");
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        super.visit(caseExpression);
        //TODO
        //stack.push(ff.function("if_then_else", whenExpression, thenExpression, elseExpression));

    }

    @Override
    public void visit(EqualsTo equalsTo) {
        Expression[] exp = binaryExpressionConverter(equalsTo);
        stack.push(ff.function("equalTo", exp[0],exp[1]));
    }

    @Override
    public void visit(Addition addition) {
        Expression[] exp = binaryExpressionConverter(addition);
        stack.push(ff.add(exp[0],exp[1]));
    }

    @Override
    public void visit(Multiplication multiplication) {
        Expression[] exp = binaryExpressionConverter(multiplication);
        stack.push(ff.multiply(exp[0],exp[1]));
    }

    @Override
    public void visit(Subtraction subtraction) {
        Expression[] exp = binaryExpressionConverter(subtraction);
        stack.push(ff.subtract(exp[0],exp[1]));
    }

    @Override
    public void visit(Division division) {
        Expression[] exp = binaryExpressionConverter(division);
        stack.push(ff.divide(exp[0],exp[1]));
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        super.visit(doubleValue);
        stack.push(ff.literal(doubleValue.getValue()));
    }

    @Override
    public void visit(StringValue stringValue) {
        super.visit(stringValue);
        stack.push(ff.literal(stringValue.getValue()));
    }

    @Override
    public void visit(LongValue longValue) {
        super.visit(longValue);
        stack.push(ff.literal(longValue.getValue()));
    }

    @Override
    public void visit(Column column) {
        super.visit(column);
        stack.push(ff.property(column.getColumnName()));
    }

    @Override
    public void visit(AndExpression andExpression) {
        Expression[] exp = binaryExpressionConverter(andExpression);
        stack.push(ff.function("and", exp[0], exp[1]));
    }

    @Override
    public void visit(OrExpression orExpression) {
        Expression[] exp = binaryExpressionConverter(orExpression);
        stack.push(ff.function("or", exp[0], exp[1]));
    }

    private Expression[]  binaryExpressionConverter(BinaryExpression binaryExpression){
        net.sf.jsqlparser.expression.Expression leftExpression= binaryExpression.getLeftExpression();
        leftExpression.accept(this);
        Expression left = stack.pop();
        net.sf.jsqlparser.expression.Expression rightExpression = binaryExpression.getRightExpression();
        rightExpression.accept(this);
        Expression right = stack.pop();
        return new Expression[]{left,right};
    }

    @Override
    public void visit(Function function) {
        super.visit(function);
        String functionName = function.getName();
        org.opengis.filter.expression.Function internalFunction = functionFinder.findFunction(functionName, stack);
        stack.clear();
        if(internalFunction!=null){
            stack.push(internalFunction);
        }
    }
}
