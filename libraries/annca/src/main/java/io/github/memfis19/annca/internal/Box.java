package io.github.memfis19.annca.internal;

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * Created by dhruv on 21/12/16.
 */

public class Box extends View {

    private Paint paint = new Paint();

    public Box(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);

        int x0 = canvas.getWidth() / 2;
        int y0 = canvas.getHeight() / 2;
        Log.d("dimensions",x0+"\n"+y0);

        canvas.drawRect(x0 - 320, y0 - 480, x0 + 320, y0 + 480, paint);
    }
}
