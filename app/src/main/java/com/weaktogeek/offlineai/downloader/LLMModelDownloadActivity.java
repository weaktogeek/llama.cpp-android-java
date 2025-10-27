package com.weaktogeek.offlineai.downloader;

import static com.weaktogeek.offlineai.downloader.Utilities.getMimeFromFileName;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.weaktogeek.offlineai.chat.ChatActivity;
import com.weaktogeek.offlineai.databinding.ActivityLlmmodelDownloadBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LLMModelDownloadActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 100;
    private static final String TAG = "LLMModelDownloadActivity";
    private long downloadId;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLlmmodelDownloadBinding binding = ActivityLlmmodelDownloadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    /**
     * Load-Button callback
     *
     * @param v Button view
     */
    public void onLoad(View v) {
        if (checkPermission()) {
            openFileChooser();
        } else {
            requestPermission();
        }
    }

    /**
     * Download-Button callback
     *
     * @param v Button view
     */
    public void onDownload(View v) {


        String url = "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q6_K_L.gguf";
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, fileName)
                .setTitle(fileName)
                .setMimeType(getMimeFromFileName(fileName));

        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadId = dm.enqueue(request); // add download request to the queue
    }

    /**
     * Broadcast receiver for handling ACTION_DOWNLOAD_COMPLETE intents
     */
    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the download ID received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            // Check if the ID matches our download ID
            if (downloadId == id) {
                Log.i(TAG, "Download ID: " + downloadId);

                // Get file URI
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor c = dm.query(query);
                if (c.moveToFirst()) {
                    int colIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(colIndex)) {
                        Log.i(TAG, "Download Complete");
                        Toast.makeText(LLMModelDownloadActivity.this, "Download Complete", Toast.LENGTH_SHORT).show();

                        @SuppressLint("Range") String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Log.i(TAG, "URI: " + uriString);
                    } else {
                        Log.w(TAG, "Download Unsuccessful, Status Code: " + c.getInt(colIndex));
                        Toast.makeText(LLMModelDownloadActivity.this, "Download Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };


    // Activity Result launcher (modern way)
    private final ActivityResultLauncher<Intent> fileChooserLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    String path = null;
                    try {
                        path = getFilePathFromUri(this, uri);
                        if (path != null) {
                            Log.d("FilePath", "Absolute path: " + path);
                            System.out.println("path:" + path);
                            Toast.makeText(this, path, Toast.LENGTH_LONG).show();
                            String str_filepath = path != null ? path : "Unable to get path";
                            Intent intent = new Intent(LLMModelDownloadActivity.this, ChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("path", path);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Unable to get file path", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            });


    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // any file type
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        fileChooserLauncher.launch(Intent.createChooser(intent, "Select File"));
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true; // No need for READ_EXTERNAL_STORAGE from Android 13+
        }
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getFilePathFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        File file = new File(context.getCacheDir(), fileName);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        }
        return file.getAbsolutePath();
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }
}