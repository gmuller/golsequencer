package golSequencer;



import golSequencer.sequencer.Sequencer;
import golSequencer.sequencerControl.ControlSequencer;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import processing.core.PApplet;
import rwmidi.MidiInput;
import rwmidi.MidiInputDevice;
import rwmidi.RWMidi;
import rwmidi.SyncEvent;
import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.MultiList;
import controlP5.MultiListButton;
import controlP5.Numberbox;
import controlP5.Radio;
import controlP5.Textarea;

public class GOLSequencer extends PApplet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3033023797726941226L;


	public static void main(String[] args) {
		PApplet.main(new String[] { "golSequencer.GOLSequencer" });
	}

	ArrayList<ControlSequencer> sequencers = new ArrayList<ControlSequencer>();
	ControlP5 controlP5;
	Button start, stop, reset, save, load;
	Numberbox tempo;
	Radio syncOptions;
	boolean externalSync = false;
	Textarea versionBox;
	static final String versionBoxString = "Game of Life Sequencer Bank Beta";
	boolean gameStarted = false;
	int pulseCount = 0;
	MidiInput syncIn;
	private MultiList optionsList;
	Constants currentTab = Constants.SEQUENCER1_TAB;
	Textarea settingsArea;
	String currentSettings;
	Metronome metronome;
	//private boolean takeScreenShot;

	public void setup(){
		size(800, 500);
		metronome = new Metronome(this);
		controlP5 = new ControlP5(this);
		setupControlP5();

		//Setup Sequencers
		for (int i = 1; i < 7; i++){
			sequencers.add(new ControlSequencer(this, i, 340, 50, true, controlP5));
		}
	}

	private void setupControlP5() {
		//Setup Tabs
		controlP5.tab("default").activateEvent(true).setId(Constants.SEQUENCER1_TAB.id());
		controlP5.tab("default").setLabel(Constants.SEQUENCER1_TAB.getName());
		controlP5.tab(Constants.SEQUENCER2_TAB.getName()).activateEvent(true).setId(Constants.SEQUENCER2_TAB.id());
		controlP5.tab(Constants.SEQUENCER3_TAB.getName()).activateEvent(true).setId(Constants.SEQUENCER3_TAB.id());
		controlP5.tab(Constants.SEQUENCER4_TAB.getName()).activateEvent(true).setId(Constants.SEQUENCER4_TAB.id());
		controlP5.tab(Constants.SEQUENCER5_TAB.getName()).activateEvent(true).setId(Constants.SEQUENCER5_TAB.id());
		controlP5.tab(Constants.SEQUENCER6_TAB.getName()).activateEvent(true).setId(Constants.SEQUENCER6_TAB.id());
		controlP5.tab(Constants.OPTIONS_TAB.getName()).activateEvent(true).setId(Constants.OPTIONS_TAB.id());
		versionBox = controlP5.addTextarea("VersionBox", versionBoxString, width-165, 3, 200, 15);
		versionBox.setTab(Constants.OPTIONS_TAB.getName());

		syncOptions = controlP5.addRadio("radio",10,70);
		syncOptions.addItem("internal", 100);
		syncOptions.addItem("external", 101);
		syncOptions.setTab(Constants.OPTIONS_TAB.getName());

		start = controlP5.addButton(" Play", 101, 290, 52, 38, 15);
		stop = controlP5.addButton(" Stop", 102, 290, 97, 38, 15);
		reset = controlP5.addButton(" Reset", 103, 290, 137, 38, 15);
		tempo = controlP5.addNumberbox("Tempo", 120, 290, 180, 38, 14);

		optionsList = controlP5.addMultiList(Constants.MIDI_SYNC_LIST.name(), 10, 52, 145, 12);
		optionsList.setTab(Constants.OPTIONS_TAB.getName());
		MultiListButton syncList = optionsList.add("Midi Sync Port", 1000);

		MidiInputDevice devices[] = RWMidi.getInputDevices();
		String deviceName;
		for (int i = 0; i < devices.length; i++) {
			deviceName = devices[i].getName();
			if (devices[i].getName().length() > 22)
				deviceName = devices[i].getName().substring(0, 21);
			createListButton(deviceName, i, syncList, Constants.MIDI_SYNC_LIST);
		}

		save = controlP5.addButton(Constants.SAVE_BUTTON.name(), 10, 10, 175, 38, 15);
		save.setTab(Constants.OPTIONS_TAB.getName());
		save.setLabel(" Save");

		load = controlP5.addButton(Constants.LOAD_BUTTON.name(), 10, 70, 175, 38, 15);
		load.setTab(Constants.OPTIONS_TAB.getName());
		load.setLabel(" Load");

		settingsArea = controlP5.addTextarea("Options", currentSettings, 10, 200, 135,100);
		settingsArea.setTab(Constants.OPTIONS_TAB.getName());
		populateText();
	}

	public void draw(){

		background(0);
		fill(0);
		//stroke(255);
		rect(0,0,width, height);
		switch (currentTab){
		case SEQUENCER1_TAB: processSequencer(sequencers.get(0)); break;
		case SEQUENCER2_TAB: processSequencer(sequencers.get(1)); break;
		case SEQUENCER3_TAB: processSequencer(sequencers.get(2)); break;
		case SEQUENCER4_TAB: processSequencer(sequencers.get(3)); break;
		case SEQUENCER5_TAB: processSequencer(sequencers.get(4)); break;
		case SEQUENCER6_TAB: processSequencer(sequencers.get(5)); break;
		}

		//		if (takeScreenShot){
		//			controlP5.draw();
		//			saveFrame("screenshot.png");
		//		}
	}

	public void processSequencer(Sequencer sequencer){
		if (mousePressed){
			sequencer.changeCellState();
		}
		sequencer.drawCells();
	}


	public void processEvents(SyncEvent syncEvent){
		switch (syncEvent.getStatus()){
		case SyncEvent.TIMING_CLOCK:
			pulseCount++;
			if (pulseCount != 2 && pulseCount % 3 !=0 && pulseCount % 4 != 0) 
			{
				break;
			}
			for (Sequencer sequencer : sequencers){
				sequencer.handleTiming(pulseCount);
			}
			//if (pulseCount == 384) pulseCount = 0;
			break;
		case SyncEvent.START: gameStarted = true; 
		for (Sequencer sequencer : sequencers){
			sequencer.handleTiming(pulseCount);
		}
		break;
		case SyncEvent.STOP: gameStarted = false; break;
		case SyncEvent.SONG_POSITION_POINTER:
			resetSequencers();
			break;
		}
	}

	private void resetSequencers(){
		for (Sequencer sequencer : sequencers){
			sequencer.setXStep(0);
			sequencer.setYStep(0);
			pulseCount = 0;
		}
	}

	void controlEvent(ControlEvent theEvent) {
		if (theEvent.isTab()) {
			currentTab =  Constants.get(theEvent.tab().id());
			if (currentTab != Constants.OPTIONS_TAB){
				if(currentTab != Constants.SEQUENCER1_TAB){
					moveControls(currentTab.getName());
				} else {
					moveControls("default");
				}
			}
			return;
		}

		if (theEvent.controller().getClass() == controlP5.Radio.class){
			//handling elsewhere
			return;
		}

		String[] splitEventName = theEvent.name().split(":");
		String eventName = splitEventName[0];
		int value = (int) theEvent.controller().value();

		if (theEvent.controller().parent().id() == Constants.OPTIONS_TAB.id()){
			Constants constant = Constants.valueOf(eventName);		
			switch (constant){
			case MIDI_SYNC_LIST:
				if (externalSync){
					syncIn.closeMidi();
					syncIn =  RWMidi.getInputDevices()[value].createInput(48);
					if (syncIn != null){
						syncIn.plug(this, "processEvents");
					}
				}
				break;
			case SAVE_BUTTON: saveGlobalConfig(); break;
			case LOAD_BUTTON: loadGlobalConfig(); break;
			}
			populateText();
		}

		if (eventName.contains("Tempo")){
			metronome.setTempo(handleEvent(theEvent, 220, 40));
		}

		if (!externalSync){
			if (eventName.contains("Play")){
				if (!metronome.isRunning()){
					metronome.startThread();
					metronome.setRunning(true);
				}
			}

			if (eventName.contains("Stop")){
				if (metronome.isRunning()){
					metronome.stop();
					metronome.setRunning(false);
				}
			}

			if (eventName.contains("Reset")){
				resetSequencers();
			}
		}
	}


	public void moveControls(String tabName){
		tempo.moveTo(tabName);
		start.moveTo(tabName);
		stop.moveTo(tabName);
		reset.moveTo(tabName);
	}

	private Integer handleEvent(ControlEvent theEvent, int highValue, int lowValue){
		int value = (int) theEvent.value();
		if(value > highValue){
			theEvent.controller().setValue(highValue);
		}
		if(value < lowValue){
			theEvent.controller().setValue(lowValue);
		}
		return value;
	}

	public void radio(int theID){
		switch (theID){
		case 100: 
			externalSync = false;
			if (syncIn != null){
				syncIn.closeMidi(); 
			}
			break;
		case 101: 
			if (metronome.isRunning()){
				metronome.stop();
			}
			externalSync = true; 
			syncIn = RWMidi.getInputDevices()[0].createInput(48);
			if (syncIn != null){
				syncIn.plug(this, "processEvents");
			}
			populateText();
			break;
		}
	}

	private void createListButton(String name, int id, MultiListButton listTo, Constants constant){
		MultiListButton thisButton;
		thisButton = listTo.add(constant.name() + ":" + id, id);
		thisButton.setLabel(name);
	}

	private void populateText() {
		String midiInString = "NONE";
		if (syncIn != null){
			midiInString = syncIn.getName();
			if (midiInString.length() > 22){
				midiInString = midiInString.substring(0, 21);
			}
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("Midi Sync: " + midiInString.toUpperCase() + "\n");

		currentSettings = buffer.toString();
		settingsArea.setText(currentSettings);
	}

	public void saveGlobalConfig(){
		FileDialog fd = new FileDialog(this.frame, "Save config", FileDialog.SAVE);
		fd.setVisible(true);
		String dir = fd.getDirectory();
		String fileName = fd.getFile(); 
		fd.dispose();

		FileOutputStream file = null;
		try {
			file = new FileOutputStream(dir + fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = new Document();
		XMLOutputter output = new XMLOutputter();
		output.setFormat(Format.getPrettyFormat());

		Element root = new Element("sequencers");
		for (int i = 0; i < 6; i++){
			root.addContent(sequencers.get(i).addSequencerConfig(i+1));
		}

		doc.addContent(root);
		try {
			output.output(doc, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	@SuppressWarnings("unchecked")
	public void loadGlobalConfig(){
		FileDialog fd = new FileDialog(this.frame, "Load config", FileDialog.LOAD);
		fd.setVisible(true);
		String dir = fd.getDirectory();
		String fileName = fd.getFile(); 
		fd.dispose();
		
        Document doc = null;
        SAXBuilder sb = new SAXBuilder();

        try {
            doc = sb.build(new File(dir+fileName));
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        Element root = doc.getRootElement();
        List<Element> allSequencers = root.getChildren("sequencer");
        
        for (int i = 0; i < 6; i++){
        	sequencers.get(i).processSequencerConfiguration(allSequencers.get(i));
        }
	}

	//	public void keyPressed() {
	//		if (key == 's') {
	//			takeScreenShot = true;
	//		}
	//	}
}
