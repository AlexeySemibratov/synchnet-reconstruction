package org.nsu.src.reconstruction;

import org.mariuszgromada.math.mxparser.Expression;

import java.io.*;

public class DimacsBuilder {

    private static String tmpdir = System.getProperty("java.io.tmpdir");

    private final String[] args;
    private final Expression e;
    private File dimacsFile;
    private FileWriter writer;
    private int clauses = 0;

    public DimacsBuilder(Expression e, String[] args) throws IOException {
        this.args = args;
        this.e = e;
        this.dimacsFile = File.createTempFile("dimacs_", ".tmp");
        this.writer = new FileWriter(dimacsFile);
    }

    public DimacsBuilder(Expression e) throws IOException {
        this.e = e;
        this.dimacsFile = File.createTempFile("dimacs_", ".tmp");
        this.writer = new FileWriter(dimacsFile);

        int n = e.getArgumentsNumber();
        String[] args = new String[n];
        for (int i = 0; i < n; i++) {
            args[i] = e.getArgument(i).getArgumentName();
        }
        this.args = args;
    }

    public File build() throws IOException {
        int n = args.length;

        generate(0, n, new int[n]);
        writer.close();


        String prefix = "p cnf " + n + " " + clauses + '\n';

        File withPrefix = new File("", "dimacs__");
        FileWriter wrt = new FileWriter(withPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(dimacsFile));
        wrt.write(prefix);
        String s;
        while((s = reader.readLine()) != null) {
            wrt.write(s + '\n');
        }
        dimacsFile.delete();
        reader.close();
        wrt.close();
        return withPrefix;
    }

    private void generate(int index, int size, int[] current) throws IOException {
        if(index == size) {
            for(int i = 0; i < size; i++) {
                e.setArgumentValue(args[i], current[i]);
            }
            int currentValue = (int)e.calculate();
            if(currentValue!=0) return;
            clauses++;
            StringBuilder cnf = new StringBuilder();
            int ind;
            for(int j = 0; j < size; j++) {
                ind = j + 1;
                cnf.append(current[j] == 1 ? "-" + ind : ind);
                cnf.append(' ');
            }
            cnf.append("0\n");

            //System.out.print(cnf.toString());

            writer.write(cnf.toString());
            return;
        }
        for(int i = 0; i < 2; i++) {
            current[index] = i;
            generate(index + 1, size, current);
        }
    }
}
