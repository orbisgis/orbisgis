package org.orbisgis.core.ui.plugins.views.geomark;

import java.io.Serializable;


import com.vividsolutions.jts.geom.Envelope;

public class Geomark implements Serializable {

	private String name;
	private Envelope envelope;

	public Geomark(String name, Envelope envelope) {
		this.name = name;
		this.envelope = envelope;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Envelope getEnvelope() {
		return envelope;
	}

	public void setEnvelope(Envelope envelope) {
		this.envelope = envelope;
	}

	@Override
	public String toString() {
		return name + "  " + envelope.toString();
	}

}
