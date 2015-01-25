package team.dhuacm.RigidJudge.local;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.LocalSpecialProblem;
import team.dhuacm.RigidJudge.model.Problem;
import team.dhuacm.RigidJudge.model.Solution;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LocalResolverTest {

    private static LocalResolver localResolver;
    private static Solution solution;
    private static final String cppCode = "#include <iostream>\n" +
                                "using namespace std;\n" +
                                "int a, b;\n" +
                                "int main() {\n" +
                                "   while (cin >> a >> b) {\n" +
                                "       cout << a + b << endl;\n" +
                                "   }\n" +
                                "   return 0;\n" +
                                "}";
    private static final String cppCode_TLE = "#include <iostream>\n" +
            "using namespace std;\n" +
            "int a, b;\n" +
            "int main() {\n" +
            "   while (true) {}\n" +
            "   while (cin >> a >> b) {\n" +
            "       cout << a + b << endl;\n" +
            "   }\n" +
            "   return 0;\n" +
            "}";
    private static final String cppCode_RE = "#include <iostream>\n" +
            "using namespace std;\n" +
            "int a, b, c[1];\n" +
            "int main() {\n" +
            "   c[-1000] = 1/0;\n" +
            "   while (cin >> a >> b) {\n" +
            "       cout << a + b << endl;\n" +
            "   }\n" +
            "   return 0;\n" +
            "}";
    private static final String cppCode_PE = "#include <iostream>\n" +
            "using namespace std;\n" +
            "int a, b;\n" +
            "int main() {\n" +
            "   while (cin >> a >> b) {\n" +
            "       cout << a + b << \" \" << endl;\n" +
            "   }\n" +
            "   return 0;\n" +
            "}";
    private static final String cppCode_WA = "#include <iostream>\n" +
            "using namespace std;\n" +
            "int a, b;\n" +
            "int main() {\n" +
            "   while (cin >> a >> b) {\n" +
            "       cout << a - b << endl;\n" +
            "   }\n" +
            "   return 0;\n" +
            "}";
    private static final String cppCode_MLE = "#include <iostream>\n" +
            "#include <cstring>\n" +
            "using namespace std;\n" +
            "int a, b;\n" +
            "long long c[3110000], d[2660000], e[2500000];\n" +
            "int main() {\n" +
            "   memset(c, 0, sizeof(c));\n" +
            "   memset(d, 0, sizeof(d));\n" +
            "   memset(e, 0, sizeof(e));\n" +
            "   for (int i = 0; i < 1000000; i++) {\n" +
            "       c[i] = i;\n" +
            "   }\n" +
            "   while (cin >> a >> b) {\n" +
            "       cout << a + b << endl;\n" +
            "   }\n" +
            "   return 0;\n" +
            "}";
    private static final String cCode = "#include <stdio.h>\n" +
                                "int a, b;\n" +
                                "int main() {\n" +
                                "   while(~scanf(\"%d%d\", &a, &b)) {\n" +
                                "       printf(\"%d\\n\", a+b);\n" +
                                "   }\n" +
                                "   return 0;\n" +
                                "}";
    private static final String cCode_SPJ_1 = "#include <stdio.h>\n" +
                                    "int main(int argc,char *args[]) {\n" +
                                    "   FILE *f_in = fopen(args[1], \"r\");\n" +
                                    "   FILE *f_out = fopen(args[2], \"r\");\n" +
                                    "   FILE *f_user = fopen(args[3], \"r\");\n" +
                                    "   int ret = 0;  // 0-AC, 1-WA, 2-PE, -1-JE\n" +
                                    "   int a, b, c;\n" +
                                    "   while (fscanf(f_in, \"%d %d\", &a, &b) != EOF) {\n" +
                                    "       fscanf(f_user, \"%d\", &c);\n" +
                                    "       if (a + b != c) {\n" +
                                    "           printf(\"%d %d %d\\n\", a, b, c);\n" +
                                    "           ret = 1;\n" +
                                    "           break;\n" +
                                    "       }\n" +
                                    "   }\n" +
                                    "   fclose(f_in);\n" +
                                    "   fclose(f_out);\n" +
                                    "   fclose(f_user);\n" +
                                    "   return ret;\n" +
                                    "}";

    @Test
    public void testRun() throws Exception {
        Map<Language, Integer> timeLimit = new HashMap<Language, Integer>();
        Map<Language, Integer> memoryLimit = new HashMap<Language, Integer>();
        timeLimit.put(Language.C, 1000);
        timeLimit.put(Language.CPP, 1000);
        timeLimit.put(Language.JAVA, 3000);
        memoryLimit.put(Language.C, 65535);
        memoryLimit.put(Language.CPP, 65535);
        memoryLimit.put(Language.JAVA, 65535);
        Problem problem = new LocalProblem(1, "full_text", "test.in", "test.out", timeLimit, memoryLimit);
        Problem problem_spj = new LocalSpecialProblem(2, "special_judge", "test.in", "test.out", timeLimit, memoryLimit, LocalResolverTest.cCode_SPJ_1, Language.C);

        // C++ SPJ(C) Accept
        LocalResolverTest.solution = new Solution(1, problem_spj, LocalResolverTest.cppCode, Language.CPP);
        LocalResolverTest.localResolver = new LocalResolver(LocalResolverTest.solution);
        LocalResolverTest.localResolver.handle();
        assertEquals(LocalResolverTest.solution.getResult(), Result.Accept);

        // C++ SPJ(C) Wrong_Answer
        LocalResolverTest.solution = new Solution(2, problem_spj, LocalResolverTest.cppCode_WA, Language.CPP);
        LocalResolverTest.localResolver = new LocalResolver(LocalResolverTest.solution);
        LocalResolverTest.localResolver.handle();
        assertEquals(LocalResolverTest.solution.getResult(), Result.Wrong_Answer);

        // C++ Accept
        LocalResolverTest.solution = new Solution(1, problem, LocalResolverTest.cppCode, Language.CPP);
        LocalResolverTest.localResolver = new LocalResolver(LocalResolverTest.solution);
        LocalResolverTest.localResolver.handle();
        assertEquals(LocalResolverTest.solution.getResult(), Result.Accept);

        // C++ Time_Limit_Exceeded
        LocalResolverTest.solution = new Solution(2, problem, LocalResolverTest.cppCode_TLE, Language.CPP);
        LocalResolverTest.localResolver = new LocalResolver(LocalResolverTest.solution);
        LocalResolverTest.localResolver.handle();
        assertEquals(LocalResolverTest.solution.getResult(), Result.Time_Limit_Exceeded);

        // C++ Runtime_Error
        LocalResolverTest.solution = new Solution(3, problem, LocalResolverTest.cppCode_RE, Language.CPP);
        LocalResolverTest.localResolver = new LocalResolver(LocalResolverTest.solution);
        LocalResolverTest.localResolver.handle();
        assertEquals(LocalResolverTest.solution.getResult(), Result.Runtime_Error);

        // C++ Presentation_Error
        LocalResolverTest.solution = new Solution(4, problem, LocalResolverTest.cppCode_PE, Language.CPP);
        LocalResolverTest.localResolver = new LocalResolver(LocalResolverTest.solution);
        LocalResolverTest.localResolver.handle();
        assertEquals(LocalResolverTest.solution.getResult(), Result.Presentation_Error);

        // C++ Wrong_Answer
        LocalResolverTest.solution = new Solution(5, problem, LocalResolverTest.cppCode_WA, Language.CPP);
        LocalResolverTest.localResolver = new LocalResolver(LocalResolverTest.solution);
        LocalResolverTest.localResolver.handle();
        assertEquals(LocalResolverTest.solution.getResult(), Result.Wrong_Answer);

        // C++ Compile_Error
        LocalResolverTest.solution = new Solution(6, problem, LocalResolverTest.cppCode.substring(0, 30), Language.CPP);
        LocalResolverTest.localResolver = new LocalResolver(LocalResolverTest.solution);
        LocalResolverTest.localResolver.handle();
        assertEquals(LocalResolverTest.solution.getResult(), Result.Compile_Error);

        // C++ Memory_Limit_Exceeded
        LocalResolverTest.solution = new Solution(7, problem, LocalResolverTest.cppCode_MLE, Language.CPP);
        LocalResolverTest.localResolver = new LocalResolver(LocalResolverTest.solution);
        LocalResolverTest.localResolver.handle();
        assertEquals(LocalResolverTest.solution.getResult(), Result.Memory_Limit_Exceeded);

        // C Accept
        LocalResolverTest.solution = new Solution(8, problem, LocalResolverTest.cCode, Language.C);
        LocalResolverTest.localResolver = new LocalResolver(LocalResolverTest.solution);
        LocalResolverTest.localResolver.handle();
        assertEquals(LocalResolverTest.solution.getResult(), Result.Accept);

        // C Compile_Error
        LocalResolverTest.solution = new Solution(9, problem, LocalResolverTest.cCode.substring(0, 30), Language.C);
        LocalResolverTest.localResolver = new LocalResolver(LocalResolverTest.solution);
        LocalResolverTest.localResolver.handle();
        assertEquals(LocalResolverTest.solution.getResult(), Result.Compile_Error);
    }
}