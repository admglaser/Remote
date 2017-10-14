package model.json;

import java.util.List;

public class ImagesWrapper implements MessageWrapper {

	private List<Image> images;

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}
	
}
