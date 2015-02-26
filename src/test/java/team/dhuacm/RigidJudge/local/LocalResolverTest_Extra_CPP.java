package team.dhuacm.RigidJudge.local;

import org.junit.Test;
import team.dhuacm.RigidJudge.config.Language;
import team.dhuacm.RigidJudge.config.Result;
import team.dhuacm.RigidJudge.model.LocalProblem;
import team.dhuacm.RigidJudge.model.Problem;
import team.dhuacm.RigidJudge.model.Solution;
import team.dhuacm.RigidJudge.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LocalResolverTest_Extra_CPP {

    private static final String cppCode_01 = "#include <iostream>\n" +
            "#include <cstdio>\n" +
            "#include <cstdlib>\n" +
            "#include <cstring>\n" +
            "#include <cmath>\n" +
            "using namespace std;\n" +
            "\n" +
            "#define maxn 1005\n" +
            "#define eps 10E-9\n" +
            "#define inf 1e18\n" +
            "\n" +
            "struct Point\n" +
            "{\n" +
            "    double x, y, z, r;\n" +
            "} point[maxn];\n" +
            "\n" +
            "double g[maxn][maxn];\n" +
            "int vis[maxn], n;\n" +
            "double lowc[maxn];\n" +
            "\n" +
            "double dis(Point &a, Point &b)\n" +
            "{\n" +
            "    Point c;\n" +
            "    c.x = abs(a.x - b.x);\n" +
            "    c.y = abs(a.y - b.y);\n" +
            "    c.z = abs(a.z - b.z);\n" +
            "    double ret = sqrt(c.x * c.x + c.y * c.y + c.z * c.z);\n" +
            "    if (ret > a.r + b.r + eps)\n" +
            "        return ret - a.r - b.r;\n" +
            "    return 0;\n" +
            "}\n" +
            "\n" +
            "void input()\n" +
            "{\n" +
            "    for (int i = 0; i < n; i++)\n" +
            "        scanf(\"%lf%lf%lf%lf\", &point[i].x, &point[i].y, &point[i].z,\n" +
            "                &point[i].r);\n" +
            "    for (int i = 0; i < n; i++)\n" +
            "        for (int j = 0; j < n; j++)\n" +
            "            g[i][j] = dis(point[i], point[j]);\n" +
            "}\n" +
            "\n" +
            "double prim(double cost[][maxn], int n)\n" +
            "{\n" +
            "    int i, j, p;\n" +
            "    double minc, res = 0;\n" +
            "    memset(vis, 0, sizeof(vis));\n" +
            "    vis[0] = 1;\n" +
            "    for (i = 1; i < n; i++)\n" +
            "        lowc[i] = cost[0][i];\n" +
            "    for (i = 1; i < n; i++)\n" +
            "    {\n" +
            "        minc = inf;\n" +
            "        p = -1;\n" +
            "        for (j = 0; j < n; j++)\n" +
            "            if (0 == vis[j] && minc > lowc[j])\n" +
            "            {\n" +
            "                minc = lowc[j];\n" +
            "                p = j;\n" +
            "            }\n" +
            "        if (inf == minc)\n" +
            "            return -1;\n" +
            "        res += minc;\n" +
            "        vis[p] = 1;\n" +
            "        for (j = 0; j < n; j++)\n" +
            "            if (0 == vis[j] && lowc[j] > cost[p][j])\n" +
            "                lowc[j] = cost[p][j];\n" +
            "    }\n" +
            "    return res;\n" +
            "}\n" +
            "\n" +
            "int main()\n" +
            "{\n" +
            "    //freopen(\"Agimadei.in\",\"r\",stdin);\n" +
            "    while (~scanf(\"%d\", &n))\n" +
            "    {\n" +
            "        input();\n" +
            "        double ans = prim(g, n);\n" +
            "        printf(\"%.6f\\n\", ans);\n" +
            "    }\n" +
            "    return 0;\n" +
            "}";
    private static final String cppCode_02 = "#include <iostream>\n" +
            "#include <cstring>\n" +
            "#include <cstdio>\n" +
            "#include <vector>\n" +
            "using namespace std;\n" +
            "\n" +
            "const int MAXN = 1010;\n" +
            "const int MAXM = 11;\n" +
            "int nCase, cnt, n, m, score[MAXN], threshold[MAXM], number[MAXM], startId[MAXM], numberOfUniversites, noDream;\n" +
            "int matching, pairUS[MAXN];\n" +
            "bool visitedU[MAXN];\n" +
            "vector<vector<int> > reachedUniversities;\n" +
            "vector<int> apply[MAXN];\n" +
            "\n" +
            "void init() {\n" +
            "    matching = noDream = 0;\n" +
            "    reachedUniversities.clear();\n" +
            "    reachedUniversities.resize(1);\n" +
            "\tfor (int i = 0; i < MAXN; i++) {\n" +
            "        apply[i].clear();\n" +
            "\t}\n" +
            "\tmemset(pairUS, -1, sizeof(pairUS));\n" +
            "}\n" +
            "\n" +
            "void input() {\n" +
            "    //scanf(\"%d%d\", &n, &m);\n" +
            "    for (int i = 1; i <= n; i++) {\n" +
            "        int x;\n" +
            "        scanf(\"%d%d\", &score[i], &x);\n" +
            "        apply[i].resize(x+1);\n" +
            "        for (int j = 1; j <= x; j++) {\n" +
            "            scanf(\"%d\", &apply[i][j]);\n" +
            "        }\n" +
            "        if (x == 0) {\n" +
            "            noDream++;\n" +
            "        }\n" +
            "    }\n" +
            "    startId[0] = 1;\n" +
            "    for (int i = 1; i <= m; i++) {\n" +
            "        scanf(\"%d\", &number[i]);\n" +
            "        startId[i] = startId[i-1] + number[i-1];\n" +
            "    }\n" +
            "    numberOfUniversites = startId[m] + number[m];\n" +
            "    for (int i = 1; i <= m; i++) {\n" +
            "        scanf(\"%d\", &threshold[i]);\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "bool findPath(int s) {\n" +
            "\tfor (int i = 0; i < reachedUniversities[s].size(); i++) {\n" +
            "\t\tint u = reachedUniversities[s][i];\n" +
            "\t\tif (!visitedU[u]) {\n" +
            "\t\t\tvisitedU[u] = true;\n" +
            "\t\t\tif ((pairUS[u] == -1) || (findPath(pairUS[u]) == true)) {\n" +
            "\t\t\t\tpairUS[u] = s;\n" +
            "\t\t\t\treturn true;\n" +
            "\t\t\t}\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\treturn false;\n" +
            "}\n" +
            "\n" +
            "void solve() {\n" +
            "    for (int i = 1; i <= n; i++) {\n" +
            "        vector<int> universities;\n" +
            "        for (int j = 1; j < apply[i].size(); j++) {\n" +
            "            if (score[i] >= threshold[apply[i][j]]) {\n" +
            "                for (int k = 0; k < number[apply[i][j]]; k++) {\n" +
            "                    universities.push_back(startId[apply[i][j]] + k);\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        reachedUniversities.push_back(universities);\n" +
            "    }\n" +
            "/*\n" +
            "    for (int i = 1; i <= n; i++) {\n" +
            "        cout << i << \": \";\n" +
            "        for (int j = 0; j < reachedUniversities[i].size(); j++) {\n" +
            "            cout << reachedUniversities[i][j] << \" \";\n" +
            "        }\n" +
            "        cout << endl;\n" +
            "    }\n" +
            "*/\n" +
            "\tfor (int s = 1; s <= n; s++) {\n" +
            "\t\tmemset(visitedU, false, sizeof(visitedU));\n" +
            "\t\tif (findPath(s)) {\n" +
            "\t\t\tmatching++;\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\n" +
            "\tprintf(\"Case #%d: %d\\n\", ++cnt, n - noDream - matching);\n" +
            "/*\n" +
            "\tfor (int i = 1; i < numberOfUniversites; i++) {\n" +
            "        cout << i << \": \" << pairUS[i] << endl;\n" +
            "\t}\n" +
            "\tcout << endl;\n" +
            "*/\n" +
            "}\n" +
            "\n" +
            "int main() {\n" +
            "    //freopen(\"in.txt\", \"r\", stdin);\n" +
            "    //freopen(\"out.txt\", \"w\", stdout);\n" +
            "    //freopen(\"in_test.txt\", \"r\", stdin);\n" +
            "    //freopen(\"out_test.txt\", \"w\", stdout);\n" +
            "    //scanf(\"%d\", &nCase);\n" +
            "    while (~scanf(\"%d%d\", &n, &m)) {\n" +
            "        init();\n" +
            "        input();\n" +
            "        solve();\n" +
            "    }\n" +
            "    return 0;\n" +
            "}";
    private static final String cppCode_03 = "#include <iostream>\n" +
            "#include <cstring>\n" +
            "#include <cstdio>\n" +
            "#include <queue>\n" +
            "using namespace std;\n" +
            "const int maxn=5050;\n" +
            "\n" +
            "int id[maxn],sum[maxn],n,m;\n" +
            "\n" +
            "void initial()\n" +
            "{\n" +
            "    for(int i=0;i<=n;i++)  id[i]=i;\n" +
            "    memset(sum,0,sizeof(sum));\n" +
            "}\n" +
            "\n" +
            "int Find(int x)\n" +
            "{\n" +
            "    if(id[x]!=x)  id[x]=Find(id[x]);\n" +
            "    return id[x];\n" +
            "}\n" +
            "\n" +
            "void Union(int x,int y)\n" +
            "{\n" +
            "    int p=Find(x),q=Find(y);\n" +
            "    if(p!=q)  id[p]=q;\n" +
            "}\n" +
            "\n" +
            "void input()\n" +
            "{\n" +
            "    int x,y;\n" +
            "    for(int i=0;i<m;i++)\n" +
            "    {\n" +
            "        // scanf(\"%d %d\",&amp;x,&amp;y);\n" +
            "cin >> x >> y;\n" +
            "         Union(x,y);\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "void solve()\n" +
            "{\n" +
            "    int num=0,len=0;\n" +
            "    if(n%3!=0) { cout<<\"No\"<<endl; return ;}\n" +
            "    for(int i=1;i<=n;i++)  sum[Find(i)]++;\n" +
            "    for(int i=1;i<=n;i++)\n" +
            "    {\n" +
            "        if(sum[i]>3)  { cout<<\"No\"<<endl; return ;}\n" +
            "        if(sum[i]==2)  num++;\n" +
            "        else if(sum[i]==1)  len++;\n" +
            "    }\n" +
            "    if(num>len)   { cout<<\"No\"<<endl; return ;}\n" +
            "    cout<<\"Yes\"<<endl;\n" +
            "}\n" +
            "\n" +
            "int main()\n" +
            "{\n" +
            " //   freopen(\"in.txt\",\"r\",stdin);\n" +
            "  //  freopen(\"out.txt\",\"w\",stdout);\n" +
            "    while(cin>>n>>m)\n" +
            "    {\n" +
            "        initial();\n" +
            "        input();\n" +
            "        solve();\n" +
            "    }\n" +
            "    return 0;\n" +
            "}";
    private static final String cppCode_04 = "#include<iostream>\n" +
            "#include <cstdio>\n" +
            "#include<string>\n" +
            "using namespace std;\n" +
            "int LOVE(int xx){\n" +
            "    return (((long long)xx*10007)/9+55)%10;\n" +
            "} \n" +
            "struct candy{\n" +
            "    string name;\n" +
            "    int RP;\n" +
            "}new_candy,Room[15];\n" +
            "int main(){\n" +
            "    int N;\n" +
            "    string type;\n" +
            "    while(cin>>N && N){\n" +
            "        for(int i=0;i<10;i++) Room[i].RP=-1;\n" +
            "        for(int i=0;i<N;i++){\n" +
            "            cin>>type;\n" +
            "            if(type==\"Come\"){\n" +
            "                cin>>new_candy.name>>new_candy.RP;\n" +
            "                int tmp=LOVE(new_candy.RP);//hash\n" +
            "                if(Room[tmp].RP==-1){//直接入住 \n" +
            "                    Room[tmp]=new_candy;\n" +
            "                    continue;\n" +
            "                }\n" +
            "                if(Room[tmp].RP<new_candy.RP){//新的战胜旧的 \n" +
            "                    Room[tmp]=new_candy;\n" +
            "                    cout<<\"Call 1 ambulance!\"<<endl;\n" +
            "                }\n" +
            "                else if(Room[tmp].RP==new_candy.RP){//两败俱伤，房间清空 \n" +
            "                    Room[tmp].RP=-1;\n" +
            "                    cout<<\"Call 2 ambulances!\"<<endl;\n" +
            "                }\n" +
            "                else{//旧的战胜新的 \n" +
            "                    cout<<\"Call 1 ambulance!\"<<endl;\n" +
            "                }\n" +
            "            }\n" +
            "            else{\n" +
            "                int tmp;\n" +
            "                cin>>tmp;\n" +
            "                if(Room[tmp].RP==-1)cout<<\"Empty!\"<<endl;\n" +
            "                else cout<<Room[tmp].name<<endl;\n" +
            "            }\n" +
            "        }\n" +
            "        cout<<endl;\n" +
            "    }\n" +
            "    return 0;\n" +
            "}";
    private static final String cppCode_05 = "#include <iostream>\n" +
            "#include <cstdio>\n" +
            "#include <cstring>\n" +
            "#include <vector>\n" +
            "#include <string>\n" +
            "#include <algorithm>\n" +
            "#include <queue>\n" +
            "#include <set>\n" +
            "#include <map>\n" +
            "using namespace std;\n" +
            "const int maxn = 200+10;\n" +
            "typedef long long LL;\n" +
            "#define REP(_,a,b) for(int _ = (a); _ <= (b); _++)\n" +
            "bool  vis[maxn][maxn][maxn];\n" +
            "struct bottle{\n" +
            "    int a,b,c;\n" +
            "    int step;\n" +
            "    bottle(int a=0,int b=0,int c=0,int step=0):a(a),b(b),c(c),step(step){}\n" +
            "};\n" +
            "queue<bottle> que;\n" +
            "int n,s,m;\n" +
            "int A,B,C;\n" +
            "int a,b,c;\n" +
            "int bfs() {\n" +
            "    memset(vis,0,sizeof  vis);\n" +
            "    while(!que.empty()) que.pop();\n" +
            "    vis[A][B][C] =  true;\n" +
            "    que.push(bottle(A,B,C,0));\n" +
            "    while(!que.empty()) {\n" +
            "        bottle ns = que.front(),tmp;\n" +
            "        que.pop();\n" +
            "        if( ns.a==a && ns.b == b && ns.c == c) {\n" +
            "            return ns.step;\n" +
            "        }\n" +
            "        int sz1 = s-ns.a,sz2 = n-ns.b,sz3 = m-ns.c;\n" +
            "        if(ns.a) {\n" +
            "            tmp = ns;\n" +
            "            if(sz2) { //1-2\n" +
            "                if(tmp.a >= sz2) {\n" +
            "                    tmp.a -= sz2;\n" +
            "                    tmp.b = n;\n" +
            "                }else{\n" +
            "                    tmp.b += tmp.a;\n" +
            "                    tmp.a = 0;\n" +
            "\n" +
            "                }\n" +
            "                if(!vis[tmp.a][tmp.b][tmp.c]){\n" +
            "                    vis[tmp.a][tmp.b][tmp.c] = true;\n" +
            "                    tmp.step++;\n" +
            "                    que.push(tmp);\n" +
            "                }\n" +
            "\n" +
            "            }\n" +
            "            tmp = ns;\n" +
            "            if(sz3) {//1-3\n" +
            "                if(tmp.a >= sz3) {\n" +
            "                    tmp.a -= sz3;\n" +
            "                    tmp.c = m;\n" +
            "                }else{\n" +
            "                    tmp.c += tmp.a;\n" +
            "                    tmp.a = 0;\n" +
            "\n" +
            "                }\n" +
            "                if(!vis[tmp.a][tmp.b][tmp.c]){\n" +
            "                    vis[tmp.a][tmp.b][tmp.c] = true;\n" +
            "                    tmp.step++;\n" +
            "                    que.push(tmp);\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        if(ns.b) {\n" +
            "            tmp = ns;\n" +
            "            if(sz1) {\n" +
            "                if(tmp.b >= sz1) {\n" +
            "                    tmp.b -= sz1;\n" +
            "                    tmp.a = s;\n" +
            "                }else{\n" +
            "                    tmp.a += tmp.b;\n" +
            "                    tmp.b = 0;\n" +
            "\n" +
            "                }\n" +
            "                if(!vis[tmp.a][tmp.b][tmp.c]){\n" +
            "                    vis[tmp.a][tmp.b][tmp.c] = true;\n" +
            "                    tmp.step++;\n" +
            "                    que.push(tmp);\n" +
            "                }\n" +
            "            }\n" +
            "            tmp = ns;\n" +
            "            if(sz3) {\n" +
            "                if(tmp.b >= sz3) {\n" +
            "                    tmp.b -= sz3;\n" +
            "                    tmp.c = m;\n" +
            "                }else{\n" +
            "                    tmp.c += tmp.b;\n" +
            "                    tmp.b = 0;\n" +
            "\n" +
            "                }\n" +
            "                if(!vis[tmp.a][tmp.b][tmp.c]){\n" +
            "                    tmp.step++;\n" +
            "                    vis[tmp.a][tmp.b][tmp.c] = true;\n" +
            "                    que.push(tmp);\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        if(ns.c) {\n" +
            "            tmp = ns;\n" +
            "            if(sz1) {\n" +
            "                if(tmp.c >= sz1) {\n" +
            "                    tmp.c -= sz1;\n" +
            "                    tmp.a = s;\n" +
            "                }else{\n" +
            "                    tmp.a += tmp.c;\n" +
            "                    tmp.c = 0;\n" +
            "\n" +
            "                }\n" +
            "                if(!vis[tmp.a][tmp.b][tmp.c]){\n" +
            "                    vis[tmp.a][tmp.b][tmp.c] = true;\n" +
            "                    tmp.step++;\n" +
            "                    que.push(tmp);\n" +
            "                }\n" +
            "            }\n" +
            "            tmp = ns;\n" +
            "            if(sz2) {\n" +
            "                if(tmp.c >= sz2) {\n" +
            "                    tmp.c -= sz2;\n" +
            "                    tmp.b = n;\n" +
            "                }else{\n" +
            "                    tmp.b += tmp.c;\n" +
            "                    tmp.c = 0;\n" +
            "\n" +
            "                }\n" +
            "               if(!vis[tmp.a][tmp.b][tmp.c]){\n" +
            "                    vis[tmp.a][tmp.b][tmp.c] = true;\n" +
            "                    tmp.step++;\n" +
            "                    que.push(tmp);\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    return -1;\n" +
            "}\n" +
            "int main(){\n" +
            "   // freopen(\"in.txt\",\"r\",stdin);\n" +
            "    //freopen(\"out.txt\",\"w\",stdout);\n" +
            "    while(~scanf(\"%d%d%d%d%d%d%d%d%d\",&s,&n,&m,&A,&B,&C,&a,&b,&c)) {\n" +
            "        a *= 2;\n" +
            "        b *= 2;\n" +
            "        c *= 2;\n" +
            "        int  ret = bfs();\n" +
            "        if(ret==-1 ) {\n" +
            "            puts(\"Agimadei!\");\n" +
            "        }else{\n" +
            "            printf(\"%d\\n\",ret);\n" +
            "        }\n" +
            "    }\n" +
            "    return 0;\n" +
            "}\n";
    private static final String cppCode_06 = "#include <iostream>\n" +
            "#include <cstdio>\n" +
            "#include <vector>\n" +
            "using namespace std;\n" +
            "\n" +
            "typedef long long ll;\n" +
            "ll a,b,c;\n" +
            "\n" +
            "ll s(ll x){\n" +
            "    ll ret=0;\n" +
            "    while(x>0){\n" +
            "        ret+=x%10;\n" +
            "        x/=10;\n" +
            "    }\n" +
            "    return ret;\n" +
            "}\n" +
            "\n" +
            "ll g(ll x){\n" +
            "    return x*x+x+s(x);\n" +
            "}\n" +
            "\n" +
            "void solve(){\n" +
            "    ll X=-1;\n" +
            "    for(ll i=81;i>=1;i--){\n" +
            "        ll tmp=1;\n" +
            "        for(int t=0;t<a;t++) tmp*=i;\n" +
            "        ll x=b*tmp+c;\n" +
            "        if(x>=1000000000 || x<=0 ) continue;\n" +
            "        if(s(x)==i){\n" +
            "            X=x;\n" +
            "            break;\n" +
            "        }\n" +
            "    }\n" +
            "    if(X==-1){\n" +
            "        printf(\"Agimadei\\n\");\n" +
            "        return;\n" +
            "    }\n" +
            "    ll l=0,r=1100000000;\n" +
            "    while(l<r){\n" +
            "        ll mid=(l+r)/2;\n" +
            "        if(g(mid)>X*X) r=mid;\n" +
            "        else l=mid+1;\n" +
            "    }\n" +
            "    printf(\"%lld\\n\",r);\n" +
            "}\n" +
            "\n" +
            "int main(){\n" +
            "    //freopen(\"in.txt\",\"r\",stdin);\n" +
            "    //freopen(\"out.txt\",\"w\",stdout);\n" +
            "    while(cin>>a>>b>>c){\n" +
            "        solve();\n" +
            "    }\n" +
            "    return 0;\n" +
            "}";
    private static final String cppCode_07 = "#include <iostream>\n" +
            "#include <cstdio>\n" +
            "using namespace std;\n" +
            "\n" +
            "const int INF = 1e9;\n" +
            "const int maxn = 5010;\n" +
            "int factor[maxn] , dis[110][110] , T[110];\n" +
            "int N , M , Q;\n" +
            "\n" +
            "void init(){\n" +
            "\tfor(int i = 2; i <= 5000; i++){\n" +
            "\t\tint n = i;\n" +
            "\t\tfactor[i] = 0;\n" +
            "\t\tfor(int j = 2; j*j <= n; j++){\n" +
            "\t\t\tif(n%j == 0){\n" +
            "\t\t\t\tfactor[i]++;\n" +
            "\t\t\t\twhile(n%j == 0) n = n/j;\n" +
            "\t\t\t}\n" +
            "\t\t}\n" +
            "\t\tif(n != 1) factor[i]++;\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "void initial(){\n" +
            "\tfor(int i = 0; i < 110; i++){\n" +
            "\t\tfor(int j = 0; j < 110; j++) dis[i][j] = INF;\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "void readcase(){\n" +
            "\tcin >> N >> M;\n" +
            "\tfor(int i = 1; i <= N; i++) cin >> T[i];\n" +
            "\tint u , v;\n" +
            "\tfor(int i = 0; i < M; i++){\n" +
            "\t\tcin >> u >> v;\n" +
            "\t\tdis[u][v] = factor[T[u]];\n" +
            "\t\tdis[v][u] = factor[T[v]];\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "void computing(){\n" +
            "\tfor(int k = 1; k <= N; k++){\n" +
            "\t\tfor(int i = 1; i <= N; i++){\n" +
            "\t\t\tfor(int j = 1; j <= N; j++){\n" +
            "\t\t\t\tif(i != j && dis[i][j] > dis[i][k]+dis[k][j]){\n" +
            "\t\t\t\t\tdis[i][j] = dis[i][k]+dis[k][j];\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tcin >> Q;\n" +
            "\tint u , v;\n" +
            "\twhile(Q--){\n" +
            "\t\tcin >> u >> v;\n" +
            "\t\tcout << dis[u][v] << endl;\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "int main(){\n" +
            "\t//freopen(\"in.txt\" , \"r\" , stdin);\n" +
            "\t//freopen(\"out.txt\" , \"w\" , stdout);\n" +
            "\tinit();\n" +
            "\tint T;\n" +
            "\tcin >> T;\n" +
            "\twhile(T--){\n" +
            "\t\tinitial();\n" +
            "\t\treadcase();\n" +
            "\t\tcomputing();\n" +
            "\t}\n" +
            "\treturn 0;\n" +
            "}";
    private static final String cppCode_08 = "#include <iostream>\n" +
            "#include <cstdio>\n" +
            "#include <cstring>\n" +
            "using namespace std;\n" +
            "const int maxn = 100+10;\n" +
            "int str[maxn][maxn];\n" +
            "int m,n;\n" +
            "bool judge(int i,int j)\n" +
            "{\n" +
            "    if(str[i][j]==1 || str[i][j]==2 || str[i][j]==3)\n" +
            "        return false;\n" +
            "    return true;\n" +
            "}\n" +
            "void dfs(int i,int j)\n" +
            "{\n" +
            "    if(i<0 || i>=m || j<0 || j>=n)\n" +
            "        return ;\n" +
            "    str[i][j]=1;\n" +
            "    if(judge(i-1,j-1)) dfs(i-1,j-1);\n" +
            "    if(judge(i-1,j)) dfs(i-1,j);\n" +
            "    if(judge(i-1,j+1)) dfs(i-1,j+1);\n" +
            "    if(judge(i,j-1)) dfs(i,j-1);\n" +
            "    if(judge(i,j+1)) dfs(i,j+1);\n" +
            "    if(judge(i+1,j-1)) dfs(i+1,j-1);\n" +
            "    if(judge(i+1,j)) dfs(i+1,j);\n" +
            "    if(judge(i+1,j+1)) dfs(i+1,j+1);\n" +
            "\n" +
            "}\n" +
            "int main ()\n" +
            "{\n" +
            "    while(~scanf(\"%d%d\",&m,&n))\n" +
            "    {\n" +
            "        memset(str,0,sizeof(str));\n" +
            "        for(int i=0;i<m;i++)\n" +
            "        {\n" +
            "            for(int j=0;j<n;j++)\n" +
            "            {\n" +
            "                scanf(\"%d\",&str[i][j]);\n" +
            "            }\n" +
            "        }\n" +
            "        int cnt=0;\n" +
            "        for(int i=0;i<m;i++)\n" +
            "        {\n" +
            "            for(int j=0;j<n;j++)\n" +
            "            {\n" +
            "                if(judge(i,j))\n" +
            "                {\n" +
            "                    cnt++;\n" +
            "                    dfs(i,j);\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        printf(\"%d\\n\",cnt);\n" +
            "    }\n" +
            "    return 0;\n" +
            "}";
    private static final String cppCode_09 = "#include <iostream>\n" +
            "#include <cstdio>\n" +
            "#include <cstring>\n" +
            "#define mod 20141027\n" +
            "using namespace std;\n" +
            "int B,n,m,f;\n" +
            "int c[100010],F[100010],g[1000200];\n" +
            "void add(int x,int d){\n" +
            "    for(;x<100010;x+=x&(-x)) c[x]+=d;\n" +
            "}\n" +
            "int ask(int x){\n" +
            "    int ret=0;\n" +
            "    for(;x;x-=x&(-x)) ret+=c[x];\n" +
            "    return ret;\n" +
            "}\n" +
            "void add1(int x){\n" +
            "    for(;x<100010;x+=x&(-x)) F[x]*=(-1);\n" +
            "}\n" +
            "int ask1(int x){\n" +
            "    int ret=1;\n" +
            "    for(;x;x-=x&(-x)) ret*=F[x];\n" +
            "    return ret;\n" +
            "}\n" +
            "int main(){\n" +
            "    //freopen(\"poemorproblem_in.txt\",\"r\",stdin);\n" +
            "    //freopen(\"poemorproblem_out.txt\",\"w\",stdout);\n" +
            "    while(~scanf(\"%d\",&B)){\n" +
            "        scanf(\"%d%d\",&n,&m);\n" +
            "        g[0]=1;\n" +
            "        for(int i=1;i<1000200;i++) g[i]=g[i-1]*B%mod;\n" +
            "        for(int i=0;i<=n;i++) F[i]=1;\n" +
            "        for(int i=1;i<=n;i++){\n" +
            "            scanf(\"%d\",&f);\n" +
            "            if(f==-1) add1(i);\n" +
            "        }\n" +
            "        int x;\n" +
            "        memset(c,0,sizeof c);\n" +
            "        for(int i=1;i<=n;i++) scanf(\"%d\",&x),add(i,x);\n" +
            "        int q,L,R,T;\n" +
            "        for(int i=0;i<m;i++){\n" +
            "            scanf(\"%d\",&q);\n" +
            "            if(q==0){\n" +
            "                scanf(\"%d\",&x);\n" +
            "                add1(x);\n" +
            "            }else if(q==1){\n" +
            "                scanf(\"%d%d\",&x,&T);\n" +
            "                add(x,T);\n" +
            "            }else{\n" +
            "                scanf(\"%d%d\",&L,&R);\n" +
            "                printf(\"%d\\n\",(ask1(R)/ask1(L-1)*g[ask(R)-ask(L-1)]%mod+mod)%mod);\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    return 0;\n" +
            "}";
    private static final String cppCode_10 = "#include<iostream>\n" +
            "#include<queue>\n" +
            "#include<cstdio>\n" +
            "using namespace std;\n" +
            "struct node{\n" +
            "    string name;\n" +
            "    int v;\n" +
            "    node(string a=\"\",int t = 0){\n" +
            "        name = a;\n" +
            "        v = t;\n" +
            "    }\n" +
            "    bool friend operator < (node a,node b){\n" +
            "        if(a.v>b.v)return true;\n" +
            "        if(a.v==b.v)return a.name > b.name;\n" +
            "    }\n" +
            "};\n" +
            "\n" +
            "int main(){\n" +
            "  //  freopen(\"C:\\\\Users\\\\s\\\\Desktop\\\\3.txt\" , \"r\" , stdin);\n" +
            "  //  freopen(\"C:\\\\Users\\\\s\\\\Desktop\\\\1.txt\" , \"w\" , stdout);\n" +
            "        int N;\n" +
            "        priority_queue<node> q1,q2;\n" +
            "        while(cin >> N){\n" +
            "        while(N--){\n" +
            "            char c;\n" +
            "            cin >> c;\n" +
            "            if(c=='A'){\n" +
            "                string name;\n" +
            "                int v;\n" +
            "                cin >> name >> v;\n" +
            "                if(v==-1){\n" +
            "                    q2.push(node(name,v));\n" +
            "                }else{\n" +
            "                    q1.push(node(name,v));\n" +
            "                }\n" +
            "            }\n" +
            "            if(c=='Q'){\n" +
            "                if(!q2.empty()){\n" +
            "                    node now = q2.top();\n" +
            "                    q2.pop();\n" +
            "                    cout<< now.name<<endl;\n" +
            "                }else{\n" +
            "                    node now = q1.top();\n" +
            "                    q1.pop();\n" +
            "                    cout<<now.name<<endl;\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";

    @Test
    public void testRun() throws Exception {
        Map<Language, Integer> memoryLimit = new HashMap<Language, Integer>();
        memoryLimit.put(Language.DEFAULT, 2 * 1024 * 1024);  // 2 GB
        Problem[] problems = new Problem[15];

        Map<Language, Integer> timeLimit1 = new HashMap<Language, Integer>();  // A
        timeLimit1.put(Language.DEFAULT, 5000);
        problems[1] = new LocalProblem(1, "11_test.in", "11_test.out", "all", timeLimit1, memoryLimit);

        Map<Language, Integer> timeLimit2 = new HashMap<Language, Integer>();  // B
        timeLimit2.put(Language.DEFAULT, 2000);
        problems[2] = new LocalProblem(2, "12_test.in", "12_test.out", "all", timeLimit2, memoryLimit);

        Map<Language, Integer> timeLimit3 = new HashMap<Language, Integer>();  // C
        timeLimit3.put(Language.DEFAULT, 1000);
        problems[3] = new LocalProblem(3, "13_test.in", "13_test.out", "all", timeLimit3, memoryLimit);

        Map<Language, Integer> timeLimit4 = new HashMap<Language, Integer>();  // D
        timeLimit4.put(Language.DEFAULT, 3000);
        problems[4] = new LocalProblem(4, "14_test.in", "14_test.out", "all", timeLimit4, memoryLimit);

        Map<Language, Integer> timeLimit5 = new HashMap<Language, Integer>();  // E
        timeLimit5.put(Language.DEFAULT, 3000);
        problems[5] = new LocalProblem(5, "15_test.in", "15_test.out", "all", timeLimit5, memoryLimit);

        Map<Language, Integer> timeLimit6 = new HashMap<Language, Integer>();  // F
        timeLimit6.put(Language.DEFAULT, 2000);
        problems[6] = new LocalProblem(6, "16_test.in", "16_test.out", "all", timeLimit6, memoryLimit);

        Map<Language, Integer> timeLimit7 = new HashMap<Language, Integer>();  // G
        timeLimit7.put(Language.DEFAULT, 2000);
        problems[7] = new LocalProblem(7, "17_test.in", "17_test.out", "all", timeLimit7, memoryLimit);

        Map<Language, Integer> timeLimit8 = new HashMap<Language, Integer>();  // H
        timeLimit8.put(Language.DEFAULT, 1000);
        problems[8] = new LocalProblem(8, "18_test.in", "18_test.out", "all", timeLimit8, memoryLimit);

        Map<Language, Integer> timeLimit9 = new HashMap<Language, Integer>();  // I
        timeLimit9.put(Language.DEFAULT, 4000);
        problems[9] = new LocalProblem(9, "19_test.in", "19_test.out", "all", timeLimit9, memoryLimit);

        Map<Language, Integer> timeLimit10 = new HashMap<Language, Integer>();  // J
        timeLimit10.put(Language.DEFAULT, 3000);
        problems[10] = new LocalProblem(10, "20_test.in", "20_test.out", "all", timeLimit10, memoryLimit);


        // C++ 01 Accepted
        Solution solution = new Solution(1, problems[1], cppCode_01, Language.CPP);
        LocalResolver localResolver = new LocalResolver(solution);
        localResolver.handle();
        System.err.println(solution.getResult() + "\n" + solution.getCompileInfo());
        assertEquals(solution.getResult(), Result.Accepted);

        // C++ 02 Accepted
        solution = new Solution(2, problems[2], cppCode_02, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // C++ 03 Accepted
        solution = new Solution(3, problems[3], cppCode_03, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // C++ 04 Accepted
        solution = new Solution(4, problems[4], cppCode_04, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // C++ 05 Accepted
        solution = new Solution(5, problems[5], cppCode_05, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // C++ 06 Accepted
        solution = new Solution(6, problems[6], cppCode_06, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // C++ 07 Accepted
        solution = new Solution(7, problems[7], cppCode_07, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // C++ 08 Accepted
        solution = new Solution(8, problems[8], cppCode_08, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // C++ 09 Accepted
        solution = new Solution(9, problems[9], cppCode_09, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);

        // C++ 10 Accepted
        solution = new Solution(10, problems[10], cppCode_10, Language.CPP);
        localResolver = new LocalResolver(solution);
        localResolver.handle();
        assertEquals(solution.getResult(), Result.Accepted);


        // Real solution data Test
        File[] files = new File("src/test/resources/testdata2").listFiles();
        File file = new File("report_detail_2.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        Writer writer = new BufferedWriter(new FileWriter(file));
        File file1 = new File("report_2.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        Writer writer1 = new BufferedWriter(new FileWriter(file1));
        for (int i = 0; i < (files != null ? files.length : 0); i++) {
            if (!files[i].isDirectory()) continue;
            File[] files2 = files[i].listFiles();
            for (int j = 0; j < (files2 != null ? files2.length : 0); j++) {
                System.out.println(files2[j].getName());
                // filename: 'SolutionID_UserID_Username_ProblemID_Result.Language'
                String[] params = files2[j].getName().replace(".", "_").split("_");
                if (params[3].equals("A+B")) continue;
                Language language = Language.valueOf(params[5]);
                Problem problem = problems[params[3].charAt(0) - 'A' + 1];
                Result result = Result.parseResult(params[4]);

                String code = FileUtils.getFileContent(files2[j]);
                solution = new Solution(Integer.parseInt(params[0]), problem, code, language);
                localResolver = new LocalResolver(solution);
                localResolver.handle();
                //assertEquals(solution.getResult(), result);
                if (!solution.getResult().equals(result)) {
                    //if (solution.getResult().equals(Result.Runtime_Error) && result.equals(Result.Time_Limit_Exceeded))
                    //    continue;  // ignore TLE -> RE
                    writer1.write(files2[j].getName().replace(".", "_").replace("_", ",") + "," + solution.getResult() + "," + solution.getTime() + "," + solution.getMemory() + "\n");
                    writer.write(files2[j].getName() + " -> " + solution.getResult() + "\n" + solution.getCompileInfo() + "\n" + solution.getExecuteInfo() + "\n" + solution.getCompareInfo() + "\n\n");
                    writer.flush();
                    writer1.flush();
                }
            }
        }
        writer.close();
        writer1.close();
    }
}