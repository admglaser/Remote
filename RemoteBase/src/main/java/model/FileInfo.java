package model;

public class FileInfo {

	private String name;
	private String path;
	private String parentPath;
	private boolean directory;
	private long size;

	public FileInfo() {
		
	}
	
	public FileInfo(String name, String path, String parentPath, boolean directory, long size) {
		this.name = name;
		this.path = path;
		this.parentPath = parentPath;
		this.directory = directory;
		this.size = size;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
