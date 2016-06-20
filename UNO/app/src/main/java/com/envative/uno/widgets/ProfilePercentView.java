package com.envative.uno.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.envative.emoba.utils.EMDrawingUtils;
import com.envative.emoba.widgets.PercentViewPiece;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by clay on 6/12/16.
 */
public class ProfilePercentView extends View {

    private String displayNumber = "";
    private String displayLabel = "";
    private int displayLabelColor = Color.LTGRAY;
    private int displayNumberColor = Color.WHITE;
    private boolean fillCenter = false;
    Paint paint;
    Paint textPaint;
    Paint bgpaint;
    RectF rect;
    RectF innerRect;
    float percentage1 = 0;
    float percentage2 = 0;
    float percentage3 = 0;
    float angle1 = 0;
    float angle2 = 0;
    float angle3 = 0;


    // used to keep reference at start of animation
    float refAngle1 = 0;
    float refAngle2 = 0;
    float refAngle3 = 0;

    private boolean useBackground = false;
    private int centerBackground = -1;
    private int centerBackgroundColor = Color.GRAY;
    private int circleBgColor = Color.GRAY; // for circle background ( same width as regular pieces )
    int initialAngle = 0;
    private ArrayList<PercentViewPiece> pieces = new ArrayList<>();
    private boolean useCircleBackground = false;

    private int circleWidth = 28;
    private int percentTextSize = 95;
    private int percentLabelSize = 35;
    private int backgroundCircleWidth = 28;
    private int pieceWidth = 28;
    private File centerBackgroundImageFile = null;
    private boolean imgSetAlready = false;

    public ProfilePercentView (Context context) {
        super(context);
        init();
    }
    public ProfilePercentView (Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }
    public ProfilePercentView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public void init(AttributeSet attrs){
        init();
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                com.envative.emoba.R.styleable.PercentView,
                0, 0);

