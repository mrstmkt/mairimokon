package jp.morishi.mairimokon;


import jp.morishi.mairimokon.data.MaiRimokonData;
import jp.morishi.mairimokon.data.SelectRimokon;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SelectRimokonDataThread extends Thread {
	private ProgressDialog waitDialog;
	private Context context;
	private Handler handler;
	private MaiRimokonData data;
	public SelectRimokonDataThread(Context contex, String message, MaiRimokonData dat, SelectRimokonDataHandler selectHandler) {
		this.context = contex;
		this.waitDialog = new ProgressDialog(context);
		this.waitDialog.setMessage(message);
		this.waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.waitDialog.setCancelable(false);
		this.data = dat;
		final SelectRimokonDataHandler handler = selectHandler;
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
		long selectedNo = data.getNo();
		if(selectedNo != -1)
		{
			SelectRimokon.select(context, null, selectedNo, 0);
			if(data.load())
			{
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
