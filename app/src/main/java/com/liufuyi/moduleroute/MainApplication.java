package com.liufuyi.moduleroute;

import android.app.Application;

import com.liufuyi.route.api.Router;

class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.init(this);
    }
}
