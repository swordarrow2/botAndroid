package com.meng.qrtools;
import android.widget.*;
import com.meng.*;
import android.app.*;

public class log
{

	public static void e(final Activity a,final Object o){
		a.runOnUiThread(new Runnable(){

				@Override
				public void run(){
					// TODO: Implement this method
					Toast.makeText(a,"发生错误:"+o.toString(),Toast.LENGTH_SHORT).show();
				}
			});
	}

	public static void c(final Activity a,final Object o){
		
		a.runOnUiThread(new Runnable(){

				@Override
				public void run(){
					// TODO: Implement this method
					MainActivity2.rightText.setText(
					MainActivity2.rightText.getText().toString()+
					"点击:"+o.toString()+"\n"
					);
				}
			});
	}
	public static void i(final Activity a,final Object o){
		a.runOnUiThread(new Runnable(){

				@Override
				public void run(){
					// TODO: Implement this method
					MainActivity2.rightText.setText(
					MainActivity2.rightText.getText().toString()+
					o.toString()+"\n"
					);
				}
			});
	}
	
	public static void t(final Activity a,final Object o){
		a.runOnUiThread(new Runnable(){

				@Override
				public void run(){
					// TODO: Implement this method
					Toast.makeText(a,o.toString(),Toast.LENGTH_SHORT).show();
				}
			});
	}
}
