package com.example.freemediaplayer

import android.content.ContentUris
import android.database.Cursor
import android.database.Cursor.FIELD_TYPE_BLOB
import android.database.Cursor.FIELD_TYPE_NULL
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log

private val TAG = "DB_DEBUG"

fun isSameOrAfterS() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
fun isSameOrAfterQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
fun isBeforeQ() = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

fun printAllColumnContent(cursor: Cursor){
//    for (i in 0..cursor.columnCount){
//        val colType = cursor.getType(i)

        val idColIndex = cursor.getColumnIndex("_id")

        //if (colType != FIELD_TYPE_BLOB && colType != FIELD_TYPE_NULL){
            val contentUri: Uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                ),
                cursor.getLong(idColIndex)
            )

            //Log.d(TAG, "${cursor.getColumnName(i)}: ${cursor.getString(i)}")

            Log.d(TAG, "$contentUri")
        //}
    //}
}