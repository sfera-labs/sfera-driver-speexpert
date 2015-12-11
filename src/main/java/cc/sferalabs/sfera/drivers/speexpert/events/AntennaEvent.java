package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.NumberEvent;

public class AntennaEvent extends NumberEvent implements SpeExpertEvent {

	public AntennaEvent(Node source, String id, Integer value) {
		super(source, "antenna." + id, value);
	}
}
