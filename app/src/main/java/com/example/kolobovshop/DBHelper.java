package com.example.kolobovshop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "KolobovShop";
    public static final String TABLE_GOODS= "Хорошо";

    public static final String KEY_ID = "id";
    public static final String KEY_NAZVANIE = "Название";
    public static final String KEY_PRICE = "Цена";

    public DBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(" create table " + TABLE_GOODS + "(" + KEY_ID  + " integer primary key," + KEY_NAZVANIE + " text," + KEY_PRICE + " text" +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_GOODS);
        onCreate(sqLiteDatabase);
    }
}
