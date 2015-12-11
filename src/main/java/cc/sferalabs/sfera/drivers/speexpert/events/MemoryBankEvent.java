package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.StringEvent;

public class MemoryBankEvent extends StringEvent implements SpeExpertEvent {

	public MemoryBankEvent(Node source, String value) {
		super(source, "memorybank", value);
	}
}
