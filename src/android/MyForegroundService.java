package org.apache.cordova.logcat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MyForegroundService extends Service {

    private static final String TAG = "MyForegroundService";
    private static final String CHANNELID = "Foreground Service ID";
    private static final NotificationChannel channel = new NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_LOW
    );
    private Process process = null;
    private BufferedReader reader = null;
    private File logFile = null;
    private BufferedWriter writer = null;

    @Override
    public void onCreate() {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {

                                while (true) {

                                    if(reader == null || !isProcessAlive(process)) {
                                        process = startLogcatProcess();
                                        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                    }

                                    logFile = generateLogFile();
                                    writer = new BufferedWriter(new FileWriter(logFile));

                                    String line;
                                    int countLines = 0;

                                    while ((line = reader.readLine()) != null) {

                                        if (!isCurrentLogFile(logFile)) {
                                            writer.close();
                                            logFile = generateLogFile();
                                            writer = new BufferedWriter(new FileWriter(logFile));
                                            Log.i(TAG,"New Logfile Created");
                                        }

                                        writeLineToLog(line, writer);
                                        writer.flush();

                                        countLines +=1;
                                        logNumberOflines(countLines);

                                    }

                                }
                            } catch (IOException e) {
                                Log.e(TAG, "", e);
                            }
                        }
                    }
            ).start();
        super.onCreate();
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service  is running...")
                .setContentTitle("Foreground service enabled")
                .setSmallIcon(R.drawable.ic_launcher_background);

        startForeground(1001, notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String createFileName(){

        String filename = "logcat_" + LocalDate.now() + ".txt";

        File file = new File(getFilesDir(), filename);

        if (file.exists()) {
            filename = "logcat" + LocalDateTime.now() + ".txt";
        }

        return filename;
    }

    public File generateLogFile(){
        return new File(getFilesDir(), createFileName());
    }

    public void logNumberOflines(Integer countLines){
        if(countLines % 100 == 0){
            Log.i(TAG,countLines + " number of lines written");
        }
    }

    private Process startLogcatProcess() throws IOException{
            return Runtime.getRuntime().exec("logcat");
    }

    private boolean isProcessAlive(Process process) {
        return process != null && process.isAlive();
    }

    private boolean isCurrentLogFile(File logFile) {
        return logFile.getName().contains(LocalDate.now().toString());
    }

    private void writeLineToLog(String line, BufferedWriter writer){
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }
    }
    
}
