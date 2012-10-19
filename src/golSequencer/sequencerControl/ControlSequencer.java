package golSequencer.sequencerControl;

import golSequencer.sequencer.SeqMode;
import golSequencer.sequencer.SeqPreset;
import golSequencer.sequencer.Sequencer;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import rwmidi.MidiOutputDevice;
import rwmidi.RWMidi;

import com.grantmuller.midiReference.NoteReference;
import com.grantmuller.midiReference.ScaleReference;
import com.grantmuller.midiReference.TimeBase;

import controlP5.Button;
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
	Button save, load;
	JFileChooser fileChooser;

	//	TODO: Implement controls for the following:
	//		cellsize (resolution) - numberBox

	public ControlSequencer(Object p, Integer sequencerId, int startX, int startY, boolean standAlone, ControlP5 controlP5) {
		super(p, sequencerId, startX, startY, standAlone);
		this.controlP5 = controlP5;
		fileChooser = new JFileChooser();
		controlListener = new EventListener(this);
		int yIncremement = 2;
		String tab = "Sequencer " + sequencerId;
		if (sequencerId == 1) {
			tab = "default";
		}
		for (BoxConstants box : BoxConstants.values()){
			if (box == BoxConstants.SAVE || box == BoxConstants.LOAD){
				break;
			}
			if (box.getType() == "toggle"){
				boolean status = true;
				switch (box){
				case ACTIVE: status = this.isActive();break;
				case KILLNOTES: status = this.isKillNotes(); break;
				default: break;
				}
				controlP5.addToggle(createName(box.name()), status)
				.setPosition(startX + 400, startY + yIncremement)
				.setSize(12, 12)
				.setCaptionLabel(box.getLabel());
			} else if (box.getType() == "button"){
				controlP5.addButton(createName(box.name()))
				.setPosition(startX + 400, startY + yIncremement)
				.setSize(38, 15)
				.setCaptionLabel(box.getLabel());
			} else if (box.getType() == "box"){
				int startValue = 0;
				switch (box){
				case RANGE: startValue = this.getRange(); break;
				case OCTAVE: startValue = this.getOctave(); break;
				case COLUMNS: startValue = this.getNumCellsX(); break;
				case ROWS: startValue = this.getNumCellsY(); break;
				default: startValue = 0;
				}
				controlP5.addNumberbox(createName(box.name()), startValue, startX + 400, startY + yIncremement, 30, 14).setCaptionLabel(box.getLabel());
			}
			controlP5.getController(createName(box.name())).addListener(controlListener);
			controlP5.getController(createName(box.name())).setTab(tab);
			yIncremement+=45;
		}

		controlP5.addRange(createName(SliderConstants.VELOCITY.name()), SliderConstants.VELOCITY.getLowValue(), 
				SliderConstants.VELOCITY.getHighValue(), SliderConstants.VELOCITY.getDefaultLowValue(), 
				SliderConstants.VELOCITY.getDefaultHighValue(), startX + 75, startY + 400, 250, 14).setCaptionLabel(
						SliderConstants.VELOCITY.getLabel());
		controlP5.getController(createName(SliderConstants.VELOCITY.name())).addListener(controlListener);
		controlP5.getController(createName(SliderConstants.VELOCITY.name())).setTab(tab);

		controlP5.addButton(createName(BoxConstants.SAVE.name()))
		.setPosition(10, 175)
		.setSize(38, 15)
		.setTab(tab)
		.setCaptionLabel(" Save")
		.addListener(controlListener);

		controlP5.addButton(createName(BoxConstants.LOAD.name()))
		.setPosition(70, 175)
		.setSize(38, 15)
		.setTab(tab)
		.setCaptionLabel(" Load")
		.addListener(controlListener);

		sequencerList = controlP5.addMultiList("SequencerList" + sequencerId, 10, 210, 145, 12);

		MultiListButton sequencerItem = sequencerList.add("Sequencer " + sequencerId + " Settings", sequencerId);

		for (ListConstants listHead : ListConstants.values()){
			MultiListButton thisList = createList(listHead, sequencerItem);
			thisList.setCaptionLabel(listHead.getLabel());
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
				for (TimeBase timeBase : TimeBase.values()) {
					createListButton(i, timeBase.name(), i, thisList, ListButtonConstants.STEPSIZE);
					i++;
				}
				break;
			case UPDATE_INTERVAL_LIST:
				i=0;
				for (TimeBase timeBase : TimeBase.values()) {
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
		thisButton.setCaptionLabel(name);
		thisButton.setId(buttonType.getId());
		thisButton.setWidth(buttonType.getButtonWidth());
	}

	protected void loadConfiguration() throws Exception{
		FileDialog fd = new FileDialog(getParent().frame, "Load config", FileDialog.LOAD);
		fd.setVisible(true);
		fd.setFile("*.xml");
		fd.setLocation(50, 50);
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
		Element sequencerElement = root.getChild("sequencer");

		processSequencerConfiguration(sequencerElement);

	}

	protected void writeConfiguration(String eventName, int sequencerId) throws Exception{
		FileDialog fd = new FileDialog(getParent().frame, "Save config", FileDialog.SAVE);
		fd.setVisible(true);
		fd.setFile("*.xml");
		fd.setLocation(50, 50);
		String dir = fd.getDirectory();
		String fileName = fd.getFile(); 
		fd.dispose();

		FileOutputStream file = new FileOutputStream(dir + fileName);
		Document doc = new Document();
		XMLOutputter output = new XMLOutputter();
		output.setFormat(Format.getPrettyFormat());

		Element root = new Element("sequencers");
		root.addContent(addSequencerConfig(sequencerId));
		doc.addContent(root);
		output.output(doc, file);	
	}

	public Element addSequencerConfig(int sequencerId){
		Element sequencerElement = new Element("sequencer");
		sequencerElement.setAttribute("id", String.valueOf(sequencerId));
		createElement(sequencerElement, "active", Boolean.toString(isActive()));
		createElement(sequencerElement, "killNotes", Boolean.toString(isKillNotes()));
		createElement(sequencerElement, "columns", Integer.toString(getNumCellsX()));
		createElement(sequencerElement, "rows", Integer.toString(getNumCellsY()));
		createElement(sequencerElement, "octave", Integer.toString(getOctave()));
		createElement(sequencerElement, "range", Integer.toString(getRange()));
		createElement(sequencerElement, "minVelocity", Integer.toString(getMinVelocity()));
		createElement(sequencerElement, "maxVelocity", Integer.toString(getMaxVelocity()));
		createElement(sequencerElement, "channel", Integer.toString(getChannel()));
		createElement(sequencerElement, "key", getBaseNote().toString());
		createElement(sequencerElement, "scale", getBaseScale().toString());
		createElement(sequencerElement, "stepSize", getStepSize().toString());
		createElement(sequencerElement, "updateTime", getUpdateInterval().toString());
		createElement(sequencerElement, "drumMap", getDrumMapMode().toString());
		createElement(sequencerElement, "seqMode", getMode().toString());

		return sequencerElement;
	}

	private void createElement(Element root, String name, String content){
		root.addContent(new Element(name).addContent(content));
	}

	public void processSequencerConfiguration(Element sequencerElement){
		setActive(Boolean.parseBoolean(sequencerElement.getChildText("active")));
		setKillNotes(Boolean.parseBoolean(sequencerElement.getChildText("killNotes")));
		setNumXCells(Integer.parseInt(sequencerElement.getChildText("columns")));
		setNumYCells(Integer.parseInt(sequencerElement.getChildText("rows")));
		setOctave(Integer.parseInt(sequencerElement.getChildText("octave")));
		setRange(Integer.parseInt(sequencerElement.getChildText("range")));
		setMinVelocity(Integer.parseInt(sequencerElement.getChildText("minVelocity")));
		setMaxVelocity(Integer.parseInt(sequencerElement.getChildText("maxVelocity")));
		setChannel(Integer.parseInt(sequencerElement.getChildText("channel")));
		setBaseNote(NoteReference.valueOf(sequencerElement.getChildText("key")));
		setBaseScale(ScaleReference.valueOf(sequencerElement.getChildText("scale")));
		setStepSize(TimeBase.valueOf(sequencerElement.getChildText("stepSize")));
		setUpdateInterval(TimeBase.valueOf(sequencerElement.getChildText("updateTime")));
		setDrumMapMode(SeqPreset.valueOf(sequencerElement.getChildText("drumMap")));
		setMode(SeqMode.valueOf(sequencerElement.getChildText("seqMode")));

		drawInfo();
	}
}

