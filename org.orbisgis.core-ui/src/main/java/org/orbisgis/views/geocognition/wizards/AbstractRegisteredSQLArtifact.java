package org.orbisgis.views.geocognition.wizards;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.orbisgis.Services;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.GeocognitionFilter;
import org.orbisgis.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.ui.sif.ChoosePanel;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.views.geocognition.wizard.INewGeocognitionElement;
import org.sif.UIFactory;

public abstract class AbstractRegisteredSQLArtifact implements
		INewGeocognitionElement {

	private ArrayList<Object> artifacts;
	private ArrayList<String> artifactNames;

	@Override
	public ElementRenderer getElementRenderer() {
		return new ElementRenderer() {

			@Override
			public Icon getIcon(String contentTypeId,
					Map<String, String> properties) {
				return AbstractRegisteredSQLArtifact.this.getIcon(
						contentTypeId, properties);
			}

			@Override
			public Icon getDefaultIcon(String contentTypeId) {
				return AbstractRegisteredSQLArtifact.this.getIcon(
						contentTypeId, new HashMap<String, String>());
			}

		};
	}

	protected abstract ImageIcon getIcon(String contentTypeId,
			Map<String, String> properties);

	@SuppressWarnings("unchecked")
	@Override
	public void runWizard() {
		Geocognition geocognition = Services.getService(Geocognition.class);
		String[] classNames = getArtifactNames();
		TreeSet<Class<?>> sortedOptions = new TreeSet<Class<?>>(
				new Comparator<Class<?>>() {

					@Override
					public int compare(Class<?> o1, Class<?> o2) {
						return o1.getSimpleName().compareTo(o2.getSimpleName());
					}

				});
		for (String name : classNames) {
			final String artifactName = name;
			GeocognitionElement[] functionInGC = geocognition
					.getElements(new GeocognitionFilter() {

						@Override
						public boolean accept(GeocognitionElement element) {
							String typeId = element.getTypeId();
							if (typeId
									.equals(GeocognitionCustomQueryFactory.BUILT_IN_QUERY_ID)
									|| typeId
											.equals(GeocognitionCustomQueryFactory.JAVA_QUERY_ID)
									|| typeId
											.equals(GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID)
									|| typeId
											.equals(GeocognitionFunctionFactory.JAVA_FUNCTION_ID)) {
								return element.getId().toLowerCase().equals(
										artifactName.toLowerCase());
							} else {
								return false;
							}
						}
					});
			if (functionInGC.length == 0) {
				sortedOptions.add(getArtifact(name));
			}
		}

		ArrayList<Class<?>> options = new ArrayList<Class<?>>();
		options.addAll(sortedOptions);
		String[] names = new String[options.size()];
		Object[] ids = new Object[options.size()];
		for (int i = 0; i < options.size(); i++) {
			Class<?> functionClass = null;
			try {
				functionClass = options.get(i);
				names[i] = getName(((Class<?>) functionClass).newInstance());
				ids[i] = functionClass;
			} catch (InstantiationException e) {
				Services.getErrorManager().error(
						"Cannot add function: " + functionClass, e);
			} catch (IllegalAccessException e) {
				Services.getErrorManager().error(
						"Cannot add function: " + functionClass, e);
			}
		}
		ChoosePanel cp = new ChoosePanel("Select registered functions to add",
				names, ids);
		cp.setMultiple(true);
		artifacts = new ArrayList<Object>();
		artifactNames = new ArrayList<String>();
		if (UIFactory.showDialog(cp)) {
			Object[] functionClasses = cp.getSelectedElements();
			for (Object object : functionClasses) {
				try {
					artifactNames
							.add(getName(((Class<?>) object).newInstance()));
					artifacts.add(object);
				} catch (IllegalArgumentException e) {
					Services.getErrorManager().error(
							"Cannot add function: " + object, e);
				} catch (InstantiationException e) {
					Services.getErrorManager().error(
							"Cannot add function: " + object, e);
				} catch (IllegalAccessException e) {
					Services.getErrorManager().error(
							"Cannot add function: " + object, e);
				}
			}
		}

	}

	protected abstract Class<?> getArtifact(String name);

	protected abstract String getName(Object sqlArtifact);

	protected abstract String[] getArtifactNames();

	@Override
	public Object getElement(int index) {
		return artifacts.get(index);
	}

	@Override
	public int getElementCount() {
		return artifacts.size();
	}

	@Override
	public String getFixedName(int index) {
		return artifactNames.get(index);
	}

}
