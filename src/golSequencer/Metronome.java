package golSequencer;

import javax.sound.midi.ShortMessage;

import rwmidi.SyncEvent;



/**
 * A metronome.
 * @author  Dave Briccetti. Heavily modified by gmuller
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
		System.out.println(timeBetweenBeats);
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
				double now;
				double previousTime = 0;
				double lag;
				
				while (keepPlaying) {
					now = System.nanoTime() * 1.0e-6;
					golSequencer.processEvents(syncEvent);
					try {
						Thread.sleep(timeBetweenBeats);
					} catch (InterruptedException ex) {
						System.out.println("Timing mechanism failed delay");
					}
					lag = (now - previousTime) - timeBetweenBeats;
					if (lag > 2) System.out.println(lag);
					previousTime = now;
				}
			}
		};
	}

	private void processTempoChange(int beatsPerMinute) {
		timeBetweenBeats = (long)((1000.0 * 60 / beatsPerMinute) / (24));
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
