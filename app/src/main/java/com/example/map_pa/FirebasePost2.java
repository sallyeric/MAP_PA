package com.example.map_pa;

import java.util.HashMap;
import java.util.Map;

public class FirebasePost2 {  //수정
    public String signupUsername;
    public String postContext;
    public String postTags;
    public String makePublic;

    public FirebasePost2(){

    }

    public FirebasePost2(String signupUsername,String postContext, String postTags,String makePublic){ //수정String signupUsername String publicPost
        this.signupUsername=signupUsername;
        this.postContext=postContext;
        this.postTags=postTags;
        this.makePublic=makePublic;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result=new HashMap<>();
        result.put("signupUsername",signupUsername);
        result.put("postContext",postContext);
        result.put("postTags",postTags);
        result.put("makePublic",makePublic);
        return result;
    }
}

