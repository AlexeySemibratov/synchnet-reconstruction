package org.nsu.src.reconstruction;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Equation extends Expression {

    private char coeffChar;
    private List<String> nullableCoeffs;

    public Equation() {
        super();
        this.nullableCoeffs = new ArrayList<>();
    }

    public void addConditions(Set<String> conditions) {
        String currentExprString = getExpressionString();
        StringBuilder newExprString = new StringBuilder(currentExprString);

        if(!currentExprString.equals("")) {
            newExprString.append("&");
        }

        conditions.forEach(x -> {
            newExprString.append(x).append("&");
            nullableCoeffs.add(x.substring(1));
        });
        addNullableArgs();
        newExprString.delete(newExprString.length() - 1, newExprString.length());
        setExpressionString(newExprString.toString());
    }

    private void addNullableArgs() {
        nullableCoeffs.forEach(x -> {
            if(getArgument(x) == null) {
                addArguments(new Argument(x));
            }
        });
    }

    public String[] getArguments() {
        int n = this.getArgumentsNumber();
        String[] args = new String[n];
        for (int i = 0; i < n; i++) {
            args[i] = this.getArgument(i).getArgumentName();
        }
        return args;
    }

    public List<String> getArgumentsExcept(List<String> excepted) {
        String[] totalArgs = getArguments();
        List<String> result = Arrays.asList(totalArgs.clone());
        return result.stream().filter(x -> !excepted.contains(x)).collect(Collectors.toList());
    }

    public char getCoeffChar() {
        return coeffChar;
    }

    public void setCoeffChar(char coeffChar) {
        this.coeffChar = coeffChar;
    }
}
