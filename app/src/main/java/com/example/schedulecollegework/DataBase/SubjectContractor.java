package com.example.schedulecollegework.DataBase;

import android.net.Uri;
import android.provider.BaseColumns;

import javax.security.auth.Subject;

public class SubjectContractor {

    //Every thing related to primary activity starts with name Subject
    //Subject represents activity name
    public static final String subjectTableName="subjects";

    public static final String authority="com.example.schedulecollegework";
    public static final Uri uriWithAuthority=Uri.parse("content://"+authority);
    public static final Uri uriWithPath=Uri.withAppendedPath(uriWithAuthority,
            subjectTableName);


    public class SubjectEntry implements BaseColumns{

        public static final String subjectId="_id";
        public static final String subjectName="subject_name";
        public static final String subjectSemester="subject_semester";
        public static final String subjectNumberOfTasks="subject_tasks";

    }
}
