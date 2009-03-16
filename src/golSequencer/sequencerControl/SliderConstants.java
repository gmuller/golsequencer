package golSequencer.sequencerControl;

public enum SliderConstants {

	VELOCITY ("Velocity", 14, 127, 0, 64, 127);
	
	private String label;
	private int id;
	private int highValue;
	private int lowValue;
	private int defaultHigh;
	private int defaultLow;

	SliderConstants(String label, Integer id, int highValue, int lowValue,
			int defaultHigh, int defaultLow){
		this.label = label;
		this.id = id;
		this.highValue = highValue;
		this.lowValue = lowValue;
		this.defaultHigh = defaultHigh;
		this.defaultLow = defaultLow;
	};

	public String getLabel(){ return label; }
	public int getId(){ return id; }
	public int getHighValue() { return highValue; }
	public int getLowValue() { return lowValue; }
	public int getDefaultLowValue() { return defaultHigh; }
	public int getDefaultHighValue() { return defaultLow; }
}
