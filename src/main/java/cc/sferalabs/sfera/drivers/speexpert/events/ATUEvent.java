package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.BooleanEvent;
import cc.sferalabs.sfera.events.Node;

public class ATUEvent extends BooleanEvent implements SpeExpertEvent {

	public ATUEvent(Node source, Boolean value) {
		super(source, "atu", value);
	}
}
