package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.StringEvent;

public class AlarmEvent extends StringEvent implements SpeExpertEvent {
	private final String message;
	
	public AlarmEvent(Node source, String value) {
		super(source, "alarm", value);
		this.message = decodeAlarm(value);
	}
	
	public String getMessage() {
		
		return message;
	}

	private static String decodeAlarm(String value) {
		
		if (value == null) {
			return null;
		} else {
			switch (value) {
			case "S":
				return "SWR EXCEEDING LIMITS";
			case "A":
				return "AMPLIFIER PROTECTION";
			case "D":
				return "INPUT OVERDRIVING";
			case "H":
				return "EXCESS OVERHEATING";
			case "C":
				return "COMBINER FAULT";
			default:
				return "";
			}
		}
	}
}
