/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.parameter.string;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.FormatNumberType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

/**
 * This class is used to embed {@code DecimalFormat} in a {@code
 * StringParameter} instance. Indeed, the description of the SE FormatNumber
 * function follows specifications that aregiven in the Java {@code
 * DecimalFormat} class. consequently, this class is mainly a wrapper, that
 * instanciates the appropriate {@code DecimalFormat} instance and use it to
 * format the numbers retrieved by its inner {@code RealParameter}.
 * @author alexis
 */
public class Number2String implements StringParameter {

        private RealParameter numericValue;

        private String formattingPattern;

        private String negativePattern;

        private String groupingSeparator;

        private String decimalPoint;

        private DecimalFormat formatter;

        /**
         * Build a new Number2String, accorgind to the {@code FormatnumberType}
         * given in argument.
         * @param fnt
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
         */
        public Number2String(FormatNumberType fnt) throws InvalidStyle {
                numericValue = SeParameterFactory.createRealParameter(fnt.getNumericValue());
                formattingPattern = fnt.getPattern();
                negativePattern = fnt.getNegativePattern();
                groupingSeparator = fnt.getGroupingSeparator();
                decimalPoint = fnt.getDecimalPoint();
                String pattern;
                if(negativePattern != null &&
                        !negativePattern.isEmpty() &&
                        !(negativePattern.equals(formattingPattern))){
                        pattern = formattingPattern+";"+negativePattern;
                } else {
                        pattern =formattingPattern;
                }
                formatter = new DecimalFormat(pattern);
                DecimalFormatSymbols dfs = formatter.getDecimalFormatSymbols();
                if(decimalPoint != null &&
                        !decimalPoint.isEmpty()){
                        dfs.setDecimalSeparator(decimalPoint.charAt(0));
                }
                if(groupingSeparator != null && !groupingSeparator.isEmpty()){
                        dfs.setGroupingSeparator(groupingSeparator.charAt(0));
                }
                formatter.setDecimalFormatSymbols(dfs);
        }

        /**
         * Build a new {@code Number2String}, using the given {@code
         * JAXBElement<FormatNumberType>} instance given in argument.
         * @param je
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
         */
        public Number2String(JAXBElement<FormatNumberType> je) throws InvalidStyle {
                this(je.getValue());
        }

        @Override
        public String getValue(DataSource sds, long fid) throws ParameterException {
                double val = numericValue.getValue(sds, fid);
                return formatter.format(val);
        }

        @Override
        public void setRestrictionTo(String[] list) {
        }

        @Override
        public String dependsOnFeature() {
                return numericValue.dependsOnFeature();
        }

        @Override
        public ParameterValueType getJAXBParameterValueType() {
                ParameterValueType p = new ParameterValueType();
                p.getContent().add(this.getJAXBExpressionType());
                return p;
        }

        @Override
        public JAXBElement<?> getJAXBExpressionType() {
                ObjectFactory of = new ObjectFactory();
                FormatNumberType fnt = new FormatNumberType();
                fnt.setDecimalPoint(decimalPoint);
                fnt.setGroupingSeparator(groupingSeparator);
                fnt.setNegativePattern(negativePattern);
                fnt.setPattern(formattingPattern);
                fnt.setNumericValue(numericValue.getJAXBParameterValueType());
                return of.createFormatNumber(fnt);
        }

}
