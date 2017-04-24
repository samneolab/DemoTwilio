package com.neo_lab.demotwilio.ui.video_calling_room.domain.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sam_nguyen on 24/04/2017.
 */

public class Token implements com.neo_lab.demotwilio.model.Token{

    @SerializedName("identity")
    private String identity;

    @SerializedName("token")
    private String token;


    public Token(String identity, String token) {
        this.identity = identity;
        this.token = token;
    }


    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getToken() {
        return this.token;
    }
}
