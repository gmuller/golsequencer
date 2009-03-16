package golSequencer.sequencerControl;

public enum ListButtonConstants {
	MIDIOUT (100, 120),
	CHANNEL (101, 40),
	KEY (102, 40),
	SCALE (103, 100),
	STEPSIZE(104, 100),
	UPDATEINTERVAL(107, 100),
	DRUM_MAP (105, 100),
	SEQ_MODE (106, 100);
	
	private int id;
	private int buttonWidth;
	
	ListButtonConstants(int id, int buttonWidth){
		this.id = id;
		this.buttonWidth = buttonWidth;
	}
	
	public int getId(){ return id; }
	public int getButtonWidth() { return buttonWidth; }
}
