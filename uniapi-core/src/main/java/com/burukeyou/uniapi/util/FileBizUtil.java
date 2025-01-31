package com.burukeyou.uniapi.util;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author caizhihao
 */
public class FileBizUtil {

    /**
     * The default buffer size used when copying bytes.
     */
    public static final int BUFFER_SIZE = StreamUtils.BUFFER_SIZE;


    //---------------------------------------------------------------------
    // Copy methods for java.io.File
    //---------------------------------------------------------------------

    /**
     * Copy the contents of the given input File to the given output File.
     * @param in the file to copy from
     * @param out the file to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(File in, File out) throws IOException {
        Assert.notNull(in, "No input File specified");
        Assert.notNull(out, "No output File specified");
        return copy(Files.newInputStream(in.toPath()), Files.newOutputStream(out.toPath()));
    }

    /**
     * Copy the contents of the given byte array to the given output File.
     * @param in the byte array to copy from
     * @param out the file to copy to
     * @throws IOException in case of I/O errors
     */
    public static void copy(byte[] in, File out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No output File specified");
        copy(new ByteArrayInputStream(in), Files.newOutputStream(out.toPath()));
    }

    /**
     * Copy the contents of the given input File into a new byte array.
     * @param in the file to copy from
     * @return the new byte array that has been copied to
     * @throws IOException in case of I/O errors
     */
    public static byte[] copyToByteArray(File in) throws IOException {
        Assert.notNull(in, "No input File specified");
        return copyToByteArray(Files.newInputStream(in.toPath()));
    }


    //---------------------------------------------------------------------
    // Copy methods for java.io.InputStream / java.io.OutputStream
    //---------------------------------------------------------------------

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Closes both streams when done.
     * @param in the stream to copy from
     * @param out the stream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");

        try {
            return StreamUtils.copy(in, out);
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {
            }
            try {
                out.close();
            }
            catch (IOException ex) {
            }
        }
    }

    /**
     * Copy the contents of the given byte array to the given OutputStream.
     * Closes the stream when done.
     * @param in the byte array to copy from
     * @param out the OutputStream to copy to
     * @throws IOException in case of I/O errors
     */
    public static void copy(byte[] in, OutputStream out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No OutputStream specified");

        try {
            out.write(in);
        }
        finally {
            try {
                out.close();
            }
            catch (IOException ex) {
            }
        }
    }

    /**
     * Copy the contents of the given InputStream into a new byte array.
     * Closes the stream when done.
     * @param in the stream to copy from (may be {@code null} or empty)
     * @return the new byte array that has been copied to (possibly empty)
     * @throws IOException in case of I/O errors
     */
    public static byte[] copyToByteArray(@Nullable InputStream in) throws IOException {
        if (in == null) {
            return new byte[0];
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        copy(in, out);
        return out.toByteArray();
    }


    //---------------------------------------------------------------------
    // Copy methods for java.io.Reader / java.io.Writer
    //---------------------------------------------------------------------

    /**
     * Copy the contents of the given Reader to the given Writer.
     * Closes both when done.
     * @param in the Reader to copy from
     * @param out the Writer to copy to
     * @return the number of characters copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(Reader in, Writer out) throws IOException {
        Assert.notNull(in, "No Reader specified");
        Assert.notNull(out, "No Writer specified");

        try {
            int byteCount = 0;
            char[] buffer = new char[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {
            }
            try {
                out.close();
            }
            catch (IOException ex) {
            }
        }
    }

    /**
     * Copy the contents of the given String to the given output Writer.
     * Closes the writer when done.
     * @param in the String to copy from
     * @param out the Writer to copy to
     * @throws IOException in case of I/O errors
     */
    public static void copy(String in, Writer out) throws IOException {
        Assert.notNull(in, "No input String specified");
        Assert.notNull(out, "No Writer specified");

        try {
            out.write(in);
        }
        finally {
            try {
                out.close();
            }
            catch (IOException ex) {
            }
        }
    }

    /**
     * Copy the contents of the given Reader into a String.
     * Closes the reader when done.
     * @param in the reader to copy from (may be {@code null} or empty)
     * @return the String that has been copied to (possibly empty)
     * @throws IOException in case of I/O errors
     */
    public static String copyToString(@Nullable Reader in) throws IOException {
        if (in == null) {
            return "";
        }

        StringWriter out = new StringWriter();
        copy(in, out);
        return out.toString();
    }

    public static void createDirIfNotExistForPath(String path) {
        createDirIfNotExist(Paths.get(path).getParent().toString());
    }

    public static void createDirIfNotExist(String baseDir) {
        Path dir = Paths.get(baseDir);
        boolean notExists = Files.notExists(dir);
        if (notExists) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException("create dir error " + baseDir, e);
            }
        }
    }

    public static File moveFile(File sourceFile, String targetPath){
       return moveFile(sourceFile.getAbsolutePath(), targetPath);
    }

    public static File moveFile(String sourcePath, String targetPath){
        Path move = null;
        try {
            createDirIfNotExistForPath(targetPath);
            move = Files.move(Paths.get(sourcePath), Paths.get(targetPath));
        } catch (IOException e) {
            throw new RuntimeException("move file error ",e);
        }
        return move.toFile();
    }

    public static File saveFile(byte[] fileBytes, String savePath) {
      return saveFile(new ByteArrayInputStream(fileBytes), savePath);
    }

    public static File saveFile(InputStream inputStream, String savePath) {
        AssertUtil.notNull(inputStream, "save file inputStream is null");
        AssertUtil.notBlank(savePath, "save file path is null");

        createDirIfNotExistForPath(savePath);
        try {
            File file = new File(savePath);
            copy(inputStream, Files.newOutputStream(file.toPath()));
            return file;
        } catch (IOException e) {
            throw new RuntimeException("save file error ",e);
        }
    }

    /**
     * Rough judgment on whether it is a file path or not
     * @param content       file path content
     */
    public static boolean isFilePathSimilar(String content) {
        return  content.startsWith("classpath:") ||
                content.startsWith("file:") ||
                content.startsWith("http:") ||
                content.startsWith(File.separator) ||
                content.endsWith(".txt") ||
                content.endsWith(".xls") ||
                content.endsWith(".xlsx") ||
                content.endsWith(".csv") ||
                content.endsWith(".pdf") ||
                content.endsWith(".mp4") ||
                content.endsWith(".mp3") ||
                content.endsWith(".key") ||
                content.endsWith(".keyStore") ||
                content.endsWith(".p10") ||
                content.endsWith(".p12") ||
                content.endsWith(".cem") ||
                content.endsWith(".pem") ||
                content.endsWith(".crt") ||
                content.startsWith("C:") ||
                content.startsWith("D:") ||
                content.startsWith("E:") ||
                content.startsWith("F:") ||
                new File(content).exists() ||
                content.length() < 100;
    }

    public static byte[] streamToByteArray(InputStream is) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            byte[] array = bos.toByteArray();
            bos.close();
            return array;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
