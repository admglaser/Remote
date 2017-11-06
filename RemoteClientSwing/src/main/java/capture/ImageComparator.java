package capture;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import model.Rectangle;

public class ImageComparator {

	private BufferedImage firstImage;
	private BufferedImage secondImage;
	protected int horizontalDivision;
	protected int verticalDivison;
	protected int width;
	protected int height;
	
	public ImageComparator(BufferedImage firstImage, BufferedImage secondImage, int horizontalDivision, int verticalDivison) {
		this.firstImage = firstImage;
		this.secondImage = secondImage;
		this.horizontalDivision = horizontalDivision;
		this.verticalDivison = verticalDivison;
		this.width = firstImage.getWidth();
		this.height = firstImage.getHeight();
		checkSizes();
		checkDivision();
	}

	private void checkDivision() {
		if (horizontalDivision < 1 || verticalDivison < 1) {
			throw new IllegalArgumentException("Division number cannot be zero or negative.");
		}
	}

	private void checkSizes() {
		if (firstImage.getWidth() != secondImage.getWidth() || firstImage.getHeight() != secondImage.getHeight()) {
			throw new DifferentSizeException();
		}
	}

	public List<Rectangle> getDifferentAreas() {
		List<Rectangle> list = new ArrayList<>();

		List<Rectangle> rectangles = getRectangles();

		ExecutorService executorService = Executors.newFixedThreadPool(4);
		List<Callable<Boolean>> callables = new ArrayList<>();
		for (int i = 0; i < rectangles.size(); i++) {
			Rectangle rectangle = rectangles.get(i);
			callables.add(() -> isDifferent(rectangle));
		}
		try {
			List<Future<Boolean>> futures = executorService.invokeAll(callables);
			for (int i = 0; i < rectangles.size(); i++) {
				if (futures.get(i).get().booleanValue()) {
					list.add(rectangles.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		executorService.shutdown();
		return list;
	}

	protected boolean isDifferent(Rectangle rectangle) {
		for (int i = rectangle.getX(); i < rectangle.getX() + rectangle.getWidth(); i++) {
			for (int j = rectangle.getY(); j < rectangle.getY() + rectangle.getHeight(); j++) {
				try {
					if (firstImage.getRGB(i, j) != secondImage.getRGB(i, j)) {
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	protected List<Rectangle> getRectangles() {
		List<Rectangle> rectangles = new ArrayList<>();
		int xStep = width / horizontalDivision;
		int yStep = height / verticalDivison;
		for (int i = 0; i < horizontalDivision; i++) {
			for (int j = 0; j < verticalDivison; j++) {
				int xStart = i * xStep;
				int yStart = j * yStep;
				int w = Math.min(width - xStart, xStep);
				int h = Math.min(height - yStart, yStep);
				Rectangle rectangle = new Rectangle(xStart, yStart, w, h);
				rectangles.add(rectangle);
			}
		}
		return rectangles;
	}
	
	@SuppressWarnings("serial")
	class DifferentSizeException extends RuntimeException {
		
		public DifferentSizeException() {
			super("Image sizes are different.");
		}
		
	}

}
