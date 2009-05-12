package golSequencer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Constants {

	OPTIONS_TAB ("all", 1002),
	MIDI_SYNC_LIST ("midi sync list", 1003),
	SEQUENCER1_TAB ("Sequencer 1", 2001),
	SEQUENCER2_TAB ("Sequencer 2", 2002),
	SEQUENCER3_TAB ("Sequencer 3", 2003),
	SEQUENCER4_TAB ("Sequencer 4", 2004),
	SEQUENCER5_TAB ("Sequencer 5", 2005),
	SEQUENCER6_TAB ("Sequencer 6", 2006),
	SAVE_BUTTON ("Save", 3001),
	LOAD_BUTTON ("Load", 3002);
	
	private static final Map<Integer,Constants> lookup 
    = new HashMap<Integer,Constants>();
	
	static {
        for(Constants s : EnumSet.allOf(Constants.class))
             lookup.put(s.id(), s);
   }

	private final int id;
	private final String constantName;
	
	Constants(String name, int id){
		this.id = id;
		this.constantName = name;
	}
	
	public static Constants get(int id) { 
        return lookup.get(id); 
   }

	public int id(){
		return id;
	}
	
	public String getName(){
		return constantName;
	}
}
