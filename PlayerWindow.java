
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class PlayerWindow {

	private JFrame frame;
	private JLabel infoLabel; // Used to display on the application which file
								// is being played
	static File[] music = new File[200]; // Still store the files of directory
											// paths so the files can be opened
	static File[] musicClone = new File[200];
	static String[] dPath = new String[10]; // Just stores the directory paths
											// from the derby database in order
											// to store the contents to music[]
	static DBase db; // Database object
	int index = 0; // used to traverse through the array of files
	static int count = 0; // will use when importing music to the array, starts
							// putting files in the array at the next possible
							// index
	File selectedFile;
	MP3 song = null; // Mp3 object
	Random rand;
	JFileChooser chooser = new JFileChooser();
	String fDirectory; // folder directory path
	static String directoryList = ""; // will use to print out a list of the
										// directory paths
	static int progressValue = 0;

	// Contents of the help menu on click
	String helpPopup = "-> Must restart program after adding & deleting music directories \n-> Can only play Mp3 files"
			+ "\n-> Hitting play, starts at the first song of the play list";
	String aboutPopup = "Created by Tyler J Ford \n December 2016";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PlayerWindow window = new PlayerWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// This creates a database object and gets the dPath strings to be able to be read by the applications
		try {
			db = new DBase();
			dPath = db.getDirectory();
			for (int m = 0; m < dPath.length; m++) {
				if (dPath[m] != null)
					importMusic(dPath[m]);
			}
			// System.out.println(count);
			// db.showAll();
			if (!db.isopen()) {
				System.out.printf("Could not connect to database.%n");
				// System.exit(1);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Create the application.
	 */
	public PlayerWindow() {
		try {
			initialize();
		} catch (Exception e) {
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() throws Exception {
		frame = new JFrame("Tyler Fords Mp3Player Application");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton resumeButton = new JButton("Resume");
		resumeButton.setVisible(false);
		JButton stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
		JButton pauseButton = new JButton("Pause");
		pauseButton.setEnabled(false);
		JButton nextButton = new JButton("Next");
		nextButton.setEnabled(false);
		JButton previousButton = new JButton("Previous");
		previousButton.setEnabled(false);
		JButton playButton = new JButton("Play");
		playButton.setEnabled(true);

		// JButton playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {

			// The actions of the play button. Starting playing the audio file
			public void actionPerformed(ActionEvent a) {
				try {
					play();
					playButton.setEnabled(false);
					stopButton.setEnabled(true);
					pauseButton.setEnabled(true);
					nextButton.setEnabled(true);
					previousButton.setEnabled(true);
				} catch (Exception e) {
				}
			}
		});

		playButton.setFont(new Font("Tahoma", Font.BOLD, 20));
		playButton.setForeground(Color.BLACK);
		playButton.setBackground(Color.GREEN);
		playButton.setBounds(141, 96, 158, 74);
		frame.getContentPane().add(playButton);

		// JButton pauseButton = new JButton("Pause");
		pauseButton.addActionListener(new ActionListener() {

			// The actions of the pause button. Pauses the playing audio file to
			// later resume
			public void actionPerformed(ActionEvent a) {
				try {
					song.pause();
					playButton.setVisible(false);
					resumeButton.setVisible(true);
					resumeButton.setEnabled(true);
					pauseButton.setEnabled(false);
				} catch (Exception e) {
				}
			}
		});

		pauseButton.setFont(new Font("Tahoma", Font.BOLD, 17));
		pauseButton.setBackground(Color.CYAN);
		pauseButton.setBounds(0, 125, 113, 43);
		frame.getContentPane().add(pauseButton);

		// JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {

			// The actions of the play button. Stops the audio file
			public void actionPerformed(ActionEvent a) {
				try {
					song.stop();
					infoLabel.setText("");
					stopButton.setEnabled(false);
					playButton.setEnabled(true);
					pauseButton.setEnabled(false);
				} catch (Exception e) {
				}
			}
		});

		// JButton resumeButton = new JButton("Resume");
		resumeButton.addActionListener(new ActionListener() {

			// The actions play the song from the moment it was paused
			public void actionPerformed(ActionEvent a) {
				try {
					song.resume();
					resumeButton.setVisible(false);
					playButton.setVisible(true);
					stopButton.setEnabled(true);
					pauseButton.setEnabled(true);
				} catch (Exception e) {
				}
			}
		});
		resumeButton.setFont(new Font("Tahoma", Font.BOLD, 17));
		resumeButton.setBackground(Color.GREEN);
		resumeButton.setBounds(141, 96, 158, 74);
		frame.getContentPane().add(resumeButton);

		stopButton.setFont(new Font("Tahoma", Font.BOLD, 17));
		stopButton.setBackground(Color.RED);
		stopButton.setBounds(329, 125, 105, 43);
		frame.getContentPane().add(stopButton);

		// JButton previousButton = new JButton("Previous");
		previousButton.addActionListener(new ActionListener() {

			// This action plays the last played song
			// See previous() method code further down
			public void actionPerformed(ActionEvent a) {
				try {
					previous();
				} catch (Exception e) {
				}
				pauseButton.setEnabled(true);
				resumeButton.setEnabled(false);
				resumeButton.setVisible(false);
				playButton.setVisible(true);
			}
		});
		previousButton.setBounds(103, 179, 89, 23);
		frame.getContentPane().add(previousButton);

		// JButton nextButton = new JButton("Next");
		nextButton.addActionListener(new ActionListener() {

			// This action plays the next available song
			// See next() method code further down
			public void actionPerformed(ActionEvent a) {
				try {
					next();
				} catch (Exception e) {// e.printStackTrace();
				}
				pauseButton.setEnabled(true);
				resumeButton.setEnabled(false);
				resumeButton.setVisible(false);
				playButton.setVisible(true);
			}
		});
		nextButton.setBounds(251, 179, 89, 23);
		frame.getContentPane().add(nextButton);

		infoLabel = new JLabel("");
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(0, 35, 434, 23);
		frame.getContentPane().add(infoLabel);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 3, 97, 21);
		frame.getContentPane().add(menuBar);

		// Contents of the Options menu
		JMenu Options = new JMenu("Options");
		menuBar.add(Options);
		JMenuItem addMusic = new JMenuItem("Add Music Directories");
		addMusic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				try {
					// This code allows the user to select from which folder
					// directory to add music from
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(new java.io.File("."));
					chooser.setDialogTitle("choosertitle");
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					chooser.setAcceptAllFileFilterUsed(false);
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						fDirectory = chooser.getSelectedFile().toString();
					}
					saveDPath(fDirectory);
					// System.exit(0);
					// JOptionPane.showMessageDialog(null, count + " Paths were
					// added");
				} catch (Exception e) {
				}
			}
		});
		Options.add(addMusic);
		JMenuItem deleteDir = new JMenuItem("Delete Music Directories");
		deleteDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				try {
					db.deleteAll();
					// System.exit(0);
				} catch (Exception e) {
				}
			}
		});
		Options.add(deleteDir);

		JMenuItem viewDir = new JMenuItem("View Music Directories");
		viewDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				try {
					for (int m = 0; m < dPath.length; m++) {
						if (dPath[m] != null)
							directoryList = directoryList + " \n " + dPath[m];
					}
					JOptionPane.showMessageDialog(null, "Directory List \n" + directoryList);
				} catch (Exception e) {
				}
			}
		});
		Options.add(viewDir);

		// Contents of the Help Menu
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				try {
					JOptionPane.showMessageDialog(null, aboutPopup);
				} catch (Exception e) {
				}
			}
		});
		helpMenu.add(about);
		JMenuItem help = new JMenuItem("Help Contents");
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				try {
					JOptionPane.showMessageDialog(null, helpPopup);
				} catch (Exception e) {
				}
			}
		});
		helpMenu.add(help);

	}

	// This method is used add music into the application array of files by
	// directory path stored in the local database
	// the application will be able to read mp3 files with the main folder chosen and its subfolders
	public static void importMusic(String musicFolder) {
		try {
			File f = new File(musicFolder);
			File array[] = f.listFiles();
			for (File file : array) {
				if (file == null) {
					return;
				}
				if (file.isHidden() || !file.canRead())
					continue;
				if (file.isDirectory()) {
					importMusic(file.getPath());
				} else if (file.getName().endsWith(".mp3")) {
					// System.out.println(file.getPath() + "\\" +
					// file.getName());
					music[count] = file;
					musicClone[count] = file;
					count++;
				}
			}
		} catch (Exception e) {
		}
	}

	// This method will save directory paths to a text file so the application
	// can read them later in the importMusic method
	public void saveDPath(String s) throws IOException {
		try {
			db.updateDirectory(s);
		} catch (Exception e) {
		}
	}

	// This method starts by playing the first song in the array
	// Should display the name of the artist and the song name if the metadata is available
	public void play() throws Exception {
		try {
			selectedFile = music[index].getAbsoluteFile();
			song = new MP3(selectedFile.getAbsolutePath());
			song.play();
			String songTitle = song.getTitle();
			String songArtist = song.getArtist();
			String songFileName = song.getFileName();
			if (songTitle == null || songArtist == null) {
				infoLabel.setText("Currently Playing: " + songFileName);
			} else {
				infoLabel.setText("Currently Playing: " + songArtist + " - " + songTitle);
			}
		} catch (Exception e) {
		}
		autoPlay();
	}


	// This method plays the next available song
	public void next() throws Exception {
		try {
			song.stop();
			index++;
			// Checks if the current index of the array is null, then it resets
			// index to 0 and starts from beginning
			if (music[index] == null) {
				index = 0;
				selectedFile = music[index].getAbsoluteFile();
				song = new MP3(selectedFile.getAbsolutePath());
				song.play();
				String songTitle = song.getTitle();
				String songArtist = song.getArtist();
				String songFileName = song.getFileName();
				// Should display the name of the artist and the song name if the metadata is available
				if (songTitle == null || songArtist == null) {
					infoLabel.setText("Currently Playing: " + songFileName);
				} else {
					infoLabel.setText("Currently Playing: " + songArtist + " - " + songTitle);
				}
				// Else is continues to traverse through the array as normal
			} else {
				selectedFile = music[index].getAbsoluteFile();
				song = new MP3(selectedFile.getAbsolutePath());
				song.play();
				String songTitle = song.getTitle();
				String songArtist = song.getArtist();
				String songFileName = song.getFileName();
				// Should display the name of the artist and the song name if the metadata is available
				if (songTitle == null || songArtist == null) {
					infoLabel.setText("Currently Playing: " + songFileName);
				} else {
					infoLabel.setText("Currently Playing: " + songArtist + " - " + songTitle);
				}
			}
		} catch (Exception e) {
		}
		autoPlay();
	}

	// This method plays the last song played
	public void previous() throws Exception {
		try {
			song.stop();
			index--;
			int n = 0; // this will be used in the for loop to save the last
						// spot in the array that is not null
			if (index < 0) {
				for (int i = 0; i < (music.length - 1); i++) {
					if (music[i] != null) {
						n++;
					}
				}
				n = n - 1;
				index = n;
				selectedFile = music[index].getAbsoluteFile();
				song = new MP3(selectedFile.getAbsolutePath());
				song.play();
				String songTitle = song.getTitle();
				String songArtist = song.getArtist();
				String songFileName = song.getFileName();
				// Should display the name of the artist and the song name if the metadata is available
				if (songTitle == null || songArtist == null) {
					infoLabel.setText("Currently Playing: " + songFileName);
				} else {
					infoLabel.setText("Currently Playing: " + songArtist + " - " + songTitle);
				}
			} else {
				selectedFile = music[index].getAbsoluteFile();
				song = new MP3(selectedFile.getAbsolutePath());
				song.play();
				String songTitle = song.getTitle();
				String songArtist = song.getArtist();
				String songFileName = song.getFileName();
				// Should display the name of the artist and the song name if the metadata is available
				if (songTitle == null || songArtist == null) {
					infoLabel.setText("Currently Playing: " + songFileName);
				} else {
					infoLabel.setText("Currently Playing: " + songArtist + " - " + songTitle);
				}
			}
		} catch (Exception e) {
		}
		autoPlay();
	}

	// This method will repeatedly check to see if the currently played song is
	// over, once it is, it will proceed to the next song in the array
	public void autoPlay() throws Exception {
		new Thread() {
			boolean testCase = false;

			public void run() {
				try {
					while (testCase == false) {
						if (song.isOver() == true) {
							next();
							testCase = true;
						}
					}
				} catch (Exception e) {
				}
			}
		}.start();
	}
}