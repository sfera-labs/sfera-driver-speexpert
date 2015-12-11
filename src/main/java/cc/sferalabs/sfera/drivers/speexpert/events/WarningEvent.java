package cc.sferalabs.sfera.drivers.speexpert.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.StringEvent;

public class WarningEvent extends StringEvent implements SpeExpertEvent {
	private final String message;
	
	public WarningEvent(Node source, String value) {
		super(source, "warning", value);
		this.message = decodeWarning(value);
	}
	
	public String getMessage() {
		
		return message;
	}

	private static String decodeWarning(String value) {
		
		if (value == null) {
			return null;
		} else {
			switch (value) {
			case "M":
				return "ALARM AMPLIFIER";
			case "A":
				return "NO SELECTED ANTENNA";
			case "S":
				return "SWR ANTENNA";
			case "B":
				return "NO VALID BAND";
			case "P":
				return "POWER LIMIT EXCEEDED";
			case "O":
				return "OVERHEATING";
			case "Y":
				return "ATU NOT AVAILABLE";
			case "W":
				return "TUNING WITH NO POWER";
			case "K":
				return "ATU BYPASSED";
			case "R":
				return "POWER SWITCH HELD BY REMOTE";
			case "T":
				return "COMBINER OVERHEATING";
			case "C":
				return "COMBINER FAULT";
			default:
				return "";
			}
		}
	}
}
