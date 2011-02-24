/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package podfeeder;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;

/**
 *
 * @author vincenpt
 */
public class pfUtilities {

	/**
	 * Creates a new date object w/ the current time, then returns
	 * a formatted string with the date time.
	 * 
	 * @return	Returns a properly formatted string with the current
	 * 			date.
	 */
    public static String getDateTime() {
    	//TODO: Verify that new Date() actually creates an object w/ the current datetime.
        Date date = new Date();
        return getDateTime(date);
    }
    
    /**
     * Using the AudioFile library, attempt to retrieve any album artwork
     * from the passed in mp3File.  If there is more than one image, only the
     * first image is saved out.
     * 
     * @param mp3File	The mp3 file to check for album art.
     * @param outfile	Path and or filename where we should save the 
     * 					retrieved album art.  This file will have .png 
     * 					appended to it.
     * @return	This also returns the image in the form of a BufferedImage
     * 			as well as saving the image file to a local directory.
     */
    public static BufferedImage saveAlbumArt(File mp3File, String outfile) {
    	//TODO: We should probably split this into two functions, saveAlbumArt will write the image to directory, retrieveAlbumArt will grab the buffered image.
        BufferedImage result = null;
        try {
            AudioFile f = AudioFileIO.read(mp3File);
            Tag tag = f.getTag();

            List<Artwork> artList =  tag.getArtworkList();
            if(artList.size() > 0) {
                Artwork a = artList.get(0);
                BufferedImage bi = a.getImage();
                File outputFile = new File(outfile + ".png");
                ImageIO.write(bi, "png", outputFile);
                result = bi;
            }

        } catch (CannotReadException ex) {
            Logger.getLogger(pfUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(pfUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TagException ex) {
            Logger.getLogger(pfUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ReadOnlyFileException ex) {
            Logger.getLogger(pfUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAudioFrameException ex) {
            Logger.getLogger(pfUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    /**
     * Uses the AudioFile library to try and find the album title for
     * the passed in mp3File.  If it can not find an album title it
     * uses the mp3File's parent directory.
     * 
     * @param dir		Directory the mp3File is contained inside.
     * @param mp3File	A mp3 file.
     * @return			A string to use for the mp3's album.
     */
    public static String getAlbumTitle(File dir, File mp3File) {
    	//TODO: Possible refactoring here, I do not think we need to pass in the 'dir' mp3File knows its directory.
    	//TODO: Probably should instead pass in a boolean, where we return an empty string if there is no albumTitle unless the boolean flag is set.
        String albumTitle = "";
        try {
            AudioFile f = AudioFileIO.read(mp3File);
            Tag tag = f.getTag();
            albumTitle = tag.getFirst(FieldKey.ALBUM).trim();
            if(albumTitle == null || albumTitle.length() == 0) {
                albumTitle = dir.getName();
            }
        } catch (CannotReadException ex) {
            Logger.getLogger(pfUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(pfUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TagException ex) {
            Logger.getLogger(pfUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ReadOnlyFileException ex) {
            Logger.getLogger(pfUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAudioFrameException ex) {
            Logger.getLogger(pfUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }

        return albumTitle;
    }

    /**
     * Takes the passed in date and returns a string formatted according to RSS
     * specifications.
     * 
     * @param spawnDate	Date to format.
     * @return			The date formatted according to RSS specifications.
     */
    public static String getDateTime(Date spawnDate) {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd, MMM yyyy hh:mm:ss Z");
        return dateFormat.format(spawnDate);
    }

    /**
     * Basic text file writer.  Writes the xmlFile string to the outFileName path.
     * 
     * @param outFileName	Path and/or name of the output file.  If no path is
     * 						supplied, it outputs to the directory the application
     * 						is run from. (I think).
     * @param xmlFile		The text to be written to the xml file.
     * @return	Returns "true" if the file is successfully written, false if it
     * 			fails.
     */
    public static boolean writeXMLFile(String outFileName, String xmlFile) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(outFileName, false));
            writer.write(xmlFile);
            writer.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Replaces invalid filename characters with 'c'.
     * 
     * @param filename	Filename to clean up.
     * @return			The cleaned filename.
     */
    public static String getCleanFilename(String filename) {
        return filename.replaceAll("[?:\\/*\"<>|]", "c");
    }
}
