package com.upc.inmoperu.ui.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.upc.inmoperu.ui.Entidad.Oferta;

import java.util.ArrayList;
import java.util.List;

public class DBAdapter extends SQLiteOpenHelper {

    private static final String NOMBRE_BD="oferta.bd";
    private static final int VERSION_BD=1;
    private static final String TABLA_OFERTA="CREATE TABLE OFERTA(CODIGO INTEGER PRIMARY KEY AUTOINCREMENT,USERID TEXT,TITLE TEXT, DESCRIPTION TEXT, FECHA TEXT)";

    public DBAdapter(Context context) {
        super(context, NOMBRE_BD,null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLA_OFERTA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLA_OFERTA);
        sqLiteDatabase.execSQL(TABLA_OFERTA);
    }

    public void agregarOferta(String userid,String title, String description, String fecha){
        SQLiteDatabase bd=getWritableDatabase();
        if(bd!=null){
            bd.execSQL("INSERT INTO OFERTA (USERID,TITLE,DESCRIPTION,FECHA) VALUES('"+userid+"','"+title+"','"+description+"','"+fecha+"')");
            bd.close();
        }
    }

    public List<Oferta> mostrarOfertas() {
        SQLiteDatabase bd = getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM OFERTA", null);
        List<Oferta> ofertas = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                ofertas.add(new Oferta(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
            }while(cursor.moveToNext());
        }
        return ofertas;
    }

    public void eliminarOfertas(int id){
        SQLiteDatabase bd = getWritableDatabase();
        if(bd!= null){
            bd.execSQL("DELETE FROM OFERTA WHERE CODIGO="+id);
            bd.close();
        }
    }
}
