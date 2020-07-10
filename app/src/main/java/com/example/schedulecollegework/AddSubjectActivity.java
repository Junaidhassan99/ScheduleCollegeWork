package com.example.schedulecollegework;



import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.schedulecollegework.DataBase.SCWHelperDataBase;
import com.example.schedulecollegework.DataBase.SubjectContractor;
import com.example.schedulecollegework.DataBase.SubjectProvider;
import com.example.schedulecollegework.DataBase.TaskContractor;
import com.example.schedulecollegework.Utilties.Utils;

public class AddSubjectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    //get Data from the fields
    String msubjectName;
    String msubjectSemester;

    //uri is obtained when update button is clicked
    Uri uriUpdate=null;

    //Views to get data
    EditText editTextSubjectName;
    Spinner spinner;

    //True when we are in update screen and false when we are in add screen
    Boolean isUpdateScreen=false;

    ActionBar actionBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        Utils.logPosition("onCreate","AddSubjectActivity");

         actionBar= getSupportActionBar();


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setSpinner();

        uriUpdate=getIntent().getData();


        if(uriUpdate!=null){
            isUpdateScreen=true;
            setTitle("Edit Subject");
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }


    //Util : Menu implementation

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Utils.logPosition("onCreateOptionsMenu","AddSubjectActivity");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_add_subject_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Utils.logPosition("onOptionsItemSelected","AddSubjectActivity");

        switch (item.getItemId()) {
            case R.id.save_subject: {
                if(isUpdateScreen) {
                    //make isUpdateScreen false to reset its value
                    isUpdateScreen=false;
                    //update subject
                    updateSubject();
                }
                else{
                    //insert new subject
                    insertSubject();
                }
                break;
            }
            case android.R.id.home:{
                alertDialogToBack();
                break;
            }
            default:{
                //do nothing
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void insertSubject(){

        Utils.logPosition("insertSubject","AddSubjectActivity");

        editTextSubjectName=findViewById(R.id.subject_name);
        msubjectName=editTextSubjectName.getText().toString();
        //Semester value is initialized from spinner



        //set the given subject to untitled when there is nothing entered
        if(msubjectName.equals("")){
            msubjectName="Untitled";
        }


        ContentValues contentValues=new ContentValues();
        contentValues.put(SubjectContractor.SubjectEntry.subjectName,msubjectName);
        contentValues.put(SubjectContractor.SubjectEntry.subjectSemester,msubjectSemester);

        Uri uri=getContentResolver().insert(SubjectContractor.uriWithPath,contentValues);

        //make tasks tables for inserted record or row
        Utils.makeNewTaskTable(uri,this);

        // Showing inserting toast
        Utils.showToast(this,"Added");


        //go to calling activity in the position where it started also updating ui there
        Log.e("Position", String.valueOf(9));
        finish();
        Utils.customIntentAnimationFades(this);

    }

    public void updateSubject(){

        Utils.logPosition("updateSubject","AddSubjectActivity");

        msubjectName=editTextSubjectName.getText().toString();
        //msubjectSemester gets updated using cursor loader

        //set the given subject to untitled when there is nothing entered
        if(msubjectName.equals("")){
            msubjectName="Untitled";
        }


        ContentValues contentValues=new ContentValues();
        contentValues.put(SubjectContractor.SubjectEntry.subjectName,msubjectName);
        contentValues.put(SubjectContractor.SubjectEntry.subjectSemester,msubjectSemester);



        //in case of error : int row was not here previously
        int row=getContentResolver().update(uriUpdate,contentValues,null,null);

        // Showing updating toast
        Utils.showToast(this,"Updated");


        //go to calling activity in the position where it started also updating ui there
        finish();
        Utils.customIntentAnimationFades(this);
    }



    //Util : Spinner implementation
    ArrayAdapter<CharSequence> adapter;

    public void setSpinner(){

        Utils.logPosition("setSpinner","AddSubjectActivity");

        spinner  = (Spinner) findViewById(R.id.semester_no);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Utils.logPosition("onItemSelected","AddSubjectActivity");

        String item = parent.getItemAtPosition(position).toString();

        msubjectSemester=item;

    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        Utils.logPosition("onNothingSelected","AddSubjectActivity");

    }

    //Cursor Loader implementation
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        Utils.logPosition("onCreateLoader","AddSubjectActivity");

        //Cursor cursor=getContentResolver().query(uriUpdate,null,null,null,null);
        return new CursorLoader(this,uriUpdate,null,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        Utils.logPosition("onLoadFinished","AddSubjectActivity");

        data.moveToFirst();


        //get and set value on edit view
        msubjectName=data.getString(data.getColumnIndex(SubjectContractor.SubjectEntry.subjectName));
        editTextSubjectName=findViewById(R.id.subject_name);
        editTextSubjectName.setText(msubjectName);


        //get and set value on spinner view
        msubjectSemester=data.getString(data.getColumnIndex(SubjectContractor.SubjectEntry.subjectSemester));
        spinner  = (Spinner) findViewById(R.id.semester_no);
        int selectionPosition= adapter.getPosition(msubjectSemester);
        spinner.setSelection(selectionPosition);





    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        Utils.logPosition("onLoaderReset","AddSubjectActivity");

    }


    //Do something when back button is pressed
    @Override
    public void onBackPressed() {

        Utils.logPosition("onBackPressed","AddSubjectActivity");

        alertDialogToBack();
    }
    public void alertDialogToBack(){

        Utils.logPosition("alertDialogToBack","AddSubjectActivity");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finish();
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }


}

