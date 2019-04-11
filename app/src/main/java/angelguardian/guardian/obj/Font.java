package angelguardian.guardian.obj;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;

import angelguardian.guardian.R;

public class Font {
    public static Typeface rothenburg;
    public static Typeface fredokafont;
    public static Typeface snowwinter;
    public static Typeface headline;

    public Font(Context context){
        fredokafont = ResourcesCompat.getFont(context, R.font.fredoka_one);
        rothenburg = ResourcesCompat.getFont(context, R.font.rothenburg);
        snowwinter = ResourcesCompat.getFont(context, R.font.snowwinter);
        headline = ResourcesCompat.getFont(context,R.font.headline);

    }
}
