import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.Font;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;
	private Socket socket;
	private JPanel contentPane;
	private JTextArea screen;
	private JButton btnSend;
	private JTextField smsSend;
	private JComboBox comboBox;
	private String sendToPort;
	static ArrayList<String> listPort = new ArrayList<>();
	Thread read;

	public static void main(String[] args) throws IOException{
		String serverIP = "localhost";
		Client client = new Client();
		client.execute(serverIP, 3010);
		client.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	public Client() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 43, 566, 267);
		contentPane.add(scrollPane);
		
		screen = new JTextArea();
		screen.setLineWrap(true);
		screen.setEditable(false);
		scrollPane.setViewportView(screen);
		
		smsSend = new JTextField();
		smsSend.setBounds(10, 316, 345, 37);
		contentPane.add(smsSend);
		
		btnSend = new JButton("Send");
		btnSend.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnSend.setBounds(496, 316, 80, 37);
		contentPane.add(btnSend);
		
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (smsSend.getText().length() != 0) {
					try {
						DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						String sms = "{\"from\":"+socket.getLocalPort()+
								",\"to\":"+sendToPort+
								",\"content\":\""+smsSend.getText()+"\"}";
						dos.writeUTF(sms);
						screen.append("[You]: " + smsSend.getText() + "\n");
						smsSend.setText("");
					} catch (IOException err) {
						err.printStackTrace();
					}
				}
			}
			
		});
		
	}
	
//	private void execute(String host, Integer port) throws IOException {
//		socket = new Socket(host, port);
//		read = new ReadClient(socket, screen, comboBox, contentPane, btnSend, smsSend);
//		read.start();
//	}
	
	
	private void execute(String host, Integer port) throws IOException {
		socket = new Socket(host, port);
		read = new Thread(new Runnable() {
			
			@Override
			public void run() {
				DataInputStream dis = null;
				try {
					dis = new DataInputStream(socket.getInputStream());
					while (true) {
						String sms = dis.readUTF();
						if (sms.charAt(0) == '[') screen.append(sms + "\n");
						else {
							Client.listPort.clear();
							for (String item: sms.split(",")) {
								Client.listPort.add(item);
							}
							Client.listPort.remove(socket.getLocalPort()+"");
							if (comboBox != null) contentPane.remove(comboBox);
							comboBox = new JComboBox(Client.listPort.toArray());
							if (Client.listPort.size() != 0) {
								comboBox.setSelectedIndex(0);
								sendToPort = Client.listPort.get(0);
							}
							comboBox.addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									sendToPort = comboBox.getSelectedItem().toString();
								}
							});
							comboBox.setBounds(361, 316, 124, 37);
							contentPane.add(comboBox);
						}
					}
				} catch (Exception e) {
					try {
						dis.close();
						socket.close();
					} catch (IOException e2) {
						JOptionPane.showConfirmDialog(null, "Disconnected from the server", "", JOptionPane.CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					}
				}
			}
		});
		read.start();
	}
}
