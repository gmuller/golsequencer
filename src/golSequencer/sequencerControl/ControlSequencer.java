package golSequencer.sequencerControl;

import golSequencer.sequencer.SeqMode;
import golSequencer.sequencer.SeqPreset;
import golSequencer.sequencer.Sequencer;
import midiReference.NoteReference;
import midiReference.ScaleReference;
import midiReference.TimeBase24;
import rwmidi.MidiOutputDevice;
import rwmidi.RWMidi;
import controlP5.ControlP5;
import controlP5.MultiList;
import controlP5.MultiListButton;
import controlP5.Textarea;

public class ControlSequencer extends Sequencer{

	protected ControlP5 controlP5;
	private MultiList sequencerList;
	private EventListener controlListener;
	private Textarea settingsArea;
	private String currentSettings;


	//	TODO: Implement controls for the following:
	//		cellsize (resolution) - numberBox

	public ControlSequencer(Object p, Integer sequencerId, int startX, int startY, boolean standAlone, ControlP5 controlP5) {
		super(p, sequencerId, startX, startY, standAlone);
		this.controlP5 = controlP5;
		controlListener = new EventListener(this);
		int yIncremement = 2;
		String tab = "Sequencer " + sequencerId;
		if (sequencerId == 1) {
			tab = "default";
		}
		for (BoxConstants box : BoxConstants.values()){
			if (box.getType() == "toggle"){
				boolean status = true;
				switch (box){
				case ACTIVE: status = this.isActive();break;
				case KILLNOTES: status = this.isKillNotes(); break;
				}
				controlP5.addToggle(createName(box.name()), status, startX + 400, startY + yIncremement, 12, 12).setLabel(box.getLabel());
			} else if (box.getType() == "button"){
				controlP5.addButton(createName(box.name()), 0, startX + 400, startY + yIncremement, 38, 15).setLabel(box.getLabel());
			} else if (box.getType() == "box"){
				int startValue = 0;
				switch (box){
				case RANGE: startValue = this.getRange(); break;
				case OCTAVE: startValue = this.getOctave(); break;
				case COLUMNS: startValue = this.getNumCellsX(); break;
				case ROWS: startValue = this.getNumCellsY(); break;
				}
				controlP5.addNumberbox(createName(box.name()), startValue, startX + 400, startY + yIncremement, 30, 14).setLabel(box.getLabel());
			}
			controlP5.controller(createName(box.name())).addListener(controlListener);
			controlP5.controller(createName(box.name())).setTab(tab);
			yIncremement+=45;
		}
		
		controlP5.addRange(createName(SliderConstants.VELOCITY.name()), SliderConstants.VELOCITY.getLowValue(), 
				SliderConstants.VELOCITY.getHighValue(), SliderConstants.VELOCITY.getDefaultLowValue(), 
				SliderConstants.VELOCITY.getDefaultHighValue(), startX + 75, startY + 400, 250, 14).setLabel(
						SliderConstants.VELOCITY.getLabel());
		controlP5.controller(createName(SliderConstants.VELOCITY.name())).addListener(controlListener);
		controlP5.controller(createName(SliderConstants.VELOCITY.name())).setTab(tab);

		sequencerList = controlP5.addMultiList("SequencerList" + sequencerId, 10, 170, 145, 12);

		MultiListButton sequencerItem = sequencerList.add("Sequencer " + sequencerId + " Settings", sequencerId);

		for (ListConstants listHead : ListConstants.values()){
			MultiListButton thisList = createList(listHead, sequencerItem);
			thisList.setLabel(listHead.getLabel());
			thisList.setWidth(55);

			switch (listHead){
			case MIDI_OUT_LIST:
				MidiOutputDevice devices[] = RWMidi.getOutputDevices();
				String deviceName;
				for (int i = 0; i < devices.length; i++) {
					deviceName = devices[i].getName();
					if (devices[i].getName().length() > 22)
						deviceName = devices[i].getName().substring(0, 23);
					createListButton(i, deviceName, i, thisList, ListButtonConstants.MIDIOUT);
				}
				break;
			case MIDI_CHANNEL_LIST:
				for (Integer i = 1; i < 17; i++) {
					createListButton(i, i.toString(), i, thisList, ListButtonConstants.CHANNEL);
				}
				break;
			case KEY_LIST:
				int i = 0;
				for (NoteReference key : NoteReference.values()) {
					if (!key.name().contains("sharp")){
						createListButton(i, key.name(), key.getBaseNumber(), thisList, ListButtonConstants.KEY);
						i++;
					}
				}
				break;
			case DRUM_MAP_LIST:
				i = 0;
				for (SeqPreset preset : SeqPreset.values()) {
					createListButton(i, preset.name(), i, thisList, ListButtonConstants.DRUM_MAP);
					i++;
				}
				break;
			case SCALE_LIST:
				i = 0;
				for (ScaleReference scaleRef : ScaleReference.values()) {
					createListButton(i, scaleRef.name(), i, thisList, ListButtonConstants.SCALE);
					i++;
				}
				break;
			case STEP_SIZE_LIST:
				i=0;
				for (TimeBase24 timeBase : TimeBase24.values()) {
					createListButton(i, timeBase.name(), i, thisList, ListButtonConstants.STEPSIZE);
					i++;
				}
				break;
			case UPDATE_INTERVAL_LIST:
				i=0;
				for (TimeBase24 timeBase : TimeBase24.values()) {
					createListButton(i, timeBase.name(), i, thisList, ListButtonConstants.UPDATEINTERVAL);
					i++;
				}
				break;
			case MODE_LIST:
				i = 0;
				for (SeqMode seqMode : SeqMode.values()) {
					createListButton(i, seqMode.name(), i, thisList, ListButtonConstants.SEQ_MODE);
					i++;
				}
				break;
			}
		}
		sequencerList.setTab(tab);
		populateText();
		settingsArea = controlP5.addTextarea(createName("Settings"), currentSettings, 10, 52,135,150);
		settingsArea.setTab(tab);
	}

	private void populateText() {
		String midiOutput = this.getOutput().getName();
		if (this.getOutput().getName().length() > 23){
			midiOutput = this.getOutput().getName().substring(0, 22);
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("MidiOut: " + midiOutput.toUpperCase() + "\n");
		buffer.append("Channel: " + this.getChannel() + "\n");
		buffer.append("Key: " + this.getBaseNote().name() + "\n");
		buffer.append("Scale: " + this.getBaseScale().name() + "\n");
		buffer.append("Step Size: " + this.getStepSize().name() + "\n");
		buffer.append("Update Time: " + this.getUpdateInterval().name() + "\n");
		buffer.append("Drum Map: " + this.getDrumMapMode().name() + "\n");
		buffer.append("Seq Mode: " + this.getMode().name() + "\n");

		currentSettings = buffer.toString();
	}

	public void drawInfo(){
		populateText();
		settingsArea.setText(currentSettings);
	}

	private String createName(String prefix){
		return prefix + ":" + this.getSequencerId();
	}

	private MultiListButton createList(ListConstants listConstant, MultiListButton headList){
		return headList.add(createName(listConstant.getLabel()), listConstant.getId());
	}

	private void createListButton(int incrementer, String name, int id, MultiListButton listTo, 
			ListButtonConstants buttonType){
		MultiListButton thisButton;
		thisButton = listTo.add(createName(buttonType.name() + ":" + incrementer ), id);
		thisButton.addListener(controlListener);
		thisButton.setLabel(name);
		thisButton.setId(buttonType.getId());
		thisButton.setWidth(buttonType.getButtonWidth());
	}
}

