package gov.va.ascent.maven.plugin.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vgadda on 7/25/17.
 */
public class FileUtil {
    public static List<String> getFiles(String directoryName){
        List<String> files = new ArrayList<>();
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile() && file.getName().endsWith(".yml")){
                files.add(file.getAbsolutePath());
            } else if (file.isDirectory()){
                files.addAll(getFiles(file.getAbsolutePath()));
            }
        }
        return files;
    }
}
