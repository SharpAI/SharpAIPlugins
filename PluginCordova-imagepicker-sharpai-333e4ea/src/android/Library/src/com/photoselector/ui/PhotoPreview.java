package com.photoselector.ui;
/**
 * @author Aizaz AZ
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.sharpai.everywhere.R;

import com.photoselector.model.PhotoModel;
import com.polites.GestureImageView;

public class PhotoPreview extends LinearLayout implements OnClickListener {

    private ProgressBar pbLoading;
    private GestureImageView ivContent;
    private OnClickListener l;

    public PhotoPreview(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_photopreview, this, true);

        pbLoading = (ProgressBar) findViewById(R.id.pb_loading_vpp);
        ivContent = (GestureImageView) findViewById(R.id.iv_content_vpp);
        ivContent.setOnClickListener(this);
    }

    public PhotoPreview(Context context, AttributeSet attrs, int defStyle) {
        this(context);
    }

    public PhotoPreview(Context context, AttributeSet attrs) {
        this(context);
    }

    public void loadImage(PhotoModel photoModel) {
//		loadImage("file://" + photoModel.getOriginalPath());

        Glide.with(getContext())
                .load("file://" + photoModel.getOriginalPath())
                .error(R.drawable.ic_picture_loadfailed)//设置错误图片
                .crossFade()
//				.into(ivContent);
                .into(new GlideDrawableImageViewTarget(ivContent) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        //在这里添加一些图片加载完成的操作
                        pbLoading.setVisibility(View.GONE);
                    }
                });

    }
//	private void loadImage(String path) {
//		ImageLoader.getInstance().loadImage(path, new SimpleImageLoadingListener() {
//			@Override
//			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//				ivContent.setImageBitmap(loadedImage);
//				pbLoading.setVisibility(View.GONE);
//			}
//
//			@Override
//			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//				ivContent.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_loadfailed));
//				pbLoading.setVisibility(View.GONE);
//			}
//		});
//	}

        @Override
        public void setOnClickListener (OnClickListener l){
            this.l = l;
        }

        @Override
        public void onClick (View v){
            if (v.getId() == R.id.iv_content_vpp && l != null)
                l.onClick(ivContent);
        }

        ;

    }
