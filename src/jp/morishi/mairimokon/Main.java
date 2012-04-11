package jp.morishi.mairimokon;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import jp.morishi.mairimokon.data.MaiRimokonData;
import jp.morishi.mairimokon.data.MaiRimokonDataStore;
import jp.morishi.mairimokon.data.SelectRimokon;
import jp.morishi.mairimokon.data.IntentData;

public class Main extends Activity {
	private MaiRimokonData rimokonData;
	private int nowPage;
	private int selectedIndex;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.rimokonData = null;
        this.nowPage = -1;
        this.selectedIndex = -1;
        
        setContentView(R.layout.main);
        setMessage("");
        Bundle extra = getIntent().getExtras();
        if(extra != null)
        {
        	String command = (String)extra.getString("INTENTDATA");
        	if(command != null)
        	{
        		if(command.equals(IntentData.COMMAND_LOADDATA))
        		{
        			this.rimokonData = MaiRimokonDataStore.getMaiRimokonData();
        			this.nowPage = MaiRimokonDataStore.getNowPage();
        			startDataPicker();
            		return;
        		}
        		else if(command.equals(IntentData.COMMAND_SELECTDATA))
        		{
        			this.rimokonData = MaiRimokonDataStore.getMaiRimokonData();
        			this.nowPage = MaiRimokonDataStore.getNowPage();
        			selectRimokonData();
            		return;
        		}
        		else if(command.equals(IntentData.COMMAND_DELETEDATA))
        		{
        			this.rimokonData = MaiRimokonDataStore.getMaiRimokonData();
        			this.nowPage = MaiRimokonDataStore.getNowPage();
        			deleteRimokonData();
            		return;
        		}
        		else if(command.equals(IntentData.COMMAND_PAGENEXT))
        		{
        	        setMessage("");
        			
    				MaiRimokonDataStore.pageIncrement();
    				launchRimokonActivity(MaiRimokonDataStore.getNowPage(), IntentData.DIRECTION_RIGHT, MaiRimokonDataStore.getMaiRimokonData());
            		return;
        		}
        		else if(command.equals(IntentData.COMMAND_PAGEPREV))
        		{
        	        setMessage("");
    				MaiRimokonDataStore.pageDecrement();
    				launchRimokonActivity(MaiRimokonDataStore.getNowPage(), IntentData.DIRECTION_LEFT, MaiRimokonDataStore.getMaiRimokonData());
            		return;
        		}
        	}
        }
        setMessage("");
        
