package jp.morishi.mairimokon;

import android.content.Context;
import jp.morishi.mairimokon.data.MaiRimokonData;

public interface SaveRimokonDataHandler {
	void saveFinished(int ret, MaiRimokonData data, Context context);
}
