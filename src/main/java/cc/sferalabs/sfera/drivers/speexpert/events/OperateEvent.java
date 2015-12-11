package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.BooleanEvent;
import cc.sferalabs.sfera.events.Node;

public class OperateEvent extends BooleanEvent implements SpeExpertEvent {

	public OperateEvent(Node source, Boolean value) {
		super(source, "operate", value);
	}
}
