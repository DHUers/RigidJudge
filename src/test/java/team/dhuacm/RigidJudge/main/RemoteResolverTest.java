package team.dhuacm.RigidJudge.main;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import team.dhuacm.RigidJudge.config.DataProvider;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.OJ;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.RemoteProblem;
import team.dhuacm.RigidJudge.model.Solution;

import static org.junit.Assert.*;

public class RemoteResolverTest {

    public static RemoteResolver remoteResolver;
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
                                "       printf(\"%d\n\", a+b);\n" +
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
        // UVa C++
        remoteResolver = new RemoteResolver(new Solution(1, new RemoteProblem(1, OJ.UVA, "100"), cppCode, Language.CPP));
        remoteResolver.run();
        assertEquals(DataProvider.JudgedSolutionQueue.take().getResult(), Result.Wrong_Answer);

        // POJ C++
        remoteResolver = new RemoteResolver(new Solution(1, new RemoteProblem(1, OJ.POJ, "1000"), cppCode, Language.CPP));
        remoteResolver.run();
        assertEquals(DataProvider.JudgedSolutionQueue.take().getResult(), Result.Accept);

        // HDU C++
        remoteResolver = new RemoteResolver(new Solution(1, new RemoteProblem(1, OJ.HDU, "1000"), cppCode, Language.CPP));
        remoteResolver.run();
        assertEquals(DataProvider.JudgedSolutionQueue.take().getResult(), Result.Accept);

        // ZOJ C++
        remoteResolver = new RemoteResolver(new Solution(1, new RemoteProblem(1, OJ.ZOJ, "1001"), cppCode, Language.CPP));
        remoteResolver.run();
        assertEquals(DataProvider.JudgedSolutionQueue.take().getResult(), Result.Accept);

        // UVaLive C++
        remoteResolver = new RemoteResolver(new Solution(1, new RemoteProblem(1, OJ.UVALIVE, "2000"), cppCode, Language.CPP));
        remoteResolver.run();
        assertEquals(DataProvider.JudgedSolutionQueue.take().getResult(), Result.Wrong_Answer);

        // SGU C++
        remoteResolver = new RemoteResolver(new Solution(1, new RemoteProblem(1, OJ.SGU, "100"), cppCode, Language.CPP));
        remoteResolver.run();
        assertEquals(DataProvider.JudgedSolutionQueue.take().getResult(), Result.Accept);
/*
        // SPOJ C++
        remoteResolver = new RemoteResolver(new Solution(1, new RemoteProblem(1, OJ.SPOJ, "100"), cppCode, Language.CPP));
        remoteResolver.run();
        assertEquals(DataProvider.JudgedSolutionQueue.take().getResult(), Result.Wrong_Answer);

        // CF C++
        remoteResolver = new RemoteResolver(new Solution(1, new RemoteProblem(1, OJ.CF, "100"), cppCode, Language.CPP));
        remoteResolver.run();
        assertEquals(DataProvider.JudgedSolutionQueue.take().getResult(), Result.Wrong_Answer);

        // AIZU C++
        remoteResolver = new RemoteResolver(new Solution(1, new RemoteProblem(1, OJ.AIZU, "100"), cppCode, Language.CPP));
        remoteResolver.run();
        assertEquals(DataProvider.JudgedSolutionQueue.take().getResult(), Result.Wrong_Answer);
*/
    }
}