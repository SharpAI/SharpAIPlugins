/**
 * An Audio Picker Plugin for Cordova/PhoneGap.
 */
package com.actiontec.gushitie.audiopicker;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import android.net.Uri;
import android.content.ContentUris;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import android.util.Base64;

public class AudioPicker extends CordovaPlugin {
	public static String TAG = "AudioPicker";
  public final int MUSIC_SELECT = 000;

	private CallbackContext callbackContext;
	private JSONObject params = null;

	public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
		//this.params = args.getJSONObject(0);
		if (action.equals("getAudio")) {
      Intent intent = new Intent("android.intent.action.PICK");
      Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // the media uri refer the mobile media provider
      intent.setDataAndType(uri, "vnd.android.cursor.dir/audio");
      if (this.cordova != null) {
				this.cordova.startActivityForResult((CordovaPlugin) this, intent, MUSIC_SELECT);
			}
		}
    else if (action.equals("deleteSongs")) {
    }
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && data != null && requestCode == MUSIC_SELECT) {
        Uri contentUri = null;
        contentUri = data.getData();
        long id = ContentUris.parseId(contentUri);
        //获取音乐的封面，
        //Uri albumUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), id);
        //img1.setImageBitmap(createThumbFromUir(getContentResolver(), albumUri));
        Music info = getInfo(String.valueOf(id));
        String artist = info.getTitle();
        String filename = info.getUrl();

        JSONArray res = new JSONArray();
        try {
					JSONObject obj = new JSONObject();
          obj.put("image", info.getImage());
          obj.put("exportedurl", "file://" + filename);
					obj.put("title", info.getTitle());
					obj.put("artist", info.getName());
					res.put(obj);

					Log.i("##RDBG", "image: " + info.getImage());
					Log.i("##RDBG", "exportedurl: " + "file://" + filename);
					Log.i("##RDBG", "title: " + info.getTitle());
					Log.i("##RDBG", "artist: " + info.getName());
        }
        catch (JSONException ex) {}

        this.callbackContext.success(res);
		} else if (resultCode == Activity.RESULT_CANCELED && data != null) {
			String error = data.getStringExtra("ERRORMESSAGE");
			this.callbackContext.error(error);
		} else if (resultCode == Activity.RESULT_CANCELED) {
			JSONArray res = new JSONArray();
			this.callbackContext.success(res);
		} else {
			this.callbackContext.error("No Audio selected");
		}
	}

  private Music getInfo(String getId) {
    ContentResolver cr = this.cordova.getActivity().getApplicationContext().getContentResolver();
    if (cr != null) {
      // 获取所有歌曲
      Cursor cursor = cr.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
        null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
      if (null == cursor) {
        return null;
      }
      if (cursor.moveToFirst()) {
        do {
          Music m = new Music();
          String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
          if (getId.equals(id)){
						String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
						String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
						String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
						m.setTitle(title);
						m.setUrl(data);
						m.setName(artist);

						Cursor artCursor = cr.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
							new String[] {MediaStore.Audio.AlbumColumns.ALBUM_ART},
							MediaStore.Audio.Media._ID+" =?",
							new String[] {getId},
							null);
						String albumArt = null;
						if (artCursor.moveToFirst()) {
							albumArt = artCursor.getString(0);
						}
						else {
							albumArt = null;
						}
						artCursor.close();
						Log.i("##RDBG", "albumArt: " + albumArt);

						Bitmap bm = null;
						if (albumArt != null)
							bm = BitmapFactory.decodeFile(albumArt);
						else
							bm = BitmapFactory.decodeResource(this.cordova.getActivity().getResources(), FakeR.getId(this.cordova.getActivity(), "drawable", "default_music"));
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
						byte[] b = baos.toByteArray();
						String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
						m.setImage(encodedImage);

            return m;
          }
        }
        while (cursor.moveToNext());
      }
			cursor.close();
    }
    return null;
  }
}
