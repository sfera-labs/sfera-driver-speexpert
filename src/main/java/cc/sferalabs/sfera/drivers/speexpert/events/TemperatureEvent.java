package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.NumberEvent;

public class TemperatureEvent extends NumberEvent implements SpeExpertEvent {

	public TemperatureEvent(Node source, String id, Integer value) {
		super(source, "temperature." + id, value);
	}
}
