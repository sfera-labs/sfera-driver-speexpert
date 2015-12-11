package cc.sferalabs.sfera.drivers.speexpert;

import cc.sferalabs.sfera.drivers.Driver;
import cc.sferalabs.sfera.drivers.speexpert.events.ATUEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.BandEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.ConnectionEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.IDEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.InputEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.MemoryBankEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.AlarmEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.WarningEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.OperateEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.PAEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.SWREvent;
import cc.sferalabs.sfera.drivers.speexpert.events.AntennaEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.TXEvent;
import cc.sferalabs.sfera.drivers.speexpert.events.TemperatureEvent;
import cc.sferalabs.sfera.events.Bus;
import cc.sferalabs.sfera.io.comm.CommPort;
import cc.sferalabs.sfera.io.comm.CommPortException;
import cc.sferalabs.sfera.core.Configuration;

public class SpeExpert extends Driver {

	private static final int ERROR_LIMIT = 4;
	private static final long POLL_INTERVAL = 200;
	private static final int RESPONSE_TIMEOUT = 1000;
	private CommPort commPort;
	private int statusErrorCount;
	private String status = "";
	private int model = -1; // -1: undefined; 13: 1.3K-FA; 20: 2K-FA

	public SpeExpert(String id) {
		super(id);
	}

	@Override
	protected boolean onInit(Configuration config) throws InterruptedException {

		statusErrorCount = 0;
		String portName = config.get("serial_port", null);
		if (portName == null) {
			log.error("Serial port not set");
			return false;
		}

		try {
			commPort = CommPort.open(portName);
			int baudRate = config.get("baud_rate", 115200);
			commPort.setParams(baudRate, 8, 1, CommPort.PARITY_NONE, CommPort.FLOWCONTROL_NONE);
			if (command((byte)0x90)) {
				Bus.postIfChanged(new ConnectionEvent(this, true));
				return true;
			} else {
				return false;
			}
		} catch (CommPortException e) {
			log.error("Error initializing serial port", e);
			return false;
		}
	}

	@Override
	protected boolean loop() throws InterruptedException {
		
		try {
			if (command((byte)0x90)) {
				statusErrorCount = 0;
			} else if (statusErrorCount < ERROR_LIMIT) {
				statusErrorCount++;
			} else {
				throw new Exception("too many errors");
			}
		} catch (Exception e) {
			log.error("Exception in loop", e);
			return false;
		}
		Thread.sleep(POLL_INTERVAL);
		return true;
	}

	@Override
	protected void onQuit() {

		Bus.postIfChanged(new ConnectionEvent(this, false));

		if (model != -1) {
			Bus.postIfChanged(new IDEvent(this, null));
			Bus.postIfChanged(new OperateEvent(this, null));
			Bus.postIfChanged(new TXEvent(this, null));
			Bus.postIfChanged(new InputEvent(this, null));
			Bus.postIfChanged(new BandEvent(this, null));
			Bus.postIfChanged(new AntennaEvent(this, "tx", null));
			Bus.postIfChanged(new ATUEvent(this, null));
			Bus.postIfChanged(new AntennaEvent(this, "rx", null));
			Bus.postIfChanged(new PAEvent<String>(this, "level", null));
			Bus.postIfChanged(new PAEvent<Integer>(this, "power", null));
			Bus.postIfChanged(new SWREvent(this, "atu", null));
			Bus.postIfChanged(new SWREvent(this, "antenna", null));
			Bus.postIfChanged(new PAEvent<Double>(this, "v", null));
			Bus.postIfChanged(new PAEvent<Double>(this, "i", null));
			if (model == 13) {
				Bus.postIfChanged(new MemoryBankEvent(this, null));
				Bus.postIfChanged(new TemperatureEvent(this, "heatsink", null));
			} else if (model == 20) {
				Bus.postIfChanged(new TemperatureEvent(this, "upper", null));
				Bus.postIfChanged(new TemperatureEvent(this, "lower", null));
				Bus.postIfChanged(new TemperatureEvent(this, "combiner", null));
			}
			Bus.postIfChanged(new WarningEvent(this, null));
			Bus.postIfChanged(new AlarmEvent(this, null));
		}

		status = "";
		model = -1;
		
		try {
			commPort.close();
		} catch (Exception e) {}
	}

	public boolean setKey(String key) {
		
		switch (key.toLowerCase()) {
		case "input":
			return command((byte)0x01);
		case "band-":
			return command((byte)0x02);
		case "band+":
			return command((byte)0x03);
		case "antenna":
			return command((byte)0x04);
		case "l-":
			return command((byte)0x05);
		case "l+":
			return command((byte)0x06);
		case "c-":
			return command((byte)0x07);
		case "c+":
			return command((byte)0x08);
		case "tune":
			return command((byte)0x09);
		case "off":
			return command((byte)0x0a);
		case "power":
			return command((byte)0x0b);
		case "display":
			return command((byte)0x0c);
		case "operate":
			return command((byte)0x0d);
		case "cat":
			return command((byte)0x0e);
		case "left":
			return command((byte)0x0f);
		case "right":
			return command((byte)0x10);
		case "set":
			return command((byte)0x11);
		default:
			return false;	
		}
	}

