package org.gdms.data.values;

import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

public class WKBUtil {

	private static WKBWriter writer3D = null;
	private static WKBWriter writer2D = null;

	private static WKTWriter textWriter3D = null;
	private static WKTWriter textWriter2D = null;

	private static WKBReader wkbReader = null;
	private static WKTReader wktReader = null;

	public static WKBReader getWKBReaderInstance() {
		if (wkbReader == null) {
			wkbReader = new WKBReader();
		}
		return wkbReader;
	}

	public static WKBWriter getWKBWriter2DInstance() {
		if (writer2D == null) {
			writer2D = new WKBWriter();
		}
		return writer2D;
	}

	public static WKBWriter getWKBWriter3DInstance() {
		if (writer3D == null) {
			writer3D = new WKBWriter(3);
		}
		return writer3D;
	}

	public static WKTReader getWKTReaderInstance() {
		if (wktReader == null) {
			wktReader = new WKTReader();
		}
		return wktReader;
	}

	public static WKTWriter getTextWKTWriter2DInstance() {
		if (textWriter2D == null) {
			textWriter2D = new WKTWriter();
		}
		return textWriter2D;
	}

	public static WKTWriter getTextWKTWriter3DInstance() {
		if (textWriter3D == null) {
			textWriter3D = new WKTWriter(3);
		}
		return textWriter3D;
	}

}