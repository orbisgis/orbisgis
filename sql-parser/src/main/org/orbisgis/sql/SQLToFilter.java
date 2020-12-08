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
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.filter.FunctionFinder;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;



/**
 * Class to convert a SQL from conditional expression to a Geotools Filter object
 * see https://docs.geotools.org/latest/userguide/library/opengis/filter.html
 *
 * Note :
 * Doesn't support specific [E]CQL key words as BEFORE, AFTER, DURING
 * Spatial functions with st_ prefix are supported.
 * e.g : ST_Buffer used the Buffer expression from Geotools
 *
 * @author Erwan Bocher (CNRS 2020)
 */
public class SQLToFilter extends ExpressionDeParser {

    private static final String NOT_SUPPORTED_YET = "Not supported yet.";
    private static final String NOT_SUPPORTED = "Not supported.";

    Stack<Filter> stack = new Stack<Filter>();
    Stack<Expression> expressionStack = new Stack<Expression>();
    Stack<Boolean> stackCondition = new Stack<Boolean>();
    private FilterFactoryImpl ff;
    FunctionFinder functionFinder;

    public SQLToFilter() {
        ff = new FilterFactoryImpl();
        functionFinder = new FunctionFinder(null);
    }

    /**
     * Convert a sql expression to ECQL filter
     * @param selectExpression list of plain sql expression
     */
    public static Filter transform(String selectExpression)  {
        SQLToFilter sqlToExpression = new SQLToFilter();
        return sqlToExpression.parse(selectExpression);
    }


        /**
         * Convert plain SQL select expression to ECQL Expression
         * @param selectExpression
         * @return
         */
    public Filter parse(String selectExpression)  {
        try {
            if (selectExpression != null && !selectExpression.isEmpty()) {
                net.sf.jsqlparser.expression.Expression parseExpression = CCJSqlParserUtil.parseCondExpression(selectExpression, false);
                if(parseExpression instanceof BinaryExpression || parseExpression instanceof  IsNullExpression || parseExpression instanceof Between) {
                    StringBuilder b = new StringBuilder();
                    setBuffer(b);
                    parseExpression.accept(this);
                    if (!stack.empty()) {
                        Filter filter = stack.pop();
                        stack.clear();
                        expressionStack.clear();
                        stackCondition.clear();
                        return filter;
                    }
                }

            }
        } catch (JSQLParserException ex) {
            return null;
        }
        return null;
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        Expression[] exp = binaryExpressionConverter(greaterThanEquals);
        stack.push(ff.greaterOrEqual(exp[0],exp[1]));
    }

    @Override
    public void visit(MinorThan minorThan) {
        Expression[] exp = binaryExpressionConverter(minorThan);
        stack.push(ff.less(exp[0],exp[1]));
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        Expression[] exp = binaryExpressionConverter(minorThanEquals);
        stack.push(ff.lessOrEqual( exp[0],exp[1]));
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        Expression[] exp = binaryExpressionConverter(notEqualsTo);
        stack.push(ff.notEqual( exp[0],exp[1]));
    }


    @Override
    public void visit(GreaterThan greaterThan) {
        Expression[] exp = binaryExpressionConverter(greaterThan);
        stack.push(ff.greater( exp[0],exp[1]));
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        Expression[] exp = binaryExpressionConverter(equalsTo);
        stack.push(ff.equals( exp[0],exp[1]));
    }

    @Override
    public void visit(Addition addition) {
        Expression[] exp = binaryExpressionConverter(addition);
        expressionStack.push(ff.add(exp[0],exp[1]));
    }

    @Override
    public void visit(Multiplication multiplication) {
        Expression[] exp = binaryExpressionConverter(multiplication);
        expressionStack.push(ff.multiply(exp[0],exp[1]));
    }

    @Override
    public void visit(Subtraction subtraction) {
        Expression[] exp = binaryExpressionConverter(subtraction);
        expressionStack.push(ff.subtract(exp[0],exp[1]));
    }

    @Override
    public void visit(Division division) {
        Expression[] exp = binaryExpressionConverter(division);
        expressionStack.push(ff.divide(exp[0],exp[1]));
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        super.visit(doubleValue);
        expressionStack.push(ff.literal(doubleValue.getValue()));
    }

    @Override
    public void visit(StringValue stringValue) {
        super.visit(stringValue);
        expressionStack.push(ff.literal(stringValue.getValue()));
    }

    @Override
    public void visit(LongValue longValue) {
        super.visit(longValue);
        expressionStack.push(ff.literal(longValue.getValue()));
    }

    @Override
    public void visit(Column column) {
        super.visit(column);
        expressionStack.push(ff.property(column.getColumnName()));
    }

    @Override
    public void visit(AndExpression andExpression) {
        Filter[] filters = binaryFilterConverter(andExpression);
        stack.push(ff.and(filters[0], filters[1]));
    }

    @Override
    public void visit(OrExpression orExpression) {
        Filter[] filters = binaryFilterConverter(orExpression);
        stack.push(ff.or(filters[0], filters[1]));
    }

    private Expression[]  binaryExpressionConverter(BinaryExpression binaryExpression){
        net.sf.jsqlparser.expression.Expression leftExpression= binaryExpression.getLeftExpression();
        leftExpression.accept(this);
        Expression left =  expressionStack.pop();
        net.sf.jsqlparser.expression.Expression rightExpression = binaryExpression.getRightExpression();
        rightExpression.accept(this);
        Expression right = expressionStack.pop();
        return new Expression[]{left,right};
    }

    private Filter[]  binaryFilterConverter(BinaryExpression binaryExpression){
        net.sf.jsqlparser.expression.Expression leftExpression= binaryExpression.getLeftExpression();
        leftExpression.accept(this);
        Filter left = stack.pop();
        net.sf.jsqlparser.expression.Expression rightExpression = binaryExpression.getRightExpression();
        rightExpression.accept(this);
        Filter right = stack.pop();
        return new Filter[]{left,right};
    }

    @Override
    public void visit(Function function) {
        super.visit(function);
        String functionName = function.getName().toLowerCase();
        org.opengis.filter.expression.Function internalFunction=null;
        try {
             internalFunction = functionFinder.findFunction(functionName, expressionStack);
        }catch (RuntimeException ex) {
            if (internalFunction == null) {
                //Workaround for spatial functions with st_ prefix
                if (functionName.startsWith("st_")) {
                    internalFunction = functionFinder.findFunction(function.getName().substring(3), expressionStack);
                }
            }
        }
        expressionStack.clear();
        if(internalFunction!=null){
            expressionStack.push(internalFunction);
        }
    }

    @Override
    public void visit(SubSelect subSelect) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }

    @Override
    public void visit(Between between) {
        net.sf.jsqlparser.expression.Expression leftExpression= between.getLeftExpression();
        leftExpression.accept(this);
        Expression left = expressionStack.pop();
        net.sf.jsqlparser.expression.Expression startBetween = between.getBetweenExpressionStart();
        startBetween.accept(this);
        Expression start = expressionStack.pop();
        net.sf.jsqlparser.expression.Expression endBetween = between.getBetweenExpressionEnd();
        endBetween.accept(this);
        Expression end = expressionStack.pop();
        stack.push(ff.between(left, start, end));
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
        Expression left = expressionStack.pop();
        if(not){
            stack.push(ff.not(ff.isNull(left)));
        }
        else{
            stack.push(ff.isNull(left));
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
