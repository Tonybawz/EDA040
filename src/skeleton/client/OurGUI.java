package skeleton.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import skeleton.server.Server;

//import com.controller.Controller;

public class OurGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane, imagePanel;
	public JTextArea inputTextArea;
	public JTextArea outputTextArea;
	private JLabel modePanel;
	private JButton inputBtn;
	private JButton outputBtn;
	private JButton sortBtn;
	public JRadioButton movieModeButton;
	public JRadioButton idleModeButton;
	public JRadioButton autoModeButton;
	public JRadioButton synchronusOnModeButton;
	public JRadioButton synchronusOffModeButton;
	public JRadioButton synchronusAutoModeButton;
	private ImagePanel leftPanel;
	private ImagePanel rightPanel;
	private JLabel leftText, rightText, cameraIndex;
	private Client client;
	private boolean connectionCamera1 = false;
	private boolean connectionCamera2 = false;
	private ImageIO imageIO;

	/*
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { GUIshell frame = new GUIshell(); }
	 * catch (Exception e) { e.printStackTrace(); } } }); }
	 */

	/**
	 * Create the frame.
	 */
	public OurGUI(final Client client) {
		// controller = new Controller(this);
		this.client = client;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		imagePanel = new JPanel(new BorderLayout());
		BorderLayout layout = new BorderLayout(0, 0);
		contentPane.setLayout(layout);
		leftText = new JLabel();
		rightText = new JLabel();
		
		leftText.setVisible(false);
		rightText.setVisible(false);

		inputTextArea = new JTextArea();
		outputTextArea = new JTextArea();
		leftPanel = new ImagePanel();
		rightPanel = new ImagePanel();
		
		imagePanel.add(leftPanel, BorderLayout.WEST);
		imagePanel.add(rightPanel, BorderLayout.EAST);
		contentPane.add(imagePanel, BorderLayout.CENTER);

		inputBtn = new JButton("Connect/Disconnect first camera");
		// connecta första kameran
		inputBtn.addActionListener(new ActionListener() {
			Socket sock = null;

			public void actionPerformed(ActionEvent e) {
			if (connectionCamera1){
				client.disconnect(sock, 1);
				connectionCamera1 = false;
			}
			else {
			try {
				sock = client.connect("localhost", 6077);
				connectionCamera1 = true;
			} catch (Throwable e2) {
				e2.printStackTrace();
			}}
			leftText.setVisible(true);
			}
		});

		outputBtn = new JButton("Connect/Disconnect second camera");
		// connecta andra kameran
		outputBtn.addActionListener(new ActionListener() {
			Socket sock = null;
			public void actionPerformed(ActionEvent e) {
			if (connectionCamera2){
				client.disconnect(sock, 0);
				connectionCamera2 = false;
			}
			else {
			try {
				sock = client.connect("localhost", 6078);
				connectionCamera2 = true;
			} catch (Throwable e2) {
				e2.printStackTrace();
			}}
				rightText.setVisible(true);
			}
		});
		JPanel tmpPanel1 = new JPanel();
		tmpPanel1.add(inputBtn);
		JPanel tmpPanel2 = new JPanel();
		tmpPanel2.add(outputBtn);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1, 6));
		topPanel.add(leftText);
		topPanel.add(tmpPanel1);
		topPanel.add(tmpPanel2);
		topPanel.add(rightText);
		contentPane.add(topPanel, BorderLayout.NORTH);
		modePanel = new JLabel();
		topPanel.add(modePanel);		
		cameraIndex = new JLabel();
		topPanel.add(cameraIndex);
		
		synchronusOnModeButton = new JRadioButton("SynchronusMode On");
		synchronusAutoModeButton = new JRadioButton("SynchronusMode Auto");
		synchronusAutoModeButton.setSelected(true);
		synchronusOffModeButton = new JRadioButton("SynchronusMode Off");
		movieModeButton = new JRadioButton("MovieMode");
		idleModeButton = new JRadioButton("IdleMode");
		idleModeButton.setSelected(true);
		autoModeButton = new JRadioButton("AutoMode");

		ButtonGroup group = new ButtonGroup();
		ButtonGroup syncGroup = new ButtonGroup();
		
		syncGroup.add(synchronusOnModeButton);
		syncGroup.add(synchronusAutoModeButton);
		syncGroup.add(synchronusOffModeButton);
		group.add(movieModeButton);
		group.add(idleModeButton);
		group.add(autoModeButton);
		
		synchronusOnModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getBuffer().setGuiSync(1);
			}
		});
		synchronusAutoModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getBuffer().setGuiSync(2);
			}
		});
		synchronusOffModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getBuffer().setGuiSync(0);
			}
		});
		movieModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getBuffer().setGuiMode(1);
			}
		});
		idleModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getBuffer().setGuiMode(0);
			}
		})
		;autoModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getBuffer().setGuiMode(2);
			}
		});

		sortBtn = new JButton("Exit");

		sortBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// add all to bottomPanel

		
				
		JPanel bottomPanel = new JPanel(new FlowLayout());
		bottomPanel.add(synchronusOnModeButton);
		bottomPanel.add(synchronusAutoModeButton);
		bottomPanel.add(synchronusOffModeButton);
		bottomPanel.add(movieModeButton);
		bottomPanel.add(movieModeButton);
		bottomPanel.add(idleModeButton);
		bottomPanel.add(autoModeButton);
		bottomPanel.add(sortBtn);
		contentPane.add(bottomPanel, BorderLayout.SOUTH);

		setContentPane(contentPane);
		setTitle("Realtidsprojekt Anthony, Erik, Carl, Kevin");
		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		setVisible(true);
	}

	public void putImage(OurImage imageSkal) {
		switch(client.getBuffer().getSync()){
		
		case 2:{
			modePanel.setText("Sync: Auto");
			break;
		}
		case 0:{
			modePanel.setText("Sync: Off");
			break;
		}
		case 1:{
			modePanel.setText("Sync: On");
			break;
		}default:{
			break;
		}
		}
		if (imageSkal.getID() == 0) {
			leftPanel.refresh(imageSkal.getImage());
			leftText.setText("Delay " + String.valueOf(imageSkal.getDelay()));
			
		} else if (imageSkal.getID() == 1) {
			rightPanel.refresh(imageSkal.getImage());
			rightText.setText("Delay " + String.valueOf(imageSkal.getDelay()));
		}
		if(client.getBuffer().getLastMotionIndex() == 0){
		cameraIndex.setText("Detected motion: First camera");
		} else if (client.getBuffer().getLastMotionIndex() == 1){
			cameraIndex.setText("Detected motion: Second Camera");
		}
		cameraIndex.setVisible(true);
	}

	class ImagePanel extends JPanel {
		ImageIcon icon;

		
		public ImagePanel() {
			super();
			icon = new ImageIcon();
			JLabel label = new JLabel(icon);
			label.setPreferredSize(new Dimension(640, 480));
			pack();
			add(label, BorderLayout.CENTER);
		}

		public void refresh(byte[] data) {
			Image theImage = getToolkit().createImage(data);
			getToolkit().prepareImage(theImage, -1, -1, null);
			icon.setImage(theImage);
			icon.paintIcon(this, this.getGraphics(), 0, 0);
		
		}
	}
}
