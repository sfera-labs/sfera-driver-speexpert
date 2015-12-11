package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.NumberEvent;

public class InputEvent extends NumberEvent implements SpeExpertEvent {

	public InputEvent(Node source, Integer value) {
		super(source, "input", value);
	}
}
