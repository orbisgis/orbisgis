/*
 * GELAT is a Geographic information system focused in geoprocessing.
 * It's able to manipulate and create vector and raster spatial information. GELAT
 * is distributed under GPL 3 license.
 *
 * Copyright (C) 2009 Fernando GONZALEZ CORTES, Victor GONZALEZ CORTES
 *
 * This file is part of GELAT.
 *
 * GELAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GELAT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GELAT. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://gelat.forge.osor.eu/>
 *
 * or contact directly fergonco _at_ gmail.com
 */

package org.orbisgis.core.ui.views.geocognition.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.Icon;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.GeocognitionFilter;
import org.orbisgis.core.geocognition.sql.GeocognitionBuiltInCustomQuery;
import org.orbisgis.core.geocognition.sql.GeocognitionBuiltInFunction;
import org.orbisgis.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.core.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.core.ui.components.sif.ChoosePanel;
import org.orbisgis.core.ui.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.core.ui.views.geocognition.wizard.INewGeocognitionElement;
import org.orbisgis.images.IconLoader;
import org.orbisgis.sif.UIFactory;

public class NewRegisteredSQLArtifact implements INewGeocognitionElement {

	private ArrayList<Object> artifacts;
	private ArrayList<String> artifactNames;

	@Override
	public ElementRenderer getElementRenderer() {
		return new ElementRenderer() {

			@Override
			public Icon getIcon(String contentTypeId,
					Map<String, String> properties) {
				if (GeocognitionCustomQueryFactory.BUILT_IN_QUERY_ID
						.equals(contentTypeId)) {
					String registered = properties
							.get(GeocognitionBuiltInCustomQuery.REGISTERED);
					if ((registered != null)
							&& registered
									.equals(GeocognitionBuiltInCustomQuery.IS_REGISTERED)) {
						return IconLoader.getIcon("builtincustomquerymap.png");
					} else {
						return IconLoader
								.getIcon("builtincustomquerymaperror.png");
					}
				} else if (GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID
						.equals(contentTypeId)) {
					String registered = properties
							.get(GeocognitionBuiltInFunction.REGISTERED);
					if ((registered != null)
							&& registered
									.equals(GeocognitionBuiltInFunction.IS_REGISTERED)) {
						return IconLoader.getIcon("builtinfunctionmap.png");
					} else {
						return IconLoader
								.getIcon("builtinfunctionmaperror.png");
					}
				} else {
					return null;
				}

			}

			@Override
			public Icon getDefaultIcon(String contentTypeId) {
				return getIcon(contentTypeId, new HashMap<String, String>());
			}

			@SuppressWarnings("unchecked")
			@Override
			public String getTooltip(GeocognitionElement element) {
				try {
					if (element.getTypeId().equals(
							GeocognitionCustomQueryFactory.BUILT_IN_QUERY_ID)) {
						Class<? extends CustomQuery> cqClass = (Class<? extends CustomQuery>) element
								.getObject();
						if (cqClass != null) {
							return cqClass.newInstance().getDescription();
						} else {
							return "Custom query class not found";
						}
					} else if (element.getTypeId().equals(
							GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID)) {
						Class<? extends Function> cqClass = (Class<? extends Function>) element
								.getObject();
						return cqClass.newInstance().getDescription();
					} else {
						return null;
					}
				} catch (UnsupportedOperationException e) {
					throw new RuntimeException("bug", e);
				} catch (InstantiationException e) {
					throw new RuntimeException("bug", e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("bug", e);
				}
			}

		};
	}

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
											.equals(GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID)) {
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

	protected Class<?> getArtifact(String name) {
		Function fnt = FunctionManager.getFunction(name);
		if (fnt != null) {
			return fnt.getClass();
		} else {
			return QueryManager.getQuery(name).getClass();
		}
	}

	private String getName(Object sqlArtifact) {
		if (sqlArtifact instanceof Function) {
			return ((Function) sqlArtifact).getName();
		} else if (sqlArtifact instanceof CustomQuery) {
			return ((CustomQuery) sqlArtifact).getName();
		} else {
			throw new RuntimeException("bug");
		}
	}

	protected String[] getArtifactNames() {
		String[] queries = QueryManager.getQueryNames();
		String[] functions = FunctionManager.getFunctionNames();
		ArrayList<String> ret = new ArrayList<String>();
		Collections.addAll(ret, queries);
		Collections.addAll(ret, functions);
		Collections.sort(ret);

		return ret.toArray(new String[0]);
	}

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

	@Override
	public String getBaseName(int elementIndex) {
		return getFixedName(elementIndex);
	}

	@Override
	public GeocognitionElementFactory[] getFactory() {
		return new GeocognitionElementFactory[] {
				new GeocognitionFunctionFactory(),
				new GeocognitionCustomQueryFactory() };
	}

	@Override
	public boolean isUniqueIdRequired(int index) {
		return false;
	}

	@Override
	public String getName() {
		return "Built-in SQL";
	}

}
