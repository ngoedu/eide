package org.ngo.eide.milestones;

public class SingleEntry {
	
	private int Id;
	private String Title;
	private String Project;
	private String Path;
	private String Name;
	private String File;
	private String Content;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getPath() {
		return Path;
	}
	public void setPath(String path) {
		Path = path;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public String getProject() {
		return Project;
	}
	public void setProject(String project) {
		Project = project;
	}
	
	
	public String getFile() {
		return File;
	}
	public void setFile(String file) {
		File = file;
	}
	@Override
	public String toString() {
		return "Id="+Id+",Title="+Title+",Project="+Project+",Path="+Path+",File="+File+",Content="+Content;
	}

}
