package com.denysbsb.cordova;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;
import android.util.Log;




public class FileBrowser extends CordovaPlugin {

    String [] permissions = { Manifest.permission.READ_EXTERNAL_STORAGE};
    String [] fileType;
    CallbackContext _callbackContext;
    JSONArray listFile;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        _callbackContext = callbackContext;
        listFile = args;
        CordovaPlugin cordova = this;

       cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {                
                if(action.equals("getPermission"))
                {
                    if(hasPermisssion())
                    {
                        PluginResult r = new PluginResult(PluginResult.Status.OK);
                        _callbackContext.sendPluginResult(r);
                        return true;
                    }
                    else {
                        PermissionHelper.requestPermissions(cordova, 0, permissions);
                    }
                }else if(action.equals("browse")){
                    runQuery();
                }
            }
        });    

        return true;
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException
    {
        PluginResult result;
        //This is important if we're using Cordova without using Cordova, but we have the geolocation plugin installed
        if(_callbackContext != null) {
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_DENIED) {
                    result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
                    _callbackContext.sendPluginResult(result);
                    return;
                }

            }
            result = new PluginResult(PluginResult.Status.OK);
            _callbackContext.sendPluginResult(result);
        }
    }

    public boolean hasPermisssion() {
        for(String p : permissions)
        {
            if(!PermissionHelper.hasPermission(this, p))
            {
                return false;
            }
        }
        return true;
    }

    /*
     * We override this so that we can access the permissions variable, which no longer exists in
     * the parent class, since we can't initialize it reliably in the constructor!
     */

    public void requestPermissions(int requestCode)
    {
        PermissionHelper.requestPermissions(this, requestCode, permissions);
    }

    private void runQuery(){
        System.out.println("Rodando Query");
        JSONObject data=new JSONObject();
        JSONArray resArray=new JSONArray();
        Cursor cursor=null;
        String baseUri="";
        String type = "file";

        if(type.equals("image")) {
            String str[] = {
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.SIZE
                };
            cursor = cordova.getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, str,
                    null, null, null);
            baseUri="content://media/external/images/media/";
        }
        else if(type.equals("audio")){
            String str[] = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.SIZE,
                };
            cursor = cordova.getActivity().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, str,
                    null, null, null);
            baseUri="content://media/external/audio/media/";
        }else if(type.equals("video")){
            String str[] = {
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.SIZE,
                };
            cursor = cordova.getActivity().getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, str,
                    null, null, null);
            baseUri="content://media/external/video/media/";
        }else if(type.equals("file")){

            System.out.println("TIPO:FILE");
            
            Uri uri = MediaStore.Files.getContentUri("external");
            
            String str[] = {
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.SIZE
            };

            String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";

            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");

            String[] selectionArgsPdf = new String[]{ mimeType };

            cursor = cordova.getActivity().getContentResolver().query(uri, str, selectionMimeType, selectionArgsPdf, null);

            baseUri="content://media/external/";
        }
        if (cursor != null) {

            System.out.println("Entroooou");

            while (cursor.moveToNext()) {
                JSONObject item=new JSONObject();

                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String path = cursor.getString(2);
                String size = cursor.getString(3);

                Uri uri=Uri.parse(baseUri+id);

                try{
                    item.put("name", name);
                    item.put("uri", uri);
                    item.put("path", path);
                    item.put("size", size);
                }catch (JSONException e){
                    System.out.println(e.getMessage());
                    _callbackContext.error(e.getMessage());
                    return;
                }
                resArray.put(item);

            }
            cursor.close();
            try {
                data.put("data",resArray);
            }catch (JSONException e){
                System.out.println(e.getMessage());
                _callbackContext.error(e.getMessage());
                return;
            }

            _callbackContext.success(data);
        }
    }    
}