	public boolean setBacklight(boolean on) {
		
		return command((on) ? (byte)0x82 : (byte)0x83);
	}
		
	private static String decodeBand(String value) {
		String[] names = {"160m", "80m", "60m", "40m", "30m", "20m", "17m", "15m", "12m", "10m", "6m", "4m"}; 
		
		try {
			return names[Integer.parseInt(value)];
		} catch (Exception e) {
			return "";
		}
	}

	private boolean decodeStatus(byte[] data) {
		int chk = 0;
		StringBuffer sb = new StringBuffer();
		
		try {
			if (data[0] != (byte)0xaa || data[1] != (byte)0xaa || data[2] != (byte)0xaa || data[3] != (byte)0x43) {
				return false;
			}
			for (int i = 4; i < 71; i++) {
				chk += data[i];
				sb.append((char)data[i]);
			}
			if (data[71] != (byte)(chk % 256) || data[72] != (byte)(chk / 256)) {
				return false;
			}
			String s = sb.toString();
			if (!s.equals(status)) {
				status = s;
				String[] sa = s.split(",");
				if (sa.length != 20) {
					return false;
				}
				if (model == -1) {
					switch (sa[1]) {
					case "13K":
						model = 13;
						break;
					case "20K":
						model = 20;
						break;
					}
					Bus.postIfChanged(new IDEvent(this, sa[1]));
				}
				Bus.postIfChanged(new OperateEvent(this, (sa[2].equals("O"))));
				Bus.postIfChanged(new TXEvent(this, (sa[3].equals("T"))));
				Bus.postIfChanged(new InputEvent(this, Integer.parseInt(sa[5])));
				Bus.postIfChanged(new BandEvent(this, decodeBand(sa[6])));
				Bus.postIfChanged(new AntennaEvent(this, "tx", Character.getNumericValue(sa[7].charAt(0))));
				Bus.postIfChanged(new ATUEvent(this, sa[7].charAt(1) == 'a'));
				Bus.postIfChanged(new AntennaEvent(this, "rx", Character.getNumericValue(sa[8].charAt(0))));
				Bus.postIfChanged(new PAEvent<String>(this, "level", sa[9]));
				Bus.postIfChanged(new PAEvent<Integer>(this, "power", Integer.parseInt(sa[10])));
				Bus.postIfChanged(new SWREvent(this, "atu", (sa[11].startsWith("_"))? 0.0 : Double.parseDouble(sa[11])));
				Bus.postIfChanged(new SWREvent(this, "antenna", (sa[12].startsWith("_"))? 0.0 : Double.parseDouble(sa[12])));
				Bus.postIfChanged(new PAEvent<Double>(this, "v", (sa[13].startsWith("_"))? 0.0 : Double.parseDouble(sa[13])));
				Bus.postIfChanged(new PAEvent<Double>(this, "i", (sa[14].startsWith("_"))? 0.0 : Double.parseDouble(sa[14])));
				if (model == 13) {
					Bus.postIfChanged(new MemoryBankEvent(this, sa[4]));
					Bus.postIfChanged(new TemperatureEvent(this, "heatsink", Integer.parseInt(sa[15].trim())));
				} else if (model == 20) {
					Bus.postIfChanged(new TemperatureEvent(this, "upper", Integer.parseInt(sa[15].trim())));
					Bus.postIfChanged(new TemperatureEvent(this, "lower", Integer.parseInt(sa[16].trim())));
					Bus.postIfChanged(new TemperatureEvent(this, "combiner", Integer.parseInt(sa[17].trim())));
				}
				Bus.postIfChanged(new WarningEvent(this, sa[18]));
				Bus.postIfChanged(new AlarmEvent(this, sa[19]));
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	synchronized private boolean command(byte data) {
		byte[] cmda = {0x55, 0x55, 0x55, 0x01, data, data};
		
		try {
			commPort.clear();
			commPort.writeBytes(cmda);
			if (data != (byte)0x90) { // non-status command
				byte[] resa = new byte[6];
				commPort.readBytes(resa, 0, resa.length, RESPONSE_TIMEOUT);
				return resa[0] == (byte)0xaa && resa[1] == (byte)0xaa && resa[2] == (byte)0xaa && resa[4] == data;
			} else { // status command
				byte[] resa = new byte[75];
				commPort.readBytes(resa, 0, resa.length, RESPONSE_TIMEOUT);
				return decodeStatus(resa);
			}
		} catch (Exception e) {
			return false;
		}
	}
}
