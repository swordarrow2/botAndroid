package com.meng.picTools.qrtools.creator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.picTools.MainActivity2;
import com.meng.picTools.MainActivity;
import com.meng.picTools.R;
import com.meng.picTools.qrtools.lib.ContentHelper;
import com.meng.picTools.qrtools.lib.qrcodelib.QrUtils;
import com.meng.picTools.qrtools.log;
import com.meng.picTools.mengViews.MengColorBar;
import com.meng.picTools.mengViews.MengEditText;
import com.meng.picTools.mengViews.MengScrollView;
import com.meng.picTools.mengViews.MengSeekBar;
import com.meng.picTools.mengViews.MengSelectRectView;
import com.waynejo.androidndkgif.GifDecoder;
import com.waynejo.androidndkgif.GifEncoder;
import com.waynejo.androidndkgif.GifImage;
import com.waynejo.androidndkgif.GifImageIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * gif二维码
 */

public class gifArbAwesome extends Fragment{

    private boolean coding=false;
    private int intGifFrameDelay;
    private int qrSize;
    private Bitmap[] bmpDecodedBitmaps;
    private Button btnEncodeGif;
    private Button btnSelectImage;
    private CheckBox cbAutoColor;
    private CheckBox cbLowMemoryMode;
    private CheckBox cbUseDither;
    private MengEditText mengEtDotScale;
    private MengEditText mengEtTextToEncode;
    private ProgressBar pbCodingProgress;
    private String strSelectedGifPath="";
    private TextView tvImagePath;
    private MengColorBar mColorBar;

