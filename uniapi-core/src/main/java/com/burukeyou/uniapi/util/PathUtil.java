package com.burukeyou.uniapi.util;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PathUtil {

    private PathUtil(){}

    public static String joinPath(Object... paths) {
        if(paths == null || paths.length == 0) {
            return "";
        }
        if (paths.length == 1) {
            return paths[0].toString();
        }

        List<String> cleanPaths = new ArrayList<>();
        String firstPath = paths[0].toString();
        // 第一个拆后不拆前
        if (firstPath.endsWith(File.separator)) {
            firstPath = firstPath.substring(0, firstPath.length() - 1);
        }
        for (int i = 1; i < paths.length; i++) {
            String path = paths[i].toString();
            if (path.startsWith(File.separator)) {
                path = path.substring(1);
            }
            if (path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }
            cleanPaths.add(path);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(firstPath).append(File.separator);
        for (String cleanPath : cleanPaths) {
            stringBuilder.append(cleanPath).append(File.separator);
        }
        return stringBuilder.toString();
    }

    public static boolean isFileForPath(String path) {
        return Paths.get(path).getFileName().toString().contains(".");
    }
}
