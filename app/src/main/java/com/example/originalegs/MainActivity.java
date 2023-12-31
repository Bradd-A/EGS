package com.example.originalegs;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.originalegs.adaptador.ProductoAdapterRecyclerView;
import com.example.originalegs.modelo.CardViewProducto;
import com.example.originalegs.modelo.ConexionSQLite;
import com.example.originalegs.utilidades.Utilidades;
import com.example.originalegs.vistas.AddProductActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ProductoAdapterRecyclerView adapter;
    private static RecyclerView recycler;
    private ArrayList<CardViewProducto> cardViewProductos;

    public static final String EXTRA_UPDATE = "update";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_COLOR = "color";
    public static final String EXTRA_INITIAL = "initial";
    public static final String EXTRA_CLA = "clave";
    public static final String EXTRA_PRE = "precio";
    public static final String EXTRA_DET = "detalle";
    public static final String EXTRA_CAN = "cantidad";
    ConexionSQLite conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        conn = new ConexionSQLite(this, "bd_prod", null, 1);
        recycler = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        cardViewProductos = new ArrayList<>();
        adapter = new ProductoAdapterRecyclerView(buildProductos(),this, R.layout.card_producto);
        recycler.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity act = MainActivity.this;
                Intent intent = new Intent(act, AddProductActivity.class);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    Explode explode = new Explode();
                    explode.setDuration(500);
                    getWindow().setExitTransition(explode);
                    startActivityForResult(intent,adapter.getItemCount(),
                            ActivityOptionsCompat.makeSceneTransitionAnimation(act,  view,
                                    getString(R.string.transition_fab)).toBundle());
                }
                else{
                    startActivityForResult(intent, adapter.getItemCount());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == adapter.getItemCount()) {
            if (resultCode == MainActivity.RESULT_OK) {
                String name = data.getStringExtra(EXTRA_NAME);
                int color = data.getIntExtra(EXTRA_COLOR, 0);
                String clue = data.getStringExtra(EXTRA_CLA);
                String precio = data.getStringExtra(EXTRA_PRE);
                String detalle = data.getStringExtra(EXTRA_DET);
                String cantidad = data.getStringExtra(EXTRA_CAN);

                adapter.addCard(clue, name, precio, detalle, cantidad, color);
            }
        }else{
            if (resultCode == MainActivity.RESULT_OK){
                RecyclerView.ViewHolder viewHolder = recycler.findViewHolderForAdapterPosition(requestCode);
                if (data.getExtras().getBoolean(EXTRA_UPDATE)) {
                    // if name changed, update user
                    String name = data.getStringExtra(EXTRA_NAME);
                    int color = data.getIntExtra(EXTRA_COLOR, 0);
                    String cla = data.getStringExtra(EXTRA_CLA);
                    String pre = data.getStringExtra(EXTRA_PRE);
                    String det = data.getStringExtra(EXTRA_DET);
                    String can = data.getStringExtra(EXTRA_CAN);
                    viewHolder.itemView.setVisibility(View.INVISIBLE);
                    adapter.updateCard(requestCode, name, cla, pre, det, can, color);
                }
            }
        }
    }

    public static void doSmoothScroll(int position)
    {
        recycler.smoothScrollToPosition(position);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private ArrayList<CardViewProducto> buildProductos() {
        SQLiteDatabase bd = conn.getReadableDatabase();
        CardViewProducto card = null;
        Cursor cursorU = bd.rawQuery("SELECT * FROM "+ Utilidades.TABLA_PRODUCTOS, null);
        Cursor cursorCU = bd.rawQuery("SELECT * FROM "+ Utilidades.TABLA_CARD_PRODUCTO, null);

        while ( cursorU.moveToNext() && cursorCU.moveToNext() ){
            card = new CardViewProducto();
            card.setClave( cursorU.getString(0) );
            card.setNombre( cursorU.getString(1) );
            card.setPrecio( String.valueOf(cursorU.getFloat(2)) );
            card.setDetalle( cursorU.getString(3) );
            card.setCantidad( String.valueOf(cursorU.getInt(4)) );
            card.setId( cursorCU.getInt(0) );
            card.setInicial( cursorCU.getString(1) );
            card.setColor( cursorCU.getInt(2) );
            cardViewProductos.add(card);
        }

        return cardViewProductos;
    }
}



/**
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnagregar;
    Button btnactualizar;

    daoProductos dao;
    Adapter adapter;
public void agregarProducto(View view){

}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnagregar = (Button) findViewById(R.id.btnagregar);
        btnagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent btnagregar = new Intent(MainActivity.this, agregar.class);
                startActivity(btnagregar);
            }
        });

        btnactualizar = (Button) findViewById(R.id.btnactualizar);
        btnactualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent btnactualizar = new Intent(MainActivity.this, editar.class);
                startActivity(btnactualizar);
            }
        });

    }} */