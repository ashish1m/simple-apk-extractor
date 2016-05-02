package com.helloapp.myapkextractor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String filePath = null;
    String newFilePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filePath = getMyApk();
        newFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myApks";
        newFilePath = copyFile(filePath, newFilePath);
        ((Button)findViewById(R.id.btn_share)).setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (newFilePath != null) {
            deleteTempDir(new File(newFilePath));
        }
    }

    //    public String getAPK() {
//        String path = null;
//
//        PackageManager packageManager = getPackageManager();
//        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.SIGNATURE_MATCH);
//
//
//
//        if (isValid(packageInfoList)) {
//            for (PackageInfo packageInfo : packageInfoList) {
//                ApplicationInfo applicationInfo;
//
//                try {
//                    //applicationInfo = getApplicationInfoFrom(packageManager, packageInfo);
//
//                    String myPackage = getPackageName();
//                    applicationInfo = packageManager.getApplicationInfo(packageInfo.packageName, 0);
//                    String packageName = applicationInfo.packageName;
//                    String versionName = packageInfo.versionName;
//                    int versionCode = packageInfo.versionCode;
//                    if (packageName.equalsIgnoreCase(myPackage)) {
//                        File apkFile = new File(applicationInfo.publicSourceDir);
//                        if (apkFile.exists()) {
//                            //installedApkFilePaths.put(packageName, apkFile.getAbsolutePath());
//                            path = apkFile.getAbsolutePath();
//                            Log.e("MYTASK", packageName + " = " + apkFile.getName());
//                        }
//                    }
//
//                } catch (PackageManager.NameNotFoundException error) {
//                    error.printStackTrace();
//                }
//            }
//        }
//
//        return path;
//    }

//    private boolean isValid(List<PackageInfo> packageInfos) {
//        return packageInfos != null && !packageInfos.isEmpty();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_share:
                File file = new File(newFilePath);
                MimeTypeMap map = MimeTypeMap.getSingleton();
                String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                String type = map.getMimeTypeFromExtension(ext);

                if (type == null)
                    type = "*/*";

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                Uri fileUri = Uri.fromFile(file);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.setType(type);
                startActivity(Intent.createChooser(shareIntent, "Share App using:"));

                break;

            default:
                break;
        }
    }


    public String getMyApk(){
        String path = null;

        PackageManager packageManager = getPackageManager();
        String myPackage = getPackageName();
        ApplicationInfo applicationInfo;

        try {
            applicationInfo = packageManager.getApplicationInfo(myPackage, 0);
            File apkFile = new File(applicationInfo.publicSourceDir);
            if (apkFile.exists()) {
                //installedApkFilePaths.put(packageName, apkFile.getAbsolutePath());
                path = apkFile.getAbsolutePath();
                Log.e("MYTASK", myPackage + " = " + apkFile.getName());
                Log.e("MYTASK", path);
            }

        } catch (PackageManager.NameNotFoundException error) {
            error.printStackTrace();
        }

        return path;
    }

    private String copyFile(String inputPath, String outputPath) {
        String outputFilePath = outputPath + "/MyApkExtractor.apk";
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
                Log.e("MYTASK", "created");
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputFilePath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

        return outputFilePath;
    }


    private void deleteTempDir(File file) {
        if (file.exists()){
            File[] contents = file.listFiles();
            if (contents != null) {
                for (File f : contents) {
                    deleteTempDir(f);
                }
            }
            file.delete();
        }


    }
}
