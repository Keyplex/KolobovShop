package com.example.kolobovshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    Button btnAdd, btnClear, btnBuy;
    EditText etNazvanie, etPrice;
    DBHelper dbHelper;
    ContentValues contentValues;
    SQLiteDatabase database;
    TextView SumItog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnAdd = (Button) findViewById(R.id.Add);
        btnAdd.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.Clear);
        btnClear.setOnClickListener(this);

        etNazvanie = (EditText) findViewById(R.id.Nazvanie);
        etPrice = (EditText) findViewById(R.id.Price);


        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        btnBuy = (Button) findViewById(R.id.BuyAll);
        btnBuy.setOnClickListener(this);

        SumItog = (TextView) findViewById(R.id.SummKorzina);

        UpdateTable();
    }

    private void UpdateTable() {
        Cursor cursor = database.query(DBHelper.TABLE_GOODS, null,null,null,null, null, null);
        if (cursor.moveToFirst())
        {
            int id = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nazvanie = cursor.getColumnIndex(DBHelper.KEY_NAZVANIE);
            int price = cursor.getColumnIndex(DBHelper.KEY_PRICE);
            TableLayout table2 = findViewById(R.id.tableLayout2);
            table2.removeAllViews();
            do {
                TableRow tableOUT = new TableRow(this);
                tableOUT.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                TextView ID1 = new TextView(this);
                params.weight = 1f;
                ID1.setLayoutParams(params);
                ID1.setText(cursor.getString(id));
                tableOUT.addView(ID1);

                TextView nazvanie1 = new TextView(this);
                params.weight = 1f;
                nazvanie1.setLayoutParams(params);
                nazvanie1.setText(cursor.getString(nazvanie));
                tableOUT.addView(nazvanie1);

                TextView price1 = new TextView(this);
                params.weight = 3f;
                price1.setLayoutParams(params);
                price1.setText(cursor.getString(price));
                tableOUT.addView(price1);

                Button delete = new Button(this);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       View outRow = (View) view.getParent();
                       ViewGroup outBD = (ViewGroup) outRow.getParent();

                        outBD.removeView(outRow);
                        outBD.invalidate();

                        database.delete(DBHelper.TABLE_GOODS, DBHelper.KEY_ID + " = ?", new String[]{String.valueOf(view.getId())});
                        contentValues = new ContentValues();
                        Cursor cursorupd = database.query(DBHelper.TABLE_GOODS, null,null,null, null, null, null);

                        if (cursorupd.moveToFirst())
                        {
                            int id = cursorupd.getColumnIndex(DBHelper.KEY_ID);
                            int nazvanie = cursorupd.getColumnIndex(DBHelper.KEY_NAZVANIE);
                            int price = cursorupd.getColumnIndex(DBHelper.KEY_PRICE);
                            int realid = 1;
                            do {
                                if (cursorupd.getInt(id) > realid)
                                {
                                    contentValues.put(DBHelper.KEY_ID, realid);
                                    contentValues.put(DBHelper.KEY_NAZVANIE, cursorupd.getString(nazvanie));
                                    contentValues.put(DBHelper.KEY_PRICE, cursorupd.getString(price));
                                    database.replace(DBHelper.TABLE_GOODS,null,contentValues);
                                }
                                realid++;
                            }
                            while (cursorupd.moveToNext());
                            if (cursorupd.moveToLast() && view.getId() != realid)
                            {
                                database.delete(DBHelper.TABLE_GOODS,DBHelper.KEY_ID + " = ?", new String[]{cursorupd.getString(id)});
                            }
                            UpdateTable();
                        }
                    }
                });
                params.weight = 1f;
                delete.setLayoutParams(params);
                delete.setText("Удалить запись");
                delete.setId(cursor.getInt(id));
                tableOUT.addView(delete);

                Button buy = new Button(this);
                buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View outRow = (View) view.getParent();
                        ViewGroup outBD = (ViewGroup) outRow.getParent();
                        outBD.removeView(outRow);
                        outBD.invalidate();
                        String k =  "id = ?";

                        Cursor price = database.query(DBHelper.TABLE_GOODS, null, k, new String[]{String.valueOf(view.getId())}, null, null, null);
                        double sum = Float.valueOf(SumItog.getText().toString());
                        double sum2 = 0;
                        if (price != null)
                        {
                            Log.d("mLog","Курсор фулл ");
                            if (price.moveToFirst())
                            {
                                int Price = price.getColumnIndex(DBHelper.KEY_PRICE);
                                do {
                                    sum2 = price.getDouble(Price);
                                    Log.d("mLog", " "+sum2);
                                }
                                while (price.moveToNext());
                                UpdateTable();
                            }
                            price.close();
                        }
                        else
                            Log.d("mLog", "Курсор равен нулю ");
                        sum = sum+sum2;
                        SumItog.setText(String.valueOf(sum));
                    }
                });
                params.weight = 1f;
                buy.setLayoutParams(params);
                buy.setText("Купить товар ");
                buy.setId(cursor.getInt(id));
                tableOUT.addView(buy);
                table2.addView(tableOUT);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
    }


    @Override
    public void onClick(View view) {
        dbHelper = new DBHelper(this);
        String nazvanie = etNazvanie.getText().toString();
        String price = etPrice.getText().toString();
        contentValues = new ContentValues();

        switch (view.getId())
        {
            case R.id.BuyAll:
                Toast toast = Toast.makeText(getApplicationContext(),"Сумма заказа = "+ SumItog.getText(),Toast.LENGTH_SHORT);
                toast.show();
                SumItog.setText(" 0");
                break;

            case R.id.Add:
                contentValues.put(DBHelper.KEY_NAZVANIE,nazvanie);
                contentValues.put(DBHelper.KEY_PRICE,price);

                database.insert(DBHelper.TABLE_GOODS,null,contentValues);
                UpdateTable();
                etNazvanie.setText(null);
                etPrice.setText(null);
                break;

            case R.id.Clear:
                database.delete(DBHelper.TABLE_GOODS,null,null);
                TableLayout dbOutput = findViewById(R.id.tableLayout2);
                dbOutput.removeAllViews();
                etNazvanie.setText(null);
                etPrice.setText(null);
                SumItog.setText(" 0");
                UpdateTable();
                break;

            default:
                break;
        }
        dbHelper.close();
    }
}