package com.example.map_pa;

import java.util.HashMap;
import java.util.Map;

public class FirebasePost_ProfileImage {
    public String signupUsername; //수정
    public String profileImageName;

    public FirebasePost_ProfileImage(){

    }

    public FirebasePost_ProfileImage(String signupUsername, String profileImageName){ //수정
        this.signupUsername=signupUsername;
        this.profileImageName=profileImageName;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result=new HashMap<>();
        result.put("signupUsername",signupUsername);
        result.put("profileImageName",profileImageName);
        return result;
    }
}
