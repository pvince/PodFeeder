/*
 * Items needed for <item> tag
 * - Title: ID3 Title or mp3 filename
 * - Link: strLink
 * - guid: strDownloadPath + (URL'ed mp3 filename)
 * - description: strDescription
 * - enclosure:
 *   - url: See Guid
 *   - length: mp3File.length()
 *   - type: audio/mpeg
 * - category: strCategory
 * - pubDate: First we try the current date, if that fails,
 *            start w/ current date, increment by a minute
 *            for each subsequent date.
 */

package podfeeder;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

/**
 *
 * @author vincenpt
 */
public class pfItem implements Comparable<pfItem> {
    private String _title;
    private String _link;
    private String _guid;
    private String _description;
    private long _length;
    private String _category;
    private int _trackNumber;
    private int _itemID;

    private static int itemCount = 0;
    private static Date pubDate = null;
    
    public pfItem(File mp3File, String link, String downloadPath,
                String description, String category) {
        if(pubDate == null)
            pubDate = new Date();
        
        _itemID = itemCount;
        itemCount++;
        try {
            AudioFile f = AudioFileIO.read(mp3File);
            Tag tag = f.getTag();

            _title = tag.getFirst(FieldKey.TITLE);
            if(_title == null || _title.trim().length() == 0) {
                _title = mp3File.getName();
            }

            String strTrackNum = tag.getFirst(FieldKey.TRACK);
            if(strTrackNum == null || strTrackNum.trim().length() == 0) {
                _trackNumber = _itemID;
            } else {
                try {
                    _trackNumber = Integer.parseInt(strTrackNum);
                } catch (NumberFormatException ex) {
                    _trackNumber = _itemID;
                }
            }
        } catch (CannotReadException ex) {
            Logger.getLogger(pfItem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(pfItem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TagException ex) {
            Logger.getLogger(pfItem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ReadOnlyFileException ex) {
            Logger.getLogger(pfItem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAudioFrameException ex) {
            Logger.getLogger(pfItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        _link = link;
        _description = description;
        _length = mp3File.length();
        _category = category;

        try {
            _guid = (downloadPath + URLEncoder.encode(mp3File.getName(), "UTF-8")).replace("+", "%20");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(pfItem.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public String getItemXML() {
        pubDate.setTime(pubDate.getTime() + 60000);
        String xmlItem = "";
        xmlItem += "<item>\n";
        xmlItem += "  <title>" + _title + "</title>\n";
        xmlItem += "  <link>" + _link + "</link>\n";
        xmlItem += "  <guid>" + _guid + "</guid>\n";
        xmlItem += "  <description>" + _description + "</description>\n";
        xmlItem += "  <enclosure url=\"" + _guid + "\" length=\"" + _length + "\" type=\"audio/mpeg\"/>\n";
        xmlItem += "  <category>" + _category + "</category>\n";
        xmlItem += "  <pubDate>" + pfUtilities.getDateTime(pubDate) + "</pubDate>\n";
        xmlItem += "</item>\n";
        return xmlItem;
    }

    public int getTrackNumber() {
        return _trackNumber;
    }

    public String getGuid() {
        return _guid;
    }

    public int getItemID() {
        return _itemID;
    }

    public String getTitle() {
        return _title;
    }

    public int compareTo(pfItem o) {
        int trackComp = _trackNumber - o.getTrackNumber();
        return trackComp;
    }
}
