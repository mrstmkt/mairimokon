package jp.morishi.mairimokon.data;

public class MaiRimokonDataStore {
	private static MaiRimokonData data;
	private static int nowPage;
	
	public static void set(int page, MaiRimokonData data1)
	{
		nowPage = page;
		data = data1;
	}
	public static int getNowPage()
	{
		return nowPage;
	}
	public static MaiRimokonData getMaiRimokonData()
	{
		return data;
	}
	public static boolean pageIncrement()
	{
		if(hasNextPage())
		{
			nowPage++;
			return true;
		}
		else
		{
			nowPage = 0;
			return true;
		}
	}
	public static boolean hasNextPage()
	{
		if(nowPage + 1 < data.getPanelInfoList().size())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public static boolean pageDecrement()
	{
		if(hasPrevPage())
		{
			nowPage--;
			return true;
		}
		else
		{
			if(data.getPanelInfoList().size() > 1)
			{
				nowPage = data.getPanelInfoList().size() - 1;
			}
			else
			{
				nowPage = 0;
			}
			return true;
		}
	}
	public static boolean hasPrevPage()
	{
		if(nowPage - 1 >= 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
