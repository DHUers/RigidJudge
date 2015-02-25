package team.dhuacm.RigidJudge.remote;

import org.junit.Test;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.OJ;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.RemoteProblem;
import team.dhuacm.RigidJudge.model.Solution;

import static org.junit.Assert.assertEquals;

public class RemoteResolverTest {

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
        Solution solution = new Solution(1, new RemoteProblem(1, OJ.UVA, "100"), cppCode, Language.CPP);
        RemoteResolver remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);

        // POJ C++
        solution = new Solution(2, new RemoteProblem(1, OJ.POJ, "1000"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // HDU C++
        solution = new Solution(3, new RemoteProblem(1, OJ.HDU, "1000"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // ZOJ C++
        solution = new Solution(4, new RemoteProblem(1, OJ.ZOJ, "1001"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // UVaLive C++
        solution = new Solution(5, new RemoteProblem(1, OJ.UVALIVE, "2000"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);

        // SGU C++
        solution = new Solution(6, new RemoteProblem(1, OJ.SGU, "100"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // SPOJ C++
        solution = new Solution(7, new RemoteProblem(1, OJ.SPOJ, "TEST"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);

        // AIZU C++
        solution = new Solution(8, new RemoteProblem(1, OJ.AIZU, "1000"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // Ural C++
        solution = new Solution(9, new RemoteProblem(1, OJ.URAL, "1000"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);
/*  Turn off Codeforces test due to account security protection
        // Codeforces C++
        solution = new Solution(10, new RemoteProblem(1, OJ.CODEFORCES, "505A"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);

        // CF::Gym C++
        solution = new Solution(11, new RemoteProblem(1, OJ.CF_GYM, "100548A"), cppCode, Language.CPP);
        remoteResolver = new RemoteResolver(solution);
        remoteResolver.handle();
        assertEquals(solution.getResult(), Result.Wrong_Answer);
*/
    }
}