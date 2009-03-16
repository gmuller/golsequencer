package golSequencer.sequencerControl;

public enum ListConstants {

	MIDI_OUT_LIST ("Midi Output", 21),
	MIDI_CHANNEL_LIST ("Channel", 23),
	KEY_LIST ("Key", 25),
	SCALE_LIST ("Scale", 27),
	STEP_SIZE_LIST ("Step Size", 29),
	UPDATE_INTERVAL_LIST("Update Time", 32),
	DRUM_MAP_LIST ("Drum Map", 30),
	MODE_LIST ("Seq Mode", 31);

	private String label;
	private int id;

	ListConstants(String label, Integer id){
		this.label = label;
	};

	public String getLabel(){ return label; }
	public int getId(){ return id; }
}
