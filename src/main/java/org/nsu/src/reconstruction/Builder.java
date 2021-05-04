package org.nsu.src.reconstruction;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Builder {

    private final int variableCount;
    private final List<int[]> fixedPoints;

    private List<List<Equation>> expressionSystem;
    private List<Equation> combinedSystem;

    private HashMap<String, int[]> conditions;

    public String[] absoluteArgs;

    public Builder(int variableCount, String[] absoluteArguments) {
        this.variableCount = variableCount;
        this.fixedPoints = new ArrayList<>();
        this.conditions = new HashMap<>();
        this.absoluteArgs = absoluteArguments;
    }

    public void setFixedPoints(List<int[]> fixedPoints) {
        for (int[] arr : fixedPoints) {
            addFixedPoint(arr);
        }
    }

    public List<int[]> getFixedPoints() {
        return fixedPoints;
    }

    public void addFixedPoint(int[] arr) {
        if (arr.length != variableCount)
            throw new IllegalArgumentException("The length of the array must be equal to the number of variables");
        fixedPoints.add(arr);
    }

    public void addCondition(String arg, int[] condition) {
        conditions.put(arg, condition);
    }

    private void buildExpresion(Equation e, List<String> coeffs, boolean inverse) {
        StringBuilder expString = new StringBuilder();
        coeffs.forEach(i -> expString.append(i).append("(+)"));
        expString.delete(expString.length() - 3, expString.length());

        e.setExpressionString(!inverse ? "(" + expString.toString() + ")" : "~(" + expString.toString() + ")");
        PMath.defineArguments(e, coeffs);
    }


    public void buildSystem() {
        expressionSystem = new ArrayList<>();
        combinedSystem = new ArrayList<>();

        int charIndex = 97;

        List<String> argsList = Arrays.asList(Solver.ARGS.clone());

        for (int i = 0; i < variableCount; i++) {
            expressionSystem.add(new ArrayList<>());
            for (int[] fixedPoint : fixedPoints) {
                Equation expression = new Equation();
                expression.setCoeffChar((char) charIndex);
                buildExpresion(
                        expression,
                        PMath.getRealCoeefs((char) charIndex, fixedPoint),
                        fixedPoint[i] == 0
                );

                if(conditions.containsKey(absoluteArgs[i])) {
                    int[] condition = conditions.get(absoluteArgs[i]);
                    expression.addConditions(PMath.getConditions((char) charIndex, condition));
                }

                expressionSystem.get(i).add(expression);
            }
            combinedSystem.add(conjunction(expressionSystem.get(i)));
            charIndex++;
        }

        System.out.println();
    }

    public File createDimacsFile(Equation expression) throws IOException {
        DimacsBuilder db = new DimacsBuilder(expression);
        return db.build();
    }

    private Equation conjunction(List<Equation> system) {
        Equation e = new Equation();
        StringBuilder expString = new StringBuilder();
        Set<String> newArgs = new HashSet<>();
        for (Equation ex : system) {
            for (int i = 0; i < ex.getArgumentsNumber(); i++) {
                newArgs.add(ex.getArgument(i).getArgumentName());
            }
            expString.append(ex.getExpressionString()).append("&");
        }
        expString.delete(expString.length() - 1, expString.length());
        e.setExpressionString(expString.toString());
        e.defineArguments(newArgs.toArray(String[]::new));
        e.setCoeffChar(system.get(0).getCoeffChar());
        return e;
    }

    public Equation getCombinedExpression(int index) {
        return combinedSystem.get(index - 1);
    }

}
