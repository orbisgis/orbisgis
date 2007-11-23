package org.orbisgis.core.actions;

public interface IActionFactory {

	IAction getAction(Object action);

	ISelectableAction getSelectableAction(Object action);

}
