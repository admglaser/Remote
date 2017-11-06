package capture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

public class ImageCapturer {

    private static final String IMAGE_PATH = "/data/local/screen.jpg";

    public Bitmap getCapturedImage() {
        File file = getCapturedImageFile();
        if (file != null) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null;
    }

    public byte[] getCapturedImageBytes() {
        File file = getCapturedImageFile();
        if (file != null) {
            int size = (int) file.length();
            try {
                byte[] bytes = new byte[size];
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
                return bytes;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private File getCapturedImageFile() {
        File file = null;
        String path = IMAGE_PATH;
        long t = System.currentTimeMillis();
        try {

            Process sh = Runtime.getRuntime().exec("su");
            OutputStream os = sh.getOutputStream();
            os.write(String.format("/system/bin/screencap -p %s",path).getBytes("ASCII"));
            os.flush();
            os.close();
            sh.waitFor();

            file = new File(path);
            if (file.exists()) {
                sh = Runtime.getRuntime().exec("su");
                os = sh.getOutputStream();
                os.write(String.format("/system/bin/chmod 777 %s", path).getBytes("ASCII"));
                os.flush();
                os.close();
                sh.waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("getCapturedImageFile: " + (System.currentTimeMillis()-t));
        return file;
    }

}
