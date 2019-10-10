import com.fazecast.jSerialComm.SerialPort;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.Color;
import java.awt.TextField;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

public class LED {
	
	static SerialPort chosenPort;
	public static int[] color={0,0,0};
	public static boolean send=false;
	public static boolean off=false;
	public static boolean update=false;
	public static boolean init=true;
	public static Color hex=new Color(0,0,0);
	private static String toArdu(int r,int g, int b){
	
		String Red="R"+Integer.toString(r);
		String Green="G"+Integer.toString(g);
		String Blue="B"+Integer.toString(b);

		
		return Red+Green+Blue;
	}
	
	
	
	public static void main(String[] args) {
		
		// create and configure the window
		JFrame window = new JFrame();
		window.setResizable(false);
		window.setTitle("Arduino");
		window.setSize(346, 316);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(null);
		
		// create a drop-down box and connect button, then place them at the top of the window

		final JComboBox<String> portList = new JComboBox<String>();
		final JButton connectButton = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.setBounds(0, 0, 378, 39);
		topPanel.add(portList);
		topPanel.add(connectButton);
		window.getContentPane().add(topPanel);
		
		final JSlider Red = new JSlider();
		Red.setBackground(Color.RED);
		Red.setMaximum(255);
		Red.setValue(0);
		Red.setBounds(81, 79, 159, 26);
		window.getContentPane().add(Red);
		
		final JSlider Green = new JSlider();
		Green.setBackground(Color.GREEN);
		Green.setMaximum(255);
		Green.setValue(0);
		Green.setBounds(81, 121, 159, 26);
		window.getContentPane().add(Green);
		
		final JSlider Blue = new JSlider();
		Blue.setToolTipText("Blue");
		Blue.setForeground(Color.LIGHT_GRAY);
		Blue.setBackground(Color.BLUE);
		Blue.setMaximum(255);
		Blue.setValue(0);
		Blue.setBounds(81, 159, 159, 26);
		window.getContentPane().add(Blue);
		
		JButton btnNewButton = new JButton("Enter Color Code");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String temp=JOptionPane.showInputDialog("Enter a HEX color");
				if(temp!=null){
					if(!temp.equals("")){
						hex=Color.decode(temp);
						send=true;
						}
					}
				}
			}
		);
		btnNewButton.setBackground(Color.GRAY);
		btnNewButton.setBounds(81, 201, 159, 29);
		window.getContentPane().add(btnNewButton);
		
		final JButton btnOff = new JButton("Off");
		btnOff.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			if(!off){
				off=true;
				btnOff.setText("On");}
			else{
				btnOff.setText("Off");
				Red.setValue(color[0]);
				Green.setValue(color[1]);
				Blue.setValue(color[2]);
				off=false;
				update=true;
			}
			}
		});
		btnOff.setBounds(102, 246, 115, 29);
		window.getContentPane().add(btnOff);
		
		final JLabel Rlabel = new JLabel("");
		Rlabel.setBounds(255, 85, 69, 20);
		window.getContentPane().add(Rlabel);
		
		final JLabel Glabel = new JLabel("");
		Glabel.setBounds(255, 121, 69, 20);
		window.getContentPane().add(Glabel);
		
		final JLabel Blabel = new JLabel("");
		Blabel.setEnabled(false);
		Blabel.setBounds(255, 165, 69, 20);
		window.getContentPane().add(Blabel);
		
		// populate the drop-down box
		SerialPort[] portNames = SerialPort.getCommPorts();

		for(int i = 0; i < portNames.length; i++)
			portList.addItem(portNames[i].getSystemPortName());
		
		// configure the connect button and use another thread to send data
		connectButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				if(connectButton.getText().equals("Connect")) {
					// attempt to connect to the serial port
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					if(chosenPort.openPort()) {
						connectButton.setText("Disconnect");
						portList.setEnabled(false);
						
						// create a new thread for sending data to the arduino
						Thread thread = new Thread(){
							@Override public void run() {
								// wait after connecting, so the bootloader can finish
								try {Thread.sleep(100); } catch(Exception e) {}

								// enter an infinite loop that sends text to the arduino
								PrintWriter output = new PrintWriter(chosenPort.getOutputStream());
								


								while(true) {
									int R = 0,G = 0,B = 0;
									
									
									
									if(!off){
										R=Red.getValue();
										G=Green.getValue();
										B=Blue.getValue();
										Rlabel.setText(Integer.toString(R));
										Glabel.setText(Integer.toString(G));
										Blabel.setText(Integer.toString(B));
									}
									if ((R!=color[0]||G!=color[1]||B!=color[2])&!off){
										color[0]=R;
										color[1]=G;
										color[2]=B;
										update=true;
									}
									
									if(send==true){  //hex color
										send=false;
										if(!off){	
									    Red.setValue(hex.getRed());
									    Green.setValue(hex.getGreen());
									    Blue.setValue(hex.getBlue());
										}}
									
									if(off){
									    

										    if(Red.getValue()+Green.getValue()+Blue.getValue()!=0){
										    update=true;
										    }
										    Red.setValue(0);
										    Green.setValue(0);
										    Blue.setValue(0);
									    
									}


									if(update){
									output.print(toArdu(R,G,B));
									output.flush();
									update=false;}
				
									try {Thread.sleep(100); } catch(Exception e) {}
								}
							}
						};
						thread.start();
					}
				} else {
					// disconnect from the serial port
					chosenPort.closePort();
					portList.setEnabled(true);
					connectButton.setText("Connect");
				    Red.setValue(0);
				    Green.setValue(0);
				    Blue.setValue(0);
				}
			}
		});
		
		// show the window
		window.setVisible(true);
	}
}
