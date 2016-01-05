package com.neeraj.blackjack.client;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class ClientThread implements Runnable {

	/*
	 * DataInput stream to listen from the server
	 */
	private DataInputStream dataIS;
	/*
	 * DataOutput stream to send data to the server
	 */
	private DataOutputStream dataOS;
	/*
	 * Id of the user
	 */
	private int id = 0;
	/*
	 * Group id for the user
	 */
	private int groupID = 0;
	/*
	 * Frame creation
	 */
	private JFrame frame = new JFrame("HelloWorldSwing");
	/*
	 * model of the ui
	 */
	private DefaultTableModel model = new DefaultTableModel();
	/*
	 * Table in the ui
	 */
	private JTable table = new JTable(model);
	/*
	 * choice of the user
	 */
	private int choice = 0;
	/*
	 * Label for the images and player name
	 */
	private JLabel imageLabel = new JLabel();
	/*
	 * Name of the player
	 */
	private String playerName;
	/*
	 * Panel for the players To be of shown on frame
	 */
	private JPanel player1 = new JPanel();
	private JPanel player2 = new JPanel();
	private JPanel player3 = new JPanel();
	private JPanel player4 = new JPanel();
	private JPanel player5 = new JPanel();
	/*
	 * Panel of the winners to be shown on dialog box
	 */
	private ArrayList<JPanel> headline = new ArrayList<JPanel>();
	private ArrayList<JPanel> midLIne = new ArrayList<JPanel>();
	private ArrayList<JPanel> endLine = new ArrayList<JPanel>();
	/*
	 * Numbers of winners
	 */
	private int winnersNumber = 0;
	/*
	 * Player number
	 */
	private int playerNumber = 0;
	/*
	 * Array of labels
	 */
	private ArrayList<String> labels = new ArrayList<String>();

	/*
	 * Initializing the different element of the thread
	 */
	public ClientThread(DataInputStream dIS, DataOutputStream dOS, String name) {
		this.dataIS = dIS;
		this.dataOS = dOS;
		this.frame = new JFrame("BlackJack");
		this.frame.setSize(1000, 800);
		this.frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try {
					dataOS.writeUTF("REMOVEUSER/GROUPNUMBER/" + groupID + "/ID/" + id);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.exit(0);

			}
		});
		this.frame.setBackground(Color.BLUE);
		this.frame.setLayout(new GridLayout(5, 1));
		this.player1.setBackground(Color.BLACK);
		this.player2.setBackground(Color.BLACK);
		this.player3.setBackground(Color.BLACK);
		this.player4.setBackground(Color.BLACK);
		this.player5.setBackground(Color.BLACK);
		this.playerName = name;
	}

	/*
	 * 
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		while (true) {

			try {
				String message = dataIS.readUTF();
				System.out.println(message);
				String ans = "A";
				String[] tokens = message.split("/");
				if (tokens[0].equals("ID")) {
					System.out.print(message);
					id = new Integer(tokens[1]);
					String[] buttons = { "New Game", "Join a Game" };
					int rc = JOptionPane.showOptionDialog(null, " Do you want to Start a New Game or join a  game?",
							"BlackJack ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons,
							buttons[1]);
					if (rc == 0) {
						ans = "N";
					} else if (rc == 1) {
						ans = "J";
					}
					if (rc == -1) {
						System.exit(0);

					}
					dataOS.writeUTF("GAMETYPE/" + ans.toUpperCase() + "/ID/" + id);

				}
				if (tokens[0].equals("GROUPAVAILABILITTY")) {
					if (tokens[1].equals("AVAILABLE")) {
						JTextField gName = new JTextField();
						String labels[] = { "2", "3", "4", "5" };
						JComboBox<String> comboBox = new JComboBox<String>(labels);
						while (gName.getText().isEmpty()) {
							final JComponent[] inputs = new JComponent[] { new JLabel("Enter Group Name"), gName,
									new JLabel("Select Number of Members"), comboBox };

							JOptionPane.showMessageDialog(null, inputs, "BlackJack Login", JOptionPane.PLAIN_MESSAGE);
						}
						dataOS.writeUTF(
								"GROUPNAME/" + gName.getText() + "/Size/" + comboBox.getSelectedItem() + "/ID/" + id);

					} else {
						System.out.println(
								("GameRoom Full \n You can wait and try with N after some timeor Join  using J:"));
						String[] buttons = { "New Game", "Join a Game" };
						int rc = JOptionPane.showOptionDialog(null, " GameRoom Full You can wait and try", "BlackJack ",
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[1]);
						if (rc == 0) {
							ans = "N";
						} else if (rc == 1) {
							ans = "J";
						} else if (rc == -1) {
							System.exit(0);

						}
						dataOS.writeUTF("GAMETYPE/" + ans.toUpperCase() + "/ID/" + id);

					}
				}

				if (tokens[0].equals("GROUP")) {
					System.out.println("Available Groups are:\n");

					model.addColumn("Choice");
					model.addColumn("Group Name");
					model.addColumn("Group SIze");
					model.addColumn("Availability");

					System.out
							.println("Choice                 Group Name              Size                  Available");
				}
				if (tokens[0].equals("GROUPNAME")) {

					System.out.println(tokens[6] + "                  " + tokens[1] + "                  " + tokens[3]
							+ "                  " + tokens[5]);
					if (tokens[5].equals("0")) {
						model.addRow(new Object[] { tokens[6], tokens[1], tokens[3], "Full" });
						choice++;
					} else {
						model.addRow(new Object[] { tokens[6], tokens[1], tokens[3], tokens[5] });
						labels.add("" + choice++);
					}

				}
				if (tokens[0].equals("LIST")) {
					if (tokens[1].equals("SENT")) {
						String labelUsed[] = new String[labels.size()];
						int i = 0;
						for (String label : labels) {
							labelUsed[i] = label;
							i++;
						}
						JComboBox<String> comboBox = new JComboBox<String>(labelUsed);
						final JComponent[] inputs = new JComponent[] { new JLabel("Available Groups are"), table,
								new JLabel("Choose a Group?"), comboBox };
						int rc = JOptionPane.showConfirmDialog(null, inputs, "BlackJack Login",
								JOptionPane.OK_CANCEL_OPTION);
						if (rc == 0) {
							dataOS.writeUTF("GROUPNUMBER/" + comboBox.getSelectedItem() + "/ID/" + id);
							this.groupID = Integer.parseInt((String) comboBox.getSelectedItem());
						} else
							System.exit(0);

					} else {
						System.out.println("No game is in session");
						String[] buttons = { "New Game", "Join a Game" };
						int rc = JOptionPane.showOptionDialog(null, " Do you want to Start a New Game or join a  game?",
								"No game is in session ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
								buttons, buttons[1]);
						if (rc == 0) {
							ans = "N";
						} else if (rc == 1) {
							ans = "J";
						} else if (rc == -1) {
							System.exit(0);

						}
						dataOS.writeUTF("GAMETYPE/" + ans.toUpperCase() + "/ID/" + id);
					}
				}
				if (tokens[0].equals("STARTGAME")) {
					if (tokens[1].equals("Now")) {
						playerNumber = 0;
						Integer playersHand = new Integer(tokens[2]);
						if (playersHand < 17) {
							if (!frame.isVisible())
								this.frame.setVisible(true);

							String[] buttons = { "HIT", "STAND" };
							int rc = JOptionPane.showOptionDialog(null,
									"Your Hand's Value is " + playersHand + " Do you want to Hit or Stand?",
									"BlackJack ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
									buttons, buttons[1]);
							if (rc == 0) {
								ans = "H";
							} else if (rc == 1) {
								ans = "S";
							} else if (rc == -1) {

								dataOS.writeUTF("REMOVEUSER/GROUPNUMBER/" + groupID + "/ID/" + id);
								System.exit(0);

							}
							System.out.println("Do you want to Hit or Stand?(H for Hit and S for Stand):");

							dataOS.writeUTF("HIT/" + ans.toUpperCase() + "/ID/" + id + "/GROUPID/" + this.groupID);
						} else if (playersHand >= 17) {
							System.out.println("You cannot Play any more");
							if (playersHand >= 17 && playersHand <= 21)
								dataOS.writeUTF("HIT/S/ID/" + id + "/GROUPID/" + this.groupID);
							else if (playersHand > 21) {
								dataOS.writeUTF("BUSTED/ID/" + id + "/GROUPID/" + this.groupID);
							}
						}
					} else if (tokens[1].equals("Wait")) {
						if (tokens[2].equals("N")) {
							JLabel imageLabel = new JLabel("Waiting for other players");
							player1.removeAll();
							player2.removeAll();
							player3.removeAll();
							player4.removeAll();
							player5.removeAll();
							player3.add(imageLabel);
							this.frame.add(player1);
							this.frame.add(player2);
							this.frame.add(player3);
							this.frame.add(player4);
							this.frame.add(player5);
							this.frame.setVisible(true);
						} else
							System.out.println("Wait for others to Complete");
					}

				}
				if (tokens[0].equals("GROUPSTATUS")) {
					if (tokens[1].equals("CREATED")) {
						JLabel imageLabel = new JLabel("Waiting for other players");
						player3.add(imageLabel);
						this.frame.add(player1);
						this.frame.add(player2);
						this.frame.add(player3);
						this.frame.add(player4);
						this.frame.add(player5);
						this.frame.setVisible(true);

						this.groupID = Integer.parseInt(tokens[2]);
						System.out.println("Waiting for Players to join");
					}

				}
				if (tokens[0].contains("cards")) {
					if (!tokens[1].contains("HIDDEN")) {
						for (String token : tokens) {
							if (token.contains(playerName)) {
								player1.removeAll();
								imageLabel = new JLabel("Your cards are");
								player1.add(imageLabel);

							} else if (!token.contains(playerName)) {
								System.out.println("/res/" + token + ".gif");
								BufferedImage image = ImageIO
										.read(getClass().getResourceAsStream("/" + token.trim() + ".gif"));
								ImageIcon icon = new ImageIcon(image);

								imageLabel = new JLabel(icon);
								imageLabel.setSize(100, 100);

								player1.add(imageLabel);

							}
						}
					} else {
						if (!tokens[0].contains(playerName + "'s")) {
							for (String token : tokens) {
								if (!token.contains(playerName) && token.contains("cards")) {
									imageLabel = new JLabel(token);
									if (playerNumber == 0) {
										player2.add(imageLabel);
									}
									if (playerNumber == 1) {
										player3.removeAll();
										player3.add(imageLabel);
									}
									if (playerNumber == 2) {
										player4.add(imageLabel);
									}
									if (playerNumber == 3) {
										player5.add(imageLabel);
									}
								} else {
									System.out.println("/res/" + token + ".gif");
									BufferedImage image = ImageIO
											.read(getClass().getResourceAsStream("/" + token.trim() + ".gif"));
									ImageIcon icon = new ImageIcon(image);

									imageLabel = new JLabel(icon);
									imageLabel.setSize(100, 100);

									if (playerNumber == 0) {
										player2.add(imageLabel);
									}
									if (playerNumber == 1) {
										player3.add(imageLabel);
									}
									if (playerNumber == 2) {
										player4.add(imageLabel);
									}
									if (playerNumber == 3) {
										player5.add(imageLabel);
									}
									SwingUtilities.updateComponentTreeUI(player2);
									SwingUtilities.updateComponentTreeUI(player3);
									SwingUtilities.updateComponentTreeUI(player4);
									SwingUtilities.updateComponentTreeUI(player5);
								}
							}
							playerNumber++;
						}
					}
					SwingUtilities.updateComponentTreeUI(player1);
					SwingUtilities.updateComponentTreeUI(player2);
					SwingUtilities.updateComponentTreeUI(player3);
					SwingUtilities.updateComponentTreeUI(player4);
					SwingUtilities.updateComponentTreeUI(player5);
					frame.add(player1);
					frame.add(player2);
					frame.add(player3);
					frame.add(player4);
					frame.add(player5);
					frame.setVisible(true);
					SwingUtilities.updateComponentTreeUI(frame);

				}
				if (tokens[0].equals("WINNER")) {
					int i = 0;
					headline.add(new JPanel());
					midLIne.add(new JPanel());
					endLine.add(new JPanel());
					for (String token : tokens) {
						if (!token.contains("WINNER")) {
							if (i == 0) {
								// headline.addAll(new JPanel() JLabel("Winner
								// is " + token));
								headline.get(headline.size() - 1).add(new JLabel("Winner is " + token));
								i++;
							} else if (token.toUpperCase().contains("OF")) {
								BufferedImage image = ImageIO
										.read(getClass().getResourceAsStream("/" + token.trim() + ".gif"));
								ImageIcon icon = new ImageIcon(image);
								midLIne.get(midLIne.size() - 1).add(new JLabel(icon));
							} else {
								endLine.get(endLine.size() - 1).add(new JLabel(token));
							}
						}
					}
					i = 0;
					winnersNumber++;
				}
				if (tokens[0].equals("RESTART")) {
					JPanel inputs = new JPanel(new GridLayout(winnersNumber, 1));
					for (int i = 0; i < winnersNumber; i++) {
						inputs.add(headline.get(i));
						inputs.add(midLIne.get(i));
						inputs.add(endLine.get(i));
					}
					// final JComponent[] inputs = new JComponent[] { head };
					JOptionPane.showMessageDialog(null, inputs, "BlackJack Winner", JOptionPane.PLAIN_MESSAGE);
					JLabel imageLabel = new JLabel("Waiting for other players");
					headline.clear();
					midLIne.clear();
					endLine.clear();
					winnersNumber = 0;
					player1.removeAll();
					player2.removeAll();
					player3.removeAll();
					player4.removeAll();
					player5.removeAll();
					player3.add(imageLabel);
					this.frame.add(player1);
					this.frame.add(player2);
					this.frame.add(player3);
					this.frame.add(player4);
					this.frame.add(player5);
					this.frame.setVisible(true);
					SwingUtilities.updateComponentTreeUI(frame);
					String[] buttons = { "Yes", "NO" };
					int rc = JOptionPane.showOptionDialog(null, " Do you want to Continue the game?", "BlackJack ",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[1]);
					if (rc == 0) {
						ans = "Y";
						dataOS.writeUTF("CONTINUE/" + ans.toUpperCase() + "/ID/" + id + "/GROUPID/" + groupID);

					} else if (rc == 1) {
						ans = "N";
						dataOS.writeUTF("CONTINUE/" + ans.toUpperCase() + "/ID/" + id + "/GROUPID/" + groupID);
						System.exit(0);
					} else if (rc == -1) {
						ans = "N";
						dataOS.writeUTF("CONTINUE/" + ans.toUpperCase() + "/ID/" + id + "/GROUPID/" + groupID);
						System.exit(0);
					}

				}
			} catch (Exception e) {
				this.dataOS = null;
				this.dataIS = null;
			}
		}
	}
}
