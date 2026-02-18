package oj.constant;

public class LanguageConstants {
    private LanguageConstants() {
        throw new AssertionError("常量类禁止实例化");
    }

    public static final Integer LANG_C = 1;
    public static final Integer LANG_CPP = 2;
    public static final Integer LANG_JAVA = 4;
    public static final Integer LANG_PYTHON = 10;
    public static final Integer LANG_CSHARP = 22;

    public static final java.util.Map<String, Integer> LANGUAGE_NAME_TO_ID = new java.util.HashMap<>();

    static {
        LANGUAGE_NAME_TO_ID.put("C", LANG_C);
        LANGUAGE_NAME_TO_ID.put("CPP", LANG_CPP);
        LANGUAGE_NAME_TO_ID.put("Java", LANG_JAVA);
        LANGUAGE_NAME_TO_ID.put("JAVA", LANG_JAVA);
        LANGUAGE_NAME_TO_ID.put("Python", LANG_PYTHON);
        LANGUAGE_NAME_TO_ID.put("PYTHON", LANG_PYTHON);
        LANGUAGE_NAME_TO_ID.put("CSharp", LANG_CSHARP);
        LANGUAGE_NAME_TO_ID.put("CSHARP", LANG_CSHARP);
    }
}