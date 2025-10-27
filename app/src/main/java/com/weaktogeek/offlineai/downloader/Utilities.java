package com.weaktogeek.offlineai.downloader;

import android.webkit.MimeTypeMap;

public class Utilities {
    public static String getMimeFromFileName(String fileName) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(fileName);
        return map.getMimeTypeFromExtension(ext);
    }
}
