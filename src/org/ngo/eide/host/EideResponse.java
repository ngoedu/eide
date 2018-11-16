package org.ngo.eide.host;

public class EideResponse {
	public static EideResponse EIDE_NOT_READY = new EideResponse("INIT", "NOK", 1, "EIDE is not ready");
	
	private String internalMessage;
	
	public EideResponse(String action, String status, int natId, String message) {
		this.internalMessage = String.format("<?xml version='1.0' encoding='utf-8'?><EideResponse xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema'><action>%s</action><status>%s</status><natid>%s</natid> <message><![CDATA[%s]]></message></EideResponse>",action, status, natId, message);
		
	}
	
	@Override
	public String toString() {
		return this.internalMessage;
	}
}