        try {
            // center value
            centerBackground = a.getResourceId(com.envative.emoba.R.styleable.PercentView_centerBackground, -1);

            centerBackgroundColor = a.getColor(com.envative.emoba.R.styleable.PercentView_centerBackgroundColor, getResources().getColor(com.envative.emoba.R.color.transparent));
            if(centerBackground != -1){
                useBackground = true;
            }
            backgroundCircleWidth = a.getInt(com.envative.emoba.R.styleable.PercentView_backgroundCircleWidth, 28);
            pieceWidth = a.getInt(com.envative.emoba.R.styleable.PercentView_pieceWidth, 28);
            displayNumber = a.getString(com.envative.emoba.R.styleable.PercentView_displayNumber);
            displayNumberColor = a.getColor(com.envative.emoba.R.styleable.PercentView_displayNumberColor, getResources().getColor(com.envative.emoba.R.color.colorLightGray));

            // center label
            displayLabel = a.getString(com.envative.emoba.R.styleable.PercentView_displayLabel);
            displayLabelColor = a.getColor(com.envative.emoba.R.styleable.PercentView_displayLabelColor, getResources().getColor(com.envative.emoba.R.color.colorLightGray));

        } finally {
            a.recycle();
        }
    }

    private void init() {
        paint = new Paint();
        textPaint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        // background color of circle
        bgpaint = new Paint();
        bgpaint.setColor(centerBackgroundColor);
        bgpaint.setAntiAlias(true);
        bgpaint.setStyle(Paint.Style.STROKE);

        rect = new RectF();
        innerRect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw background circle anyway
        int left = 0;
        int width = getWidth();
        int top = 0;
        int spacing = 15;
        int textCenter = (width) / 2;

        if(width > 0){
            circleWidth = width / 12;
            percentTextSize = width / 4;
            percentLabelSize = width / 9;
        }

        rect.set(left, top, left + width, top + width);
        innerRect.set(left + spacing, top + spacing, left + width - spacing, top + width - spacing);

        paint.setStrokeWidth(circleWidth);

        if(useBackground){
            // if they set a centerBackground
            if(centerBackground != -1){
                int imgWidth = width - (2*pieceWidth);
                int startX = pieceWidth;
                int startY = pieceWidth;
                Bitmap icon = BitmapFactory.decodeResource(getResources(), centerBackground);
                icon = Bitmap.createScaledBitmap(icon, imgWidth, imgWidth, false);
                canvas.drawBitmap(EMDrawingUtils.getCircleBitmap( icon ), startX, startY, paint);
            }

            if(centerBackgroundImageFile != null){
//                if(!imgSetAlready){
                    Log.d("image not set", "path: " + centerBackgroundImageFile.getPath());
                    int imgWidth = width - (2*pieceWidth);
                    int startX = pieceWidth;
                    int startY = pieceWidth;
                    Bitmap icon = BitmapFactory.decodeFile(centerBackgroundImageFile.getPath());

                    if(icon != null){
                        double imgWidthFactor = 2.5;
                        int topBotPadding = (icon.getHeight() - (int)(imgWidth*2.5)) / 2;
                        int leftRightPadding = (icon.getWidth() - (int)(imgWidth*2.5)) / 2;

                        if(icon.getHeight() < imgWidth){
                            imgWidthFactor = 1;
                            leftRightPadding = 0;
                        }

                        Log.d("ProfilePercentView", "leftRightPadding: " + leftRightPadding + " icon.getHeight(): " + icon.getHeight() + " icon.getWidth(): " + icon.getWidth() + " imgWidth: " + imgWidth
                        );
                        icon = Bitmap.createBitmap(icon, leftRightPadding, 0, icon.getHeight(), icon.getHeight());
                        icon = Bitmap.createScaledBitmap(icon, imgWidth, imgWidth, false);
                        icon = ExifUtil.rotateBitmap(centerBackgroundImageFile.getPath(), icon);
                        canvas.drawBitmap(EMDrawingUtils.getCircleBitmap( icon ), startX, startY, paint);

                        imgSetAlready = true;
                    }
//                }
            }

            bgpaint.setColor(centerBackgroundColor);
            canvas.drawArc(rect, -90, 360, true, bgpaint); // background if you want it
        }

        if(useCircleBackground){
            paint.setColor(circleBgColor);
            paint.setStrokeWidth(backgroundCircleWidth);
            canvas.drawArc(innerRect, 0, 360, false, paint);
        }

        paint.setStrokeWidth(pieceWidth);

        float angleOffset = 0;
        for(int i = 0, length = pieces.size(); i < length; i++){
            PercentViewPiece piece = pieces.get(i);
            paint.setColor(piece.color);

            if(i == 0) {
                angleOffset = initialAngle;
            }else{
                angleOffset = angleOffset + pieces.get(i -1).currAngle;
            }

            piece.sweepAngle = angleOffset;
            canvas.drawArc(innerRect, piece.sweepAngle, piece.currAngle, false, paint);
        }

        // draw amount text
        if(displayNumber != null) {
            textPaint.setStrokeWidth(0);
            textPaint.setAntiAlias(true);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(displayNumberColor);
            textPaint.setTextSize(percentTextSize);

            canvas.drawText(displayNumber, textCenter, ((width / 5) * 2) + (textPaint.getTextSize() / 3), textPaint);
        }

        // draw label text
        if(displayLabel != null){
            // draw total text
            textPaint.setStrokeWidth(0);
            textPaint.setAntiAlias(true);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setColor(displayLabelColor);
            textPaint.setTextSize(percentLabelSize);
            canvas.drawText(displayLabel, textCenter, ((width / 3) * 2) + (textPaint.getTextSize() / 3), textPaint);
        }
    }



    /**
     * Used if you just want to set the percentages with no animation
     * @param percentage1
     * @param percentage2
     * @param percentage3
     */
    public void setPercentages(float percentage1, float percentage2, float percentage3) {
        this.percentage1 = percentage1;
        this.percentage2 = percentage2;
        this.percentage3 = percentage3;
        updateAngles();
        invalidate();
    }

    /**
     * set the value to be displayed in the circle
     * @param num
     */
    public void setDisplayNumber(int num, boolean isPercent){
        String unit = (isPercent) ? "%" : "";
        this.displayNumber = num + unit;
    }

    public void setDisplayLabel(String label){
        this.displayLabel = label;
    }

    /**
     * whether or not to fill in the center to make it look like a pie chart
     * @param fillCenter
     */
    public void setFillCenter(boolean fillCenter){
        this.fillCenter = fillCenter;
    }

    /**
     * set the pieces to display
     * @param pieces
     */
    public void setPieces(ArrayList<PercentViewPiece> pieces){
        this.pieces.clear();
        this.pieces.addAll(pieces);
    }

    /**
     * Set angle from where to start
     * @param angle
     */
    public void setInitialAngle(int angle){
        this.initialAngle = angle;
    }

    /**
     * Update all angles ( used in animation )
     */
    private void updateAngles() {
        angle1 = (360*percentage1);
        angle2 = (360*percentage2);
        angle3 = (360*percentage3);
    }

    // Was trying to get this working so they would animated from a reference point
    // instead of 0 but gave up for now
    public void setRefAngles(float refAngle1, float refAngle2, float refAngle3) {
        this.refAngle1 = refAngle1;
        this.refAngle2 = refAngle2;
        this.refAngle3 = refAngle3;
    }

    public void setUseBackground(boolean useBackground){
        this.useBackground = useBackground;
    }

    public void setUseCircleBackground(boolean useCircleBackground){
        this.useCircleBackground = useCircleBackground;
    }

    public void setBackgroundColor(int centerBackgroundColor){
        setUseBackground(true); // use background color
        this.centerBackgroundColor = centerBackgroundColor;
    }

    public void setBackgroundCircleColor(int circleBgColor){
        setUseCircleBackground(true); // use background color
        this.circleBgColor = circleBgColor;
    }

    public void setCenterBackground(int resId){
        this.centerBackground = resId;
    }

    public void setCenterBackgroundImageFile(File file){
        imgSetAlready = false;
        this.centerBackgroundImageFile = file;
    }

    public void setDisplayLabelColor(int color) {
        this.displayLabelColor = color;
    }

    public void setDisplayNumberColor(int color) {
        this.displayNumberColor = color;
    }

    public void reset() {
        percentage1 = 0;
        percentage2 = 0;
        percentage3 = 0;
        angle1 = 0;
        angle2 = 0;
        angle3 = 0;
        init();
        requestLayout();
    }
}
