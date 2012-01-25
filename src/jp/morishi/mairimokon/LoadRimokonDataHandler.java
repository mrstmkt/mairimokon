package jp.morishi.mairimokon;

import android.content.Context;
import jp.morishi.mairimokon.data.MaiRimokonData;

public interface LoadRimokonDataHandler {
	void saveFinished(int ret, int panelNo, MaiRimokonData data, Context context);
}
