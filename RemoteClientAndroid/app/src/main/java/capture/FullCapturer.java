package capture;

import android.util.Base64;

import javax.inject.Inject;

import application.Application;
import main.Main;
import model.ImagePiece;
import model.Rectangle;
import network.ServerConnection;

public class FullCapturer implements Capturer, Runnable {

    @Inject
    Application application;

    @Inject
    ServerConnection serverConnection;

    private boolean isCapturing;

    public FullCapturer() {
        Main.injector.inject(this);
    }

    @Override
    public void startCapture() {
        application.showMessage("Capturing started.");
        new Thread(this).start();
    }

    @Override
    public void stopCapture() {
        if (isCapturing) {
            application.showMessage("Capturing stopped.");
            isCapturing = false;
        }
    }

    @Override
    public void run() {
        isCapturing = true;
        while (isCapturing) {
            sendImage();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendImage() {
        try {
            byte[] byteArray = new ImageCapturer().getCapturedImageBytes();
            String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Rectangle rect = new Rectangle(0, 0, application.getDeviceWidth(), application.getDeviceHeight());
            ImagePiece imagePiece = new ImagePiece(rect, encodedString);
            serverConnection.sendImagePiece(imagePiece);
        } catch (Exception e) {
            application.showMessage("Error sending image.");
        }
    }

}
