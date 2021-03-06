/*
 * For help on the AudioLibrary: http://www.jthink.net/jaudiotagger/examples_read.jsp
 * 
 */
package podfeeder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author vincenpt
 */
public class Main {

    /**
     * This function's only purpose is to kill the verbose logging of the
     * jaudiotagger library.
     */
    @SuppressWarnings("deprecation")
	private static void killVerboseLogs() {
        // Disable the horribly verbose logs from AudioTagger
        try {
            LogManager.getLogManager().readConfiguration(new StringBufferInputStream("org.jaudiotagger.level = OFF"));
        } catch (SecurityException e) {
            //log.warn("Fail to suppress the java.util.logger config.", e);
        } catch (IOException e) {
            //log.warn("Fail to suppress the java.util.logger config.", e);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        killVerboseLogs();

        // Disable this for limited output.
        boolean beVerbose = true;

        // Added this as a convienience function when working w/ multiple
        // folders inside of another folder..
        //String Directory = "DnD_Series4_DarkSun";

        // The location of the directory to podcastify.
        //String strAlbumDir = "Z:/Media/Podcasts/Penny Arcade/" + Directory;

        String strAlbumDir = pfProperties.getInstance().getAlbumDir();

        // Load the path to the directory
        File mp3Directory = new File(strAlbumDir);

        // The base name for the output file, (default name)
        String strOutputName = mp3Directory.getName().replace(" ", ""); 

        // The output directory + name for XML and images
        String strOutputXMLPath = strAlbumDir + "/" + strOutputName + ".xml";
        String strOutputImagePath = strAlbumDir + "/" + strOutputName;
        
        // Podcast RSS variables.
        String strLink = pfProperties.getInstance().getDisplayLink();
        String strCopyright = "Not Mine";
        String strDescription = "Podcast generated by PodcastFeeder";
        String strWebmaster = pfProperties.getInstance().getDisplayLink();
        //String strServerAddress = "http://" + "192.168.2.143";  // Possibly see about grabbing the IP address dynamically?
        //String strServerDirectory = "podcasts"; // strServerAddress + "/" + strServerDirectory + "/" + albumTitle
        String strCategory = "Podcasts";

        String strDownloadPath = pfProperties.getInstance().getServerAddress();

        if(!strDownloadPath.endsWith("/"))
            strDownloadPath += "/";


        // Validate we have, in fact, loaded a directory.
        if (!mp3Directory.isDirectory()) { // If not a directory...
            System.err.println("Error: Specified location ('"
                    + mp3Directory.getPath() + "') is not a directory.");
        } else {
            // Now that we know we have a directory, lets grab a safe version of its name.
            // and tack it onto the end of the DownloadURL
            try {
                strDownloadPath += URLEncoder.encode(mp3Directory.getName(), "UTF-8") + "/";
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Create a filter so that only mp3 files are loaded.
            FilenameFilter mp3Filter = new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".mp3");
                }
            };

            // Grab a filtered list of mp3 files.
            File[] mp3Files = mp3Directory.listFiles(mp3Filter);

            if (mp3Files.length == 0) {
                System.err.println("Error: No mp3 files found, wtf man.");
            } else {
                // Create an arraylist of pfItems, sorted by trackNumber.
                if (beVerbose) {
                    System.out.println("Loading mp3 files...");
                }

                pfUtilities.saveAlbumArt(mp3Files[0], "test.jpg");
                ArrayList<pfItem> mp3ItemList = new ArrayList<pfItem>();
                for (int i = 0; i < mp3Files.length; i++) {
                    if (beVerbose) {
                        System.out.println("\t" + mp3Files[i].getName());
                    }
                    mp3ItemList.add(new pfItem(mp3Files[i], strLink, strDownloadPath, strDescription, strCategory));
                }
                Collections.sort(mp3ItemList);

                // Generate the final XML result from the priorityQueue.
                if (beVerbose) {
                    System.out.println("Generating XML file...");
                }
                String xmlOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                xmlOutput += "<rss version=\"2.0\">\n";
                xmlOutput += "<channel>\n";
                String strAlbumTitle = pfUtilities.getAlbumTitle(mp3Directory, mp3Files[0]);
                xmlOutput += "<title>" + strAlbumTitle + "</title>\n";
                xmlOutput += "<description>" + strDescription + "</description>\n";
                xmlOutput += "<link>" + strLink + "</link>\n";
                xmlOutput += "<language>en-us</language>\n";
                xmlOutput += "<copyright>" + strCopyright + "</copyright>\n";
                xmlOutput += "<lastBuildDate>" + pfUtilities.getDateTime() + "</lastBuildDate>\n"; //TODO: Generate todays date in correct format
                xmlOutput += "<pubDate>" + pfUtilities.getDateTime() + "</pubDate>\n";              //TODO: Generate todays date in correct format
                xmlOutput += "<docs>http://blogs.law.harvard.edu/tech/rss</docs>\n";
                xmlOutput += "<webMaster>" + strWebmaster + "</webMaster>\n";

                try {
                    String xmlImage = "";
                    BufferedImage bi = null;
                    if ((bi = pfUtilities.saveAlbumArt(mp3Files[0], strOutputImagePath)) != null) {
                        xmlImage += "<image>\n";
                        xmlImage += "  <url>" + (strDownloadPath + URLEncoder.encode(strOutputName, "UTF-8")).replace("+", "%20") + ".png</url>\n";
                        xmlImage += "  <title>" + strAlbumTitle + "</title>\n";
                        xmlImage += "  <width>" + bi.getWidth() + "</width>\n";
                        xmlImage += "  <height>" + bi.getHeight() + "</height>\n";
                        xmlImage += "</image>\n";
                        xmlOutput += xmlImage;
                        if(beVerbose) {
                            System.out.println("Found Album Art! Saving as '" + strOutputImagePath + ".png'");
                        }
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

                Iterator<pfItem> e = mp3ItemList.iterator();
                while (e.hasNext()) {
                    pfItem curItem = e.next();
                    if (beVerbose) {
                        System.out.println("Track: " + curItem.getTrackNumber()
                                + "\tID: " + curItem.getItemID()
                                + "\tTitle: " + curItem.getTitle()
                                + "\tURL: " + curItem.getGuid());
                    }
                    xmlOutput += curItem.getItemXML();
                }

                xmlOutput += "</channel>\n</rss>";

                // Write out the XML file.
                if (beVerbose) {
                    System.out.println("Writing XML file...");
                }
                pfUtilities.writeXMLFile(strOutputXMLPath, xmlOutput);
            }
        }
    }
}
