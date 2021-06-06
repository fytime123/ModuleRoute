package com.liufuyi.route.api;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dalvik.system.DexFile;

public class Router {
    /**
     * 为了保持在应用中只有一个路由表，我们需要声明为单例
     */
    private static final Router ourInstance=new Router();
    public static Router getInstance(){
        return ourInstance;
    }
    private Router(){

    }
    //自动生成的类所在的包名
    public final static   String packageName="com.liufuyi.route.output";

    public static void init(Application application){
        //获取到com.liufuyi.route.output所有的类
        final Set<String> classNames=new HashSet<>();


        try {
            List<String> paths=getSourcePaths(application);
            for(final String path:paths){
                DexFile dexfile=new DexFile(path);
                //获取dexfilei里面所有的class
                Enumeration<String> dexEntries=dexfile.entries();
                while (dexEntries.hasMoreElements()){
                    String className=dexEntries.nextElement();
                    //挑选出我们预定义的class
                    if(className.startsWith(packageName)){
                        classNames.add(className);
                    }

                }

            }
            for (String className : classNames) {
                Class<?> aClass = Class.forName(className);
                //判断是否有实现定义的接口
                if (RouterLoad.class.isAssignableFrom(aClass)){
                    RouterLoad load= (RouterLoad) aClass.newInstance();
                    Log.i("liufuyi",aClass.getName());
                    load.load();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    /**
     * 获取当前apk下的所有包路径
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    private static List<String>getSourcePaths(Context context) throws PackageManager.NameNotFoundException {
        ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        List<String> sourcePaths=new ArrayList<>();
        //当前apk存储path
        sourcePaths.add(applicationInfo.sourceDir);
        if(applicationInfo.splitPublicSourceDirs!=null){
            sourcePaths.addAll(Arrays.asList(applicationInfo.splitPublicSourceDirs));
            Log.i("hello",applicationInfo.splitPublicSourceDirs.toString());
        }
        return sourcePaths;
    }

    //路由表
    private static Map<String,Class<? extends Activity>>routers=new HashMap<>();

    public void register(String path,String cls) {
        Log.i("liufuyi","register");
        try {
            Class<?> aClass = Class.forName(cls);
            routers.put(path, (Class<? extends Activity>) aClass);
            Log.i("liufuyi",aClass.getName());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void startActivity(Activity activity,String path){

        Class<? extends Activity> aClass = routers.get(path);
        if(aClass!=null){
            activity.startActivity(new Intent(activity,aClass));
        }

    }


}