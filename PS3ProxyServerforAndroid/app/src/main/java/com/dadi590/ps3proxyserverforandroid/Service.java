package com.dadi590.ps3proxyserverforandroid;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.util.Date;

import ps3_proxy.Ps3_proxy;

public class Service extends android.app.Service {

    private static final String CHANNEL_ID_1 = "NOTIFICATIONS_com.dadi590.ps3proxyserverforandroid.PPSFA_server";
    private static final String CHANNEL_ID_2 = "NOTIFICATIONS_com.dadi590.ps3proxyserverforandroid.PPSFA_restart_server_request";

    public static final int id_server_notification = 3234_1;
    public static final int id_restart_server_notification_request = 3234_2;

    private String ip;
    private String port;
    private String custom_ps3_updatelist;
    private String manual_ps3_updatelist;
    private String path_ps3_updatelist;
    private String custom_rules_urls;
    private String custom_rules_urls_uncompleted;
    private String whats_new_psx_place_news;
    private String whats_new_rss_feed;
    private String whats_new_rss_feed_url;
    private String ps_store_psx_place_news;
    private String ps_store_rss_feed;
    private String ps_store_rss_feed_url;
    private String tv_video_services_psx_place_news;
    private String tv_video_services_rss_feed;
    private String tv_video_services_rss_feed_url;
    private String whats_new_redirect;
    private String whats_new_redirect_url;
    private String whats_new_redirect_url_ps3_like;
    private String ps_store_redirect;
    private String ps_store_redirect_url;
    private String ps_store_redirect_url_ps3_like;
    private String tv_video_services_redirect;
    private String tv_video_services_redirect_url;
    private String tv_video_services_redirect_url_ps3_like;
    private String psx_place_xml_url;
    private String see_requests;
    private String search_in_logs;
    private Bundle extras;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private NotificationManager notificationManager;
    private Intent notificationIntent;
    private int auto_start_boot=0;

    private int session_id = 0;
    public String log_string="";
    private long startTime;
    private int log_num=0;
    private String last_detected_ip="";
    private Handler handler = new Handler();
    private Handler handler_1 = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Probably this is if the system restarts the server automatically after it's shutdown by memory management
        int auto_restart_system=0;
        try {
            extras = intent.getExtras();
        } catch (NullPointerException e) {
            auto_restart_system=1;
        }
        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /*editor.putString("settings_log_string_busy", "false");
        editor.putString("settings_log_string", "");
        editor.apply();*/

        if (auto_restart_system==0) {
            if (intent.hasExtra("extras_reload_ip")) {
                editor.putString("settings_ip", Main.getLocalIpAddress());
                editor.apply();
            }
            if (intent.hasExtra("extras_auto_start_boot")) {
                if (settings.getString("settings_auto_start_boot", "false").equals("false")) {
                    System.exit(0);
                } else {
                    auto_start_boot=1;
                    if (settings.getString("settings_auto_save_ip", "false").equals("false")) {
                        editor.putString("settings_ip", Main.getLocalIpAddress());
                        editor.apply();
                    }
                }
            }
        }

        /*String detected_ip = Main.getLocalIpAddress();
        if (!settings.getString("settings_ip", "N/A").equals(detected_ip)) {
            editor.putString("settings_ip", detected_ip);
            editor.apply();
        }*/
        ip = settings.getString("settings_ip", Main.getLocalIpAddress());
        port=settings.getString("settings_port", "8080");
        custom_ps3_updatelist=settings.getString("settings_custom_ps3_updatelist", "true");
        path_ps3_updatelist=settings.getString("settings_path_ps3_updatelist", "NONE");
        manual_ps3_updatelist=settings.getString("settings_manual_ps3_updatelist", "false");
        custom_rules_urls_uncompleted=settings.getString("settings_custom_rules_urls","");
        whats_new_psx_place_news=settings.getString("settings_whats_new_psx_place_news","false");
        whats_new_rss_feed=settings.getString("settings_whats_new_rss_feed","false");
        whats_new_rss_feed_url=settings.getString("settings_whats_new_rss_feed_url","");
        ps_store_psx_place_news=settings.getString("settings_ps_store_psx_place_news","false");
        ps_store_rss_feed=settings.getString("settings_ps_store_rss_feed","false");
        ps_store_rss_feed_url=settings.getString("settings_ps_store_rss_feed_url","");
        tv_video_services_psx_place_news=settings.getString("settings_tv_video_services_psx_place_news","false");
        tv_video_services_rss_feed=settings.getString("settings_tv_video_services_rss_feed","false");
        tv_video_services_rss_feed_url=settings.getString("settings_tv_video_services_rss_feed_url","");
        psx_place_xml_url=settings.getString("settings_psx_place_xml_url","");
        String[] custom_rules_urls_uncompleted_list = custom_rules_urls_uncompleted.split("\n");
        custom_rules_urls="";

