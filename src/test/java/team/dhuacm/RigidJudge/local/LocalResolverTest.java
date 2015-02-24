package team.dhuacm.RigidJudge.local;

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
            "int main(int argc, char *args[]) {\n" +
            "   FILE *f_in = fopen(args[1], \"r\");\n" +
            "   FILE *f_out = fopen(args[2], \"r\");\n" +
            "   FILE *f_user = fopen(args[3], \"r\");\n" +
            "   int ret = 0;  // 0-AC, 1-WA, 2-PE, -1-JE\n" +
            "   int a, b, c;\n" +
            "   while (fscanf(f_in, \"%d %d\", &a, &b) != EOF) {\n" +
            "       fscanf(f_user, \"%d\", &c);\n" +
            "       if (a + b != c) {\n" +
            "           printf(\"%d + %d != %d\\n\", a, b, c);\n" +
            "           ret = 1;\n" +
            "           break;\n" +
            "       }\n" +
            "   }\n" +
            "   fclose(f_in);\n" +
            "   fclose(f_out);\n" +
            "   fclose(f_user);\n" +
            "   return ret;\n" +
            "}\n";
    private static final String cppCode_SPJ_1 = "#include <iostream>\n" +
            "#include <fstream>\n" +
            "using namespace std;\n" +
            "\n" +
            "int main(int argc, char *args[]) {\n" +
            "    ifstream in(args[1]);\n" +
            "    ifstream out(args[2]);\n" +
            "    ifstream user(args[3]);\n" +
            "    int ret = 0;  // 0-AC, 1-WA, 2-PE, -1-JE\n" +
            "    int a, b, c;\n" +
            "    while (in >> a >> b) {\n" +
            "        user >> c;\n" +
            "        if (a + b != c) {\n" +
            "            cout << a << \" + \" << b << \" != \" << c << endl;\n" +
            "            ret = 1;\n" +
            "            break;\n" +
            "        }\n" +
            "    }\n" +
            "    in.close();\n" +
            "    out.close();\n" +
            "    user.close();\n" +
            "    return ret;\n" +
            "}\n";
    private static final String javaCode_SPJ_1 = "import java.io.File;\n" +
            "import java.util.Scanner;\n" +
            "\n" +
            "public class spj {  // class name must be 'spj', do NOT change.\n" +
            "    public static void main(String args[]) {\n" +
            "        try {\n" +
            "            Scanner in = new Scanner(new File(args[0]));\n" +
            "            Scanner out = new Scanner(new File(args[1]));\n" +
            "            Scanner user = new Scanner(new File(args[2]));\n" +
            "            int ret = 0;  // 0-AC, 1-WA, 2-PE, -1-JE\n" +
            "            while (in.hasNext()) {\n" +
            "                int a = in.nextInt(), b = in.nextInt();\n" +
            "                int c = user.nextInt();\n" +
            "                if (a + b != c) {\n" +
            "                    System.out.printf(\"%d + %d != %d\\n\", a, b, c);\n" +
            "                    ret = 1;\n" +
            "                    break;\n" +
            "                }\n" +
            "            }\n" +
            "            System.exit(ret);\n" +
            "        } catch (Exception e) {\n" +
            "            System.exit(-1);\n" +
            "        }\n" +
            "    }\n" +
            "}";
    private static final String javaCode = "import java.util.Scanner;\n" +
            "\n" +
            "public class Main {\n" +
            "   public static void main(String[] args) {\n" +
            "      Scanner in = new Scanner(System.in);\n" +
            "      while (in.hasNextInt()) {\n" +
            "         int a = in.nextInt();\n" +
            "         int b = in.nextInt();\n" +
            "         System.out.println(a + b);\n" +
            "      }\n" +
            "   }\n" +
            "}";
    private static final String javaCode_WA = "import java.util.Scanner;\n" +
            "\n" +
            "public class Main {\n" +
            "   public static void main(String[] args) {\n" +
            "      Scanner in = new Scanner(System.in);\n" +
            "      while (in.hasNextInt()) {\n" +
            "         int a = in.nextInt();\n" +
            "         int b = in.nextInt();\n" +
            "         System.out.println(a - b);\n" +
            "      }\n" +
            "   }\n" +
            "}";
    private static final String javaCode_TLE = "import java.util.Scanner;\n" +
            "\n" +
            "public class Main {\n" +
            "   public static void main(String[] args) {\n" +
            "      Scanner in = new Scanner(System.in);\n" +
            "      while (in.hasNextInt()) {\n" +
            "         int a = in.nextInt();\n" +
            "         int b = in.nextInt();\n" +
            "         System.out.println(a - b);\n" +
            "      }\n" +
            "      while (true) {}\n" +
            "   }\n" +
            "}";
    private static final String javaCode_RE = "import java.util.Scanner;\n" +
            "\n" +
            "public class Main {\n" +
            "   public static void main(String[] args) {\n" +
            "      Scanner in = new Scanner(System.in);\n" +
            "      while (in.hasNextInt()) {\n" +
            "         int a = in.nextInt();\n" +
            "         int b = in.nextInt();\n" +
            "         System.out.println(a - b / 0);\n" +
            "      }\n" +
            "   }\n" +
            "}";
    private static final String javaCode_RE_Malicious = "import java.util.Scanner;\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Main {\n" +
            "   public static void main(String[] args) {\n" +
            "   Scanner cin = new Scanner(System.in);\n" +
            "      File file = new File(\"data\");\n" +
            "      if (!file.exists()) {\n" +
            "         file.mkdir();\n" +
            "      }\n" +
            "      while (cin.hasNext()) {\n" +
            "         int a = cin.nextInt(), b = cin.nextInt();\n" +
            "         System.out.println(a + b);\n" +
            "      }\n" +
            "   }\n" +
            "}";
    private static final String javaCode_MLE = "import java.util.Scanner;\n" +
            "\n" +
            "public class Main {\n" +
            "   public static long[] c = new long[4110000], d = new long[2660000], e = new long[2500000];\n" +
            "\n" +
            "   public static void main(String[] args) {\n" +
            "      Scanner cin = new Scanner(System.in);\n" +
            "      while (cin.hasNext()) {\n" +
            "         int a = cin.nextInt(), b = cin.nextInt();\n" +
            "         System.out.println(a + b);\n" +
            "      }\n" +
            "   }\n" +
            "}";

    @Test
    public void testRun() throws Exception {
        Map<Language, Integer> timeLimit = new HashMap<Language, Integer>();
        Map<Language, Integer> memoryLimit = new HashMap<Language, Integer>();
        timeLimit.put(Language.DEFAULT, 1000);
        timeLimit.put(Language.JAVA, 3000);
        memoryLimit.put(Language.DEFAULT, 65535);
        memoryLimit.put(Language.JAVA, 70000);
        Problem problem = new LocalProblem(1, "test.in", "test.out", "all", timeLimit, memoryLimit);
        Problem problem_spj_c = new LocalSpecialProblem(2, "test.in", "test.out", "all", timeLimit, memoryLimit, cCode_SPJ_1, Language.C);
        Problem problem_spj_cpp = new LocalSpecialProblem(3, "test.in", "test.out", "all", timeLimit, memoryLimit, cppCode_SPJ_1, Language.CPP);
        Problem problem_spj_java = new LocalSpecialProblem(4, "test.in", "test.out", "all", timeLimit, memoryLimit, javaCode_SPJ_1, Language.JAVA);

        // C++ SPJ(C) Accept_Answer
        Solution solution = new Solution(1, problem_spj_c, cppCode, Language.CPP);
        LocalResolver localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C++ SPJ(C) Wrong_Answer
        solution = new Solution(2, problem_spj_c, cppCode_WA, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);

        // C++ SPJ(C++) Accept_Answer
        solution = new Solution(1, problem_spj_cpp, cppCode, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C++ SPJ(C++) Wrong_Answer
        solution = new Solution(2, problem_spj_cpp, cppCode_WA, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);

        // C++ SPJ(Java) Accept_Answer
        solution = new Solution(1, problem_spj_java, cppCode, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C++ SPJ(Java) Wrong_Answer
        solution = new Solution(2, problem_spj_java, cppCode_WA, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);

        // C++ Accept_Answer
        solution = new Solution(1, problem, cppCode, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

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

        // C++ Memory_Limit_Exceeded
        solution = new Solution(7, problem, cppCode_MLE, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Memory_Limit_Exceeded);

        // C Accept_Answer
        solution = new Solution(1, problem, cCode, Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C Compile_Error
        solution = new Solution(2, problem, cCode.substring(0, 30), Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Compile_Error);

        // Java Accept_Answer
        solution = new Solution(1, problem, javaCode, Language.JAVA);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // Java Wrong_Answer
        solution = new Solution(2, problem, javaCode_WA, Language.JAVA);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);

        // Java Time_Limit_Exceeded
        solution = new Solution(3, problem, javaCode_TLE, Language.JAVA);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Time_Limit_Exceeded);

        // Java Runtime_Error
        solution = new Solution(4, problem, javaCode_RE, Language.JAVA);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Runtime_Error);

        // Java Runtime_Error (Malicious)
        solution = new Solution(5, problem, javaCode_RE_Malicious, Language.JAVA);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Runtime_Error);

        // Java Memory_Limit_Exceeded
        solution = new Solution(6, problem, javaCode_MLE, Language.JAVA);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Memory_Limit_Exceeded);
    }
}