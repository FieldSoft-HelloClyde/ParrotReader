package com.fieldsoft.parrotreader.model;

import java.io.*;
import java.util.*;

import android.os.Environment;

import com.google.gson.Gson;

public class GetLocalBook {
	ArrayList<BookInfo> BookInfos; 
	File BookLibFolder;
	File[] BookFiles;
	
	public GetLocalBook(){
		this.BookInfos = new ArrayList<BookInfo>();
		this.BookLibFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "BookLibary" + File.separator);
		this.BookFiles = BookLibFolder.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".zip")){
					return true;
				}
				return false;
			}
		});
	}
	//ªÒ»°img,name,path
	
	public String GetBookInfosJson(){
		if (BookFiles == null){
			return "";
		}
		else{
			for (int i = 0;i < BookFiles.length;i ++){
				BookInfos.add(new BookInfo(BookFiles[i]));
			}
			return new Gson().toJson(BookInfos);
		}
	}
}
