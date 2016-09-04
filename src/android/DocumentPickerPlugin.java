package com.leonkenneth.DocumentPicker;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Base64;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class DocumentPickerPlugin extends CordovaPlugin {

    private Context context;
    private CallbackContext callbackContext;

    private static final int PICK = 1;

    @Override
    public boolean execute(String action, JSONArray data,
                           CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        this.context = cordova.getActivity().getApplicationContext();

        try {
            if (action.equals("pick")) {
                startPick(data.getJSONArray(0));
                return true;
            }

            if (action.equals("get")) {
                get(data.getString(0));
                return true;
            }
        } catch (Exception e) {
            this.callbackContext.error(e.getMessage());
        }

        return false;
    }

    private String getFileName(Uri uri) {
        String fileName = null;

        if (uri.getScheme().equals("content")) {
          Cursor cursor = this.context.getContentResolver().query(uri, null, null, null, null);
          try {
            if (cursor != null && cursor.moveToFirst()) {
              fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
          } finally {
            cursor.close();
          }
        }

        if (fileName == null) {
          fileName = uri.getPath();
          int cut = fileName.lastIndexOf('/');
          if (cut != -1) {
            fileName = fileName.substring(cut + 1);
          }
        }

        return fileName;
    }

    private String getFileContent(Uri uri) throws FileNotFoundException, IOException {
      InputStream inputStream = this.context.getContentResolver().openInputStream(uri);
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int readCount;
      byte[] data = new byte[16384];

      while ((readCount = inputStream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, readCount);
      }

      buffer.flush();

      byte[] byteArray = buffer.toByteArray();

      return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void get(String url) throws JSONException, FileNotFoundException, IOException {
        Uri uri = Uri.parse(url);

        String name = getFileName(uri);
        String contentBase64 = getFileContent(uri);

        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("contentBase64", contentBase64);

        this.callbackContext.success(result);
    }

    private String[] getMimeTypesFromJSONArray(JSONArray mimeTypes) throws JSONException {
      int length = mimeTypes.length();
      String[] result = new String[length];

      for (int i = 0 ; i < length ; i++) {
        result[i] = mimeTypes.getString(i);
      }

      return result;
    }

    private void startPick(JSONArray mimeTypes) throws JSONException {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, getMimeTypesFromJSONArray(mimeTypes));

        this.cordova.startActivityForResult(this, intent, PICK);

        PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
        r.setKeepCallback(true);
        this.callbackContext.sendPluginResult(r);
    }

    private void onPickSuccess(Intent data) {
        Uri documentUri = data.getData();

        this.callbackContext.success(documentUri.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == PICK) {
          onPickSuccess(data);
        }
    }
}
