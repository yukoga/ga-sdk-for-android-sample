package com.google.samples.quickstart.analytics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.analytics.CampaignTrackingReceiver;
//import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.google.android.gms.analytics.CampaignTrackingService;

import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.net.URLDecoder.*;

public class ReferrerReceiver extends BroadcastReceiver {
    public ReferrerReceiver() {
    }

    private Tracker mTracker;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String referrer = extras.getString("referrer");

        try {
            Map<String, String> params = getHashMapFromReferrer(referrer);
            for (String key : params.keySet()) {
                Log.i("REFERRER", "key is : " + key + " and value is : " + params.get(key));
            }
        } catch (UnsupportedEncodingException e) {
            Log.e("Error on referrer:", e.getMessage());
        } finally {
            Log.i("REFERRER", "Finish capture install referrer.");
            CampaignTrackingReceiver receiver = new CampaignTrackingReceiver();
            receiver.onReceive(context, intent);

            AnalyticsApplication application = (AnalyticsApplication) context.getApplicationContext();
            mTracker = application.getDefaultTracker();
            mTracker.send(new HitBuilders.EventBuilder()
                .setCampaignParamsFromUrl(referrer)
                .setCategory("Campaign Measurement")
                .setAction("measure source and medium")
                .setLabel("test")
                .build()
            );
        }
    }

    public Map<String, String> getHashMapFromReferrer(String referrer) throws UnsupportedEncodingException {
        Map<String, String> params = new LinkedHashMap<String, String>();
        String[] queryPairs = referrer.split("%26", 0);
        for (String queryParam : queryPairs) {
            int index = queryParam.indexOf("%3D");
            params.put(decode(queryParam.substring(0, index), "UTF-8"),
                    decode(queryParam.substring(index+3), "UTF-8"));
        }

        return params;
    }
}
