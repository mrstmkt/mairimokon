package jp.morishi.mairimokon;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;

public interface SearchRimokonFileHandler {
	void searchFinished(int ret, ArrayList<File>fileList, Context context);
}
