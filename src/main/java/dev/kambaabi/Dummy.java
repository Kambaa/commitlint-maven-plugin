package dev.kambaabi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dummy {
    public static void main(String[] args) {
        final String regexOld = "^(build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test){1}(\\([\\w\\-\\.]+\\))?(!)?: ([\\w ])+([\\s\\S]*)";

        final String regex = "^(build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test){1}(\\([\\w\\-\\.]+\\))?(!)?: ([\\w ]+)(\\n[\\s\\S]*)?";

        String testCommitMessage = "feat(SCOPE)!: ornek deneme\nseni Allah bildiği gibi yapsın\n";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(testCommitMessage);
        System.out.println("MATCHES: " + m.matches());
        System.out.println("GROUP COUINT: " + m.groupCount());

        for (int i = 0; i <= m.groupCount(); i++) {
            System.out.println(String.format("Matcher Group %d is: [%s]", i, m.group(i)));
        }
    }
}
