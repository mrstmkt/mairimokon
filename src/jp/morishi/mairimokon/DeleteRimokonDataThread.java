package jp.morishi.mairimokon;


import java.util.ArrayList;

import jp.morishi.mairimokon.data.MaiRimokonData;
import jp.morishi.mairimokon.data.SelectRimokon;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class DeleteRimokonDataThread extends Thread {
	private ProgressDialog waitDialog;
	private Context context;
	private Handler handler;
	private ArrayList<MaiRimokonData> datalist;
	private boolean checkedItems[];
	private MaiRimokonData data;
	public DeleteRimokonDataThread(Context contex, String message, ArrayList<MaiRimokonData> list, boolean cItems[], DeleteRimokonDataHandler deleteHandler) {
		this.context = contex;
		this.waitDialog = new ProgressDialog(context);
		this.waitDialog.setMessage(message);
		this.waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.waitDialog.setCancelable(false);
		this.datalist = list;
		this.checkedItems = cItems;
		final DeleteRimokonDataHandler handler = deleteHandler;
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
    	for(int i = 0; i < checkedItems.length; i++)
    	{
    		if(checkedItems[i] == true)
    		{
    			MaiRimokonData data = datalist.get(i);
				MaiRimokonData.deleteMaiRimokonData(context, data.getNo());
    		}
    	}
    	SelectRimokon selectRimokon = new SelectRimokon(context);
    	if(selectRimokon.getSelectedDataInfo())
    	{
    		MaiRimokonData dat = new MaiRimokonData(context);
    		dat.setNo(selectRimokon.getRimokonNo());
    		if(dat.load())
    		{
    			data = dat;
    			ret = true;
    		}
    		else
    		{
    			data = null;
    			ret = false;
    		}
    	}
    	else
    	{
			data = null;
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
