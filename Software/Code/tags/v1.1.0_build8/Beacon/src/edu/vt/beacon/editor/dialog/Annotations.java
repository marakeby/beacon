package edu.vt.beacon.editor.dialog;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import edu.vt.beacon.editor.document.Document;



/**
 * 
 * @author Farzaneh S Tabataba
 *
 */
public class Annotations extends JFrame {
	
   // informative Variables:
	String PubmedID_ = "";
	String URL_= "";
	String Notes_= "";
	String GeneID= "";


    TitledBorder titledBorder1 = new TitledBorder(null,"",TitledBorder.RIGHT,0);
    JPanel jPanel1 = new JPanel();

    JTextArea jTextArea_GeneID_; // 2 int
    JTextArea jTextArea_Notes_;
    JLabel jLabel_EnterURL_ = new JLabel();
    JTextField jTextField_getURL = new JTextField();
    JTextField jTextField_getPubMEDID = new JTextField();
    
    JButton jButton_Edit_ = new JButton();
    JButton jButton_Delete_ = new JButton();
    JPanel jPanel2 = new JPanel();
    TitledBorder titledBorder2 = new TitledBorder(null,"",TitledBorder.RIGHT,0);
    TitledBorder titledBorder3 = new TitledBorder(null,"",TitledBorder.RIGHT,0);

    JLabel jLabel_PubMedID_ = new JLabel();
    JTextField jTextField3 = new JTextField();
    JButton jButton_Add_ = new JButton();
    JTextField jTextField4 = new JTextField();

    JButton jButton_color_picker_ = new JButton();
    JButton jButton_cancel_ = new JButton();
    JButton jButton_Apply_ = new JButton();
    JButton jButton_OK_ = new JButton();
    JPanel jPanel_background = new JPanel();
    JLabel jLabel_Notes_ = new JLabel();
    JLabel jLabel_GeneID_ = new JLabel();
    
    int x_ = 300;
    int y_=  100;
    int width_ = 470;
	int height_ = 610;
	int hPanel1 = height_ - 37 , hPanel2 = 100 , hPanel3 = 237;
	int selectedIndex; 

	private Document document_;
	
