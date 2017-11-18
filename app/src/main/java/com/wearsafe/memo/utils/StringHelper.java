package com.wearsafe.memo.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ali on 05-Nov-17.
 */

public class StringHelper {
    //Regular expression used to determine whether a string contains HTML or not
    public static final String HTML_CHARS_REGEX = "[<>/:&+%;\"]|\\.\\w{2,4}";
    //This enum is used when determine whether a string is empty, contains HTML or it is valid
    public enum INPUT_VALIDITY {
        VALID,
        EMPTY,
        CONTAINS_HTML
    }

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

    /**
     *
     * @param input input string
     * @return an enum value specifying whether a string is empty, contains HTML or it is valid
     */
    public static INPUT_VALIDITY validateInput(String input){
        if(input==null || input.length()==0)
            return INPUT_VALIDITY.EMPTY;
        if(containsHTMLTags(input))
            return INPUT_VALIDITY.CONTAINS_HTML;
        return INPUT_VALIDITY.VALID;
    }
}
