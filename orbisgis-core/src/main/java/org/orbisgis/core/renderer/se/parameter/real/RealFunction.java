/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.renderer.se.parameter.real;

import java.util.*;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.FunctionType;
import net.opengis.fes._2.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;

/**
 * Defines a function on real numbers. A function is defined with a operation and
 * a set of operands. Available operations are :<br/>
 *   * addition - <code>ADD</code><br/>
 *   * Multiplication - <code>MUL</code><br/>
 *   * Division - <code>DIV</code><br/>
 *   * Substraction - <code>SUB</code><br/>
 *   * Square root - <code>SQRT</code><br/>
 *   * Decimal logarithm - <code>LOG</code><br/>
 *   * Neperian logarithm - <code>LN</code>
 * @author Maxence Laurent, Alexis Guéganno
 */
public class RealFunction extends AbstractSymbolizerNode implements SeParameter, RealParameter {

    public enum Operators {
        ADD, MUL, DIV, SUB, SQRT, LOG, LN
    };

    private Operators op;
    private RealParameterContext ctx;
    private ArrayList<RealParameter> operands;

    /**
     * Builds an empty <code>RealFunction</code>, where only the operation
     * is defined.
     * @param operator
     */
    public RealFunction(Operators operator){
            op = operator;
            operands = new ArrayList<RealParameter>();
    }

    /**
     * Builds an empty <code>RealFunction</code>, where only the name of the operation
     * is defined.
     * @param name 
     */
    public RealFunction(String name) {
        ctx = RealParameterContext.REAL_CONTEXT;
        this.op = Operators.valueOf(name.toUpperCase());
        operands = new ArrayList<RealParameter>();
    }

    /**
     * Build a <code>RealFunction</code> from a <code>FunctionType</code> instance.
     * As the <code>FunctionType</code>'s tree can contain informations to build both
     * the operation and the operands, this constructor will naturally try to build them
     * all.
     * @param fcn
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public RealFunction(FunctionType fcn) throws InvalidStyle {
        this(fcn.getName());
        for (JAXBElement<? extends Object> expr : fcn.getExpression()) {
            RealParameter rp =SeParameterFactory.createRealParameter(expr);
            operands.add(rp);
            rp.setParent(this);
        }
    }

    /**
     * Build a <code>RealFunction</code> from a <code>JAXBElement</code> instance.
     * @param fcn
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public RealFunction(JAXBElement<FunctionType> fcn) throws InvalidStyle {
        this(fcn.getValue());
    }

    /**
     * Get the instance of {@code Operators} associated to this {@code RealFunction}.
     * @return 
     */
    public Operators getOperator() {
            return op;
    }

    /**
     * Gets the list of operands
     * @return
     */
    public List<RealParameter> getOperands() {
        return operands;
    }

    /**
     * Return i'th operand
     *
     * @param i
     * @return the real parameter
     * @throws IndexOutOfBoundsException if i is out of bounds
     */
    public RealParameter getOperand(int i){
        return operands.get(i);
    }

    /**
     * Add a new operand
     * @param operand the new operand to add
     * @throws ParameterException if this function doesn't support more
     */
    public void addOperand(RealParameter operand) throws ParameterException {
        switch (op) {
            case ADD:
            case MUL:
                this.operands.add(operand);
                operand.setParent(this);
                return;
            case DIV:
            case SUB:
                if (operands.size() < 2) {
                    this.operands.add(operand);
                    operand.setParent(this);
                } else {
                    throw new ParameterException(op + " requires exactly two operands");
                }
                return;
            case SQRT:
            case LN:
            case LOG:
                if (operands.size() < 1) {
                    this.operands.add(operand);
                    operand.setParent(this);
                } else {
                    throw new ParameterException(op + " requires exactly one operand");
                }
                return;
        }
    }

    @Override
    public Double getValue(DataSet sds, long fid) throws ParameterException {
        List<Double>  vals = new LinkedList<Double>();
        for(RealParameter p : operands){
            vals.add(p.getValue(sds, fid));
        }
        return getValue(vals);
    }

    @Override
    public Double getValue(Map<String,Value> map)throws ParameterException {
        List<Double>  vals = new LinkedList<Double>();
        for(RealParameter p : operands){
            vals.add(p.getValue(map));
        }
        return getValue(vals);

    }

    private Double getValue(List<Double> vals) throws ParameterException {
        double result;
        switch (op) {
            case ADD:
                result = 0.0;
                for (Double p : vals) {
                    result += p;
                }
                return result;
            case MUL:
                result = 1.0;
                for (Double p : vals) {
                    result *= p;
                }
                return result;
            case DIV:
                if (vals.size() != 2) {
                    throw new ParameterException("A division requires two arguments !");
                }
                return vals.get(0) / vals.get(1);
            case SUB:
                if (vals.size() != 2) {
                    throw new ParameterException("A subtraction requires two arguments !");
                }
                return vals.get(0) / vals.get(1);
            case SQRT:
                if (vals.size() != 1) {
                    throw new ParameterException("A Square-root requires one argument !");
                }
                return Math.sqrt(vals.get(0));
            case LOG:
                if (vals.size() != 1) {
                    throw new ParameterException("A Log10 requires one argument !");
                }
                return Math.log10(vals.get(0));
            case LN:
                if (vals.size() != 1) {
                    throw new ParameterException("A natural logarithm requires one argument !");
                }
                return Math.log(vals.get(0));
        }
        throw new ParameterException("Unknown function name: " + op.toString());

    }

    @Override
    public String toString() {
        String result = op.toString() + "(";
        for (int i = 0; i < operands.size(); i++) {
            result += operands.get(i).toString();
            if (i < operands.size() - 1) {
                result += ",";
            }
        }
        result += ")";
        return result;
    }

    @Override
    public void setContext(RealParameterContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public RealParameterContext getContext() {
        return ctx;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }


    @Override
    public HashSet<String> dependsOnFeature() {
        HashSet<String> result = new HashSet<String>();
        for (RealParameter p : operands){
            result.addAll(p.dependsOnFeature());
        }
        return result;
    }

    @Override
	public ParameterValueType getJAXBParameterValueType() {
		ParameterValueType p = new ParameterValueType();
		p.getContent().add(this.getJAXBExpressionType());
		return p;
	}

    @Override
    public JAXBElement<?> getJAXBExpressionType() {
        FunctionType fcn = new FunctionType();
        fcn.setName(op.name());
        List<JAXBElement<?>> expression = fcn.getExpression();

        for (RealParameter p : operands){
            expression.add(p.getJAXBExpressionType());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createFunction(fcn);
    }

    @Override
    public UsedAnalysis getUsedAnalysis() {
        UsedAnalysis ua  = new UsedAnalysis();
        ua.include(this);
        for(RealParameter r : operands){
                ua.merge(r.getUsedAnalysis());
        }
        return ua;
    }


}
