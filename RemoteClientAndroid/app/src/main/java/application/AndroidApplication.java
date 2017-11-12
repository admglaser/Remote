package application;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.Main;
import model.FileInfo;
import model.message.KeyEvent;
import model.message.MouseClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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

    @Override
    public List<FileInfo> listFileInfos(String path) {
        List<FileInfo> files = new ArrayList<>();
        if (path == null) {
            return listFileInfos("/sdcard/");
        } else {
            File root = new File(path);
            if (root.exists() && root.isDirectory()) {
                for (java.io.File f : root.listFiles()) {
                    FileInfo file = new FileInfo(f.getName(), f.getPath(), f.getParent(), f.isDirectory(), f.isDirectory() ? 0 : f.length());
                    files.add(file);
                }
            }
            Collections.sort(files, new Comparator<FileInfo>() {
                @Override
                public int compare(FileInfo f1, FileInfo f2) {
                    if (f1.isDirectory() && !f2.isDirectory()) {
                        return -1;
                    }
                    if (f2.isDirectory() && !f1.isDirectory()) {
                        return 1;
                    }
                    return f1.getName().compareTo(f2.getName());
                }
            });
        }
        return files;
    }

    @Override
    public String getParentPath(String path) {
        if (path != null) {
            File file = new File(path);
            if (file != null && file.exists()) {
                return file.getParent();
            }
        }
        return null;
    }

    @Override
    public java.io.File getFile(String path, String name) {
        File file = new File(path + "/" + name);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    @Override
    public String uploadFile(String url, File file) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("text/csv"), file))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        }
        return null;
    }

}
