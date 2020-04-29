package com.example.mvcsocketserver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_D = "MainActivity_D";
    private static boolean isStart = true;
    private static ServerResponseThread serverResponseThread;

    Button btnStartServer;
    EditText etServerIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartServer = (Button) findViewById(R.id.btn_start_server);
        etServerIP = (EditText) findViewById(R.id.et_server_IP);

        initUI();
    }

    private void initUI(){

        btnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initServer();
            }
        });

    }
    private void initServer() {
        ServerSocket serverSocket = null;
        ExecutorService executorService = Executors.newCachedThreadPool();
       try {
           etServerIP.setText(SocketUtil.getIP());
           serverSocket = new ServerSocket(SocketUtil.PORT);
           while (isStart) {
                Socket socket = serverSocket.accept();
                //设定输入流读取阻塞超时时间(10秒收不到客户端消息判定断线)
                socket.setSoTimeout(10000);
                serverResponseThread = new ServerResponseThread(socket,
                        new SocketServerResponseInterface() {

                            @Override
                            public void clientOffline() {// 对方不在线
                                Toast.makeText(getApplicationContext(),"offline",Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void clientOnline(String clientIp) {
                                Toast.makeText(getApplicationContext(),clientIp + " is online",Toast.LENGTH_SHORT);
                            }
                        });

                if (socket.isConnected()) {
                    executorService.execute(serverResponseThread);
                }
            }

            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    isStart = false;
                    serverSocket.close();
                    serverResponseThread.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

