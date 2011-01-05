package org.orbisgis.core.ui.pluginSystem.message;

import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.utils.I18N;

public class ErrorMessages {

	public static final String CannotRegisterSource = I18N
			.getText("orbisgis.errorMessages.CannotRegisterSource");
	public static final String CannotFindTheDataSource = I18N
			.getText("orbisgis.errorMessages.CannotFindTheDataSource");
	public static final String CannotCreateSource = I18N
			.getText("orbisgis.errorMessages.CannotCreateSource");
	public static final String CannotModifyDataSource = I18N
			.getText("orbisgis.errorMessages.CannotModifyDataSource");
	public static final String CannotDeleteField = I18N
			.getText("orbisgis.errorMessages.CannotDeleteField");
	public static final String CannotAccessFieldInformation = I18N
			.getText("orbisgis.errorMessages.CannotAccessFieldInformation");
	public static final String CannotRedo = I18N
			.getText("orbisgis.errorMessages.CannotRedo");
	public static final String CannotDeleteSelectedRow = I18N
			.getText("orbisgis.errorMessages.CannotDeleteSelectedRow");
	public static final String CannotInsertANewRow = I18N
			.getText("orbisgis.errorMessages.CannotAdd");
	public static final String IncompatibleFieldTypes = I18N
			.getText("orbisgis.errorMessages.IncompatibleTypes");
	public static final String DataError = I18N
			.getText("orbisgis.errorMessages.dataAccessError");
	public static final String BadInputValue = I18N
			.getText("orbisgis.errorMessages.badInputValue");
	public static final String CannotObtainNumberRows = I18N
			.getText("orbisgis.errorMessages.CannotObtainNumberRows");
	public static final String CannotReadSource = I18N
			.getText("orbisgis.errorMessages.CannotReadSource");
	public static final String CannotSetNullValue = I18N
			.getText("orbisgis.errorMessages.CannotSetNullValue");
	public static final String CannotTestNullValue = I18N
			.getText("orbisgis.errorMessages.CannotTestNullValue");
	public static final String CannotObtainDataSource = I18N
			.getText("orbisgis.errorMessages.CannotObtainDataSource");
	public static final String CannotCreateDataSource = I18N
			.getText("orbisgis.errorMessages.CannotCreateDataSource");
	public static final String WrongSQLQuery = I18N
			.getText("orbisgis.errorMessages.WrongSQLQuery");
	public static final String CannotUndo = I18N
			.getText("orbisgis.errorMessages.CannotUndo");
	public static final String CannotComputeEnvelope = I18N
			.getText("orbisgis.errorMessages.CannotComputeEnvelope");
	public static final String CannotWriteImage = I18N
			.getText("orbisgis.errorMessages.cannotWriteImage");
	public static final String CannotWriteOnDisk = I18N
			.getText("orbisgis.errorMessages.cannotWriteOnDisk");
	public static final String CannotWritePDF = I18N
			.getText("orbisgis.errorMessages.cannotWritePDF");
	public static final String CommandLineError = I18N
			.getText("orbisgis.errorMessages.CommandLineError");

	public static void error(String message, Exception e) {
		Services.getService(ErrorManager.class).error(message, e);
	}

}
