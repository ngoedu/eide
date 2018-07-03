package org.ngo.milestone;

public class Parser {

	public static MileStoneEntry parseMileStone(String content) {
		if (content == null || content.trim().length() ==0)
			return null;
		
		MileStoneEntry entry = new MileStoneEntry();
		String[] items = content.split(NEWLINE);
		String path;
		for(int i=0; i<items.length;i++) {
			if (items[i].startsWith("M.MSID")) {
				entry.setId(Integer.parseInt(items[i].replace("M.MSID=", "")));
			} else if (items[i].startsWith("M.MSTitle")) {
				entry.setTitle(items[i].replace("M.MSTitle=", ""));
			} else if (items[i].startsWith("M.MSPath")) {
				path = items[i].replace("M.MSPath=", "");
				entry.setPath(path.split("!")[1]);
				entry.setProject(path.split("!")[0]);
				entry.setFile(path.split("!")[2]);
				
			} else if (items[i].startsWith("M.MSContent")) {
				entry.setContent(items[i].replace("M.MSContent=", ""));
			}
		}
		return entry;				
	}
	
	public static String NEWLINE = "_R_N_";
	
	public static void main(String[] args) {
		String message = "M.MSID=1_R_N_M.MSTitle=How to work with Html_R_N_M.MSPath=sweb-a1!src!ngo.java_R_N_M.MSContent=public class Console{}";
		MileStoneEntry entry = Parser.parseMileStone(message);
		System.out.println(entry);
		
	}

}