    private MengSelectRectView mengSelectView;
    private float screenW;
    private float screenH;
    private int gifWidth;
    private int gifHeight;
    private int bmpCount;
    private MengScrollView msv;
    private MengSeekBar mengSeekBar;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.gif_arb_qr_main,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        msv=(MengScrollView)view.findViewById(R.id.gif_arb_awesome_qrMengScrollView);
        mengSelectView=(MengSelectRectView)view.findViewById(R.id.gif_arb_awesome_qrselectRectView);
        mColorBar=(MengColorBar)view.findViewById(R.id.gif_arb_qr_main_colorBar);
        btnEncodeGif=(Button)view.findViewById(R.id.gif_arb_qr_button_encode_gif);
        btnSelectImage=(Button)view.findViewById(R.id.gif_arb_qr_button_selectImg);
        cbAutoColor=(CheckBox)view.findViewById(R.id.gif_arb_qr_checkbox_autocolor);
        cbLowMemoryMode=(CheckBox)view.findViewById(R.id.gif_arb_qr_checkbox_low_memery);
        cbUseDither=(CheckBox)view.findViewById(R.id.gif_arb_qr_checkbox_dither);
        mengEtDotScale=(MengEditText)view.findViewById(R.id.gif_arb_qr_mengEdittext_dotScale);
        mengEtTextToEncode=(MengEditText)view.findViewById(R.id.gif_arb_qr_mainmengTextview_content);
        pbCodingProgress=(ProgressBar)view.findViewById(R.id.gif_arb_qr_mainProgressBar);
        tvImagePath=(TextView)view.findViewById(R.id.gif_arb_qr_selected_path);
        mengSeekBar=(MengSeekBar)view.findViewById(R.id.gif_arb_qr_mainMengSeekBar);
        cbAutoColor.setOnCheckedChangeListener(check);
        btnSelectImage.setOnClickListener(listenerBtnClick);
        btnEncodeGif.setOnClickListener(listenerBtnClick);
        msv.setSelectView(mengSelectView);
        DisplayMetrics dm=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenW=dm.widthPixels;
        screenH=dm.heightPixels;
    }

    CompoundButton.OnCheckedChangeListener check=new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
            switch(buttonView.getId()){
                case R.id.gif_arb_qr_checkbox_autocolor:
                    mColorBar.setVisibility(isChecked?View.GONE:View.VISIBLE);
                    if(!isChecked) log.t("如果颜色搭配不合理,二维码将会难以识别");
                    break;
            }
        }
    };
    View.OnClickListener listenerBtnClick=new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.gif_arb_qr_button_selectImg:
                    cbLowMemoryMode.setEnabled(false);
                    MainActivity2.selectImage(gifArbAwesome.this);
                    break;
                case R.id.gif_arb_qr_button_encode_gif:
                    if(coding){
                        log.t("正在执行操作");
                    }else{
                        btnSelectImage.setEnabled(false);
                        encodeGIF();
                    }
                    break;
            }
        }
    };

    private void encodeGIF(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    coding=true;
                     String filePath=MainActivity.instence.getGifArbAwesomeQRPath();
                    GifEncoder gifEncoder=new GifEncoder();
                    gifEncoder.setDither(cbUseDither.isChecked());
                    if(cbLowMemoryMode.isChecked()){
                        gifEncoder.init(gifWidth,gifHeight,filePath,GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
                        for(int t=0;t<bmpCount;t++){
                            gifEncoder.encodeFrame(
                                    encodeAwesome(BitmapFactory.decodeFile(MainActivity.instence.getTmpFolder()+t+".png")),
                                    intGifFrameDelay);
                            setProgress((int)((t+1)*100.0f/bmpDecodedBitmaps.length),true);
                        }
                    }else{
                        gifEncoder.init(gifWidth,gifHeight,filePath,GifEncoder.EncodingType.ENCODING_TYPE_FAST);
                        for(int t=0;t<bmpCount;t++){
                            gifEncoder.encodeFrame(
                                    encodeAwesome(bmpDecodedBitmaps[t]),
                                    intGifFrameDelay);
                            setProgress((int)((t+1)*100.0f/bmpDecodedBitmaps.length),true);
                        }
                    }
                    gifEncoder.close();
                    getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(filePath))));
                    log.t("完成 : "+filePath);
                }catch(FileNotFoundException e){
                    log.e(e);
                }
                coding=false;
                System.gc();
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        cbLowMemoryMode.setEnabled(true);
                        btnSelectImage.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    private void decodeGif(final String path){
        if(cbLowMemoryMode.isChecked()){
            new Thread(new Runnable(){
                @Override
                public void run(){
                    GifDecoder gifDecoder=new GifDecoder();
                    GifImageIterator iterator=gifDecoder.loadUsingIterator(path);
                    int flag=0;
                    while(iterator.hasNext()){
                        GifImage next=iterator.next();
                        if(next!=null){
                            gifHeight=next.bitmap.getHeight();
                            gifWidth=next.bitmap.getWidth();
                            try{
                                QrUtils.saveMyBitmap(MainActivity.instence.getTmpFolder()+flag+++".png",next.bitmap);
                            }catch(IOException e){
                                log.e(e);
                            }
                            intGifFrameDelay=next.delayMs;
                        }else{
                            log.e("解码失败，可能文件损坏");
                        }
                    }
                    iterator.close();
                    log.t("共"+(flag-1)+"张,解码成功");
                    bmpCount=flag;
                    coding=false;
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            btnEncodeGif.setVisibility(View.VISIBLE);
                            cbUseDither.setVisibility(View.VISIBLE);
                            tvImagePath.setVisibility(View.VISIBLE);
                            mengSelectView.setup(
                                    BitmapFactory.decodeFile(MainActivity.instence.getTmpFolder()+"0.png"),
                                    screenW,
                                    screenH,
                                    qrSize);
                            ViewGroup.LayoutParams para=mengSelectView.getLayoutParams();
                            para.height=(int)(screenW/gifWidth*gifHeight);
                            mengSelectView.setLayoutParams(para);
                            mengSelectView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }).start();
        }else{
            new Thread(new Runnable(){
                @Override
                public void run(){
                    final GifDecoder gifDecoder=new GifDecoder();
                    if(gifDecoder.load(path)){
                        bmpDecodedBitmaps=new Bitmap[gifDecoder.frameNum()];
                        intGifFrameDelay=gifDecoder.delay(1);
                        for(int i=0;i<gifDecoder.frameNum();i++){
                            bmpDecodedBitmaps[i]=gifDecoder.frame(i);
                            setProgress((int)((i+1)*100.0f/gifDecoder.frameNum()),false);
                        }
                        log.t("共"+(bmpCount=gifDecoder.frameNum())+"张,解码成功");
                    }else{
                        log.e("解码失败，可能不是GIF文件");
                    }
                    coding=false;
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            btnEncodeGif.setVisibility(View.VISIBLE);
                            cbUseDither.setVisibility(View.VISIBLE);
                            tvImagePath.setVisibility(View.VISIBLE);
                            gifHeight=bmpDecodedBitmaps[0].getHeight();
                            gifWidth=bmpDecodedBitmaps[0].getWidth();
                            log.i("setup");
                            mengSelectView.setup(
                                    bmpDecodedBitmaps[0],
                                    screenW,
                                    screenH,
                                    qrSize);
                            log.i("setPara");
                            ViewGroup.LayoutParams para=mengSelectView.getLayoutParams();
                            para.height=(int)(screenW/gifWidth*gifHeight);
                            mengSelectView.setLayoutParams(para);
                            mengSelectView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }).start();
        }
    }

    private Bitmap encodeAwesome(Bitmap bg){
        return QrUtils.generate(
                mengEtTextToEncode.getString(),
                Float.parseFloat(mengEtDotScale.getString()),
                cbAutoColor.isChecked()?Color.BLACK:mColorBar.getTrueColor(),
                cbAutoColor.isChecked()?Color.WHITE:mColorBar.getFalseColor(),
                cbAutoColor.isChecked(),
                between(mengSelectView.getSelectLeft()/mengSelectView.getXishu(),0,bg.getWidth()-qrSize),
                between(mengSelectView.getSelectTop()/mengSelectView.getXishu(),0,bg.getHeight()-qrSize),
                qrSize,
                bg);
    }

    private int between(float a,int min,int max){
        if(a<min) a=min;
        if(a>max) a=max;
        return (int)a;
    }

    private void setProgress(final int p,final boolean encoing){
        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                pbCodingProgress.setProgress(p);
                if(p==100){
                    pbCodingProgress.setVisibility(View.GONE);
                    log.t(encoing?"编码完成":"解码完成");
                }else{
                    if(pbCodingProgress.getVisibility()==View.GONE){
                        pbCodingProgress.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==MainActivity2.SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            try{
                if(coding){
                    log.t("正在执行操作");
                }else{
                    Uri imageUri=data.getData();
                    strSelectedGifPath=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),imageUri);
                    tvImagePath.setText(strSelectedGifPath);
                    final Bitmap selectedBmp=BitmapFactory.decodeFile(strSelectedGifPath);
                    final int selectedBmpWidth=selectedBmp.getWidth();
                    final int selectedBmpHeight=selectedBmp.getHeight();
                    final MengSeekBar msb=new MengSeekBar(getActivity());
                    int maxProg=Math.min(selectedBmpWidth,selectedBmpHeight);
                    msb.setMax(maxProg);
                    msb.setProgress(maxProg/3);
                    new AlertDialog.Builder(getActivity())
                            .setTitle("输入要添加的二维码大小(像素)")
                            .setView(msb)
                            .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface p1,int p2){
                                    qrSize=msb.getProgress();
                                    //ll.addView(new mengSelectRectView(getActivity(),selectedBmp,screenW,screenH));
                                    mengSeekBar.setVisibility(View.VISIBLE);
                                    mengSeekBar.setMax(msb.getMax());
                                    mengSeekBar.setProgress(msb.getProgress());
                                    mengSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                                        @Override
                                        public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){
                                            mengSelectView.setSize(qrSize=mengSeekBar.getProgress());
                                        }
                                        @Override
                                        public void onStartTrackingTouch(SeekBar seekBar){    }
                                        @Override
                                        public void onStopTrackingTouch(SeekBar seekBar){    }
                                    });
                                    mengSelectView.setup(selectedBmp,screenW,screenH,qrSize);
                                    ViewGroup.LayoutParams para=mengSelectView.getLayoutParams();
                                    para.height=(int)(screenW/selectedBmpWidth*selectedBmpHeight);
                                    mengSelectView.setLayoutParams(para);
                                    mengSelectView.setVisibility(View.VISIBLE);
                                    decodeGif(strSelectedGifPath);
                                    if(para.height>screenH*2/3){
                                        log.t("可使用音量键滚动界面");
                                    }
                                    msv.post(new Runnable(){
                                        public void run(){
                                            msv.fullScroll(View.FOCUS_DOWN);
                                        }
                                    });
                                    coding=true;
                                }
                            }).show();
                }
            }catch(Exception e){
                log.e(e);
            }
        }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"用户取消了操作",Toast.LENGTH_SHORT).show();
        }else{
            MainActivity2.selectImage(this);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    public void onKeyDown(int keyCode,KeyEvent event){

        if((keyCode==KeyEvent.KEYCODE_VOLUME_UP)){
            msv.post(new Runnable(){
                public void run(){
                    msv.scrollBy(0,0xffffff9c);//(0xffffff9c)16=(-100)10
                }
            });
        }
        if((keyCode==KeyEvent.KEYCODE_VOLUME_DOWN)){
            msv.post(new Runnable(){
                public void run(){
                    msv.scrollBy(0,100);
                }
            });
        }
    }
}