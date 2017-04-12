package edu.vt.beacon.simulation;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Logger
{
    static java.util.logging.Logger logger;
    public FileHandler fileHandler;
    Formatter plainText;

    private Logger() throws IOException{
        //instance the logger
        logger = java.util.logging.Logger.getLogger(Logger.class.getName());
        //instance the filehandler
        File f = new File(System.getProperty("user.home") + File.separator + ".ViSiBooL" + File.separator + "log.txt");
        
        if(!f.getParentFile().exists())
        	f.getParentFile().mkdirs();
        
        fileHandler = new FileHandler(System.getProperty("user.home") + File.separator + ".ViSiBooL" + File.separator + "log.txt",true);
        //instance formatter, set formatting, and handler
        plainText = new SimpleFormatter();
        fileHandler.setFormatter(plainText);
        logger.addHandler(fileHandler);

    }
    
    
    private static java.util.logging.Logger getLogger(){
        if(logger == null){
            try {
                new Logger();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logger;
    }
    
    public static void log(Level level, String msg){
        getLogger().log(level, msg);
        System.out.println(msg);
    }
    
}