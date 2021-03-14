package com.example.einzelphase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

    public void sendButtonAction(View view) throws ExecutionException, InterruptedException {
        final EditText editTextNumber = (EditText) findViewById(R.id.editTextNumber);
        final String studentNumber = editTextNumber.getText().toString();

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    Socket client = new Socket("se2-isys.aau.at", 53212);

                    OutputStreamWriter writer = new OutputStreamWriter(client.getOutputStream());
                    InputStreamReader reader = new InputStreamReader(client.getInputStream());

                    // Write data to stream; must append LF character.
                    writer.write(studentNumber + "\n");
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

    public void calcButtonAction(View view) {
        final EditText editTextNumber = (EditText) findViewById(R.id.editTextNumber);
        final String studentNumber = editTextNumber.getText().toString();

        String message = "Es existieren keine zwei Ziffern mit ggT > 1";
        if (studentNumber.matches("[0-9]{8}")) {
            loop:
            for (int i = 0; i < studentNumber.length(); ++i) {
                for (int j = 0; j < studentNumber.length(); ++j) {
                    if (i != j && getGCD(Character.getNumericValue(studentNumber.charAt(i)), Character.getNumericValue(studentNumber.charAt(j))) > 1) {
                        message = "Es existieren zwei Ziffern mit ggT = " + getGCD(Character.getNumericValue(studentNumber.charAt(i)), Character.getNumericValue(studentNumber.charAt(j))) + ": [" + i + "] und [" + j + "]";
                        break loop;
                    }
                }
            }
        } else {
            message = "Dies ist keine gueltige Matrikelnummer";
        }

        final TextView bottomTextView = (TextView) findViewById(R.id.bottomTextView);
        bottomTextView.setText(message);
    }

    private int getGCD(int p, int q) {
        while (q != 0) {
            int temp = q;
            q = p % q;
            p = temp;
        }
        return p;
    }
}