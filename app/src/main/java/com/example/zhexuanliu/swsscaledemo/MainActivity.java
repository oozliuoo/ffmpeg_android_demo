package com.example.zhexuanliu.swsscaledemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Context mContext = null;

    private String LOG_TAG = "Eric";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mContext = getApplicationContext();

        TextView tv = (TextView) findViewById(R.id.test_text);

        tv.setText(stringFromNative());

        // set permission
        verifyStoragePermissions(MainActivity.this);

        AssetManager am = mContext.getAssets();
        InputStream is = null;

        // read input
        byte[] source = null;
        try {
            is = am.open("tulips_yuv422_inter_planar_qcif.yuv");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[0xFFFF];

            for (int len; (len = is.read(buffer)) != -1;)
                os.write(buffer, 0, len);

            os.flush();

            source = os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] dst = scaileImage(source, 176, 144, 352, 288, 8);

        // store file
        String filename = "scaled.yuv";
        String albumName = "yuvTest";
        File f = getAlbumStorageDir(albumName, filename);

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(f);
            outputStream.write(dst);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getAlbumStorageDir(String albumName, String filename) {
        // Get the directory for the user's public pictures directory.
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!dir.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName + "/" + filename);

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public native String stringFromNative();

    public native byte[] scaileImage(byte[] src, int src_width, int src_height, int dst_width, int dst_height, int frame_num);

    static {
        System.loadLibrary("demo_scaling");
        System.loadLibrary("avutil-55");
        System.loadLibrary("swscale-4");
    }
}
