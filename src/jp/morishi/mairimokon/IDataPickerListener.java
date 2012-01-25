package jp.morishi.mairimokon;

import java.io.File;

public interface IDataPickerListener {
	public static final int RESULT_OK = 0;
	public static final int RESULT_CANCELED = 1;
	
	void dataPickerDialogClosed(int ret, File file);
	
}