	public Annotations(Document doc){
		try{
			document_ = doc;
			jbInit();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void jbInit() throws Exception {
        this.getContentPane().setBackground(SystemColor.LIGHT_GRAY);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 13));
       // this.setJMenuBar(null);
        this.setResizable(false);
        this.setTitle("Annotations");
        this.setSize(width_,height_);
        this.getContentPane().setLayout(null);
        this.setLocation(x_,y_);
        
        jPanel_background.setLayout(null);
        jPanel_background.setBackground(SystemColor.inactiveCaptionBorder);
        jPanel_background.setBounds(new Rectangle(1, 0, width_-8, height_-20)); // ?? 

        titledBorder1.setTitle("Annotations");
        titledBorder1.setTitleColor(SystemColor.blue);
        jPanel1.setBackground(SystemColor.inactiveCaption); //  
        jPanel1.setBorder(titledBorder1);
        jPanel1.setBounds(new Rectangle(2, 2, width_-12, hPanel1));
        jPanel1.setLayout(null);
        jPanel_background.add(jPanel1, null);
        
        int xPubmedID = 75, yPubmedID = 60;
        int xLabel4 = xPubmedID ;
        int hLabel4 = 26;
//        int yLabel4 = yPubmedID - hLabel4;
        
        
        int xLabel2 = width_/6  ;
        int yLabel2 = hPanel2/4;
        
        jLabel_PubMedID_.setText("PubMed ID: ");
        jLabel_PubMedID_.setBounds(new Rectangle(xLabel2, yLabel2 , 220, hLabel4));

        jLabel_PubMedID_.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel_PubMedID_.setHorizontalTextPosition(SwingConstants.LEFT);
        jLabel_PubMedID_.setLabelFor(jTextField_getURL);
        jPanel1.add(jLabel_PubMedID_, null);

        
        jTextField_getPubMEDID.setBounds(new Rectangle(xLabel2, yLabel2+ 25, 300, 26));
//        jTextField_getURL.addActionListener(new Legend_jTextField_getURL_actionAdapter(this));
//        jTextField_getPubMEDID.setEnabled(false);
        jPanel1.add(jTextField_getPubMEDID, null);
        
        int xLabelURL = width_/6  ;
        int yLabelURL = hPanel1/5 -15;
        jLabel_EnterURL_.setLabelFor(jTextField_getURL);
        jLabel_EnterURL_.setText("URL:");
        jLabel_EnterURL_.setBounds(new Rectangle(xLabelURL, yLabelURL, 220, 26));

        jLabel_EnterURL_.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel_EnterURL_.setHorizontalTextPosition(SwingConstants.LEFT);
        jLabel_EnterURL_.setEnabled(true);
        jPanel1.add(jLabel_EnterURL_, null);
        jTextField_getURL.setBounds(new Rectangle(xLabelURL, yLabelURL+ 25, 300, 26));
//        jTextField_getURL.addActionListener(new Legend_jTextField_getURL_actionAdapter(this));
//        jTextField_getURL.setEnabled(false);
        jPanel1.add(jTextField_getURL, null);

//        int xLabel3 = 4*width_/6  ;
        int yLabel3 = hPanel1/3 -15 ;
        jLabel_Notes_.setLabelFor(jTextField_getURL);
        jLabel_Notes_.setText("Notes: ");
        jLabel_Notes_.setBounds(new Rectangle(xLabelURL, yLabel3, 175, 26));

        jLabel_Notes_.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel_Notes_.setHorizontalTextPosition(SwingConstants.LEFT);
        jPanel1.add(jLabel_Notes_, null);
        
        int xTextArea1 = xLabelURL, yTextArea1 = yLabel3+25;
        int wTextArea1 =300 , hTextArea1 =125 ;
        
        jTextArea_Notes_ = new JTextArea(5,20); // 2 int
        jTextArea_Notes_.setBounds(new Rectangle(xTextArea1, yTextArea1 , wTextArea1, hTextArea1));
        jPanel1.add(jTextArea_Notes_);

        int yLabel4 = 2*hPanel1/3 -35 ;
        jLabel_GeneID_.setLabelFor(jTextField_getURL);
        jLabel_GeneID_.setText("Gene ID: ");
        jLabel_GeneID_.setBounds(new Rectangle(xLabelURL, yLabel4, 175, 26));

        jLabel_GeneID_.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel_GeneID_.setHorizontalTextPosition(SwingConstants.LEFT);
        jPanel1.add(jLabel_GeneID_, null);
        
        int xTextArea2 = xLabelURL, yTextArea2 = yLabel4+25;
//        int wTextArea1 =300 , hTextArea1 =125 ;
        
        jTextArea_GeneID_ = new JTextArea(5,20); // 2 int
        jTextArea_GeneID_.setBounds(new Rectangle(xTextArea2, yTextArea2 , wTextArea1, hTextArea1));
        jPanel1.add(jTextArea_GeneID_);

        this.getContentPane().add(jPanel_background, null);
        this.setVisible(true);
        this.repaint();


	}
	


    //---------------------listener methods:-------------------------
    public void jTextField_getName_actionPerformed(ActionEvent e) {
    }

     public void jButton_cancel__actionPerformed(ActionEvent e) {

         this.dispose();
//         System.exit(1);
     }

     public void jButton_OK__actionPerformed(ActionEvent e) {
		 PubmedID_ = jTextField_getPubMEDID.getText();
		 URL_= jTextField_getURL.getText();
		 Notes_= jTextArea_Notes_.getText();
		 GeneID= jTextArea_GeneID_.getText();
    	 this.dispose();
     }

}
