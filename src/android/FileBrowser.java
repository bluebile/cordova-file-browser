package com.denysbsb.cordova;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class FileBrowser extends CordovaPlugin {


    public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

       cordova.getThreadPool().execute(new Runnable() {
           @Override
           public void run() {
               runQuery(action,callbackContext);
           }
       });
        return true;
    }

    private void runQuery(String action, CallbackContext callback){
        JSONObject data=new JSONObject();
        JSONArray resArray=new JSONArray();
        Cursor cursor=null;
        String baseUri="";
        if(action.equals("image")) {
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
        else if(action.equals("audio")){
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
        }else if(action.equals("video")){
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
        }else if(action.equals("file")){
            
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
                    callback.error(e.getMessage());
                    return;
                }
                resArray.put(item);

            }
            cursor.close();
            try {
                data.put("data",resArray);
            }catch (JSONException e){
                System.out.println(e.getMessage());
                callback.error(e.getMessage());
                return;
            }

            callback.success(data);
        }
    }
}
