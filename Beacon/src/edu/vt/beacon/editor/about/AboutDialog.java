package edu.vt.beacon.editor.about;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.io.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import edu.vt.beacon.editor.dialog.AbstractDialog;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.resources.icons.IconType;

public class AboutDialog extends AbstractDialog {
	
	// Label Constants
	private static final String LABEL_VERSION = "Beacon Pathway Editor v1.1.0 build 13";
	private static final String LABEL_CONTRIBUTORS = "Beacon Contributors";
	private static final String LABEL_ACKNOWLEDGEMENTS = "Acknowledgements";
	private static final String LABEL_LICENSE = "Software License";

	private static final String AC_CONTRIBUTORS = "CONTRIBUTORS";
	private static final String AC_LICENSE = "LICENSE";
	private static final String AC_ACKNOWLEDGEMENTS = "ACKNOWLEDGEMENTS";
//	private static final String LICENSE_FILE = "src/edu/vt/beacon/editor/about/GNULesser.txt";
	private static final String LICENSE_FILE = "GNULesser.txt";
	private static final String CONTRIBUTOR_FILE = "Contributors.txt";
	private static final String ACK_FILE = "Acknowledgements.txt";
	

	public AboutDialog(Document document) {
		super(document, document.getFrame());
//		System.out.println("Working Directory = " +
//				System.getProperty("user.dir"));

//		System.out.println(AboutDialog.class.getPackage().getName());
		setModalityType(ModalityType.DOCUMENT_MODAL);
	    this.document_ = document;
	    add(createContentPanel());		
		pack();
		setLocationRelativeTo(getOwner());		
		setVisible(true);
	}

	public String getABoutDirectoryPath() {
		return AboutDialog.class.getPackage().getName().replace(".",
				System.getProperty("file.separator")) +
				System.getProperty("file.separator");
	}

	private Component createContentPanel() {

		JPanel basePanel = createAltBasePanel();
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		GridBagConstraints c = new GridBagConstraints();
//
//		ImagePanel imagePanel = new ImagePanel(
//				((ImageIcon)IconType.valueOf("NO_IMAGE").getIcon()).getImage());

        ImagePanel imagePanel = new ImagePanel(
                ((ImageIcon)IconType.valueOf("LOGO").getIcon()).getImage());

		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		contentPanel.add(imagePanel, c);
		
		JLabel beaconLabel = new JLabel(convertToMultiline(LABEL_VERSION));
		c.gridx = 1;
		c.gridy = 0;
		contentPanel.add(beaconLabel, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		contentPanel.add(new JSeparator(JSeparator.HORIZONTAL), c);
		
		
		JButton contributorsButton = new JButton(convertToMultiline(LABEL_CONTRIBUTORS));		
		contributorsButton.setActionCommand(AC_CONTRIBUTORS);
		contributorsButton.addActionListener(this);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.HORIZONTAL;
		contentPanel.add(contributorsButton, c);
		
		JButton licenseButton = new JButton(convertToMultiline(LABEL_LICENSE));	
		licenseButton.setActionCommand(AC_LICENSE);
		licenseButton.addActionListener(this);
		c.gridx = 1;
		c.gridy = 3;
		contentPanel.add(licenseButton, c);
			
		JButton acknowledgementsButton = new JButton(convertToMultiline(LABEL_ACKNOWLEDGEMENTS));		
		acknowledgementsButton.setActionCommand(AC_ACKNOWLEDGEMENTS);
		acknowledgementsButton.addActionListener(this);
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 4;
		contentPanel.add(acknowledgementsButton, c);
	
		basePanel.add(contentPanel);
		return basePanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	    String actionCommand = e.getActionCommand();
	    
	    if (actionCommand.equals(AC_OK_BUTTON))
			ok();
	    
	    else if (actionCommand.equals(AC_CONTRIBUTORS))
			launchInfoFrame(CONTRIBUTOR_FILE, "Contributors");
	    
	    else if (actionCommand.equals(AC_LICENSE))			
			launchInfoFrame(LICENSE_FILE, "GNU Lesser Public License");
	    
	    else if (actionCommand.equals(AC_ACKNOWLEDGEMENTS))
			launchInfoFrame(ACK_FILE, "Acknowledgements");
	}
	
	private void launchInfoFrame(String contentFile, String title) {
		String s = null;
		try {

			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream(getABoutDirectoryPath() + contentFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line +"\n");
			}

			s = out.toString();

//			File file = new File(Thread.currentThread().getContextClassLoader().getResource(
//					getABoutDirectoryPath() + contentFile).toString().trim());
//		    FileInputStream fis = new FileInputStream(file);
//		    byte[] data = new byte[(int)file.length()];
//		    fis.read(data);
//		    fis.close();
//		    s = new String(data, "UTF-8");
		  } catch(Exception e){
			  System.out.println(e);
		  }
		JTextArea textArea = new JTextArea(s);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);  
		textArea.setLineWrap(true);  
		textArea.setWrapStyleWord(true); 
		scrollPane.setPreferredSize( new Dimension( 500, 500 ) );
		JOptionPane.showMessageDialog(this, scrollPane, title,JOptionPane.PLAIN_MESSAGE);
		
		
	}

	private void ok() {
		this.dispose();
		
	}
	
	public static String convertToMultiline(String orig)
	{
	    return "<html>" + orig.replaceAll("\n", "<br>") + "</html>";
	}
	
	protected JPanel createAltBasePanel() {
		JPanel panel = new JPanel();
		LayoutManager borderLayout = new BorderLayout();
		panel.setLayout(borderLayout);
		panel.add(createOkButtonPanel(), BorderLayout.SOUTH);
		
		return panel;
	}
	
	private Component createOkButtonPanel() {
//		System.out.println("button");
		JComponent panel = new JPanel();
		((FlowLayout)panel.getLayout()).setAlignment(FlowLayout.RIGHT);
		JButton okButton = new JButton(LABEL_BUTTON_OK);
		okButton.setActionCommand(AC_OK_BUTTON);
		okButton.addActionListener(this);
		
		return panel;
	}



}
