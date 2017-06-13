package router.com.ssevening.www.moubase;

import android.content.pm.ResolveInfo;
import android.support.v4.util.LruCache;

/**
 * Created by Pan on 2017/6/13.
 */

public class CacheUtils {
    public static CacheUtils cacheUtils;
    private LruCache lruCache;

    public static CacheUtils getInstance() {
        if (cacheUtils == null) {
            cacheUtils = new CacheUtils();
        }
        return cacheUtils;
    }

    private CacheUtils() {
        lruCache = new LruCache<>(20);
    }


    public LruCache getLruCache() {
        return lruCache;
    }
}
