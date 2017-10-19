package ro.duoline.agenti;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by Paul on 03.10.2017.
 */

public class ButoaneMeniuPrincipal {
    private int culoareButon = Color.parseColor("#ffffff"); //white color
    private String textButon;
    private Drawable icon;
    private Boolean mesaj;

    public int getCuloareButon(){
        return culoareButon;
    }

    public Boolean getMesaj(){
        return mesaj;
    }

    public String getTextButon(){
        return textButon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setCuloareButon(int culoare) {
        this.culoareButon = culoare;
    }

    public void setTextButon(String text){
        this.textButon = text;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setMesaj(Boolean msj){
        this.mesaj = msj;
    }
}
