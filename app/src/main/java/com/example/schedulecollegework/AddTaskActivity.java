package com.example.schedulecollegework;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.schedulecollegework.DataBase.SubjectContractor;
import com.example.schedulecollegework.DataBase.TaskContractor;
import com.example.schedulecollegework.Utilties.DatePickerFragment;
import com.example.schedulecollegework.Utilties.Utils;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor> , DatePickerDialog.OnDateSetListener{

    //get Data from the fields
    //m denotes edit text view
    String mtaskName;
    String mtaskDescription;
    Boolean submissionStatus=false;
    String submissionDate;
    String submissionTime;



    //uri is obtained when update button is clicked
    Uri uriUpdate=null;

    //Views to get data
    EditText editTextTaskName;
    EditText  editTextDescription;
    TextView submissionStatusView;
    TextView submissionDateView;
    TextView submissionTimeView;



    //True when we are in update screen and false when we are in add screen
    Boolean isUpdateScreen=false;

    int taskTableNumber;



    //Submission Status initial value fro edit
    boolean submissionStatusInitialValue;

    ActionBar actionBar;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Utils.logPosition("onCreate","AddSubjectActivity");

         actionBar= getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        taskTableNumber = intent.getIntExtra("intVariableName", 0);



        uriUpdate=getIntent().getData();


        if(uriUpdate!=null){
            isUpdateScreen=true;
            setTitle("Edit task");
            getSupportLoaderManager().initLoader(0,null,this);
        }
        else{
            setTitle("Add task");
        }

        //whenever submission_date text view is clicked we can select date and output is passed to onDateSet override function at the end
        final TextView submissionDate=findViewById(R.id.submission_date);
        submissionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                //Date output can be obtained from override function onDateSet
            }
        });

        //whenever submission_time text view is clicked we can select time and output is passed to onDateSet override function at the end
       submissionTimeView=findViewById(R.id.submission_time);
        submissionTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                TimePickerDialog picker = new TimePickerDialog(AddTaskActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {

                                //submissionTime has value of selected time


                                if(String.valueOf(sHour).length()==1&&String.valueOf(sMinute).length()==1){
                                    submissionTime="0"+sHour+":"+"0"+sMinute;
                                }
                                else if(String.valueOf(sHour).length()==2&&String.valueOf(sMinute).length()==1){
                                    submissionTime=sHour+":"+"0"+sMinute;
                                }
                                else if(String.valueOf(sHour).length()==1&&String.valueOf(sMinute).length()==2){
                                    submissionTime="0"+sHour+":"+sMinute;
                                }
                                else{
                                   submissionTime=sHour + ":" + sMinute;
                                }

                                submissionTimeView.setText(submissionTime);

                                //Time output can be obtained from here
                            }
                        }, hour, minutes, false);
                picker.show();

            }});

        //in case of error : make submissionStatusView final
        submissionStatusView=findViewById(R.id.submission_status);
        submissionStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //convert true to false and false to true
                submissionStatus=!submissionStatus;
                Utils.setSubmissionStatus(submissionStatus,submissionStatusView);


            }
        });
    }


    //Util : Menu implementation
    //subject menu will be used for add task

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
                    updateTask();
                    Log.e("position", String.valueOf(6));
                    //finish();

                }
                else{
                    //insert new subject
                    insertTask();

                    Log.e("Position ", String.valueOf(1));

                   // finish();

                    //Even after finish this jugarForErrorFix gets auto called
                    //Previous error was removed here
                    jugarForErrorFix();
                    //animation used in jugarForErrorFix function previouly

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


    public void insertTask(){

        Utils.logPosition("insertTask","AddSubjectActivity");

        editTextTaskName=findViewById(R.id.task_topic);
        mtaskName=editTextTaskName.getText().toString();

        editTextDescription=findViewById(R.id.task_description);
        mtaskDescription=editTextDescription.getText().toString();


        //set the given subject to untitled when there is nothing entered
        if(mtaskName.equals("")){
            mtaskName="Untitled";
        }

        if(submissionDate==null){
            submissionDate="Select Date";
        }
        if (submissionTime == null) {
            submissionTime="Select Time";
        }


        ContentValues contentValues=new ContentValues();
        contentValues.put(TaskContractor.TaskEntry.taskName,mtaskName);
        contentValues.put(TaskContractor.TaskEntry.taskSubmissionStatus,Utils.convertSubmissionStatusBooleanToInt(submissionStatus));
        contentValues.put(TaskContractor.TaskEntry.taskSubmissionDate,submissionDate);
        contentValues.put(TaskContractor.TaskEntry.taskSubmissionTime,submissionTime);
        contentValues.put(TaskContractor.TaskEntry.taskDescription,mtaskDescription);

        //uri for respective task table
        Uri uri=Uri.withAppendedPath(TaskContractor.uriWithAuthority, TaskContractor.taskPrimaryTableName+String.valueOf(taskTableNumber));


        //Note : also add the logic to see that wheater insertion was unsuccessful
        getContentResolver().insert(uri,contentValues);
        Log.e("ss", String.valueOf(taskTableNumber));

        Utils.subjectNumberOfTasksImplementation(0,submissionStatus,taskTableNumber,this);



        // Showing inserting toast
        Utils.showToast(this,"Added");


        //go to calling activity in the position where it started also updating ui there
        finish();


    }

    public void updateTask(){

        Utils.logPosition("updateTask","AddSubjectActivity");

        mtaskName=editTextTaskName.getText().toString();

        editTextDescription=findViewById(R.id.task_description);
        mtaskDescription=editTextDescription.getText().toString();


        //set the given subject to untitled when there is nothing entered
        if(mtaskName.equals("")){
            mtaskName="Untitled";
        }

        if(submissionDate==null){
            submissionDate="Select Date";
        }
        if (submissionTime == null) {
            submissionTime="Select Time";
        }


        ContentValues contentValues=new ContentValues();
        contentValues.put(TaskContractor.TaskEntry.taskName,mtaskName);
        contentValues.put(TaskContractor.TaskEntry.taskSubmissionStatus,Utils.convertSubmissionStatusBooleanToInt(submissionStatus));
        contentValues.put(TaskContractor.TaskEntry.taskSubmissionDate,submissionDate);
        contentValues.put(TaskContractor.TaskEntry.taskSubmissionTime,submissionTime);
        contentValues.put(TaskContractor.TaskEntry.taskDescription,mtaskDescription);



        //in case of error : int row was not here previously
        int row=getContentResolver().update(uriUpdate,contentValues,null,null);

        if(submissionStatusInitialValue!=submissionStatus) {
            Utils.subjectNumberOfTasksImplementation(1, submissionStatus, Utils.getTaskTableNumber(uriUpdate), this);
        }
        else{
            //do nothing
        }



        // Showing updating toast
        Utils.showToast(this,"Updated");


        //go to calling activity in the position where it started also updating ui there
        finish();
        //After change 3
        Utils.customIntentAnimationFades(this);
    }








    //Cursor Loader implementation
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        //Cursor cursor=getContentResolver().query(uriUpdate,null,null,null,null);
        return new CursorLoader(this,uriUpdate,null,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        data.moveToFirst();


        //get and set value on edit view
        mtaskName=data.getString(data.getColumnIndex(TaskContractor.TaskEntry.taskName));
        editTextTaskName=findViewById(R.id.task_topic);
        editTextTaskName.setText(mtaskName);


        //in case of error : see the submissionStatusView
        int submissionStatusInt=data.getInt(data.getColumnIndex(TaskContractor.TaskEntry.taskSubmissionStatus));
        submissionStatus=Utils.convertSubmissionStatusIntToBoolean(submissionStatusInt);
        Utils.setSubmissionStatus(submissionStatus,submissionStatusView);

        submissionStatusInitialValue=submissionStatus;

        submissionDate=data.getString(data.getColumnIndex(TaskContractor.TaskEntry.taskSubmissionDate));
        submissionDateView=findViewById(R.id.submission_date);
        submissionDateView.setText(submissionDate);

        submissionTime=data.getString(data.getColumnIndex(TaskContractor.TaskEntry.taskSubmissionTime));
        submissionTimeView=findViewById(R.id.submission_time);
        submissionTimeView.setText(submissionTime);

        mtaskDescription=data.getString(data.getColumnIndex(TaskContractor.TaskEntry.taskDescription));
        editTextDescription=findViewById(R.id.task_description);
        editTextDescription.setText(mtaskDescription);




    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


    //Do something when back button is pressed
    @Override
    public void onBackPressed() {
        alertDialogToBack();
    }
    public void alertDialogToBack(){
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

    //jugar function when the activity kept on going to subject activity but we wanted to go to task activity with updated screen so
    //we sent an intent to task activity with respective taskTableNumber
    public void jugarForErrorFix(){

        Utils.logPosition("jugarForErrorFix","AddTaskActivity");
        Intent intent=new Intent(this,TaskActivity.class);
        intent.putExtra("intVariableName", taskTableNumber);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        //After change 3
        Utils.customIntentAnimationFades(this);

    }



    //get result of submit date
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        //submissionDate has value of selected date
        submissionDate= DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        TextView textView = (TextView) findViewById(R.id.submission_date);
        textView.setText(submissionDate);
    }


}

