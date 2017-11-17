package com.wearsafe.memo.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ali on 05-Nov-17.
 */

public class StringHelper {
    public static final String HTML_CHARS_REGEX = "[<>/:&+%;\"]|\\.\\w{2,4}";

    /**
     * The below method checks if a given string input contains HTML tags
     * @param input input string
     * @return boolean value true if the input contains HTML tags, otherwise returns false
     */
    public static boolean containsHTMLTags(String input){
        Log.d(StringHelper.class.getSimpleName(),HTML_CHARS_REGEX);
        Pattern p = Pattern.compile(HTML_CHARS_REGEX);
        Matcher m = p.matcher(input);
        boolean b = m.find();
        return b;
    }
}
