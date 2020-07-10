package com.example.schedulecollegework;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.schedulecollegework.DataBase.SubjectContractor;
import com.example.schedulecollegework.DataBase.TaskContractor;
import com.example.schedulecollegework.Utilties.SubjectCursorAdapter;
import com.example.schedulecollegework.Utilties.TaskCursorAdapter;
import com.example.schedulecollegework.Utilties.Utils;
import com.example.schedulecollegework.appmenu.AboutUs;
import com.example.schedulecollegework.appmenu.Help;

import java.net.URI;

public class TaskActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    TaskCursorAdapter taskCursorAdapter;
    private static final int URL_LOADER = 0;

    int taskTableNumber;

    Cursor cursor;

    ActionBar actionBar;
    private ProgressBar spinner;
    LinearLayout emptyViewTask;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);



        Utils.logPosition("onCreate","AddSubjectActivity");

        Log.e("Position", String.valueOf(7));

        actionBar= getSupportActionBar();





        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Tasks");



        Intent intent = getIntent();
        taskTableNumber = intent.getIntExtra("intVariableName", 0);




        ListView tasklistView=(ListView)findViewById(R.id.task_list_view);
        taskCursorAdapter=new TaskCursorAdapter(this,null,taskTableNumber);
        tasklistView.setAdapter(taskCursorAdapter);

        emptyViewTask=findViewById(R.id.empty_view_linear_layout);
        tasklistView.setEmptyView(emptyViewTask);

        emptyViewTask.setVisibility(View.GONE);

        //move to update activity of add task activity
        tasklistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e("Table number", String.valueOf(taskTableNumber));
                position=cursor.getInt(cursor.getColumnIndex(TaskContractor.TaskEntry.taskId));
                final Uri uri=ContentUris.withAppendedId(Uri.withAppendedPath(TaskContractor.uriWithAuthority,TaskContractor.taskPrimaryTableName+taskTableNumber),
                        position);

                Intent intent=new Intent(TaskActivity.this, AddTaskActivity.class);
                intent.setData(uri);
                startActivities(new Intent[]{intent});

            }
        });


        getLoaderManager().initLoader(URL_LOADER,null,this);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

    }



    //Util : Menu implementation
    //copy note : subject menu is used as task menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Utils.logPosition("onCreateOptionsMenu","AddSubjectActivity");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_subject_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Utils.logPosition("onOptionsItemSelected","AddSubjectActivity");

        switch (item.getItemId()) {
            case R.id.add_subject: {


                Intent intent=new Intent(this,AddTaskActivity.class);
                intent.putExtra("intVariableName", taskTableNumber);
                startActivity(intent);
                finish();
              break;

            }
            case android.R.id.home:{
                finish();
                break;
            }
            case R.id.help:{
                Intent intent=new Intent(this, Help.class);
                startActivity(intent);
                break;
            }
            case R.id.about_us:{
                Intent intent=new Intent(this, AboutUs.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //Cursor Loader implementation
    //UI of subject is updated here using cursor loader


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.e("Position", String.valueOf(3));

        Utils.logPosition("onCreateLoader","AddSubjectActivity");

        //String []projection={TaskContractor.TaskEntry.taskName};

        //uri for respective task table
        Uri uri=Uri.withAppendedPath(TaskContractor.uriWithAuthority, TaskContractor.taskPrimaryTableName+String.valueOf(taskTableNumber));


        //Note : query method is not used here
        return new CursorLoader(this, uri,null,null,null,null);
    }



    @Override
    public void onLoadFinished( Loader<Cursor> loader, Cursor data) {

        Log.e("Position", String.valueOf(4));

        if(data==null){
            emptyViewTask.setVisibility(View.VISIBLE);
        }

        try {
            Utils.logPosition("onLoadFinished","AddSubjectActivity");
        } catch (Exception e) {
            e.printStackTrace();
        }

        cursor=data;

        taskCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset( Loader<Cursor> loader) {

        Utils.logPosition("onLoaderReset","AddSubjectActivity");

    }






}


