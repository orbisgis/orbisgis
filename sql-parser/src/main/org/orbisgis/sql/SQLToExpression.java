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
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.StringProvider;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.filter.FunctionFinder;
import org.opengis.filter.expression.Expression;

import java.util.*;


/**
 * Class to convert a SQL select expression to a Geotools Expression object
 * see https://docs.geotools.org/latest/userguide/library/opengis/filter.html
 *
 * Note :
 * Spatial functions with st_ prefix are supported.
 * e.g : ST_Buffer used the Buffer expression from Geotools
 *
 * @author Erwan Bocher (CNRS 2020)
 */
public class SQLToExpression extends ExpressionDeParser {

    private static final String NOT_SUPPORTED_YET = "Not supported yet.";

    Stack<Expression> stack = new Stack<Expression>();
    Stack<Boolean> stackCondition = new Stack<Boolean>();
    private FilterFactoryImpl ff;
    FunctionFinder functionFinder;

    public SQLToExpression() {
        ff = new FilterFactoryImpl();
        functionFinder = new FunctionFinder(null);
    }

    /**
     * Convert a sql expression to ECQL expression
     * @param selectExpression list of plain sql expression
     * @return a list of ECQL expression with an alias
     */
    public static Expression transform(String selectExpression)  {
        SQLToExpression sqlToExpression = new SQLToExpression();
        return sqlToExpression.parse(selectExpression);
    }

    /**
     * Convert a plain list of sql select expressions to Geotools Expression object
     * @param selectExpression list of plain select sql expressions
     * @return a list of Geotools Expression object with an alias
     */
    public static Map<String, Expression> toExpressions(String... selectExpression)  {
        SQLToExpression sqlToExpression = new SQLToExpression();
        HashMap<String, Expression> expressions = new HashMap<String, Expression>();
        String alias = "exp_";
        for(int i = 0; i < selectExpression.length; i++) {
            Expression exp = sqlToExpression.parse(selectExpression[i]);
            if(exp!=null){
                expressions.put(alias+i, exp);
            }
        }
        return expressions;
    }

    /**
     * Convert select SQL  expression to Geotools Expression object
     * @param selectExpression
     * @return
     */
    public Expression parse(String selectExpression)  {
        try {
            if (selectExpression != null && !selectExpression.isEmpty()) {
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

    @Override
    public void visit(SubSelect subSelect) {
        throw new RuntimeException("Sub selection is not supported");
    }


    @Override
    public void visit(WhenClause whenClause) {
        net.sf.jsqlparser.expression.Expression when = whenClause.getWhenExpression();
        when.accept(this);
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        List<WhenClause> whenClauses = caseExpression.getWhenClauses();
        Expression whenExpression = null;
        Expression  thenExpression =null;
        if(whenClauses.size()>1){
            throw new RuntimeException("Only one clause is supported yet.");
        }
        WhenClause whenClause = whenClauses.get(0);
        net.sf.jsqlparser.expression.Expression when = whenClause.getWhenExpression();
        when.accept(this);
        whenExpression = stack.pop();
        net.sf.jsqlparser.expression.Expression then = whenClause.getThenExpression();
        then.accept(this);
        thenExpression = stack.pop();
        net.sf.jsqlparser.expression.Expression elseExp = caseExpression.getElseExpression();
        elseExp.accept(this);
        Expression  elseExpression = stack.pop();
        stack.push(ff.function("if_then_else", whenExpression, thenExpression, elseExpression));
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        Expression[] exp = binaryExpressionConverter(greaterThanEquals);
        stack.push(ff.function("greaterEqualThan", exp[0],exp[1]));
    }

    @Override
    public void visit(MinorThan minorThan) {
        Expression[] exp = binaryExpressionConverter(minorThan);
        stack.push(ff.function("lessThan", exp[0],exp[1]));
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        Expression[] exp = binaryExpressionConverter(minorThanEquals);
        stack.push(ff.function("lessEqualThan", exp[0],exp[1]));
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        Expression[] exp = binaryExpressionConverter(notEqualsTo);
        stack.push(ff.function("notEqualTo", exp[0],exp[1]));
    }


    @Override
    public void visit(GreaterThan greaterThan) {
        Expression[] exp = binaryExpressionConverter(greaterThan);
        stack.push(ff.function("greaterThan", exp[0],exp[1]));
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
        String functionName = function.getName().toLowerCase();
        org.opengis.filter.expression.Function internalFunction=null;
        try {
            internalFunction = functionFinder.findFunction(functionName, stack);
        }catch (RuntimeException ex) {
            if (internalFunction == null) {
                //Workaround for spatial functions with st_ prefix
                if (functionName.startsWith("st_")) {
                    internalFunction = functionFinder.findFunction(function.getName().substring(3), stack);
                }
            }
        }
        stack.clear();
        if(internalFunction!=null){
            stack.push(internalFunction);
        }
    }

    @Override
    public void visit(Between between) {
        net.sf.jsqlparser.expression.Expression leftExpression= between.getLeftExpression();
        leftExpression.accept(this);
        Expression left = stack.pop();
        net.sf.jsqlparser.expression.Expression startBetween = between.getBetweenExpressionStart();
        startBetween.accept(this);
        Expression start = stack.pop();
        net.sf.jsqlparser.expression.Expression endBetween = between.getBetweenExpressionEnd();
        endBetween.accept(this);
        Expression end = stack.pop();
        stack.push(ff.function("between", start, end));
    }

    @Override
    public void visit(NotExpression notExpr) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }

    @Override
    public void visit(InExpression inExpression) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        boolean not = isNullExpression.isNot();
        net.sf.jsqlparser.expression.Expression leftExpression= isNullExpression.getLeftExpression();
        leftExpression.accept(this);
        Expression left = stack.pop();
        if(not){
            stack.push(ff.function("not", ff.function("isNull", left)));
        }
        else{
            stack.push( ff.function("isNull", left));
        }
    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }

    @Override
    public void visit(NullValue nullValue) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }
}
