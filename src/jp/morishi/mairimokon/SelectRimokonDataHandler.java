package jp.morishi.mairimokon;


import jp.morishi.mairimokon.data.MaiRimokonData;

import android.content.Context;

public interface SelectRimokonDataHandler {
	void saveFinished(int ret,  MaiRimokonData data, Context context);
}
