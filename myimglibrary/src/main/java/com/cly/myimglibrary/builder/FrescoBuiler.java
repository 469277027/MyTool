package com.cly.myimglibrary.builder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.cly.myimglibrary.builder.fresco.FrescoCircle;
import com.cly.myimglibrary.builder.interfaces.Builder;
import com.cly.myimglibrary.builder.interfaces.FrescoBuilderSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by 丛龙宇 on 2017/1/6.
 */

public class FrescoBuiler<T> implements FrescoBuilderSource {

    //获取全局Context
    private final Context context;
    //获取需要构造的SimpleDraweeView
    private SimpleDraweeView simpleDraweeView;
    //需要加载的uri
    private String imageUri;

    //是否使用ImageRequest
    private boolean isUseImageRequest = false;

    /*设置hierarchy所需要的变量*/

    //设置要加载的图的缩放类型
    private ScalingUtils.ScaleType actualImageScaleType;
    //设置占位图的缩放类型
    private ScalingUtils.ScaleType placeHolderImageScaleType;
    //设置加载失败图的缩放类型
    private ScalingUtils.ScaleType failureImageScaleType;
    //设置进度条的缩放类型
    private ScalingUtils.ScaleType progressScaleType;


    //通过Drawable设置占位图
    private Drawable placeHolderDrawable;
    //通过Drawable设置加载失败图
    private Drawable failureDrawable;

    //通过资源文件id设置占位图
    private int placeHolderImage = -1;
    //通过资源文件id设置加载失败图
    private int failureImage = -1;

    //焦点缩放焦点横坐标
    private int pointX = -1;
    //焦点缩放焦点纵坐标
    private int pointY = -1;

    //圆角左上弧度
    private float cornersRadiusTopLeft = 0f;
    //圆角左下弧度
    private float cornersRadiusBottomLeft = 0f;
    //圆角右上弧度
    private float cornersRadiusTopRight = 0f;
    //圆角右下弧度
    private float cornersRadiusBottomRight = 0f;

    //是否设置圆圈
    private boolean roundAsCircle = false;

    //图片边框颜色
    private int borderColor = -1;
    //图片边框宽度
    private float borderWidth = 0;

    //是否展示进度条
    private boolean isShowProgressBar = false;
    //进度条Drawable
    private Drawable progressDrawable;
    //进度条资源文件id
    private int progressResId = -1;

    /*设置controller所需要的变量*/

    //最低请求级别
    private ImageRequest.RequestLevel requestLevel;

    /*设置request所需要的变量*/

    //是否点击重新加载
    private boolean isTapToRetryEnabled = false;
    //是否加载缩略图
    private boolean isLocalThumbnailPreviewsEnabled = false;
    //是否开启渐进式加载
    private boolean isProgressiveRenderingEnabled = false;

    public FrescoBuiler(Context context, SimpleDraweeView simpleDraweeView, String uri, T t) {
        this.simpleDraweeView = simpleDraweeView;
        this.context = context.getApplicationContext();
        this.imageUri = uri;

        if (t instanceof FrescoCircle) {
            FrescoCircle circle = (FrescoCircle) t;
            roundAsCircle = true;

            borderColor = Color.parseColor(circle.getBorderColor());
            borderWidth = circle.getBorderWidth();
        }

    }

    @Override
    public void buildHierarchy() {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        if (actualImageScaleType != null) {
            if (actualImageScaleType.equals(ScalingUtils.ScaleType.FOCUS_CROP)) {
                if (pointX == -1 && pointY == -1) {
                    throw new IllegalStateException("If you want to use the FOCUS_CROP,please set pointX or pointY first!");
                }
            } else {
                builder.setActualImageScaleType(actualImageScaleType);
            }
        }
        if (placeHolderImageScaleType != null) {
            builder.setPlaceholderImageScaleType(placeHolderImageScaleType);
        }
        if (failureImageScaleType != null) {
            builder.setFailureImageScaleType(failureImageScaleType);
        }
        if (placeHolderDrawable != null) {
            builder.setPlaceholderImage(placeHolderDrawable);
        }
        if (failureDrawable != null) {
            builder.setFailureImage(failureDrawable);
        }
        if (placeHolderImage != -1) {
            builder.setPlaceholderImage(placeHolderImage);
        }
        if (failureImage != -1) {
            builder.setFailureImage(failureImage);
        }
        GenericDraweeHierarchy hierarchy = builder.build();
        if (actualImageScaleType.equals(ScalingUtils.ScaleType.FOCUS_CROP)) {
            hierarchy.setActualImageFocusPoint(new PointF(new Point(pointX, pointY)));
        }
        if (roundAsCircle) {
            RoundingParams roundingParams = new RoundingParams();
            roundingParams.setBorder(borderColor, borderWidth);
            roundingParams.setRoundAsCircle(roundAsCircle);
            hierarchy.setRoundingParams(roundingParams);
        }

        if (isShowProgressBar) {
            if (progressDrawable != null) {
                if (progressScaleType == null)
                    hierarchy.setProgressBarImage(progressDrawable);
                else
                    hierarchy.setProgressBarImage(progressDrawable, progressScaleType);
            } else if (progressResId != -1) {
                if (progressScaleType == null)
                    hierarchy.setProgressBarImage(progressResId);
                else
                    hierarchy.setProgressBarImage(progressResId, progressScaleType);
            } else {
                hierarchy.setProgressBarImage(new ProgressBarDrawable());
            }
        }
        if (!roundAsCircle) {
            RoundingParams roundingParams = RoundingParams.fromCornersRadii(cornersRadiusTopLeft, cornersRadiusTopRight, cornersRadiusBottomRight, cornersRadiusBottomLeft);
            hierarchy.setRoundingParams(roundingParams);
        }

        if (simpleDraweeView != null) {
            buildController();
            simpleDraweeView.setHierarchy(hierarchy);
        }
    }

    @Override
    public void buildController() {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();

        if (isUseImageRequest) {
            builder.setImageRequest(buildImageRequest());
        } else {
            if (imageUri != null) {
                builder.setUri(imageUri);
            } else {
                throw new NullPointerException("please give me a view");
            }
        }


        builder.setTapToRetryEnabled(isTapToRetryEnabled);

        simpleDraweeView.setController(builder.build());
    }

    @Override
    public ImageRequest buildImageRequest() {
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUri));
        if (requestLevel != null) {
            builder.setLowestPermittedRequestLevel(requestLevel);
        }
        builder.setLocalThumbnailPreviewsEnabled(isLocalThumbnailPreviewsEnabled);
        builder.setProgressiveRenderingEnabled(isProgressiveRenderingEnabled);


        return builder.build();
    }

    @Override
    public void runtimeBuildHierarchy() {
        if (simpleDraweeView != null) {
            GenericDraweeHierarchy hierarchy = simpleDraweeView.getHierarchy();
//            if (roundAsCircle){
//                hierarchy.setRo
//            }
        }
    }

    @Override
    public Builder build() {
        buildHierarchy();
        return null;
    }
}
