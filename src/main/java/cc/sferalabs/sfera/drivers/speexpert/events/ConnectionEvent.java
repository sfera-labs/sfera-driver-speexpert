package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.BooleanEvent;
import cc.sferalabs.sfera.events.Node;

public class ConnectionEvent extends BooleanEvent implements SpeExpertEvent {

	public ConnectionEvent(Node source, Boolean value) {
		super(source, "connection", value);
	}
}
