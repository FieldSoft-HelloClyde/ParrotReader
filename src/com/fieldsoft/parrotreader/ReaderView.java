package com.fieldsoft.parrotreader;

import java.io.*;

import com.fieldsoft.parrotreader.model.Book;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ReaderView extends View{
	
	File ZipFile;

	Paint BgPaint = new Paint();
	Paint FontPaint = new Paint();
	
	Book DesBook;
	
	int TextBaseLineYPos;
	int TextBaseLineXPos;
	int TextHeight;
	
	//
	int TextPadding;
	
	//
	String[] PageStringArray;
	
	//触屏操作参数
	Point DownPos;

	public ReaderView(Context context) {
		super(context);
		init();
	}

	public ReaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ReaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	void init(){
		//BgPaint.setColor(Color.rgb(255, 255, 255));
		//FontPaint.setColor(Color.rgb(0, 0, 0));
		FontPaint.setColor(Color.rgb(255, 255, 255));
		BgPaint.setColor(Color.rgb(0, 0, 0));
		FontPaint.setTextSize(30);
		FontPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		FontPaint.setAntiAlias(true);
		
		//计算文字坐标
		this.TextBaseLineYPos = (int) -this.FontPaint.getFontMetrics().top;
		this.TextBaseLineXPos = this.GetTextLeft();
		this.TextHeight = (int) (this.FontPaint.getFontMetrics().bottom - this.FontPaint.getFontMetrics().top);
		
		this.TextPadding = 20;
		
		this.PageStringArray = null;
		
		this.DownPos = null;
	}
	
	int GetTextLeft(){
		Rect TempRect = new Rect();
		this.FontPaint.getTextBounds("中国A", 0, 3, TempRect);
		return TempRect.left;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), this.BgPaint);
		if (this.ZipFile == null){
			canvas.drawText("ZipFile没有传入", this.TextPadding - this.TextBaseLineXPos, this.TextPadding + this.TextBaseLineYPos, this.FontPaint);
		}
		else{
			if (this.PageStringArray != null){
				for (int i = 0;i < this.PageStringArray.length;i ++){
					Log.d("NoText", this.PageStringArray[i]);
					canvas.drawText(this.PageStringArray[i], this.TextPadding - this.TextBaseLineXPos, this.TextPadding + i * this.TextHeight + this.TextBaseLineYPos, this.FontPaint);
				} 
			}
		}
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		this.DesBook.SizeChange(w, h);
		this.DesBook = new Book(this.ZipFile,this.getWidth(),this.getHeight(),this.FontPaint, this.TextHeight,this.TextPadding);
		this.PageStringArray = this.DesBook.GetNowPage();
	}
	
	public void SetZipFile(String ZipFilePath){
		this.ZipFile = new File(ZipFilePath);
		//构建Book
		this.DesBook = new Book(this.ZipFile,this.getWidth(),this.getHeight(),this.FontPaint, this.TextHeight,this.TextPadding);
		this.NextPage();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if (this.getWidth() == 0 || this.getHeight() == 0){
			return false;
		}
		else{
			if (event.getAction() == MotionEvent.ACTION_DOWN){
				this.DownPos = new Point((int) event.getX(),(int) event.getY());
			}
			else if (event.getAction() == MotionEvent.ACTION_UP){
				if (this.DownPos != null){
					//计算UP点与DOWN点距离
					float dx = this.DownPos.x - event.getX();
					float dy = this.DownPos.y - event.getY();
					double dis = Math.sqrt(dx * dx + dy * dy);
					//距离小于20像素认定为单击
					if (dis <= 20){
						//1/3~2/3的盒子表示菜单位置
						if (event.getX() >= this.getWidth() / 3 && event.getX() <= this.getWidth() * 2 / 3
								&& event.getY() >= this.getHeight() / 3 && event.getY() <= this.getHeight() * 2 / 3){
							//跳出菜单
						}
						else{
							//计算对分函数
							float k = (float)this.getHeight() / this.getWidth();
							if (event.getY() >= -k * event.getX() + this.getHeight()){
								this.NextPage();
							}
							else{
								this.PrePage();
							}
						}
					}
				}
				this.DownPos = null;
			}
			return true;
		}
	}
	
	public void ShowMenu(){
		
	}
	
	public void NextPage(){
		String[] TempStrArr = this.DesBook.GetNextPage();
		if (TempStrArr == null){
			if (this.DesBook.GetBookFinish() == 1){
				Toast.makeText(this.getContext(),"已经到最后一页。", Toast.LENGTH_SHORT).show();
			}
		}
		else{
			this.DesBook.Save();
			this.PageStringArray = TempStrArr;
			this.invalidate();
		}
	}
	
	public void PrePage(){
		String[] TempStrArr = this.DesBook.GetPrePage();
		if (TempStrArr == null){
			if (this.DesBook.GetBookFinish() == -1){
				Toast.makeText(this.getContext(),"已经到最前一页。", Toast.LENGTH_SHORT).show();
			}
		}
		else{
			this.DesBook.Save();
			this.PageStringArray = TempStrArr;
			this.invalidate();
		}
	}
	
	
}
