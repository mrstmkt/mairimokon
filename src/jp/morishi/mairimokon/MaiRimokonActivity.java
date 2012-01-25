package jp.morishi.mairimokon;

import java.util.ArrayList;

import jp.morishi.mairimokon.data.IntentData;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import jp.morishi.mairimokon.data.MaiRimokonData;
import jp.morishi.mairimokon.data.MaiRimokonDataStore;
import jp.morishi.mairimokon.data.MaiPanelInfo;
import jp.morishi.mairimokon.data.MaiButtonInfo;
import jp.morishi.mairimokon.data.SelectRimokon;

public class MaiRimokonActivity extends Activity {
//	private IntentData intentData;
	private GestureDetector gestureDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        if(extra != null)
        {
        	String command = (String)extra.getString("INTENTDATA");
        	if(command != null)
        	{
        		if(command.equals(IntentData.COMMAND_LAUNCH))
        		{
        			int direction = extra.getInt("DIRECTION");
        			switch(direction)
        			{
        			case IntentData.DIRECTION_LEFT:
    					setTheme(R.style.left_rimokon_theme);
        				break;
        			case IntentData.DIRECTION_RIGHT:
    					setTheme(R.style.right_rimokon_theme);
        				break;
        			case IntentData.DIRECTION_STAY:
    					setTheme(R.style.stay_rimokon_theme);
        				break;
        			default:
    					setTheme(R.style.stay_rimokon_theme);
        				break;
        			}
        			MaiRimokonData data = MaiRimokonDataStore.getMaiRimokonData();
        			int page = MaiRimokonDataStore.getNowPage();
        			if(data != null&& page >= 0 && page < data.getPanelInfoList().size())
        			{
//        				if(data.getPanelInfoList().size() == 1)
//        				{
//        					setTheme(R.style.center_rimokon_theme);
//        				}
//        				else if(page == data.getPanelInfoList().size() - 1)
//        				{
//        					setTheme(R.style.right_rimokon_theme);
//        				}
//        				else if(page == 0)
//        				{
//        					setTheme(R.style.left_rimokon_theme);
//        				}
//        				else
//        				{
//        					setTheme(R.style.center_rimokon_theme);
//        				}
        				super.onCreate(savedInstanceState);
        				MaiPanelInfo panel = data.getPanelInfoList().get(page);
        				setTitle(data.getTitle() + " " + panel.getTitle());
        				switch(panel.getType())
        				{
        				case MaiPanelInfo.TYPE1:
                			setContentView(R.layout.panel_type1);
                			setupButtons(panel);
                			addFlickListener();
                			SelectRimokon.select(this, null, data.getNo(), page);
        					break;
        				case MaiPanelInfo.TYPE2:
                			setContentView(R.layout.panel_type2);
                			setupButtons(panel);
                			addFlickListener();
                			SelectRimokon.select(this, null, data.getNo(), page);
        					break;
        				case MaiPanelInfo.TYPE3:
                			setContentView(R.layout.panel_type3);
                			setupButtons(panel);
                			addFlickListener();
                			SelectRimokon.select(this, null, data.getNo(), page);
        					break;
        				case MaiPanelInfo.TYPE4:
                			setContentView(R.layout.panel_type4);
                			setupButtons(panel);
                			addFlickListener();
                			SelectRimokon.select(this, null, data.getNo(), page);
        					break;
    					default:
    						Toast.makeText(this,R.string.fatal_message, Toast.LENGTH_LONG);
    						finish();
    						break;
        				}
            			return;
        				
        			}
        		}
        	}
        }
		Toast.makeText(this,R.string.fatal_message, Toast.LENGTH_LONG).show();
		finish();
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
    	
    	switch(item.getItemId())
    	{
    	case R.id.loaddata_item:
    		sendSelectMenuIntent(IntentData.COMMAND_LOADDATA);
    		break;
    	case R.id.selectdata_Item:
    		sendSelectMenuIntent(IntentData.COMMAND_SELECTDATA);
    		break;
    	case R.id.deletedata_item:
    		sendSelectMenuIntent(IntentData.COMMAND_DELETEDATA);
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    private void sendSelectMenuIntent(String command)
    {
//    	intentData.setCommand(command);
    	
    	Intent intent = new Intent();
    	Resources res = getResources();
//        intent.setClassName(
//                "jp.morishi.mairimokon",
//                "jp.morishi.mairimokon.Main");
        intent.setClassName(
                res.getString(R.string.intent_package_main),
                res.getString(R.string.intent_classname_main));
        intent.putExtra("INTENTDATA", command );
        startActivity(intent);
        finish();
    	
    }
    private void setupButtons(MaiPanelInfo panel)
    {
    	int buttonIndex = 0;
		LinearLayout rootlayout = (LinearLayout) findViewById(R.id.root_linear_layout);
		ArrayList<MaiButtonInfo> buttonInfoList = panel.getButtunInfoList();
		for(int i = 0; i < rootlayout.getChildCount() && buttonIndex < buttonInfoList.size(); i++)
		{
			View v = rootlayout.getChildAt(i);
			if(v instanceof LinearLayout)
			{
				LinearLayout layout = (LinearLayout) v;
				for(int j = 0; j < layout.getChildCount() && buttonIndex < buttonInfoList.size(); j++)
				{
					View v2 = layout.getChildAt(j);
					if(v2 instanceof MaiButton)
					{
						MaiButton button = (MaiButton) v2;
						button.setButtonInfo(panel.getButtunInfoList().get(buttonIndex));
						buttonIndex++;
					}
				}
			}
		}
    }
    private void goNext()
    {
//    	if(MaiRimokonDataStore.hasNextPage())
//    	{
//    		sendSelectMenuIntent(IntentData.COMMAND_PAGENEXT);
//    	}
    	if(MaiRimokonDataStore.getMaiRimokonData().getPanelInfoList().size() > 1)
    	{
    		sendSelectMenuIntent(IntentData.COMMAND_PAGENEXT);
    	}
    }
    private void goPrev()
    {
//    	if(MaiRimokonDataStore.hasPrevPage())
//    	{
//    		sendSelectMenuIntent(IntentData.COMMAND_PAGEPREV);
//    	}
    	if(MaiRimokonDataStore.getMaiRimokonData().getPanelInfoList().size() > 1)
    	{
    		sendSelectMenuIntent(IntentData.COMMAND_PAGEPREV);
    	}
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
    
    private void addFlickListener()
    {
		this.gestureDetector = 
    		new GestureDetector(this, new OnGestureListener() {
			
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}
			
			public void onShowPress(MotionEvent e) {
				
			}
			
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) {
				return false;
			}
			
			public void onLongPress(MotionEvent e) {
				
			}
			
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				if (e2.getX() - 10 > e1.getX()) {
                    goPrev();
                } else if (e1.getX() - 10 > e2.getX()){
                    goNext();
                }
				return true;
			}
			
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
    }
}
