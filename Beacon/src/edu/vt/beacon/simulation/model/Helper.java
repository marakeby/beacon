package edu.vt.beacon.simulation.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import edu.vt.beacon.simulation.model.containers.NetworkContainer;
import edu.vt.beacon.simulation.model.parser.FormulaParser;
import edu.vt.beacon.simulation.model.tree.BooleanTree;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;

public class Helper {

	public static NetworkContainer loadNetwork(String file, boolean simplify, boolean fromFile) throws Exception
	{
		BufferedReader f;
		if(fromFile)
		{
			File fi = new File(file);
			if (!fi.exists())
			{

				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("ViSiBooL");
				alert.setHeaderText("File could not be found!");
				alert.setContentText("Check if file was moved or deleted.");
				alert.showAndWait();
				
				return null;

				//alert.setContentText("Deleting this literal would cause an invalid transition function"); 
				
			}
			f = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
		}
		else
		{
			f = new BufferedReader(new StringReader(file));
		}
		Vector<String> lines = new Vector<String>();
		Vector<Double> uncertainties = new Vector<Double>();
	    Vector<String> varNames = new Vector<String>();
	    
		
			
	    
		String buf;
		buf = f.readLine();
		while(true)
		{
			if (buf == null)
				break;
	        
			// find the next non-empty line
			do
			{
				buf = f.readLine();
			}
			while ( (!(buf == null) && buf.length() == 0) || (!(buf == null) && buf.startsWith("#")));
	        
			if ((buf == null))
				break;
	        
			// find the first separator
			int breakPos = buf.indexOf(",");
			if (breakPos == 0){
				f.close();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Wrong file format");
				alert.setHeaderText("Parsing error in network file");
				alert.setContentText("Comma to seperate regulatory factor and its Boolean function is expected in each line");
				throw new Exception("Missing separator!");
			}
	        
			// find the uncertainty of the variables, if any.
	        // should be the last ',' that does not belong to the expression
			int uncertaintyPos = buf.lastIndexOf(",");
	        //cut part after last comma, trim it and check, if it is a number -> uncertainty expression
	        String uncertaintyExpression = buf.substring(uncertaintyPos+1,buf.length());
	        uncertaintyExpression = uncertaintyExpression.trim();
	        
			String geneName = buf.substring(0,breakPos);
			geneName = geneName.trim();
	        
			// read the transition function
			String logic;
			if (uncertaintyPos == breakPos || (isNumeric(uncertaintyExpression) && Double.parseDouble(uncertaintyExpression) == 0))
			{
				logic = buf.substring(breakPos+1);
				uncertainties.add(1.0);
			}
			else if(!isNumeric(uncertaintyExpression))
			{
				logic = buf.substring(breakPos + 1);
				uncertainties.add(1.0);
			}
			else
			{
				logic = buf.substring(breakPos+1,uncertaintyPos-breakPos-1);
	            uncertainties.add(1.0 - Double.parseDouble(uncertaintyExpression));
			}
	        
			// add the variable names and functions to lists
			varNames.add(geneName);
			lines.add(logic);
		}
		
		HashMap<Integer,String> varHash = new HashMap<Integer, String>();
		for(int i = 0; i < varNames.size(); i++)
		{
			varHash.put(i, varNames.get(i));
		}
	    
		FormulaParser p = new FormulaParser(varHash);
		Vector<BooleanTree> res = new Vector<BooleanTree>(); 
		
		//indice 
		HashMap<Integer,Integer> indiceMapper = new HashMap<Integer, Integer>();
		
		for(int i = 0; i < varNames.size(); i++)
		{
			indiceMapper.put(i, i);
		}
	    
		// parse the transition functions
		for (int i = 0; i < varNames.size(); ++i)
		{
			BooleanTree rule;

			rule = new BooleanTree(p.parse(lines.elementAt(i)), uncertainties.elementAt(i));
			rule.computeTemporalMemory(varNames.size(),indiceMapper);


			if (simplify)
				rule.simplify();
	        
			res.addElement(rule);
		}
		f.close();
		return new NetworkContainer(res, varNames, null);
	}
	
	
	public static String getConfigFile()
	{
		if(isWindows())
		{
			return System.getProperty("user.home") + File.separator + ".ViSiBooL" + File.separator +"recent.ini";
		}
		else if(isMac())
		{
			return System.getProperty("user.home") + File.separator + ".ViSiBooL" + File.separator + ".recent.ini";
		}
		else if(isUnix())
		{
			return System.getProperty("user.home") + File.separator + ".ViSiBooL" + File.separator + ".recent.ini";
		}
		else
			return null;
	}
	
	public static String getViSiBooLDirection()
	{
		if(isWindows())
		{
			return System.getProperty("user.home") + File.separator + ".ViSiBooL" + File.separator;
		}
		else if(isMac())
		{
			return System.getProperty("user.home") + File.separator + ".ViSiBooL" + File.separator;
		}
		else if(isUnix())
		{
			return System.getProperty("user.home") + File.separator + ".ViSiBooL" + File.separator;
		}
		else
			return null;
	}

	private static boolean isWindows(){
		return (System.getProperty("os.name").toLowerCase().indexOf("win")>=0);
	}

	private static boolean isMac(){
		return (System.getProperty("os.name").toLowerCase().indexOf("mac")>=0);
	}

	private static boolean isUnix() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("nix") >=0 || OS.indexOf("nux") >=0 || OS.indexOf("aix") >= 0);
	}

	@SuppressWarnings("unused")
	private static boolean isSolaris(){
		return (System.getProperty("os.name").toLowerCase().indexOf("sunos") >=0);
	}

	
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}

}
