package vn.iotstar.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.servlet.http.Part;

public final class FileUploadUtil {

    private FileUploadUtil() {
    }

    public static String saveUploadedFile(Part part, String fallbackValue,
                                          String oldLocalFile, String defaultExt) throws IOException {
        File uploadDir = new File(Constant.DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        if (part != null && part.getSize() > 0 && part.getSubmittedFileName() != null
                && !part.getSubmittedFileName().isBlank()) {
            if (isLocalManagedFile(oldLocalFile)) {
                deleteLocalFile(oldLocalFile);
            }
            String submittedName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            String ext = extractExtension(submittedName, defaultExt);
            String fname = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + ext;
            part.write(Constant.DIR + File.separator + fname);
            return fname;
        }

        return fallbackValue;
    }

    public static void deleteLocalFile(String fileName) {
        if (!isLocalManagedFile(fileName)) {
            return;
        }
        String safeName = Paths.get(fileName.trim()).getFileName().toString();
        if (safeName.isBlank()) {
            return;
        }
        try {
            Path path = Paths.get(Constant.DIR, safeName);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isLocalManagedFile(String value) {
        return value != null && !value.isBlank() && !"avatar.png".equals(value.trim());
    }

    private static String extractExtension(String filename, String defaultExt) {
        int index = filename.lastIndexOf('.');
        if (index >= 0 && index < filename.length() - 1) {
            String rawExt = filename.substring(index + 1).replaceAll("[^A-Za-z0-9]", "");
            if (!rawExt.isEmpty() && rawExt.length() <= 10) {
                return rawExt.toLowerCase();
            }
        }
        return defaultExt;
    }
}
