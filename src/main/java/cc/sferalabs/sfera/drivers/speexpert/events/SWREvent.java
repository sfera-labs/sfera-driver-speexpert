package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.NumberEvent;

public class SWREvent extends NumberEvent implements SpeExpertEvent {

	public SWREvent(Node source, String id, Double value) {
		super(source, "swr." + id, value);
	}
}
