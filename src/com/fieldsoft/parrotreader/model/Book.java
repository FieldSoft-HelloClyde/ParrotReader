package com.fieldsoft.parrotreader.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.http.util.EncodingUtils;

import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Rect;

public class Book {
	File BookZipFile;
	int ScreenWidth;
	int ScreenHeight;
	Paint FontPaint;
	int TextHeight;
	int TextPadding;
	
	int ChapterIndexNow;
	int PageIndexNow;
	ArrayList<String[]> TextLines;
	
	//标示是到最前还是最后,-1时最前，1时最后
	int BookFinish;
	
	
	public Book(File bookZipFile,int screenWidth,int screenHeight,Paint fontPaint,int textHeight,int textPadding){
		this.BookZipFile = bookZipFile;
		this.ScreenHeight = screenHeight;
		this.ScreenWidth = screenWidth;
		this.FontPaint = fontPaint;
		this.TextHeight = textHeight;
		this.TextPadding = textPadding;

    	//Log.d("ZipFile", this.BookZipFile.toString());
		//数据初始化
		//读取书签
		if (!this.Read()){
			this.ChapterIndexNow = 1;
			this.PageIndexNow = 0;
		}
		this.TextLines = new ArrayList<String[]>();
		this.BookFinish = 0;
	}
	
	public int GetBookFinish(){
		return this.BookFinish;
	}
	
	public String[] GetNowPage(){
		if (this.ScreenHeight == 0 || this.ScreenWidth == 0){
			return null;
		}
		else{
			String TempStr = this.GetTxtFileString(this.ChapterIndexNow);
			ReadChapter(TempStr);
			String[] TempStrArray = this.TextLines.get(this.PageIndexNow);
			return TempStrArray;
		}
	}
	
	public String[] GetNextPage(){
		if (this.ScreenWidth == 0 || this.ScreenWidth == 0){
			return null;
		}
		else{
			this.PageIndexNow ++;
			if (this.PageIndexNow >= this.TextLines.size()){
				this.ChapterIndexNow ++;
				String TempStr = this.GetTxtFileString(this.ChapterIndexNow);
				if (TempStr == null){
					//说明本书已经看完
					this.BookFinish = 1;
					this.PageIndexNow --;
					this.ChapterIndexNow --;
					return null;
				}
				else{
					ReadChapter(TempStr);
					this.PageIndexNow = 0;
				}
			}
			String[] TempStrArray = this.TextLines.get(this.PageIndexNow);
			this.BookFinish = 0;
			return TempStrArray;
		}
	}
	
	public String[] GetPrePage(){
		if (this.ScreenWidth == 0 || this.ScreenWidth == 0){
			return null;
		}
		else{
			this.PageIndexNow --;
			if (this.PageIndexNow < 0){
				this.ChapterIndexNow --;
				if (this.ChapterIndexNow == 0){
					//已经到最前
					this.BookFinish = -1;
					this.PageIndexNow = 0;
					this.ChapterIndexNow = 1;
					return null;
				}
				else{
					String TempStr = this.GetTxtFileString(this.ChapterIndexNow);
					ReadChapter(TempStr);
					this.PageIndexNow = this.TextLines.size() - 1;
				}
			}
			String[] TempStrArray = this.TextLines.get(this.PageIndexNow);
			this.BookFinish = 0;
			return TempStrArray;
		}
	}
	
	public void SizeChange(int WidthNew,int HeightNew){
		this.ScreenWidth = WidthNew;
		this.ScreenHeight = HeightNew;
	}
	
	void ReadChapter(String text){
		this.TextLines.clear();
		ArrayList<String> PageStrings = new ArrayList<String>();
		int textIndex = 0;
		while (textIndex < text.length()){
			PageStrings.clear();
			for (int LineNum = 0;!IsOutOfHeight(LineNum) && textIndex < text.length();LineNum ++){
				StringBuffer LineStr = new StringBuffer();
				while (textIndex < text.length()){
					char TempChar = text.charAt(textIndex);
					if (TempChar == '\n'){
						textIndex ++;
						break;
					}
					else{
						LineStr.append(TempChar);
						if (IsOutOfWidth(LineStr.toString())){
							LineStr.deleteCharAt(LineStr.length() - 1);
							break;
						}
						else{
							textIndex ++;
						}
					}
				}
				PageStrings.add(LineStr.toString());
			}
			String[] TempStrArr = new String[PageStrings.size()];
			this.TextLines.add(PageStrings.toArray(TempStrArr));
		}
	}
	
	boolean IsOutOfWidth(String line){
		return GetTextWidth(line) >= this.ScreenWidth - 2 * this.TextPadding;
	}
	
	boolean IsOutOfHeight(int LineLength){
		return LineLength * this.TextHeight >= this.ScreenHeight - 2 * this.TextPadding;
	}
	
	int GetTextWidth(String text){
		Rect TempRect = new Rect();  
		this.FontPaint.getTextBounds(text, 0, text.length(), TempRect);
		return TempRect.width();
	}
	
	int GetTextHeight(String text){
		Rect TempRect = new Rect();  
		this.FontPaint.getTextBounds(text, 0, text.length(), TempRect);
		return TempRect.height();
	}
	
	
	
	String GetTxtFileString(int ChapterIndex){
		try {
			InputStream fin = this.GetFileFromZip("chapter/" + ChapterIndex + ".txt");
			int maxLength = fin.available();
	        byte [] buffer = new byte[maxLength];
	        fin.read(buffer);
	        return new String(buffer,"gb2312");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressLint("DefaultLocale")
	@SuppressWarnings("resource")
	InputStream GetFileFromZip(String FilePath){
        try {
			ZipFile zf = new ZipFile(this.BookZipFile);
			InputStream in = new BufferedInputStream(new FileInputStream(this.BookZipFile));
	        ZipInputStream zin = new ZipInputStream(in);
	        ZipEntry ze;
			while ((ze = zin.getNextEntry()) != null) {
			    if (!ze.isDirectory()) {
			    	if (ze.getName().toLowerCase().equals(FilePath)){
			    		return zf.getInputStream(ze);
			    	}
			    }
			}
			zf.close();
			zin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void Save(){
		BookMark bookMark = new BookMark(this.ChapterIndexNow,this.PageIndexNow);
		String Write_Str = (new Gson()).toJson(bookMark);
		String BookMarkFilePath = this.BookZipFile.getAbsolutePath() + ".cfg";
        try {
        	FileOutputStream fout = new FileOutputStream(BookMarkFilePath);   
            byte [] bytes = Write_Str.getBytes();
            fout.write(bytes);   
			fout.close();
        } catch (IOException e){
        	e.printStackTrace();
        }
	}
	
	public boolean Read(){
		String BookMarkFilePath = this.BookZipFile.getAbsolutePath() + ".cfg";
        try {
        	FileInputStream fin = new FileInputStream(BookMarkFilePath);
        	int length = fin.available();
            byte [] buffer = new byte[length];
            fin.read(buffer);
            String Read_Str = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
            BookMark bookMark = (new Gson()).fromJson(Read_Str, BookMark.class);
            this.ChapterIndexNow = bookMark.ChapterIndexNow;
            this.PageIndexNow = bookMark.PageIndexNow;
            return true;
        } catch (Exception e){
        	e.printStackTrace();
        	return false;
        }
	}
}
	
class BookMark{
	public int ChapterIndexNow;
	public int PageIndexNow;
	public BookMark(int chapterIndex,int pageIndex){
		this.ChapterIndexNow = chapterIndex;
		this.PageIndexNow = pageIndex;
	}
}
