package org.ngo.milestone;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class Parser {

	public static Revision Deserilize(String xml) {
		XMLDecoder decoder=null;
		try {
			decoder=new XMLDecoder(new BufferedInputStream(new StringBufferInputStream(xml)));
		} catch (Exception e) {
			System.out.println("ERROR: File dvd.xml not found");
		}
		Revision revision=(Revision)decoder.readObject();
		return revision;
	}
	

	private static void Serialize(Revision revision) {
		XMLEncoder encoder;
		try {
			encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("rev.xml")));
			encoder.writeObject(revision); 
			encoder.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * xStream version
	 * @param xml
	 * @return
	 */
	public static Revision Deserilize2(String xml) {
		XStream xstream = new XStream(new StaxDriver()); // does not require XPP3 library starting with Java 6
		xstream.alias("Revision", Revision.class);
		xstream.alias("File", SingleEntry.class);
				
		Revision revision = (Revision)xstream.fromXML(xml);
		return revision;
	}
	
	
	
	
	//https://x-stream.github.io/tutorial.html
	//https://x-stream.github.io/alias-tutorial.html
	private static String Serialize2(Revision revision) {
		XStream xstream = new XStream(new StaxDriver()); // does not require XPP3 library starting with Java 6
		
		//use alias for node name convert
		xstream.alias("Revision", Revision.class);
		xstream.alias("File", SingleEntry.class);
		
		String xml = xstream.toXML(revision);
		System.out.println(xml);
		return xml;
	}
	
	
	public static void main(String[] args) {
		
		List<SingleEntry> fileEntires = new ArrayList<SingleEntry>();
		SingleEntry se1 = new SingleEntry();
		se1.setId(1);
		se1.setTitle("file 1");
		se1.setPath("/WEB-INF/src/");
		se1.setFile("public class");
		se1.setContent("content");
		se1.setProject("sweb-a01-proj1");
		
		fileEntires.add(se1);
		
		SingleEntry se2 = new SingleEntry();
		se2.setId(2);
		se2.setTitle("file 2");
		se2.setPath("/WEB-INF/src/");
		se2.setFile("public class 2");
		se2.setContent("content");
		se2.setProject("sweb-a01-proj1");
		
		fileEntires.add(se2);
		
		
		Revision revision = new Revision();
		revision.setId(1);
		revision.setLinkId("1");
		revision.setRefId("1");
		revision.setStatus("A");
		revision.setFileEntries(fileEntires);
		
		String xml = Serialize2(revision);
		
		Revision revDes = Deserilize2(xml);
		
		System.out.println(revDes);
		
		
		Path xmlFile = Paths.get("D:/NGO/client/eide/src/workspace.1/NGO.EIDE/", "toObj.xml");

	    try {
	      byte[] revByty = Files.readAllBytes(xmlFile);

	      String revString = new String(revByty, "utf-8");
	      System.out.println(revString);
	      
	      Revision revDes2 = Deserilize2(revString);
	      System.out.println(revDes2);
			
	      
	    } catch (IOException e) {
	      System.out.println(e);
	    }
	    
		
		
		
	}

}
