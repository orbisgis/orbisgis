package org.orbisgis.core.ui.plugins.views.sqlConsole.util;

import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.TokenMgrError;
import org.orbisgis.core.javaManager.autocompletion.NodeUtils;

public class CodeErrors {

	public static CodeError getCodeError(ParseException e, String script) {
		int startPos = NodeUtils.getPosition(script, e.currentToken.beginLine,
				e.currentToken.beginColumn);
		int endPos = NodeUtils.getPosition(script, e.currentToken.next.beginLine,
				e.currentToken.next.endColumn);

		return new CodeError(startPos, endPos+1, e.getMessage());

	}

	public static CodeError getCodeError(TokenMgrError e) {

		return new CodeError(0, 0, e.getMessage());
	}

}
