package smartcharge.smartcharge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import eu.chainfire.libsuperuser.Shell;
import android.os.BatteryManager;
import android.content.Intent;
import android.content.IntentFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SmartCharge extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    AlarmManager am;
    Calendar calendar;
    Context context;
    PendingIntent pendingIntent;
    Button but;
    int battLevel;
    int schedule_time;
    boolean active = false;
    boolean already_schedule = false;
    boolean was_called = false;
    boolean schedule_active = false;
    private TextView batt;
    private TextView time;

    private BroadcastReceiver mBatInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            battLevel = level;
            SimpleDateFormat h = new SimpleDateFormat("HH");
            SimpleDateFormat m = new SimpleDateFormat("mm");
            Calendar cal = Calendar.getInstance();

            final String nowHour = h.format(cal.getTime());
            final String nowMin = m.format(cal.getTime());
            batt.setText("Bateria Actual: " + String.valueOf(level) + "%");
            boolean su = false;
            su = Shell.SU.available();
            int usbCharge = BatteryManager.BATTERY_PLUGGED_USB;
            if(su){
                    if(schedule_active){

                        int nHour = Integer.parseInt(nowHour);
                        int nMin = Integer.parseInt(nowMin);
                        int nTotal = nHour*100 + nMin;
                        but = (Button) findViewById(R.id.ap1);

                        if(active && nTotal >= schedule_time){
                            schedule_active = false;
                            schedule_time = 0;
                            but.performClick();
                        }

                        if(battLevel >= 99 && active && !was_called){
                            Shell.SU.run("echo \"0\" > /sys/class/power_supply/battery/charging_enabled");
                            Toast.makeText(getApplicationContext(),"¡Cargado de Bateria Desactivado!",Toast.LENGTH_SHORT).show();
                            was_called = true;
                        }else{
                            if(battLevel < 99 && was_called && active) {
                                Shell.SU.run("echo \"1\" > /sys/class/power_supply/battery/charging_enabled");
                                Toast.makeText(getApplicationContext(), "¡Cargado de Bateria Activado!", Toast.LENGTH_SHORT).show();
                                was_called = false;
                            }
                        }

                    }else if (!schedule_active){
                        if(battLevel >= 99 && active && !was_called){
                            Shell.SU.run("echo \"0\" > /sys/class/power_supply/battery/charging_enabled");
                            Toast.makeText(getApplicationContext(),"¡Cargado de Bateria Desactivado!",Toast.LENGTH_SHORT).show();
                            was_called = true;
                        }else{
                            if(battLevel < 99 && was_called && active) {
                                Shell.SU.run("echo \"1\" > /sys/class/power_supply/battery/charging_enabled");
                                Toast.makeText(getApplicationContext(), "¡Cargado de Bateria Activado!", Toast.LENGTH_SHORT).show();
                                was_called = false;
                            }
                        }
                    }

            }else {
                Toast.makeText(getApplicationContext(),"Tu telefono no esta Rooteado, es posible (MUY) que no sirva :/",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_charge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        time = (TextView) findViewById(R.id.time);
        but = (Button) findViewById(R.id.ap1);
        batt = (TextView) findViewById(R.id.bat);
        this.context = this;
        calendar = Calendar.getInstance();
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Intent my_intent = new Intent(this.context, Alarm_Receiver.class);
        this.registerReceiver(this.mBatInfo, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active = !active;
                if(active){
                    Toast.makeText(getApplicationContext(),"ACTIVADO",Toast.LENGTH_SHORT).show();
                    but.setText("Desactivar");
                }else if(!active){
                    Toast.makeText(getApplicationContext(),"DESACTIVADO",Toast.LENGTH_SHORT).show();
                    but.setText("Activar");
                }

                if(was_called){
                    Shell.SU.run("echo \"1\" > /sys/class/power_supply/battery/charging_enabled");
                    was_called = false;
                }

                if(savedInstanceState == null){
                    Bundle extras = getIntent().getExtras();
                    if(extras != null){
                        schedule_time = extras.getInt("horaTotal");
                        schedule_active = true;
                        SimpleDateFormat h = new SimpleDateFormat("HH");
                        SimpleDateFormat m = new SimpleDateFormat("mm");
                        Calendar cal = Calendar.getInstance();
                        final String nowHour = h.format(cal.getTime());
                        final String nowMin = m.format(cal.getTime());
                        int nHour = Integer.parseInt(nowHour);
                        int nMin = Integer.parseInt(nowMin);
                        int nTotal = nHour*100 + nMin;
                        if(nTotal > schedule_time){
                            calendar.add(Calendar.DATE, 1);
                            calendar.set(Calendar.HOUR_OF_DAY, schedule_time / 100);
                            calendar.set(Calendar.MINUTE, (schedule_time - (schedule_time/100)*100));
                            calendar.set(Calendar.SECOND, 0);
                            pendingIntent = PendingIntent.getBroadcast(SmartCharge.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                        }else {
                            calendar.set(Calendar.HOUR_OF_DAY, schedule_time / 100);
                            calendar.set(Calendar.MINUTE, (schedule_time - (schedule_time / 100) * 100));
                            calendar.set(Calendar.SECOND, 0);
                            pendingIntent = PendingIntent.getBroadcast(SmartCharge.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        }
                        if(schedule_time/100 < 10 && (schedule_time - (schedule_time / 100) * 100) > 10 ){
                            time.setText("Hora Programada: " + "0" + schedule_time/100 + ":" + (schedule_time - (schedule_time/100)*100));
                            already_schedule = true;
                        }


                        if(schedule_time/100 > 10 && (schedule_time - (schedule_time / 100) * 100) < 10 ){
                            time.setText("Hora Programada: " + schedule_time/100 + ":" + "0" + (schedule_time - (schedule_time/100)*100));
                            already_schedule = true;
                        }


                        if(schedule_time/100 < 10 && (schedule_time - (schedule_time / 100) * 100) < 10 ){
                            time.setText("Hora Programada: " + "0" + schedule_time/100 + ":" + "0" + (schedule_time - (schedule_time/100)*100));
                            already_schedule = true;
                        }


                        if(schedule_time/100 > 10 && (schedule_time - (schedule_time / 100) * 100) > 10 ){
                            time.setText("Hora Programada: " + schedule_time/100 + ":" + (schedule_time - (schedule_time/100)*100));
                            already_schedule = true;
                        }
                    }
                }

                if(!schedule_active && already_schedule){
                    time.setText("Hora no programada");
                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.smart_charge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.about) {
            if(active){
                but = (Button) findViewById(R.id.ap1);
                but.performClick();
            }
            Intent myIntent = new Intent(SmartCharge.this, About.class);
            SmartCharge.this.startActivity(myIntent);


        } else if (id == R.id.schedule) {
            if(active){
                but = (Button) findViewById(R.id.ap1);
                but.performClick();
            }
            Intent myIntent = new Intent(SmartCharge.this, Schedule.class);
            SmartCharge.this.startActivity(myIntent);

        } else if (id == R.id.consejos) {
            if(active){
                but = (Button) findViewById(R.id.ap1);
                but.performClick();
            }
            Intent myIntent = new Intent(SmartCharge.this, Consejos.class);
            SmartCharge.this.startActivity(myIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
