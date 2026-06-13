package com.vkym.player.data.remote.vk;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VKResponse<T> {
    @SerializedName("response")
    public ResponseData<T> response;
    
    @SerializedName("error")
    public Error error;
    
    public static class ResponseData<T> {
        public List<T> items;
        public int count;
    }
    
    public static class Error {
        public int error_code;
        public String error_msg;
    }
    
    public boolean isSuccessful() {
        return error == null;
    }
}
