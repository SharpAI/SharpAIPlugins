package com.photoselector.ui;
/**
 * @author Aizaz AZ
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



import org.sharpai.everywhere.R;

import com.photoselector.domain.PhotoSelectorDomain;
import com.photoselector.model.AlbumModel;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoItem.onItemClickListener;
import com.photoselector.ui.PhotoItem.onPhotoItemCheckedListener;
import com.photoselector.util.AnimationUtil;
import com.photoselector.util.CommonUtils;

/**
 * @author Aizaz AZ
 */
public class PhotoSelectorActivity extends Activity implements
        onItemClickListener, onPhotoItemCheckedListener, OnItemClickListener,
        OnClickListener {

    public static final int SINGLE_IMAGE = 1;
    public static final String KEY_MAX = "MAX_IMAGES";
    public static final String WIDTH_KEY = "WIDTH";
    public static final String HEIGHT_KEY = "HEIGHT";
    public static final String QUALITY_KEY = "QUALITY";
    private int MAX_IMAGE;

    public static final int REQUEST_PHOTO = 0;
    private static final int REQUEST_CAMERA = 1;

    public static String RECCENT_PHOTO = null;

    private GridView gvPhotos;
    private ListView lvAblum;
    private Button btnOk;
    private TextView tvAlbum, tvPreview, tvTitle;
    private PhotoSelectorDomain photoSelectorDomain;
    private PhotoSelectorAdapter photoAdapter;
    private AlbumAdapter albumAdapter;
    private RelativeLayout layoutAlbum;
    private ArrayList<PhotoModel> selected;
    private TextView tvNumber;
    private int imageOrder = 0;
    private int desiredWidth;
    private int desiredHeight;
    private int quality;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RECCENT_PHOTO = getResources().getString(R.string.recent_photos);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.activity_photoselector);

        if (getIntent().getExtras() != null) {
            MAX_IMAGE = getIntent().getIntExtra(KEY_MAX, 10);
            desiredWidth = getIntent().getIntExtra(WIDTH_KEY, 0);
            desiredHeight = getIntent().getIntExtra(HEIGHT_KEY, 0);
            quality = getIntent().getIntExtra(QUALITY_KEY, 0);
        }

//        initImageLoader();

        photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());

        selected = new ArrayList<PhotoModel>();

        tvTitle = (TextView) findViewById(R.id.tv_title_lh);
        gvPhotos = (GridView) findViewById(R.id.gv_photos_ar);
        lvAblum = (ListView) findViewById(R.id.lv_ablum_ar);
        btnOk = (Button) findViewById(R.id.btn_right_lh);
        tvAlbum = (TextView) findViewById(R.id.tv_album_ar);
        tvPreview = (TextView) findViewById(R.id.tv_preview_ar);
        layoutAlbum = (RelativeLayout) findViewById(R.id.layout_album_ar);
        tvNumber = (TextView) findViewById(R.id.tv_number);

        btnOk.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
        tvPreview.setOnClickListener(this);

        photoAdapter = new PhotoSelectorAdapter(getApplicationContext(),
                new ArrayList<PhotoModel>(), CommonUtils.getWidthPixels(this),
                this, this, this);
        gvPhotos.setAdapter(photoAdapter);

        albumAdapter = new AlbumAdapter(getApplicationContext(),
                new ArrayList<AlbumModel>());
        lvAblum.setAdapter(albumAdapter);
        lvAblum.setOnItemClickListener(this);

        findViewById(R.id.bv_back_lh).setOnClickListener(this); // 返回

        photoSelectorDomain.getReccent(reccentListener); // 更新最近照片
        photoSelectorDomain.updateAlbum(albumListener); // 跟新相册信息
        progress = new ProgressDialog(this);
        progress.setTitle(R.string.progress_title);
        progress.setMessage(getString(R.string.progress_message));
    }
