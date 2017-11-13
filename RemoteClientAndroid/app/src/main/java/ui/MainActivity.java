package ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import remote.aut.bme.hu.remote.R;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViewPager();
        requestFilePermission();
        configureLogging();
    }

    private void configureLogging() {
        String logFilePath = new File(getBaseContext().getExternalFilesDir(null), "log.txt").getAbsolutePath();
        String filePattern = "%d - [%c] - %p : %m%n";
        int maxBackupSize = 10;
        long maxFileSize = 1024 * 1024;

        LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(logFilePath);
        logConfigurator.setFilePattern(filePattern);
        logConfigurator.setMaxBackupSize(maxBackupSize);
        logConfigurator.setMaxFileSize(maxFileSize);
        logConfigurator.configure();
    }

    private void requestFilePermission() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void setViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mTabLayout.setupWithViewPager(mViewPager);
    }

}