        for (int i=0;i<custom_rules_urls_uncompleted_list.length;i++) {
            if (custom_rules_urls_uncompleted_list[i].contains("http") && custom_rules_urls_uncompleted_list[i].contains(" --> /")) {
                if (!custom_rules_urls_uncompleted_list[i].substring(0, 4).equals("http")) {
                    custom_rules_urls += "http://";
                }
                if (i != custom_rules_urls_uncompleted_list.length - 1) {
                    custom_rules_urls += custom_rules_urls_uncompleted_list[i] + " \\\\// ";
                } else {
                    custom_rules_urls += custom_rules_urls_uncompleted_list[i];
                }
            }
        }
        if (whats_new_psx_place_news.equals("true") || whats_new_rss_feed.equals("true")) {
            whats_new_redirect="true";
            if (whats_new_psx_place_news.equals("true")) {
                whats_new_redirect_url=psx_place_xml_url;
                whats_new_redirect_url_ps3_like="true";
            } else {
                whats_new_redirect_url=whats_new_rss_feed_url;
                whats_new_redirect_url_ps3_like="false";
            }
        } else {
            whats_new_redirect="false";
        }
        if (ps_store_psx_place_news.equals("true") || ps_store_rss_feed.equals("true")) {
            ps_store_redirect="true";
            if (ps_store_psx_place_news.equals("true")) {
                ps_store_redirect_url=psx_place_xml_url;
                ps_store_redirect_url_ps3_like="true";
            } else {
                ps_store_redirect_url=ps_store_rss_feed_url;
                ps_store_redirect_url_ps3_like="false";
            }
        } else {
            ps_store_redirect="false";
        }
        if (tv_video_services_psx_place_news.equals("true") || tv_video_services_rss_feed.equals("true")) {
            tv_video_services_redirect="true";
            if (tv_video_services_psx_place_news.equals("true")) {
                tv_video_services_redirect_url=psx_place_xml_url;
                tv_video_services_redirect_url_ps3_like="true";
            } else {
                tv_video_services_redirect_url=tv_video_services_rss_feed_url;
                tv_video_services_redirect_url_ps3_like="false";
            }
        } else {
            tv_video_services_redirect="false";
        }

        notificationIntent = new Intent(Service.this, Main.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(Service.this,
                0, notificationIntent, 0);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Notification notification = new NotificationCompat.Builder(Service.this, createNotificationChannel(CHANNEL_ID_1))
                    .setContentTitle("PS3 Proxy Server - " + getString(R.string.active))
                    .setContentText("IP: " + ip + " | " + getString(R.string.port) + port)
                    .setSmallIcon(R.drawable.main_old_icon_without_borders)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(id_server_notification, notification);
        } else {
            Notification notification = new NotificationCompat.Builder(Service.this, createNotificationChannel(CHANNEL_ID_1))
                    .setContentTitle("PS3 Proxy Server - " + getString(R.string.active))
                    .setContentText("IP: " + ip + " | " + getString(R.string.port) + port)
                    .setSmallIcon(R.drawable.main_new_icon)
                    .setLargeIcon(BitmapFactory.decodeResource( getResources(), R.drawable.main_old_icon_without_borders))
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(id_server_notification, notification);
        }

        session_id = 10000 + (int)(Math.random() * ((999999999 - 10000) + 1));
        editor.putString("settings_session_id", String.valueOf(session_id));
        editor.apply();
        Server.start();
        //IP_logs_Checker.start_server();
        //Logs_checker.start_server();
        //handler.postDelayed(Logs_checker, 5000);
        handler.postDelayed(IP_Checker, 2500);

