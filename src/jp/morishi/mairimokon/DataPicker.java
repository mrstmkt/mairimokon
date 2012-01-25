package jp.morishi.mairimokon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.util.Xml;
import jp.morishi.mairimokon.data.MaiRimokonData;
import jp.morishi.mairimokon.data.MaiPanelInfo;
import jp.morishi.mairimokon.data.MaiButtonInfo;
import jp.morishi.mairimokon.data.IRFrame;

public class DataPicker {
	private static final int  XML_MODE_INIT = -1;
	private static final int XML_MODE_RIMOKONINFO = 0;
	private static final int XML_MODE_PANELINFO = 1;
	private static final int XML_MODE_BUTTONINFO = 2;
	private static final int XML_MODE_IRFRAMEINFO = 3;
	private Context context;
	private IDataPickerListener listener;
	private ArrayList<File> dataFiles;
	private int selectIndex;
	public DataPicker(Context context, IDataPickerListener listener)
	{
		this.context = context;
		this.listener = listener;
	}
	public void open()
	{
		this.selectIndex = -1;

		this.dataFiles = new ArrayList<File>();
		Resources res = this.context.getResources();
		SearchRimokonFileThread thread = new SearchRimokonFileThread(this.context,res.getString(R.string.searchfile_message), new SearchRimokonFileHandler() {
			
			public void searchFinished(int ret, ArrayList<File> fileList,
					Context context) {
				if(ret == 0)
				{
					dataFiles = fileList;
					final String items[] = new String[fileList.size()];
					for(int i = 0; i < fileList.size(); i++)
					{
						items[i] = fileList.get(i).getName();
					}
			    	AlertDialog.Builder dlb = new AlertDialog.Builder(context);
			    	dlb.setTitle(R.string.datafileselect_dialog_title);
			    	dlb.setCancelable(false);
			    	dlb.setSingleChoiceItems(items, -1, new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							selectIndex = which;
							
						}
					});
			    	dlb.setPositiveButton("OK", new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							if(selectIndex >= 0 && selectIndex < items.length)
							{
								success(selectIndex);
							}
							else
							{
								canceled();
							}
							
						}
					});
			    	dlb.setNegativeButton("Cancel", new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							canceled();
						}
					});
			    	dlb.show();
				}
				else
				{
					canceled();
				}
			}
		});
		thread.showDialog();
		thread.start();
		
	}
	
	private void success(int i)
	{
		if(this.listener != null)
		{
			File f = dataFiles.get(i);
			this.listener.dataPickerDialogClosed(IDataPickerListener.RESULT_OK, f);
		}
	}
	private void canceled()
	{
		if(this.listener != null)
		{
			this.listener.dataPickerDialogClosed(IDataPickerListener.RESULT_CANCELED, null);
		}
	}
	public static Intent createFilePickerIntent()
	{
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		/* 明示的インテント */
		intent.setClassName(
		    "jp.co.sharp.android.cm",
		    "jp.co.sharp.android.cm.main.view.ContentManagerActivity"
		);
		 
		/* MIMEタイプの設定 */
		//imageの場合
//		intent.setType("vnd.android.cursor.dir/etc");
		 
		/* カテゴリ設定 */
		ArrayList<String> category = new ArrayList<String>();
		/*必要なカテゴリを追加する*/
//		category.add("18");/*その他*/
		category.add("0");/*その他*/
		intent.putStringArrayListExtra(
		    "jp.co.sharp.android.contentsmanager.EXTRA_KEY_CATEGORY",
		    category
		);
		 
		/* 保存場所設定 */
		ArrayList<String> savePoint = new ArrayList<String>();
		savePoint.add("1");/* 外部Storage */
		intent.putStringArrayListExtra(
		    "jp.co.sharp.android.contentsmanager.EXTRA_KEY_SAVE_POINT",
		    savePoint
		);
		 
		 
		//選択モード設定
		intent.putExtra(
		    "jp.co.sharp.android.contentsmanager.EXTRA_KEY_SELECT_TYPE",
		    "0"
		);  /*一件選択モード*/
		
		return intent;
	}
	
	public static MaiRimokonData createRimokonData(Context context,InputStream is)
	{
		MaiRimokonData data = new MaiRimokonData(context);
		XmlPullParser parser = Xml.newPullParser();
		
		try {
			parser.setInput(is, "utf-8");
			String tag = null;
			int mode = XML_MODE_INIT;
			int eventType = parser.getEventType();
			MaiPanelInfo panelInfo = null;
			MaiButtonInfo buttonInfo = null;
			IRFrame frame = null;
			boolean exit = false;
			while(eventType != XmlPullParser.END_DOCUMENT && exit == false)
			{
				switch(eventType)
				{
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if(tag.equals("rimokoninfo"))
					{
						mode = XML_MODE_RIMOKONINFO;
					}
					else if(tag.equals("panelinfo"))
					{
						mode = XML_MODE_PANELINFO;
						panelInfo = new MaiPanelInfo();
					}
					else if(tag.equals("buttoninfo"))
					{
						mode = XML_MODE_BUTTONINFO;
						buttonInfo = new MaiButtonInfo();
					}
					else if(tag.equals("irinfo"))
					{
						mode = XML_MODE_IRFRAMEINFO;
						frame = new IRFrame();
					}
					else if(tag.equals("title"))
					{
						switch(mode)
						{
						case XML_MODE_RIMOKONINFO:
							data.setTitle(trimWhiteSpace(parser.nextText()));
							break;
						case XML_MODE_PANELINFO:
							if(panelInfo != null)
							{
								panelInfo.setTitle(trimWhiteSpace(parser.nextText()));
							}
							break;
						}
					}
					else if(tag.equals("description"))
					{
						switch(mode)
						{
						case XML_MODE_RIMOKONINFO:
							data.setDescription(trimWhiteSpace(parser.nextText()));
							break;
						}
					}
					else if(tag.equals("type"))
					{
						switch(mode)
						{
						case XML_MODE_PANELINFO:
							if(panelInfo != null)
							{
								panelInfo.setType(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("upperlabel"))
					{
						switch(mode)
						{
						case XML_MODE_BUTTONINFO:
							if(buttonInfo != null)
							{
								buttonInfo.setUpperLabel(trimWhiteSpace(parser.nextText()));
							}
							break;
						}
					}
					else if(tag.equals("innerlabel"))
					{
						switch(mode)
						{
						case XML_MODE_BUTTONINFO:
							if(buttonInfo != null)
							{
								buttonInfo.setInnerLabel(trimWhiteSpace(parser.nextText()));
							}
							break;
						}
					}
					else if(tag.equals("color"))
					{
						switch(mode)
						{
						case XML_MODE_BUTTONINFO:
							if(buttonInfo != null)
							{
								buttonInfo.setColor(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("longpush"))
					{
						switch(mode)
						{
						case XML_MODE_BUTTONINFO:
							if(buttonInfo != null)
							{
								buttonInfo.setLongPush(Boolean.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("disable"))
					{
						switch(mode)
						{
						case XML_MODE_BUTTONINFO:
							if(buttonInfo != null)
							{
								buttonInfo.setDisable(Boolean.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("format"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							String formatStr = trimWhiteSpace(parser.nextText());
							if(buttonInfo != null)
							{
								buttonInfo.setFormat(Integer.valueOf(formatStr));
							}
							if(frame != null)
							{
								frame.setFormat(Integer.valueOf(formatStr));
							}
							break;
						}
					}
					else if(tag.equals("carrierhigh"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setCarrierHigh(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("carrierlow"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setCarrierLow(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("leaderhigh"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setLeaderHigh(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("leaderlow"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setLeaderLow(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("pulse0modulation"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setPulse0Modulation(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("pulse0high"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setPulse0High(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("pulse0low"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setPulse0Low(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("pulse1modulation"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setPulse1Modulation(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("pulse1high"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setPulse1High(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("pulse1low"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setPulse1Low(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("stophigh"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setStopHigh(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("stoplow"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setStopLow(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("frameinterval"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setFrameInterval(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("repeathigh"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setRepeatHigh(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("repeatlow"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null)
							{
								frame.setRepeatLow(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					else if(tag.equals("data"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null && frame.getValue() != null)
							{
								String dataStr = trimWhiteSpace(parser.nextText());
								if(dataStr != null)
								{
									ArrayList<Byte> valueList = new ArrayList<Byte>();
									for(int i = 0; i < dataStr.length(); i = i+2)
									{
										int valueInt = Integer.parseInt(dataStr.substring(i, i + 2), 16);
										valueList.add((byte)valueInt);
									}
									frame.getValue().setValueList(valueList);
								}
							}
							break;
						}
					}
					else if(tag.equals("len"))
					{
						switch(mode)
						{
						case XML_MODE_IRFRAMEINFO:
							if(frame != null && frame.getValue() != null)
							{
								frame.getValue().setValueLength(Integer.valueOf(trimWhiteSpace(parser.nextText())));
							}
							break;
						}
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if(tag.equals("rimokoninfo"))
					{
						exit = true;
					}
					else if(tag.equals("panelinfo"))
					{
						if(panelInfo != null)
						{
							panelInfo.setParent(data);
							data.getPanelInfoList().add(panelInfo);
						}
						panelInfo = null;
						mode = XML_MODE_RIMOKONINFO;
					}
					else if(tag.equals("buttoninfo"))
					{
						if(panelInfo != null && buttonInfo != null)
						{
							buttonInfo.setParent(panelInfo);
							panelInfo.getButtunInfoList().add(buttonInfo);
						}
						buttonInfo = null;
						mode = XML_MODE_PANELINFO;
					}
					else if(tag.equals("irinfo"))
					{
						if(buttonInfo != null && frame != null)
						{
							frame.setParent(buttonInfo);
							buttonInfo.getFrames().add(frame);
						}
						frame = null;
						mode = XML_MODE_BUTTONINFO;
					}
					break;
				}
				if(exit == false)
				{
					eventType = parser.next();
				}
			}
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return data;
	}
	private static String trimWhiteSpace(String str)
	{
		String ret;
		
		ret = str.replace("\n", "");
		ret = ret.trim();
		
		return ret;
	}
}
