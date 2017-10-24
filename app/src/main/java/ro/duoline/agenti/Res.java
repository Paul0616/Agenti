package ro.duoline.agenti;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;

/**
 * Created by Paul on 24.10.2017.
 */

public class Res extends Resources {
    public Res(Resources original){
        super(original.getAssets(), original.getDisplayMetrics(),original.getConfiguration());
    }

    @Override
    public int getColor(int id) throws NotFoundException {
        return super.getColor(id, null);
    }

    @Override
    public int getColor(int id, Theme theme) throws NotFoundException {
        switch (getResourceEntryName(id)) {
            case "colorBackground":
                // You can change the return value to an instance field that loads from SharedPreferences.
                return Color.RED; // used as an example. Change as needed.
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return super.getColor(id, theme);
                }
                return super.getColor(id);
        }
    }
}
