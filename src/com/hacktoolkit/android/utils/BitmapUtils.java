package com.hacktoolkit.android.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

public class BitmapUtils {
	public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
		if (scaleBitmapImage == null) {
			return null;
		}
		int sourceWidth = scaleBitmapImage.getWidth();
		int sourceHeight = scaleBitmapImage.getHeight();
	    int targetWidth = Math.min(sourceWidth, sourceHeight);
	    int targetHeight = targetWidth;
	    Bitmap targetBitmap = Bitmap.createBitmap(
	    		targetWidth,
	    		targetHeight,
	    		Bitmap.Config.ARGB_8888
	    		);

	    Canvas canvas = new Canvas(targetBitmap);
	    Path path = new Path();
	    path.addCircle(
	    		((float) targetWidth - 1) / 2,
	    		((float) targetHeight - 1) / 2,
	    		Math.min(((float) targetWidth), ((float) targetHeight)) / 2,
	    		Path.Direction.CCW
	    		);

	    canvas.clipPath(path);
	    Bitmap sourceBitmap = scaleBitmapImage;
	    canvas.drawBitmap(
	    		sourceBitmap,
	        new Rect(0, 0, sourceBitmap.getWidth(),
	        sourceBitmap.getHeight()),
	        new Rect(0, 0, targetWidth, targetHeight),
	        null
	    		);
	    return targetBitmap;
	}
}
