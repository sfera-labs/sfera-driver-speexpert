package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.BaseEvent;

public class PAEvent<T> extends BaseEvent implements SpeExpertEvent {
	private T value;

	public PAEvent(Node source, String id, T value) {
		super(source, "pa." + id);
		this.value = value;
	}

	@Override
	public T getValue() {
		return value;
	}
}
