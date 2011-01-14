/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher. * 
 * 
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO,Adelin PIAU
 * 
 * Copyright (C) 2011 Erwan BOCHER, Alexis GUEGANNO, Antoine GOURLAY
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
 *
 * or contact directly:
 * info_at_orbisgis.org
 */
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
	public static final String CannotUpdate = I18N
			.getText("orbisgis.errorMessages.CannotUpdate");
	public static final String CannotFind = I18N
			.getText("orbisgis.errorMessages.CannotFind");
	public static final String CannotSave = I18N
			.getText("orbisgis.errorMessages.CannotSave");
	public static final String CannotSaveFile = I18N
			.getText("orbisgis.errorMessages.CannotSaveFile");
	public static final String CannotWriteFile = I18N
			.getText("orbisgis.errorMessages.CannotWriteFile");
	public static final String CannotRemoveSource = I18N
			.getText("orbisgis.errorMessages.CannotRemoveSource");
	public static final String CannotRevertSource = I18N
			.getText("orbisgis.errorMessages.CannotRevertSource");
	public static final String CannotSaveSource = I18N
			.getText("orbisgis.errorMessages.CannotSaveSource");
	public static final String SourceAlreadyRegistered = I18N
			.getText("orbisgis.errorMessages.SourceAlreadyRegistered");
	public static final String CannotConnect = I18N
			.getText("orbisgis.errorMessages.CannotConnect");
	public static final String FileNotFound = I18N
			.getText("orbisgis.errorMessages.FileNotFound");
	public static final String CannotConvertFile = I18N
			.getText("orbisgis.errorMessages.CannotConvertFile");
	public static final String CannotSaveWorkspace = I18N
			.getText("orbisgis.errorMessages.CannotSaveWorkspace");
	public static final String UnsupportedSourceType = I18N
			.getText("orbisgis.errorMessages.UnsupportedSourceType");
	public static final String NotImplementedYet = I18N
			.getText("orbisgis.errorMessages.NotImplementedYet");
	public static final String SourceAlreadyExists = I18N
			.getText("orbisgis.errorMessages.SourceAlreadyExists");
	public static final String CannotInitializeSource = I18N
			.getText("orbisgis.errorMessages.CannotInitializeSource");
	public static final String CannotRecoverView = I18N
			.getText("orbisgis.errorMessages.CannotRecoverView");
	public static final String CannotAddField = null;
	public static final String UnknownDataType = null;
	public static final String CannotAddValue = null;
	public static final String CannotRenameField = null;
	public static final String CannotAddLayer = null;
	public static final String SQLStatementError = null;
	public static final String SourceAlreadyRegisteredWithName = null;
	public static final String CannotReadDataSource = null;
	public static final String CannotDeleteLayer = null;
	public static final String CannotSavelayer = null;
	public static final String CannotExportInSelectedFormat = null;
	public static final String CannotRevertlayer = null;

	/**
	 * Return an error message and the exception
	 * 
	 * @param message
	 */
	public static void error(String message, Exception e) {
		Services.getService(ErrorManager.class).error(message, e);
	}

	/**
	 * Return an error message
	 * 
	 * @param message
	 */
	public static void error(String message) {
		Services.getService(ErrorManager.class).error(message);
	}

	/**
	 * Return an error message and the exception
	 * 
	 * @param message
	 * @param t
	 */
	public static void error(String message, Throwable t) {
		Services.getService(ErrorManager.class).error(message, t);
	}

}
