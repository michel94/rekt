package com.crowdconquer.crowdconquer.network;

import android.util.Log;

import com.crowdconquer.crowdconquer.utils.Callback;

import com.crowdconquer.crowdconquer.utils.RPC;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by michel on 9/24/15.
 */
public class NetworkTask extends Thread{
    private String T="Network"; // tag for Log
    private String socketEndpoint = "wss://192.168.1.78:8080";
    private RPC rpc;
    private boolean running = false;

    private Callback onConnectionCallback = null;

    private WebSocket ws;

    public NetworkTask(){
    }

    public boolean isRunning() {
        return running;
    }

    public WebSocket getSocket() {
        return ws;
    }

    public void onConnect(Callback callback){
        this.onConnectionCallback = callback;
    }

    public RPC getRpc(){
        return rpc;
    }
    @Override
    public void run() {
        Log.d(T, "Network thread created.");
        started = true;

        try{
            WebSocketFactory factory = new WebSocketFactory();
            factory.setSSLContext(NaiveSSLContext.getInstance("TLS"));

            ws = factory.createSocket(socketEndpoint)
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket ws, String message) {
                            /* handles server messages */

                            try {
                                JSONObject jRoot = new JSONObject(message);
                                if (!jRoot.has("protocol")) {
                                    return;
                                }
                                if (jRoot.getString("protocol").equals("rpcCallback") && jRoot.has("rpcCallback")) {
                                    handleCallback(jRoot.getJSONObject("rpcCallback"));
                                } else if (jRoot.getString("protocol").equals("update") && jRoot.has("update")) {
                                    handleUpdate(jRoot.getJSONObject("update"));
                                }

                            } catch (JSONException e) {
                                Log.d(T, "Invalid JSON object.");
                            }

                            // Close the WebSocket connection.
                            //ws.disconnect();
                        }
                    })
                    .connect();

            rpc = new RPC(ws);
            if(onConnectionCallback != null)
                onConnectionCallback.action();


            Log.d(T, "connected");
        }catch (Exception e){
            Log.d(T, "could not connect");
            e.printStackTrace();
        }
    }

    public void handleCallback(JSONObject json){
        // TODO entire function needs better error handling but depends on the specific json to class code

        try {
            JSONObject jsonResponse = json.getJSONObject("response");
            int id = json.getInt("callbackId");

            String response = jsonResponse.getString("info"); // TODO automatic conversion from json response to Object
            rpc.handleResponse(id, response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void handleUpdate(JSONObject json){

    }

}