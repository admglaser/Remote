package application;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;

import java.io.OutputStream;

import main.Main;
import model.json.KeyEvent;
import model.json.MouseClick;
import remote.aut.bme.hu.remote.R;

public class AndroidApplication implements Application {

    @Override
    public void showMessage(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Main.instance.getBaseContext());
        builder.setSmallIcon(R.drawable.tray);
        builder.setContentTitle("Remote");
        builder.setContentText(message);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        NotificationManager mNotificationManager = (NotificationManager) Main.instance.getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1234, builder.build());
    }

    @Override
    public void mouseClick(MouseClick mouseClick) {
        try {
            Process sh = Runtime.getRuntime().exec("su");
            OutputStream os = sh.getOutputStream();
            os.write(String.format("input tap %d %d", mouseClick.getX(), mouseClick.getY()).getBytes("ASCII"));
            os.flush();
            os.close();
            sh.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyEvent(KeyEvent keyEvent) {
        String character = keyEvent.getCharacter();
        if (keyEvent.getType() == KeyEvent.TYPE_KEYPRESS && character.length() == 1) {
            try {
                Process sh = Runtime.getRuntime().exec("su");
                OutputStream os = sh.getOutputStream();
                os.write(String.format("input text %s", character).getBytes("ASCII"));
                os.flush();
                os.close();
                sh.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getDeviceWidth() {
        Point size = new Point();
        WindowManager windowManager = (WindowManager) Main.instance.getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealSize(size);
        return size.x;
    }

    @Override
    public int getDeviceHeight() {
        Point size = new Point();
        WindowManager windowManager = (WindowManager) Main.instance.getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealSize(size);
        return size.y;
    }

    @Override
    public String getDeviceName() {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        return myDevice.getName();
    }

}
