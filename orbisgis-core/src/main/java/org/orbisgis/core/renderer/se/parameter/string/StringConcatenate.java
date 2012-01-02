/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.parameter.string;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ConcatenateType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

/**
 * Implementation of the {@code Concatenate} SE function. This function takes at
 * least to String in input, and simply concatenates them. it is consequently
 * only dependant on a list of {@code StringParameter} instances.
 * @author alexis
 */
public class StringConcatenate implements StringParameter {

        private List<StringParameter> inputStrings;

        /**
         * Build a new {@code StringConcatenate} instance from the given JAXB
         * {@code ConcatenateType} instance.
         * @param concatenate
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
         */
        public StringConcatenate(ConcatenateType concatenate) throws InvalidStyle {
                List<ParameterValueType> jaxbList = concatenate.getStringValue();
                inputStrings = new ArrayList<StringParameter>(jaxbList.size());
                for(ParameterValueType pvt : jaxbList){
                        inputStrings.add(SeParameterFactory.createStringParameter(pvt));
                }
        }
        /**
         * Build a new {@code StringConcatenate} instance from the given 
         * {@code JAXBElement<ConcatenateType>} instance.
         * @param concatenate
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
         */

        public StringConcatenate(JAXBElement<ConcatenateType> concat) throws InvalidStyle {
                this(concat.getValue());
        }

        @Override
        public String getValue(org.gdms.data.DataSource sds, long fid) throws ParameterException {
                List<String> inputs = new LinkedList<String>();
                int expectedSize = 0;
                for(StringParameter sp : inputStrings){
                        String tmp = sp.getValue(sds, fid);
                        inputs.add(tmp);
                        expectedSize+=tmp.length();
                }
                StringBuilder sb = new StringBuilder(expectedSize);
                for(String temps : inputs){
                        sb.append(temps);
                }
                return sb.toString();
        }

        @Override
        public void setRestrictionTo(String[] list) {
        }

        @Override
        public String dependsOnFeature() {
                StringBuilder sb = new StringBuilder();
                for(StringParameter sp :inputStrings){
                        sb.append(sp.dependsOnFeature());
                }
                return sb.toString();
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
                ConcatenateType ct = new ConcatenateType();
                List<ParameterValueType> inc = ct.getStringValue();
                for(StringParameter sp : inputStrings){
                        inc.add(sp.getJAXBParameterValueType());
                }
                return of.createConcatenate(ct);
        }

}
