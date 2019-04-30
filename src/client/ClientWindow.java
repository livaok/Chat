package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientWindow extends JFrame {

	private final static String      SERVER_HOST = "localhost";
	private final static int         SERVER_PORT = 8880;
	private              Socket      clientSocket;
	private              Scanner     inMessage;
	private              PrintWriter outMessage;

	private JTextField inputMsgField;
	private JTextField inputNameField;
	private JTextArea  msgTextArea;

	private String clientName;

	public ClientWindow() throws HeadlessException {
		try {
			clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
			inMessage = new Scanner(clientSocket.getInputStream());
			outMessage = new PrintWriter(clientSocket.getOutputStream());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		setBounds(600, 300, 600, 500);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Chat on socket");

		msgTextArea = new JTextArea();
		msgTextArea.setEditable(false);
		msgTextArea.setLineWrap(true);

		JScrollPane jScrollPane = new JScrollPane(msgTextArea);
		add(jScrollPane, BorderLayout.CENTER);

		final JLabel clientsCountLabel = new JLabel("Clients on chat :");
		add(clientsCountLabel, BorderLayout.NORTH);

		JPanel jPanel = new JPanel(new BorderLayout());
		add(jPanel, BorderLayout.SOUTH);

		JButton sendButton = new JButton("Send message");
		jPanel.add(sendButton, BorderLayout.EAST);

		inputNameField = new JTextField("Input your name:");
		jPanel.add(inputNameField, BorderLayout.WEST);

		inputMsgField = new JTextField("Input your message:");
		jPanel.add(inputMsgField, BorderLayout.CENTER);

		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = inputMsgField.getText().trim();
				String nickname = inputNameField.getText().trim();

				if (!msg.isEmpty()) {
					clientName = nickname.isEmpty() ? "Anon" : nickname;
					sendMsg();
					inputMsgField.grabFocus();
				}
			}
		});

		inputNameField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				inputNameField.setText("");
			}
		});

		inputMsgField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				inputMsgField.setText("");
			}
		});

		new Thread(() -> {
			while (true) {
				if (inMessage.hasNext()) {
					String inMsg = inMessage.nextLine();
					Singleton.INSTANCE.getQueue().add(inMsg);
					String clientInChat = "In our chat client count is ";
					if (inMsg.indexOf(clientInChat) == 0) {
						clientsCountLabel.setText(inMsg);
					}
					else {
						System.out.println(inMsg);
						msgTextArea.append(inMsg);
						msgTextArea.append("\n");
					}
				}
			}
		}).start();

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				String nickname = clientName.trim().isEmpty() ? "Anon" : clientName;

				try {
					outMessage.println(nickname + " exit from  our chat");
					outMessage.println("EXIT");
					outMessage.flush();
					outMessage.close();
					inMessage.close();
					clientSocket.close();
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		setVisible(true);
	}

	private void sendMsg() {
		String msg = inputNameField.getText().trim() + " : " + inputMsgField.getText().trim();
		outMessage.println(msg);
		outMessage.flush();
		inputMsgField.setText("");
	}
}

