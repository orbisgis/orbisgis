/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.parameter;

import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.ExpressionType;
import net.opengis.fes._2.FunctionType;
import net.opengis.fes._2.LiteralType;
import net.opengis.fes._2.ValueReferenceType;
import net.opengis.se._2_0.core.*;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.color.*;
import org.orbisgis.core.renderer.se.parameter.real.*;
import org.orbisgis.core.renderer.se.parameter.string.*;

/**
 * This class will build <code>SeParameter</code> instances using the given <code>JAXBElement</code> instances.
 * @author Alexis Guéganno, Maxence Laurent
 */
public final class SeParameterFactory {

    private SeParameterFactory() {
    }

    /**
     * Using the <code>JAXBElement</code> given in argument, tries to build a new 
     * <code>RealParameter</code>.
     * @param expr
     * @return
     * <ul><li><code>null</code> if <code>expr == null</code>, or if expr does not represent 
     * a <code>RealParameter</code> of any kind</li>
     * <li>A <code>RealParameter</code> otherwise. </li>
     * </ul>
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public static RealParameter createRealParameter(Object expr) throws InvalidStyle {
        if (expr == null){
            return null;
        }else if (expr instanceof net.opengis.fes._2.FunctionType) {
            return new RealFunction((FunctionType) expr);
        } else if (expr instanceof ValueReferenceType) {
            return new RealAttribute((ValueReferenceType) expr);

        } else if (expr instanceof LiteralType) {
            return new RealLiteral((LiteralType) expr);

        } else if (expr instanceof net.opengis.se._2_0.core.CategorizeType) {
            return new Categorize2Real((CategorizeType) expr);

        } else if (expr instanceof net.opengis.se._2_0.core.RecodeType) {
            return new Recode2Real((RecodeType) expr);

        } else if (expr instanceof net.opengis.se._2_0.core.InterpolateType) {
            return new Interpolate2Real((InterpolateType) expr);
        }

        return null;

    }

    public static RealParameter createRealParameter(JAXBElement expr) throws InvalidStyle {
        if (expr == null){
            return null;
        } else if(expr.getName().getLocalPart().equals("ValueReference") ){
            return new RealAttribute((String) expr.getValue());
        } else {
                return SeParameterFactory.createRealParameter(expr.getValue());
        }

    }

    /**
     * Creates a <code>RealParameter</code> using the given <code>ParametervalueType</code>
     * @param p
     * @return
     * <ul>
     * <li><code>null</code> if <code>expr == null</code>, or if expr does not represent 
     * a <code>RealParameter</code> of any kind</li>
     * <li>A <code>RealParameter</code> otherwise.</li>
     * </ul>
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public static RealParameter createRealParameter(ParameterValueType p) throws InvalidStyle {
        if (p == null){
            return null;
        }

        StringBuilder result = new StringBuilder();

        for (Object o : p.getContent()) {
            if (o instanceof String) {
                result.append(o.toString());
            } else if (o instanceof JAXBElement) {
                return SeParameterFactory.createRealParameter((JAXBElement<? extends ExpressionType>) o);
            }
        }

        // has not return, so it's a literal !
        return new RealLiteral(result.toString());
    }

    /**
     * Build a <code>ColorParameter</code> from the given <code>JAXBElement</code>.
     * @param expr
     * @return
     * <ul><li><code>null</code> if <code>expr == null</code> or if there is not any
     * way to build a <code>ColorParameter</code> from the given argument.</li>
     * <li>A <code>ColorParameter</code> otherwise.</li>
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public static ColorParameter createColorParameter(JAXBElement<? > expr) throws InvalidStyle {
        if (expr == null){
            return null;
        }

        if (expr.getDeclaredType() == net.opengis.fes._2.FunctionType.class) {
            // TODO ??
        } else if (expr.getDeclaredType() == String.class) {
            return new ColorAttribute((JAXBElement<String>) expr);

        } else if (expr.getDeclaredType() == LiteralType.class) {
            return new ColorLiteral((JAXBElement<LiteralType>) expr);

        } else if (expr.getDeclaredType() == net.opengis.se._2_0.core.CategorizeType.class) {
            return new Categorize2Color((JAXBElement<CategorizeType>) expr);

        } else if (expr.getDeclaredType() == net.opengis.se._2_0.core.RecodeType.class) {
            return new Recode2Color((JAXBElement<RecodeType>) expr);

        } else if (expr.getDeclaredType() == net.opengis.se._2_0.core.InterpolateType.class) {
            return new Interpolate2Color((JAXBElement<InterpolateType>) expr);
        }

        return null;

    }

    
    /**
     * Creates a <code>ColorParameter</code> using the given <code>ParametervalueType</code>
     * @param p
     * @return
     * <ul>
     * <li><code>null</code> if <code>expr == null</code>, or if <code>expr</code> does not represent 
     * a <code>ColorParameter</code> of any kind</li>
     * <li>A <code>ColorParameter</code> otherwise.</li>
     * </ul>
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public static ColorParameter createColorParameter(ParameterValueType p) throws InvalidStyle {
        if (p == null){
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (Object o : p.getContent()) {
            if (o instanceof String) {
                result.append((String) o);
            } else if (o instanceof JAXBElement) {
                return SeParameterFactory.createColorParameter((JAXBElement<? extends ExpressionType>) o);
            }
        }
        // has not return, so it's a literal !
        return new ColorLiteral(result.toString());
    }


    /**
     * Build a <code>StringParameter</code> from the given <code>JAXBElement</code>.
     * @param expr
     * @return
     * <ul><li><code>null</code> if <code>expr == null</code> or if there is not any
     * way to build a <code>StringParameter</code> from the given argument.</li>
     * <li>A <code>StringParameter</code> otherwise.</li>
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public static StringParameter createStringParameter(JAXBElement<?> expr) throws InvalidStyle {
        if (expr == null){
            return null;
        }

        if (expr.getDeclaredType() == FormatNumberType.class) {
            return new Number2String((JAXBElement<FormatNumberType>) expr);
        } else if (expr.getDeclaredType() == String.class) {
            return new StringAttribute((JAXBElement<String>) expr);

        } else if (expr.getDeclaredType() == LiteralType.class) {
            return new StringLiteral((JAXBElement<LiteralType>) expr);

        } else if (expr.getDeclaredType() == net.opengis.se._2_0.core.CategorizeType.class) {
            return new Categorize2String((JAXBElement<CategorizeType>) expr);

        } else if (expr.getDeclaredType() == net.opengis.se._2_0.core.RecodeType.class) {
            return new Recode2String((JAXBElement<RecodeType>) expr);
        } else if (expr.getDeclaredType() == ConcatenateType.class){
            return new StringConcatenate((JAXBElement<ConcatenateType>) expr);
        }

        return null;

    }
    
    /**
     * Creates a <code>StringParameter</code> using the given <code>ParametervalueType</code>
     * @param p
     * @return
     * <ul>
     * <li><code>null</code> if <code>expr == null</code>, or if <code>expr</code> does not represent 
     * a <code>StringParameter</code> of any kind</li>
     * <li>A <code>StringParameter</code> otherwise.</li>
     * </ul>
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public static StringParameter createStringParameter(ParameterValueType p) throws InvalidStyle {
        if (p == null){
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (Object o : p.getContent()) {
            if (o instanceof String) {
                result.append((String) o);
            } else if (o instanceof JAXBElement) {
                return SeParameterFactory.createStringParameter((JAXBElement<? extends ExpressionType>) o);
            }
        }
        // has not return, so it's a literal !
        return new StringLiteral(result.toString());
    }

    /**
     * Create a <code>ParameterValueType</code> from the string given in argument.
     * @param token
     * @return 
     */
    public static ParameterValueType createParameterValueType(String token){
        ParameterValueType p = new ParameterValueType();
        p.getContent().add(token);
        return p;
    }

    /**
     * Get a string representation of the content of <code>p</code>
     * @param p
     * @return 
     */
    public static String extractToken(ParameterValueType p){
        StringBuilder res = new StringBuilder();
        for (Object o : p.getContent()){
            res.append(o.toString());
        }
        return res.toString();
    }

}
