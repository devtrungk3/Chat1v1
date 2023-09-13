import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea screen;
	private Integer port;
	static ArrayList<Socket> ListSocket;
	static String listPort = "";
	public ArrayList<User> UserList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws IOException {
		Server.ListSocket = new ArrayList<>();
		Server server = new Server(3010);
		server.setVisible(true);
		server.execute();
	}

	/**
	 * Create the frame.
	 */
	public Server(Integer port) {
		
		this.port = port;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 566, 343);
		contentPane.add(scrollPane);
		
		screen = new JTextArea();
		screen.setLineWrap(true);
		screen.setEditable(false);
		scrollPane.setViewportView(screen);
	}
	
	private void execute() throws IOException {
		ServerSocket server = new ServerSocket(port);
		screen.append("Server is listening...\n");
		while (true) {
			Socket socket = server.accept();
			screen.append("Connected with " + socket + "\n");
			Server.ListSocket.add(socket); 
			listPort += socket.getPort() + ",";
			ReadServer read = new ReadServer(socket, screen);
			read.start();
			try {
				for (Socket item : Server.ListSocket) {
					DataOutputStream dos = new DataOutputStream(item.getOutputStream());
					dos.writeUTF(listPort);
				}
			} catch (IOException err) {
				err.printStackTrace();
			}
		}
	}
}

class User {
	Integer port;
	String username;
	
	public User(Integer port, String username) {
		this.port = port;
		this.username = username;
	}
	
	public Integer getPort() {
		return port;
	}
	public String getUsername() {
		return username;
	}
}


class ReadServer extends Thread {
	
	Socket sender;
	JTextArea screen;
	String sms = "";
	
	public ReadServer(Socket sender, JTextArea screen) {
		this.sender = sender;
		this.screen = screen;
	}
	
	@Override 
	public void run() {
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(sender.getInputStream());
			Gson gson = new Gson();
			while (true) {
				String txt = dis.readUTF();
				Message msg = gson.fromJson(txt, Message.class);
				sms = "[" + msg.getFrom() + "]: " + msg.content;
//				if (sms.equals("exit")) {
//					Server.ListSK.remove(server);
//					dis.close();
//					server.close();
//					continue;
//				}
				for (Socket item : Server.ListSocket) {
					if (item.getPort() == msg.getTo()) {
						DataOutputStream dos = new DataOutputStream(item.getOutputStream());
						dos.writeUTF(sms);
					}
				}
				screen.append(sms + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				dis.close();
				sender.close();
			} catch (IOException ex) {
				screen.append("Disconnected from the server\n");
			}
		}
	}
}

class Message {
	Integer from, to;
	String content;
	
	public Message(Integer from, Integer to, String content) {
		this.from = from;
		this.to = to;
		this.content = content;
	}
	
	public Integer getFrom() {
		return from;
	}
	public Integer getTo() {
		return to;
	}
	public String getContent() {
		return content;
	}
}

