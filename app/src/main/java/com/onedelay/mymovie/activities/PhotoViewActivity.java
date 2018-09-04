package com.onedelay.mymovie.activities;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;

public class PhotoViewActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = "Touch";
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        // 앱바 제목 텍스트 변경
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.appbar_photo_view));
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        ImageView view = findViewById(R.id.imageView);
        Glide.with(this).load(getIntent().getStringExtra(Constants.KEY_IMAGE_URL)).into(view);
        view.setOnTouchListener(this);
    }


    /**
     * Description : check the spacing between the two fingers on touch
     *
     * @param event
     * @return float
     */

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Description : calculates the midpoint between the two fingers
     *
     * @param point
     * @param event
     */

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // Show an event in the ogCat view, for debugging
    private void dumpEvent(MotionEvent motionEvent) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = motionEvent.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(motionEvent.getPointerId(i));
            sb.append(")=").append((int) motionEvent.getX(i));
            sb.append(",").append((int) motionEvent.getY(i));
            if (i + 1 < motionEvent.getPointerCount()) {
                sb.append(";");
            }
        }

        sb.append("]");
        Log.d("Touch Events ------", sb.toString());
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(motionEvent);
        // Handle touch events here

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:           // first finger down only
                savedMatrix.set(matrix);
                start.set(motionEvent.getX(), motionEvent.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP:             // first finger lifted

            case MotionEvent.ACTION_POINTER_UP:     // second finger lifted
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN:   // first and second finger down
                oldDist = spacing(motionEvent);
                Log.d(TAG, "mode=NONE");
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, motionEvent);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    // create the transformation in the matrix of points
                    matrix.postTranslate(motionEvent.getX() - start.x, motionEvent.getY() - start.y);
                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist = spacing(motionEvent);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist;  // setting the scaling of the matrix
                                                    // if scale > 1 means zoom in
                                                    // if scale < 1 means zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;

        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
