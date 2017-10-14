package capture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import application.Application;
import main.Main;
import model.ImagePiece;
import model.Rectangle;
import network.ServerConnection;

public class AdaptiveCapturer implements Capturer, Runnable {

	@Inject
	Application frame;

	@Inject
	ServerConnection serverConnection;

	private static final int X_DIV = 16;
	private static final int Y_DIV = 8;
	private static int FRAMES_PER_SECOND = -1;

	private boolean isCapturing;

	private BufferedImage previousImage;

	public AdaptiveCapturer() {
		Main.injector.inject(this);
	}

	@Override
	public void startCapture() {
		frame.showMessage("Capturing started.");
		new Thread(this).start();
	}

	@Override
	public void stopCapture() {
		if (isCapturing) {
			frame.showMessage("Capturing stopped.");
			isCapturing = false;
		}
	}

	@SuppressWarnings("unused")
	public void run() {
		int sleepMS = 1000 / FRAMES_PER_SECOND;
		isCapturing = true;

		long startTime = System.currentTimeMillis();
		long framesCount = 0;
		int fps = 0;

		while (isCapturing) {
			long t = System.currentTimeMillis();
			sendImage();
			long time = System.currentTimeMillis() - t;
//			System.out.println("Took: " + time);

			if (FRAMES_PER_SECOND != -1) {
				long toSleep = sleepMS - time;
				if (toSleep > 0) {
					try {
						Thread.sleep(toSleep);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (System.currentTimeMillis() - startTime > 1000) {
				startTime = System.currentTimeMillis();
				fps = (int) framesCount;
				framesCount = 0;
			}
			framesCount++;
//			System.out.println("fps: " + fps);
		}
	}

	private void sendImage() {
		try {
			BufferedImage currentImage = ImageUtils.getCapturedImage();
			if (previousImage == null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(currentImage, "jpeg", baos);
				byte[] byteArray = baos.toByteArray();
				String encodedString = Base64.getEncoder().encodeToString(byteArray);
				ImagePiece imagePiece = new ImagePiece(
						new Rectangle(0, 0, currentImage.getWidth(), currentImage.getHeight()), encodedString);
				serverConnection.sendImagePiece(imagePiece);
			} else {
				List<Rectangle> areas = getDifferenceAreas(currentImage, previousImage, X_DIV, Y_DIV);
				for (Rectangle area : areas) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					BufferedImage subimage = currentImage.getSubimage(area.getX(), area.getY(), area.getWidth(), area.getHeight());
					ImageIO.write(subimage, "jpeg", baos);
					byte[] byteArray = baos.toByteArray();
					String encodedString = Base64.getEncoder().encodeToString(byteArray);
					ImagePiece imagePiece = new ImagePiece(area, encodedString);
					serverConnection.sendImagePiece(imagePiece);
				}
				previousImage.flush();
			}
			previousImage = currentImage;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<Rectangle> getDifferenceAreas(BufferedImage currentImage, BufferedImage previousImage, int xDiv,
			int yDiv) {
		List<Rectangle> list = new ArrayList<>();
		int width = currentImage.getWidth();
		int height = currentImage.getHeight();
		if (width != previousImage.getWidth() || height != previousImage.getHeight()) {
			throw new RuntimeException("Image sizes are different.");
		}
		List<Rectangle> rectangles = getRectangles(width, height, xDiv, yDiv);

		ExecutorService executorService = Executors.newFixedThreadPool(4);
		List<Callable<Boolean>> callables = new ArrayList<>();
		for (int i = 0; i < rectangles.size(); i++) {
			Rectangle rectangle = rectangles.get(i);
			callables.add(() -> isDifferent(currentImage, previousImage, rectangle));
		}
		try {
//			long t = System.currentTimeMillis();
			List<Future<Boolean>> futures = executorService.invokeAll(callables);
//			System.out.println("isDifferent ========== " + (System.currentTimeMillis() - t));
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

	private boolean isDifferent(BufferedImage currentImage, BufferedImage previousImage, Rectangle rectangle) {
		for (int i = rectangle.getX(); i < rectangle.getX() + rectangle.getWidth(); i++) {
			for (int j = rectangle.getY(); j < rectangle.getY() + rectangle.getHeight(); j++) {
				try {
					if (currentImage.getRGB(i, j) != previousImage.getRGB(i, j)) {
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	private static List<Rectangle> getRectangles(int width, int height, int xDiv, int yDiv) {
		List<Rectangle> rectangles = new ArrayList<>();
		int xStep = width / xDiv;
		int yStep = height / yDiv;
		for (int i = 0; i < xDiv; i++) {
			for (int j = 0; j < yDiv; j++) {
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

}
