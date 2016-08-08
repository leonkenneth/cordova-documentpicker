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
import android.util.Log;

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
import java.io.InputStream;

public class DocumentPickerPlugin extends CordovaPlugin {

    private Context context;
    private CallbackContext callbackContext;

    private static final int PICK_DOCUMENT = 1;

    @Override
    public boolean execute(String action, JSONArray data,
                           CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        this.context = cordova.getActivity().getApplicationContext();
        if (action.equals("pick")) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");

            cordova.startActivityForResult(this, intent, PICK_DOCUMENT);

            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);
            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        Uri documentUri = data.getData();

        try {
            JSONObject result = new JSONObject();
            result.put("url", documentUri.toString());

            callbackContext.success(result);

        } catch (Exception e) {
            Log.v("DocumentPicker", "Document picking failed: " + e.getMessage());
            callbackContext.error("Document picking failed: " + e.getMessage());
        }
    }
}
