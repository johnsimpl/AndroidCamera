package com.protectsoft.camshare.bucket;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by abraham on 18/9/2015.
 *
 * arraylist with all the filePath picture taken and saved from camera capture
 *
 */
public class BucketFiles {

    private static ArrayList<File> picturesFileList = new ArrayList<>();

    public static boolean isPictureFileListEmpty() {
        return picturesFileList.isEmpty();
    }


    public static void addPictureFile(File file) {
        picturesFileList.add(file);
    }


    public static ArrayList<File> getPicturesFileList() {
        return picturesFileList;
    }


    public static void removePictureFile(File file) {
        if(!picturesFileList.isEmpty()) {
            for(int i =0; i < picturesFileList.size(); i++) {
                if(picturesFileList.get(i).equals(file)) {
                    picturesFileList.remove(i);
                }
            }
        }
    }


    public static void clearPictureFiles() {
        if(!picturesFileList.isEmpty()) {
            picturesFileList.clear();
        }
    }


    public static File getNextPictureFile(File file) {
        if(!picturesFileList.isEmpty()) {
            for(int i =0; i < picturesFileList.size(); i++) {
                if(picturesFileList.get(i).equals(file)) {
                    if(++i < picturesFileList.size()) {
                        if (picturesFileList.get(i) != null) {
                            return picturesFileList.get(i);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isNextPictureFileExists(File file) {
        if(!picturesFileList.isEmpty()) {
            for(int i =0; i < picturesFileList.size(); i++) {
                if(picturesFileList.get(i).equals(file)) {
                    if(++i < picturesFileList.size()) {
                        if (picturesFileList.get(i) != null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public static File getPreviusPictureFile(File file) {
        if(!picturesFileList.isEmpty()) {
            for(int i =0; i < picturesFileList.size(); i++) {
                if(picturesFileList.get(i).equals(file)) {
                    if( --i >= 0) {
                        if (picturesFileList.get(i) != null) {
                            return picturesFileList.get(i);
                        }
                        break;
                    }
                    break;
                }
            }
        }
        return null;
    }

    public static boolean isPreviusPictureFileExists(File file) {
        if(!picturesFileList.isEmpty()) {
            for(int i =0; i < picturesFileList.size(); i++) {
                if(picturesFileList.get(i).equals(file)) {
                    if( --i >= 0) {
                        if (picturesFileList.get(i) != null) {
                            return true;
                        }
                        break;
                    }
                    break;
                }
            }
        }
        return false;
    }


    public static void removeDeletedFiles() {
        if(!picturesFileList.isEmpty()) {
            for(int i =0; i < picturesFileList.size(); i++) {
                if(!picturesFileList.get(i).exists()) {
                    picturesFileList.remove(i);
                }
            }
        }
    }

    public static boolean isFileExists(File file) {
        if(!picturesFileList.isEmpty()) {
            for(int i =0; i < picturesFileList.size(); i++) {
                if(picturesFileList.get(i).equals(file)) {
                    return picturesFileList.get(i).exists();
                }
            }
        }
        return false;
    }

    //// TODO: 24/9/2015 delete not working properly
    //on android 4.2 and above delete is disabled!
    // actualy remove delete functionality! leave it for photo album apps and etc.
    @Deprecated
    public static void deleteFileFromFileSystem(File file) {
        if(!picturesFileList.isEmpty()) {
            for(int i =0; i < picturesFileList.size(); i++) {
                if(picturesFileList.get(i).equals(file)) {
                    if(picturesFileList.get(i).exists()) {
                        //picturesFileList.get(i).delete();
                        picturesFileList.remove(i);
                    }
                }
            }
        }
    }

    public static void deteleAllFiles() {
        if(!picturesFileList.isEmpty()) {
            for(int i =0; i < picturesFileList.size(); i++) {
                if(picturesFileList.get(i).exists()) {
                    picturesFileList.get(i).delete();
                }
            }
        }
    }

    /**
     *
     * @return never returns null
     */
    public static int getPictureFileSize() {
        return picturesFileList.size();
    }




}
