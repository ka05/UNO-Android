package com.envative.uno.widgets;

import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;

import com.envative.emoba.widgets.PercentViewPiece;

import java.util.ArrayList;

/**
 * Created by clay on 6/12/16.
 */
public class CircleAnimation extends Animation {

    private ProfilePercentView circle;
    private ArrayList<PercentViewPiece> pieces;

    public enum AnimationType{
        Bounce,
        Overshoot,
        Decelerate,
        Accelerate,
        Anticipate,
        FastOutSlowIn,
    }

    public CircleAnimation(ProfilePercentView circle, ArrayList<PercentViewPiece> pieces, AnimationType type) {
        this.pieces = pieces;
        this.circle = circle;

        handleInterpolator(type);
    }

    private void handleInterpolator(AnimationType type) {
        switch (type){
            case Bounce:
                setInterpolator(new BounceInterpolator());
                break;
            case Overshoot:
                setInterpolator(new OvershootInterpolator());
                break;
            case Decelerate:
                setInterpolator(new DecelerateInterpolator());
                break;
            case Accelerate:
                setInterpolator(new AccelerateInterpolator());
                break;
            case Anticipate:
                setInterpolator(new AnticipateInterpolator());
                break;
            case FastOutSlowIn:
                setInterpolator(new FastOutSlowInInterpolator());
                break;
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        ArrayList<PercentViewPiece> transformedPieces = new ArrayList<>();

        for(int i = 0, length = pieces.size(); i < length; i++){
            PercentViewPiece piece = pieces.get(i);
            piece.currAngle = piece.startAngle * interpolatedTime;
            transformedPieces.add(piece);
        }

        circle.setPieces(transformedPieces);
        circle.requestLayout(); // update view
    }
}
