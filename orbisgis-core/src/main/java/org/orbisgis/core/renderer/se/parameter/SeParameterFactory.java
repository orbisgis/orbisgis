/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.parameter;

import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.ogc.BinaryOperatorType;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.ogc.LiteralType;
import org.orbisgis.core.renderer.persistance.ogc.PropertyNameType;
import org.orbisgis.core.renderer.persistance.se.CategorizeType;
import org.orbisgis.core.renderer.persistance.se.InterpolateType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.RecodeType;
import org.orbisgis.core.renderer.persistance.se.UnitaryOperatorType;
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
import org.orbisgis.core.renderer.se.parameter.real.RealUnitaryOperator;
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

    public static RealParameter createRealParameter(JAXBElement<? extends ExpressionType> expr) {
        if (expr == null)
            return null;

        if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.ogc.FunctionType.class) {
            // TODO ??
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

        } else if (expr.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.UnitaryOperatorType.class) {
            return new RealUnitaryOperator((JAXBElement<UnitaryOperatorType>) expr);
        }

        return null;

    }

    public static RealParameter createRealParameter(ParameterValueType p) {
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

    public static ColorParameter createColorParameter(JAXBElement<? extends ExpressionType> expr) {
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


    public static ColorParameter createColorParameter(ParameterValueType p) {
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


    public static StringParameter createStringParameter(JAXBElement<? extends ExpressionType> expr) {
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

    public static StringParameter createStringParameter(ParameterValueType p) {
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
