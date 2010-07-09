package org.orbisgis.core.renderer.se.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.CategorizeType;
import org.gdms.data.DataSource;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.ThreshholdsBelongToType;

import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.EditFeatureTypeStylePanel;

/**
 *
 * @param <ToType> One of ColorParameter, RealParameter, StringParameter
 * @param <FallbackType> the Literal implementation of <ToType>
 * @author maxence
 *
 */
public abstract class Categorize<ToType extends SeParameter, FallbackType extends ToType> implements SeParameter {

	public enum CategorizeMethod {

		MANUAL, NATURAL_BREAKS, QUANTILE, EQUAL_INTERVAL, STANDARD_DEVIATION
	}

	protected Categorize() {
		this.classes = new ArrayList<Category<ToType>>();
	}

	public Categorize(ToType firstClassValue, FallbackType fallbackValue, RealParameter lookupValue) {
		this.firstClass = firstClassValue;
		this.fallbackValue = fallbackValue;
		this.lookupValue = lookupValue;
		this.classes = new ArrayList<Category<ToType>>();
		this.method = CategorizeMethod.MANUAL;
	}

	@Override
	public final boolean dependsOnFeature() {
		if (this.getLookupValue().dependsOnFeature()) {
			return true;
		}

		int i;
		for (i = 0; i < this.getNumClasses(); i++) {
			if (this.getClassValue(i).dependsOnFeature()) {
				return true;
			}
		}

		return false;
	}

	public void setFallbackValue(FallbackType fallbackValue) {
		this.fallbackValue = fallbackValue;
	}

	public final FallbackType getFallbackValue() {
		return fallbackValue;
	}

	public void setLookupValue(RealParameter lookupValue) {
		this.lookupValue = lookupValue;
	}

	public final RealParameter getLookupValue() {
		return lookupValue;
	}

	/**
	 * Return the number of classes defined within the classification. According to this number (n),
	 *  available class IDs are [0;n] and IDs for threshold are [0;n-1]
	 *
	 *  @return number of defined class
	 */
	public final int getNumClasses() {
		return classes.size() + 1;
	}

	/**
	 * the new class begin from the specified threshold, up to the next one.
	 * The class is inserted at the right place
	 * @param threshold
	 * @param value
	 */
	public final void addClass(RealParameter threshold, ToType value) {
		classes.add(new Category<ToType>(value, threshold));
		sortClasses();
		this.method = CategorizeMethod.MANUAL;
	}

	public void removeClass(int i) {
		if (getNumClasses() > 1) {
			if (i < getNumClasses() && i >= 0) {
				if (i == 0) {
					// when the first class is remove, the second one takes its place
					Category<ToType> cat = classes.remove(0);
					firstClass = cat.getClassValue();
				} else {
					Category<ToType> cat = classes.remove(i - 1);
				}
			} else {
                // TODO Throws
			}
		} else {
			// TODO throws must have at least 1 category !!!
		}
		this.method = CategorizeMethod.MANUAL;
	}

	public ToType getClassValue(int i) {
		if (i == 0) {
			return firstClass;
		} else {
			return classes.get(i - 1).getClassValue();
		}
	}

	public void setClassValue(int i, ToType value) {
		if (i == 0) {
			firstClass = value;
		} else if (i > 0 && i < getNumClasses() - 1) {
			classes.get(i - 1).setClassValue(value);
		} else {
			// TODO throw
		}
	}

	public void setThresholdValue(int i, RealParameter threshold) {
		if (i >= 0 && i < getNumClasses() - 1) {
			classes.get(i).setThreshold(threshold);
			sortClasses();
		} else {
			// TODO throw
		}
		this.method = CategorizeMethod.MANUAL;
	}

	public RealParameter getThresholdValue(int i) {
		return classes.get(i).getThreshold();
	}

	public void setThresholdsSucceeding() {
		succeeding = true;
	}

	public boolean areThresholdsSucceeding() {
		return succeeding;
	}

	public void setThresholdsPreceding() {
		succeeding = false;
	}

	public boolean areThresholdsPreceding() {
		return (!succeeding);
	}

	private void sortClasses() {
		Collections.sort(classes);
	}

