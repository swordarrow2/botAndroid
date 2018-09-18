package com.meng.qrtools.mengViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meng.qrtools.R;

/**
 * Created by Administrator on 2018/8/19.
 */

public class mengSeekBar extends LinearLayout{
    private TextView tv;
    private SeekBar sb;
    public mengSeekBar(Context context){
        super(context);
        afterCreate(context);
    }
    public mengSeekBar(Context c,AttributeSet a){
        super(c,a);
        afterCreate(c);
    }
    private void afterCreate(Context context){
        LayoutInflater.from(context).inflate(R.layout.meng_seekbar_view,this);
        tv=(TextView)findViewById(R.id.progress_view_tv);
        sb=(SeekBar)findViewById(R.id.progress_view_sb);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){
                tv.setText("当前:"+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            }
        });
    }

    public void setMax(int max){
        sb.setMax(max);
    }

    public void setProgress(int progress){
        sb.setProgress(progress);
    }

    public int getProgress(){
        return sb.getProgress();
    }
}