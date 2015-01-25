package team.dhuacm.RigidJudge.remote;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.OJ;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.RemoteProblem;
import team.dhuacm.RigidJudge.model.Solution;

import static org.junit.Assert.assertEquals;

public class RemoteResolverTest {

    private static RemoteResolver remoteResolver;
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

    @Test
    public void testRun() throws Exception {
        // UVa C++
        RemoteResolverTest.solution = new Solution(1, new RemoteProblem(1, OJ.UVA, "100"), RemoteResolverTest.cppCode, Language.CPP);
        RemoteResolverTest.remoteResolver = new RemoteResolver(RemoteResolverTest.solution);
        RemoteResolverTest.remoteResolver.handle();
        assertEquals(RemoteResolverTest.solution.getResult(), Result.Wrong_Answer);

        // POJ C++
        RemoteResolverTest.solution = new Solution(2, new RemoteProblem(1, OJ.POJ, "1000"), RemoteResolverTest.cppCode, Language.CPP);
        RemoteResolverTest.remoteResolver = new RemoteResolver(RemoteResolverTest.solution);
        RemoteResolverTest.remoteResolver.handle();
        assertEquals(RemoteResolverTest.solution.getResult(), Result.Accept);

        // HDU C++
        RemoteResolverTest.solution = new Solution(3, new RemoteProblem(1, OJ.HDU, "1000"), RemoteResolverTest.cppCode, Language.CPP);
        RemoteResolverTest.remoteResolver = new RemoteResolver(RemoteResolverTest.solution);
        RemoteResolverTest.remoteResolver.handle();
        assertEquals(RemoteResolverTest.solution.getResult(), Result.Accept);

        // ZOJ C++
        RemoteResolverTest.solution = new Solution(4, new RemoteProblem(1, OJ.ZOJ, "1001"), RemoteResolverTest.cppCode, Language.CPP);
        RemoteResolverTest.remoteResolver = new RemoteResolver(RemoteResolverTest.solution);
        RemoteResolverTest.remoteResolver.handle();
        assertEquals(RemoteResolverTest.solution.getResult(), Result.Accept);

        // UVaLive C++
        RemoteResolverTest.solution = new Solution(5, new RemoteProblem(1, OJ.UVALIVE, "2000"), RemoteResolverTest.cppCode, Language.CPP);
        RemoteResolverTest.remoteResolver = new RemoteResolver(RemoteResolverTest.solution);
        RemoteResolverTest.remoteResolver.handle();
        assertEquals(RemoteResolverTest.solution.getResult(), Result.Wrong_Answer);

        // SGU C++
        RemoteResolverTest.solution = new Solution(6, new RemoteProblem(1, OJ.SGU, "100"), RemoteResolverTest.cppCode, Language.CPP);
        RemoteResolverTest.remoteResolver = new RemoteResolver(RemoteResolverTest.solution);
        RemoteResolverTest.remoteResolver.handle();
        assertEquals(RemoteResolverTest.solution.getResult(), Result.Accept);
/*
        // SPOJ C++
        solution = new Solution(7, new RemoteProblem(1, OJ.SPOJ, "100"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);

        // CF C++
        solution = new Solution(8, new RemoteProblem(1, OJ.CF, "100"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);

        // AIZU C++
        solution = new Solution(9, new RemoteProblem(1, OJ.AIZU, "100"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);
*/
    }
}