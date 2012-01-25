package jp.morishi.mairimokon.data;

import java.io.Serializable;

public class IntentData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String COMMAND_LAUNCH = "LAUNCH";
	public static final String COMMAND_PAGENEXT = "PAGENEXT";
	public static final String COMMAND_PAGEPREV = "PAGEPREV";
	public static final String COMMAND_LOADDATA = "LOADDATA";
	public static final String COMMAND_SELECTDATA = "SELECTDATA";
	public static final String COMMAND_DELETEDATA = "DELETEDATA";
	public static final int DIRECTION_STAY = 0;
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_RIGHT = 2;
	
	private String command;
	private int nowPage;
	private MaiRimokonData rimokonData;
	
	public IntentData()
	{
		this.command = "";
		this.nowPage = 0;
		this.rimokonData = null;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getCommand() {
		return command;
	}
	public boolean load(int no)
	{
		this.rimokonData.setNo(no);
		if(this.rimokonData.load())
		{
			if(rimokonData.getPanelInfoList().size() > 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	public boolean pageIncrement()
	{
		if(hasNextPage())
		{
			this.nowPage++;
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean hasNextPage()
	{
		if(this.nowPage + 1 < this.rimokonData.getPanelInfoList().size())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean pageDecrement()
	{
		if(hasPrevPage())
		{
			this.nowPage--;
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean hasPrevPage()
	{
		if(this.nowPage - 1 >= 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public void setNowPage(int nowPage) {
		this.nowPage = nowPage;
	}
	public int getNowPage() {
		return nowPage;
	}
	public void setRimokonData(MaiRimokonData rimokonData) {
		this.rimokonData = rimokonData;
	}
	public MaiRimokonData getRimokonData() {
		return rimokonData;
	}

}
