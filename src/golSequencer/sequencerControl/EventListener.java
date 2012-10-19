package golSequencer.sequencerControl;

import golSequencer.sequencer.SeqMode;
import golSequencer.sequencer.SeqPreset;
import rwmidi.MidiOutput;
import rwmidi.RWMidi;

import com.grantmuller.midiReference.NoteReference;
import com.grantmuller.midiReference.ScaleReference;
import com.grantmuller.midiReference.TimeBase;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.Range;

public class EventListener implements ControlListener {
	int value;
	ControlSequencer sequencer;
	EventListener(ControlSequencer sequencer){
		this.sequencer = sequencer;
	}

	public void controlEvent(ControlEvent theEvent) {

		String[] splitEventName = theEvent.getName().split(":");
		String eventName = splitEventName[0];
		int sequencerId = Integer.parseInt(splitEventName[1]);

		if (theEvent.getController().getClass() == controlP5.Numberbox.class){
			BoxConstants boxConstant = BoxConstants.valueOf(eventName);

			switch (boxConstant){
			case COLUMNS: sequencer.setNumXCells(handleEvent(theEvent, boxConstant)); break;
			case ROWS: sequencer.setNumYCells(handleEvent(theEvent, boxConstant)); break;
			case OCTAVE: sequencer.setOctave(handleEvent(theEvent, boxConstant)); break;
			case RANGE: sequencer.setRange(handleEvent(theEvent, boxConstant)); break;
			default: break;
			}
		}

		if (theEvent.getController().getClass() == controlP5.MultiListButton.class){
			ListButtonConstants buttonConstant = ListButtonConstants.valueOf(eventName);
			int value = (int) theEvent.getController().getValue();
			String label = theEvent.getController().getCaptionLabel().getText();
			switch (buttonConstant){
			case MIDIOUT:
				MidiOutput midiOut = RWMidi.getOutputDevices()[value].createOutput();
				if (midiOut != null){
					sequencer.setOutput(midiOut);
				}
				break;
			case CHANNEL: sequencer.setChannel(value); break;
			case KEY: sequencer.setBaseNote(NoteReference.valueOf(label)); break;
			case SCALE: sequencer.setBaseScale(ScaleReference.valueOf(label)); break;
			case STEPSIZE: sequencer.setStepSize(TimeBase.valueOf(label)); break;
			case UPDATEINTERVAL: sequencer.setUpdateInterval(TimeBase.valueOf(label)); break;
			case DRUM_MAP: sequencer.setDrumMapMode(SeqPreset.valueOf(label)); break;
			case SEQ_MODE: sequencer.setMode(SeqMode.valueOf(label)); break;
			}
			sequencer.drawInfo();
		}		

		if (theEvent.getController().getClass() == controlP5.Range.class){
			SliderConstants sliderConstant = SliderConstants.valueOf(eventName);
			Range range = (Range) theEvent.getController();
			int highValue = (int) range.getHighValue();
			int lowValue = (int) range.getLowValue();

			switch (sliderConstant){
			case VELOCITY: 
				sequencer.setMaxVelocity(highValue);
				sequencer.setMinVelocity(lowValue);
				break;
			}
		}

		if (theEvent.getController().getClass() == controlP5.Button.class ||
				theEvent.getController().getClass() == controlP5.Toggle.class){
			BoxConstants buttonConstant = BoxConstants.valueOf(eventName);

			switch (buttonConstant){
			case RANDOM: sequencer.randomizeCells(); break;
			case CLEAR: sequencer.clearCells(); break;
			case KILLNOTES: 
				if (theEvent.getController().getValue() == 0){
					sequencer.setKillNotes(false);
				} else {
					sequencer.setKillNotes(true);
				}			
				break;
			case ACTIVE:
				if (theEvent.getController().getValue() == 0){
					sequencer.setActive(false);
				} else {
					sequencer.setActive(true);
				}			

				break;
			case LOAD:
				try {
					sequencer.loadConfiguration();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case SAVE:
				try {
					sequencer.writeConfiguration(eventName, sequencerId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default: break;
			}
		}
	}

	private Integer handleEvent(ControlEvent theEvent, BoxConstants boxConstant){
		value = (int) theEvent.getValue();
		if(value > boxConstant.getHighValue()){
			theEvent.getController().setValue(boxConstant.getHighValue());
		}
		if(value < boxConstant.getLowValue()){
			theEvent.getController().setValue(boxConstant.getLowValue());
		}
		return value;
	}
}
