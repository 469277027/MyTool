package com.cly.myimglibrary.builder.interfaces;

import com.facebook.imagepipeline.request.ImageRequest;

/**
 * Created by 丛龙宇 on 2017/1/5.
 */

public interface FrescoBuilderSource extends Builder {

    /**
     * 构建hierarchy
     */
    void buildHierarchy();

    /**
     * 构建controller
     */
    void buildController();

    /**
     * 构建imagerequest
     */
    ImageRequest buildImageRequest();

    /**
     * 运行时动态修改图片状态
     */
    void runtimeBuildHierarchy();


}
