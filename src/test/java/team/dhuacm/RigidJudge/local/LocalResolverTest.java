package team.dhuacm.RigidJudge.local;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.Problem;
import team.dhuacm.RigidJudge.model.Solution;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LocalResolverTest {

    public static LocalResolver localResolver;
    public static Solution solution;
    public static String cppCode = "#include <iostream>\n" +
                                "using namespace std;\n" +
                                "int a, b;\n" +
                                "int main() {\n" +
                                "   while (cin >> a >> b) {\n" +
                                "       cout << a + b << endl;\n" +
                                "   }\n" +
                                "   return 0;\n" +
                                "}";
    public static String cppCode_TLE = "#include <iostream>\n" +
            "using namespace std;\n" +
            "int a, b;\n" +
            "int main() {\n" +
            "   while (true) {}\n" +
            "   while (cin >> a >> b) {\n" +
            "       cout << a + b << endl;\n" +
            "   }\n" +
            "   return 0;\n" +
            "}";
    public static String cppCode_RE = "#include <iostream>\n" +
            "using namespace std;\n" +
            "int a, b, c[1];\n" +
            "int main() {\n" +
            "   c[-1000] = 1;\n" +
            "   while (cin >> a >> b) {\n" +
            "       cout << a + b << endl;\n" +
            "   }\n" +
            "   return 0;\n" +
            "}";
    public static String cppCode_PE = "#include <iostream>\n" +
            "using namespace std;\n" +
            "int a, b;\n" +
            "int main() {\n" +
            "   while (cin >> a >> b) {\n" +
            "       cout << a + b << \" \" << endl;\n" +
            "   }\n" +
            "   return 0;\n" +
            "}";
    public static String cppCode_WA = "#include <iostream>\n" +
            "using namespace std;\n" +
            "int a, b;\n" +
            "int main() {\n" +
            "   while (cin >> a >> b) {\n" +
            "       cout << a - b << \" \" << endl;\n" +
            "   }\n" +
            "   return 0;\n" +
            "}";
    public static String cCode = "#include <stdio.h>\n" +
                                "int a, b;\n" +
                                "int main() {\n" +
                                "   while(~scanf(\"%d%d\", &a, &b)) {\n" +
                                "       printf(\"%d\\n\", a+b);\n" +
                                "   }\n" +
                                "   return 0;\n" +
                                "}";
    public static String javaCode = "";

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

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

        // C++ Accept
        solution = new Solution(1, problem, cppCode, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept);

        // C++ Time_Limit_Exceeded
        solution = new Solution(2, problem, cppCode_TLE, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Time_Limit_Exceeded);

        // C++ Runtime_Error
        solution = new Solution(3, problem, cppCode_RE, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Runtime_Error);

        // C++ Presentation_Error
        solution = new Solution(4, problem, cppCode_PE, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Presentation_Error);

        // C++ Wrong_Answer
        solution = new Solution(5, problem, cppCode_WA, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);

        // C++ Compile_Error
        solution = new Solution(6, problem, cppCode.substring(0, 30), Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Compile_Error);

        // C Accept
        solution = new Solution(7, problem, cCode, Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept);

        // C Compile_Error
        solution = new Solution(8, problem, cCode.substring(0, 30), Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Compile_Error);
    }
}