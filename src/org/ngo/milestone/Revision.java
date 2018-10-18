package org.ngo.milestone;

public class Revision {
	private int ID;
	private String Title;
	private String Project;
	private String LinkID;
	private String RefID;
	private String Status;
	private java.util.List<SingleEntry>  Files;
	public int getId() {
		return ID;
	}
	public void setId(int id) {
		this.ID = id;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		this.Title = title;
	}
	public String getLinkId() {
		return LinkID;
	}
	public void setLinkId(String linkId) {
		this.LinkID = linkId;
	}
	public String getRefId() {
		return RefID;
	}
	public void setRefId(String refId) {
		this.RefID = refId;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		this.Status = status;
	}
	public java.util.List<SingleEntry> getFiles() {
		return Files;
	}
	public void setFileEntries(java.util.List<SingleEntry> fileEntries) {
		this.Files = fileEntries;
	}

	public String getProject() {
		return Project;
	}
	public void setProject(String project) {
		Project = project;
	}
	@Override
	public String toString() {
		return "Id="+this.ID+",project="+Project+",linkId="+this.LinkID+",refId="+this.RefID+",status="+this.Status+",Files="+this.Files.toString();
	}
}
