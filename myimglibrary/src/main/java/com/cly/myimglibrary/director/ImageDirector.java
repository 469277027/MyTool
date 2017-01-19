package com.cly.myimglibrary.director;

import android.content.Context;
import android.os.Environment;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 丛龙宇 on 2017/1/6.
 */

public class ImageDirector {

    //硬盘缓存默认文件名
    private static final String DISK_CACHE_NAME = "bitmaps";

    private static Context context;

    //硬盘缓存文件名
    private static String diskCacheName = DISK_CACHE_NAME;
    //内存缓存最大大小
    private static int maxMemoryCache = -1;
    //硬盘缓存最大大小
    private static int maxDiskCacheSize = -1;


    /**
     * 工具初始化
     *
     * @param context Application上下文对象
     */
    public static void init(Context context) {
        init(context, -1, -1, null);
    }

    /**
     * 工具初始化
     *
     * @param context        Application上下文对象
     * @param maxMemoryCache 内存缓存阀值
     */
    public static void init(Context context, int maxMemoryCache) {
        init(context, maxMemoryCache, -1, null);
    }

    /**
     * 工具初始化
     *
     * @param context          Application上下文对象
     * @param maxDiskCacheSize 硬盘缓存阀值
     * @param diskCacheName    硬盘缓存文件名
     */
    public static void init(Context context, int maxDiskCacheSize, String diskCacheName) {
        init(context, -1, maxDiskCacheSize, diskCacheName);
    }

    /**
     * 工具初始化
     *
     * @param context          Application上下文对象
     * @param maxMemoryCache   内存缓存阀值
     * @param maxDiskCacheSize 硬盘缓存阀值
     * @param diskCacheName    硬盘缓存文件名
     */
    public static void init(Context context, int maxMemoryCache, int maxDiskCacheSize, String diskCacheName) {
        ImageDirector.context = context.getApplicationContext();
        ImageDirector.maxMemoryCache = maxMemoryCache;
        ImageDirector.maxDiskCacheSize = maxDiskCacheSize;
        ImageDirector.diskCacheName = diskCacheName;
        initFresco();
    }

    /**
     * 初始化Fresco
     */
    private static void initFresco() {
        Fresco.initialize(context, initImagePipeline());
    }

    /**
     * Fresco硬盘缓存文件路径
     *
     * @return
     */
    private static File DISK_CACHE_PATH() {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            file = context.getExternalCacheDir();
        } else {
            file = context.getCacheDir();
        }

        if (file != null) {
            String path = file.getPath();
        } else {
            file = new File("/");
        }
        return file;
    }

    /**
     * 初始化FrescoImagePipeline
     *
     * @return
     */
    private static ImagePipelineConfig initImagePipeline() {

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryName(diskCacheName)
                .setBaseDirectoryPath(DISK_CACHE_PATH())
                .setMaxCacheSize(maxDiskCacheSize)
                .build();

        Supplier<MemoryCacheParams> supplier = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {

                int MAX_MEM = maxMemoryCache == -1 ? 10 * ByteConstants.MB : maxMemoryCache;

                MemoryCacheParams memoryCacheParams = new MemoryCacheParams(MAX_MEM,
                        Integer.MAX_VALUE,
                        MAX_MEM,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE);
                return memoryCacheParams;
            }
        };


        Set<RequestListener> requestListeners = new HashSet<>();

        ImagePipelineConfig.Builder builder = ImagePipelineConfig.newBuilder(context);

        builder.setRequestListeners(requestListeners);
        if (maxMemoryCache != -1)
            builder.setBitmapMemoryCacheParamsSupplier(supplier);
        if (maxDiskCacheSize != -1)
            builder.setMainDiskCacheConfig(diskCacheConfig);
        builder.setDownsampleEnabled(true);

        ImagePipelineConfig config = builder.build();
        return config;
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        Fresco.getImagePipeline().clearCaches();
    }

}
