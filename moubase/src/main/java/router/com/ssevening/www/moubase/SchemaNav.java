package router.com.ssevening.www.moubase;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by Pan on 2017/6/13.
 */

public class SchemaNav {

    static Context context;
    private long startTime;
    Intent intent;
    int resultCode;
    private String TAG = "SchemaNav";


    public static SchemaNav from(Activity activity) {
        return new SchemaNav(activity);
    }

    public SchemaNav(Context context) {
        this.context = context;
        startTime = System.currentTimeMillis();
        // 创建一个ActionView的Action
        intent = new Intent(Intent.ACTION_VIEW);


    }

    public SchemaNav withExtras(Bundle bundle) {
        intent.putExtras(bundle);
        return this;
    }

    public SchemaNav withCategory(String category) {
        intent.addCategory(category);
        return this;
    }

    public SchemaNav withFlags(final int flags) {
        intent.addFlags(flags);
        return this;
    }

    public SchemaNav forResult(int resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    /**
     * 发射
     *
     * @param url
     * @return
     */
    public boolean fire(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        Uri uri = Uri.parse(url);
        // 给intent设置好要去的URL
        intent.setData(uri);
        // 查询可以处理此URL的Activity
        final ResolveInfo info;
        // 增加cache，加快速度
        if (CacheUtils.getInstance().getLruCache().get(url) != null) {
            info = (ResolveInfo) CacheUtils.getInstance().getLruCache().get(url);
            Log.d(TAG, "cache hit");
        } else {
            final List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            info = (list != null && list.size() >= 1) ? list.get(0) : null;
            if (info == null) {
                throw new ActivityNotFoundException("No Activity found to handle " + intent);
            }
            CacheUtils.getInstance().getLruCache().put(url, info);
            Log.d(TAG, "cache not hit");
        }

        // 设置好类名
        intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
        Log.d(TAG, "time:" + (System.currentTimeMillis() - startTime));
        // 下面就准备发射了
        if (resultCode >= 0) {
            ((Activity) context).startActivityForResult(intent, resultCode);
        } else {
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                context.startActivity(intent);
            }
        }

        return true;
    }


}
