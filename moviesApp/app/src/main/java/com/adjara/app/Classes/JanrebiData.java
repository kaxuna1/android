package com.adjara.app.Classes;

import com.adjara.app.model.Janri;

import java.util.ArrayList;

/**
 * Created by vakhtanggelashvili on 8/9/15.
 */
public class JanrebiData {
    private ArrayList<Janri> janrebi;
    public JanrebiData(){
        janrebi=new ArrayList<Janri>();
        janrebi.add(new Janri("სამეცნიერო ფანტასტიკა","878"));
        janrebi.add(new Janri("დეტექტივი","728"));
        janrebi.add(new Janri("მძაფრ სიუჟეტიანი","871"));
        janrebi.add(new Janri("თრილერი","872"));
        janrebi.add(new Janri("კრიმინალური","873"));
        janrebi.add(new Janri("მისტიკა","874"));
        janrebi.add(new Janri("დრამა","875"));
        janrebi.add(new Janri("კომედია","876"));
        janrebi.add(new Janri("სათავგადასავლო","877"));
        janrebi.add(new Janri("ანიმაციური","882"));
        janrebi.add(new Janri("საშინელება","890"));
    }

    public ArrayList<Janri> getJanrebi() {
        return janrebi;
    }
}
