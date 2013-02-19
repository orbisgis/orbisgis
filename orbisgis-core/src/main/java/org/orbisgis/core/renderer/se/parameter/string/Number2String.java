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
package org.orbisgis.core.renderer.se.parameter.string;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.FormatNumberType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 * This class is used to embed {@code DecimalFormat} in a {@code
 * StringParameter} instance. Indeed, the description of the SE FormatNumber
 * function follows specifications that aregiven in the Java {@code
 * DecimalFormat} class. consequently, this class is mainly a wrapper, that
 * instanciates the appropriate {@code DecimalFormat} instance and use it to
 * format the numbers retrieved by its inner {@code RealParameter}.
 * @author Alexis Guéganno
 */
public class Number2String extends AbstractSymbolizerNode implements SeParameter, StringParameter {

        //We're currently forced to keep some duplicated informations about the
        //content of the formatting pattern. Indeed, it's not possible to
        //easily retrieve the default and negative patterns using directly
        //formatter.
        private RealParameter numericValue;

        private String formattingPattern;

        private String negativePattern;

        private String groupingSeparator;

        private String decimalPoint;

        //We use DecimalFormat here, because it seems simple. Take care of bugs
        //that are hidden in it, though...
        //Because of these bugs, we don't let access to formatter through
        //accessors.
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
                numericValue.setParent(this);
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
        public String getValue(DataSet sds, long fid) throws ParameterException {
                double val = numericValue.getValue(sds, fid);
                return formatter.format(val);
        }

        @Override
        public String getValue(Map<String,Value> map) throws ParameterException {
                double val = numericValue.getValue(map);
                return formatter.format(val);
        }

        @Override
        public void setRestrictionTo(String[] list) {
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

        /**
         * Get the character that must be used to separate the integer and the
         * fraction part of the numbers that will be represented with this
         * function.
         * @return
         */
        public String getDecimalPoint() {
                return decimalPoint;
        }

        /**
         * Set the character that must be used to separate the integer and the
         * fraction part of the numbers that will be represented with this
         * function.
         * @param decimalPt
         */
        public void setDecimalPoint(String decimalPt) {
                if(decimalPt.length() > 1){
                        throw new IllegalArgumentException("The input you give must not be exactly"
                                + "one charater long - blank characters are characters too !");
                }
                DecimalFormatSymbols dfs = formatter.getDecimalFormatSymbols();
                dfs.setDecimalSeparator(decimalPt.charAt(0));
                formatter.setDecimalFormatSymbols(dfs);
                decimalPoint = decimalPt;
        }

        /**
         * Get the pattern that is used to format the input numbers. This
         * pattern is used to format positive numbers. If {@code
         * getNegativePattern()} returns null or an empty {@code String}, this
         * pattern will be used to format negative numbers too. In this case,
         * negative numbers will be prefixed with a minus sign.
         * @return
         * A String, whose content is compatible with the pattern part described
         * in the {@code DecimalFormat} class from the Java API.
         */
        public String getFormattingPattern() {
                return formattingPattern;
        }

        /**
         * Set the formatting pattern that must be used for positive numbers. If
         * {@code getNegativePattern()} returns {@code null} or an empty {@code
         * String}, this pattern will be used to format negative numbers too.
         * @param formattingPat
         * The {@code String} given in argument must match the syntax for
         * patterns that is given in the Java class {@code DecimalFormat}.
         */
        public void setFormattingPattern(String formattingPat) {
                String pattern;
                if(negativePattern != null && !negativePattern.isEmpty()){
                        pattern = formattingPat+";"+negativePattern;
                } else {
                        pattern = formattingPat;
                }
                formatter.applyPattern(pattern);
                formattingPattern = formattingPat;
        }

        /**
         * Get the character that is used to separate the groups of digits in
         * the numbers this class will format.
         * @return
         */
        public String getGroupingSeparator() {
                return groupingSeparator;
        }

        /**
         * Set the character that must be used to separate the groups of digits
         * in the numbers this class will format.
         * @param groupingseparator
         * Shall be a String made of only one character.
         */
        public void setGroupingSeparator(String groupingseparator) {
                if(groupingseparator.length() != 1){
                        throw new IllegalArgumentException("The input you give must not be exactly"
                                + "one charater long - blank characters are characters too !");
                }
                DecimalFormatSymbols dfs = formatter.getDecimalFormatSymbols();
                dfs.setGroupingSeparator(groupingseparator.charAt(0));
                formatter.setDecimalFormatSymbols(dfs);
                this.groupingSeparator = groupingseparator;
        }

        /**
         * Get the pattern used to format the numerical values that are lower
         * than 0.
         * @return
         * The pattern as a string. The semantics follow what is explained in SE
         * 2.0, and in the Java API class {@code DecimalFormat}.
         */
        public String getNegativePattern() {
                throw new UnsupportedOperationException("There is still some work in SE...");
        }

        /**
         * Set the pattern used to format the numerical values that are lower
         * than 0. The pattern must be usable for the Java API class {@code
         * DecimalFormat}.
         * @param negativePattern
         * The pattern to be used for negative numbers. It must match the
         * semantics described in {@code DecimalFormat}.
         */
        public void setNegativePattern(String negativePattern) {
                throw new UnsupportedOperationException("There is still some work in SE...");
        }

        /**
         * Get the {@code RealParameter} that returns the values we format using
         * this class.
         * @return
         */
        public RealParameter getNumericValue() {
                return numericValue;
        }

        /**
         * Set the {@code RealParameter} instance that will be used to retrieve
         * the numeric values we want to format thanks to this class.
         * @param numericValue
         */
        public void setNumericValue(RealParameter numericValue) {
                this.numericValue = numericValue;
                if(this.numericValue != null){
                        this.numericValue.setParent(this);
                }
        }

        @Override
        public List<SymbolizerNode> getChildren() {
                List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
                ls.add(numericValue);
                return ls;
        }
}
