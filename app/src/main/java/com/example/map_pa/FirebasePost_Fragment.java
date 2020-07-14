package com.example.map_pa;

import java.util.HashMap;
import java.util.Map;

public class FirebasePost_Fragment {
    public String signupUsername;
    public String postContext;
    public String postTags;

    public FirebasePost_Fragment(){

    }

    public FirebasePost_Fragment(String signupUsername,String postContext, String postTags){ //수정String signupUsername String publicPost
        this.signupUsername=signupUsername;
        this.postContext=postContext;
        this.postTags=postTags;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result=new HashMap<>();
        result.put("signupUsername",signupUsername);
        result.put("postContext",postContext);
        result.put("postTags",postTags);
        return result;
    }
}

