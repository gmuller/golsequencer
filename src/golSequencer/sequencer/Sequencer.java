package golSequencer.sequencer;

import java.util.HashMap;
import java.util.Map;

import midiReference.MidiReference;
import midiReference.NoteReference;
import midiReference.ScaleReference;
import midiReference.TimeBase24;
import processing.core.PApplet;
import processing.core.PGraphics;
import rwmidi.MidiOutput;
import rwmidi.RWMidi;

public class Sequencer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8931646463830193187L;
	private Integer sequencerId;
	private boolean active;
	private boolean standAlone;
	protected int numCellsX;
	protected int numCellsY;
	private int cellSize;
	private int currCellX = -1;
	private int currCellY = -1;
	private boolean currDrawMode = false;
	private boolean cells[][];
	private int startX, startY;
	private int scale[];
	private int colorValue;
	private int numValues;
	private int xStep = 0;
	private int yStep = 0;
	private int yReading;
	private int xReading;
	private int stepSize;
	private int updateInterval;
	private SeqMode mode;

	MidiReference midiReference = MidiReference.getMidiReference();
	private static Map<Integer, Integer> drumNoteMap = new HashMap<Integer, Integer>();

	private NoteReference baseNote;
	private ScaleReference baseScale;
	boolean killNotes = false;
	private MidiOutput output;
	private int currentNote = 0;
	private int channel;
	private int octave;
	private int range;
	private int minVelocity;
	private int maxVelocity;

	protected PApplet parent;
	protected PGraphics buffer;
	private SeqPreset drumMapMode;

	public Sequencer(Object p, Integer sequencerId, int startX, int startY, boolean standAlone){
		if (p instanceof PGraphics)
			buffer = (PGraphics) p;

		if (p instanceof PApplet)
			parent = (PApplet) p;

		this.sequencerId = sequencerId;
		this.active = true;
		this.killNotes = false;

		this.startX = startX;
		this.startY = startY;
		this.standAlone = standAlone;
		if (this.standAlone)
			this.output = RWMidi.getOutputDevices()[0].createOutput();
		colorValue = parent.color(255, 0, 0, 50);

		stepSize = TimeBase24.EIGHTH.getValue();
		updateInterval = 0;
		mode = SeqMode.STEP_SEQUENCE;
		numCellsX = 32;
		numCellsY = 32;
		cells = new boolean[numCellsX][numCellsY];
		cellSize = 12;
		channel = sequencerId - 1;
		octave = 3;
		range = 3;
		minVelocity = 64;
		maxVelocity = 110;
		baseNote = NoteReference.C;
		baseScale = ScaleReference.MAJOR;
		initScale();
		drumMapMode = SeqPreset.IMPULSE;
		makeDrumMap();	
	}

	public void initScale(){
		scale = MidiReference.createScale(baseScale, baseNote);
		numValues = scale.length-1;
	}
	public void changeCellState() {
		int x;
		int y;
		if (parent.mouseX > startX && parent.mouseX < startX  + (numCellsX * cellSize)){
			x = PApplet.floor((float)((parent.mouseX - 1) - startX)/(float)cellSize);
		}else {
			return;
		}
		if (parent.mouseY > startY && parent.mouseY < startY  + (numCellsY * cellSize)){
			y = PApplet.floor((float)((parent.mouseY -1) - startY)/(float)cellSize);
		} else {
			return;
		}

		if (x < numCellsX && y < numCellsY){
			if (currCellX != x || currCellY != y){
				if (currCellX == -1){
					if (cells[x][y]){
						currDrawMode = false;
					}else {
						currDrawMode = true;
					}
				}
				//draw
				cells[x][y] = currDrawMode;
				currCellX = x;
				currCellY = y;
			}
		}
	}

	public void drawCells(){
		int numCellsX = this.numCellsX;
		int numCellsY = this.numCellsY;
		int cellSize = this.cellSize;
		int startX = this.startX;
		int startY = this.startY;
		int colorValue = this.colorValue;

		for (int x = 0; x < numCellsX; x++){
			for (int y = 0; y < numCellsY; y++){
				parent.fill(colorValue);
				parent.stroke(0);
				parent.rect(startX + (x*cellSize+1),
						startY + (y*cellSize+1),
						cellSize, cellSize);
				drawCell(x, y);
			}
		}

		parent.line(startX + (numCellsX/2 * cellSize),startY,startX + (numCellsX/2 * cellSize),
				startY + (numCellsY * cellSize));
		parent.line(startX, startY + (numCellsY/2 * cellSize),startX + (numCellsX * cellSize),
				startY + (numCellsY/2 * cellSize));

		if (mode == SeqMode.STEP_SEQUENCE || mode == SeqMode.SINGLE_NOTE){
			drawCell(xReading, yReading, 127);
		} else if (mode == SeqMode.DRUM_SEQUENCE){
			for (int i = 0; i < numCellsY; i++){
				drawCell(xReading, i, 127);
			}
		}
	}

	void drawCell(int x, int y, int color){
		int cellSize = this.cellSize;
		int startX = this.startX;
		int startY = this.startY;

		parent.fill(color);
		parent.stroke(0);
		parent.rect(startX + (x*cellSize+1), startY + (y*cellSize+1), cellSize, cellSize);
	}

	void drawCell(int x, int y){
		if (cells[x][y]){
			drawCell(x, y, 255);
		}
	}

	public void randomizeCells() {
		for (int i = 0; i < parent.random(0, numCellsX*numCellsY);i++){
			cells[(int) parent.random(0, numCellsX)][(int) parent.random(0, numCellsY)] = true;
		}
	}

	public void clearCells() {
		cells = new boolean[numCellsX][numCellsY];
	}

	public void original(){
		updateGameOfLife();
	}

	public void stepSequence(){		
		if (xStep >= this.numCellsX){
			yStep++;
			if (yStep > numCellsY-1){
				yStep = 0; //reset yStep
				updateGameOfLife();
			}
			xStep = 0; //reset xStep
		}
		xReading = xStep;
		yReading = yStep;
		if (cells[xStep][yStep]){
			outputCell(xStep, yStep);
		}
		xStep++;
	}

	public void drumSequence(){
		if (xStep >= numCellsX){
			xStep = 0;
			updateGameOfLife();
		}
		xReading = xStep;
		for (int i = 0; i < numCellsY; i++){
			if (cells[xStep][i]){
				outputCell(xStep, i);
			}
		}
		xStep++;
	}

	void updateGameOfLife(){
		int numCellsX = this.numCellsX;
		int numCellsY = this.numCellsY;
		boolean cells[][] = this.cells;
		boolean newCells[][] = new boolean[numCellsX][numCellsY];
		for (int y = 0; y < numCellsY; y++){
			for (int x = 0; x < numCellsX; x++){
				int neighbors = cellNeighbors(x, y);
				newCells[x][y] = cells[x][y];
				if (neighbors < 2){
					newCells[x][y] = false;
					killCell(x, y);
				}else if (neighbors > 3){
					newCells[x][y] = false;
					killCell(x, y);
				} else if (neighbors == 3 && !cells[x][y]){
					newCells[x][y] = true;
					if (mode == SeqMode.ORIGINAL){ outputCell(x, y); }
				}
			}
		}
		this.cells = newCells;
	}

	int cellNeighbors(int x, int y){
		int result = 0;
		for (int i = -1; i <= 1; i++){
			for (int j = -1; j <= 1; j++){
				int newX = x + i;
				int newY = y + j;
				if (newX == x && newY == y)
					continue;

				if (newX >= 0 && newY >= 0 && newX < this.numCellsX && newY < this.numCellsY){
					if (cells[newX][newY])
						result++;
				}
			}
		}
		return result;
	}

	static int cellValue(int x, int y, int numValues) {
		return (x + y) % numValues;
	}

	@SuppressWarnings("static-access")
	int pitchValue(int x, int y) {
		int returnValue = 0;
		switch (mode){
		case ORIGINAL:
		case STEP_SEQUENCE: 
			int octaveAdd = octave * 12;
			int rangeAdd = parent.parseInt(parent.random(-range, range)) * 12;
			int note = scale[cellValue(x, y, numValues)] + (x + y) % 3 * 12 + (octaveAdd);
			if (note > 127) {
				note = note - 12;
			}

			if ((note + rangeAdd) < 0 || (note + rangeAdd) > 127){
				returnValue = note;
			} else {
				returnValue = note + rangeAdd;
			}
			//System.out.println(midiReference.getNoteName(returnValue) + " , " + rangeAdd);
			break;
		case DRUM_SEQUENCE:
			returnValue = drumNoteMap.get(y); 
			break;
		case SINGLE_NOTE:
			returnValue = baseNote.getBaseNumber() + (octave * 12);
			break;
		}
		return returnValue;
	}

	@SuppressWarnings("static-access")
	void outputCell(int x, int y) {
		if (!active) { return; }
		int pitch = pitchValue(x, y);
		if (standAlone){
			output.sendNoteOn(channel, pitch, parent.parseInt(parent.random(minVelocity, maxVelocity)));
			if(mode != SeqMode.ORIGINAL && killNotes){
				output.sendNoteOff(channel, currentNote, 0);
			}
			currentNote = pitch;
		} else {
			//Do something else (Send to requester for VST wrapper)
		}
	}

	void killCell(int x, int y) {
		if (killNotes) output.sendNoteOff(0, pitchValue(x, y), 0);
	}

	public void handleTiming(int pulseCount) {
		if ((stepSize != 0) && (pulseCount == 0 || pulseCount % stepSize == 0)){
			switch (mode){
			case SINGLE_NOTE:
			case STEP_SEQUENCE: stepSequence(); break;
			case DRUM_SEQUENCE: drumSequence(); break;
			case ORIGINAL: updateGameOfLife(); break;
			}
		}
		
		if (updateInterval!= 0 && mode != SeqMode.ORIGINAL && pulseCount % updateInterval == 0){
			updateGameOfLife();
		}
	}

	private void makeDrumMap(){
		int currentNote = midiReference.getNoteNumber("C4");
		for (int i = 0; i < numCellsY; i++){
			if (drumMapMode == SeqPreset.IMPULSE && midiReference.getNoteName(currentNote).contains("#")){
				currentNote++;
			}
			if (drumMapMode == SeqPreset.IMPULSE && currentNote >= midiReference.getNoteNumber("C#5")){
				currentNote = midiReference.getNoteNumber("C4");
			}
			drumNoteMap.put(i, currentNote);
			currentNote++;
		}
	}

	public int getXStep() {
		return xStep;
	}

	public void setXStep(int step) {
		xStep = step;
		xReading = step;
	}

	public void setYStep(int step) {
		yStep = step;
		yReading = step;
	}

	public void setStepSize(TimeBase24 timeBase) {
		this.stepSize = timeBase.getValue();
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getChannel() {
		return channel + 1;
	}

	public SeqMode getMode() {
		return mode;
	}

	public void setMode(SeqMode mode) {
		this.mode = mode;
	}

	public void setBaseNote(NoteReference baseNote) {
		this.baseNote = baseNote;
		initScale();
	}

	public void setBaseScale(ScaleReference baseScale) {
		this.baseScale = baseScale;
		initScale();	
	}

	public void setNumXCells(int x_cells) {
		numCellsX = x_cells;
		clearCells();
	}

	public void setNumYCells(int y_cells) {
		numCellsY = y_cells;
		clearCells();
		makeDrumMap();
	}

	public void setCELL_SIZE(int cell_size) {
		cellSize = cell_size;
	}

	public Integer getSequencerId() {
		return sequencerId;
	}

	public void setMinVelocity(int minVelocity) {
		this.minVelocity = minVelocity;
	}

	public void setMaxVelocity(int maxVelocity) {
		this.maxVelocity = maxVelocity;
	}

	public void setOctave(int octave) {
		this.octave = octave;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public MidiOutput getOutput() {
		return output;
	}

	public void setOutput(MidiOutput output) {
		this.output = output;
	}

	public SeqPreset getDrumMapMode() {
		return drumMapMode;
	}

	public void setDrumMapMode(SeqPreset drumMapMode) {
		this.drumMapMode = drumMapMode;
	}

	public NoteReference getBaseNote() {
		return baseNote;
	}

	public ScaleReference getBaseScale() {
		return baseScale;
	}

	public int getOctave() {
		return octave;
	}

	public int getRange() {
		return range;
	}

	public int getMinVelocity() {
		return minVelocity;
	}

	public void setScale(int[] scale) {
		this.scale = scale;
	}

	public int getMaxVelocity() {
		return maxVelocity;
	}

	public TimeBase24 getStepSize() {
		return TimeBase24.get(stepSize);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean isActive) {
		this.active = isActive;
	}

	public boolean isKillNotes() {
		return killNotes;
	}

	public void setKillNotes(boolean killNotes) {
		this.killNotes = killNotes;
	}
	
	public TimeBase24 getUpdateInterval() {
		return TimeBase24.get(updateInterval);
	}

	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}
	
	public int getNumCellsX() {
		return numCellsX;
	}

	public int getNumCellsY() {
		return numCellsY;
	}
}
