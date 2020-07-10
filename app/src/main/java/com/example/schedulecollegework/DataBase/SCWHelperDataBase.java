package com.example.schedulecollegework.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.schedulecollegework.Utilties.Utils;

public class SCWHelperDataBase extends SQLiteOpenHelper {

    private static final String dataBaseName="scw.db";
    private static final int dataBaseVersion=1;

    public SCWHelperDataBase(@Nullable Context context) {
        super(context, dataBaseName, null, dataBaseVersion);
        Utils.logPosition("SCWHelperDataBase Constructor","SCWHelperDataBase");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        Utils.logPosition("onCreate","SCWHelperDataBase");

        final String subjectTableCreater=
                "CREATE TABLE "+SubjectContractor.subjectTableName
                +"("+SubjectContractor.SubjectEntry.subjectId+" INTEGER PRIMARY KEY " +
                        "AUTOINCREMENT "+","+SubjectContractor.SubjectEntry.subjectName+
                        " TEXT "+","+SubjectContractor.SubjectEntry.subjectSemester+
                        " TEXT "+","+SubjectContractor.SubjectEntry.subjectNumberOfTasks+" INTEGER DEFAULT 0"+")";

        Log.e("subjectTableCreater",subjectTableCreater);

        db.execSQL(subjectTableCreater);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Utils.logPosition("onUpgrade","SCWHelperDataBase");


    }
}
