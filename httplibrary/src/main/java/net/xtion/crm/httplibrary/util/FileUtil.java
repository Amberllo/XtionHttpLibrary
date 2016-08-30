package net.xtion.crm.httplibrary.util;

import net.xtion.crm.httplibrary.exception.FileCreateException;

import java.io.File;

/**
 * Created by LYL on 2016/8/30.
 */
public class FileUtil {


    /**
     * 创建文件方法，有旧文件，立即删除重新创建
     * @param dir 所在目录 如 /store/0/sdcard/net.xtion.crm
     * @param filename 文件名 如 "xxxx.mp3"
     * 完整路径： /store/0/sdcard/net.xtion.crm/xxxx.mp3
     * */
    public static File createFile(String dir, String filename) throws FileCreateException {
        try {

            File directory = new File(dir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(dir+"/"+filename);
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            throw new FileCreateException();
        }
    }

    /**
     * 删除文件
     * @param path 完整路径
     * */
    public static void deleteFile(String path){
        File f = new File(path);
        if(f.exists()){
            f.delete();
        }
    }

}
