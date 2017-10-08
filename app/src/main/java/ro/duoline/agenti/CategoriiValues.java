package ro.duoline.agenti;

/**
 * Created by Paul on 10/8/2017.
 */

public class CategoriiValues {
    private int nrProduseInCategorie;
    private String numeCategorie;


    public int getNrProduseInCategorie(){
        return nrProduseInCategorie;
    }

    public String getNumeCategorie(){
        return numeCategorie;
    }



    public void setNrProduseInCategorie(int nr) {
        this.nrProduseInCategorie = nr;
    }

    public void setNumeCategorie(String text){
        this.numeCategorie = text;
    }

}
