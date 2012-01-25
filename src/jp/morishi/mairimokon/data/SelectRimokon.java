package jp.morishi.mairimokon.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class SelectRimokon implements Serializable {

	/**
	 * 
	 */
	public static final String TABLE_NAME = "SELECTRIMOKON";
	public static final String COLUMN_KEY = "KEY";
	public static final String COLUMN_RIMOKON_NO = "RIMOKON_NO";
	public static final String COLUMN_PANEL_NO = "PANEL_NO";
	
	private static final long serialVersionUID = 1L;
	private int rimokonNo;
	private int panelNo;
	Context context;
	public SelectRimokon(Context context)
	{
		this.rimokonNo = 0;
		this.panelNo = 0;
		this.context = context;
	}
	public boolean getSelectedDataInfo()
	{
		boolean ret = false;
		
		DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
		SQLiteDatabase sdb = null;
		
		try
		{
			sdb = helper.getReadableDatabase();
			final String[] columns = new String[]{COLUMN_RIMOKON_NO, COLUMN_PANEL_NO};
			String where = COLUMN_KEY + "=?";
			String param = "1";
			Cursor c = sdb.query(TABLE_NAME,columns,where,new String[]{param},
							null,null, null, null);
			if(c.moveToFirst() == true)
			{
				int rimokonNo = c.getInt(0);
				int panelNo = c.getInt(1);
				if(rimokonNo < 0 || panelNo < 0)
				{
					ret = false;
				}
				else
				{
					setRimokonNo(rimokonNo);
					setPanelNo(panelNo);
					ret = true;
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
	public static boolean select(Context context,SQLiteDatabase db, long no, int panelNo)
	{
		boolean ret = true;
		
		DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
		SQLiteDatabase sdb = null;
		
		try
		{
			if(db != null)
			{
				sdb = db;
			}
			else
			{
				sdb = helper.getWritableDatabase();
				sdb.beginTransaction();
			}
			final String[] columns = new String[]{COLUMN_KEY};
			String where = COLUMN_KEY + "=?";
			String param = "1";
			Cursor c = sdb.query(TABLE_NAME,columns,where,new String[]{param},
							null,null, null, null);
			if(c.moveToFirst() == true)
			{
				String key = "1";
				ContentValues values = new ContentValues();
				values.put(COLUMN_RIMOKON_NO, no);
				values.put(COLUMN_PANEL_NO, panelNo);
				sdb.update(TABLE_NAME, values, COLUMN_KEY + "=?", new String[]{key});
				ret = true;
			}
			else
			{
				ContentValues values = new ContentValues();
				values.put(COLUMN_KEY, 1);
				values.put(COLUMN_RIMOKON_NO, no);
				values.put(COLUMN_PANEL_NO, panelNo);
				long rowId = sdb.insert(TABLE_NAME, null, values);
				if(rowId < 0)
				{
					ret = false;
				}
				else
				{
					ret = true;
				}
			}
			c.close();
			if(ret == true)
			{
				if(db == null)
				{
			        sdb.setTransactionSuccessful();
				}
			}
		}catch(SQLiteException e){
			ret = false;
		}		
		finally
		{
			if(db == null)
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
	public static boolean autoselect(Context context, SQLiteDatabase db)
	{
		boolean ret = false;
		
		ArrayList<MaiRimokonData> list = MaiRimokonData.getMaiRimokonDataList(context, db);
		if(list.size() > 0)
		{
			ret = select(context, db, list.get(0).getNo(), 0);
		}
		else
		{
			ret = select(context, db,  -1, -1);
		}
		return ret;
	}
	public void setRimokonNo(int rimokonNo) {
		this.rimokonNo = rimokonNo;
	}
	public int getRimokonNo() {
		return rimokonNo;
	}
	public void setPanelNo(int panelNo) {
		this.panelNo = panelNo;
	}
	public int getPanelNo() {
		return panelNo;
	}
}
