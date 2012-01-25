package jp.morishi.mairimokon;

import android.content.Context;
import jp.morishi.mairimokon.data.MaiRimokonData;

public interface DeleteRimokonDataHandler {
	void saveFinished(int ret, MaiRimokonData data, Context context);
}
