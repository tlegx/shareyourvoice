package com.tlegx.shareyourvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFtask;


public class MainActivity extends AppCompatActivity {

    //Variables.
    private String type;
    private TextView audioText;
    private TextView imageText;
    private Button selAudio;
    private Button selImage;
    private Button makeVid;
    FloatingActionButton creditsButton;
    private int audioDuration;
    public CheckBox dontShowAgain;
    public CheckBox dontShowAgain1;
    public static final String PREFS_NAME = "MyPrefsFile1";
    private FFtask fFtask; //Initialize FFtask
    ProgressDialog progressDialog;
    String timenow;
    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    int PERMISSION_CODE_ALL = 1;
    private boolean audioSelected = false;
    private boolean imageSelected = false;
    String audioFilePath;
    String imageFilePath;

    //Boolean to check whether the app has got permissions or not.
    private boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize components.
        audioText = findViewById(R.id.audioText);
        imageText = findViewById(R.id.imageText);
        selAudio = findViewById(R.id.selAudio);
        selImage = findViewById(R.id.selImage);
        makeVid = findViewById(R.id.makeVid);
        creditsButton = findViewById(R.id.creditsButton);

        //Ignore Android 10+ night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Setting up onClickListener.
        selImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPermissions(getApplicationContext(), PERMISSIONS)){
                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater adbInflater = LayoutInflater.from(MainActivity.this);
                    View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    String skipMessage = settings.getString("skipMessage", "NOT checked");

                    dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
                    adb.setView(eulaLayout);
                    adb.setTitle("Attention!");
                    adb.setMessage(Html.fromHtml("Due to Android's restrictions, please open the 'Hamburger' menu, then choose 'Internal Storage' or 'SD Card/Secondary Storage, after that choose your desired file."));

                    adb.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String checkBoxResult = "NOT checked";
                            if (dontShowAgain.isChecked()){
                                checkBoxResult = "checked";
                            }

                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();

                            editor.putString("skipMessage", checkBoxResult);
                            editor.commit();

                            chooseImage();
                            dialogInterface.dismiss();

                            return;
                        }
                    });

                    if (!skipMessage.equals("checked")){
                        adb.show();
                    } else {
                        chooseImage();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(PERMISSIONS, PERMISSION_CODE_ALL);
                    } else {
                        requestStorePermissions();
                    }
                }
            }
        });

        selAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPermissions(getApplicationContext(), PERMISSIONS)){
                    AlertDialog.Builder adb1 = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater adbInflater1 = LayoutInflater.from(MainActivity.this);
                    View eulaLayout = adbInflater1.inflate(R.layout.checkbox, null);
                    SharedPreferences settings1 = getSharedPreferences(PREFS_NAME, 0);
                    String skipMessage = settings1.getString("skipMessage", "NOT checked");

                    dontShowAgain1 = (CheckBox) eulaLayout.findViewById(R.id.skip);
                    adb1.setView(eulaLayout);
                    adb1.setTitle("Attention!");
                    adb1.setMessage(Html.fromHtml("Due to Android's restrictions, please open the 'Hamburger' menu, then choose 'Internal Storage' or 'SD Card/Secondary Storage, after that choose your desired file."));

                    adb1.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String checkBoxResult1 = "NOT checked";
                            if (dontShowAgain1.isChecked()){
                                checkBoxResult1 = "checked";
                            }

                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();

                            editor.putString("skipMessage", checkBoxResult1);
                            editor.commit();

                            chooseAudio();
                            dialogInterface.dismiss();

                            return;
                        }
                    });

                    if (!skipMessage.equals("checked")){
                        adb1.show();
                    } else {
                        chooseAudio();
                    }


                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(PERMISSIONS, PERMISSION_CODE_ALL);
                    } else {
                        requestStorePermissions();
                    }
                }

            }
        });

        makeVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if the user has selected the required components. If not, warn the user. If selected, run FFmpeg to make video.
                if (audioSelected && imageSelected){
                    //Intent i = new Intent(getApplicationContext(), VideoPlay.class);
                    //i.putExtra("toActivity2", toActivity2);
                    //startActivity(i);

                    if (FFmpeg.getInstance(getApplicationContext()).isSupported()){
                        //Get date and time from the system
                        SimpleDateFormat format = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
                        Date now = new Date();
                        timenow = format.format(now);
                        String[] command = new String[]{"-loop", "1", "-i", imageFilePath, "-i", audioFilePath, "-b:a", "25k", "-b:v", "40k", "-shortest", "/storage/emulated/0/syv_video_" + timenow + ".avi"};
                        FFmpeg fFmpeg = FFmpeg.getInstance(getApplicationContext());
                        fFtask = fFmpeg.execute(command, new ExecuteBinaryResponseHandler(){
                            @Override
                            public void onStart(){
                                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                                metaRetriever.setDataSource(audioFilePath);
                                String out = "";
                                audioDuration = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                                Log.v("time1", String.valueOf(audioDuration));
                                metaRetriever.release();

                                progressDialog = new ProgressDialog(MainActivity.this);
                                progressDialog.setCancelable(false);
                                progressDialog.setTitle("Please wait");
                                progressDialog.setMessage("Now creating video, this might take a while...");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                progressDialog.setMax(100);
                                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        fFtask.sendQuitSignal();
                                        dialogInterface.dismiss();
                                    }
                                });
                                progressDialog.show();

                            }

                            @Override
                            public void onProgress(String message){
                                Log.d("progress", message);

                                //Calculate the percent of the FFmpeg process, then put it into ProgressDialog.
                                Pattern timePattern = Pattern.compile("(?<=time=)[\\d:.]*");
                                Scanner sc = new Scanner(message);


                                String match = sc.findWithinHorizon(timePattern, 0);
                                if (match != null) {
                                    String[] matchSplit = match.split(":");

                                    if (audioDuration != 0) {
                                        float progress = (Integer.parseInt(matchSplit[0]) * 3600 +
                                                Integer.parseInt(matchSplit[1]) * 60 +
                                                Float.parseFloat(matchSplit[2])) / audioDuration;
                                        float showProgress = (progress * 100000);
                                        Log.d("progress1", "=======PROGRESS======== " + showProgress);
                                        progressDialog.setProgress(Math.round(showProgress));

                                        if (progressDialog.getProgress() == progressDialog.getMax()){
                                            progressDialog.dismiss();
                                        }
                                    }
                                }
                            }

                            //If FFmpeg failed, notify the user.
                            @Override
                            public void onFailure(String message){
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Cannot make video, please try again later.", Toast.LENGTH_SHORT).show();
                                //Log.d("fail reason", message);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage(message);
                                builder.setTitle("Something went wrong...");
                                builder.setCancelable(true);
                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }

                            @Override
                            public void onSuccess(String message){
                                Toast.makeText(getApplicationContext(), "Done.", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), VideoPlay.class);
                                i.putExtra("toActivity2", timenow);
                                startActivity(i);
                            }

                            @Override
                            public void onFinish(){}
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, FFmpeg (the core feature) is not supported on your device.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!audioSelected){
                        audioText.setTextColor(Color.RED);
                    }
                    if (!imageSelected){
                        imageText.setTextColor(Color.RED);
                    }
                }
            }
        });

        creditsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Credits.class);
                startActivity(intent);
            }
        });

    }

    //Shows a File Chooser intent to select an image.
    private void chooseImage(){
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("image/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, 1);
    }

    //Shows a File Chooser intent to select an audio file.
    private void chooseAudio(){
        Intent chooseFile1 = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile1.setType("audio/*");
        chooseFile1 = Intent.createChooser(chooseFile1, "Choose a file");
        startActivityForResult(chooseFile1, 1);
    }

    //Method to check permissions for devices <= SDK level 23 (Android Marshmallow).
    public void requestStorePermissions(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, String.valueOf(PERMISSIONS))){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("We need access to your storage in order to make and store videos for you.")
                    .setPositiveButton("grant permission", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_CODE_ALL);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE_ALL);
        }
    }

    //Return the result for requestStorePermissions().
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if (requestCode == PERMISSION_CODE_ALL) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getApplicationContext(), "Storage Permission Denied, please enable it again to keep using the app.", Toast.LENGTH_SHORT).show();
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myAppSettings);
            }
        }
    }

    //When selected image/audio, proceed to get it's path from URI.
    protected void onActivityResult(int requestCode, int resultCode, Intent chooseFile) {
        super.onActivityResult(requestCode, resultCode, chooseFile);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            Uri content_describer = chooseFile.getData();
            String filename = content_describer.getLastPathSegment();
            Log.d("filename", filename);
            String repfilename = filename.replace("primary:", "");
            Log.d("Filename is", repfilename);
            String[] parts = filename.split(":");
            String identifier = parts[0];
            Log.d("therealpath0", identifier);
            if (filename.contains("primary:")){
                File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + repfilename);
                Log.d("Destination is", destination.toString());
                getMimeType(String.valueOf(destination));
                try{
                    Log.d("mimetype", type);
                } catch (NullPointerException e){
                    e.printStackTrace();
                    String exception = repfilename.substring(repfilename.lastIndexOf("."));
                    Log.d("exception", exception);
                    switch (exception){
                        case ".m4a": type = "audio";
                                    Log.d("exceptiontype", type);
                                    break;

                        case ".mp3": type = "audio";
                                    Log.d("exceptiontype", type);
                                    break;
                    }
                }
                if (type.contains("audio")){
                    audioText.setText("File chosen\n" + destination);
                    audioText.setTextColor(Color.GREEN);
                    audioSelected = true;
                    audioFilePath = String.valueOf(destination);
                } else {
                    imageText.setText("File chosen\n" + destination);
                    imageText.setTextColor(Color.GREEN);
                    imageSelected = true;
                    imageFilePath = String.valueOf(destination);
                }
            } else {
                repfilename = filename.replace(identifier + ":", "");
                identifier = "/storage/" + identifier;
                File f_secs = new File(identifier + "/" + repfilename);
                Log.d("therealpath1", String.valueOf(f_secs));
                getMimeType(String.valueOf(f_secs));
                try{
                    Log.d("mimetype", type);
                } catch (NullPointerException e){
                    e.printStackTrace();
                    String exception1 = repfilename.substring(repfilename.lastIndexOf("."));
                    Log.d("exception", exception1);
                    switch (exception1){
                        case ".m4a": type = "audio";
                            Log.d("exceptiontype", type);
                            break;
                    }
                }
                if (type.contains("audio")){
                    audioText.setText("File chosen\n" + f_secs);
                    audioText.setTextColor(Color.GREEN);
                    audioSelected = true;
                    audioFilePath = String.valueOf(f_secs);
                } else {
                    imageText.setText("File chosen\n" + f_secs);
                    imageText.setTextColor(Color.GREEN);
                    imageSelected = true;
                    imageFilePath = String.valueOf(f_secs);
                }
            }

        }
    }

    //Returns the MIME type of the selected file.
    public String getMimeType(String destination) {
        type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(destination);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
