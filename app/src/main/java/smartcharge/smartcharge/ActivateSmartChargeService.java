package smartcharge.smartcharge;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.security.Provider;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by jota on 13-09-16.
 */
public class ActivateSmartChargeService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i("LocalService", "Received start id" + startId + ": " + intent);

        Shell.SU.run("echo \"1\" > /sys/class/power_supply/battery/charging_enabled");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        Toast.makeText(this, "On Destroy Called", Toast.LENGTH_SHORT).show();
    }


}
