package golSequencer.sequencerControl;

public enum BoxConstants {
	ACTIVE ("Active", "toggle", 18, 1, 0),
	KILLNOTES ("Kill Notes", "toggle", 13, 1, 0),
	COLUMNS ("columns", "box", 11, 32, 2), 
	ROWS ("rows", "box", 12, 32, 2),
	OCTAVE ("octave", "box", 16, 8, 0),
	RANGE ("range", "box", 17, 3, 1),
	RANDOM ("Random", "button", 14, 1, 0),
	CLEAR ("Clear", "button", 14, 1, 0),
	SAVE (" Save", "button", 21, 1, 0),
	LOAD (" Load", "button", 22, 1, 0);
	
	
	
	private String label;
	private String type;
	private int id;
	private int highValue;
	private int lowValue;

	BoxConstants(String label, String type, Integer id, int highValue, int lowValue){
		this.label = label;
		this.type = type;
		this.id = id;
		this.highValue = highValue;
		this.lowValue = lowValue;
	};

	public String getLabel(){ return label; }
	public String getType(){ return type; }
	public int getId(){ return id; }
	public int getHighValue() { return highValue; }
	public int getLowValue() { return lowValue; }
}
