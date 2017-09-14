package com.workforce.test;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.java_websocket.WebSocket;

import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = (Button) findViewById(R.id.button);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Operation().execute("");
            }
        });
    }

    private class Operation extends AsyncTask<String, Void, String>{
        private StompClient client;
        private static final String TAG = "Operation";

        @Override
        protected String doInBackground(String... strings) {
            client = Stomp.over(WebSocket.class,
                    "http://67.207.91.31:8080/mobstaff-mobile/websocket");
            client.connect();

            client.topic("/topic/notification").subscribe(new Consumer<StompMessage>() {
                @Override
                public void accept(StompMessage stompMessage) throws Exception {
                    Log.d(TAG, "Got response - " + stompMessage.getPayload());
                }
            });

            client.lifecycle().subscribe(new Consumer<LifecycleEvent>() {
                @Override
                public void accept(LifecycleEvent lifecycleEvent) throws Exception {
                    switch (lifecycleEvent.getType()){
                        case OPENED:
                            Log.d(TAG, "Opened");
                            break;
                        case CLOSED:
                            Log.d(TAG, "Closed");
                            break;
                        case ERROR:
                            Log.d(TAG, "Error");
                            break;
                    }
                }
            });
            return "Executed";
        }
    }
}
