package ro.duoline.agenti;

/**
 * Created by Paul on 10/9/2017.
 */

public class ProduseValues {
    private int codProdus, stoc, rezervata, tva;
    private String clasa, denumire, um;
    private float pret_livr;


    public int getCodProdus(){
        return codProdus;
    }
    public int getStoc(){
        return stoc;
    }
    public int getRezervata(){
        return rezervata;
    }
    public int getTva(){
        return tva;
    }

    public String getClasa(){
        return clasa;
    }
    public String getDenumire(){
        return denumire;
    }
    public String getUm(){
        return um;
    }

    public float getPret_livr() {
        return pret_livr;
    }

    public void setCodProdus(int codProdus) {
        this.codProdus = codProdus;
    }

    public void setStoc(int stoc) {
        this.stoc = stoc;
    }

    public void setRezervata(int rezervata) {
        this.rezervata = rezervata;
    }

    public void setTva(int tva) {
        this.tva = tva;
    }

    public void setClasa(String clasa) {
        this.clasa = clasa;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public void setUm(String um) {
        this.um = um;
    }

    public void setPret_livr(float pret_livr) {
        this.pret_livr = pret_livr;
    }
}
