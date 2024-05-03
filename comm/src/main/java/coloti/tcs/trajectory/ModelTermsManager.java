package astri.trajectory;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;

import astri.astron.TimeUtil;

public class ModelTermsManager {

    private List<String> azTerms;
    private List<String> elTerms;
    private List<Double> coeff;
    private int nCoeff = 1;
    private final int MAXNUMCOEFF = 25;
    private ObjectMapper om = new ObjectMapper();
    private ModelTerms modelTerms;
    private List<Double> coeffAz;
    private List<Double> coeffEl;

    public ModelTermsManager() {
        initTerms();
        // addStandardTerms();
    }

    public void initTerms() {
        azTerms = new ArrayList<>();
        elTerms = new ArrayList<>();
        coeff = new ArrayList<>();
        coeffAz = new ArrayList<>();
        coeffEl = new ArrayList<>();
        for (int i = 0; i < MAXNUMCOEFF; i++) {
            azTerms.add(i, "0");
            elTerms.add(i, "0");
            coeff.add(i, 0.);
            coeffAz.add(i, 0.);
            coeffEl.add(i, 0.);
        }
        nCoeff = 1;
    }

    public double eval(String expression, double val) {
        DoubleEvaluator eval = new DoubleEvaluator();
        StaticVariableSet<Double> variables = new StaticVariableSet<>();
        if (expression.contains("x"))
            variables.set("x", val);
        if (expression.contains("y"))
            variables.set("y", val);

        return eval.evaluate(expression, variables);
    }

    public double eval(String expression, double val, double val1) {
        DoubleEvaluator eval = new DoubleEvaluator();
        StaticVariableSet<Double> variables = new StaticVariableSet<>();
        variables.set("x", val);
        variables.set("y", val1);

        return eval.evaluate(expression, variables);
    }

    public double eval(String expression) {
        DoubleEvaluator eval = new DoubleEvaluator();

        return eval.evaluate(expression);
    }

    public void addAzTerms(String term, int pos) {
        azTerms.remove(pos);
        azTerms.add(pos, term);
        nCoeff++;
        // System.out.println(azterms.get(pos));
    }

    public void addElTerms(String term, int pos) {
        elTerms.remove(pos);
        elTerms.add(pos, term);
        nCoeff++;
        // System.out.println(azterms.get(pos));
    }

    public void addAzTerms(String term) {
        azTerms.remove(nCoeff);
        azTerms.add(nCoeff, term);
        nCoeff++;
        // System.out.println(azterms.get(pos));
    }

    public void removeTerm(int idx) {

        azTerms.remove(idx);
        elTerms.remove(idx);
        nCoeff--;
        for (int i = 0; i < nCoeff; i++)
            System.out.println("i=" + i + " " + azTerms.get(i) + " " + elTerms.get(i));

        // System.out.println(azterms.get(pos));
    }

    public void removeAzTerm(String term) {
        int idx = azTerms.indexOf(term);
        removeTerm(idx);
    }

    public void removeElTerm(String term) {
        int idx = elTerms.indexOf(term);
        removeTerm(idx);
    }

    public void addElTerms(String term) {
        elTerms.remove(nCoeff);
        elTerms.add(nCoeff, term);
        nCoeff++;
        // System.out.println(azterms.get(pos));
    }

    public void addStandardTerms() {
        initTerms();
        addAzTerms("1", 0);
        addAzTerms("tan(y)", 1);
        addAzTerms("-1./cos(y)", 2);
        addAzTerms("sin(x)*tan(y)", 3);
        addAzTerms("-cos(x)*tan(y)", 4);
        addElTerms("cos(x)", 3);
        addElTerms("sin(x)", 4);
        addElTerms("1", 5);
        addElTerms("1*cos(y)", 6);
        addElTerms("1./tan(y)", 7);
        nCoeff = 8;
    }

    public double[] getAzMatRow(double az, double el) {
        return getMat(azTerms, az, el);
    }

    public double[] getElMatRow(double az, double el) {
        return getMat(elTerms, az, el);
    }

    private double[] getMat(List<String> terms, double az, double el) {
        double[][] A = new double[1][nCoeff];
        double[] B = new double[nCoeff];
        double x = az;
        double y = el;
        for (int i = 0; i < nCoeff; i++) {
            if ("0123456789".contains(terms.get(i)))
                // A[0][i] = eval(terms.get(i));
                B[i] = eval(terms.get(i));
            else if (terms.get(i).contains("x") && terms.get(i).contains("y"))
                // A[0][i] = eval(terms.get(i), x, y);
                B[i] = eval(terms.get(i), x, y);
            else {
                if (terms.get(i).contains("x"))
                    // A[0][i] = eval(terms.get(i), x);
                    B[i] = eval(terms.get(i), x);
                if (terms.get(i).contains("y"))
                    // A[0][i] = eval(terms.get(i), y);
                    B[i] = eval(terms.get(i), y);
            }

        }
        /*
         * A[0] = B;
         * for (int i = 0; i < nCoeff; i++)
         * System.out.println("i=" + i + " " + A[0][i]);
         */
        return B;
    }

