
/************************************************************************************************************
 * NEED TO ADD JARS FOR LIBRARIES FOR TEXTING SERVICE, CHART, AND SERIAL CABLE
 * All Jars needed in JarLibrary Folder
 * Right click project --> Build Path --> Configure Build Path --> Add External JARs
 *************************************************************************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

import com.fazecast.jSerialComm.SerialPort;

//TEXT MESSAGE IMPORTS
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import gnu.io.*; //RXTX
//RXTX
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
//import gnu.io.SerialPort;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * http://stackoverflow.com/questions/7231824/setting-range-for-x-y-axis-jfreechart
 * */

public class SensorGraph {
	
	public static final String ACCOUNT_SID = "AC2d94e3ab9715a2de6a58fead2c2861fe";
	public static final String AUTH_TOKEN = "17ae461a1db9348ab8fcfe938af86111";

	static SerialPort chosenPort;
	static int x = 0;

	public static void main(String[] args) {

		// Create and configure the window
		JFrame window = new JFrame();
		window.setTitle("Temperature Readings");
		// window.setSize(900, 900);
		window.setMinimumSize(new Dimension(1200, 600));
		window.setExtendedState(window.MAXIMIZED_BOTH);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create a drop-down box and connect button, then place them at the top
		// of the window
		
		JComboBox<String> portList = new JComboBox<String>();
		JButton connectButton = new JButton("Connect");
		JButton virtualButton = new JButton("LIGHT LEDs");
		virtualButton.setOpaque(true);
	    virtualButton.setForeground(Color.RED);
		JLabel enterPhone = new JLabel("10-digit phone Number:");
		JTextField phoneNumberTextField = new JTextField(20);
		JLabel upperBound = new JLabel("lower bound:");
		JTextField upperBoundField = new JTextField(3);
		JLabel lowerBound = new JLabel("upper bound:");
		JTextField lowerBoundField = new JTextField(3);
		JPanel topPanel = new JPanel();
		topPanel.add(enterPhone);
		topPanel.add(phoneNumberTextField);
		topPanel.add(portList);
		topPanel.add(connectButton);
		topPanel.add(virtualButton);
		topPanel.add(lowerBound);
		topPanel.add(lowerBoundField);
		topPanel.add(upperBound);
		topPanel.add(upperBoundField);
		window.add(topPanel, BorderLayout.NORTH);
		JPanel bottomPanel = new JPanel();
		
		JLabel realTimeTempLabel = new JLabel("Real Time Temperature:");
		JLabel realTimeTemp = new JLabel("24 ");
		JLabel realTimeTempUnits = new JLabel("Degrees Celsius");
		bottomPanel.add(realTimeTempLabel);
		bottomPanel.add(realTimeTemp);
		bottomPanel.add(realTimeTempUnits);
		bottomPanel.setPreferredSize(new Dimension(100, 50));
		
		Font f = new Font ("Arial", Font.BOLD, 26);
		realTimeTempLabel.setFont(f);
		realTimeTemp.setFont(f);
		realTimeTempUnits.setFont(f);
		window.add(bottomPanel, BorderLayout.SOUTH);

		// Populate the drop-down box
		SerialPort[] portNames = SerialPort.getCommPorts();
		SerialPort leftUSBPort = SerialPort.getCommPort("/dev/cu.usbmodem1421");
		SerialPort rightUSBPort = SerialPort.getCommPort("/dev/cu.usbmodem1411");
		// SerialPort[0]= myPort.getCommPort("/dev/cu.usbmodem1421");
		for (int i = 0; i < portNames.length; i++) {
			System.out.println("Port Name: " + portNames[i]);
			System.out.println("Port Name: " + portNames[i].getSystemPortName());
		}
		/*
		for (int i = 0; i < portNames.length; i++) {
			portList.addItem(portNames[i].getSystemPortName());
		}
		*/
		portList.addItem(leftUSBPort.getSystemPortName());
		portList.addItem(rightUSBPort.getSystemPortName());
		/*------------------------------------------------------------------------------------------------
		 * 		CREATE GRAPH
		 *------------------------------------------------------------------------------------------------*/

		XYSeries series = new XYSeries("Temperature Readings");
		// series.setMaximumItemCount(300);
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		JFreeChart chart = ChartFactory.createXYLineChart("Temperature", "Time (seconds)", "Degrees (C)", dataset);

		XYPlot xyPlot = (XYPlot) chart.getPlot();
		xyPlot.setDomainCrosshairVisible(true);
		xyPlot.setRangeCrosshairVisible(true);
		XYItemRenderer renderer = xyPlot.getRenderer();
		renderer.setSeriesPaint(0, Color.black);
		NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
		domain.setRange(0, 300);
		domain.setTickUnit(new NumberTickUnit(20.0));
		domain.setVerticalTickLabels(true);
		domain.setTickLabelsVisible(false);
		NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
		range.setRange(-25, 75);
		range.setRange(5, 75);
		range.setTickUnit(new NumberTickUnit(10));
		domain.setLabel("Time Elapsed (s)");

		window.add(new ChartPanel(chart), BorderLayout.CENTER);

		/*------------------------------------------------------------------------------------------------
		 * 		INCOMING DATA TO DRAW GRAPH
		 *------------------------------------------------------------------------------------------------*/
		// Configure the connect button and use another thread to listen for data
		connectButton.addActionListener(new ActionListener() {
			int temperatureReading;
			int tempExtremeCount = 0;
			int tempHotCount = 0;
			int tempColdCount = 0;
			String recipientNumber="";
			int upperRangeInt= 63;
			int lowerRangeInt= -10;

			int[] temperatureList = new int[300];

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (connectButton.getText().equals("Connect")) {
					recipientNumber= phoneNumberTextField.getText();
					
					String lowerRange= lowerBoundField.getText();
					String upperRange= upperBoundField.getText();
					
					System.out.println("Recipient Number: " + recipientNumber);
					System.out.println("lower range: " + lowerRange);
					System.out.println("upper range: " + upperRange);
					
					if(!lowerRange.equals("") && !upperRange.equals("")){
						lowerRangeInt = Integer.parseInt(lowerRange);
						upperRangeInt = Integer.parseInt(upperRange);
						range.setRange(lowerRangeInt, upperRangeInt);
						window.repaint();
					}
					
					// attempt to connect to the serial port
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					if (chosenPort.openPort()) {
						connectButton.setText("Disconnect");
						portList.setEnabled(false);
					}
					 
					Thread thread = new Thread() {
						@Override
						public void run() {
							Scanner scanner = new Scanner(chosenPort.getInputStream());
							OutputStream output = chosenPort.getOutputStream();
							int tempCount = 0;
					
							while (scanner.hasNextLine()) {
								try {
									String line = scanner.nextLine();
									realTimeTemp.setText(line);
									bottomPanel.repaint();
									
									int temperatureReading = Integer.parseInt(line);
									
									
									//if (temperatureReading > 63 && temperatureReading != 1000) {
									if (temperatureReading > upperRangeInt && temperatureReading != 1000) {
										sendTextMessage(temperatureReading, tempHotCount, recipientNumber);
										tempHotCount++;
									} 
									//else if (temperatureReading < -10) {
									else if (temperatureReading < lowerRangeInt) {
										sendTextMessage(temperatureReading, tempColdCount, recipientNumber);
										tempColdCount++;
									} 
									else if(temperatureReading == 1000){
										chart.setTitle("UNPLUGGED SENSOR");
										chart.setBackgroundPaint(Color.red);
									}
									else if(temperatureReading == 2000){
										chart.setTitle("NO DATA AVAILABLE");
										chart.setBackgroundPaint(Color.red);
									}
									else{
										chart.setTitle("TEMPERATURE READINGS");
										chart.setBackgroundPaint(Color.white);
									}

									if (tempCount < 300) {
										temperatureList[tempCount] = temperatureReading;
									}

									else {
										for (int j = 0; j < 299; j++) {
											temperatureList[j] = temperatureList[j + 1];
										}
										temperatureList[299] = temperatureReading;
									}
									System.out.println(temperatureList[0]);

									// Code to check if text should be sent for
									// too high or too low of a temperature
									if (temperatureReading > 63 || temperatureReading < -10) {
										tempExtremeCount++;
									}

									if (tempCount < 300) {
										series.add(x++, temperatureReading);
									} else {
										series.clear();
										for (int k = 0; k < temperatureList.length; k++) {
											series.add(k, temperatureList[k]);
										}
									}

									// NOTE: If sleep time is too fast, processor can't keep up with
									// clearing and repainting the graph and exceptions occur
									try {
										Thread.sleep(500);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									window.repaint();
									tempCount++;
									
									virtualButton.addActionListener(new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent e) {
											//send 0 to arduino for: PC ready
											//send 1 to arduino for: push third box button 
											//send 2 to arduino for: release third box button
											//send 3 to arduino for: PC no longer ready
											try {
												output.write(0);
												output.write(1);
												output.write(2);
											} catch (IOException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
	
										}
										
									});
								} catch (Exception e) {
									
									
								}
							}
						} //END WHILE LOOP
					};
					
					thread.start();

				} else {
					// disconnect from the serial port
					chosenPort.closePort();
					portList.setEnabled(true);
					connectButton.setText("Connect");
					series.clear();
					x = 0;
				}
				

			}
		});

