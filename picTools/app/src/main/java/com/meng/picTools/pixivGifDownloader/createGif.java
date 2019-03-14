package com.meng.picTools.pixivGifDownloader;

import android.content.*;
import android.net.*;

import com.meng.picTools.*;
import com.waynejo.androidndkgif.*;

import java.io.*;

import android.graphics.*;

import java.util.*;

import com.meng.picTools.qrtools.*;

public class createGif extends Thread {
    private int delay;
    private String pid = "";
    private Context context;
    private String picsFolder = "";
	public boolean isCreated=false;
	public int nowFile=0;

    public createGif(Context context, String folder, String pid, int delay) {
        this.delay = delay;
        this.pid = pid;
        picsFolder = folder;
        this.context = context;
    }

    @Override
    public void run() {
        if (MainActivity.instence.sharedPreference.getBoolean(Data.preferenceKeys.useJava)) {
            createGifJava(picsFolder, pid, delay);
        } else {
            createGifNative(picsFolder, pid, delay);
        }
		isCreated=true;

    }

    private void createGifJava(String folder, String file_name, int delay) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(delay);
        File[] images = new File(folder).listFiles();
        Arrays.sort(images);
        int count = images.length;
        for (int i = 0; i < count; i++) {
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeFile(images[i].getAbsolutePath()));
            nowFile=i;
        }
        localAnimatedGifEncoder.finish();
        String path = MainActivity.instence.getGifPath(file_name);
        try {
            FileOutputStream fos = new FileOutputStream(path);
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        registImage(path);
    }

    private void createGifNative(String folder, String file_name, int d) {
        String filePath = MainActivity.instence.getGifPath(file_name);
        File[] images = new File(folder).listFiles();
        Arrays.sort(images);
        Bitmap bmp = BitmapFactory.decodeFile(images[0].getAbsolutePath());
        GifEncoder gifEncoder = new GifEncoder();
        gifEncoder.setDither(false);
        try {
            gifEncoder.init(bmp.getWidth(), bmp.getHeight(), filePath, GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        //	log.t(filePath+" "+file_name+" "+images.length+" "+folder);
        for (int i = 0; i < images.length; i++) {
            try {
                gifEncoder.encodeFrame(BitmapFactory.decodeFile(images[i].getAbsolutePath()), d);
				nowFile=i;
            } catch (Exception e) {
                log.t(e.toString());
            }
            playLayout.gifProgress.setProgress(i * 100 / images.length);

        }
        gifEncoder.close();
        registImage(filePath);
    }

    private void registImage(String path) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }
}
