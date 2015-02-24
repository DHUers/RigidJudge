package team.dhuacm.RigidJudge.local;

import org.junit.Test;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.Problem;
import team.dhuacm.RigidJudge.model.Solution;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LocalResolverTest_Extra {

    private static final String cCode_01 = "#include <stdio.h>\n" +
            "int main()\n" +
            "{\n" +
            "    double aver;\n" +
            "    int sum, num, temp, i;\n" +
            "    while(scanf(\"%d\", &num)== 1)\n" +
            "    {\n" +
            "        sum = 0;\n" +
            "        aver = 0;\n" +
            "        i = num;\n" +
            "        for(; i>0; i--)\n" +
            "        {\n" +
            "            scanf(\"%d\", &temp);\n" +
            "            sum += temp;\n" +
            "        }\n" +
            "        if(num != 0)\n" +
            "            aver = (double)sum / num;\n" +
            "        printf(\"%f\\n\", aver);\n" +
            "    }\n" +
            "    return 0;\n" +
            "}\n";
    private static final String cCode_02 = "#include <stdio.h>\n" +
            "int main()\n" +
            "{\n" +
            "    unsigned long i, n, term, sum;\n" +
            "    while(scanf(\"%lu\", &n)!=EOF)\n" +
            "    {\n" +
            "        term = 1;\n" +
            "        sum = 0;\n" +
            "        for(i=1; ; i++)\n" +
            "        {\n" +
            "            term *= i;\n" +
            "            sum += term;\n" +
            "            if(sum >= n)\n" +
            "                break;\n" +
            "        }\n" +
            "        printf(\"%d\\n\", i-1);\n" +
            "    }\n" +
            "    return 0;\n" +
            "}\n";
    private static final String cCode_03 = "#include <stdio.h> \n" +
            "\n" +
            "int main() \n" +
            "{ \n" +
            "\tint xr,yr,w,h,x,y; \n" +
            "\t\n" +
            "\twhile(scanf( \"%d%d%d%d\",&xr,&yr,&w, &h) != EOF)\n" +
            "\t{\n" +
            "\t\tscanf( \"%d%d\",&x, &y);\n" +
            "\t\tif (x> xr && x< xr+w && y > yr && y< yr+h)\n" +
            "\t\t\tprintf(\"yes\\n\");\n" +
            "\t\telse\n" +
            "\t\t    printf(\"no\\n\");\n" +
            "\t}\n" +
            "\t\n" +
            "\treturn 0; \n" +
            "}";
    private static final String cCode_04 = "#include<stdio.h>\n" +
            "int M,count,flag;\n" +
            "void move(int n,char a,char b,char c)\n" +
            "{\n" +
            "\tif (n>0)\n" +
            "\t{\n" +
            "\t\tmove(n-1,a,c,b);\n" +
            "\t\tcount ++;\n" +
            "\t\tif (count == M)\n" +
            "\t\t{\n" +
            "\t\t\tprintf(\"%c--%c\\n\",a,b);\n" +
            "\t\t\tflag=1;\n" +
            "\t\t}\n" +
            "\t\tmove(n-1,c,b,a);\n" +
            "\t}\n" +
            "}\n" +
            "int main()\n" +
            "{\n" +
            "\tint n,m;\n" +
            "\twhile(scanf(\"%d%d\",&n,&m)!= EOF)\n" +
            "\t{\n" +
            "\t\tM=m;\n" +
            "\t\tcount = 0;\n" +
            "\t\tflag=0;\n" +
            "\t\tif (m<=0)\n" +
            "\t\t\tprintf(\"none\\n\");\n" +
            "\t\telse\n" +
            "\t\t{\n" +
            "\t\t\tmove(n,'A','C','B');\n" +
            "\t\t\tif (flag == 0)\n" +
            "\t\t\t\tprintf(\"none\\n\");\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\treturn 0;\n" +
            "}";
    private static final String cCode_05 = "#include <stdio.h>\n" +
            "#define N 10\n" +
            "\n" +
            "void trans(int a[][N],int b[][N],int n)\n" +
            "{\n" +
            "\tint i,j;\n" +
            "\n" +
            "    for(i=0;i<n;i++)\n" +
            "        for(j=0;j<n;j++)\n" +
            "             b[j][i]=a[i][j];\n" +
            "}\n" +
            "\n" +
            "void add(int a[][N],int b[][N],int n)\n" +
            "{\n" +
            "\tint i,j;\n" +
            "\n" +
            "    for(i=0;i<n;i++)\n" +
            "        for(j=0;j<n;j++)\n" +
            "             a[i][j]+=b[i][j];\n" +
            "}\n" +
            "\n" +
            "int main()\n" +
            "{\n" +
            "\tint a[N][N],b[N][N];\n" +
            "\tint i,j,n;\n" +
            "\n" +
            "\twhile(scanf(\"%d\",&n)!=EOF)\n" +
            "    {\n" +
            "        for(i=0; i<n; i++)\n" +
            "            for(j=0;j<n;j++)\n" +
            "                scanf(\"%d\",&a[i][j]);\n" +
            "        trans(a,b,n);\n" +
            "        add(a,b,n);\n" +
            "        for(i=0; i<n; i++)\n" +
            "        {\n" +
            "            for(j=0;j<n;j++)\n" +
            "                 printf(\"%d\\t\",a[i][j]);\n" +
            "            printf(\"\\n\");\n" +
            "        }\n" +
            "    }\n" +
            "    return 0;\n" +
            " }";
    private static final String cCode_06 = "#include <stdio.h>\n" +
            "#include <stdlib.h>\n" +
            "\n" +
            "struct DATA\n" +
            "{\n" +
            "\tint data;\n" +
            "\tstruct DATA *next;\n" +
            "};\n" +
            "typedef struct DATA Data;\n" +
            "\n" +
            "int main()\n" +
            "{\n" +
            "\tData *head=NULL, *p, *q1, *q2;\n" +
            "\tint number,n,i;\n" +
            "\n" +
            "    while(scanf(\"%d\", &number)!=EOF)\n" +
            "    {\n" +
            "        for(i=0;i<number;i++)\n" +
            "        {\n" +
            "            scanf(\"%d\", &n);\n" +
            "            p=(Data *)malloc(sizeof(Data));\n" +
            "            p->data = n;\n" +
            "            if(head == NULL)\n" +
            "            {\n" +
            "                head=p;\n" +
            "                head->next = NULL;\n" +
            "            }\n" +
            "            else\n" +
            "            {\n" +
            "                q2=NULL;\n" +
            "                q1=head;\n" +
            "                while(q1!=NULL && q1->data < n)\n" +
            "                {\n" +
            "                    q2=q1;\n" +
            "                    q1=q1->next;\n" +
            "                }\n" +
            "                if(q1==NULL)\t\t//n是最大的数\n" +
            "                {\n" +
            "                    q2->next = p;\n" +
            "                    p->next =NULL;\n" +
            "                }\n" +
            "                else if(q2==NULL)\t//n是最小的数\n" +
            "                {\n" +
            "                    p->next = head;\n" +
            "                    head = p;\n" +
            "                }\n" +
            "                else\t\t\t\t//n是中间的数\n" +
            "                {\n" +
            "                    q2->next = p;\n" +
            "                    p->next = q1;\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        p=head;\n" +
            "        while(p!=NULL)\n" +
            "        {\n" +
            "    \t\tq1=p;\n" +
            "            printf(\"%d\\t\",p->data);\n" +
            "            p=p->next;\n" +
            "    \t\tfree(q1);\n" +
            "        }\n" +
            "        printf(\"\\n\");\n" +
            "        head = NULL;\n" +
            "    }\n" +
            "    return 0;\n" +
            "}";
    private static final String cCode_07 = "#include<stdio.h>\n" +
            "#include<ctype.h>\n" +
            "#include<string.h>\n" +
            "#include<stdlib.h>\n" +
            "\n" +
            "int compare(const void *a, const void *b)\n" +
            "{\n" +
            "    return strcmp((char*)a, (char*)b);\n" +
            "}\n" +
            "\n" +
            "int main()\n" +
            "{\n" +
            "    char line[300];\n" +
            "    while( gets(line)!=NULL )\n" +
            "    {\n" +
            "        char words[10][30]={{0}};\n" +
            "        int cnt=0;\n" +
            "        int len=strlen(line);\n" +
            "        int i;\n" +
            "        for( i=0; i<len; i++ ){\n" +
            "            if( isalpha(line[i]) ){\n" +
            "                int j=0;\n" +
            "                do{\n" +
            "                    words[cnt][j]=line[i];\n" +
            "                    i++;\n" +
            "                    j++;\n" +
            "                }while( isalpha(line[i]) );\n" +
            "                cnt++;\n" +
            "            }\n" +
            "        }\n" +
            "        qsort(words, cnt, 30, compare);\n" +
            "        printf(\"%d\\n\", cnt);\n" +
            "        for(i=0; i<cnt; i++)\n" +
            "        {\n" +
            "            if( i )\n" +
            "                printf(\" \");\n" +
            "            printf(\"%s\", words[i]);\n" +
            "        }\n" +
            "        printf(\"\\n\");\n" +
            "    }\n" +
            "    return 0;\n" +
            "}";
    private static final String cCode_08 = "#include <stdio.h>\n" +
            "#include <stdlib.h>\n" +
            "#include <ctype.h>\n" +
            "#include <string.h>\n" +
            "#define SIZE 26\n" +
            "\n" +
            "struct cryptanalysis\n" +
            "{\n" +
            "    char letter;\n" +
            "    int cnt;\n" +
            "}count[SIZE];\n" +
            "\n" +
            "void init();\n" +
            "void calculate(char line[]);\n" +
            "int cmp( const void* a, const void* b );\n" +
            "void output();\n" +
            "\n" +
            "int main()\n" +
            "{\n" +
            "\tint n;\n" +
            "\tchar line[110];\n" +
            "\tint r=0;\n" +
            "\twhile(scanf(\"%d\", &n)!=EOF)\n" +
            "\t{\n" +
            "\t\tinit();\n" +
            "        \tgetchar();\n" +
            "        \tif( r )\n" +
            "            \t\tprintf(\"\\n\");\n" +
            "        \tr++;\n" +
            "        \twhile(n--)\n" +
            "        \t{\n" +
            "            \t\tgets(line);\n" +
            "            \t\tcalculate(line);\n" +
            "        \t}\n" +
            "        \tqsort( count, SIZE, sizeof(count[0]), cmp );\n" +
            "        \toutput();\n" +
            "\t}\n" +
            "\treturn 0;\n" +
            "}\n" +
            "void init()\n" +
            "{\n" +
            "    int i;\n" +
            "    for(i=0; i<SIZE; i++)\n" +
            "    {\n" +
            "        count[i].cnt=0;\n" +
            "        count[i].letter='A'+i;\n" +
            "    }\n" +
            "}\n" +
            "void calculate(char line[])\n" +
            "{\n" +
            "    int i;\n" +
            "    int len=strlen(line);\n" +
            "    for(i=0; i<len; i++)\n" +
            "    {\n" +
            "        if(isalpha(line[i]))\n" +
            "        {\n" +
            "            line[i]=toupper(line[i]);\n" +
            "            count[line[i]-'A'].cnt++;\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "/*按cnt从大到小排序，cnt相等时按letter从小到大排序*/\n" +
            "int cmp( const void* a, const void* b )\n" +
            "{\n" +
            "    struct cryptanalysis* c=(struct cryptanalysis*)a;\n" +
            "    struct cryptanalysis* d=(struct cryptanalysis*)b;\n" +
            "    if(c->cnt != d->cnt)\n" +
            "        return d->cnt - c->cnt;\n" +
            "    else\n" +
            "        return c->letter - d->letter;\n" +
            "}\n" +
            "\n" +
            "void output()\n" +
            "{\n" +
            "    int i;\n" +
            "    for(i=0; i<SIZE; i++)\n" +
            "    {\n" +
            "        if(count[i].cnt)\n" +
            "            printf(\"%c %d\\n\", count[i].letter, count[i].cnt);\n" +
            "    }\n" +
            "}";
    private static final String cppCode_09 = "#include <iostream>\n" +
            "#include <cstring>\n" +
            "#include <cstdio>\n" +
            "using namespace std;\n" +
            "\n" +
            "const int MAXN = 110;\n" +
            "const int INF = 0x3f3f3f3f;\n" +
            "int nCase, n, dp[MAXN][MAXN], value[MAXN], sum[MAXN];\n" +
            "bool visited[MAXN][MAXN];\n" +
            "\n" +
            "void init() {\n" +
            "    sum[0] = 0;\n" +
            "    memset(dp, 0, sizeof(dp));\n" +
            "    memset(visited, false, sizeof(visited));\n" +
            "}\n" +
            "\n" +
            "void input() {\n" +
            "    scanf(\"%d\", &n);\n" +
            "    for (int i = 1; i <= n; i++) {\n" +
            "        scanf(\"%d\", &value[i]);\n" +
            "        sum[i] = sum[i-1] + value[i];\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "int DP(int l, int r) {\n" +
            "    if (l >= r) return 0;\n" +
            "    if (visited[l][r]) return dp[l][r];\n" +
            "    visited[l][r] = true;\n" +
            "\n" +
            "    int ret = -INF;\n" +
            "    for (int i = 1; i <= r-l; i++) {\n" +
            "        ret = max(ret, sum[r] - sum[l] - min(DP(l+i, r), DP(l, r-i)));\n" +
            "    }\n" +
            "    return dp[l][r] = ret;\n" +
            "}\n" +
            "\n" +
            "void solve() {\n" +
            "    printf(\"%d\\n\", DP(0, n) * 2 - sum[n]);\n" +
            "}\n" +
            "\n" +
            "int main() {\n" +
            "    //freopen(\"YK_Game.in\", \"r\", stdin);\n" +
            "    //freopen(\"YK_Game.out\", \"w\", stdout);\n" +
            "    scanf(\"%d\", &nCase);\n" +
            "    while (nCase--) {\n" +
            "        init();\n" +
            "        input();\n" +
            "        solve();\n" +
            "    }\n" +
            "    return 0;\n" +
            "}";
    private static final String cppCode_10 = "#include <cstdio>\n" +
            "using namespace std;\n" +
            "\n" +
            "const int MAXN = 10010;\n" +
            "const int MAXM = 100010;\n" +
            "int nCase, ans[MAXM], father[MAXN], rank[MAXN], cnt, N, M;\n" +
            "\n" +
            "struct Line {\n" +
            "    int x, y;\n" +
            "} line[MAXM];\n" +
            "\n" +
            "void init() {\n" +
            "    for (int i = 0; i < N; i++) {\n" +
            "        father[i] = i;\n" +
            "    }\n" +
            "    cnt = N;\n" +
            "}\n" +
            "\n" +
            "int find(int v) {\n" +
            "    return father[v] = father[v] == v ? v : find(father[v]);\n" +
            "}\n" +
            "\n" +
            "void merge(int x, int y) {\n" +
            "    int a = find(x), b = find(y);\n" +
            "    if (a == b) return;\n" +
            "    if (rank[a] < rank[b]) {\n" +
            "        father[a] = b;\n" +
            "    } else {\n" +
            "        father[b] = a;\n" +
            "        if (rank[b] == rank[a]) {\n" +
            "            rank[a]++;\n" +
            "        }\n" +
            "    }\n" +
            "    cnt--;\n" +
            "}\n" +
            "\n" +
            "void input() {\n" +
            "    scanf(\"%d%d\", &N, &M);\n" +
            "    for (int i = 0; i < M; i++) {\n" +
            "        scanf(\"%d%d\", &line[i].x, &line[i].y);\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "void work() {\n" +
            "    for (int i = M-1; i >= 0; i--) {\n" +
            "        ans[i] = cnt;\n" +
            "        merge(line[i].x, line[i].y);\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "void output() {\n" +
            "    for (int i = 0; i < M; i++) {\n" +
            "        printf(\"%d\\n\", ans[i]);\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "int main() {\n" +
            "    //freopen(\"DH_Network.in\", \"r\", stdin);\n" +
            "    //freopen(\"DH_Network.out\", \"w\", stdout);\n" +
            "    scanf(\"%d\", &nCase);\n" +
            "    while (nCase--) {\n" +
            "        input();\n" +
            "        init();\n" +
            "        work();\n" +
            "        output();\n" +
            "    }\n" +
            "    return 0;\n" +
            "}";

    @Test
    public void testRun() throws Exception {
        Map<Language, Integer> timeLimit = new HashMap<Language, Integer>();
        Map<Language, Integer> memoryLimit = new HashMap<Language, Integer>();
        timeLimit.put(Language.DEFAULT, 1000);
        timeLimit.put(Language.JAVA, 3000);
        memoryLimit.put(Language.DEFAULT, 65535);
        memoryLimit.put(Language.JAVA, 70000);
        Problem[] problems = new Problem[15];
        problems[1] = new LocalProblem(1, "01_test.in", "01_test.out", "all", timeLimit, memoryLimit);
        problems[2] = new LocalProblem(2, "02_test.in", "02_test.out", "all", timeLimit, memoryLimit);
        problems[3] = new LocalProblem(3, "03_test.in", "03_test.out", "all", timeLimit, memoryLimit);
        problems[4] = new LocalProblem(4, "04_test.in", "04_test.out", "all", timeLimit, memoryLimit);
        problems[5] = new LocalProblem(5, "05_test.in", "05_test.out", "all", timeLimit, memoryLimit);
        problems[6] = new LocalProblem(6, "06_test.in", "06_test.out", "all", timeLimit, memoryLimit);
        problems[7] = new LocalProblem(7, "07_test.in", "07_test.out", "all", timeLimit, memoryLimit);
        problems[8] = new LocalProblem(8, "08_test.in", "08_test.out", "all", timeLimit, memoryLimit);
        problems[9] = new LocalProblem(9, "09_test.in", "09_test.out", "all", timeLimit, memoryLimit);
        problems[10] = new LocalProblem(10, "10_test.in", "10_test.out", "all", timeLimit, memoryLimit);

        // C 01 Accept_Answer
        Solution solution = new Solution(1, problems[1], cCode_01, Language.C);
        LocalResolver localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C 02 Accept_Answer
        solution = new Solution(2, problems[2], cCode_02, Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C 03 Accept_Answer
        solution = new Solution(3, problems[3], cCode_03, Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C 04 Accept_Answer
        solution = new Solution(4, problems[4], cCode_04, Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C 05 Accept_Answer
        solution = new Solution(5, problems[5], cCode_05, Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C 06 Accept_Answer
        solution = new Solution(6, problems[6], cCode_06, Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C 07 Accept_Answer
        solution = new Solution(7, problems[7], cCode_07, Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C 08 Accept_Answer
        solution = new Solution(8, problems[8], cCode_08, Language.C);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C++ 09 Accept_Answer
        solution = new Solution(9, problems[9], cppCode_09, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);

        // C++ 10 Accept_Answer
        solution = new Solution(10, problems[10], cppCode_10, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accept_Answer);


        // Real solution data Test
        File[] files = new File("src/test/resources/testdata").listFiles();
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i].getName());
            Language language = Language.valueOf(files[i].getName().split(".")[1]);
            String code = files[i].getContent();  // TODO:
            String[] params = files[i].getName().split(".")[0].split("_");
            Problem problem = problems[Integer.parseInt(params[3])];
        }

    }
}