	protected ToType getParameter(Feature feat) {
		try {
			if (getNumClasses() > 1) {
				double value = lookupValue.getValue(feat);
				Iterator it = classes.iterator();
				ToType classValue = this.firstClass;
				while (it.hasNext()) {
					Category<ToType> cat = (Category<ToType>) it.next();
					double threshold = cat.getThreshold().getValue(feat);
					if ((!succeeding && value <= threshold) || ((value < threshold))) {
						return classValue;
					}
					classValue = cat.getClassValue();
				}
				return classValue;
			}

		} catch (ParameterException ex) {
			Logger.getLogger(Categorize.class.getName()).log(Level.SEVERE, "Unable to categorize the feature", ex);
		}
		return firstClass;
	}

	/**
	 *
	 * @param ds
	 * @param values the values to affect to classes. number of values give the number of classes
	 */
	public void categorizeByEqualsInterval(DataSource ds, ToType[] values) {
		method = CategorizeMethod.EQUAL_INTERVAL;
		int n = values.length;
		// compute n-1 thresholds and assign values
	}

	/**
	 *
	 * @param ds
	 * @param values the values to affect to classes. number of values give the numbe of classes
	 */
	public void categorizeByNaturalBreaks(DataSource ds, ToType[] values) {
		method = CategorizeMethod.NATURAL_BREAKS;
		int n = values.length;
		// compute n-1 thresholds and assign values
	}

	/**
	 *
	 * @param ds
	 * @param values the values to affect to classes. number of values give the numbe of classes
	 */
	public void categorizeByQuantile(DataSource ds, ToType[] values) {
		method = CategorizeMethod.QUANTILE;
		int n = values.length;
		// compute n-1 thresholds and assign values
	}

	/**
	 *
	 *
	 * @param ds
	 * @param values the values to affect to classes. number of values give the numbe of classes
	 * @param factor class (except first and last) interval equals sd*factor
	 */
	public void categorizeByStandardDeviation(DataSource ds, ToType[] values, double factor) {
		method = CategorizeMethod.STANDARD_DEVIATION;
		// even => mean is a threshold
		// odd => mean is the central point of the central class
		int n = values.length;

		// compute n-1 thresholds and assign values

	}

	@Override
	public ParameterValueType getJAXBParameterValueType() {
		ParameterValueType p = new ParameterValueType();
		p.getContent().add(this.getJAXBExpressionType());
		return p;
	}

	@Override
	public JAXBElement<? extends ExpressionType> getJAXBExpressionType() {
		CategorizeType c = new CategorizeType();

		if (fallbackValue != null) {
			c.setFallbackValue(fallbackValue.toString());
		}

		if (lookupValue != null) {
			c.setLookupValue(lookupValue.getJAXBParameterValueType());
		}

		if (this.succeeding) {
			c.setThreshholdsBelongTo(ThreshholdsBelongToType.SUCCEEDING);
		} else {
			c.setThreshholdsBelongTo(ThreshholdsBelongToType.PRECEDING);
		}
		ObjectFactory of = new ObjectFactory();

		List<JAXBElement<ParameterValueType>> tv = c.getThresholdAndValue();

		if (firstClass != null) {
			tv.add(of.createValue(firstClass.getJAXBParameterValueType()));
			//c.setFirstValue(firstClass.getJAXBParameterValueType());
		}

		for (Category<ToType> cat : classes) {
			ParameterValueType t = cat.getThreshold().getJAXBParameterValueType();
			ParameterValueType v = cat.getClassValue().getJAXBParameterValueType();

			tv.add(of.createThreshold(t));
			tv.add(of.createValue(v));
		}

		return of.createCategorize(c);
	}


	@Override
	public JPanel getEditionPanel(EditFeatureTypeStylePanel ftsPanel){
		throw new UnsupportedOperationException("Not yet implemented ("+ this.getClass() + " )");
	}

	private CategorizeMethod method;
	private boolean succeeding = true;
	private RealParameter lookupValue;
	protected FallbackType fallbackValue;
	private ToType firstClass;
	private ArrayList<Category<ToType>> classes;
}
