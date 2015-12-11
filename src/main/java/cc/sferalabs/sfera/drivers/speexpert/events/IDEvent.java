package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.StringEvent;

public class IDEvent extends StringEvent implements SpeExpertEvent {

	public IDEvent(Node source, String value) {
		super(source, "id", value);
	}
}