		// show the window
		window.setVisible(true);
	}

	/*------------------------------------------------------------------------------------------------
	 * 		RANDOM NUMBER METHOD
	 * ------------------------------------------------------------------------------------------------*/
	public static int randInt() {
		int max = 63;
		int min = -10;
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	/*------------------------------------------------------------------------------------------------
	 * 		SEND TEXT METHOD
	 * ------------------------------------------------------------------------------------------------*/
	public static void sendTextMessage(int temperature, int textCount, String recipientNumber) {
		recipientNumber= "+" + recipientNumber;
		String temperatureString = Integer.toString(temperature);
		TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
		String temperatureTooLow = "Our sensor is picking up some frigid temperatures! The current reading is "
				+ temperature + " degrees Celsius";
		String temperatureTooHot = "Our sensor is picking up some flamin temperatures! The current reading is "
				+ temperature + " degrees Celsius";

		// SEND TEXT MESSAGE TO xxx-xxx-xxxx
		//String joe = "+15153714907";
		String defaultNumber = "+15154025546"; //ARMOND CELL

		// Build a filter for the MessageList
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// HOT OR COLD MESSAGE SENT
		if (temperature < -10 && textCount < 15) {
			params.add(new BasicNameValuePair("Body", temperatureTooLow));
		} else if (temperature > 63 && textCount < 15) {
			params.add(new BasicNameValuePair("Body", temperatureTooHot));
		}

		// FROM SERVICE
		params.add(new BasicNameValuePair("From", "+15153032132"));

		// TO RECEIVER
		//params.add(new BasicNameValuePair("To", armond));
		if(recipientNumber.length() == 11){
			params.add(new BasicNameValuePair("To", recipientNumber));
		}
		else{
			params.add(new BasicNameValuePair("To", defaultNumber));
		}

		MessageFactory messageFactory = client.getAccount().getMessageFactory();
		Message message = null;
		try {
			message = messageFactory.create(params);
		} catch (TwilioRestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(message.getSid());

	}
	
	
}
