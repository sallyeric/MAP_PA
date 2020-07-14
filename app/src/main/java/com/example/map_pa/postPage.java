package com.example.map_pa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class postPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;

    //logcat
    private static final String TAG="PostPage";

    // Storage
    private static final int PICK_IMAGE = 777;
    private StorageReference mStorageRef;
    private StorageReference mStorageRef2;
    Uri currentImageUri;
    boolean check;

    private DatabaseReference mPostReference;
    private DatabaseReference mPostReference2;
    private DatabaseReference mPostReference3;
    String signupUsername="", signupFullname="", signupBirthday="", signupEmail="", profileImageName="";
    ArrayList<String> data;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> data2;
    ArrayAdapter<String> arrayAdapter2;

    //String[] info;
    String param="", queryResult="", result1="", result2="", result3="";

    // adapter
    ListView listView;
    ArrayList<PostItem> memos;
    PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);

        Intent postPageIntent = getIntent();
        String username = postPageIntent.getStringExtra("Username");
        signupUsername=username; // 프로필 이미지 post함수에

        data=new ArrayList<String>();
        mPostReference= FirebaseDatabase.getInstance().getReference();
        arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        data2=new ArrayList<String>(); // 프로필 이미지 리스트
        mPostReference2= FirebaseDatabase.getInstance().getReference();
        arrayAdapter2=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        ImageButton newPost = (ImageButton)findViewById(R.id.newPost);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostIntent = new Intent(postPage.this, NewPost.class);
                newPostIntent.putExtra("Username", signupUsername);
                startActivity(newPostIntent);
            }
        });

        //FRAGMENT
/*
        memos=new ArrayList<PostItem>(); //추가
        listView=(ListView) findViewById(R.id.dataList); //추가
        mPostReference3= FirebaseDatabase.getInstance().getReference(); //추가

        adapter=new PostAdapter(this, memos);
        listView.setAdapter(adapter);*/

        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewPager2 viewPager2 = findViewById(R.id.viewpager);
        viewPager2.setAdapter(new myFragmentStateAdapter(this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Personal");
                        break;
                    case 1:
                        tab.setText("Public");
                        break;
                }
            }
        });
        tabLayoutMediator.attach();



        //getFirebaseDatabase();

        //DRAWER
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);
        final Menu menu = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();

        // 로그인한 아이디로 정보를 가져오려면??
        //헤더에 ImageStorage랑 아이디도 가져와야함 //url 연결
        // header
        View headerView = navigationView.getHeaderView(0);
        TextView headerID = (TextView) headerView.findViewById(R.id.drawer_username) ;
        headerID.setText(username) ;

        mStorageRef = FirebaseStorage.getInstance().getReference("Images"); // for upload

        ImageButton image = (ImageButton) headerView.findViewById(R.id.profileImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);

                if(check){
                    final StorageReference riversRef = mStorageRef.child(currentImageUri.getLastPathSegment());
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
                            Toast.makeText(postPage.this,"Upload Success!!", Toast.LENGTH_LONG);
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            //profileImageName=taskSnapshot.getMetadata().getName().toString();
                            //profileImageName=riversRef.getDownloadUrl().toString();
                            //Log.d(TAG, profileImageName);
                            //postFirebaseDatabase(true);
                        }
                    });
                }


                //profileImage=currentImageUri.getPath().toString();
                //postFirebaseDatabase(true);
            }
        });

        //이미지 uri 를 realtime DB에 넣어줘야함

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
        drawerLayout.closeDrawer(GravityCompat.START);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            ImageView img = (ImageView) findViewById(R.id.profileImage);
            currentImageUri = data.getData();
            check = true;
            img.setImageURI(currentImageUri);
            //profileImage=img.toString();
        }
    }

    public void getFirebaseDatabase(){ //매개변수 추가 해보자
        final ValueEventListener postListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange","Data is Updated");
                memos.clear();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String key=postSnapshot.getKey();
                    FirebasePost_Fragment get=postSnapshot.getValue(FirebasePost_Fragment.class);
                    String[] info={get.signupUsername, get.postContext, get.postTags};
                    PostItem result=new PostItem(info[0],info[1],info[2]);

                    memos.add(result);
                    Log.d("getFirebaseDatabase","key: "+key);
                    Log.d("getFirebaseDatabase","info: "+info[0]+info[1]+info[2]);

                }
                //arrayAdapter.clear();
                //arrayAdapter.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mPostReference.child("post_list").addValueEventListener(postListener); // 여기도 에러 ㅠㅠ
    }


    public void postFirebaseDatabase(boolean add){
        Map<String,Object> childUpdates=new HashMap<>();
        Map<String,Object> postValues=null;
        if(add){
            FirebasePost_ProfileImage post=new FirebasePost_ProfileImage(signupUsername,profileImageName);
            postValues=post.toMap();
        }
        childUpdates.put("/profileImage__list/"+signupUsername,postValues);
        mPostReference2.updateChildren(childUpdates);
        clearET();
    }

    public void clearET(){
        signupUsername="";
        profileImageName="";
    }

}