//
//    private void initImageLoader() {
//        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.ic_picture_loading)
//                .showImageOnFail(R.drawable.ic_picture_loadfailed)
//                .cacheInMemory(true).cacheOnDisk(true)
//                .resetViewBeforeLoading(true).considerExifParams(false)
//                .bitmapConfig(Bitmap.Config.RGB_565).build();
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//                this)
//                .memoryCacheExtraOptions(400, 400)
//                // default = device screen dimensions
//                .diskCacheExtraOptions(400, 400, null)
//                .threadPoolSize(5)
//                // default Thread.NORM_PRIORITY - 1
//                .threadPriority(Thread.NORM_PRIORITY)
//                // default FIFO
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                // default
//                .denyCacheImageMultipleSizesInMemory()
//                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
//                .memoryCacheSize(2 * 1024 * 1024)
//                .memoryCacheSizePercentage(13)
//                // default
//                .diskCache(
//                        new UnlimitedDiscCache(StorageUtils.getCacheDirectory(
//                                this, true)))
//                // default
//                .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
//                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
//                // default
//                .imageDownloader(new BaseImageDownloader(this))
//                // default
//                .imageDecoder(new BaseImageDecoder(false))
//                // default
//                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
//                // default
//                .defaultDisplayImageOptions(imageOptions).build();
//
//        ImageLoader.getInstance().init(config);
//    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_right_lh)
            ok(); // 选完照片
        else if (v.getId() == R.id.tv_album_ar)
            album();
        else if (v.getId() == R.id.tv_preview_ar)
            priview();
        else if (v.getId() == R.id.tv_camera_vc)
            catchPicture();
        else if (v.getId() == R.id.bv_back_lh)
            finish();
    }

    /**
     * 拍照
     */
    private void catchPicture() {
        CommonUtils.launchActivityForResult(this, new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            PhotoModel photoModel = new PhotoModel(CommonUtils.query(
                    getApplicationContext(), data.getData()));
            // selected.clear();
            // //--keep all
            // selected photos
            // tvNumber.setText("(0)");
            // //--keep all
            // selected photos
            // ///////////////////////////////////////////////////////////////////////////////////////////
            if (selected.size() >= MAX_IMAGE) {
                Toast.makeText(
                        this,
                        String.format(
                                getString(R.string.max_img_limit_reached),
                                MAX_IMAGE), Toast.LENGTH_SHORT).show();
                photoModel.setChecked(false);
                photoAdapter.notifyDataSetChanged();
            } else {
                if (!selected.contains(photoModel)) {
                    selected.add(photoModel);
                }
            }
            ok();
        }
    }

    /**
     * 完成
     */
    private void ok() {
        if (selected.isEmpty()) {
            setResult(RESULT_CANCELED);
        } else {
            progress.show();
            new ResizeImagesTask().execute(selected);
            /*
            Intent data = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("photos", selected);
			data.putExtras(bundle);
			setResult(RESULT_OK, data);
			*/
        }
    }

    /**
     * 预览照片
     */
    private void priview() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("photos", selected);
        CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
    }

    private void album() {
        if (layoutAlbum.getVisibility() == View.GONE) {
            popAlbum();
        } else {
            hideAlbum();
        }
    }

    /**
     * 弹出相册列表
     */
    private void popAlbum() {
        layoutAlbum.setVisibility(View.VISIBLE);
        new AnimationUtil(getApplicationContext(), R.anim.translate_up_current)
                .setLinearInterpolator().startAnimation(layoutAlbum);
    }

    /**
     * 隐藏相册列表
     */
    private void hideAlbum() {
        new AnimationUtil(getApplicationContext(), R.anim.translate_down)
                .setLinearInterpolator().startAnimation(layoutAlbum);
        layoutAlbum.setVisibility(View.GONE);
    }

    /**
     * 清空选中的图片
     */
    private void reset() {
        selected.clear();
        tvNumber.setText("(0)");
        tvPreview.setEnabled(false);
    }

    @Override
    /** 点击查看照片 */
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        if (tvAlbum.getText().toString().equals(RECCENT_PHOTO))
            bundle.putInt("position", position - 1);
        else
            bundle.putInt("position", position);
        bundle.putString("album", tvAlbum.getText().toString());
        CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
    }



    @Override
    /** 照片选中状态改变之后 */
    public void onCheckedChanged(PhotoModel photoModel,
                                 CompoundButton buttonView, boolean isChecked) {
        Log.e("选中状态", "选中状态" + isChecked);
        if (isChecked) {
            if (imageOrder > MAX_IMAGE - 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                Resources res = getResources();
                String title = String.format(res.getString(R.string.alert_limit_title), MAX_IMAGE);
                String message = String.format(res.getString(R.string.alert_limit_message), MAX_IMAGE);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton(res.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                buttonView.toggle();
            } else {
                if (!selected.contains(photoModel)) {
                    photoModel.setOrder(imageOrder);
                    selected.add(photoModel);
                    imageOrder++;
                }
                tvPreview.setEnabled(true);
            }
        } else {
            PhotoModel photomodel = null;
            for (int i = 0; i < selected.size(); i++) {
                if (photomodel != null) {
                    selected.get(i).setOrder(selected.get(i).getOrder() - 1);
                }
                if (selected.get(i).equals(photoModel)) {
                    photomodel = selected.get(i);
                }
            }
            if (photomodel != null) {
                selected.remove(photomodel);
                imageOrder--;
            }
            //selected.remove(photoModel);
        }

        photoAdapter.notifyDataSetChanged();


        tvNumber.setText("(" + selected.size() + ")");
        if (selected.isEmpty()) {
            tvPreview.setEnabled(false);
            tvPreview.setText(getString(R.string.preview));
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutAlbum.getVisibility() == View.VISIBLE) {
            hideAlbum();
        } else
            super.onBackPressed();
    }

    @Override
    /** 相册列表点击事件 */
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
        for (int i = 0; i < parent.getCount(); i++) {
            AlbumModel album = (AlbumModel) parent.getItemAtPosition(i);
            if (i == position) {
                album.setCheck(true);
            } else
                album.setCheck(false);
        }
        albumAdapter.notifyDataSetChanged();
        hideAlbum();
        tvAlbum.setText(current.getName());
        // tvTitle.setText(current.getName());

        // 更新照片列表
        if (current.getName().equals(RECCENT_PHOTO))
            photoSelectorDomain.getReccent(reccentListener);
        else
            photoSelectorDomain.getAlbum(current.getName(), reccentListener); // 获取选中相册的照片
    }

    /**
     * 获取本地图库照片回调
     */
    public interface OnLocalReccentListener {
        public void onPhotoLoaded(List<PhotoModel> photos);

        public void onPhotoAdded(List<PhotoModel> photos);
    }

    /**
     * 获取本地相册信息回调
     */
    public interface OnLocalAlbumListener {
        public void onAlbumLoaded(List<AlbumModel> albums);
    }

    private OnLocalAlbumListener albumListener = new OnLocalAlbumListener() {
        @Override
        public void onAlbumLoaded(List<AlbumModel> albums) {
            albumAdapter.update(albums);
        }
    };

    private OnLocalReccentListener reccentListener = new OnLocalReccentListener() {
        @Override
        public void onPhotoLoaded(List<PhotoModel> photos) {
            for (PhotoModel model : photos) {
                if (selected.contains(model)) {
                    model.setChecked(true);
                }
            }
            photoAdapter.update(photos);
            gvPhotos.smoothScrollToPosition(0); // 滚动到顶端
            // reset(); //--keep selected photos

        }

        @Override
        public void onPhotoAdded(List<PhotoModel> photos) {
            for (PhotoModel model : photos) {
                if (selected.contains(model)) {
                    model.setChecked(true);
                }
            }
            photoAdapter.add(photos);
            //gvPhotos.smoothScrollToPosition(0); // 滚动到顶端
            // reset(); //--keep selected photos

        }
    };


    private class ResizeImagesTask extends AsyncTask<ArrayList<PhotoModel>, Void, ArrayList<String>> {
        private Exception asyncTaskError = null;

        @Override
        protected ArrayList<String> doInBackground(ArrayList<PhotoModel>... fileSets) {
            ArrayList<PhotoModel> fileNames = fileSets[0];
            Bitmap bmp = null;
            int rotate = 0;
            String filename = null;
            ArrayList<String> al = new ArrayList<String>();
            try {
                for (int i = 0; i < fileNames.size(); i++) {
                    if (i != fileNames.get(i).getOrder()) {
                        for (int j = 0; j < fileNames.size(); j++) {
                            if (i == fileNames.get(j).getOrder()) {
                                filename = fileNames.get(j).getOriginalPath();
                                rotate = fileNames.get(j).getRotation();
                                break;
                            }
                        }
                    } else {
                        filename = fileNames.get(i).getOriginalPath();
                        rotate = fileNames.get(i).getRotation();
                    }
                    int index = filename.lastIndexOf('.');
                    String ext = filename.substring(index);
                    filename = filename.replaceAll("file://", "");
                    File file = new File(filename);
                    if (ext.compareToIgnoreCase(".gif") != 0) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 1;
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                        int width = options.outWidth;
                        int height = options.outHeight;
                        float scale = calculateScale(width, height);
                        if (scale < 1) {
                            int finalWidth = (int) (width * scale);
                            int finalHeight = (int) (height * scale);
                            int inSampleSize = calculateInSampleSize(options, finalWidth, finalHeight);
                            options = new BitmapFactory.Options();
                            options.inSampleSize = inSampleSize;
                            try {
                                try {
                                    bmp = this.tryToGetBitmap(file, options, rotate, true);
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } catch (OutOfMemoryError e) {
                                options.inSampleSize = calculateNextSampleSize(options.inSampleSize);
                                try {
                                    try {
                                        bmp = this.tryToGetBitmap(file, options, rotate, false);
                                    } catch (IOException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                    }
                                } catch (OutOfMemoryError e2) {
                                    throw new IOException("Unable to load image into memory.");
                                }
                            }
                        } else {
                            try {
                                bmp = this.tryToGetBitmap(file, null, rotate, false);
                            } catch (OutOfMemoryError e) {
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 2;
                                try {
                                    bmp = this.tryToGetBitmap(file, options, rotate, false);
                                } catch (OutOfMemoryError e2) {
                                    options = new BitmapFactory.Options();
                                    options.inSampleSize = 4;
                                    try {
                                        bmp = this.tryToGetBitmap(file, options, rotate, false);
                                    } catch (OutOfMemoryError e3) {
                                        throw new IOException("Unable to load image into memory.");
                                    }
                                }
                            }
                        }
                        file = this.storeImage(bmp, file.getName());
                        al.add(Uri.fromFile(file).toString());
                    } else {
                        file = this.copyImage(file);
                        al.add(Uri.fromFile(file).toString());
                    }
                }

                return al;
            } catch (IOException e) {
                try {
                    asyncTaskError = e;
                    for (int i = 0; i < al.size(); i++) {
                        URI uri = new URI(al.get(i));
                        File file = new File(uri);
                        file.delete();
                    }
                } catch (Exception exception) {
                    // the finally does what we want to do
                } finally {
                    return new ArrayList<String>();
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> al) {
            Intent data = new Intent();

            if (asyncTaskError != null) {
                Bundle res = new Bundle();
                res.putString("ERRORMESSAGE", asyncTaskError.getMessage());
                data.putExtras(res);
                setResult(RESULT_CANCELED, data);
            } else if (al.size() > 0) {
                Bundle res = new Bundle();
                res.putStringArrayList("MULTIPLEFILENAMES", al);
                if (selected != null) {
                    res.putInt("TOTALFILES", selected.size());
                }
                data.putExtras(res);
                setResult(RESULT_OK, data);
            } else {
                setResult(RESULT_CANCELED, data);
            }
            progress.dismiss();
            finish();
        }

        private Bitmap tryToGetBitmap(File file, BitmapFactory.Options options, int rotate, boolean shouldScale) throws IOException, OutOfMemoryError {
            Bitmap bmp;
            if (options == null) {
                bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            } else {
                bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            }
            if (bmp == null) {
                throw new IOException("The image file could not be opened.");
            }
            if (options != null && shouldScale) {
                float scale = calculateScale(options.outWidth, options.outHeight);
                bmp = this.getResizedBitmap(bmp, scale);
            }
            if (rotate != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotate);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            }
            return bmp;
        }

        /*
        * The following functions are originally from
        * https://github.com/raananw/PhoneGap-Image-Resizer
        *
        * They have been modified by Andrew Stephan for Sync OnSet
        *
        * The software is open source, MIT Licensed.
        * Copyright (C) 2012, webXells GmbH All Rights Reserved.
        */
        private File storeImage(Bitmap bmp, String fileName) throws IOException {
            int index = fileName.lastIndexOf('.');
            String name = "Temp_" + fileName.substring(0, index).replaceAll("([^a-zA-Z0-9])", "");
            String ext = fileName.substring(index);
            File file = File.createTempFile(name, ext);
            OutputStream outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, outStream);
            outStream.flush();
            outStream.close();
            return file;
        }

        private File copyImage(File srcfile) throws IOException {
            byte[] buf = new byte[4 * 1024];
            int b = 0;
            String fileName = srcfile.getName();
            int index = fileName.lastIndexOf('.');
            String name = "Temp_" + fileName.substring(0, index).replaceAll("([^a-zA-Z0-9])", "");
            String ext = fileName.substring(index);
            File file = File.createTempFile(name, ext);

            OutputStream outStream = new FileOutputStream(file);
            FileInputStream inStream = new FileInputStream(srcfile.getAbsolutePath());
            while ((b = inStream.read(buf, 0, buf.length)) != -1) {
                outStream.write(buf, 0, b);
                outStream.flush();
            }
            outStream.flush();
            outStream.close();
            inStream.close();
            return file;
        }

        private Bitmap getResizedBitmap(Bitmap bm, float factor) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            // create a matrix for the manipulation
            Matrix matrix = new Matrix();
            // resize the bit map
            matrix.postScale(factor, factor);
            // recreate the new Bitmap
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
            return resizedBitmap;
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private int calculateNextSampleSize(int sampleSize) {
        double logBaseTwo = (int) (Math.log(sampleSize) / Math.log(2));
        return (int) Math.pow(logBaseTwo + 1, 2);
    }

    private float calculateScale(int width, int height) {
        float widthScale = 1.0f;
        float heightScale = 1.0f;
        float scale = 1.0f;
        if (desiredWidth > 0 || desiredHeight > 0) {
            if (desiredHeight == 0 && desiredWidth < width) {
                scale = (float) desiredWidth / width;
            } else if (desiredWidth == 0 && desiredHeight < height) {
                scale = (float) desiredHeight / height;
            } else {
                if (desiredWidth > 0 && desiredWidth < width) {
                    widthScale = (float) desiredWidth / width;
                }
                if (desiredHeight > 0 && desiredHeight < height) {
                    heightScale = (float) desiredHeight / height;
                }
                if (widthScale < heightScale) {
                    scale = widthScale;
                } else {
                    scale = heightScale;
                }
            }
        }

        return scale;
    }
}
