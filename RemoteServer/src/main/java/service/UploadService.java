package service;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.fileupload.disk.DiskFileItem;

@Startup
@Singleton
public class UploadService {

	private Map<String, DiskFileItem> diskFileItemsByName;
	
	public UploadService() {
		diskFileItemsByName = new HashMap<>();
	}

	public void add(DiskFileItem diskFileItem) {
		diskFileItemsByName.put(diskFileItem.getStoreLocation().getName(), diskFileItem);
	}
	
	public DiskFileItem get(String name) {
		DiskFileItem diskFileItem = diskFileItemsByName.get(name);
		return diskFileItem;
	}
	
}
