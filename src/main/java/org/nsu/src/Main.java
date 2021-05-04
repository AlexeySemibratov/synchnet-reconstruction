package org.nsu.src;

import org.nsu.src.reconstruction.ModelSolution;
import org.nsu.src.reconstruction.PMath;
import org.nsu.src.reconstruction.Solver;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.out;

public class Main {

    public static void main(String[] args) {

        /*Solver s = new Solver(3, new String[]{"x", "y", "z"});
        try {
            s.reconstructModel();
        } catch (TimeoutException | IOException | ParseFormatException | ContradictionException e) {
            e.printStackTrace();
        }*/

        boolean exitFlag = false;

        Scanner scanner = new Scanner(System.in);
        int n = 0;
        String[] arguments = null;
        List<int[]> fixedPoints = new ArrayList<>();
        Map<String, int[]> clauses = new HashMap<>();
        Solver solver = new Solver();

        while (!exitFlag) {
            String command = scanner.nextLine();

            String cName = parseCommandName(command);
            String cArgs = parseCommandArgumet(command);

            if (cName.equals("n")) {
                n = Integer.parseInt(cArgs);
            } else if (cName.equals("setargs")) {
                arguments = cArgs.split(" ");
            } else if (cName.equals("addpoint")) {
                var fixedPoint = Arrays.stream(cArgs.split(" ")).mapToInt(((Integer::parseInt))).toArray();
                if (fixedPoint.length != n) {
                    out.println("Fixed point count must be same as args count = " + n + ".");
                } else {
                    fixedPoints.add(fixedPoint);
                    out.println("Point added.");
                }
            }
            else if (cName.equals("addclause")) {
                var comArguments = cArgs.split(" ");
                String argument = comArguments[0];
                var condition = IntStream.range(0, n).map(i -> Integer.parseInt(comArguments[i+1])).toArray();
                clauses.put(argument, condition);
                out.println("Condition added.");
            }
            else if (cName.equals("models")) {
                int index = Integer.parseInt(cArgs) - 1;
                var solution = solver.getAllSolutions().get(index);
                solution.printModels();
            } else if (cName.equals("model")) {
                int index = Integer.parseInt(cArgs) - 1;
                var s = solver.getAllSolutions().get(index);
                s.printModel(index);
            } else if (cName.equals("start")) {
                solver = new Solver(n, arguments);
                solver.setFixedPoints(fixedPoints);
                solver.setClauses(clauses);
                try {
                    solver.reconstructModel();
                } catch (TimeoutException | IOException | ParseFormatException | ContradictionException e) {
                    exitFlag = true;
                    e.printStackTrace();
                }
            } else if (cName.equals("applymodel")) {
                String[] strArgs = cArgs.split(" ");
                var comArgs = IntStream.range(0, 2).mapToObj(i -> Integer.valueOf(strArgs[i])).collect(Collectors.toList());
                var solution = solver.getAllSolutions().get(comArgs.get(0));
                solution.applyModel(comArgs.get(1));
            } else if (cName.equals("randmodel")) {
                Random rand = new Random();
                for (ModelSolution s : solver.getAllSolutions()) {
                    int modelsCount = s.models.size();
                    s.applyModel(rand.nextInt(modelsCount));
                }
            } else if (cName.equals("save")) {

            } else {
                out.println("Unresolved command.");
                printHelp();
            }

            if (command.equals("/exit")) {
                break;
            }
        }
    }

    private static void printHelp() {

        out.println("/n [n] --- to setup number of functions");
        out.println("/setargs [args...] --- to setup arguments");
        out.println("/add [fixedPoint] --- to add new fixed point");
        out.println("/start --- to start execution");
        out.println("/getmodel functionNumber --- to get all solutions for the function");

    }

    private static String parseCommandName(String command) {
        if (!command.startsWith("/")) {
            printHelp();
            return null;
        }
        int index = command.indexOf(' ');
        return index == -1 ? command.substring(1) : command.substring(1, index);
    }

    private static String parseCommandArgumet(String command) {
        if (!command.startsWith("/")) {
            printHelp();
            return null;
        }
        int index = command.indexOf(' ');
        return index == -1 ? "" : command.substring(index + 1);
    }

    private static String modelToString(int[] model, List<String> coeffs) {
        StringBuilder res = new StringBuilder();
        res.append('[');
        for (int i = 0; i < model.length; i++) {
            res.append(coeffs.get(i) + " = " + (model[i] < 0 ? 0 : 1))
                    .append(", ");
        }

        return res
                .delete(res.length() - 2, res.length())
                .append(']')
                .toString();
    }

}
