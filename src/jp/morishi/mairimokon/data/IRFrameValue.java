package jp.morishi.mairimokon.data;

import java.io.Serializable;
import java.util.ArrayList;

public class IRFrameValue  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Byte> valueList;
	private int valueLength;
	public IRFrameValue()
	{
		this.valueList = new ArrayList<Byte>();
		this.valueLength = 0;
	}
	public void setValueList(ArrayList<Byte> valueList) {
		this.valueList = valueList;
	}
	public ArrayList<Byte> getValueList() {
		return valueList;
	}
	public void setValueLength(int valueLength) {
		this.valueLength = valueLength;
	}
	public int getValueLength() {
		return valueLength;
	}
}
