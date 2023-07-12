package com.example.originalegs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;

public class daoProductos {
    SQLiteDatabase cx;
    ArrayList<Productos> lista= new ArrayList<Productos>();
    Productos c;
    Context ct;
    String nombreBD = "EGS";
    String tabla = "create table if not exists productos(id integer primary key autoincrement, nombre text, stock integer, precio double)";

    public daoProductos(Context c) {
        this.ct = c;
        cx = c.openOrCreateDatabase(nombreBD, c.MODE_PRIVATE, null);
        cx.execSQL(tabla);
    }

    public boolean insertar(Productos c) {
        ContentValues contenedor=new ContentValues();
        contenedor.put("nombre",c.getNombre());
        contenedor.put("stock",c.getStock());
        contenedor.put("precio",c.getPrecio());
        return (cx.insert("productos",null,contenedor))>0;

    }
    public boolean editar(Productos c){
        ContentValues contenedor=new ContentValues();
        contenedor.put("nombre",c.getNombre());
        contenedor.put("stock",c.getStock());
        contenedor.put("precio",c.getPrecio());
        return (cx.update("productos" , contenedor,"id"+c.getId(), null))>0;


    }
    public boolean eliminar(int id){

        return (cx.delete("productos","id"+id,null))>0;

    }

    public ArrayList<Productos> verTodos(){
        lista.clear();
        Cursor cursor=cx.rawQuery("select * from productos", null);
        if(cursor!=null &&cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                lista.add(new Productos(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getDouble(3)));
            }while (cursor.moveToNext());
        }
        return  lista;

    }
    public Productos verUno(int posicion){
        Cursor cursor=cx.rawQuery("select * from productos", null);
        cursor.moveToPosition(posicion);
        c=new Productos(cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getDouble(3));
        return c;
    }
}
