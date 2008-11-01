package org.orbisgis.views.geocognition.actions;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.orbisgis.Services;
import org.orbisgis.action.IAction;
import org.orbisgis.action.IActionAdapter;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.actions.ActionCode;
import org.orbisgis.geocognition.actions.GeocognitionActionElementFactory;
import org.orbisgis.javaManager.CompilationException;
import org.orbisgis.javaManager.JavaManager;
import org.orbisgis.javaManager.parser.ParseException;
import org.orbisgis.views.geocognition.action.IGeocognitionAction;
import org.orbisgis.windows.mainFrame.UIManager;

public class InstallAction implements IGeocognitionAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		return element.getTypeId().equals(
				GeocognitionActionElementFactory.ACTION_ID);
	}

	@Override
	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return true;
	}

	@Override
	public void execute(Geocognition geocognition, GeocognitionElement element) {
		UIManager ui = Services.getService(UIManager.class);
		ActionCode ac = (ActionCode) element.getObject();
		try {
			ui.installMenu(element.getIdPath(), ac.getText(), ac.getMenuId(),
					ac.getGroup(), new CustomActionAdapter(ac.getCode()));
			JOptionPane.showMessageDialog(null, "Installation successful");
		} catch (IllegalArgumentException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot install action", e);
		} catch (IOException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot compile action", e);
		} catch (CompilationException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot compile action", e);
		} catch (ParseException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot compile action", e);
		} catch (InstantiationException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot instantiate action", e);
		} catch (IllegalAccessException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot instantiate action", e);
		} catch (ClassCastException e) {
			Services.getService(ErrorManager.class).error(
					"The code doesn't evaluate to an action class", e);
		}
	}

	private final class CustomActionAdapter implements IActionAdapter {

		private IAction action;

		public CustomActionAdapter(String code) throws IOException,
				CompilationException, ParseException, InstantiationException,
				IllegalAccessException {
			JavaManager jm = Services.getService(JavaManager.class);
			Class<?> cl = jm.compile(code, null);
			action = (IAction) cl.newInstance();
		}

		@Override
		public boolean isVisible() {
			return action.isVisible();
		}

		@Override
		public boolean isEnabled() {
			return action.isEnabled();
		}

		@Override
		public void actionPerformed() {
			action.actionPerformed();
		}
	}

}
