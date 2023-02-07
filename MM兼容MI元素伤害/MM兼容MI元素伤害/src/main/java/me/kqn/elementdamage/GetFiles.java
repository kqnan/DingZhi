package me.kqn.elementdamage;

import java.io.File;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class GetFiles {

// 存储文件列表

    private ArrayList<String> fileList = new ArrayList<>();



    public ArrayList<String> getFile(File path) throws IOException {

        File[] listFile = path.listFiles();

        if(listFile==null)return this.fileList;
        for (File a : listFile) {

            BasicFileAttributes basicFileAttributes = Files.readAttributes(a.toPath(), BasicFileAttributes.class);
            if (basicFileAttributes.isDirectory()) {


                getFile(new File(a.getAbsolutePath()));

            } else if (basicFileAttributes.isRegularFile()) {

                this.fileList.add(a.getAbsolutePath());

            }

        }

        return fileList;

    }

}
