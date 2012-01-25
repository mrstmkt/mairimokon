package jp.morishi.mairimokon.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class MaiPanelInfo  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "PANEL";
	public static final String COLUMN_RIMOKON_NO = "RIMOKON_NO";
	public static final String COLUMN_NO = "NO";
	public static final String COLUMN_TYPE = "TYPE";
	public static final String COLUMN_TITLE = "TITLE";
	
	public static final int TYPE1 = 0;
	public static final int TYPE2 = 1;
	public static final int TYPE3 = 2;
	public static final int TYPE4 = 3;
	
	private int no;
	private int type;
	private ArrayList<MaiButtonInfo> buttonInfoList;
	private String title;
	private MaiRimokonData parent;
	
	public MaiPanelInfo()
	{
		this.type = TYPE1;
		this.buttonInfoList = new ArrayList<MaiButtonInfo>();
		this.title = "";
	}
	public boolean loadButtons(SQLiteDatabase sdb)
	{
		boolean ret = true;
		getButtunInfoList().clear();
		try{
			final String[] columns = new String[]{MaiButtonInfo.COLUMN_NO,
													MaiButtonInfo.COLUMN_COLOR,
													MaiButtonInfo.COLUMN_FORMAT,
													MaiButtonInfo.COLUMN_UPPERLABEL,
													MaiButtonInfo.COLUMN_INNERLABEL,
													MaiButtonInfo.COLUMN_LONGPUSH,
													MaiButtonInfo.COLUMN_DISABLE};
			String where = MaiButtonInfo.COLUMN_RIMOKON_NO + "=? and " + 
							MaiButtonInfo.COLUMN_PANEL_NO  + "=?";
			String params[] = new String[] {String.valueOf(getParent().getNo()),
											String.valueOf(getNo())};
			Cursor c = sdb.query(MaiButtonInfo.TABLE_NAME,columns,where, params,
							null,null, null, null);
			while(c.moveToNext())
			{
				int no = c.getInt(0);
				int color = c.getInt(1);
				int format = c.getInt(2);
				String upperLabel = c.getString(3);
				String innerLabel = c.getString(4);
				int longPushInt = c.getInt(5);
				int disableInt = c.getInt(6);
				if(upperLabel == null)
				{
					upperLabel = "";
				}
				if(innerLabel == null)
				{
					innerLabel = "";
				}
				boolean longPush;
				if(longPushInt == 0)
				{
					longPush = false;
				}
				else
				{
					longPush = true;
				}
				boolean disable;
				if(disableInt == 0)
				{
					disable = false;
				}
				else
				{
					disable = true;
				}
				MaiButtonInfo buttonInfo = new MaiButtonInfo();
				buttonInfo.setParent(this);
				buttonInfo.setNo(no);
				buttonInfo.setColor(color);
				buttonInfo.setFormat(format);
				buttonInfo.setUpperLabel(upperLabel);
				buttonInfo.setInnerLabel(innerLabel);
				buttonInfo.setLongPush(longPush);
				buttonInfo.setDisable(disable);
				if(buttonInfo.loadFrames(sdb))
				{
					getButtunInfoList().add(buttonInfo);
				}
				else
				{
					ret = false;
					break;
				}
			}
			c.close();
			if(getButtunInfoList().size() > 0)
			{
				ret = true;
			}
			else
			{
				ret = false;
			}
		}catch(SQLiteException e){
			ret = false;
		}		
		finally
		{
		}
		
		return ret;
	}
	public boolean saveButtons(SQLiteDatabase sdb)
	{
		boolean ret = true;
		if(this.buttonInfoList == null || this.buttonInfoList.size() == 0)
		{
			return false;
		}
		try{
			long rowId = -1;
			for(int i = 0; i < this.buttonInfoList.size(); i++)
			{
				ContentValues values = new ContentValues();
				values.put(MaiButtonInfo.COLUMN_RIMOKON_NO, getParent().getNo());
				values.put(MaiButtonInfo.COLUMN_PANEL_NO, getNo());
				values.put(MaiButtonInfo.COLUMN_NO, i);
				values.put(MaiButtonInfo.COLUMN_COLOR, this.buttonInfoList.get(i).getColor());
				values.put(MaiButtonInfo.COLUMN_FORMAT, this.buttonInfoList.get(i).getFormat());
				values.put(MaiButtonInfo.COLUMN_UPPERLABEL, this.buttonInfoList.get(i).getUpperLabel());
				values.put(MaiButtonInfo.COLUMN_INNERLABEL, this.buttonInfoList.get(i).getInnerLabel());
				int longPush;
				if(this.buttonInfoList.get(i).isLongPush())
				{
					longPush = 1;
				}
				else
				{
					longPush = 0;
				}
				values.put(MaiButtonInfo.COLUMN_LONGPUSH, longPush);
				int disable;
				if(this.buttonInfoList.get(i).isDisable())
				{
					disable = 1;
				}
				else
				{
					disable = 0;
				}
				values.put(MaiButtonInfo.COLUMN_DISABLE, disable);
				rowId = sdb.insert(MaiButtonInfo.TABLE_NAME, null, values);
				if(rowId < 0)
				{
					ret = false;
					break;
				}
				this.buttonInfoList.get(i).setNo(i);
				ret = this.buttonInfoList.get(i).saveFrames(sdb);
				if(ret == false)
				{
					break;
				}
			}
		}catch(SQLiteException e){
			ret = false;
		}		
		
		return ret;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public int getNo() {
		return no;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
	}
	public void setButtunInfoList(ArrayList<MaiButtonInfo> buttunInfoList) {
		this.buttonInfoList = buttunInfoList;
		if(this.buttonInfoList != null)
		{
			for(MaiButtonInfo button : this.buttonInfoList)
			{
				button.setParent(this);
			}
		}
	}
	public ArrayList<MaiButtonInfo> getButtunInfoList() {
		return buttonInfoList;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setParent(MaiRimokonData parent) {
		this.parent = parent;
	}
	public MaiRimokonData getParent() {
		return parent;
	}

}
