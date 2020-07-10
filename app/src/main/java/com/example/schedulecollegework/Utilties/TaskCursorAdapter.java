package com.example.schedulecollegework.Utilties;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.schedulecollegework.AddSubjectActivity;
import com.example.schedulecollegework.AddTaskActivity;
import com.example.schedulecollegework.DataBase.SubjectContractor;
import com.example.schedulecollegework.DataBase.TaskContractor;
import com.example.schedulecollegework.R;

public class TaskCursorAdapter extends CursorAdapter {
    Context context=null;
    int taskTableNumber;

    public TaskCursorAdapter(Context context, Cursor c,int taskTableNumber) {
        super(context, c, 0);
        Utils.logPosition("Constructor","TaskCursorAdapter");
        this.context=context;
        this.taskTableNumber=taskTableNumber;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Utils.logPosition("newView","TaskCursorAdapter");

        View view= LayoutInflater.from(context).inflate(R.layout.task_items,parent,false);
        //in case of error : return value was changed
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        Utils.logPosition("bindView","TaskCursorAdapter");


        final String task=cursor.getString(cursor.getColumnIndex(TaskContractor.TaskEntry.taskName));
        TextView task_view=view.findViewById(R.id.task_text_view);
        task_view.setText(task);

        final int submissionStatus=cursor.getInt(cursor.getColumnIndex(TaskContractor.TaskEntry.taskSubmissionStatus));
        TextView submission_status_view=view.findViewById(R.id.task_submission_status_text_view);
        Utils.setSubmissionStatus(Utils.convertSubmissionStatusIntToBoolean(submissionStatus),submission_status_view);

        String submissionDate=cursor.getString(cursor.getColumnIndex(TaskContractor.TaskEntry.taskSubmissionDate));
        String submissionTime=cursor.getString(cursor.getColumnIndex(TaskContractor.TaskEntry.taskSubmissionTime));
        TextView submission_date_and_time_view=view.findViewById(R.id.task_due_date_text_view);

        if(submissionDate=="Select Date"){
            submissionDate="- ";
        }
        if(submissionTime=="Select Time"){
            submissionTime=" -";
        }

        submission_date_and_time_view.setText("Due Date: "+submissionDate+" at "+submissionTime);



        final int position=cursor.getInt(cursor.getColumnIndex(TaskContractor.TaskEntry.taskId));
       // final Uri uri= ContentUris.withAppendedId(TaskContractor.uriWithPath,(long)position);
        final Uri uri=ContentUris.withAppendedId(Uri.withAppendedPath(TaskContractor.uriWithAuthority,TaskContractor.taskPrimaryTableName+taskTableNumber),
        position);
        //incase of error : uri was used inside

        ImageView updateButton=view.findViewById(R.id.task_update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent intent=new Intent(context, AddTaskActivity.class);
                intent.setData(uri);
                context.startActivities(new Intent[]{intent});


            }
        });

        ImageView deleteButton=view.findViewById(R.id.task_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //Delete dialog alert implementation
                String dialogMessage="Are you sure you want to delete a subject";
                String dialogTitle="Alert";
                String dialogLeftButton="Cancel";
                String dialogRightButton="Yes";
                Utils.alertDialogToDeleteForTaskActivity(context,dialogMessage,dialogTitle,dialogLeftButton,dialogRightButton,uri,taskTableNumber,
                        Utils.convertSubmissionStatusIntToBoolean(submissionStatus));


            }
        });


    }



}

