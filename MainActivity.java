package com.fmm27.scout;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.Base64;

public class MainActivity extends Activity {
    private static final int FILE_CHOOSER_REQUEST = 4102;
    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(244, 246, 250));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(244, 246, 250));
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (filePathCallback != null) filePathCallback.onReceiveValue(null);
                filePathCallback = callback;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/octet-stream", "application/x-fmm-save", "*/*"});
                try {
                    startActivityForResult(intent, FILE_CHOOSER_REQUEST);
                } catch (Exception e) {
                    filePathCallback = null;
                    callback.onReceiveValue(null);
                    Toast.makeText(MainActivity.this, "Unable to open file picker", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        webView.addJavascriptInterface(new DownloadBridge(), "Android");
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST && filePathCallback != null) {
            Uri[] result = null;
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                result = new Uri[]{data.getData()};
            }
            filePathCallback.onReceiveValue(result);
            filePathCallback = null;
        }
    }

    @Override
    public void onBackPressed() {
        // Close the in-app player detail sheet before leaving the app.
        webView.evaluateJavascript("(function(){var s=document.getElementById('sh');if(s&&s.classList.contains('show')){s.classList.remove('show');return true}return false})()", value -> {
            if (!"true".equals(value)) {
                if (webView.canGoBack()) webView.goBack();
                else MainActivity.super.onBackPressed();
            }
        });
    }

    private class DownloadBridge {
        @JavascriptInterface
        public void saveFile(String requestedName, String base64Data, String mimeType) {
            runOnUiThread(() -> {
                try {
                    String safeName = requestedName == null ? "download.bin" : requestedName.replaceAll("[^A-Za-z0-9._-]", "_");
                    byte[] bytes = Base64.getDecoder().decode(base64Data);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME, safeName);
                    values.put(MediaStore.Downloads.MIME_TYPE, mimeType == null ? "application/octet-stream" : mimeType);
                    values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/FMM27 Scout");
                    Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    if (uri == null) throw new IllegalStateException("Could not create download entry");
                    try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                        if (out == null) throw new IllegalStateException("Could not open download stream");
                        out.write(bytes);
                    }
                    Toast.makeText(MainActivity.this, "Saved to Downloads/FMM27 Scout", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
