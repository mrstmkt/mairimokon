package jp.morishi.mairimokon.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "MAIRIMOKONDB";

//	public DatabaseOpenHelper(Context context, String name,
//			CursorFactory factory, int version) {
	public DatabaseOpenHelper(Context context) {
		super(context, DBNAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try
		{
			// テーブルの生成
			
	        StringBuilder createSql1 = new StringBuilder();
	        createSql1.append("create table " + MaiRimokonData.TABLE_NAME + " (");
	        createSql1.append(MaiRimokonData.COLUMN_NO + " integer primary key autoincrement not null,");
	        createSql1.append(MaiRimokonData.COLUMN_TITLE + " text,");
	        createSql1.append(MaiRimokonData.COLUMN_DESCRIPTION + " text");
	        createSql1.append(")");

	        db.execSQL( createSql1.toString());

	        StringBuilder createSql2 = new StringBuilder();
	        createSql2.append("create table " + MaiPanelInfo.TABLE_NAME + " (");
	        createSql2.append(MaiPanelInfo.COLUMN_RIMOKON_NO + " integer not null,");
	        createSql2.append(MaiPanelInfo.COLUMN_NO + " integer key not null,");
	        createSql2.append(MaiPanelInfo.COLUMN_TITLE + " text,");
	        createSql2.append(MaiPanelInfo.COLUMN_TYPE + " integer not null, ");
	        createSql2.append("primary key(" + MaiPanelInfo.COLUMN_RIMOKON_NO + "," + MaiPanelInfo.COLUMN_NO + ") ");
	        createSql2.append(")");

	        db.execSQL( createSql2.toString());
	        
	        StringBuilder createSql3 = new StringBuilder();
	        createSql3.append("create table " + MaiButtonInfo.TABLE_NAME + " (");
	        createSql3.append(MaiButtonInfo.COLUMN_RIMOKON_NO + " integer not null,");
	        createSql3.append(MaiButtonInfo.COLUMN_PANEL_NO + " integer not null,");
	        createSql3.append(MaiButtonInfo.COLUMN_NO + " integer not null,");
	        createSql3.append(MaiButtonInfo.COLUMN_COLOR + " integer not null,");
	        createSql3.append(MaiButtonInfo.COLUMN_FORMAT + " integer not null,");
	        createSql3.append(MaiButtonInfo.COLUMN_UPPERLABEL + " string,");
	        createSql3.append(MaiButtonInfo.COLUMN_INNERLABEL + " string,");
	        createSql3.append(MaiButtonInfo.COLUMN_LONGPUSH + " integer not null,");
	        createSql3.append(MaiButtonInfo.COLUMN_DISABLE + " integer not null, ");
	        createSql3.append("primary key(" + MaiButtonInfo.COLUMN_RIMOKON_NO + "," 
	        								+ MaiButtonInfo.COLUMN_PANEL_NO + "," 
	        								+ MaiButtonInfo.COLUMN_NO + ") ");
	        createSql3.append(")");

	        db.execSQL( createSql3.toString());

	        StringBuilder createSql4 = new StringBuilder();
	        createSql4.append("create table " + IRFrame.TABLE_NAME + " (");
	        createSql4.append(IRFrame.COLUMN_RIMOKON_NO + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_PANEL_NO + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_BUTTON_NO + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_NO + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_FORMAT + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_CARRIERHIGH + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_CARRIERLOW + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_LEADERHIGH + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_LEADERLOW + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_PULSE0MODULATION + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_PULSE0HIGH + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_PULSE0LOW + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_PULSE1MODULATION + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_PULSE1HIGH + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_PULSE1LOW + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_STOPHIGH + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_STOPLOW + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_FRAMEINTERVAL + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_REPEATHIGH + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_REPEATLOW + " integer not null,");
	        createSql4.append(IRFrame.COLUMN_VAL + " text,");
	        createSql4.append(IRFrame.COLUMN_LEN + " integer not null, ");
	        createSql4.append("primary key(" + IRFrame.COLUMN_RIMOKON_NO + "," 
					+ IRFrame.COLUMN_PANEL_NO + "," 
					+ IRFrame.COLUMN_BUTTON_NO + "," 
					+ MaiButtonInfo.COLUMN_NO + ") ");
	        createSql4.append(")");

	        db.execSQL( createSql4.toString());

	        StringBuilder createSql5 = new StringBuilder();
	        createSql5.append("create table " + SelectRimokon.TABLE_NAME + " (");
	        createSql5.append(SelectRimokon.COLUMN_KEY + " integer primary key autoincrement not null,");
	        createSql5.append(SelectRimokon.COLUMN_RIMOKON_NO + " integer,");
	        createSql5.append(SelectRimokon.COLUMN_PANEL_NO + " integer ");
	        createSql5.append(")");

	        db.execSQL( createSql5.toString());
	        
	        db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
