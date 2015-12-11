package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.BooleanEvent;
import cc.sferalabs.sfera.events.Node;

public class TXEvent extends BooleanEvent implements SpeExpertEvent {

	public TXEvent(Node source, Boolean value) {
		super(source, "tx", value);
	}
}