    public double getCorr(List<String> terms, List<Double> coef, double az, double el) {
        double A = 0.0;
        double x = az;
        double y = el;
        // terms.forEach(e -> System.out.println(e));
        if (!terms.isEmpty() && !coef.isEmpty()) {
            for (int i = 0; i < nCoeff; i++) {

                if ("0123456789".contains(terms.get(i))) {
                    A += coef.get(i) * eval(terms.get(i));
                    // System.out.println("AA:" + coef.get(i) + " " + terms.get(i) + " " + A);
                } else if (terms.get(i).contains("x") && terms.get(i).contains("y")) {
                    A += coef.get(i) * eval(terms.get(i), x, y);
                    // System.out.println("BB:" + coef.get(i) + " " + terms.get(i) + " " + A);
                } else {
                    if (terms.get(i).contains("x")) {
                        A += coef.get(i) * eval(terms.get(i), x);
                        // System.out.println("cc:" + coef.get(i) + " " + terms.get(i) + " " + A);
                    }
                    if (terms.get(i).contains("y")) {
                        A += coef.get(i) * eval(terms.get(i), y);
                        // System.out.println("dd:" + coef.get(i) + " " + terms.get(i) + " " + A);

                    }
                }
            }
        }
        //System.out.println(A);
        return A;
    }

    public List<String> getAzTerms() {
        return azTerms;
    }

    public List<String> getElTerms() {
        return elTerms;
    }

    public Object[][] getTermsTable() {
        Object[][] tab;
        if (nCoeff > 1) {
            tab = new Object[nCoeff][3];
            for (int i = 0; i < nCoeff; i++) {
                tab[i][0] = i;
                tab[i][1] = azTerms.get(i).replace("x", "Az").replace("y", "El");
                tab[i][2] = elTerms.get(i).replace("x", "Az").replace("y", "El");

                System.out.println(tab[i][0] + "\t" + tab[i][1] + "\t\t" + tab[i][2]);
            }
            return tab;
        } else {
            tab = new Object[][] { { 0, "", "" } };
            return tab;
        }
    }

    public void setCoeff(double[] coeff) {
        Object[][] tab = getTermsTable();
        for (int i = 0; i < nCoeff; i++) {
            this.coeff.add(i, coeff[i]);
            if (!String.valueOf(tab[i][1]).equals("0")) {
                coeffAz.remove(i);
                coeffAz.add(i, coeff[i]);
            }
            if (!String.valueOf(tab[i][2]).equals("0")) {
                coeffEl.remove(i);
                coeffEl.add(i, coeff[i]);
            }
        }
    }

    public int getnCoeff() {
        return nCoeff;
    }

    public int getMaxnumcoeff() {
        return MAXNUMCOEFF;
    }

    public double getAzCorr(double az, double el) {
        return getCorr(azTerms, coeffAz, az, el);
    }

    public double getElCorr(double az, double el) {

        return getCorr(elTerms, coeffEl, az, el);
    }

    public void loadModelTermsFromFile(String file) {
        try {
            modelTerms = om.readValue(new File(file), ModelTerms.class);
            initTerms();
            nCoeff = 0;
            List<Term> terms = modelTerms.getTerms();
            int i = -1;
            for (Term term : terms) {

                if (term.getHorCoord().contains("az")) {
                    addAzTerms(term.getFunc(), term.getId());
                    coeffAz.add(term.getId(), term.getCoeff());
                    //System.out.println(term);

                }
                if (term.getHorCoord().contains("el")) {
                    addElTerms(term.getFunc(), term.getId());
                    coeffEl.add(term.getId(), term.getCoeff());
                    //System.out.println(term);
                }
                if (term.getId() > i)
                    i = term.getId();
                coeff.add(term.getId(), term.getCoeff());
            }
            nCoeff = i + 1;
            getTermsTable();


        } catch (IOException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    public void saveModelTermsToFile(String file, String telescope) {
        modelTerms = new ModelTerms();
        modelTerms.setTelescope(telescope);
        modelTerms.setDate(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm").format(TimeUtil.getCurrentTime()));
        List<Term> terms = new ArrayList<>();
        for (int i = 0; i < nCoeff; i++) {
            if (!azTerms.get(i).contains("0")) {
                Term a = new Term(i, azTerms.get(i), coeff.get(i), "az", "test");
                terms.add(a);
            }
        }
        for (int i = 0; i < nCoeff; i++) {
            if (!elTerms.get(i).contains("0")) {
                Term a = new Term(i, elTerms.get(i), coeff.get(i), "el", "test");
                terms.add(a);
            }
        }
        modelTerms.setTerms(terms);
        ObjectWriter writer = om.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File(file), modelTerms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
