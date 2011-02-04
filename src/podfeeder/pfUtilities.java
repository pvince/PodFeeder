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

    public static String getDateTime() {
        Date date = new Date();
        return getDateTime(date);
    }
    
    public static BufferedImage saveAlbumArt(File mp3File, String outfile) {
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
    public static String getAlbumTitle(File dir, File mp3File) {
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

    public static String getDateTime(Date spawnDate) {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd, MMM yyyy hh:mm:ss Z");
        return dateFormat.format(spawnDate);
    }

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

    public static String getCleanFilename(String filename) {
        return filename.replaceAll("[?:\\/*\"<>|]", "c");
    }
}
