package com.example.schedulecollegework;



import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schedulecollegework.DataBase.SCWHelperDataBase;
import com.example.schedulecollegework.DataBase.SubjectContractor;
import com.example.schedulecollegework.Utilties.SubjectCursorAdapter;
import com.example.schedulecollegework.Utilties.Utils;
import com.example.schedulecollegework.appmenu.AboutUs;
import com.example.schedulecollegework.appmenu.Help;

public class SubjectActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SubjectCursorAdapter subjectCursorAdapter;
    private static final int URL_LOADER = 0;

    Cursor cursorIntentId;

    ActionBar actionBar;
    private ProgressBar spinner;
    LinearLayout emptyView;




    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        actionBar= getSupportActionBar();


        Log.e("Position", String.valueOf(8));

        Utils.logPosition("onCreate","AddSubjectActivity");

        setTitle("Subjects");




        //Cursor c=getContentResolver().query(SubjectContractor.uriWithPath,null,null,null,null);

        ListView subjectlistView=(ListView)findViewById(R.id.subject_list_view);
        subjectCursorAdapter=new SubjectCursorAdapter(this,null);
        subjectlistView.setAdapter(subjectCursorAdapter);

        emptyView=findViewById(R.id.empty_view_linear_layout);
        subjectlistView.setEmptyView(emptyView);

        emptyView.setVisibility(View.GONE);


        //in case of error : move on item click listener below getLoaderManager
        //This block of code will call intent to task activity hence acting as a bridge between two main parts of app
        subjectlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {


                //intent id is the _id attribute of subject table with respective position if its item in the list
                cursorIntentId.moveToPosition(position);
                int intentId=cursorIntentId.getInt(cursorIntentId.getColumnIndex(SubjectContractor.SubjectEntry.subjectId));

                //below 3 lines are to check the respective id

                String checkStringForLog=cursorIntentId.getString(cursorIntentId.getColumnIndex(SubjectContractor.SubjectEntry.subjectName));


                Intent intent=new Intent(getApplicationContext(),TaskActivity.class);
                intent.putExtra("intVariableName", intentId);
                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(URL_LOADER,null,this);
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

    }



    //Util : Menu implementation

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

                Intent intent=new Intent(this,AddSubjectActivity.class);
                startActivity(intent);

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

        Utils.logPosition("onCreateLoader","AddSubjectActivity");

        //String []projection={SubjectContractor.SubjectEntry.subjectName,
          //      SubjectContractor.SubjectEntry.subjectSemester};

        //Note : query method is not used here
        return new CursorLoader(this, SubjectContractor.uriWithPath,null,null,null,null);
    }



    @Override
    public void onLoadFinished( Loader<Cursor> loader, Cursor data) {

        Utils.logPosition("onLoadFinished","AddSubjectActivity");

        if(data==null){
            emptyView.setVisibility(View.VISIBLE);
        }

        cursorIntentId=data;
        subjectCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset( Loader<Cursor> loader) {

        Utils.logPosition("onLoaderReset","AddSubjectActivity");

    }


}

