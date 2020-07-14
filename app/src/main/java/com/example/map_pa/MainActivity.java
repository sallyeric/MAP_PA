package com.example.map_pa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;
    String uid="", pw="";
    EditText usernameET,passwordET;

    ArrayList<String> data;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data=new ArrayList<String>();
        usernameET=(EditText) findViewById(R.id.userid);
        passwordET=(EditText) findViewById(R.id.password);

        mPostReference= FirebaseDatabase.getInstance().getReference();

        if(getIntent().getExtras() != null){
            EditText username = (EditText)findViewById(R.id.userid);
            Intent signupIntent = getIntent();
            username.setText(signupIntent.getStringExtra("Username"));
        }

        Button login = (Button)findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에서 값 비교

                uid=usernameET.getText().toString();
                pw=passwordET.getText().toString();
                DatabaseReference idRef = FirebaseDatabase.getInstance().getReference("id_list");
                DatabaseReference usernameRef = idRef.child(uid);
                DatabaseReference passwordRef = usernameRef.child("signupPassword");

                passwordRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Log.i(TAG, dataSnapshot.getValue(String.class));
                        String getfullname=dataSnapshot.getValue(String.class);
                        if(getfullname.equals(pw)==true){
                            EditText username = (EditText)findViewById(R.id.userid);
                            Intent loginIntent = new Intent(MainActivity.this, postPage.class);
                            loginIntent.putExtra("Username", username.getText().toString());
                            startActivity(loginIntent);
                        }else{
                            Toast.makeText(MainActivity.this,"Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });


            }
        });

        TextView signup = (TextView)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(MainActivity.this, SignUp.class);
                startActivity(signupIntent);
            }
        });

        // 로그인 정보가 맞는지 확인하려면 ??
        // 해당 로그인 정보를 intent로 넘겨주고
        // 그 아이디 밑에 회원정보와 post 목록 db에 저장
    }
}
