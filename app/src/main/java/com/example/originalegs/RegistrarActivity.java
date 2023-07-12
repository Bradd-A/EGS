package com.example.originalegs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class RegistrarActivity extends AppCompatActivity {
    Button signup;
    TextInputLayout nameR, userR, passR, repassR;
    DBHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        enlazarControles();
        register();
    }

    private void register() {
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameR.getEditText().getText().toString();
                String user = userR.getEditText().getText().toString();
                String pass = passR.getEditText().getText().toString();
                String repass = repassR.getEditText().getText().toString();

                if(name.equals("")||user.equals("")||pass.equals("")||repass.equals("")){
                    Toast.makeText(RegistrarActivity.this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show();
                }else{

                    if(pass.equals(repass)){
                        Boolean checkuser = DB.checkusername(user);
                        if(checkuser==false){
                            Boolean insert = DB.insertData(user, name, pass);
                            if(insert==true){
                                Toast.makeText(RegistrarActivity.this, "Registro satisfactorio", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(RegistrarActivity.this, "Registro fallido", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(RegistrarActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegistrarActivity.this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }

    private void enlazarControles() {
        signup = (Button) findViewById(R.id.btnRegister);
        nameR = findViewById(R.id.txtInputName);
        userR = findViewById(R.id.txtInputUser);
        passR = findViewById(R.id.txtInputPassword);
        repassR = findViewById(R.id.txtInputRepass);
        DB = new DBHelper(this);
    }


}
