package org.ngo.eide.host;

public class EideResponse {
	public static EideResponse EIDE_NOT_READY = new EideResponse("INIT", "NOK","EIDE is not ready");
	
	private String internalMessage;
	
	public EideResponse(String action, String status, String message) {
		this.internalMessage = String.format("<EIDE action={0} status='{1}' message='{2}'/>",action, status, message);
		
	}
	
	@Override
	public String toString() {
		return this.internalMessage;
	}
}
