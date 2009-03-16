/* 
 * jVSTwRapper - The Java way into VST world!
 * 
 * jVSTwRapper is an easy and reliable Java Wrapper for the Steinberg VST interface. 
 * It enables you to develop VST 2.3 compatible audio plugins and virtual instruments 
 * plus user interfaces with the Java Programming Language. 3 Demo Plugins(+src) are included!
 * 
 * Copyright (C) 2006  Daniel Martin [daniel309@users.sourceforge.net] 
 * 					   and many others, see CREDITS.txt
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package golSequencerVST;


import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jvst.wrapper.VSTPluginAdapter;
import jvst.wrapper.VSTPluginGUIAdapter;
import jvst.wrapper.gui.VSTPluginGUIRunner;
import processing.core.PApplet;


public class GOLSequencerVSTGUI extends VSTPluginGUIAdapter implements ChangeListener {
	private static final long serialVersionUID = 7004230677068347485L;

	private JSlider VolumeSlider;
	private JSlider Freg1Slider;
	private JSlider Freg2Slider;
	private JSlider Level1Slider;
	private JSlider Level2Slider;

	private JRadioButton SawRadio1;
	private JRadioButton SawRadio2;
	private JRadioButton PulseRadio1;
	private JRadioButton PulseRadio2;

	private JTextField VolumeText;
	private JTextField Freg1Text;
	private JTextField Freg2Text;
	private JTextField Level1Text;
	private JTextField Level2Text;

	private VSTPluginAdapter pPlugin;

	protected static boolean DEBUG = false;


	public GOLSequencerVSTGUI(VSTPluginGUIRunner r, VSTPluginAdapter plug) {
		super(r,plug);
		log("JayVSTxSynthGUI <init>");

		this.setTitle("JayVSTxSynth v0.7");
		this.setSize(350, 240);
		this.setResizable(false);

		this.pPlugin=plug;
		try {
			this.init();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this is needed on the mac only, 
		//java guis are handled there in a pretty different way than on win/linux
		//XXX
		if (RUNNING_MAC_X) this.show();
	}

	public void setupFrame() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		
		 //Get the System Classloader
        ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

        //Get the URLs
        URL[] urls = ((URLClassLoader)sysClassLoader).getURLs();

        for(int i=0; i< urls.length; i++)
        {
            log(urls[i].getFile());
        }       
		
		
		
		
		Class c = Class.forName("golSequencerVST.PGOLSequencer");
		//Class<?> c = Thread.currentThread().getContextClassLoader().loadClass("golSequencerVST.PGOLSequencer");
		final PApplet applet = (PApplet) c.newInstance();

		// these are needed before init/start
		log("There are " + this.getFrames().length + " frames");
		applet.frame = this.getFrames()[0];
		
		applet.frame.setUndecorated(true);
		applet.init();
		applet.frame.add(applet);
		while (applet.defaultSize && !applet.finished) {
			//System.out.println("default size");
			try {
				Thread.sleep(5);

			} catch (InterruptedException e) {
				//System.out.println("interrupt");
			}
		}
		//println("not default size " + applet.width + " " + applet.height);
		//println("  (g width/height is " + applet.g.width + "x" + applet.g.height + ")");

		// if not presenting
		// can't do pack earlier cuz present mode don't like it
		// (can't go full screen with a frame after calling pack)
		//	        frame.pack();  // get insets. get more.
		Insets insets = applet.frame.getInsets();

		int windowW = Math.max(applet.width, 128) +
		insets.left + insets.right;
		int windowH = Math.max(applet.height, 128) +
		insets.top + insets.bottom;

		applet.frame.setSize(windowW, windowH);


		applet.frame.setLocation((applet.screen.width - applet.width) / 2,
				(applet.screen.height - applet.height) / 2);

		//	        frame.setLayout(null);
		//	        frame.add(applet);


		applet.frame.setBackground(Color.black);

		int usableWindowH = windowH - insets.top - insets.bottom;
		applet.setBounds((windowW - applet.width)/2,
				insets.top + (usableWindowH - applet.height)/2,
				applet.width, applet.height);



		// handle frame resizing events
		applet.setupFrameResizeListener();

		// all set for rockin
		if (applet.displayable()) {
			applet.frame.setVisible(true);
		}


		applet.requestFocus(); // ask for keydowns
		//System.out.println("exiting main()");
	}
	public void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		setupFrame();
		if (!DEBUG) {
			this.VolumeSlider = new JSlider(JSlider.VERTICAL, 1, 100, (int)(this.pPlugin.getParameter(JayVSTxSynthProgram.PARAM_ID_VOLUME) * 100F));
			this.Freg1Slider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int)(this.pPlugin.getParameter(JayVSTxSynthProgram.PARAM_ID_FREQ1) * 100F));
			this.Freg2Slider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int)(this.pPlugin.getParameter(JayVSTxSynthProgram.PARAM_ID_FREQ2) * 100F));
			this.Level1Slider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int)(this.pPlugin.getParameter(JayVSTxSynthProgram.PARAM_ID_VOLUME1) * 100F));
			this.Level2Slider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int)(this.pPlugin.getParameter(JayVSTxSynthProgram.PARAM_ID_VOLUME2) * 100F));
		}
		else {

			this.VolumeSlider = new JSlider(JSlider.VERTICAL, 1, 100, 1);
			this.Freg1Slider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
			this.Freg2Slider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
			this.Level1Slider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
			this.Level2Slider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
		}

		this.VolumeSlider.addChangeListener(this);
		this.Freg1Slider.addChangeListener(this);
		this.Freg2Slider.addChangeListener(this);
		this.Level1Slider.addChangeListener(this);
		this.Level2Slider.addChangeListener(this);

		this.SawRadio1 = new JRadioButton("Saw", true);
		this.SawRadio2 = new JRadioButton("Saw");
		this.PulseRadio1 = new JRadioButton("Pulse");
		this.PulseRadio2 = new JRadioButton("Pulse", true);
		this.SawRadio1.addChangeListener(this);
		this.SawRadio2.addChangeListener(this);
		this.PulseRadio1.addChangeListener(this);
		this.PulseRadio2.addChangeListener(this);
		ButtonGroup Wave1Group = new ButtonGroup();
		ButtonGroup Wave2Group = new ButtonGroup();
		Wave1Group.add(this.SawRadio1);
		Wave1Group.add(this.PulseRadio1);
		Wave2Group.add(this.SawRadio2);
		Wave2Group.add(this.PulseRadio2);

		if (!DEBUG) {
			this.VolumeText = new JTextField((int)(this.pPlugin.getParameter(JayVSTxSynthProgram.PARAM_ID_VOLUME) * 100F) + "%");
			this.Freg1Text = new JTextField(this.pPlugin.getParameterDisplay(JayVSTxSynthProgram.PARAM_ID_FREQ1) + " " + this.pPlugin.getParameterLabel(JayVSTxSynthProgram.PARAM_ID_FREQ1));
			this.Freg2Text = new JTextField(this.pPlugin.getParameterDisplay(JayVSTxSynthProgram.PARAM_ID_FREQ2) + " " + this.pPlugin.getParameterLabel(JayVSTxSynthProgram.PARAM_ID_FREQ2));
			this.Level1Text = new JTextField(this.pPlugin.getParameterDisplay(JayVSTxSynthProgram.PARAM_ID_VOLUME1) + " " + this.pPlugin.getParameterLabel(JayVSTxSynthProgram.PARAM_ID_VOLUME1));
			this.Level2Text = new JTextField(this.pPlugin.getParameterDisplay(JayVSTxSynthProgram.PARAM_ID_VOLUME2) + " " + this.pPlugin.getParameterLabel(JayVSTxSynthProgram.PARAM_ID_VOLUME2));
		}
		else {
			this.VolumeText = new JTextField((1 * 100F) + "%");
			this.Freg1Text = new JTextField("234 Hz");
			this.Freg2Text = new JTextField("2345 Hz");
			this.Level1Text = new JTextField("324 db");
			this.Level2Text = new JTextField("345 db");
		}


		this.VolumeText.setEditable(false);
		this.Freg1Text.setEditable(false);
		this.Freg2Text.setEditable(false);
		this.Level1Text.setEditable(false);
		this.Level2Text.setEditable(false);

		JLabel VolumeLabel = new JLabel("Volume");
		JLabel Freg1Label = new JLabel("Freqency");
		JLabel Freg2Label = new JLabel("Freqency");
		JLabel Level1Label = new JLabel("Volume");
		JLabel Level2Label = new JLabel("Volume");

		GridLayout grids = new GridLayout(1, 3);
		this.getContentPane().setLayout(grids);

		Box VolumeBox = new Box(BoxLayout.Y_AXIS);
		Box Osc1box = new Box(BoxLayout.Y_AXIS);
		Box Osc2box = new Box(BoxLayout.Y_AXIS);


		VolumeBox.add(VolumeLabel);
		VolumeBox.add(this.VolumeSlider);
		VolumeBox.add(this.VolumeText);
		VolumeBox.setBorder(BorderFactory.createTitledBorder("Amp"));

		Osc1box.add(this.SawRadio1);
		Osc1box.add(this.PulseRadio1);
		Osc1box.add(Box.createVerticalStrut(15));
		Osc1box.add(Freg1Label);
		Osc1box.add(this.Freg1Slider);
		Osc1box.add(this.Freg1Text);
		Osc1box.add(Box.createVerticalStrut(15));
		Osc1box.add(Level1Label);
		Osc1box.add(this.Level1Slider);
		Osc1box.add(this.Level1Text);
		Osc1box.setBorder(BorderFactory.createTitledBorder("Osc1"));

		Osc2box.add(this.SawRadio2);
		Osc2box.add(this.PulseRadio2);
		Osc2box.add(Box.createVerticalStrut(15));
		Osc2box.add(Freg2Label);
		Osc2box.add(this.Freg2Slider);
		Osc2box.add(this.Freg2Text);
		Osc2box.add(Box.createVerticalStrut(15));
		Osc2box.add(Level2Label);
		Osc2box.add(this.Level2Slider);
		Osc2box.add(this.Level2Text);
		Osc2box.setBorder(BorderFactory.createTitledBorder("Osc2"));

		this.getContentPane().add(VolumeBox);
		this.getContentPane().add(Osc1box);
		this.getContentPane().add(Osc2box);
	}


	public void stateChanged(ChangeEvent e) {
		Object o = e.getSource();
		if (!DEBUG) {
			if (o.equals(this.VolumeSlider)) {
				JSlider s = (JSlider)o;
				this.pPlugin.setParameter(JayVSTxSynthProgram.PARAM_ID_VOLUME, (float)((float)s.getValue() / 100F));
				this.VolumeText.setText((int)(this.pPlugin.getParameter(JayVSTxSynthProgram.PARAM_ID_VOLUME) * 100F) + "%");
			}
			else if (o.equals(this.Freg1Slider)) {
				JSlider s = (JSlider)o;
				this.pPlugin.setParameter(JayVSTxSynthProgram.PARAM_ID_FREQ1, (float)((float)s.getValue() / 100F));
				this.Freg1Text.setText(this.pPlugin.getParameterDisplay(JayVSTxSynthProgram.PARAM_ID_FREQ1) + " " + this.pPlugin.getParameterLabel(JayVSTxSynthProgram.PARAM_ID_FREQ1));
			}
			else if (o.equals(this.Freg2Slider)) {
				JSlider s = (JSlider)o;
				this.pPlugin.setParameter(JayVSTxSynthProgram.PARAM_ID_FREQ2, (float)((float)s.getValue() / 100F));
				this.Freg2Text.setText(this.pPlugin.getParameterDisplay(JayVSTxSynthProgram.PARAM_ID_FREQ2) + " " + this.pPlugin.getParameterLabel(JayVSTxSynthProgram.PARAM_ID_FREQ2));
			}
			else if (o.equals(this.Level1Slider)) {
				JSlider s = (JSlider)o;
				this.pPlugin.setParameter(JayVSTxSynthProgram.PARAM_ID_VOLUME1, (float)((float)s.getValue() / 100F));
				this.Level1Text.setText(this.pPlugin.getParameterDisplay(JayVSTxSynthProgram.PARAM_ID_VOLUME1) + " " + this.pPlugin.getParameterLabel(JayVSTxSynthProgram.PARAM_ID_VOLUME1));
			}
			else if (o.equals(this.Level2Slider)) {
				JSlider s = (JSlider)o;
				this.pPlugin.setParameter(JayVSTxSynthProgram.PARAM_ID_VOLUME2, (float)((float)s.getValue() / 100F));
				this.Level2Text.setText(this.pPlugin.getParameterDisplay(JayVSTxSynthProgram.PARAM_ID_VOLUME2) + " " + this.pPlugin.getParameterLabel(JayVSTxSynthProgram.PARAM_ID_VOLUME2));
			}
			else if (o.equals(this.PulseRadio1)) {
				JRadioButton r = (JRadioButton)o;
				if (r.isEnabled()) this.pPlugin.setParameter(JayVSTxSynthProgram.PARAM_ID_WAVEFORM1, 0.9F);
			}
			else if (o.equals(this.PulseRadio2)) {
				JRadioButton r = (JRadioButton)o;
				if (r.isEnabled()) this.pPlugin.setParameter(JayVSTxSynthProgram.PARAM_ID_WAVEFORM2, 0.9F);
			}
			else if (o.equals(this.SawRadio1)) {
				JRadioButton r = (JRadioButton)o;
				if (r.isEnabled()) this.pPlugin.setParameter(JayVSTxSynthProgram.PARAM_ID_WAVEFORM1, 0.1F);
			}
			else if (o.equals(this.SawRadio2)) {
				JRadioButton r = (JRadioButton)o;
				if (r.isEnabled()) this.pPlugin.setParameter(JayVSTxSynthProgram.PARAM_ID_WAVEFORM2, 0.1F);
			}
		}
	}


	//for testing purpose only!
//	public static void main(String[] args) throws Exception {
//		DEBUG=true;
//
//		GOLSequencerVSTGUI gui = new GOLSequencerVSTGUI(null,null);
//		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	}

	       

}