        Resources res = getResources();
        setMessage(res.getString(R.string.loading_message));
        String message = res.getString(R.string.loading_message);
        LoadRimokonDataThread thread = new LoadRimokonDataThread(this, message, new LoadRimokonDataHandler() {
			
			public void saveFinished(int ret, int panelNo, MaiRimokonData data, Context context) {
				nowPage = panelNo;
				rimokonData = data;
				if(ret == 0)
				{
	        		launchRimokonActivity(nowPage, IntentData.DIRECTION_STAY, rimokonData);
				}
				else
				{
			        Resources res = getResources();
			        setMessage(res.getString(R.string.loaddata_nodata_message));
				}
			}
		});
        thread.showDialog();
        thread.start();
        		
        		
        		
//        SelectRimokon selectRimokon = new SelectRimokon(this);
//        if(selectRimokon.getSelectedDataInfo())
//        {
//            this.rimokonData = new MaiRimokonData(this);
//            this.rimokonData.setNo(selectRimokon.getRimokonNo());
//        	if(this.rimokonData.load())
//        	{
//        		launchRimokonActivity(selectRimokon.getPanelNo(), IntentData.DIRECTION_STAY, this.rimokonData);
//        	}
//        }
//        this.rimokonData = new MaiRimokonData(this);
//        this.nowPage = -1;
//        setMessage(res.getString(R.string.loaddata_nodata_message));
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
//    	switch(item.getItemId())
//    	{
//    	case R.id.loaddata_item:
//    		startDataPicker();
//    		break;
//    	case R.id.selectdata_Item:
//    		selectRimokonData();
//    		break;
//    	case R.id.deletedata_item:
//    		deleteRimokonData();
//    		break;
//    	}
    	if(item.getItemId() == R.id.loaddata_item)
    	{
    		startDataPicker();
    	}
    	else if(item.getItemId() == R.id.selectdata_Item)
    	{
    		selectRimokonData();
    	}
    	else if(item.getItemId() == R.id.deletedata_item)
    	{
    		deleteRimokonData();
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    private void startDataPicker()
    {
//		Intent intent = DataPicker.createFilePickerIntent();
//		startActivityForResult(intent, 0);
    	DataPicker dataPicker = new DataPicker(this, new IDataPickerListener() {
			
			public void dataPickerDialogClosed(int ret, File file) {
				if(ret == IDataPickerListener.RESULT_OK)
				{
					readRimokonData(file);
				}
				else
				{
					if(launchRimokonActivity(nowPage, IntentData.DIRECTION_STAY, rimokonData) == false)
					{
						Resources res = getResources();
						setMessage(res.getString(R.string.loaddata_error_message));
					}
				}
			}
		});
    	dataPicker.open();
    }
    
    private void readRimokonData(File file)
    {
		Resources res = getResources();
		String message = res.getString(R.string.datasaving_dialog_message);
    	SaveRimokonDataThread thread = new SaveRimokonDataThread(this, message, file, new SaveRimokonDataHandler() {
			
			public void saveFinished(int ret, MaiRimokonData data, Context context) {
				if(ret == 0)
				{
					if(SelectRimokon.select(context, null, data.getNo(), 0) == true)
					{
						launchRimokonActivity(0, IntentData.DIRECTION_STAY, data);
					}
					else
					{
						Toast.makeText(context, R.string.loaddata_error_message, Toast.LENGTH_LONG).show();
						if(launchRimokonActivity(nowPage, IntentData.DIRECTION_STAY, rimokonData) == false)
						{
							Resources res = getResources();
							setMessage(res.getString(R.string.loaddata_error_message));
						}
					}
				}
				else
				{
					Toast.makeText(context, R.string.loaddata_error_message, Toast.LENGTH_LONG).show();
					if(launchRimokonActivity(nowPage, IntentData.DIRECTION_STAY, rimokonData) == false)
					{
						Resources res = getResources();
						setMessage(res.getString(R.string.loaddata_error_message));
					}
				}
			}
		});
    	thread.showDialog();
    	thread.start();
    	
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
//    	if(resultCode == Activity.RESULT_OK)
//    	{
//    		/* ファイルパス（選択されたUriのリストを格納したテキストファイル）の取得 */
//    		String readFile = data.getStringExtra(
//    		    "jp.co.sharp.android.contentsmanager.EXTRA_KEY_SELECT_LIST"
//    		);
//    		File file = new File(readFile);
//    		try {
//				FileInputStream is = new FileInputStream(file);
//				MaiRimokonData rimokonData = DataPicker.createRimokonData(this, is);
//				if(rimokonData != null)
//				{
//					if(rimokonData.save())
//					{
//						if(SelectRimokon.select(this, null, rimokonData.getNo(), 0) == true)
//						{
//							launchRimokonActivity(0, rimokonData);
//						}
//						else
//						{
//							Toast.makeText(this, R.string.loaddata_error_message, Toast.LENGTH_LONG).show();
//							if(launchRimokonActivity(this.nowPage, this.rimokonData) == false)
//							{
//								Resources res = getResources();
//								setMessage(res.getString(R.string.loaddata_error_message));
//							}
//						}
//					}
//					else
//					{
//						Toast.makeText(this, R.string.loaddata_error_message, Toast.LENGTH_LONG).show();
//						if(launchRimokonActivity(this.nowPage, this.rimokonData) == false)
//						{
//							Resources res = getResources();
//							setMessage(res.getString(R.string.loaddata_error_message));
//						}
//					}
//				}
//				else
//				{
//					Toast.makeText(this, R.string.loaddata_error_message, Toast.LENGTH_LONG).show();
//					if(launchRimokonActivity(this.nowPage, this.rimokonData) == false)
//					{
//					}
//				}
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//				Toast.makeText(this, R.string.loaddata_error_message, Toast.LENGTH_LONG).show();
//				if(launchRimokonActivity(this.nowPage, this.rimokonData) == false)
//				{
//					Resources res = getResources();
//					setMessage(res.getString(R.string.loaddata_error_message));
//				}
//			}
//    	}
//    	else
//    	{
//			if(launchRimokonActivity(this.nowPage, this.rimokonData) == false)
//			{
//				setMessage("");
//			}
//    	}
    }
    private void selectRimokonData()
    {
    	final ArrayList<MaiRimokonData> list = MaiRimokonData.getMaiRimokonDataList(this, null);
    	this.selectedIndex = -1;
    	SelectRimokon selectRimokon = new SelectRimokon(this);
    	int no = -1;
    	if(selectRimokon.getSelectedDataInfo())
    	{
    		no = selectRimokon.getRimokonNo();
    	}
    	int checkIndex = -1;
    	final String []items = new String[list.size()];
    	for(int i = 0; i < list.size(); i++)
    	{
    		items[i] = list.get(i).getTitle();
    		if(no == list.get(i).getNo())
    		{
    			checkIndex = i;
    			this.selectedIndex = i;
    		}
    	}
    	AlertDialog.Builder dlb = new AlertDialog.Builder(this);
    	dlb.setCancelable(false);
    	dlb.setTitle(R.string.selectdata_dialog_title);
    	dlb.setSingleChoiceItems(items, checkIndex, new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				selectedIndex = which;
			}
		});
    	dlb.setPositiveButton("OK", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if(selectedIndex >= 0 && selectedIndex < items.length)
				{
					selectRimokon(list, selectedIndex);
//					launchRimokonActivity(nowPage, rimokonData);
				}
				
			}
		});
    	dlb.setNegativeButton("Cancel", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				launchRimokonActivity(nowPage, IntentData.DIRECTION_STAY, rimokonData);
			}
		});
    	dlb.show();
    }
    
    private void selectRimokon(ArrayList<MaiRimokonData> list, int index)
    {
    	MaiRimokonData data = list.get(index);
        Resources res = getResources();
		SelectRimokonDataThread thread = new SelectRimokonDataThread(this, res.getString(R.string.loading_message), data , new SelectRimokonDataHandler() {
			
			public void saveFinished(int ret, MaiRimokonData data, Context context) {
				if(ret == 0)
				{
					nowPage = 0;
					rimokonData = data;
				}
				launchRimokonActivity(nowPage, IntentData.DIRECTION_STAY, rimokonData);
			}
		});
		thread.showDialog();
		thread.start();
    }
    private void deleteRimokonData()
    {
    	final ArrayList<MaiRimokonData> list = MaiRimokonData.getMaiRimokonDataList(this, null);
    	final String []items = new String[list.size()];
    	final boolean []checkedItems = new boolean[list.size()];
    	for(int i = 0; i < list.size(); i++)
    	{
    		items[i] = list.get(i).getTitle();
    		checkedItems[i] = false;
    	}
    	AlertDialog.Builder dlb = new AlertDialog.Builder(this);
    	dlb.setTitle(R.string.deletedata_dialog_title);
    	dlb.setCancelable(false);
    	dlb.setMultiChoiceItems(items, checkedItems, new OnMultiChoiceClickListener() {
			
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				checkedItems[which] = isChecked;
			}
		});
    	dlb.setPositiveButton("OK", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				deleteRimokon(list, items, checkedItems);
//				launchRimokonActivity(nowPage, rimokonData);
			}
		});
    	dlb.setNegativeButton("Cancel", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				launchRimokonActivity(nowPage, IntentData.DIRECTION_STAY, rimokonData);
			}
		});
    	dlb.show();
    }
    private void deleteRimokon(ArrayList<MaiRimokonData> list, String items[], boolean checkedItems[])
    {
        Resources res = getResources();
        DeleteRimokonDataThread thread = new DeleteRimokonDataThread(this, res.getString(R.string.deleting_message), list, checkedItems, new DeleteRimokonDataHandler() {
			
			public void saveFinished(int ret, MaiRimokonData data, Context context) {
				if(ret == 0)
				{
	    			nowPage = 0;
	    			rimokonData = data;
				}
				else
				{
	    			nowPage = -1;
	    			rimokonData = null;
				}
				launchRimokonActivity(nowPage, IntentData.DIRECTION_STAY, rimokonData);
				
			}
		});
        thread.showDialog();
        thread.start();
        
    }
    private void setMessage(String message)
    {
    	TextView textView = (TextView) findViewById(R.id.main_massage);
    	textView.setText(message);
    }
    private boolean launchRimokonActivity(int panelNo, int direction, MaiRimokonData data)
    {
    	if(panelNo < 0 || data == null)
    	{
    		return false;
    	}
    	MaiRimokonDataStore.set(panelNo, data);
    	
    	Intent intent = new Intent();
    	Resources res = getResources();
//        intent.setClassName(
//                "jp.morishi.mairimokon",
//                "jp.morishi.mairimokon.MaiRimokonActivity");
        intent.setClassName(
                res.getString(R.string.intent_package_rimokon),
                res.getString(R.string.intent_classname_rimokon));
        intent.putExtra("INTENTDATA", IntentData.COMMAND_LAUNCH );
        intent.putExtra("DIRECTION", direction);
        startActivity(intent);
        finish();
        return true;
    }
}