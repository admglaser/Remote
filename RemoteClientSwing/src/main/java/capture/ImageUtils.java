package capture;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtils {

	public static BufferedImage getCapturedImage() throws AWTException {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = new Robot().createScreenCapture(screenRect);
		return capture;
	}

	public static void write(BufferedImage image, float quality, OutputStream out) throws IOException {
		Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpg");
		if (!writers.hasNext())
			throw new IllegalStateException("No writers found");
		ImageWriter writer = (ImageWriter) writers.next();
		ImageOutputStream ios = ImageIO.createImageOutputStream(out);
		writer.setOutput(ios);
		ImageWriteParam param = writer.getDefaultWriteParam();
		if (quality >= 0) {
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
		}
		writer.write(null, new IIOImage(image, null, null), param);
	}

	public static byte[] getBytes(BufferedImage image, float quality) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream(50000);
			write(image, quality, out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedImage compress(BufferedImage image, float quality) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write(image, quality, out);
			return ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] getBytes(BufferedImage image) {
		int[] ints = ((DataBufferInt) image.getData().getDataBuffer()).getData();
		byte[] datas = new byte[ints.length];
		for (int i = 0; i < ints.length; i++) {
			datas[i] = Byte.valueOf(datas[i]);
		}
		return datas;
	}

}
