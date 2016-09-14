package smartcharge.smartcharge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jota on 13-09-16.
 */
public class Alarm_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent_service = new Intent(context, ActivateSmartChargeService.class);
        context.startService(intent_service);
    }
}
