package cn.a2end.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.a2end.todolist.UI.ListAdapter;
import cn.a2end.todolist.db.DBHelper;
import cn.a2end.todolist.UI.AddEventDialog;
import cn.a2end.todolist.db.Event;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private List<Event> eventList;
    private List<Event> eventListDefault=new ArrayList<>();
    private List<Event> eventListSortByTime=new ArrayList<>();
    private List<Event> eventListCompleted=new ArrayList<>();
    private List<Event> eventListUncompleted=new ArrayList<>();

    private boolean changed=false;
    private int menuChecked=0;

    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private ListAdapter adapter;
    private ListAdapter adapterDefault;
    private ListAdapter adapterSortByTime=null;
    private ListAdapter adapterCompleted;
    private ListAdapter adapterUncompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView=findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        eventList=eventListDefault;
        //数据库操作
        Log.d("DB","Start DB opration");
        dbHelper=new DBHelper(this,"List.db",null,1);
        try {
            initEventListDefaultDB();
        }catch (Exception e){
            Toast.makeText(this,"出现致命错误,无法操作数据库",Toast.LENGTH_LONG).show();
            System.exit(0);
        }

        //RecyclerView初始化
        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapterDefault=new ListAdapter(eventListDefault);
        recyclerView.setAdapter(adapterDefault);

        //fab按钮监听事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                showAddDialog();
                changed=true;
                //Event event=new Event("2019-1-1 0:0","gfgfg");
                //event.addToDB(MainActivity.this);//添加到数据库
                //eventList.add(event);
            }
        });

        adapter=adapterDefault;
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //右上角菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_fresh) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_default) {
            recyclerView.setAdapter(adapterDefault);
            recyclerView.scheduleLayoutAnimation();

            menuChecked=0;
            adapter=adapterDefault;
            eventList=eventListDefault;
        }

        else if (id == R.id.nav_sortByTime) {
            refreshSortByTime();

            menuChecked=1;
            eventList=eventListSortByTime;
            adapter=adapterSortByTime;
        }

        else if (id == R.id.nav_showCompleded) {
            refreshCompleted();

            menuChecked=2;
            eventList=eventListCompleted;
            adapter=adapterCompleted;
        }

        else if (id == R.id.nav_showUncompleted) {
            refreshUncompleted();

            menuChecked=3;
            eventList=eventListUncompleted;
            adapter=adapterUncompleted;
        }

        else if (id == R.id.nav_download) {

        }

        else if (id == R.id.nav_upload) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void showAddDialog(){
        AddEventDialog addEventDialog=new AddEventDialog(MainActivity.this,eventList,adapter,eventListDefault);
        addEventDialog.setCanceledOnTouchOutside(false);
        addEventDialog.show();
        /*btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar;// 用来装日期的
                DatePickerDialog dialog;
                calendar = Calendar.getInstance();
                dialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                System.out.println("年-->" + year + "月-->"
                                        + monthOfYear + "日-->" + dayOfMonth);

                            }
                        }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });*/
        //dialog.show();
    }
    private void initEventListDefaultDB(){
        /**
         * 初始化RecyclerView所需要的数据
         */
        sqLiteDatabase =dbHelper.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.query("List",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                int id=cursor.getInt(cursor.getColumnIndex("id"));
                String time=cursor.getString(cursor.getColumnIndex("time"));
                String data=cursor.getString(cursor.getColumnIndex("data"));
                int status=cursor.getInt(cursor.getColumnIndex("isCompleted"));
                int isNotification=cursor.getInt(cursor.getColumnIndex("isNotification"));
                Log.d("DB",time+" "+data);
                Event e=new Event(id,time,data,status,isNotification);
                eventListDefault.add(e);
            }while (cursor.moveToNext());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbHelper.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refresh();
    }

    private void refresh(){
        eventListDefault.clear();
        initEventListDefaultDB();
        switch (menuChecked){
            case 0:
                refreshDefault();
                break;
            case 1:
                refreshSortByTime();
                break;
            case 2:
                refreshCompleted();
                break;
            case 3:
                refreshUncompleted();
                break;
        }
    }

    private void refreshDefault(){
        adapterDefault.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void refreshSortByTime(){
        if(adapterSortByTime==null){
            adapterSortByTime=new ListAdapter(eventListSortByTime);
        }
        eventListSortByTime.clear();
        eventListSortByTime.addAll(eventListDefault);
        sortByTime(eventListSortByTime);
        recyclerView.setAdapter(adapterSortByTime);
        adapterSortByTime.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void refreshCompleted(){
        eventListCompleted.clear();
        eventListUncompleted.clear();
        selectCompleted(eventListDefault);
        if(adapterCompleted==null){
            adapterCompleted=new ListAdapter(eventListCompleted);
        }
        recyclerView.setAdapter(adapterCompleted);
        adapterCompleted.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void refreshUncompleted(){
        eventListCompleted.clear();
        eventListUncompleted.clear();
        selectCompleted(eventListDefault);
        if(adapterUncompleted==null){
            adapterUncompleted=new ListAdapter(eventListUncompleted);
        }
        adapterUncompleted.notifyDataSetChanged();
        recyclerView.setAdapter(adapterUncompleted);
        recyclerView.scheduleLayoutAnimation();
    }

    private void sortByTime(List<Event> eventListSortByTime){
        /**
         * 将List用时间排序
         */
        class MyComparator implements Comparator<Event>{

            @Override
            public int compare(Event event1, Event event2) {
                Date date1=event1.getTime();
                Date date2=event2.getTime();
                return date1.compareTo(date2);
            }
        }
        Collections.sort(eventListSortByTime,new MyComparator());
    }
    private void selectCompleted(List<Event>eventList){
        for(int i=0;i<eventList.size();i++){
            if(eventList.get(i).getStatus()==0){
                eventListUncompleted.add(eventList.get(i));
            }else{
                eventListCompleted.add(eventList.get(i));
            }
        }
    }
}
