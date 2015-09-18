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

        String appNameString = this.context.getResources().getString(R.string.app_name);

        Intent notificationIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

//        Intent notificationIntent = new Intent(context, intentClass); // по клику на уведомлении откроется HomeActivity
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.tray-icon) //иконка уведомления
                .setAutoCancel(true) //уведомление закроется по клику на него
                .setTicker(message) //текст, который отобразится вверху статус-бара при создании уведомления
                .setContentText(message) // Основной текст уведомления
                .setContentIntent(PendingIntent.getActivity(context, -1, notificationIntent, PendingIntent.FLAG_ONE_SHOT))
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle(title) //заголовок уведомления
                .setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию

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