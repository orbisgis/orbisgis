package org.orbisgis.core.renderer.se.parameter.real;

import java.util.Iterator;

import net.opengis.fes._2.LiteralType;
import net.opengis.se._2_0.core.CategorizeType;
import net.opengis.se._2_0.core.ParameterValueType;
import net.opengis.se._2_0.core.ThresholdBelongsToType;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

/**
 * A categorization from {@code RealParameter} to {@code RealParamter}
 * @author alexis, maxence
 */
public final class Categorize2Real extends Categorize<RealParameter, RealLiteral> implements RealParameter {

        private RealParameterContext ctx;

        /**
         * Build a new {@code Categorize2Real} with the given parameters. Built using 
         * {@link Categorize#Categorize(org.orbisgis.core.renderer.se.parameter.SeParameter, 
         * org.orbisgis.core.renderer.se.parameter.SeParameter, 
         * org.orbisgis.core.renderer.se.parameter.real.RealParameter) Categorize}
         * @param initialClass
         * The value of the first class.
         * @param fallback
         * The default value if an input can't be processed.
         * @param lookupValue 
         * The {@code RealParameter} used to retrieve the input values.
         */
        public Categorize2Real(RealParameter initialClass, RealLiteral fallback, RealParameter lookupValue) {
                super(initialClass, fallback, lookupValue);
                this.setContext(ctx);
        }

        /**
         * Build a new {@code Categorize2Real} from a JAXB element.
         * @param expr
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public Categorize2Real(CategorizeType expr) throws InvalidStyle {
                
                this.setFallbackValue(new RealLiteral(expr.getFallbackValue()));
                this.setLookupValue(SeParameterFactory.createRealParameter(expr.getLookupValue()));


                Iterator<Object> it = expr.getThresholdAndValue().iterator();

                this.setClassValue(0, SeParameterFactory.createRealParameter((ParameterValueType)it.next()));

                // Fetch class values and thresholds
                while (it.hasNext()) {
                        RealLiteral th = new RealLiteral((LiteralType)(it.next()));
                        RealParameter vl = SeParameterFactory.createRealParameter((ParameterValueType)it.next());
                        this.addClass(th,vl);
                }

                if (expr.getThresholdBelongsTo() == ThresholdBelongsToType.PRECEDING) {
                        this.setThresholdsPreceding();
                }
                else {
                        this.setThresholdsSucceeding();
                }
             
                super.setPropertyFromJaxB(expr);
        }

        @Override
        public Double getValue(DataSource sds, long fid) throws ParameterException{

		if (sds == null){
			throw new ParameterException("No feature");
		}

		return getParameter(sds, fid).getValue(sds, fid);
        }


	@Override
	public void setClassValue(int i, RealParameter value){
		super.setClassValue(i, value);
		if (value != null){
			value.setContext(ctx);
		}
	}

	@Override
	public void setFallbackValue(RealLiteral l){
		super.setFallbackValue(l);
		if (l != null){
			l.setContext(ctx);
		}
	}

	@Override
	public void setContext(RealParameterContext ctx) {
		this.ctx = ctx;
		this.getFallbackValue().setContext(ctx);

		for (int i=0; i<this.getNumClasses();i++){
			RealParameter classValue = this.getClassValue(i);
			classValue.setContext(ctx);
		}

	}

	@Override
	public String toString(){
		return "NA";
	}

	@Override
	public RealParameterContext getContext() {
		return ctx;
	}

        /**
         * Always return 0, whatever the object is...
         * @param o
         * @return 
         */
        @Override
        public int compareTo(Object o) {
                return 0;
        }

}
