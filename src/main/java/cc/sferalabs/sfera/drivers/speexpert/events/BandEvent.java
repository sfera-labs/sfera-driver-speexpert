package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.StringEvent;

public class BandEvent extends StringEvent implements SpeExpertEvent {

	public BandEvent(Node source, String value) {
		super(source, "band", value);
	}
}
