package com.example.schedulecollegework.Utilties;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulecollegework.AddSubjectActivity;
import com.example.schedulecollegework.AddTaskActivity;
import com.example.schedulecollegework.DataBase.SCWHelperDataBase;
import com.example.schedulecollegework.DataBase.SubjectContractor;
import com.example.schedulecollegework.DataBase.TaskContractor;
import com.example.schedulecollegework.R;

import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static String getTableNameWithoutId(Uri uri) {

        Utils.logPosition("getTableNameWithoutId","Utils");

        String table = uri.getPath().substring(1);


        return table;
    }

    public static String getTableNameWithId(Uri uri) {

        Utils.logPosition("getTableNameWithId","Utils");

        String table = uri.getPath().substring(1);


        long record_id = ContentUris.parseId(uri);

        int record_id_length = Long.toString(record_id).length();
        record_id_length++;

        table = table.substring(0, table.length() - record_id_length);



        return table;
    }

    //this function deleted respective uri and its table when button to right is pressed other wise it does nothing
    public static void alertDialogToDelete(final Context context, String dialogMessage, String dialogTitle, final String leftButtonText, final String rightButtonText, final Uri uri) {

        Utils.logPosition("alertDialogToDelete","Utils");

        //isRightButtonPressed state is true when right button is pressed for left button it is false


        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(dialogMessage);
        dialog.setTitle(dialogTitle);

        //Right button
        dialog.setPositiveButton(rightButtonText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //Toast.makeText(context,rightButtonText,Toast.LENGTH_LONG).show();
                        int row = context.getContentResolver().delete(uri, null, null);

                        //after deleting a record also delete its table
                        deleteRespectiveTaskTable(uri,context);

                        // Showing deleting toast
                        Utils.showToast(context,"Deleted");


                    }
                });
        //Left button
        dialog.setNegativeButton(leftButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Utils.showToast(context,leftButtonText + "ed");

                //do nothing for delete

            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }

    //used for task activity
    //this function deleted respective uri and its table when button to right is pressed other wise it does nothing
    public static void alertDialogToDeleteForTaskActivity(final Context context, String dialogMessage, String dialogTitle, final String leftButtonText, final String rightButtonText,
                                                          final Uri uri, final int subjectId, final Boolean submissionStatus) {

        Utils.logPosition("alertDialogToDeleteForTaskActivity","Utils");

        //isRightButtonPressed state is true when right button is pressed for left button it is false


        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(dialogMessage);
        dialog.setTitle(dialogTitle);

        //Right button
        dialog.setPositiveButton(rightButtonText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //Toast.makeText(context,rightButtonText,Toast.LENGTH_LONG).show();
                        int row = context.getContentResolver().delete(uri, null, null);
                        Utils.subjectNumberOfTasksImplementation(2, submissionStatus, subjectId, context);


                        // Showing deleting toast
                        Utils.showToast(context,"Deleted");


                    }
                });
        //Left button
        dialog.setNegativeButton(leftButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Utils.showToast(context,leftButtonText + "ed");
                //do nothing for delete

            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }

    //check weather a given table exists in our data base
    public static boolean checkForTableExists(SQLiteDatabase db, String table){

        Utils.logPosition("checkForTableExists","Utils");

        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.getCount() > 0) {
            return true;
        }
        mCursor.close();
        return false;
    }

    //This function takes uri of newly inserted row and makes a respective tasks table for it
    public static void makeNewTaskTable(Uri uri,Context context){

        Utils.logPosition("makeNewTaskTable","Utils");

        //get the _id of row inserted in the respective table
        long getInsertUriId= ContentUris.parseId(uri);


        String newTaskTable= TaskContractor.taskPrimaryTableName+getInsertUriId;


        SCWHelperDataBase scwHelperDataBase=new SCWHelperDataBase(context);
        SQLiteDatabase sqLiteDatabase=scwHelperDataBase.getWritableDatabase();

        if(!checkForTableExists(sqLiteDatabase,newTaskTable)){


            final String taskTableCreater=
                    "CREATE TABLE "+newTaskTable
                            +"("+TaskContractor.TaskEntry.taskId+" INTEGER PRIMARY KEY " +
                            "AUTOINCREMENT "+","+TaskContractor.TaskEntry.taskName+
                            " TEXT"+","+TaskContractor.TaskEntry.taskSubmissionStatus+" INTEGER"
                            +","+TaskContractor.TaskEntry.taskSubmissionDate+" TEXT"+
                            ","+TaskContractor.TaskEntry.taskSubmissionTime+" TEXT"+","+
                            TaskContractor.TaskEntry.taskDescription+" TEXT"+")";

            Log.e("Task Table ",taskTableCreater);

            sqLiteDatabase.execSQL(taskTableCreater);
        }
        else{

        }

    }


    //this function deletes the respective table associated with deleted record
    public static void deleteRespectiveTaskTable(Uri uri,Context context){

        Utils.logPosition("deleteRespectiveTaskTable","Utils");

        //get the _id of row inserted in the respective table
        long getDeleteUriId= ContentUris.parseId(uri);


        String taskTableToDelete= TaskContractor.taskPrimaryTableName+getDeleteUriId;


        SCWHelperDataBase scwHelperDataBase=new SCWHelperDataBase(context);
        SQLiteDatabase sqLiteDatabase=scwHelperDataBase.getWritableDatabase();

        if(checkForTableExists(sqLiteDatabase,taskTableToDelete)){


            final String taskTableDeleter=
                    "drop table if exists "+taskTableToDelete;

            sqLiteDatabase.execSQL(taskTableDeleter);
        }
        else{

        }
    }

    //check frequency of a character in a string
    public static int freq(String line,char character) {

        Utils.logPosition("freq","Utils");

        int frequency = 0;

        for(int i = 0; i < line.length(); i++) {
            if(character == line.charAt(i)) {
                ++frequency;
            }
        }

       return frequency;
    }

    // this function returns true if id exists in a given uri else returns false based on frequency of '/' in its path
    // this function is little bit jugar so must be replaced in future with an efficent way
    public static Boolean checkForIdInYourUri(Uri uri){

        Utils.logPosition("checkForIdInYourUri","Utils");

        String taskPath=uri.getPath();
        int frequency=Utils.freq(taskPath,'/');



        if(frequency==1){
            return false;
        }
        else {
            return true;
        }
    }

    public static void logPosition(String Position,String Class){

       // Utils.logPosition("logPosition","Utils");

        Log.e("Position",Position+" : Class "+Class);
    }


    //Returns selection on the base of table name
    public static String selectionOnBaseOfTableName(String table){
        Utils.logPosition("selectionOnBaseOfTable","Utils");

        if(table.equals(SubjectContractor.subjectTableName)) {
            return SubjectContractor.SubjectEntry.subjectId + "=?";
        }
        else{
            return TaskContractor.TaskEntry.taskId + "=?";
        }
    }

    //Add a custom intent animation for frequent use
    public static void customIntentAnimationFades(Context context){
        ((Activity) context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        //overridePendingTransition(R.anim.abc_slide_out_bottom,R.anim.abc_slide_in_bottom);
        //overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.abc_slide_out_bottom,R.anim.abc_slide_in_bottom);
    }

    //This fn takes context and string msg and displays a toast
    public static void showToast(Context context,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    //when called returns current date and time
    public static String getCurrentDateAndTime(){

        Date timeNow= Calendar.getInstance().getTime();
        return timeNow.toString();
    }

    public static int convertSubmissionStatusBooleanToInt(Boolean submissionStatus){
        int submissionStatusInt=0;

        if(submissionStatus){
            submissionStatusInt=1;
        }
        else{
            submissionStatusInt=0;
        }
        return submissionStatusInt;
    }

    public static Boolean convertSubmissionStatusIntToBoolean(int submissionStatusInt){
        Boolean submissionStatus;
        if(submissionStatusInt==1){
            submissionStatus=true;
        }
        else{
            submissionStatus=false;
        }
        return submissionStatus;
    }

    //Takes submission status boolean and set value on respective view
    public static void setSubmissionStatus(Boolean submissionStatus, TextView textView){
        if(submissionStatus){
            textView.setText("Submission done");
            //Green Color
            textView.setTextColor(Color.parseColor("#4CAF50"));
        }
        else{
            textView.setText("Not yet submitted");
            //Red Color
            textView.setTextColor(Color.parseColor("#F44336"));
        }
    }

    public static void subjectNumberOfTasksImplementation(int where, Boolean submissionState,int subjectId,Context context){

        //where
        //0= Add
        //1=update
        //2=delete

        //inc_dec true for increment and false for decrement
        //submissionStatus true for Submission done and false for Not yet submitted

        Uri uri=ContentUris.withAppendedId(SubjectContractor.uriWithPath,subjectId);
        Log.e("Uri pos : Utils", String.valueOf(uri));
        Log.e("ss where", String.valueOf(where));

        //get current value
        Cursor cursor=context.getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();
        int currentNumberOfTasks=cursor.getInt(cursor.getColumnIndex(SubjectContractor.SubjectEntry.subjectNumberOfTasks));



        //process current value

        if(where==0){ //add
            if(submissionState){
                //do nothing
            }
            else{
                currentNumberOfTasks++;
            }
        }
        else if(where==1) {//update
            if(submissionState){
                currentNumberOfTasks--;
            }
            else{
                currentNumberOfTasks++;
            }
        }
        else if(where==2){//delete
            if(submissionState){
                //do nothing
            }
            else{
                currentNumberOfTasks--;
            }
        }

        //update current value
        ContentValues contentValues=new ContentValues();
        contentValues.put(SubjectContractor.SubjectEntry.subjectNumberOfTasks,currentNumberOfTasks);

        context.getContentResolver().update(uri,contentValues,null,null);


    }

    public static int getTaskTableNumber(Uri uri){
        String stingTableNumber=Utils.getTableNameWithId(uri).replace(TaskContractor.taskPrimaryTableName, "");
        int tableNumber = Integer.parseInt(stingTableNumber);
        return tableNumber;

    }





}








