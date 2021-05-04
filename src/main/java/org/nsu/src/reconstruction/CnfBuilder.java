package org.nsu.src.reconstruction;

import org.mariuszgromada.math.mxparser.Expression;


public class CnfBuilder {

    private Expression e;
    private String[] args;
    private StringBuilder cnf;

    public CnfBuilder(Expression e, String[] arguments) {
        this.e = new Expression();
        this.e.setExpressionString(e.getExpressionString());
        this.e.defineArguments(arguments);
        this.args = arguments;
        this.cnf= new StringBuilder();
    }

    public Expression toNewCnf() {
        int n = args.length;
        int[] currentArgValues = new int[n];
        generateTable(0, n, currentArgValues);
        e.setExpressionString(cnf.delete(cnf.length()-1,cnf.length()).toString());
        return this.e;
    }

    private void generateTable(int index, int size, int[] current) {
        if(index == size) {
            for(int i = 0; i < size; i++) {
                e.setArgumentValue(args[i], current[i]);
            }
            int currentValue = (int)e.calculate();
            if(currentValue!=0) return;

            cnf.append('(');
            for(int j = 0; j < size; j++) {
                cnf.append(current[j] == 1 ? ('~' + args[j]) : (args[j]));
                if(j != size-1) cnf.append('|');
            }
            cnf.append(')');
            cnf.append('&');
            return;
        }
        for(int i = 0; i < 2; i++) {
            current[index] = i;
            generateTable(index + 1, size, current);
        }
    }
}
