package com.akash.xkxd;

import android.content.Intent;
import android.database.SQLException;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ListViewActivity extends ActionBarActivity implements ComicsListRecyclerViewAdapter.OnItemClickListener {

    private static final String TAG = "ListViewActivity";

//    private List<String> mFilesList;
//    private ListView comicsListView;
//    private SwipeRefreshLayout swipeLayout;

    private DataBaseHelper mDbHelper;
    private ComicsListRecyclerViewAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comics_list);

        setTitle("XKCD Comics");

/*        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeLayout.setProgressBackgroundColor(R.color.progress_spinner);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(ListViewActivity.this, MainActivity.class);
                intent.putExtra(getPackageName() + ".NUMBER", 0);
                startActivity(intent);
            }
        });*/

/*        comicsListView = (ListView) findViewById(R.id.listview);
        comicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                int num = Integer.parseInt(mFilesList.get(position));
                Intent intent = new Intent(ListViewActivity.this, MainActivity.class);
                intent.putExtra(getPackageName() + ".NUMBER", num);
                startActivity(intent);
            }
        });*/

        if (mDbHelper != null)
            mDbHelper.close();

        mDbHelper = new DataBaseHelper(this);
        try {
            mDbHelper.createDataBase();

        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            mDbHelper.openDataBase();
        } catch(SQLException sqle){
            throw sqle;
        }


        ArrayList<XkcdData> mComics = mDbHelper.getAllComics();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_comics);
        if (myAdapter == null){
            final String format = Settings.System.getString(getContentResolver(), Settings.System.DATE_FORMAT);
            DateFormat dateFormat;
            if (TextUtils.isEmpty(format)) {
                dateFormat = android.text.format.DateFormat.getMediumDateFormat(this);
            } else {
                dateFormat = new SimpleDateFormat(format);
            }
            Log.d(TAG, "onResume: "+ mComics.size());
            myAdapter = new ComicsListRecyclerViewAdapter(mComics, dateFormat, this);
        }
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    protected void onResume() {
/*        if (mDbHelper != null)
            mDbHelper.close();

        mDbHelper = new DataBaseHelper(this);
        try {
            mDbHelper.createDataBase();

        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            mDbHelper.openDataBase();
        } catch(SQLException sqle){
            throw sqle;
        }


        mComics = mDbHelper.getAllComics();*/
//        Log.d(TAG, "onResume: "+mComics.size());
//        mFilesList = getComicNumbers(mComics);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, mFilesList);
//
//        comicsListView.setAdapter(adapter);
        myAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mDbHelper.close();

/*        if(swipeLayout.isRefreshing())
            swipeLayout.setRefreshing(false);*/
    }

    public static ArrayList<String> getComicNumbers(ArrayList<XkcdData> comics) {
        ArrayList<String> nums = new ArrayList<>();
        for(XkcdData c : comics) {
            nums.add(String.valueOf(c.getNum()));
//            Log.d(TAG, "getComicNumbers: "+c.getNum());
        }
        return nums;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                AboutApp.Show(ListViewActivity.this);
                return true;

            case R.id.action_whatif:
                Intent intent = new Intent(this, WhatIf.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(XkcdData comic) {
        Intent intent = new Intent(ListViewActivity.this, MainActivity.class);
        intent.putExtra(getPackageName() + ".NUMBER", comic.getNum());
        startActivity(intent);
    }


}
