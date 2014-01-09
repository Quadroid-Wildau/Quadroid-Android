package com.quadroid.quadroidmobile.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Class for {@link Bitmap} manipulation
 * 
 * @author Georg Baumgarten
 * @version 1.0
 *
 */
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
}
