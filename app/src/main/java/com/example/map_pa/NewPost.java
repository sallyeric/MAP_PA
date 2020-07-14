package com.example.map_pa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewPost extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;

    // Storage
    private static final int PICK_IMAGE = 777;
    private StorageReference mStorageRef;
    private StorageReference mStorageRef2;
    Uri currentImageUri;
    boolean check;

    //  Database
    private DatabaseReference mPostReference;
    String signupUsername="";
    String postContent="", postTags="";
    String makePublic=""; //체크박스
    EditText postContentET,postTagsET;
    CheckBox publicPostCB;
    ArrayList<String> data;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        data=new ArrayList<String>();
        postContentET=(EditText) findViewById(R.id.postContent);
        postTagsET=(EditText) findViewById(R.id.postTags);
        publicPostCB=(CheckBox) findViewById(R.id.publicPost);

        mPostReference= FirebaseDatabase.getInstance().getReference();

        mStorageRef= FirebaseStorage.getInstance().getReference("Images");
        check=false;

        Intent newPostIntent = getIntent();
        String username = newPostIntent.getStringExtra("Username");
        signupUsername=username;

        // 이미지 버튼 onclick 안됨
        ImageButton image = (ImageButton)findViewById(R.id.postImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        Button createPost = (Button)findViewById(R.id.createPost);
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 업로드
                if(check){
                    StorageReference riversRef = mStorageRef.child(currentImageUri.getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(currentImageUri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(NewPost.this,"Upload Success!!", Toast.LENGTH_LONG);
                        }
                    });
                }


                postContent=postContentET.getText().toString();
                postTags=postTagsET.getText().toString();

                if(publicPostCB.isChecked()){
                    // 체크박스 어떻게..?? // 공개/비공개 DB에 어떻게 따로?
                    makePublic="0"; //공개
                } else {
                    makePublic="1"; //비공개
                }

                if((postContent.length())==0){
                    Toast.makeText(NewPost.this,"Please input contents", Toast.LENGTH_SHORT).show();
                } else {
                    postFirebaseDatabase(true);
                }

                // fragment 따라 어떻게??
                Intent createPostIntent = new Intent(NewPost.this, postPage.class);
                createPostIntent.putExtra("Username", signupUsername);
                startActivity(createPostIntent);
            }
        });

        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 여기도 drawer에 회원 정보 구현
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);
        final Menu menu = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();

        //drawer 구현
        View headerView = navigationView.getHeaderView(0);
        TextView headerID = (TextView) headerView.findViewById(R.id.drawer_username) ;
        headerID.setText(username) ;

        mStorageRef2 = FirebaseStorage.getInstance().getReference("Images"); // for upload

        ImageButton profileimage = (ImageButton) headerView.findViewById(R.id.profileImage);
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);

                if(check){
                    final StorageReference riversRef = mStorageRef2.child(currentImageUri.getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(currentImageUri);
                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(NewPost.this,"Upload Success!!", Toast.LENGTH_LONG);
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                        }
                    });
                }
            }
        });

        // menu
        DatabaseReference idRef = FirebaseDatabase.getInstance().getReference("id_list");
        DatabaseReference usernameRef = idRef.child(username);
        DatabaseReference fullnameRef = usernameRef.child("signupFullname");
        final MenuItem fullname = menu.findItem(R.id.navigationFullname);

        fullnameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.i(TAG, dataSnapshot.getValue(String.class));
                String getfullname=dataSnapshot.getValue(String.class);
                fullname.setTitle(getfullname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });

        DatabaseReference birthdayRef = usernameRef.child("signupBirthday");
        final MenuItem birthday = menu.findItem(R.id.navigationBirthday);

        birthdayRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.i(TAG, dataSnapshot.getValue(String.class));
                String getbirthday=dataSnapshot.getValue(String.class);
                birthday.setTitle(getbirthday);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });

        DatabaseReference emailRef = usernameRef.child("signupEmail");
        final MenuItem email = menu.findItem(R.id.navigationEmail);

        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.i(TAG, dataSnapshot.getValue(String.class));
                String getemail=dataSnapshot.getValue(String.class);
                email.setTitle(getemail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeDrawer();

        switch (item.getItemId()){

            case R.id.navigationBirthday:
                break;

            case R.id.navigationEmail:
                break;

            case R.id.navigationFullname:
                break;
        }


        return false;
    }

    private void closeDrawer(){
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            closeDrawer();
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            ImageView img = (ImageView) findViewById(R.id.postImage);
            currentImageUri = data.getData();
            check = true;
            img.setImageURI(currentImageUri);
        }
    }

    public void postFirebaseDatabase(boolean add){
        Map<String,Object> childUpdates=new HashMap<>();
        Map<String,Object> postValues=null;
        if(add){
            FirebasePost2 post2=new FirebasePost2(signupUsername,postContent,postTags,makePublic); //makePublic
            postValues=post2.toMap();
        }
        childUpdates.put("/post_list/"+postContent,postValues);
        mPostReference.updateChildren(childUpdates);
        clearET();
    }

    public void clearET(){
        postContentET.setText("");
        postTagsET.setText("");
        publicPostCB.setChecked(false);
        postContent="";
        postTags="";
        makePublic="";
    }
}
