package jp.morishi.mairimokon;


import jp.morishi.mairimokon.data.MaiRimokonData;
import jp.morishi.mairimokon.data.SelectRimokon;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class LoadRimokonDataThread extends Thread {
	private ProgressDialog waitDialog;
	private Context context;
	private Handler handler;
	private int panelNo;
	private MaiRimokonData data;
	public LoadRimokonDataThread(Context contex, String message, LoadRimokonDataHandler loadHandler) {
		this.context = contex;
		this.waitDialog = new ProgressDialog(context);
		this.waitDialog.setMessage(message);
		this.waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.waitDialog.setCancelable(false);
		this.data = null;
		this.panelNo = -1;
		final LoadRimokonDataHandler handler = loadHandler;
		this.handler = new Handler(){
			public void handleMessage(Message msg){
				Bundle ret = msg.getData();
				handler.saveFinished(ret.getInt("ret"), panelNo, data, context);
				
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
        SelectRimokon selectRimokon = new SelectRimokon(this.context);
        if(selectRimokon.getSelectedDataInfo())
        {
            this.data = new MaiRimokonData(this.context);
            this.data.setNo(selectRimokon.getRimokonNo());
        	if(this.data.load())
        	{
        		this.panelNo = selectRimokon.getPanelNo();
        		ret = true;
        	}
        	else
        	{
        		this.panelNo = -1;
        		ret = false;
        	}
        }
        else
        {
    		this.panelNo = -1;
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
