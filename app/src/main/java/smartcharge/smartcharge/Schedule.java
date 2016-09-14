package smartcharge.smartcharge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Schedule extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        AlarmManager am;
        TimePicker tp;
        Calendar calendar;
        Context context;
        PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.context = this;
        calendar = Calendar.getInstance();
        tp = (TimePicker) findViewById(R.id.timePicker);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Intent my_intent = new Intent(this.context, Alarm_Receiver.class);
        Button but = (Button) findViewById(R.id.but1);
        assert but != null;
        but.setOnClickListener(new View.OnClickListener(){


            public void onClick(View v){
                int total = tp.getCurrentHour()*100 + tp.getCurrentMinute();
                Intent myIntent = new Intent(Schedule.this, SmartCharge.class);
                myIntent.putExtra("horaTotal", total);
                Schedule.this.startActivity(myIntent);
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
        getMenuInflater().inflate(R.menu.schedule, menu);
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

        if (id == R.id.inicio) {
            Intent myIntent = new Intent(Schedule.this, SmartCharge.class);
            Schedule.this.startActivity(myIntent);
        } else if (id == R.id.consejos) {
            Intent myIntent = new Intent(Schedule.this, Consejos.class);
            Schedule.this.startActivity(myIntent);
        } else if (id == R.id.about) {
            Intent myIntent = new Intent(Schedule.this, About.class);
            Schedule.this.startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
