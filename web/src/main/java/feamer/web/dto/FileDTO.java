package feamer.web.dto;

public class FileDTO {
	
	String id; 
	String name;
	String relatedUser;
	byte[] file;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRelatedUser() {
		return relatedUser;
	}
	public void setRelatedUser(String relatedUser) {
		this.relatedUser = relatedUser;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}

}
