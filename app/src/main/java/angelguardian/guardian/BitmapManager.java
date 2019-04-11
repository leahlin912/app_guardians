package angelguardian.guardian;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;


public class BitmapManager {
    private Context context;

    public BitmapManager(Context context){
        this.context = context;
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                   int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public Bitmap scaleBitmap(Bitmap bmp, float width, float height){
        int originWidth = bmp.getWidth();
        int originHeight = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(width/originWidth, height/originHeight);
        // create the new Bitmap object
        return Bitmap.createBitmap(bmp,
                0,
                0,
                originWidth,
                originHeight,
                matrix,
                true);
    }

    private float convertPixelsToDp(float px){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public Bitmap getBitmap(int resourceId, int width, int height){
        Bitmap bmp = decodeSampledBitmapFromResource(context.getResources(),
                resourceId,
                width,
                height);
//                (int)convertPixelsToDp(width), (int)convertPixelsToDp(height));  取消pixel轉dp
        return scaleBitmap(bmp, width, height);
    }

}
