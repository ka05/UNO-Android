package com.envative.uno.comms;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.envative.emoba.delegates.Callback;
import com.envative.emoba.services.EMWebService;
import com.envative.uno.comms.tools.BasicImageDownloader;
import com.envative.uno.models.User;
import com.envative.uno.widgets.ExifUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by clay on 5/29/16.
 */
public class UNOUtil {

    private static UNOUtil instance;
    private Context context;
    private final SharedPreferences sharedPref;
    public static String baseImageDirectory = "";

    private UNOUtil(Context context){
        this.context  = context;
        sharedPref = context.getSharedPreferences("UNOPref", Context.MODE_PRIVATE);
    }

    public static UNOUtil get(Context context) {
        if (instance == null) {
            instance = new UNOUtil(context.getApplicationContext());
        }

        if(baseImageDirectory.equals("")){
            baseImageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                    "Android"+ File.separator +"data"+ File.separator + context.getPackageName() + File.separator +
                    "files" + File.separator + "Pictures" + File.separator + "profile_images" + File.separator;
        }

        instance.context = context;
        return instance;
    }

    public Boolean checkLoggedIn(){
        boolean loggedIn = sharedPref.getBoolean("loggedIn", false);
//        Log.d("logged in::",loggedIn + "");
        EMWebService.loggedIn = loggedIn;

        if(loggedIn){
            getUser();
        }

        return loggedIn;
    }


    public void setLoggedIn(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("loggedIn", true);
        editor.apply();
        EMWebService.loggedIn = true;

        saveUser();
    }

    public void saveUser() {
        if(UNOAppState.currUser != null){
            SharedPreferences.Editor prefsEditor = sharedPref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(UNOAppState.currUser);
            prefsEditor.putString("user", json);
            prefsEditor.apply();
        }else{
            Log.d("ERROR: ","user obj is null");
        }
    }

    public void getUser() {
        String userInfoJsonString = sharedPref.getString("user", null);
        if(userInfoJsonString != null){
            UNOAppState.currUser = new Gson().fromJson(userInfoJsonString, User.class);
        }else{
            Log.d("Error: ", "User info not saved");
        }
    }

    public void setLoggedOut(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("loggedIn");
        editor.remove("user");
        editor.remove("sessionToken");
        editor.remove("lastViewedProjectId");
        editor.apply();

//        UNOAppState.getInstance().initialized = false; // for coming in from notification
        UNOAppState.getInstance().setLoggedOut();

//        updateNotificationCount(0);
        EMWebService.loggedIn = false;
    }

    public static ArrayList<String> buildUsersChallengedArray(JsonArray usersChallenged){
        ArrayList<String> usernames = new ArrayList<>();

        for(JsonElement user : usersChallenged){
            usernames.add(user.getAsJsonObject().get("username").getAsString());
        }

        return usernames;
    }


    public void saveImage(final Context context, final String url, final Callback callback){
        saveImage(context, url, "", false, callback);
    }

