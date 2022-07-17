package org.cordova;

import android.Manifest;
import android.content.pm.PackageManager;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.PermissionHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.ArrayList;

/**
 * This class echoes a string called from JavaScript.
 */
public class scanner extends CordovaPlugin {

    private CallbackContext scanCallbackContext;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        if (!action.equals("scan") && !action.equals("permission")) {
            callbackContext.error("\"" + action + "\" is not a recognized action.");
            return false;
        }
        
        if(action.equals("permission")) {
            if(!PermissionHelper.hasPermission(this,Manifest.permission.CAMERA)) {
                PermissionHelper.requestPermission(this, 33, Manifest.permission.CAMERA);
            } else {
                callbackContext.success("Permission already granted");
            }
        } else {
            IntentIntegrator integrator = new IntentIntegrator((CordovaActivity) cordova.getActivity());
            cordova.setActivityResultCallback(this);
            try {
                JSONObject params = args.getJSONObject(0);
                if (params.has("prompt_message") && params.getString("prompt_message").length() > 0) integrator.setPrompt(params.getString("prompt_message")); // Prompt Message
                if (params.has("orientation_locked")) integrator.setOrientationLocked(params.getBoolean("orientation_locked")); // Orientation Locked
                if (params.has("camera_id")) integrator.setCameraId(params.getInt("camera_id")); // Camera Id
                if (params.has("beep_enabled")) integrator.setBeepEnabled(params.getBoolean("beep_enabled")); // Beep Enabled
                if (params.has("timeout")) integrator.setTimeout(params.getInt("timeout")); // Timeout
                
                // Scan Type
                if (params.has("scan_type")) {
                    String scanType = params.getString("scan_type");
                    if (scanType.equals("inverted")) integrator.addExtra("SCAN_TYPE", 1);
                    else if (scanType.equals("mixed")) integrator.addExtra("SCAN_TYPE", 2);
                    else integrator.addExtra("SCAN_TYPE", 0);
                }
                
                // Barcode Formats
                if (params.has("barcode_formats")) {
                    ArrayList<String> formats = new ArrayList<String>();
                    JSONArray barcodeFormats = (JSONArray) params.getJSONArray("barcode_formats");
                    if (barcodeFormats != null) {
                        for (int i = 0; i < barcodeFormats.length(); i++) {
                            formats.add(barcodeFormats.getString(i));
                        }
                        if (!formats.isEmpty()) integrator.setDesiredBarcodeFormats(formats);
                    }
                }
                
                // Extras
                JSONObject extras = params.has("extras") ? params.getJSONObject("extras") : null;
                if (extras != null) {
                    JSONArray extraNames = extras.names();
                    if (extraNames != null) {
                        for (int i = 0; i < extraNames.length(); i++) {
                            String key = extraNames.getString(i);
                            Object value = extras.get(key);
                            integrator.addExtra(key, value);
                        }
                    }
                }
            } catch (JSONException e) {
                callbackContext.error("Error encountered: " + e.getMessage());
                return false;
            }
            
            // Init scanner using a camera
            integrator.initiateScan();
        }
        scanCallbackContext = callbackContext;
        return true;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        try {
            for (int i = 0; i < grantResults.length; i++) {
                if(requestCode == 33) {
                    if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        scanCallbackContext.success("Permission is granted");
                        return;
                    } else {
                        scanCallbackContext.error("Permission not granted");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            scanCallbackContext.error("Permission grant failure");
            return;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(result != null) {
            if(result.getContents() == null) {
                scanCallbackContext.error("Scan Cancelled");
            } else {
                scanCallbackContext.success(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }
}
