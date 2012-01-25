package jp.morishi.mairimokon.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class MaiRimokonData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = "RIMOKONDATA";
	public static final String COLUMN_NO = "NO";
	public static final String COLUMN_TITLE = "TITLE";
	public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
	
	private long no;
	private String title;
	private String description;
	private ArrayList<MaiPanelInfo> panelInfoList;
	private Context context;
	public MaiRimokonData(Context context)
	{
		this.no = -1;
		this.title = "";
		this.description = "";
		this.panelInfoList = new ArrayList<MaiPanelInfo>();
		this.context = context;
	}
	public static ArrayList<MaiRimokonData> getMaiRimokonDataList(Context context,SQLiteDatabase db)
	{
		ArrayList<MaiRimokonData> ret = new ArrayList<MaiRimokonData>();
		DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
		SQLiteDatabase sdb = null;
		try{
			if(db == null)
			{
				sdb = helper.getReadableDatabase();
			}
			else
			{
				sdb = db;
			}
			final String[] columns = new String[]{COLUMN_NO, COLUMN_TITLE, COLUMN_DESCRIPTION};
			Cursor c = sdb.query(TABLE_NAME,columns,null, null,
							null, null, COLUMN_NO + " asc", null);
			while(c.moveToNext())
			{
				long no = c.getLong(0);
				String title = c.getString(1);
				String description = c.getString(2);
				if(title == null)
				{
					title = "";
				}
				if(description == null)
				{
					description = "";
				}
				MaiRimokonData data = new MaiRimokonData(context);
				data.setNo(no);
				data.setTitle(title);
				data.setDescription(description);
				ret.add(data);
			}
			c.close();
		}catch(SQLiteException e){
			ret.clear();
		}		
		finally
		{
			if(db == null)
			{
				if(sdb != null)
				{
					sdb.close();
				}
			}
		}
		
		return ret;
	}
	public static boolean deleteMaiRimokonData(Context context, long no)
	{
		boolean ret = false;
		SelectRimokon selectRimokon = new SelectRimokon(context);
		if((ret = selectRimokon.getSelectedDataInfo()) == true)
		{
			DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
			SQLiteDatabase sdb = null;
			try{
				sdb = helper.getWritableDatabase();
				sdb.beginTransaction();
				String where = COLUMN_NO + "=?";
				String[]params = new String[]{String.valueOf(no)};
				int rows = sdb.delete(TABLE_NAME, where, params);
				if(rows > 0)
				{
					if(no == selectRimokon.getRimokonNo())
					{
						SelectRimokon.autoselect(context,sdb);
					}
					sdb.setTransactionSuccessful();
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
				if(sdb != null)
				{
					sdb.endTransaction();
					sdb.close();
				}
			}
		}
		
		return ret;
	}
	public boolean load()
	{
		boolean ret = true;
		DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
		SQLiteDatabase sdb = null;
		try{
			sdb = helper.getReadableDatabase();
			final String[] columns = new String[]{COLUMN_TITLE, COLUMN_DESCRIPTION};
			String where = COLUMN_NO + "=?";
			String param = String.valueOf(getNo());
			Cursor c = sdb.query(TABLE_NAME,columns,where,new String[]{param},
							null,null, null, null);
			if(c.moveToFirst() == true)
			{
				String title = c.getString(0);
				String description = c.getString(1);
				if(title == null)
				{
					title = "";
				}
				if(description == null)
				{
					description = "";
				}
				setTitle(title);
				setDescription(description);

				getPanelInfoList().clear();
				final String[] columns2 = new String[]{MaiPanelInfo.COLUMN_NO,MaiPanelInfo.COLUMN_TITLE, MaiPanelInfo.COLUMN_TYPE};
				String where2 = MaiPanelInfo.COLUMN_RIMOKON_NO + "=?";
				String param2 = String.valueOf(getNo());
				Cursor c2 = sdb.query(MaiPanelInfo.TABLE_NAME,columns2,where2,new String[]{param2},
								null,null, null, null);
				while(c2.moveToNext())
				{
					int panelNo = c2.getInt(0);
					String panelTitle = c2.getString(1);
					int panelType = c2.getInt(2);
					if(panelTitle == null)
					{
						panelTitle = "";
					}
					MaiPanelInfo panelInfo = new MaiPanelInfo();
					panelInfo.setParent(this);
					panelInfo.setNo(panelNo);
					panelInfo.setTitle(panelTitle);
					panelInfo.setType(panelType);
					if(panelInfo.loadButtons(sdb))
					{
						getPanelInfoList().add(panelInfo);
					}
					else
					{
						ret = false;
						break;
					}
				}
				c2.close();
				if(getPanelInfoList().size() > 0)
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
			c.close();
		}catch(SQLiteException e){
			ret = false;
		}		
		finally
		{
			if(sdb != null)
			{
				sdb.close();
			}
		}
		
		return ret;
	}
	public boolean save()
	{
		boolean ret = false;
		if(this.panelInfoList == null || this.panelInfoList.size() == 0)
		{
			return false;
		}
		
		DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
		SQLiteDatabase sdb = null;
		try{
			sdb = helper.getWritableDatabase();
			sdb.beginTransaction();
			ContentValues values = new ContentValues();
			values.put(COLUMN_TITLE, getTitle());
			values.put(COLUMN_DESCRIPTION, getTitle());
			long rowId = sdb.insert(TABLE_NAME, null, values);
			if(rowId >= 0)
			{
				setNo(rowId);
				ret = true;
				long rowId2 = -1;
				for(int i = 0; i < this.panelInfoList.size(); i++)
				{
					values = new ContentValues();
					values.put(MaiPanelInfo.COLUMN_RIMOKON_NO, getNo());
					values.put(MaiPanelInfo.COLUMN_NO, i);
					values.put(MaiPanelInfo.COLUMN_TITLE, this.panelInfoList.get(i).getTitle());
					values.put(MaiPanelInfo.COLUMN_TYPE, this.panelInfoList.get(i).getType());
					rowId2 = sdb.insert(MaiPanelInfo.TABLE_NAME, null, values);
					if(rowId2 < 0)
					{
						ret = false;
						break;
					}
					this.panelInfoList.get(i).setNo(i);
					ret = this.panelInfoList.get(i).saveButtons(sdb);
					if(ret == false)
					{
						break;
					}
				}
				if(ret == true)
				{
			        sdb.setTransactionSuccessful();
				}
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
			if(sdb != null)
			{
				sdb.endTransaction();
				sdb.close();
			}
		}
		return ret;
	}
	public void setNo(long no) {
		this.no = no;
	}
	public long getNo() {
		return no;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setPanelInfoList(ArrayList<MaiPanelInfo> panelInfoList) {
		this.panelInfoList = panelInfoList;
		if(this.panelInfoList != null)
		{
			for(MaiPanelInfo panel : this.panelInfoList)
			{
				panel.setParent(this);
			}
		}
	}
	public ArrayList<MaiPanelInfo> getPanelInfoList() {
		return panelInfoList;
	}
}
