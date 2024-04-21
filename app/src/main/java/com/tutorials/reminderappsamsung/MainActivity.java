package com.tutorials.reminderappsamsung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.tutorials.reminderappsamsung.adapter.Adapter;
import com.tutorials.reminderappsamsung.data.database.ReminderDAO;
import com.tutorials.reminderappsamsung.data.database.ReminderDatabase;
import com.tutorials.reminderappsamsung.data.model.Reminder;
import com.tutorials.reminderappsamsung.ui.AddReminder;
import com.tutorials.reminderappsamsung.ui.UpdateReminderItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fab;
    List<Reminder> listReminder;

    RecyclerView recyclerView;

    ReminderDAO reminderDAO;

    Adapter adapter;

    DrawerLayout drawerLayout;

    SearchView searchView;

    ImageView emptyReminder;

//    List<Reminder> noCompletedListReminder = new ArrayList<>();;
//    List<Reminder> todayListReminder = new ArrayList<>();;
//    List<Reminder> CompletedListReminder = new ArrayList<>();;
//    List<Reminder> importantListReminder = new ArrayList<>();;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        navClickedDrawer();

        // Database:
        ReminderDatabase db = ReminderDatabase.getInstance(this);

        //DAO
        reminderDAO = db.getReminderDAO();

        // Nhận Intent đã gửi đến
        Intent intentGet = getIntent();

        // Nhận biến boolean checkAddReminder từ Intent
        boolean checkAddReminder = intentGet.getBooleanExtra("CheckReminderAdd", false);

        if(checkAddReminder == true){
            String DateReminderAdd = intentGet.getStringExtra("DateReminderAdd");
            String TimeReminderAdd = intentGet.getStringExtra("TimeReminderAdd");
            String TitleReminderAdd = intentGet.getStringExtra("TitleReminderAdd");
            String NoteReminderAdd = intentGet.getStringExtra("NoteReminderAdd");
            String LocationReminderAdd = intentGet.getStringExtra("LocationReminderAdd");

            boolean ImportantReminderAdd = intentGet.getBooleanExtra("ImportantReminderAdd", false);
            int StateReminderAdd = intentGet.getIntExtra("StateReminderAdd", 0);
            Reminder rm = new Reminder(
                    DateReminderAdd, TimeReminderAdd,
                    TitleReminderAdd,NoteReminderAdd, ImportantReminderAdd,
                    LocationReminderAdd, StateReminderAdd
            );
            reminderDAO.insert(rm);
        }



//        for(Reminder r: listReminder){
//            Log.v("TAGY", r.getId() + " " + r.getTitle() + " " + r.getDescription());
//        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddReminder.class);
                startActivity(intent);
            }
        });


        setupAdapter(this);
    }

    private void initUI() {
        fab = findViewById(R.id.fab);
        emptyReminder = findViewById(R.id.emptyReminderImage);
        emptyReminder.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recyclerview);
    }

    private void setupAdapter(Context context) {
        //Get data roomdb (initdata)
        listReminder = reminderDAO.getAllReminder();

        if(listReminder.size() < 1){
            emptyReminder.setVisibility(View.VISIBLE);
        }
//        for(Reminder rm: listReminder){
//            if(rm.getState() == 0){
//                noCompletedListReminder.add(rm);
//            }
//        }
        adapter = new Adapter(listReminder, context, new Adapter.ClickUpdateItemReminder(){
            @Override
            public void updateItemReminder(Reminder reminder){
                clickUpdateReminderItem(reminder);
            }

            @Override
            public void deleteItemReminder(Reminder reminder) {
                clickDeleteReminderItem(reminder);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    private void clickDeleteReminderItem(Reminder reminder) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm delete reminder")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete User
                        reminderDAO.deleteReminderItem(reminder);
                        Toast.makeText(MainActivity.this, "Delete reminder successfully", Toast.LENGTH_SHORT).show();
                        listReminder = reminderDAO.getAllReminder();
                        adapter.setData(listReminder);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clickUpdateReminderItem(Reminder reminder) {
        Intent itentUpdateReminderItem = new Intent(MainActivity.this, UpdateReminderItem.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_reminder_item", reminder);
        itentUpdateReminderItem.putExtras(bundle);
        startActivity(itentUpdateReminderItem);
    }

    //reminder with title ton tai chua

    private boolean isReminderExist(String title){
        List<Reminder> listCheck = reminderDAO.checkReminder(title);
        return listCheck != null && listCheck.isEmpty();
    }
    private void navClickedDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the listener
        navigationView.setNavigationItemSelectedListener(this);

        // Hide the title
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set transparent background
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.nav_all) {
            showToast("All Clicked");
        } else if (id == R.id.nav_today) {
            showToast("Today Clicked");
        } else if (id == R.id.nav_scheduled) {
            showToast("Scheduled Clicked");
        } else if (id == R.id.nav_important) {
            showToast("Important Clicked");
        } else if (id == R.id.nav_completed) {
            showToast("Completed Clicked");
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search...");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}

//        Reminder reminder = new Reminder("Mon, Apr 18 2024", "10:20", "Hoc Toan", "Lam bai so 2", false, "Ha Noi", 0);
//        Reminder reminder1 = new Reminder("Tue, Apr 19 2024", "20:10","Hoc Van", "Lam bai so 3", false, "Ha Noi", 0);
//        Reminder reminder2 = new Reminder("Wed, Apr 20 2024", "05:20", "Hoc T.A", "Lam bai so 4", false, "Ha Noi", 0);
//
//        reminderDAO.insert(reminder);
//        reminderDAO.insert(reminder1);
//        reminderDAO.insert(reminder2);