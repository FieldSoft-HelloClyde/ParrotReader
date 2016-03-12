package com.fieldsoft.parrotreader;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class BookReader extends Activity{
	
	ReaderView mReaderView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_reader);
		mReaderView = (ReaderView)findViewById(R.id.ReaderView);
		mReaderView.SetZipFile(getIntent().getStringExtra("FilePath"));
	}
	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
			 //ǰһҳ
			 mReaderView.PrePage();
			 return true;
		 }
		 if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_HEADSETHOOK){
			 mReaderView.NextPage();
			 return true;
		 }
		 if (keyCode == KeyEvent.KEYCODE_MENU){
			 mReaderView.ShowMenu();
		 }
		 return super.onKeyDown(keyCode, event);
	 }
}
