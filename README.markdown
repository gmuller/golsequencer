The Game of Life Sequencer Bank is based on a game of life sequencer screencast produced by Wesen in 2008. To see the original screencast <a href="http://vimeo.com/1824904">click here</a>. It is a bank of 6 step or drum sequencers that generate patterns based on the basics of Conway's Game of Life cellular automaton. Each sequencer operates independently of the others, and can be configured in a variety of ways.

The Game of Life Sequencer Bank is written in Java using the Processing, controlP5 and a customized RWMidi library. It should work wherever Java 1.5 is available (Windows, OS X, Linux), though it has only been tested in Windows and Linux. Any problems should be reported here.

For more info please see the video and example audio at the bottom of the page.

## Download

Game of Life Sequencer Bank Beta- <a href="http://www.grantmuller.com/wp-content/uploads/golseqbank-beta-win.zip">WINDOWS</a> - <a href="http://www.grantmuller.com/wp-content/uploads/golseqbank-beta-osx.zip">OSX</a> - <a href="http://www.grantmuller.com/wp-content/uploads/golseqbank-beta-linux.zip">LINUX</a>

You can check out the source or report an issue <a href="https://github.com/gmuller/golsequencer/issues">here</a>

## Compile from source

1. `git clone git@github.com:gmuller/golsequencer.git`
2. `cd golsequencer`
3. `ant`

## Open in Eclipse

1. Follow the instructions about to compile from source.
2. In Eclipse Navigate to "File -> New -> Other".
3. Select "Java Project from Existing Ant Buildfile".
4. Navigate to .../golsequencer/build.xml
5. Complete the wizard

## Documentation

The application is divided into 7 tabs, one tab for each sequencer, and a global options for options affecting all sequencers.

### Sequencer Tabs

<img class="alignnone size-full wp-image-563" title="screenshot" src="http://www.grantmuller.com/wp-content/uploads/screenshot.gif" alt="screenshot" width="530" height="331" />

Under the options readout is a dropdown labeled "Sequencer 1" containing the following options for the sequencer:

<dl class="dl-horizontal">
<dt>Midi Output</dt> 
<dd>Sets the MIDI Output that the sequencer will use. If a MIDI port is unavailable for any reason you will not be able to select it.</dd>
    
<dt>Channel</dt> 
<dd>Set the MIDI Channel of the sequencer. Options are 1-16.</dd>
    
<dt>Key</dt> 
<dd>Sets the key of the sequencer. In SINGLE_NOTE SEQ_MODE this is the only note that will play. This options is ignored in DRUM_SEQUENCE mode.</dd>
    
<dt>Scale</dt> 
<dd>The scale/mode from which notes will be selected.</dd>
    
<dt>Step Size</dt> 
<dd>The size of each step. Values range from 32nd note triplets to Whole Notes.</dd>
    
<dt>Update Time</dt> 
<dd>Forces the game to update at an interval independent of the sequencer speed. Use this option to generate patterns more quickly. Values range from 32nd note triplets to Whole Notes.</dd>
    
<dt>Drum Map</dt> 
<dd>The Drum Map to use. The IMPULSE map uses the note values native the Ableton Live's Impulse drum machine. The ALL_KEYS map starts at C4 and uses each subsequent note until the number of columns is exhausted.</dd>
    
<dt>Seq Mode</dt> 
<dd>The mode of the sequencer. Modes are as follows:
<dl>
<dt>DRUM_SEQUENCE</dt> 
<dd>Steps column at a time t the speed selected in the TIME_BASE option. Outputs the note values of the active notes in that column. Notes are selected based on the row and which drum map was used.</dd>

<dt>STEP_SEQUENCE</dt> 
<dd>Steps cell at a time, left to right top to bottom at the speed selected in the TIME_BASE option. Outputs a midi note for each on cell. Notes are selected based on an algorithm using the key, scale, octave and range.</dd>
    		
<dt>ORIGINAL</dt>
<dd>Outputs not based on the original Wesen setup. The game is updated at the interval selected in the the TIME_BASE option, and outputs are sent for ALL active cells. Best used with monophonic synths.</dd>
    		
<dt>SINGLE_NOTE</dt> 
<dd>Acts as the step sequencer, but will only output the note selected in the KEY option.</dd>
</dl>
</dl>

To the left of the sequencer there are several buttons controlling playback.

<dl>
<dt>Play</dt> 
<dd>Starts playback of all sequencers.</dd>
    
<dt>Stop</dt> 
<dd>Stops playback of all sequencers.</dd>
    
<dt>Tempo</dt> 
<dd>Sets the playback tempo of all sequencers</dd>
</dl>

To the right of the sequencer you have the following options:

<dl>
<dt>Cell Matrix</dt> 
<dd>The cell matrix itself is an adjustable 32 x 32 grid of cells that represent on or off states. The game is played in the grid, and the patterns generated are played in various ways depending on the Seq Mode (see Sequencer Options). Clicking on a cell will toggle its on or off state.</dd>
    
<dt>Active</dt>
<dd>Toggle representing the on/off state of the sequencer. A sequencer that is turned off will still step, it will just not output any notes.</dd>
    
<dt>Kill Notes</dt> 
<dd>When this toggle is activated, only one note will play at a time, the last note that the step sequencer saw as on.</dd>
    
<dt>Random</dt> 
<dd>This button will generate a random pattern in the cell matrix. Clicking multiple times will turn on additional cells.</dd>
    
<dt>Clear</dt>
<dd>This button will clear all cells in the Cell Matrix</dd>
    
<dt>Columns</dt>
<ddChange the number of columns in the cell matrix. The maximum number of columns is 32.</dd>
    
<dt>Rows</dt>
<dd>Change the number of rows in the cell matrix. The maximum number of rows is 32.</dd>
    
<dt>Octave</dt> 
<dd>Change the starting octave value of the notes that are generated. Default is 3. Max is 8.</dd>
    
<dt>Range</dt> 
<dd>Change the octave range when randomizing notes. Default and maximum are 3.</dd>

<dt>Velocity</dt> 
<dd>The velocity of generated notes will reside randomly in the range represented by this slider. The slider has two ends so that notes can be created in any velocity range between 0 and 127.</dd>
</dl>

### Options Tab

<img class="size-full wp-image-564 alignleft" title="screenshot2" src="http://www.grantmuller.com/wp-content/uploads/screenshot2.gif" alt="screenshot2" width="193" height="248" />

The following options apply to all sequencers.

<dl>
<dt>Midi Sync Port</dt> 
<dd>Which port to use for incoming sync. Has no effect if sync mode is set to 'internal'</dd>
    
<dt>Sync Mode</dt> 
<dd>Selects whether to use internal or external sync.</dd>
</dl>
