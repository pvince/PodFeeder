package podfeeder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by vincentp on 8/28/2015.
 */
public class pfProperties {
   private static pfProperties instance = null;

   private Properties config;

   private final String defaultAlbumDir    = "";
   private final String defaultDisplayLink   = "";
   private final String defaultServerAddress  = "127.0.0.1/podcasts";

   protected pfProperties() {
      config = new Properties();
      try {
         config.load(new FileInputStream("PodFeeder.properties"));
      } catch (IOException ex) {
         System.err.println(ex);
      }
   }

   public synchronized static pfProperties getInstance() {
      if (instance == null) {
         instance = new pfProperties();
      }
      return instance;
   }

   public synchronized static pfProperties refresh() {
      instance = null; // Kill the current instance.
      return getInstance();
   }

   public String getProp(String key, String defaultString) {
      return config.getProperty(key, defaultString);
   }

   public String getAlbumDir() {
      return getProp("AlbumDir", defaultAlbumDir);
   }

   public String getDisplayLink() {
      return getProp("DisplayLink", defaultDisplayLink);
   }

   public String getServerAddress() {
      return getProp("ServerAddress", defaultServerAddress);
   }
}
