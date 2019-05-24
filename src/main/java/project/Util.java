package project;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static List<Integer> checkNesting(String file, String strSearch) {
        List<Integer> nestings = new ArrayList<>();
        file = file.toLowerCase();
        strSearch = strSearch.toLowerCase();
        int length = strSearch.length();
        List<String> quotes = new ArrayList<>();
        for (int i = 0; i < file.length() - length; i ++) {
            String symb = Character.toString(file.charAt(i));
            if (symb.equalsIgnoreCase("{")) {
                quotes.add(symb);
            } else {
                if (symb.equalsIgnoreCase("}")) {
                    quotes.remove(quotes.size() - 1);
                }
            }

            if (file.substring(i, i + length).equalsIgnoreCase(strSearch)) {
                nestings.add(quotes.size());
            }
        }
        return nestings;
    }

    public static Boolean checkInsteadOfChecking(String file, String strSearch, Integer nesting) {
        List<Integer> nestings = checkNesting(file, strSearch);
        if (nestings.isEmpty()) {
            return false;
        }
        for (Integer nest : nestings) {
            if (nest != nesting) return true;
        }

        return false;
    }

    public static Boolean checkOnlyNesting(String file, String strSearch, Integer nesting) {
        List<Integer> nestings = checkNesting(file, strSearch);
        if (nestings.isEmpty()) {
            return false;
        }

        for (Integer nest : nestings) {
            if (nest == nesting) return true;
        }

        return false;
    }

    public static Boolean checkMoreThanNesting(String file, String strSearch, Integer nesting) {
        List<Integer> nestings = checkNesting(file, strSearch);
        if (nestings.isEmpty()) {
            return false;
        }

        for (Integer nest : nestings) {
            if (nest > nesting) return true;
        }

        return false;
    }

    public static Boolean checkLessThanNesting(String file, String strSearch, Integer nesting) {
        List<Integer> nestings = checkNesting(file, strSearch);
        if (nestings.isEmpty()) {
            return false;
        }

        for (Integer nest : nestings) {
            if (nest < nesting) return true;
        }

        return false;
    }

}
