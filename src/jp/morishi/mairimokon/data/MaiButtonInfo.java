package jp.morishi.mairimokon.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class MaiButtonInfo  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "BUTTON";
	public static final String COLUMN_RIMOKON_NO = "RIMOKON_NO";
	public static final String COLUMN_PANEL_NO = "PANEL_NO";
	public static final String COLUMN_NO = "NO";
	public static final String COLUMN_COLOR = "COLOR";
	public static final String COLUMN_FORMAT = "FORMAT";
	public static final String COLUMN_UPPERLABEL = "UPPERLABEL";
	public static final String COLUMN_INNERLABEL = "INNERLABEL";
	public static final String COLUMN_LONGPUSH = "LONGPUSH";
	public static final String COLUMN_DISABLE = "DISABLE";
	
	public static final int COLOR_DEFAULT = 0;
	public static final int COLOR_BLUE = 1;
	public static final int COLOR_RED = 2;
	public static final int COLOR_GREEN = 3;
	public static final int COLOR_YELLOW = 4;
	
	private int no;
	private int color;
	private int format;
	private String upperLabel;
	private String innerLabel;
	private boolean longPush;
	private boolean disable;
	private ArrayList<IRFrame> frames;
	private MaiPanelInfo parent;
	public MaiButtonInfo()
	{
		this.color = COLOR_DEFAULT;
		this.format = IRFrame.FORMAT_DENKYO;
		this.upperLabel = "";
		this.innerLabel = "";
		this.longPush = false;
		this.disable = false;
		this.frames = new ArrayList<IRFrame>();
	}
	public boolean loadFrames(SQLiteDatabase sdb)
	{
		boolean ret = true;
		getFrames().clear();
		try{
			final String[] columns = new String[]{IRFrame.COLUMN_NO,
													IRFrame.COLUMN_FORMAT,
													IRFrame.COLUMN_CARRIERHIGH,
													IRFrame.COLUMN_CARRIERLOW,
													IRFrame.COLUMN_LEADERHIGH,
													IRFrame.COLUMN_LEADERLOW,
													IRFrame.COLUMN_PULSE0MODULATION,
													IRFrame.COLUMN_PULSE0HIGH,
													IRFrame.COLUMN_PULSE0LOW,
													IRFrame.COLUMN_PULSE1MODULATION,
													IRFrame.COLUMN_PULSE1HIGH,
													IRFrame.COLUMN_PULSE1LOW,
													IRFrame.COLUMN_STOPHIGH,
													IRFrame.COLUMN_STOPLOW,
													IRFrame.COLUMN_FRAMEINTERVAL,
													IRFrame.COLUMN_REPEATHIGH,
													IRFrame.COLUMN_REPEATLOW,
													IRFrame.COLUMN_VAL,
													IRFrame.COLUMN_LEN };
			String where = IRFrame.COLUMN_RIMOKON_NO + "=? and " + 
							IRFrame.COLUMN_PANEL_NO  + "=? and " +
							IRFrame.COLUMN_BUTTON_NO  + "=? " ;
			String params[] = new String[] { String.valueOf(getParent().getParent().getNo()),
											String.valueOf(getParent().getNo()),
											String.valueOf(getNo())};
			Cursor c = sdb.query(IRFrame.TABLE_NAME,columns,where, params,
							null,null, null, null);
			while(c.moveToNext())
			{
//				int no = c.getInt(0);
				int format = c.getInt(1);
				int carrierHigh = c.getInt(2);
				int carrierLow = c.getInt(3);
				int leaderHigh = c.getInt(4);
				int leaderLow = c.getInt(5);
				int pulse0Modulation = c.getInt(6);
				int pulse0High = c.getInt(7);
				int pulse0Low = c.getInt(8);
				int pulse1Modulation = c.getInt(9);
				int pulse1High = c.getInt(10);
				int pulse1Low = c.getInt(11);
				int stopHigh = c.getInt(12);
				int stopLow = c.getInt(13);
				int frameInterval = c.getInt(14);
				int repeatHigh = c.getInt(15);
				int repeatLow = c.getInt(16);
				String valStr = c.getString(17);
				int len = c.getInt(18);
				ArrayList<Byte> valueList = new ArrayList<Byte>();
				if(valStr != null)
				{
					for(int i = 0; i< valStr.length(); i = i + 2)
					{
						int valueInt = Integer.parseInt(valStr.substring(i, i+2), 16);
						byte b = Byte.valueOf((byte)valueInt);
						valueList.add(b);
					}
				}
				IRFrame frame = new IRFrame();
				frame.setParent(this);
				frame.setFormat(format);
				frame.setCarrierHigh(carrierHigh);
				frame.setCarrierLow(carrierLow);
				frame.setLeaderHigh(leaderHigh);
				frame.setLeaderLow(leaderLow);
				frame.setPulse0Modulation(pulse0Modulation);
				frame.setPulse0High(pulse0High);
				frame.setPulse0Low(pulse0Low);
				frame.setPulse1Modulation(pulse1Modulation);
				frame.setPulse1High(pulse1High);
				frame.setPulse1Low(pulse1Low);
				frame.setStopHigh(stopHigh);
				frame.setStopLow(stopLow);
				frame.setFrameInterval(frameInterval);
				frame.setRepeatHigh(repeatHigh);
				frame.setRepeatLow(repeatLow);
				IRFrameValue value = new IRFrameValue();
				value.setValueList(valueList);
				value.setValueLength(len);
				frame.setValue(value);
				getFrames().add(frame);
			}
			c.close();
		}catch(SQLiteException e){
			ret = false;
		}		
		finally
		{
		}
		
		return ret;
	}
	public boolean saveFrames(SQLiteDatabase sdb)
	{
		boolean ret = true;
		if(this.frames == null || this.frames.size() == 0)
		{
			return true;
		}
		try{

			long rowId = -1;
			for(int i = 0; i < this.frames.size(); i++)
			{
				ContentValues values = new ContentValues();
				values.put(IRFrame.COLUMN_RIMOKON_NO, getParent().getParent().getNo());
				values.put(IRFrame.COLUMN_PANEL_NO, getParent().getNo());
				values.put(IRFrame.COLUMN_BUTTON_NO, getNo());
				values.put(IRFrame.COLUMN_NO, i);
				values.put(IRFrame.COLUMN_FORMAT, this.frames.get(i).getFormat());
				values.put(IRFrame.COLUMN_CARRIERHIGH, this.frames.get(i).getCarrierHigh());
				values.put(IRFrame.COLUMN_CARRIERLOW, this.frames.get(i).getCarrierLow());
				values.put(IRFrame.COLUMN_LEADERHIGH, this.frames.get(i).getLeaderHigh());
				values.put(IRFrame.COLUMN_LEADERLOW, this.frames.get(i).getLeaderLow());
				values.put(IRFrame.COLUMN_PULSE0MODULATION, this.frames.get(i).getPulse0Modulation());
				values.put(IRFrame.COLUMN_PULSE0HIGH, this.frames.get(i).getPulse0High());
				values.put(IRFrame.COLUMN_PULSE0LOW, this.frames.get(i).getPulse0Low());
				values.put(IRFrame.COLUMN_PULSE1MODULATION, this.frames.get(i).getPulse1Modulation());
				values.put(IRFrame.COLUMN_PULSE1HIGH, this.frames.get(i).getPulse1High());
				values.put(IRFrame.COLUMN_PULSE1LOW, this.frames.get(i).getPulse1Low());
				values.put(IRFrame.COLUMN_STOPHIGH, this.frames.get(i).getStopHigh());
				values.put(IRFrame.COLUMN_STOPLOW, this.frames.get(i).getStopLow());
				values.put(IRFrame.COLUMN_FRAMEINTERVAL, this.frames.get(i).getFrameInterval());
				values.put(IRFrame.COLUMN_REPEATHIGH, this.frames.get(i).getRepeatHigh());
				values.put(IRFrame.COLUMN_REPEATLOW, this.frames.get(i).getRepeatLow());

				if(this.frames.get(i).getValue() != null)
				{
					StringBuffer sb = new StringBuffer();
					for(byte b:this.frames.get(i).getValue().getValueList())
					{
						sb.append(String.format("%02x", b));
					}
					values.put(IRFrame.COLUMN_VAL, sb.toString());
					values.put(IRFrame.COLUMN_LEN, this.frames.get(i).getValue().getValueLength());
				}
				else
				{
					values.put(IRFrame.COLUMN_VAL, "");
					values.put(IRFrame.COLUMN_LEN, 0);
				}
				
				rowId = sdb.insert(IRFrame.TABLE_NAME, null, values);
				if(rowId < 0)
				{
					ret = false;
					break;
				}
			}
		}catch(SQLiteException e){
			ret = false;
		}		
		return ret;
	}
	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public int getFormat() {
		return format;
	}

	public void setUpperLabel(String upperLabel) {
		this.upperLabel = upperLabel;
	}

	public String getUpperLabel() {
		return upperLabel;
	}

	public void setInnerLabel(String innerLabel) {
		this.innerLabel = innerLabel;
	}

	public String getInnerLabel() {
		return innerLabel;
	}

	public void setLongPush(boolean longPush) {
		this.longPush = longPush;
	}

	public boolean isLongPush() {
		return longPush;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	public boolean isDisable() {
		return disable;
	}

	public void setFrames(ArrayList<IRFrame> frames) {
		this.frames = frames;
		if(this.frames != null)
		{
			for(IRFrame frame : this.frames)
			{
				frame.setParent(this);
			}
		}
	}

	public ArrayList<IRFrame> getFrames() {
		return frames;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public int getNo() {
		return no;
	}
	public void setParent(MaiPanelInfo parent) {
		this.parent = parent;
	}
	public MaiPanelInfo getParent() {
		return parent;
	}

}
