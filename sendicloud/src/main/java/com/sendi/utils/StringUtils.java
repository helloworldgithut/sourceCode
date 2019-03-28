package com.sendi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {


    public static boolean isEmpty(String input) {
        return input == null || "".equals(input);
//
    }


    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

}



