package org.nsu.src.reconstruction;

import org.mariuszgromada.math.mxparser.Expression;
import org.nsu.src.Config;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.InstanceReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import java.io.IOException;
import java.util.*;

public class Solver {

    public static String[] ARGS = new String[0];
    public static Map<Integer, String> argsMatcherMap;

    private Builder builder;
    private Reader reader;

    private Long executionTime;

    private int functionCount;

    private List<ModelSolution> allSolutions;

    public Solver() {
    }

    public Solver(int functionCount, String[] absoluteArguments) {
        this.functionCount = functionCount;
        ARGS = absoluteArguments;
        this.builder = new Builder(functionCount, ARGS);
        ISolver solver = SolverFactory.newDefault();
        ModelIterator mi = new ModelIterator(solver);
        this.reader = new InstanceReader(mi);
        initMatcherMap();
    }

    private void initMatcherMap() {
        argsMatcherMap = new LinkedHashMap<>();
        for (int i = 0; i < ARGS.length; i++) {
            argsMatcherMap.put(i + 1, ARGS[i]);
        }
    }

    public static void setARGS(String[] ARGS) {
        Solver.ARGS = ARGS;
    }

    public void setFunctionCount(int functionCount) {
        this.functionCount = functionCount;
    }

    public void reconstructModel() throws TimeoutException, IOException, ParseFormatException, ContradictionException {
        /*builder.addFixedPoint(new int[]{1, 0, 0});
        builder.addFixedPoint(new int[]{0, 1, 1});*/
        /*builder.addFixedPoint(new int[]{0, 0, 1, 0});
        builder.addFixedPoint(new int[]{0, 1, 0, 0});
        builder.addFixedPoint(new int[]{1, 1, 0, 0});
        builder.addFixedPoint(new int[]{1, 0, 1, 0});
        builder.addFixedPoint(new int[]{1, 1, 1, 0});
        builder.addFixedPoint(new int[]{1, 0, 1, 1});
        builder.addFixedPoint(new int[]{0, 1, 1, 1});

        builder.addFixedPoint(new int[]{0, 0, 0, 0});
        builder.addFixedPoint(new int[]{1, 1, 1, 1});
        builder.addFixedPoint(new int[]{1, 0, 0, 1});
        builder.addFixedPoint(new int[]{0, 0, 0, 1});*/

        builder.buildSystem();

        allSolutions = new ArrayList<>();

        String lines = "-".repeat(50);

        boolean unsat = true;
        long startTime = System.nanoTime();
        for (int i = 1; i <= functionCount; i++) {
            Equation expression = builder.getCombinedExpression(i);
            String[] mainCoeffs = PMath.getArguments(expression);

            System.out.println(lines);
            if (Config.DETAIL_OUTPUT) {
                System.out.println("Solution for " + expression.getExpressionString());
                System.out.println(Arrays.toString(mainCoeffs));
            }

            ModelSolution solution = new ModelSolution(expression);
            solution.fullExpression = PMath.buildFullExpression(expression.getCoeffChar(), ARGS);
            solution.mainCoeffs = mainCoeffs;
            solution.initCoeffs();

            if (Config.PRINT_FREE_VARS) {
                solution.insignificantCoeffs.forEach(x -> System.out.print(x + " "));
                System.out.println();
            }

            System.out.println("Start prepare " + i + "-th function...");
            long t = System.nanoTime();
            IProblem problem = reader.parseInstance(builder.createDimacsFile(expression).getAbsolutePath());
            System.out.println("Start reconstruct " + i + "-th function...");
            while (problem.isSatisfiable()) {
                unsat = false;
                int[] model = problem.model();
                solution.addModel(model);
                if (Config.DETAIL_OUTPUT) System.out.println(Arrays.toString(model));
            }
            System.out.println("Finished. Passed time " + (System.nanoTime() - t) / 1000000 + " ms.");
            solution.initInfo();
            System.out.println("Found " + solution.getFindedSolutionCount() + " solutions");
            System.out.println("Total " + solution.getTotalSolutionCount() + " solutions");
            allSolutions.add(solution);
            if (unsat) {
                throw new ReconstructException("This system is unsatisfiable!");
            }
        }
        this.executionTime = System.nanoTime() - startTime;
        System.out.println(lines);
        System.out.println("Reconstruction finished. Total time " + (executionTime) / 1000000 + " ms");
        System.out.println(lines);
    }

    public List<ModelSolution> getAllSolutions() {
        return allSolutions;
    }

    public void addFixedPoint(int[] point) {
        builder.addFixedPoint(point);
    }

    public void setFixedPoints(List<int[]> fixedPoints) {
        builder.setFixedPoints(fixedPoints);
    }

    public void setClauses(Map<String, int[]> clauses) {
        clauses.forEach( (arg, clause) -> builder.addCondition(arg, clause));
    }

    public List<int[]> getFixedPoints() {
        return builder.getFixedPoints();
    }

    public Long getExecutionTime() {
        return executionTime;
    }
}
