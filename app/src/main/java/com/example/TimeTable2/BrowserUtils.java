package com.example.TimeTable2;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

public class BrowserUtils {
    public static void openUrlInChromeCustomTab(Context context, String url) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            customTabsIntent.launchUrl(context, url.contains("http://") ? Uri.parse(url) : Uri.parse("http://" + url));
        } catch (ActivityNotFoundException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}