    /**
     * save an Image to the disk
     * @param url
     * @return
     */
    public void saveImage(final Context context, final String url, final String newFilename, final boolean fromFile, final Callback callback){
        Log.d("saveImage", "called");
        Assent.setActivity((android.app.Activity) context, (android.app.Activity)context);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("saveImage","build > LOLLIPOP_MR1");
            // Marshmallow+
            if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                Log.d("saveImage","does not have permission");
                // The if statement checks if the permission has already been granted before
                Assent.requestPermissions(new AssentCallback() {
                    @Override
                    public void onPermissionResult(PermissionResultSet result) {
                        // Permission granted or denied
                        Log.d("saveImage","granted permission");
                        handleSaveImage(url, newFilename, fromFile, callback);
                    }
                }, 69, Assent.WRITE_EXTERNAL_STORAGE);
            }else{
                Log.d("saveImage","has permission");
                handleSaveImage(url, newFilename, fromFile, callback);
            }
        }else{
            Log.d("saveImage","build < LOLLIPOP_MR1");
            handleSaveImage(url, newFilename, fromFile, callback);
        }
    }

    public void handleSaveImage(final String url, String newFilename, final boolean fromFile, final Callback callback){
        Log.d("handleSaveImage", "called");
        final String imageFilename = (newFilename.equals("")) ? getFilenameFromUrl(url) : newFilename;

        // if we arent copying a file we are downloading it
        if(!fromFile){
            Log.d("NEW FILENAME", imageFilename);
            // save image
            final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
                @Override
                public void onError(BasicImageDownloader.ImageError error) {
                    Log.d("Image save error", "Error code " + error.getErrorCode() + ": " + error.getMessage());
//                    Toast.makeText(context, "Error code " + error.getErrorCode() + ": " + error.getMessage(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                    if(callback != null) callback.callback("error");
                }

                @Override
                public void onProgressChange(int percent) {
                }

                @Override
                public void onComplete(Bitmap result) {
                    final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG; // format - file ext

                    // PATH: // Path = /Android/data/com.envative.uno/profile_images/<filename>.jpg
                    String imageFilenamePath = baseImageDirectory + imageFilename + "." + mFormat.name().toLowerCase();

                    final File myImageFile = new File(imageFilenamePath);

                    BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                        @Override
                        public void onBitmapSaved() {
                            Log.d("Saved", "Image saved as: " + myImageFile.getAbsolutePath());
                            if(callback != null) callback.callback(myImageFile.getAbsolutePath());
                        }

                        @Override
                        public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                            Log.d("Error", "Error code " + error.getErrorCode() + ": " + error.getMessage());
                            error.printStackTrace();
                            if(callback != null) callback.callback("error");
                        }

                    }, mFormat, false);
                }
            });
            downloader.download(UNOAppState.devURL + url, true);
        }else{
            Log.d("saveFile", "renaming");
            final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
            // we are copying a file
            File from = new File(baseImageDirectory + imageFilename + "." + mFormat.name().toLowerCase());
            File to = new File(baseImageDirectory + newFilename + "." + mFormat.name().toLowerCase());
            final String toPath = to.getPath();
            // check file we are renaming it to exists - if so delete it so this can be renamed to it
            if(to.exists()){
                to.delete();
            }

            from.renameTo(to);

//            FileOutputStream fStream = null;
//            try {
//                fStream = new FileOutputStream(to, false);
//                byte[] myBytes = convertFileToByteArray(from);
//                fStream.write(myBytes);
//                fStream.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


//            BasicImageDownloader.writeToDisk(to, BitmapFactory.decodeFile(from.getPath()), new BasicImageDownloader.OnBitmapSaveListener() {
//                @Override
//                public void onBitmapSaved() {
//                    Log.d("Saved", "Image saved as: " + toPath);
//                    if(callback != null) callback.callback(toPath);
//                }
//
//                @Override
//                public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
//                    Log.d("Error", "Error code " + error.getErrorCode() + ": " + error.getMessage());
//                    error.printStackTrace();
//                    if(callback != null) callback.callback("error");
//                }
//
//            }, mFormat, false);

            if(callback != null) callback.callback(to.getPath());
        }
    }

    public static byte[] convertFileToByteArray(File f) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024*8];
            int bytesRead =0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public String getFilenameFromUrl(String url){

        // get name after all "/" in URI
        String[] filenameArr = url.split("/");
        String filenameWithExt = filenameArr[filenameArr.length - 1];

        filenameWithExt = filenameWithExt.toLowerCase();
        filenameWithExt = filenameWithExt.replace(".jpg", "");
        filenameWithExt = filenameWithExt.replace(".jpeg", "");
        filenameWithExt = filenameWithExt.replace(".png", "");
        filenameWithExt = filenameWithExt.replace(" ", "\\ ");

        return filenameWithExt;
    }

    public void processImage(File photoFile, Callback callback) {
        String path = photoFile.getPath();

        try {
            // handle resizing
            // first get bitmap of image
            Bitmap image = BitmapFactory.decodeFile(photoFile.getPath());

            // crop image

            /**
             *
             * In landscape to start off
             * ---------------
             * -             -
             * -             -
             * ---------------
             */

            double newImageWith = image.getHeight();
            int leftRightPadding = (image.getWidth() - (int)newImageWith) / 2;
            image = Bitmap.createBitmap(image, leftRightPadding, 0, image.getHeight(), image.getHeight());

            // scale it
            image = Bitmap.createScaledBitmap(image, 400, 400, false);

            // rotate image : pre rotate so we dont have to when getting it back
            image = ExifUtil.rotateBitmap(photoFile.getPath(), image);

            // compress for save
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

            // save it to file
            FileOutputStream fo = new FileOutputStream(photoFile);
            fo.write(bytes.toByteArray());
            fo.close();

            callback.callback(true);
        } catch (IOException e) {
            e.printStackTrace();
            callback.callback(false);
        }
    }

    /*
    public void savePhoto(File photoFile) {
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("profilePhoto", photoFile.getPath());
        editor.apply();
    }

    public String getProfilePhoto(){
        return sharedPref.getString("profilePhoto", "");
    }
    */
}
