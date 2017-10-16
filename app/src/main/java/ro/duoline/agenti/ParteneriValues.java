package ro.duoline.agenti;

/**
 * Created by Paul on 10/12/2017.
 */

public class ParteneriValues {
    private String cod_fiscal;
    private String denumire;
    private String codtara;
    private Boolean isSelected = false;

    public String getCod_fiscal() {
        return cod_fiscal;
    }

    public String getDenumire() {
        return denumire;
    }

    public String getCodtara() {
        return codtara;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setCod_fiscal(String cod_fiscal) {
        this.cod_fiscal = cod_fiscal;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public void setCodtara(String codtara) {
        this.codtara = codtara;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

}
