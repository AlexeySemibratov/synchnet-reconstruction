package org.nsu.src.reconstruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModelSolution {

    public Equation fullExpression = null;
    public List<String> insignificantCoeffs = new ArrayList<>();
    public String[] mainCoeffs;

    public Equation expression;
    public List<int[]> models;
    public int[] currentModel;

    private int findedSolutionCount;
    private int totalSolutionCount;

    public ModelSolution(Equation e) {
        this.expression = e;
        this.models = new ArrayList<>();
    }

    public void initCoeffs() {
        if (fullExpression == null) throw new NullPointerException("First set up full expression!");
        //String[] systemCoeffs = PMath.getArguments(expression);
        var coeffs = Arrays.stream(mainCoeffs).collect(Collectors.toCollection(ArrayList::new));
        coeffs.addAll(Arrays.asList(Solver.ARGS));
        insignificantCoeffs = fullExpression.getArgumentsExcept(coeffs);
    }

    public void addModel(int[] model) {
        models.add(model);
    }

    public void initInfo() {
        this.findedSolutionCount = models.size();
        this.totalSolutionCount = (int) (findedSolutionCount * Math.pow(2, insignificantCoeffs.size()));
    }

    public void printModels() {
        System.out.println(Arrays.toString(mainCoeffs));
        System.out.println(Arrays.toString(insignificantCoeffs.toArray()));
        models.forEach(model -> System.out.println(Arrays.toString(model)));
    }

    public void printCurrentModel() {
        System.out.println(Arrays.toString(mainCoeffs));
        System.out.println(Arrays.toString(insignificantCoeffs.toArray()));
        System.out.println(Arrays.toString(currentModel));
    }

    public void printModel(int index) {
        System.out.println(Arrays.toString(mainCoeffs));
        System.out.println(Arrays.toString(insignificantCoeffs.toArray()));
        System.out.println(Arrays.toString(models.get(index)));
    }

    public void applyModel(int index) {
        if (index < 0 || index >= models.size()) {
            System.out.print("Model with index " + (index + 1) + "doesn't exist. Index must be in range [1," +
                    models.size() + "].");
            return;
        }
        currentModel = models.get(index);
        System.out.println("Model applied!");
        System.out.println(Arrays.toString(currentModel));
    }

    public int getFindedSolutionCount() {
        return findedSolutionCount;
    }

    public int getTotalSolutionCount() {
        return totalSolutionCount;
    }
}
