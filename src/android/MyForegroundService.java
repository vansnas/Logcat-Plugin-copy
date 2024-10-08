package org.apache.cordova.logcat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
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
import java.time.format.DateTimeFormatter;

public class MyForegroundService extends Service {

    private static final String TAG = "Settings and Support App";
    private static final String CHANNELID = "Foreground Service ID";
    private Process process = null;
    private BufferedReader reader = null;
    private File logFile = null;
    private BufferedWriter writer = null;
    private volatile boolean isRunning = true;

    @Override
    public void onCreate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRunning) {

                        if (reader == null || !isProcessAlive(process)) {
                            process = startLogcatProcess();
                            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        }

                        logFile = generateLogFile();
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
                            readLinesFromLog();
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error starting logcat process", e);
                } finally {
                    cleanupResources();
                }
            }
        }).start();
        super.onCreate();
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int appIconResId = applicationInfo.icon;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNELID,
                    "Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is running")
                .setContentTitle("Settings and Support App")
                .setSmallIcon(appIconResId);

        startForeground(1001, notification.build());

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        cleanupResources();
        super.onDestroy();
    }

    private void cleanupResources() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to close reader", e);
        }

        if (process != null) {
            process.destroy();
        }
    }

    public String createFileName() throws IOException {
        String filename = "logcat_" + LocalDate.now() + ".txt";
        File file = new File(getFilesDir(), filename);

        if (file.exists()) {
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmm");
            String formattedDateTime = dateTime.format(formatter);
            filename = "logcat_" + formattedDateTime + ".txt";
        }
        return filename;
    }

    private boolean isCurrentLogFile(File logFile) {
        return logFile.getName().contains(LocalDate.now().toString());
    }

    public File generateLogFile() throws IOException {
        String filename = createFileName();
        Log.i(TAG, "New Logfile Created: " + filename);
        return new File(getFilesDir(), filename);
    }

    public void logNumberOflines(Integer countLines) {
        if (countLines % 100 == 0) {
            Log.i(TAG, countLines + " number of lines written");
        }
    }

    private Process startLogcatProcess() throws IOException {
        return Runtime.getRuntime().exec("logcat");
    }

    private boolean isProcessAlive(Process process) {
        return process != null && process.isAlive();
    }

    private void readLinesFromLog() throws IOException {
        String line;
        int countLines = 0;

        try {
            while ((line = reader.readLine()) != null) {
                if (!isCurrentLogFile(logFile)) {
                    writer.close();
                    logFile = generateLogFile();
                    writer = new BufferedWriter(new FileWriter(logFile));
                    Log.i(TAG, "New Logfile Created");
                }

                writer.write(line);
                writer.newLine();
                writer.flush();

                countLines++;
                logNumberOflines(countLines);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to read logcat or write to file", e);
        }
    }
}
