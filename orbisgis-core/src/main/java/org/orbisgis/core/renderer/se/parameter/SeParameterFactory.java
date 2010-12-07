/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */


package org.orbisgis.core.renderer.se.parameter;

import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.ogc.BinaryOperatorType;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.ogc.LiteralType;
import org.orbisgis.core.renderer.persistance.ogc.PropertyNameType;
import org.orbisgis.core.renderer.persistance.se.CategorizeType;
import org.orbisgis.core.renderer.persistance.se.InterpolateType;

import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.RecodeType;
import org.orbisgis.core.renderer.persistance.se.UnaryOperatorType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorAttribute;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.color.Interpolate2Color;
import org.orbisgis.core.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.core.renderer.se.parameter.real.Categorize2Real;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealBinaryOperator;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealUnaryOperator;
import org.orbisgis.core.renderer.se.parameter.real.Recode2Real;
import org.orbisgis.core.renderer.se.parameter.string.Categorize2String;
import org.orbisgis.core.renderer.se.parameter.string.Recode2String;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

/**
 *
 * @author maxence
 */
public final class SeParameterFactory {

    private SeParameterFactory() {
    }

    public static RealParameter createRealParameter(JAXBElement<? extends ExpressionType> expr) throws InvalidStyle {
        if (expr == null)
            return null;

        if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.ogc.FunctionType.class) {
            // TODO (for geometry functions)
        } else if (expr.getDeclaredType() == BinaryOperatorType.class) {
            return new RealBinaryOperator((JAXBElement<BinaryOperatorType>) expr);

        } else if (expr.getDeclaredType() == PropertyNameType.class) {
            return new RealAttribute((JAXBElement<PropertyNameType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.ogc.LiteralType.class) {
            return new RealLiteral((JAXBElement<LiteralType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.CategorizeType.class) {
            return new Categorize2Real((JAXBElement<CategorizeType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.RecodeType.class) {
            return new Recode2Real((JAXBElement<RecodeType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.InterpolateType.class) {
            return new Interpolate2Real((JAXBElement<InterpolateType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.UnaryOperatorType.class) {
            return new RealUnaryOperator((JAXBElement<UnaryOperatorType>) expr);
        }

        return null;

    }

    public static RealParameter createRealParameter(ParameterValueType p) throws InvalidStyle {
        if (p == null)
            return null;

        String result = "";

        for (Object o : p.getContent()) {
            if (o instanceof String) {
                result += o.toString();
            } else if (o instanceof JAXBElement) {
                return SeParameterFactory.createRealParameter((JAXBElement<? extends ExpressionType>) o);
            }
        }

        // has not return, so it's a literal !
        return new RealLiteral(result);
    }

    public static ColorParameter createColorParameter(JAXBElement<? extends ExpressionType> expr) throws InvalidStyle {
        if (expr == null)
            return null;

        if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.ogc.FunctionType.class) {
            // TODO ??
        } else if (expr.getDeclaredType() == PropertyNameType.class) {
            return new ColorAttribute((JAXBElement<PropertyNameType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.ogc.LiteralType.class) {
            return new ColorLiteral((JAXBElement<LiteralType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.CategorizeType.class) {
            return new Categorize2Color((JAXBElement<CategorizeType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.RecodeType.class) {
            return new Recode2Color((JAXBElement<RecodeType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.InterpolateType.class) {
            return new Interpolate2Color((JAXBElement<InterpolateType>) expr);
        }

        return null;

    }


    public static ColorParameter createColorParameter(ParameterValueType p) throws InvalidStyle {
        if (p == null)
            return null;

        String result = "";
        for (Object o : p.getContent()) {
            if (o instanceof String) {
                result += ((String) o);
            } else if (o instanceof JAXBElement) {
                return SeParameterFactory.createColorParameter((JAXBElement<? extends ExpressionType>) o);
            }
        }
        // has not return, so it's a literal !
        return new ColorLiteral(result);
    }


    public static StringParameter createStringParameter(JAXBElement<? extends ExpressionType> expr) throws InvalidStyle {
        if (expr == null)
            return null;

        if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.ogc.FunctionType.class) {
            // TODO ??
        } else if (expr.getDeclaredType() == PropertyNameType.class) {
            return new StringAttribute((JAXBElement<PropertyNameType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.ogc.LiteralType.class) {
            return new StringLiteral((JAXBElement<LiteralType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.CategorizeType.class) {
            return new Categorize2String((JAXBElement<CategorizeType>) expr);

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.RecodeType.class) {
            return new Recode2String((JAXBElement<RecodeType>) expr);
        }

        return null;

    }

    public static StringParameter createStringParameter(ParameterValueType p) throws InvalidStyle {
        if (p == null)
            return null;


        String result = "";
        for (Object o : p.getContent()) {
            if (o instanceof String) {
                result += ((String) o);
            } else if (o instanceof JAXBElement) {
                return SeParameterFactory.createStringParameter((JAXBElement<? extends ExpressionType>) o);
            }
        }
        // has not return, so it's a literal !
        return new StringLiteral(result);
    }

    public static ParameterValueType createParameterValueType(String token){
        ParameterValueType p = new ParameterValueType();
        p.getContent().add(token);
        return p;
    }

    public static String extractToken(ParameterValueType p){
        String res = "";
        for (Object o : p.getContent()){
            res += o.toString();
        }
        return res;
    }

}
