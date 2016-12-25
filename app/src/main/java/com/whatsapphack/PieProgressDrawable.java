package com.whatsapphack;

/**
 * Created by hasana on 12/22/2016.
 */

        import android.graphics.Canvas;
        import android.graphics.ColorFilter;
        import android.graphics.Paint;
        import android.graphics.Rect;
        import android.graphics.RectF;
        import android.graphics.drawable.Drawable;
        import android.util.DisplayMetrics;

/**
 * A PieProgressDrawable does this:
 * <a href="http://stackoverflow.com/questions/12458476/how-to-create-circular-progress-barpie-chart-like-indicator-android">Circular Progress Bar Android</a>
 */
public class PieProgressDrawable extends Drawable {

    private Paint mPaint;
    private RectF mOuterBoundsF;
    private RectF mBoundsF;
    private RectF mInnerBoundsF;
    final float START_ANGLE = 0.f;
    float mDrawTo;
    Paint pt;
    private float mMaxProgress=12f;
    private int mOuterCircleWidth;
    private int mInnerCircleWidth;

    public PieProgressDrawable() {
        super();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pt=new Paint(Paint.ANTI_ALIAS_FLAG);
    }
    public void setMaxProgress(int progress){
        mMaxProgress=progress;
    }

    public void setOuterCircleWidth(int width){
        mOuterCircleWidth=width;
    }
    public void setInnerCircleWidth(int width){
        mInnerCircleWidth=width;
    }

    /**
     * Set the border width.
     * @param widthDp in dip for the pie border
     */
    public void setBorderWidth(float widthDp, DisplayMetrics dm) {
        float borderWidth = widthDp * dm.density;
        mPaint.setStrokeWidth(borderWidth);
    }

    /**
     * @param color you want the pie to be drawn in
     */
    public void setPieColor(int color) {
        mPaint.setColor(color);
    }
    public void setCircleColor(int color){
        pt.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        // Rotate the canvas around the center of the pie by 90 degrees
        // counter clockwise so the pie stars at 12 o'clock.
        canvas.rotate(-90f, getBounds().centerX(), getBounds().centerY());
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawOval(mOuterBoundsF, mPaint);
        canvas.drawOval(mBoundsF, pt);

        canvas.drawArc(mInnerBoundsF, START_ANGLE, mDrawTo, true, mPaint);

        // Draw inner oval and text on top of the pie (or add any other
        // decorations such as a stroke) here..
        // Don't forget to rotate the canvas back if you plan to add text!
        // ...
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        makeBoundRectangularAndCenter(bounds);

        mOuterBoundsF = new RectF(bounds);
        mBoundsF=new RectF(mOuterBoundsF.left+mOuterCircleWidth,mOuterBoundsF.top+mOuterCircleWidth,mOuterBoundsF.right-mOuterCircleWidth,mOuterBoundsF.bottom-mOuterCircleWidth);
        mInnerBoundsF=new RectF(mBoundsF.left+mInnerCircleWidth,mBoundsF.top+mInnerCircleWidth,mBoundsF.right-mInnerCircleWidth,mBoundsF.bottom-mInnerCircleWidth);
        final int halfBorder = (int) (mPaint.getStrokeWidth()/2f + 0.5f);
        mInnerBoundsF.inset(halfBorder, halfBorder);
    }

    private void makeBoundRectangularAndCenter(Rect bounds) {

        if(bounds.right>bounds.bottom){
//            bounds.right=bounds.bottom;
            int margin=(bounds.right-bounds.bottom)/2;
            bounds.left=margin;
            bounds.right=bounds.left+bounds.bottom;
        }else{
//            bounds.bottom=bounds.right;
            int margin=(bounds.bottom-bounds.right)/2;
            bounds.top=margin;
            bounds.bottom=bounds.top+bounds.right;
        }
    }

    @Override
    protected boolean onLevelChange(int level) {
        final float drawTo = START_ANGLE + ((float)360*level)/mMaxProgress;
        boolean update = drawTo != mDrawTo;
        mDrawTo = drawTo;
        return update;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return mPaint.getAlpha();
    }
}