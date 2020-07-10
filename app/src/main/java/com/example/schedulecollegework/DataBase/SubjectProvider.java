package com.example.schedulecollegework.DataBase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.schedulecollegework.Utilties.Utils;

public class SubjectProvider extends ContentProvider {

    //in case of error : check utils class usage

    SCWHelperDataBase scwHelperDataBase;

    private static UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(SubjectContractor.authority,SubjectContractor.subjectTableName,001);
        uriMatcher.addURI(SubjectContractor.authority,SubjectContractor.subjectTableName+"/#",002);
    }

    @Override
    public boolean onCreate() {

        Utils.logPosition("onCreate","SubjectProvider");
        scwHelperDataBase=new SCWHelperDataBase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {


        Utils.logPosition("query","SubjectProvider");

        SQLiteDatabase sqLiteDatabase=scwHelperDataBase.getReadableDatabase();



        int match_id=uriMatcher.match(uri);
        Cursor c=null;

        if(match_id==001 || !Utils.checkForIdInYourUri(uri)){

            String table=Utils.getTableNameWithoutId(uri);

            c = sqLiteDatabase.query(table, null, null, null, null, null, null, null);
            //Notifying a change to all listeners
            c.setNotificationUri(getContext().getContentResolver(),uri);

        }
        else if(match_id==002 || Utils.checkForIdInYourUri(uri)){

            Log.e("kk","Subject Provider is working fine");

            long record_id= ContentUris.parseId(uri);
            String table=Utils.getTableNameWithId(uri);


            //query baseed on table name
            selection=Utils.selectionOnBaseOfTableName(table);
            selectionArgs= new String[]{"" + record_id};


            c = sqLiteDatabase.query(table, null, selection, selectionArgs, null, null, null, null);
            //Notifying a change to all listeners
            c.setNotificationUri(getContext().getContentResolver(),uri);

        }
        else{

        }


            return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        Utils.logPosition("getType","SubjectProvider");
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {


        Utils.logPosition("insert","SubjectProvider");



        //in case of error : scwHelperDataBase was here but moved to onCreate

         SQLiteDatabase sqLiteDatabase=scwHelperDataBase.getWritableDatabase();

         //id gets _id of inserted row in subject table
        long id=0;


        int match_id=uriMatcher.match(uri);

        if(match_id==001 || !Utils.checkForIdInYourUri(uri)){
            //gets table name of subjects
            String table=Utils.getTableNameWithoutId(uri);

            id=sqLiteDatabase.insert(table,null,values);
            getContext().getContentResolver().notifyChange(uri,null);
            //Notifying a change to all listeners
        }
        else{

        }



        //the uri returned will contain _id of subject table
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {


        Utils.logPosition("delete","SubjectProvider");


        SQLiteDatabase sqLiteDatabase=scwHelperDataBase.getWritableDatabase();
       // String table=uri.getPath().substring(1);

        //Log.e("value ",table);

        int match_id=uriMatcher.match(uri);
        //Cursor c=null;

        //incase of error : some thing was retured in latest version
        int deleted_row=0;

        if(match_id==001 || !Utils.checkForIdInYourUri(uri)){

            String table=Utils.getTableNameWithoutId(uri);
            deleted_row=sqLiteDatabase.delete(table,null,null);

            //Notifying a change to all listeners
            getContext().getContentResolver().notifyChange(uri,null);

        }
        else if(match_id==002 || Utils.checkForIdInYourUri(uri)){

            long record_id= ContentUris.parseId(uri);
            String table=Utils.getTableNameWithId(uri);


            //delete baseed on table name
            selection=Utils.selectionOnBaseOfTableName(table);
            selectionArgs= new String[]{"" + record_id};


            deleted_row=sqLiteDatabase.delete(table,selection,selectionArgs);
            //Notifying a change to all listeners
            getContext().getContentResolver().notifyChange(uri,null);

        }
        else{

        }


        return deleted_row;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {


        Utils.logPosition("update","SubjectProvider");


        SQLiteDatabase sqLiteDatabase=scwHelperDataBase.getWritableDatabase();
        // String table=uri.getPath().substring(1);

        //Log.e("value ",table);

        int match_id=uriMatcher.match(uri);
        //Cursor c=null;

        //incase of error : some thing was retured in latest version
        int update_row=0;

        Log.e("Update Uri", String.valueOf(uri));

        if(match_id==001 || !Utils.checkForIdInYourUri(uri)){

            Log.e("001","Path without Id");
            Log.e("001 Uri", String.valueOf(uri));

            String table=Utils.getTableNameWithoutId(uri);
            update_row=sqLiteDatabase.update(table,values,null,null);

            //Notifying a change to all listeners
            getContext().getContentResolver().notifyChange(uri,null);

        }
        else if(match_id==002 || Utils.checkForIdInYourUri(uri)){

            Log.e("002","Path with Id");
            Log.e("002 Uri", String.valueOf(uri));

            long record_id= ContentUris.parseId(uri);
            String table=Utils.getTableNameWithId(uri);

            Log.e("table",table);


            //update baseed on table name
            selection=Utils.selectionOnBaseOfTableName(table);
            selectionArgs= new String[]{"" + record_id};


            update_row=sqLiteDatabase.update(table,values,selection,selectionArgs);
            //Notifying a change to all listeners
            getContext().getContentResolver().notifyChange(uri,null);

        }
        else{



        }


        return update_row;
    }
}