        //do heavy work on a background thread
        //stopSelf();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Server.interrupt();
        Server=null;
        notificationManager.cancel(Service.id_restart_server_notification_request);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Thread Server = new Thread(new Runnable() {
        @Override
        public void run() {
            String address = ip+":"+port;
            String error = Ps3_proxy.server(address+"|"+manual_ps3_updatelist+"|"+path_ps3_updatelist+"|"+custom_ps3_updatelist+"|"+custom_rules_urls+"|"+whats_new_redirect+"|"+whats_new_redirect_url+"|"+whats_new_redirect_url_ps3_like+"|"+ps_store_redirect+"|"+ps_store_redirect_url+"|"+ps_store_redirect_url_ps3_like+"|"+tv_video_services_redirect+"|"+tv_video_services_redirect_url+"|"+tv_video_services_redirect_url_ps3_like+"|"+session_id);
            if (!error.equals("3234")) {
                System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDD");
                System.out.println(error);
                System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDD");
            }

            Intent mStartActivity = new Intent(Service.this, Main.class);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mStartActivity.putExtra("extras_wrong_ip",ip);
            int mPendingIntentId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            PendingIntent mPendingIntent = PendingIntent.getActivity(Service.this, mPendingIntentId, mStartActivity, 0);
            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2500, mPendingIntent);
            Intent stopMyService = new Intent(Service.this, Main.class);
            stopMyService.setClass(Service.this, Service.class);
            stopService(stopMyService);
            //System.exit(0); - Not necessary because the server really stopped (invalid_port)
        }
    });

    /*Thread IP_Logs_Checker_old = new Thread(new Runnable() {
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() == startTime + 5000) {
                    String new_ip = Main.getLocalIpAddress();
                    if (!new_ip.equals(ip)) {
                        if (auto_start_boot==0) {
                            if (settings.getString("settings_auto_save_ip","false").equals("false")) {
                                if (!new_ip.equals(last_detected_ip)) {
                                    last_detected_ip = new_ip;
                                    notificationIntent = new Intent(Service.this, Restarter_Service.class);
                                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(Service.this,
                                            0, notificationIntent, 0);

                                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(Service.this, createNotificationChannel(CHANNEL_ID_2))
                                                .setContentTitle("PS3 Proxy Server - IP changed")
                                                .setContentText(getString(R.string.notification_restart_service_request_title))
                                                .setSmallIcon(R.drawable.main_old_icon_without_borders)
                                                .setContentIntent(pendingIntent)
                                                .setAutoCancel(true);
                                        Notification notification = builder.build();
                                        notificationManager.notify(id_restart_server_notification_request, notification);
                                    } else {
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(Service.this, createNotificationChannel(CHANNEL_ID_2))
                                                .setContentTitle("PS3 Proxy Server - IP changed")
                                                .setContentText(getString(R.string.notification_restart_service_request_content))
                                                .setSmallIcon(R.drawable.main_new_icon)
                                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.main_old_icon_without_borders))
                                                .setContentIntent(pendingIntent)
                                                .setAutoCancel(true);
                                        Notification notification = builder.build();
                                        notificationManager.notify(id_restart_server_notification_request, notification);
                                    }
                                }
                            }
                        } else {
                            if (settings.getString("settings_auto_save_ip", "false").equals("false")) {
                                Intent Restarter_Service = new Intent(Service.this, Restarter_Service.class);
                                Restarter_Service.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Restarter_Service.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                Restarter_Service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Restarter_Service);
                                System.exit(0);
                            }
                        }
                    }

                    try {
                        Process process = Runtime.getRuntime().exec("logcat -d");
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));

                        see_requests=settings.getString("settings_see_requests","false");
                        if (see_requests.equals("true")) {
                            search_in_logs="PPSFA > ";
                        } else {
                            search_in_logs="PPSFA - ";
                        }
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.contains("PPSFA ")) {
                                if (line.contains("SI:"+session_id+" ")) {
                                    if (line.contains("LN:" + log_num + " ")) {
                                        if (line.contains("PPSFA - ") || line.contains(search_in_logs)) {
                                            editor.putString("settings_log_string_busy", "true");
                                            editor.apply();
                                            log_string+=(line.substring(line.indexOf("P") + 8)+"\n\n");
                                            editor.putString("settings_log_string", log_string);
                                            editor.putString("settings_log_string_busy", "false");
                                            editor.apply();
                                        }
                                        log_num += 1;
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                    }


                    startTime = System.currentTimeMillis();
                }
            }
        }
    });*/

    Runnable IP_Checker = new Runnable() {
        @Override
        public void run() {
            String new_ip = Main.getLocalIpAddress();
            if (!new_ip.equals(ip)) {
                if (auto_start_boot==0) {
                    if (settings.getString("settings_auto_save_ip","false").equals("false")) {
                        if (!new_ip.equals(last_detected_ip)) {
                            last_detected_ip = new_ip;
                            notificationIntent = new Intent(Service.this, Restarter_Service.class);
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(Service.this,
                                    0, notificationIntent, 0);

                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(Service.this, createNotificationChannel(CHANNEL_ID_2))
                                        .setContentTitle("PS3 Proxy Server - IP changed")
                                        .setContentText(getString(R.string.notification_restart_service_request_title))
                                        .setSmallIcon(R.drawable.main_old_icon_without_borders)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                Notification notification = builder.build();
                                notificationManager.notify(id_restart_server_notification_request, notification);
                            } else {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(Service.this, createNotificationChannel(CHANNEL_ID_2))
                                        .setContentTitle("PS3 Proxy Server - IP changed")
                                        .setContentText(getString(R.string.notification_restart_service_request_content))
                                        .setSmallIcon(R.drawable.main_new_icon)
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.main_old_icon_without_borders))
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                Notification notification = builder.build();
                                notificationManager.notify(id_restart_server_notification_request, notification);
                            }
                        }
                    }
                } else {
                    if (settings.getString("settings_auto_save_ip", "false").equals("false")) {
                        Intent Restarter_Service = new Intent(Service.this, Restarter_Service.class);
                        Restarter_Service.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Restarter_Service.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        Restarter_Service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Restarter_Service);
                        System.exit(0);
                    }
                }
            }


            /*try {
                Process process = Runtime.getRuntime().exec("logcat -d");
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                see_requests=settings.getString("settings_see_requests","false");
                if (see_requests.equals("true")) {
                    search_in_logs="PPSFA > ";
                } else {
                    search_in_logs="PPSFA - ";
                }
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("PPSFA ")) {
                        if (line.contains("SI:"+session_id+" ")) {
                            if (line.contains("LN:" + log_num + " ")) {
                                if (line.contains("PPSFA - ") || line.contains(search_in_logs)) {
                                    editor.apply();
                                    log_string+=(line.substring(line.indexOf("P") + 8)+"\n\n");
                                    editor.putString("settings_log_string", log_string);
                                    editor.apply();
                                }
                                log_num += 1;
                            }
                        }
                    }
                }
            } catch (IOException e) {
            }*/

            handler.postDelayed(this, 2500);
        }
    };

    //Along with the Logs_printer in Real_time_logging, would put the phone REALLY hot, so they're both disabled and
    // manual check is required.
    /*Thread Logs_checker = new Thread() {
        public void run() {
            startTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() == startTime + 5000) {
                    try {
                        Process process = Runtime.getRuntime().exec("logcat -d");
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));

                        see_requests=settings.getString("settings_see_requests","false");
                        if (see_requests.equals("true")) {
                            search_in_logs="PPSFA > ";
                        } else {
                            search_in_logs="PPSFA - ";
                        }
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.contains("PPSFA ")) {
                                if (line.contains("SI:"+session_id+" ")) {
                                    if (line.contains("LN:" + log_num + " ")) {
                                        if (line.contains("PPSFA - ") || line.contains(search_in_logs)) {
                                            editor.putString("settings_log_string_busy", "true");
                                            editor.apply();
                                            log_string+=(line.substring(line.indexOf("P") + 8) + "\n\n");
                                            editor.putString("settings_log_string", log_string);
                                            editor.putString("settings_log_string_busy", "false");
                                            editor.apply();
                                        }
                                        log_num += 1;
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                    }
                    startTime = System.currentTimeMillis();
                }
            }
        }
    };*/

    private String createNotificationChannel(String channel_id) {

        //Copied from developer.android.com
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.service_name);
            String description = getString(R.string.service_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
        return channel_id;
    }
}
