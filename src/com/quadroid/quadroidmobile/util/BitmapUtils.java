package com.quadroid.quadroidmobile.util;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class BitmapUtils {

	public static Bitmap drawTextOnBitmap(Bitmap source, String text, int x, int y) {
		Canvas canvas = new Canvas(source);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE); // Text Color
        paint.setStrokeWidth(12); // Text Size
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern

        canvas.drawBitmap(source, 0, 0, paint);
        canvas.drawText("Testing...", x, y, paint);
        
        return source;
	}
	
	public static String saveImageToMemoryCard(Bitmap bitmap, int imageId) {
		FileOutputStream imageOutStream = null;
		try {
			File outFile = StorageUtils.getImageFile(imageId);
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
