package team.dhuacm.RigidJudge.local;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.Solution;

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
        // C++ Accept
        solution = new Solution(1, new LocalProblem(1, "full_text", "test.in", "test.out", null, null), cppCode, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Queue);

        // C++ Compile_Error
        solution = new Solution(1, new LocalProblem(1, "full_text", "test.in", "test.out", null, null), cppCode.substring(0, 30), Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Compile_Error);

        // C Accept
        solution = new Solution(1, new LocalProblem(1, "full_text", "test.in", "test.out", null, null), cCode, Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Queue);

        // C Compile_Error
        solution = new Solution(1, new LocalProblem(1, "full_text", "test.in", "test.out", null, null), cCode.substring(0, 30), Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Compile_Error);
    }
}