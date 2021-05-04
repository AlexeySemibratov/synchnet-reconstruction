package org.nsu.src.reconstruction;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.*;
import java.util.stream.Collectors;

public class PMath {

    public static List<String> getRealCoeefs(char coeffChar, int[] fixedPoint) {
        int m = fixedPoint.length;
        int n = (int) Math.pow(2, m);
        List<String> result = new ArrayList<>();
        boolean toAdd;
        for (int i = 0; i < n; i++) {
            toAdd = true;

            String c = Integer.toBinaryString(i);
            while (c.length() < m) {
                c = '0' + c;
            }
            char[] chars = c.toCharArray();
            for (int k = 0; k < m; k++) {
                if (chars[k] == '1' && fixedPoint[k] == 0) {
                    toAdd = false;
                    break;
                }
            }
            if (toAdd) result.add(buildCoeff(coeffChar, chars));
        }
        return result;
    }

    public static Set<String> getConditions(char coeffChar, int[] condition) {
        int m = condition.length;
        int n = (int) Math.pow(2, m);
        Set<String> result = new HashSet<>();
        for (int i = 0; i < n; i++) {
            StringBuilder c = new StringBuilder(Integer.toBinaryString(i));
            while (c.length() < m) {
                c.insert(0, '0');
            }
            char[] chars = c.toString().toCharArray();

            for (int k = 0; k < m; k++) {
                if (chars[k] == '1' && condition[k] == 0) {
                    String clause = '~' + buildCoeff(coeffChar, chars);
                    result.add(clause);
                    break;
                }
            }
        }
        return result;
    }

    public static Equation buildFullExpression(char coeffChar, String[] args) {
        int m = args.length;
        int n = (int) Math.pow(2, m);
        Equation e = new Equation();
        e.defineArguments(args);
        StringBuilder exprString = new StringBuilder();
        for (int i = 0; i < n; i++) {
            StringBuilder c = new StringBuilder(Integer.toBinaryString(i));
            while (c.length() < m) {
                c.insert(0, '0');
            }
            char[] chars = c.toString().toCharArray();
            String coeff = buildCoeff(coeffChar, chars);
            e.addArguments(new Argument(coeff));

            exprString.append(coeff);
            if (i != 0) exprString.append('&');

            exprString.append(buildArg(chars, args))
                    .append("(+)");
        }
        exprString.delete(exprString.length() - 3, exprString.length());
        e.setExpressionString(exprString.toString());

        return e;
    }

    private static String buildArg(char[] binChars, String[] args) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < binChars.length; i++) {
            if (binChars[i] == '1') result.append(args[i]).append('&');
        }
        result.delete(Math.max(0, result.length() - 1), result.length());
        return result.toString();
    }

    private static String buildCoeff(char coeffChar, char[] binChars) {
        return String.valueOf(coeffChar) + '_' + new String(binChars);
    }

    public static void defineArguments(Expression e, List<String> args) {
        e.defineArguments(args.toArray(String[]::new));
    }

    public static String[] getArguments(Expression e) {
        int n = e.getArgumentsNumber();
        String[] args = new String[n];
        for (int i = 0; i < n; i++) {
            args[i] = e.getArgument(i).getArgumentName();
        }
        return args;
    }

    public static List<String> getArgumentsExcept(Expression e, List<String> excepted) {
        String[] totalArgs = getArguments(e);
        List<String> result = Arrays.asList(totalArgs.clone());
        return result.stream().filter(x -> !excepted.contains(x)).collect(Collectors.toList());
    }

}
