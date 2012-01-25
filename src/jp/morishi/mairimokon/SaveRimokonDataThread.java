package jp.morishi.mairimokon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import jp.morishi.mairimokon.data.MaiRimokonData;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SaveRimokonDataThread extends Thread {
	private ProgressDialog waitDialog;
	private Context context;
	private File file;
	private Handler handler;
	private MaiRimokonData data;
	public SaveRimokonDataThread(Context contex, String message, File file, SaveRimokonDataHandler saveHandler) {
		this.context = contex;
		this.waitDialog = new ProgressDialog(context);
		this.waitDialog.setMessage(message);
		this.waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.waitDialog.setCancelable(false);
		this.file = file;
		this.data = null;
		final SaveRimokonDataHandler handler = saveHandler;
		this.handler = new Handler(){
			public void handleMessage(Message msg){
				Bundle ret = msg.getData();
				handler.saveFinished(ret.getInt("ret"), data, context);
				
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
		try {
			FileInputStream is = new FileInputStream(this.file);
			MaiRimokonData rimokonData = DataPicker.createRimokonData(this.context, is);
			if(rimokonData != null)
			{
				if(rimokonData.save())
				{
					this.data = rimokonData;
					ret = true;
				}
				else
				{
					ret = false;
				}
			}
			else
			{
				ret = false;
			}
		} catch (FileNotFoundException e) {
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
	

}
