package org.apache.cordova.logcat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;

public class ZipFilesPlugin extends CordovaPlugin {

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("uploadPlugin")) {
            Activity activity = cordova.getActivity();
            return true;
        } else {
            return false;
        }

    }

}
