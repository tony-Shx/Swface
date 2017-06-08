package com.henu.swface.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Created by Administrator on 2017/4/16.
 */

public class RoundTransform implements Transformation {

	@Override
	public Bitmap transform(Bitmap source) {
		int size = Math.min(source.getWidth(), source.getHeight());

		int x = (source.getWidth() - size) / 2;
		int y = (source.getHeight() - size) / 2;

		Bitmap squaredBitmap = Bitmap
				.createBitmap(source, x, y, size, size);


		Bitmap bitmap = Bitmap.createBitmap(size, size, squaredBitmap.getConfig());

		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		BitmapShader shader = new BitmapShader(squaredBitmap,
				BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
		paint.setShader(shader);
		paint.setAntiAlias(true);

		float r = size / 2f;
		canvas.drawCircle(r, r, r, paint);
		if (squaredBitmap != source) {
			source.recycle();
		}
		squaredBitmap.recycle();
		return bitmap;
	}

	@Override
	public String key() {
		return "round";
	}
}
