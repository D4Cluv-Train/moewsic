package com.example.yin.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class UploadUtil {

    private static final Set<String> IMAGE_EXT = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp", "bmp"));
    private static final Set<String> AUDIO_EXT = new HashSet<>(Arrays.asList(
            "mp3", "flac", "wav", "aac", "m4a", "ogg", "wma"));

    private UploadUtil() {
    }

    public static String storeImage(MultipartFile file, String dir) throws IOException {
        return store(file, dir, IMAGE_EXT);
    }

    public static String storeAudio(MultipartFile file, String dir) throws IOException {
        return store(file, dir, AUDIO_EXT);
    }

    private static String store(MultipartFile file, String dir, Set<String> allowed) throws IOException {
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null) {
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                ext = original.substring(dot + 1).toLowerCase(Locale.ROOT);
            }
        }
        if (ext.isEmpty() || !allowed.contains(ext)) {
            throw new IllegalArgumentException("不允许的文件类型: " + original);
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        File folder = new File(dir);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File dest = new File(folder, fileName);
        file.transferTo(dest);
        return fileName;
    }
}
