package com.example.schedulecollegework.DataBase;

import android.net.Uri;
import android.provider.BaseColumns;

public class TaskContractor {
    //Every thing related to primary activity starts with name Subject
    //Subject represents activity name
    public static final String taskPrimaryTableName="tasks";

    public static final String authority="com.example.schedulecollegework";
    public static final Uri uriWithAuthority=Uri.parse("content://"+authority);



    public class TaskEntry implements BaseColumns {

        public static final String taskId="_id";
        public static final String taskName="task_name"; //task topic
        public static final String taskSubmissionStatus="task_submission_status";
        public static final String taskSubmissionDate="task_submission_date";
        public static final String taskSubmissionTime="task_submission_time";
        public static final String taskDescription="task_description";



    }
}
