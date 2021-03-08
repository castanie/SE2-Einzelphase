package com.example.einzelphase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) throws ExecutionException, InterruptedException {
        Callable callable = new Callable() {
            @Override
            public String call() throws Exception {
                try {
                    Socket client = new Socket("se2-isys.aau.at", 53212);

                    OutputStreamWriter writer = new OutputStreamWriter(client.getOutputStream());
                    InputStreamReader reader = new InputStreamReader(client.getInputStream());

                    // Write data to stream; must append LF character.
                    writer.write("11904712\n");
                    writer.flush();

                    // Read data from stream.
                    char[] message = new char[96];
                    reader.read(message);

                    client.close();
                    writer.close();
                    reader.close();
                    return String.valueOf(message).trim();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        Future<String> message = Executors.newCachedThreadPool().submit(callable);

        final TextView bottomTextView = (TextView) findViewById(R.id.bottomTextView);
        bottomTextView.setText(message.get());
    }
}