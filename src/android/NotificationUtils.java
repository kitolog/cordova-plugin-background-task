package com.applurk.plugin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/*
shit hack
 */
import com.applurk.flashtaxidriver.R;

public class NotificationUtils {

    private static final String TAG = NotificationUtils.class.getSimpleName();

    private static NotificationUtils instance;

    private static Context context;
    private NotificationManager manager; // Системная утилита, упарляющая уведомлениями
    private int lastId = 0; //постоянно увеличивающееся поле, уникальный номер каждого уведомления
    private HashMap<Integer, Notification> notifications; //массив ключ-значение на все отображаемые пользователю уведомления


    //приватный контструктор для Singleton
    private NotificationUtils(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifications = new HashMap<Integer, Notification>();
    }

    /**
     * Получение ссылки на синглтон
     */
    public static NotificationUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationUtils(context);
        } else {
            instance.context = context;
        }
        return instance;
    }

    public int createInfoNotification(String title, String message) {

        Intent notificationIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

//        Intent notificationIntent = new Intent(context, intentClass); // по клику на уведомлении откроется HomeActivity
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon) //иконка уведомления
                .setAutoCancel(true) //уведомление закроется по клику на него
                .setTicker(message) //текст, который отобразится вверху статус-бара при создании уведомления
                .setContentText(message) // Основной текст уведомления
                .setContentIntent(PendingIntent.getActivity(context, -1, notificationIntent, PendingIntent.FLAG_ONE_SHOT))
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle(title) //заголовок уведомления
                .setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию


//        09-18 15:20:10.501    1375-1505/com.applurk.flashtaxidriver:remote I/AL:PollingTask﹕ --------
//                09-18 15:20:10.509      603-603/system_process E/NotificationService﹕ Not posting notification with icon==0: Notification(pri=0 contentView=com.applurk.flashtaxidriver/0x1090064 vibrate=default sound=default defaults=0xffffffff flags=0x11 kind=[null])
//        09-18 15:20:10.509      603-603/system_process E/NotificationService﹕ WARNING: In a future release this will crash the app: com.applurk.flashtaxidriver

        Notification notification = nb.build(); //генерируем уведомление
        manager.notify(lastId, notification); // отображаем его пользователю.
        notifications.put(lastId, notification); //теперь мы можем обращаться к нему по id
        lastId = lastId++;

        return lastId;
    }

    public int createOrderNotification(String addressFrom) {
        String title = "Новый заказ";
        return createInfoNotification(title, addressFrom);
    }
}