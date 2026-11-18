package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.util.Constant;

@WebServlet(urlPatterns = "/video-file")
public class VideoStreamController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int BUFFER_SIZE = 16 * 1024;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fileName = req.getParameter("fname");
        if (fileName == null || fileName.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String safeFileName = Paths.get(fileName.trim()).getFileName().toString();
        if (safeFileName.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        File file = new File(Constant.DIR, safeFileName);
        if (!file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long fileLength = file.length();
        resp.setContentType(resolveVideoMimeType(safeFileName));
        resp.setHeader("Accept-Ranges", "bytes");

        String rangeHeader = req.getHeader("Range");
        long start = 0;
        long end = fileLength - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String rangeValue = rangeHeader.substring("bytes=".length()).split(",")[0].trim();
            int dashIdx = rangeValue.indexOf('-');
            if (dashIdx >= 0) {
                String startPart = rangeValue.substring(0, dashIdx).trim();
                String endPart = rangeValue.substring(dashIdx + 1).trim();
                try {
                    if (startPart.isEmpty()) {
                        long suffixLength = Long.parseLong(endPart);
                        start = Math.max(0, fileLength - suffixLength);
                    } else {
                        start = Long.parseLong(startPart);
                        if (!endPart.isEmpty()) {
                            end = Long.parseLong(endPart);
                        }
                    }
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    return;
                }
            }
            if (start < 0 || start >= fileLength || end < start) {
                resp.setHeader("Content-Range", "bytes */" + fileLength);
                resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }
            if (end >= fileLength) {
                end = fileLength - 1;
            }
            long contentLength = end - start + 1;
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            resp.setHeader("Content-Length", String.valueOf(contentLength));
            streamRange(file, start, contentLength, resp.getOutputStream());
            return;
        }

        resp.setHeader("Content-Length", String.valueOf(fileLength));
        streamRange(file, 0, fileLength, resp.getOutputStream());
    }

    private void streamRange(File file, long start, long length, OutputStream out) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(start);
            byte[] buffer = new byte[BUFFER_SIZE];
            long remaining = length;
            while (remaining > 0) {
                int toRead = (int) Math.min(buffer.length, remaining);
                int read = raf.read(buffer, 0, toRead);
                if (read == -1) {
                    break;
                }
                out.write(buffer, 0, read);
                remaining -= read;
            }
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private String resolveVideoMimeType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".mp4")) {
            return "video/mp4";
        }
        if (lower.endsWith(".webm")) {
            return "video/webm";
        }
        if (lower.endsWith(".ogg") || lower.endsWith(".ogv")) {
            return "video/ogg";
        }
        if (lower.endsWith(".mov")) {
            return "video/quicktime";
        }
        String mimeType = getServletContext().getMimeType(fileName);
        return mimeType != null ? mimeType : "application/octet-stream";
    }
}
