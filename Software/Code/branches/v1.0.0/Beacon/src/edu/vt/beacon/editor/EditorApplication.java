package edu.vt.beacon.editor;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import edu.vt.beacon.editor.action.handler.FileHandler;
import edu.vt.beacon.editor.util.PlatformManager;

public class EditorApplication
    implements Runnable
{
    private static final boolean isDebugging = true;
    
    // TODO document method
    public static Logger getLogger()
    {
        return Logger.getLogger(EditorApplication.class.getName());
    }
    
    // TODO document method
    public static String getVersion()
    {
        return "0.1";
    }
    
    // FIXME complete method
    private static void initializeLogging()
    {
        Logger logger = getLogger();
        logger.setLevel(Level.ALL);
        
        if (isDebugging) {
            
            ConsoleHandler debugHandler = new ConsoleHandler();
            debugHandler.setLevel(Level.ALL);
            logger.addHandler(debugHandler);
        }
        
        logger.config(System.getProperty("os.name") + " operating system " +
            "version " + System.getProperty("os.version") + " " +
            System.getProperty("os.arch"));
        logger.config("Java version " + System.getProperty("java.version") +
            " by " + System.getProperty("java.vendor"));
        logger.config("Application version " + getVersion());
    }
    
	private void testOnlyInstance(boolean isMac)
	{
		try{
			ServerSocket server = new ServerSocket(4322);
			//System.out.println("First instance of Beacon, run successfully");
			//Socket clientSocket = server.accept();
			//Socket s = new Socket( 9090);
			//PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		    //BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		  } catch (IOException e) {
			  //System.out.println("Could not listen on port 4321\nThere is another instance of Beacon running on this machine");
			  
			  JOptionPane.showMessageDialog(null, "Just one instance of the editor can be run.\nPlease quit all instances and run the editor again.");
			  
			  System.exit(-1);
		  }
		
		/*try{
				Socket socket = new Socket("BeaconHost", 4321);
			//PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			//BufferedReader in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
		   } catch (UnknownHostException e) {
			   System.out.println("Unknown host: BeaconHost");
		     //System.exit(1);
		   } catch  (IOException e) {
			   System.out.println("No I/O");
		     //System.exit(1);
		   }*/
	}
    
    // TODO document method
    private static void initializeLookAndFeel()
    {
        try {
            
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName());
            
            LookAndFeelInfo[] lafInfoSet =
                UIManager.getInstalledLookAndFeels();
            
            for (LookAndFeelInfo lafInfo : lafInfoSet)
                if (lafInfo.getName().equals("Nimbus"))
                    UIManager.setLookAndFeel(lafInfo.getClassName());
            
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {}
        
        getLogger().config(UIManager.getLookAndFeel().getName() +
                           " look and feel");
    }
    
    // FIXME complete method
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new EditorApplication());
    }
    
    // FIXME complete method
    @Override
    public void run()
    {
        initializeLogging();
        initializeLookAndFeel();
        
        if (PlatformManager.isMacPlatform())
            PlatformManager.initializeApplication();
        
        testOnlyInstance(PlatformManager.isMacPlatform());
        
        FileHandler.getInstance().newFile(null);
    }
}