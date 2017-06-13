package router.com.ssevening.www.moubase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by Pan on 2017/6/12.
 */

public class ReflectNav {

    private String TAG = "ReflectNav";

    static Activity activity;
    Intent intent;
    int resultCode;

    long startTime;

    public static ReflectNav from(Activity activity) {
        return new ReflectNav(activity);
    }

    public ReflectNav(Activity activity) {
        startTime = System.currentTimeMillis();
        this.activity = activity;

    }

    public ReflectNav setClass(String classPath) {
        Class c;
        if (CacheUtils.getInstance().getLruCache().get(classPath) != null) {
            c = (Class) CacheUtils.getInstance().getLruCache().get(classPath);
            intent = new Intent(activity, c);
        } else {
            try {
                c = Class.forName(classPath);
                CacheUtils.getInstance().getLruCache().put(classPath, c);
                intent = new Intent(activity, c);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public ReflectNav withExtras(Bundle bundle) {
        intent.putExtras(bundle);
        return this;
    }

    public ReflectNav withCategory(String category) {
        intent.addCategory(category);
        return this;
    }

    public ReflectNav withFlags(final int flags) {
        intent.addFlags(flags);
        return this;
    }


    public ReflectNav withAction(String action) {
        intent.setAction(action);
        return this;
    }

    public ReflectNav forResult(int resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public void fire() {
        Log.d(TAG, "time:" + (System.currentTimeMillis() - startTime));
        if (resultCode != 0) {
            activity.startActivity(intent);
        } else {
            activity.startActivityForResult(intent, resultCode);
        }

    }


}
