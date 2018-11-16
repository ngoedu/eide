package org.ngo.eide.commands;

//Command
public interface Command{
	
	public static final String EXIT = "$EXIT";
	public static final String ADDPROJ = "$ADDPROJ";
	public static final String MILESTONE = "$MILESTONE";
	public static final String TEST = "$TEST";
	public static final String MARK = "$MARK";
	public static final String CONSOLE = "$CONSOLE";
	
	
	public void execute(String message) throws Exception;
}