package me.nereo.multi_image_selector.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import android.media.ExifInterface;

/**
 *
 * @author Aizaz
 *
 */


public class PhotoModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String originalPath;
    private Integer order=0;
    private Integer rotation=0;
    private boolean isChecked;

    public void setOrder(Integer order)
    {
        this.order=order;
    }
    public void setRotation(Integer rotation)
    {
        this.rotation=rotation;
    }
    public Integer getOrder()
    {
        return order;
    }
    public Integer getRotation()
    {
        return rotation;
    }

    public PhotoModel(String originalPath, boolean isChecked) {
        super();
        this.originalPath = originalPath;
        this.isChecked = isChecked;
    }

    public PhotoModel(String originalPath) {
        this.originalPath = originalPath;
        this.isChecked = true;
    }

    public PhotoModel() {
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;

        File imageFile = new File(originalPath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    this.rotation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    this.rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    this.rotation = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

//	@Override
//	public boolean equals(Object o) {
//		if (o.getClass() == getClass()) {
//			PhotoModel model = (PhotoModel) o;
//			if (this.getOriginalPath().equals(model.getOriginalPath())) {
//				return true;
//			}
//		}
//		return false;
//	}

    public void setChecked(boolean isChecked) {
        System.out.println("checked " + isChecked + " for " + originalPath);
        this.isChecked = isChecked;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((originalPath == null) ? 0 : originalPath.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PhotoModel)) {
            return false;
        }
        PhotoModel other = (PhotoModel) obj;
        if (originalPath == null) {
            if (other.originalPath != null) {
                return false;
            }
        } else if (!originalPath.equals(other.originalPath)) {
            return false;
        }
        return true;
    }

}

