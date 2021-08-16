package co.osp.base.parsingpltool.utils;

public class StringUtils {

    public static long countCharacter(String str, char target) {
        return str.chars().filter(c -> c == target).count();
    }

    public static long countCharacter(Double d, char target) {
        String str = d + "";
        return countCharacter(str, target);
    }

}
