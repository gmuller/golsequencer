package golSequencer;

import javax.sound.midi.ShortMessage;

import rwmidi.SyncEvent;



/**
 * A metronome.
 * @author  Dave Briccetti. Modified by gmuller
 */
public class Metronome{

	private Thread thread; // New thread each time the metronome is turned on
	private final Runnable runnable = createRunnable();
	private long timeBetweenBeats;
	private boolean keepPlaying;
	private GOLSequencer golSequencer;
	private static ShortMessage msg = new ShortMessage();
	private static SyncEvent syncEvent;
	private boolean running;

	/** Creates new form Metronome */
	public Metronome(GOLSequencer golSequencer) {
		this.golSequencer = golSequencer;
		setTempo(120);
	}

	/**
	 * Sets the tempo. May be called while the metronome is on.
	 * @param beatsPerMinute the tempo, in beats per minute
	 */
	public void setTempo(int beatsPerMinute) {
		processTempoChange(beatsPerMinute);
	}

	/**
	 * Stops the metronome.
	 */
	public void stop() {
		keepPlaying = false;
		if (thread != null) {
			thread.interrupt(); // Interrupt the sleep
		}
	}

	private Runnable createRunnable() {
		return new Runnable() {

			public void run() {
				//final long startTime = System.currentTimeMillis();
				long wokeLateBy = 0;

				while (keepPlaying) {

					if (wokeLateBy > 10) {
						//log.debug("Woke late by " + wokeLateBy);
					} else {

						golSequencer.processEvents(syncEvent);
					}
					//final long currentTimeBeforeSleep = System.currentTimeMillis();
					//final long currentLag = (currentTimeBeforeSleep - startTime) % timeBetweenBeats;
					//final long sleepTime = timeBetweenBeats - currentLag;
					//final long expectedWakeTime = currentTimeBeforeSleep + sleepTime;
					try {
						Thread.sleep(timeBetweenBeats);
					} catch (InterruptedException ex) {
						//log.debug("Interrupted");
					}
					//wokeLateBy = System.currentTimeMillis() - expectedWakeTime;
					//channel.noteOff(noteForThisBeat);
				}
				//log.debug("Thread ending");
			}
		};
	}

	private void processTempoChange(int beatsPerMinute) {
		timeBetweenBeats = (1000 * 60 / beatsPerMinute) / (24);
		restartAtEndOfBeatIfRunning();
	}

	private void restartAtEndOfBeatIfRunning() {
		if (keepPlaying) {
			keepPlaying = false;
			try {
				thread.join();
			} catch (InterruptedException ex) {
				//log.debug(ex);
			}
			startThread();
		}
	}

	public void startThread() {
		try{
			msg.setMessage(ShortMessage.TIMING_CLOCK);
			syncEvent = new SyncEvent(msg);
		} catch (Exception e){

		}
		keepPlaying = true;
		thread = new Thread(runnable, "Metronome");
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
