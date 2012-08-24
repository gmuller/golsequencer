<h1 class="title_page">Game Of Life Sequencer Bank</h1>

<h2>Introduction</h2>

<p>The Game of Life Sequencer Bank is based on a game of life sequencer screencast produced by Wesen in 2008. To see the original screencast <a href="http://vimeo.com/1824904">click here</a>. It is a bank of 6 step or drum sequencers that generate patterns based on the basics of Conway&#8217;s Game of Life cellular automaton. Each sequencer operates independently of the others, and can be configured in a variety of ways.</p>

<p>The Game of Life Sequencer Bank is written in Java using the Processing, controlP5 and a customized RWMidi library. It should work wherever Java 1.5 is available (Windows, OS X, Linux), though it has only been tested in Windows and Linux. Any problems should be reported here.</p>

<p>For more info please see the video and example audio at the bottom of the page.</p>

<h2>Download</h2>

<p>Game of Life Sequencer Bank Beta- <a href="http://www.grantmuller.com/wp-content/uploads/golseqbank-beta-win.zip">WINDOWS</a> &#8211; <a href="http://www.grantmuller.com/wp-content/uploads/golseqbank-beta-osx.zip">OSX</a> &#8211; <a href="http://www.grantmuller.com/wp-content/uploads/golseqbank-beta-linux.zip">LINUX</a></p>

<p>You can check out the source or report an issue <a href="https://github.com/gmuller/golsequencer">here</a></p>

<h2>Documentation</h2>

<p>The application is divided into 7 tabs, one tab for each sequencer, and a global options for options affecting all sequencers.</p>

<h3>Sequencer Tabs</h3>

<p><img class="alignnone size-full wp-image-563" title="screenshot" src="http://www.grantmuller.com/wp-content/uploads/screenshot.gif" alt="screenshot" width="530" height="331" /></p>

<p>Under the options readout is a dropdown labeled &#8220;Sequencer 1&#8243; containing the following options for the sequencer:</p>

<ul>
    <li><strong>Midi Output</strong> &#8211; Sets the MIDI Output that the sequencer will use. If a MIDI port is unavailable for any reason you will not be able to select it.</li>
    <li><strong>Channel </strong>- Set the MIDI Channel of the sequencer. Options are 1-16.</li>
    <li><strong>Key</strong> &#8211; Sets the key of the sequencer. In SINGLE_NOTE SEQ_MODE this is the only note that will play. This options is ignored in DRUM_SEQUENCE mode.</li>
    <li><strong>Scale</strong> &#8211; The scale/mode from which notes will be selected.</li>
    <li><strong>Step Size</strong> &#8211; The size of each step. Values range from 32nd note triplets to Whole Notes.</li>
    <li><strong>Update Time</strong> &#8211; Forces the game to update at an interval independent of the sequencer speed. Use this option to generate patterns more quickly. Values range from 32nd note triplets to Whole Notes.</li>
    <li><strong>Drum Map</strong> &#8211; The Drum Map to use. The IMPULSE map uses the note values native the Ableton Live&#8217;s Impulse drum machine. The ALL_KEYS map starts at C4 and uses each subsequent note until the number of columns is exhausted.</li>
    <li><strong>Seq Mode</strong> &#8211; The mode of the sequencer. Modes are as follows:<strong>DRUM_SEQUENCE</strong> &#8211; Steps column at a time t the speed selected in the TIME_BASE option. Outputs the note values of the active notes in that column. Notes are selected based on the row and which drum map was used.<strong>STEP_SEQUENCE</strong> &#8211; Steps cell at a time, left to right top to bottom at the speed selected in the TIME_BASE option. Outputs a midi note for each on cell. Notes are selected based on an algorithm using the key, scale, octave and range.<strong>ORIGINAL </strong>- Outputs not based on the original Wesen setup. The game is updated at the interval selected in the the TIME_BASE option, and outputs are sent for ALL active cells. Best used with monophonic synths.<strong>SINGLE_NOTE</strong> &#8211; Acts as the step sequencer, but will only output the note selected in the KEY option.</li>
</ul>

<p>To the left of the sequencer there are several buttons controlling playback.</p>

<ul>
    <li><strong>Play</strong> &#8211; Starts playback of all sequencers.</li>
    <li><strong>Stop</strong> &#8211; Stops playback of all sequencers.</li>
    <li><strong>Tempo</strong> &#8211; Sets the playback tempo of all sequencers</li>
</ul>

<p>To the right of the sequencer you have the following options:</p>

<ul>
    <li><strong>Cell Matrix</strong> &#8211; The cell matrix itself is an adjustable 32 x 32 grid of cells that represent on or off states. The game is played in the grid, and the patterns generated are played in various ways depending on the Seq Mode (see Sequencer Options). Clicking on a cell will toggle its on or off state.</li>
    <li><strong>Active </strong>- Toggle representing the on/off state of the sequencer. A sequencer that is turned off will still step, it will just not output any notes.</li>
    <li><strong>Kill Notes</strong> &#8211; When this toggle is activated, only one note will play at a time, the last note that the step sequencer saw as on.</li>
    <li><strong>Random </strong>- This button will generate a random pattern in the cell matrix. Clicking multiple times will turn on additional cells.</li>
    <li><strong>Clear </strong>- This button will clear all cells in the Cell Matrix</li>
    <li><strong>Columns </strong>- Change the number of columns in the cell matrix. The maximum number of columns is 32.</li>
    <li><strong>Rows </strong>- Change the number of rows in the cell matrix. The maximum number of rows is 32.</li>
    <li><strong>Octave </strong>- Change the starting octave value of the notes that are generated. Default is 3. Max is 8.</li>
    <li><strong>Range </strong>- Change the octave range when randomizing notes. Default and maximum are 3.</li>
    <li><strong>Velocity </strong>- The velocity of generated notes will reside randomly in the range represented by this slider. The slider has two ends so that notes can be created in any velocity range between 0 and 127.</li>
</ul>

<h3>Options Tab</h3>

<p><img class="size-full wp-image-564 alignleft" title="screenshot2" src="http://www.grantmuller.com/wp-content/uploads/screenshot2.gif" alt="screenshot2" width="193" height="248" />
The following options apply to all sequencers.</p>

<ul>
    <li><strong>Midi Sync Port</strong> &#8211; Which port to use for incoming sync. Has no effect if sync mode is set to &#8216;internal&#8217;</li>
    <li><strong>Sync Mode</strong> &#8211; Selects whether to use internal or external sync.</li>
</ul>

<h3 style="clear: both">Known Issues</h3>

<ul> Performance Problems &#8211; its a pretty hefty program, and I&#8217;m working on scaling down some of the loops to use fewer cycles. It gets a little better with each release.</ul>
