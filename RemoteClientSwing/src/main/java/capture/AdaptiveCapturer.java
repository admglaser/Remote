package capture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import application.Application;
import main.Main;
import model.ImagePiece;
import model.Rectangle;
import network.ServerConnection;

public class AdaptiveCapturer implements Capturer, Runnable {

	private static final int VERTICAL_DIVISON = 8;
	private static final int HORIZONTAL_DIVISION = 16;
	
	private Logger logger = Logger.getLogger(AdaptiveCapturer.class);
	
	@Inject
	Application frame;

	@Inject
	ServerConnection serverConnection;

	private boolean isCapturing;
	private BufferedImage previousImage;
	private long frames;

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

	public void run() {
		isCapturing = true;
		frames = 0;
		while (isCapturing) {
			sendImage();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}

			frames++;
		}
	}

	private void sendImage() {
		try {
			BufferedImage image = new ImageCapturer().captureImage();
			if (previousImage == null || frames % 10 == 0) {
				serverConnection.sendImagePiece(getImagePiece(image));
			} else {
				for (ImagePiece imagePiece : getDifferentImagePieces(image, previousImage)) {
					serverConnection.sendImagePiece(imagePiece);
				}
				previousImage.flush();
			}
			previousImage = image;
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private ImagePiece getImagePiece(BufferedImage image) throws IOException {
		String encodedString = getImageString(image);
		return new ImagePiece(new Rectangle(0, 0, image.getWidth(), image.getHeight()), encodedString);
	}

	private List<ImagePiece> getDifferentImagePieces(BufferedImage image, BufferedImage previousImage) throws IOException {
		List<ImagePiece> list = new ArrayList<>();
		for (Rectangle area : new ImageComparator(image, previousImage, HORIZONTAL_DIVISION, VERTICAL_DIVISON).getDifferentAreas()) {
			BufferedImage subImage = image.getSubimage(area.getX(), area.getY(), area.getWidth(), area.getHeight());
			String encodedString = getImageString(subImage);
			list.add(new ImagePiece(area, encodedString));
		}
		return list;
	}

	private String getImageString(BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpeg", baos);
		byte[] byteArray = baos.toByteArray();
		String encodedString = Base64.getEncoder().encodeToString(byteArray);
		return encodedString;
	}

}
