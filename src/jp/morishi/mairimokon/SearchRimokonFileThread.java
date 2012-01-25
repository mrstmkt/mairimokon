package jp.morishi.mairimokon;


import java.io.File;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class SearchRimokonFileThread extends Thread {
	private ProgressDialog waitDialog;
	private Context context;
	private Handler handler;
	private ArrayList<File> files;
	public SearchRimokonFileThread(Context contex, String message, SearchRimokonFileHandler searchHandler) {
		this.context = contex;
		this.waitDialog = new ProgressDialog(context);
		this.waitDialog.setMessage(message);
		this.waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.waitDialog.setCancelable(false);
		this.files = new ArrayList<File>();
		final SearchRimokonFileHandler handler = searchHandler;
		this.handler = new Handler(){
			public void handleMessage(Message msg){
				Bundle ret = msg.getData();
				handler.searchFinished(ret.getInt("ret"), files, context);
				
		        // プログレスダイアログ終了
		        waitDialog.dismiss();
		        waitDialog = null;
		    }    			
		};
	}
	public void showDialog()
	{
		this.waitDialog.show();
	}
	
	@Override
	public void run()
	{
		boolean ret = false;
		File file = Environment.getExternalStorageDirectory();
		searchFiles(file, 3);
		
		if(files.size() > 0)
		{
			ret = true;
		}
		else
		{
			ret = false;
		}

		
		Message msg = new Message();
		Bundle retBundle = new Bundle();
		if(ret == true)
		{
			retBundle.putInt("ret", 0);
			msg.setData(retBundle);
		}
		else
		{
			retBundle.putInt("ret", 1);
			msg.setData(retBundle);
		}
		handler.sendMessage(msg);
			
	}
	
	private void searchFiles(File f, int i){
    	if(f.isDirectory()){ // ディレクトリならそれ以下のディレクトリ、ファイルを検査
    		File[] files = f.listFiles();
    		if(files != null)
    		{
        		for(File file : files){
        			if(i - 1 > 0)
        			{
            			searchFiles(file, i - 1); // 再帰
        			}
        		}
    		}
    	}else{ // ファイルの場合はリモコンデータファイルかどうかの判断
    		if(f.getName().endsWith("mrxml") ){
    			files.add(f);
    		}
    	}
    }
	

}
