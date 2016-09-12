package delete;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaquinilloDelete {

	private static String line;
	private static String mem;
	private static String tableName;
	private static String columnName;
	private static String columnValue;
	private static boolean commentFlag = false;
	private static boolean comm = false;
	private static Map<String,List<List<String>>> daos = new HashMap<String,List<List<String>>>();
	private static List<List<String>> dao = new ArrayList<List<String>>();
	private static List<String> daoLine = new ArrayList<String>();
	private static List<String> lstKey = new ArrayList<String>();
	
	private static String patternInsert = "\\-?\\-?INSERT\\sINTO\\s(.*)\\s?\\(([A-Z_0-9]*),";
	private static Pattern regexpInsert = Pattern.compile(patternInsert);
	private static Matcher mInsert;
	
	private static String patternValues = "VALUES\\s?\\('?([0-9]*)'?,";
	private static Pattern regexpValues = Pattern.compile(patternValues);
	private static Matcher mValues;

	private static void Maquinillo(String[] args) throws IOException{
		/* ***** CHANGE ME  ****** */
		String route="C:\\Users\\jjcalderon_ext\\Desktop\\SCRIPTS_DAO\\";
		String fileIn = args[0];
	    String ext=".sql";
	    String fileOut=fileIn+" - DELETE";
	    /* *********************** */

	    InputStream fis = new FileInputStream(route+fileIn+ext);
	    InputStreamReader isr = new InputStreamReader(fis);
	    BufferedReader br = new BufferedReader(isr);
	    
		while ((line = br.readLine()) != null) {
	    	if (line.startsWith("*/"))
	    		commentFlag=false;
	    	if(!commentFlag){
	    		//Block init
	    		if (line.startsWith("----")){
	    			if(dao.size()>0){
	    				daos.put(mem, dao);
	    				lstKey.add(mem);
	    			}
	    			mem = line;
	    			dao = new ArrayList<List<String>>();
	    		}else{
	    			line=line.toUpperCase();
	    			comm=false;
	    			if(line.startsWith("--"))
	    				comm=true;
	    			//Find if it's a INSERT line with table name and column name. First column is taken as PK column
	    			mInsert = regexpInsert.matcher(line);
	    			if(mInsert.find()){
	    				tableName=mInsert.group(1);
	    				columnName=mInsert.group(2);
	    			}
	    			//Find if its a VALUES line with column VALUE. First value is taken as PK value
	    			mValues = regexpValues.matcher(line);
	    			if(mValues.find()){
	    				columnValue=mValues.group(1);
	    				daoLine = new ArrayList<String>();
	    				daoLine.add(tableName);
	    				daoLine.add(columnName);
	    				daoLine.add(columnValue);
	    				if (comm)
	    					daoLine.add("");	//Add another empty string so length is 4, so we know its a commented line
	    				dao.add(daoLine);
	    			}
	    		}
	    	}
	    	if (line.startsWith("/*"))
	    		commentFlag=true;
	    }
		daos.put(mem, dao);
		lstKey.add(mem);
		br.close();
		
		int count=0;
		OutputStream fos = new FileOutputStream(route+fileOut+ext);
		Collections.reverse(lstKey);
		
		for (Iterator<String> it = lstKey.iterator();it.hasNext();){
			String key = it.next();
			fos.write(("\n"+key+"\n").getBytes());
			List<List<String>> table = daos.get(key);
			Collections.reverse(table);
			for (List<String> tableStr : table){
				count++;
				if (tableStr.size()==4) //If thres 4th string it means its a commented line, so comment it
					fos.write(("-- DELETE FROM "+tableStr.get(0)+" WHERE "+tableStr.get(1)+" = "+tableStr.get(2)+";\n").getBytes());
				else
					fos.write(("DELETE FROM "+tableStr.get(0)+" WHERE "+tableStr.get(1)+" = "+tableStr.get(2)+";\n").getBytes());
			}
		}
		fos.close();
		System.out.println("--------------------");
		System.out.println(lstKey.size()+" blocks of sql");
		System.out.println(count+" sentences generated");
		System.out.println("--------------------");
	}
	
	
	public static void main(String[] args) throws IOException{
		Maquinillo(args);
	}
	
	

}
