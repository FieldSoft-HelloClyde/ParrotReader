package com.fieldsoft.parrotreader;
import com.fieldsoft.parrotreader.model.GetLocalBook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {
	
	public final static int REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		WebView webView=(WebView)findViewById(R.id.WebViewMain);
		webView.getSettings().setJavaScriptEnabled(true);	//支持JavaScript
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);//关闭缓存
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//不加上，会显示白边
		webView.addJavascriptInterface(this, "AndroidOperate");
		webView.setWebChromeClient(new WebChromeClient());
		webView.loadUrl("file:///android_asset/index.html");
		//Toast.makeText(this,this.GetBookInfosJson(), Toast.LENGTH_LONG).show();
	}
	
	public String GetBookInfosJson(){
		return new GetLocalBook().GetBookInfosJson();
	}
	
	public void StartReader(String FilePath){
		Intent intent = new Intent(MainActivity.this, BookReader.class);
		//传输数据
		Bundle bl = new Bundle();
		bl.putString("FilePath", FilePath);
		intent.putExtras(bl);
		startActivity(intent); 
		//Toast.makeText(this,FilePath, Toast.LENGTH_LONG).show();
	}
}
