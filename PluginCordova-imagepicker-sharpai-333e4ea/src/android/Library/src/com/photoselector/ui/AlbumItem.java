package com.photoselector.ui;
/**
 *
 * @author Aizaz AZ
 *
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import org.sharpai.everywhere.R;
import com.photoselector.model.AlbumModel;

public class AlbumItem extends LinearLayout {

    private ImageView ivAlbum, ivIndex;
    private TextView tvName, tvCount;

    public AlbumItem(Context context) {
        this(context, null);
    }

    public AlbumItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_album, this, true);

        ivAlbum = (ImageView) findViewById(R.id.iv_album_la);
        ivIndex = (ImageView) findViewById(R.id.iv_index_la);
        tvName = (TextView) findViewById(R.id.tv_name_la);
        tvCount = (TextView) findViewById(R.id.tv_count_la);
    }

    public AlbumItem(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }


    public void setAlbumImage(String path) {
//      ImageLoader.getInstance().displayImage("file://" + path, ivAlbum);
        Glide.with(getContext())
                .load("file://" + path)
                .placeholder(R.drawable.ic_picture_loading) //设置占位图
                .error(R.drawable.ic_picture_loadfailed)
                .into(ivAlbum);//设置错误图片
    }


    public void update(AlbumModel album) {
        setAlbumImage(album.getRecent());
        setName(album.getName());
        setCount(album.getCount());
        isCheck(album.isCheck());
    }

    public void setName(CharSequence title) {
        tvName.setText(title);
    }

    public void setCount(int count) {
        tvCount.setHint(""+count);
    }

    public void isCheck(boolean isCheck) {
        if (isCheck)
            ivIndex.setVisibility(View.VISIBLE);
        else
            ivIndex.setVisibility(View.GONE);
    }

}
