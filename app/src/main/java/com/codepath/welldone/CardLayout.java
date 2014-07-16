package com.codepath.welldone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public final class CardLayout extends FrameLayout {

    private static Paint sPaint;
    private static Drawable sCardTop, sCardLeft, sCardRight, sCardBottom;
    private static float sDensity;

    public CardLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);

        final Resources res = context.getResources();

        if (sCardTop == null) {
            sPaint = new Paint();
            sPaint.setColor(res.getColor(android.R.color.white));
            sCardTop = res.getDrawable(R.drawable.card_top_background);
            sCardBottom = res.getDrawable(R.drawable.card_bottom_background);
            sCardLeft = res.getDrawable(R.drawable.card_left_background);
            sCardRight = res.getDrawable(R.drawable.card_right_background);

            sDensity = res.getDisplayMetrics().density;
        }
    }

    @Override
    public void onDraw(final Canvas canvas) {
        final int w = getWidth();
        final int h = getHeight();

        sCardTop.setBounds(0, 0, w, dipify(4));
        sCardTop.draw(canvas);

        sCardLeft.setBounds(0, dipify(4), dipify(1), h - dipify(4));
        sCardLeft.draw(canvas);

        sCardRight.setBounds(w - dipify(1), dipify(4), w, h - dipify(4));
        sCardRight.draw(canvas);

        sCardBottom.setBounds(0, h - dipify(4), w, h);
        sCardBottom.draw(canvas);

        canvas.drawRect(dipify(1), dipify(4), w - dipify(1), h - dipify(4), sPaint);
    }

    private static int dipify(final int dips) {
        return (int) (dips * sDensity + 0.5f);
    }
}
