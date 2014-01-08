package com.quadroid.quadroidmobile.util;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BitmapUtils {

	/**
	 * This method draws text on a {@link Bitmap}
	 * @param source
	 * 			The image
	 * @param text
	 * 			The text to write
	 * @param x
	 * 			X-Coordinate of text begin
	 * @param y
	 * 			Y-Coordinate of text begin
	 * @return
	 * 			The bitmap with the text on it
	 */
	public static Bitmap drawTextOnBitmap(Bitmap source, String text, int x, int y) {
		Canvas canvas = new Canvas(source);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE); // Text Color
        paint.setStrokeWidth(12); // Text Size

        canvas.drawBitmap(source, 0, 0, paint);
        canvas.drawText(text, x, y, paint);
        
        return source;
	}
	
	/**
	 * Saves image to a file and returns the filepath
	 * @param context
	 * 			A context
	 * @param bitmap
	 * 			The bitmap to save
	 * @param imageId
	 * 			Id of the image (is used as filename)
	 * @return
	 * 			File path of the saved image as string
	 * 			
	 */
	public static String saveImageToMemoryCard(Context context, Bitmap bitmap, int imageId) {
		FileOutputStream imageOutStream = null;
		try {
			File outFile = StorageUtils.getImageFile(context, imageId, true);
			imageOutStream = new FileOutputStream(outFile);
			bitmap.compress(CompressFormat.PNG, 100, imageOutStream);
			return outFile.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				imageOutStream.close();
			} catch (Exception e) {}
		}
		return null;
	}
}
