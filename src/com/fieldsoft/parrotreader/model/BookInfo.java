package com.fieldsoft.parrotreader.model;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


import com.google.gson.Gson;

public class BookInfo {
	File BookZipFile;
	String BookZipFilePath;
	String CoverImgBase64;
	String BookTitle;
	
	/**
	 * test
	 * @param args
	 */
	static public void main(String[] args){
		BookInfo TestBook = new BookInfo(new File("d:/BookLibary/480.zip"));
		System.out.println(TestBook.BookTitle);
		System.out.println(TestBook.BookZipFilePath);
		System.out.println(TestBook.CoverImgBase64);
		System.out.println(TestBook.BookZipFile);
	}
	
	@SuppressLint("DefaultLocale")
	public BookInfo(File ZipSrcFile){
		try {
			this.BookZipFile = ZipSrcFile;
			this.BookZipFilePath = ZipSrcFile.getAbsolutePath();
			ZipFile zf = new ZipFile(ZipSrcFile);
			InputStream in;
			in = new BufferedInputStream(new FileInputStream(ZipSrcFile));
	        ZipInputStream zin = new ZipInputStream(in);  
	        ZipEntry ze;
	        while ((ze = zin.getNextEntry()) != null) {
	            if (!ze.isDirectory()) {
	            	if (ze.getName().toLowerCase().equals("config.txt")){
	            		//获取书籍信息
	            		InputStream fin = zf.getInputStream(ze);
	            		int length = fin.available();
	                    byte [] buffer = new byte[length];   
	                    fin.read(buffer);    
	                    String Read_Str = new String(buffer,"gb2312");
	                    //EncodingUtils.getString(buffer, "utf-8");
	                    fin.close();
	                    ConfigJson ConfigJson = (new Gson()).fromJson(Read_Str, ConfigJson.class);
	                    this.BookTitle = ConfigJson.Title;
	            	}
	            	else if (ze.getName().toLowerCase().equals("coverimage.jpg")){
	            		//获取书籍封面
	            		InputStream fin = zf.getInputStream(ze);
	            		int maxLength = fin.available();
	            		byte[] TempBytes = new byte[maxLength];
	            		fin.read(TempBytes);
	            		this.CoverImgBase64 = Base64.encodeToString(TempBytes, Base64.DEFAULT);
	            		fin.close();
	            	}
	            }  
	        }  
	        zin.closeEntry(); 
	        zin.close();
	        zf.close();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}  
	}
	
	public File getBookZipFile() {
		return BookZipFile;
	}
	
	public String getBookZipFilePath() {
		return BookZipFilePath;
	}

	public String getCoverImg() {
		return CoverImgBase64;
	}

	public String getBookTitle() {
		return BookTitle;
	}
}

class ConfigJson{
	public String Title;
	public String Author;
	public String Classification;
	public String State;
	public String LastUpdateTime;
}
