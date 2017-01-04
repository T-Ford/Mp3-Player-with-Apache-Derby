import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MP3 {
	//File file;
	String filename; // the directory address of where the file is located
	String fileLocation; // copy of filename for resume/play methods
	Player player;
	long pauseLocation; // used to store the time in which a song is paused
	long songLength; // used to store the current songs full length

	FileInputStream fis; // global fileinputstream object
	BufferedInputStream bis; // global bufferedinputstream object

	// constructor that takes the name of an MP3 file
	public MP3(String filename) {
		this.filename = filename;
	}

	public void stop() {
		if (player != null) {
			player.close();
			pauseLocation = 0;
			songLength = 0;
		}
	}

	// play the MP3 file to the sound card
	public void play() {
		try {
			fis = new FileInputStream(filename);
			bis = new BufferedInputStream(fis);
			player = new Player(bis);

			songLength = fis.available();

			fileLocation = filename + "";
		} catch (Exception e) {
		}

		// run in new thread to play in background which is used to play the
		// audio file
		new Thread() {
			public void run() {
				try {
					player.play();
				} catch (Exception e) {
					// System.out.println(e);
				}
			}
		}.start();
	}

	// This method saves the current location where the audio stopped so it can
	// then be resumed using the resume method
	public void pause() {
		if (player != null) {
			try {
				pauseLocation = fis.available();
				player.close();

			} catch (IOException e) {

			}
		}
	}

	// This method allows the audio to pick up where it left off when it stopped
	// using the pause method
	public void resume() {
		try {
			fis = new FileInputStream(fileLocation);
			bis = new BufferedInputStream(fis);

			player = new Player(bis);
			fis.skip(songLength - pauseLocation);
		} catch (Exception e) {
		}

		// run in new thread to play in background which is used to play the
		// audio file
		new Thread() {
			public void run() {
				try {
					player.play();
				} catch (Exception e) {
					// System.out.println(e);
				}
			}
		}.start();
	}

	// Checks if the current mp3 is finished playing
	public boolean isOver() {
		if (player.isComplete() == false) {
			return false;
		} else {
			return true;
		}
	}

	// This method retrieves the title of the current song being played
	public String getTitle() {
		String title = null;
		try {
			InputStream input = new FileInputStream(new File(filename));
			ContentHandler handler = new DefaultHandler();
			Metadata metadata = new Metadata();
			Parser parser = new Mp3Parser();
			ParseContext parseCtx = new ParseContext();
			parser.parse(input, handler, metadata, parseCtx);
			input.close();

			// Retrieve the metadata of the title of the song
			title = metadata.get("title");

		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		} catch (SAXException e) {
			// e.printStackTrace();
		} catch (TikaException e) {
			// e.printStackTrace();
		}
		return title;
	}

	// This method retrieves the title of the current song being played
	public String getArtist() {
		String artist = null;
		try {
			InputStream input = new FileInputStream(new File(filename));
			ContentHandler handler = new DefaultHandler();
			Metadata metadata = new Metadata();
			Parser parser = new Mp3Parser();
			ParseContext parseCtx = new ParseContext();
			parser.parse(input, handler, metadata, parseCtx);
			input.close();
			 

			// Retrieve the metadata of the title of the song
			artist = metadata.get("xmpDM:albumArtist");

		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		} catch (SAXException e) {
			// e.printStackTrace();
		} catch (TikaException e) {
			// e.printStackTrace();
		}
		return artist;
	}
	
	// This gets the song duration
	public long getDuration(){
		long d = songLength;
		return d;
	}
	
	// This method is used if getTitle and getArtist return null, this will
	// return the visible name of the file
	public String getFileName() {
		String sFileName = null;
		try {
			sFileName = FilenameUtils.getBaseName(filename);
		}catch(Exception e){}
		return sFileName;
	}
}