package com.example.facedetect.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.facedetect.R;

public class SCFaceCover extends View {
    private int mBgColor;

    private Paint mBgPaint;
    private Paint mClearPaint;

    float[] mFaceCoverCorners;

    private AppCompatImageView mFaceCoverImage;
    private Rect mFaceRect;
    private RectF mFaceRectF;

    public SCFaceCover(Context context) {
        super(context);
    }

    public SCFaceCover(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CoverView);

        mBgColor = typedArray.getColor(R.styleable.CoverView_backgroundColor, context.getResources().getColor(R.color.black));

        typedArray.recycle();

        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
        mBgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

        mClearPaint = new Paint();
        mClearPaint.setColor(Color.TRANSPARENT);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mFaceCoverCorners = new float[]{
                30, 30,        // Top left radius in px
                30, 30,        // Top right radius in px
                30, 30,        // Bottom right radius in px
                30, 30         // Bottom left radius in px
        };
    }

    public SCFaceCover(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SCFaceCover(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setImageSource(AppCompatImageView faceCoverImage) {
        this.mFaceCoverImage = faceCoverImage;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFaceCoverImage == null) return;

        canvas.drawRect(0F, 0F, getWidth() * 1f, getHeight() * 1f, mBgPaint);

        float width = (float) (mFaceCoverImage.getHeight() * 150) /200;
        int left = (int) ((mFaceCoverImage.getWidth() - width) / 2);
        int right = (int) (left + width);

        mFaceRect = new Rect(
                left,
                mFaceCoverImage.getTop(),
                right,
                mFaceCoverImage.getBottom()
        );
        mFaceRectF = new RectF(mFaceRect);

        Path path = new Path();
        path.addRoundRect(mFaceRectF, mFaceCoverCorners, Path.Direction.CW);

        canvas.drawPath(path, mClearPaint);
    }

    public Rect getBoundingBox() {
        return mFaceRect;
    }
}
