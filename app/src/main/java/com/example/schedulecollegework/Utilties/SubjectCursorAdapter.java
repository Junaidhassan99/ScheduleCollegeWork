package com.example.schedulecollegework.Utilties;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulecollegework.AddSubjectActivity;
import com.example.schedulecollegework.DataBase.SubjectContractor;
import com.example.schedulecollegework.R;

public class SubjectCursorAdapter extends CursorAdapter {
    Context context=null;

    public SubjectCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        Utils.logPosition("Constructor","SubjectCursorAdapter");
        this.context=context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Utils.logPosition("newView","SubjectCursorAdapter");

        View view=LayoutInflater.from(context).inflate(R.layout.subject_items,parent,false);
        //in case of error : return value was changed
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        Utils.logPosition("bindView","SubjectCursorAdapter");

       // cursor.moveToFirst();
        final String Subject=cursor.getString(cursor.getColumnIndex(SubjectContractor.SubjectEntry.subjectName));
        String Semester=cursor.getString(cursor.getColumnIndex(SubjectContractor.SubjectEntry.subjectSemester));

        TextView subject_view=view.findViewById(R.id.subject_text_view);
        TextView semester_view=view.findViewById(R.id.semester_text_view);

        TextView number_of_tasks_view=view.findViewById(R.id.number_of_tasks);
        String numberOfTasks=cursor.getString(cursor.getColumnIndex(SubjectContractor.SubjectEntry.subjectNumberOfTasks));

        if(Integer.parseInt(numberOfTasks)>9){
            numberOfTasks="9+";
        }

        number_of_tasks_view.setText(numberOfTasks);



        subject_view.setText(Subject);
        semester_view.setText("Semester : "+Semester);

        final int position=cursor.getInt(cursor.getColumnIndex(SubjectContractor.SubjectEntry.subjectId));
        final Uri uri= ContentUris.withAppendedId(SubjectContractor.uriWithPath,(long)position);
        //incase of error : uri was used inside

        ImageView updateButton=view.findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent=new Intent(context, AddSubjectActivity.class);
                intent.setData(uri);
                context.startActivities(new Intent[]{intent});


            }
        });

        ImageView deleteButton=view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //Delete dialog alert implementation
                String dialogMessage="Are you sure you want to delete a subject";
                String dialogTitle="Alert";
                String dialogLeftButton="Cancel";
                String dialogRightButton="Yes";
                Utils.alertDialogToDelete(context,dialogMessage,dialogTitle,dialogLeftButton,dialogRightButton,uri);


            }
        });


    }
}
