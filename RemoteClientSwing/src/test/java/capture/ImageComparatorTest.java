package capture;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;

import capture.ImageComparator.DifferentSizeException;
import model.Rectangle;

public class ImageComparatorTest {

	private static BufferedImage image_801_601;
	private static BufferedImage image_800_600_empty;
	private static BufferedImage image_800_600_changes;
	private static BufferedImage image_800_600_empty_copy;

	@BeforeClass
	public static void setup() throws IOException {
		image_801_601 = ImageIO.read(new File("src/test/resources/image_801_601.png"));
		image_800_600_empty = ImageIO.read(new File("src/test/resources/image_800_600_empty.png"));
		image_800_600_changes = ImageIO.read(new File("src/test/resources/image_800_600_changes.png"));
		image_800_600_empty_copy = ImageIO.read(new File("src/test/resources/image_800_600_empty.png"));
	}

	@Test(expected = DifferentSizeException.class)
	public void differentSizeException() {
		new ImageComparator(image_801_601, image_800_600_empty, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void illegalArgumentExceptionForDivision() {
		new ImageComparator(image_800_600_empty, image_800_600_changes, 0, 0);
	}

	@Test
	public void getRectangles() {
		int horizontalDivision = 8;
		int verticalDivison = 6;
		ImageComparator imageComparator = new ImageComparator(image_800_600_empty, image_800_600_changes,
				horizontalDivision, verticalDivison);

		List<Rectangle> rectangles = imageComparator.getRectangles();

		assertEquals(horizontalDivision * verticalDivison, rectangles.size());
	}

	@Test
	public void isNotDifferent() {
		ImageComparator imageComparator = new ImageComparator(image_800_600_empty, image_800_600_empty_copy, 1, 1);

		Rectangle rectangle = new Rectangle(0, 0, image_800_600_empty.getWidth(), image_800_600_empty.getHeight());

		assertEquals(false, imageComparator.isDifferent(rectangle));
	}

	@Test
	public void isDifferent() {
		ImageComparator imageComparator = new ImageComparator(image_800_600_empty, image_800_600_changes, 1, 1);

		Rectangle rectangle = new Rectangle(0, 0, image_800_600_empty.getWidth(), image_800_600_changes.getHeight());

		assertEquals(true, imageComparator.isDifferent(rectangle));
	}

	@Test
	public void getDifferentAreas() {
		ImageComparator imageComparator = new ImageComparator(image_800_600_empty, image_800_600_changes, 4, 2);

		List<Rectangle> differentAreas = imageComparator.getDifferentAreas();

		assertEquals(4, differentAreas.size());
		
		assertEquals(0, differentAreas.get(0).getX());
		assertEquals(0, differentAreas.get(0).getY());
		assertEquals(200, differentAreas.get(0).getWidth());
		assertEquals(300, differentAreas.get(0).getHeight());
		
		assertEquals(400, differentAreas.get(1).getX());
		assertEquals(0, differentAreas.get(1).getY());
		assertEquals(200, differentAreas.get(1).getWidth());
		assertEquals(300, differentAreas.get(1).getHeight());
		
		assertEquals(400, differentAreas.get(2).getX());
		assertEquals(300, differentAreas.get(2).getY());
		assertEquals(200, differentAreas.get(2).getWidth());
		assertEquals(300, differentAreas.get(2).getHeight());
		
		assertEquals(600, differentAreas.get(3).getX());
		assertEquals(300, differentAreas.get(3).getY());
		assertEquals(200, differentAreas.get(3).getWidth());
		assertEquals(300, differentAreas.get(3).getHeight());
	}

}
