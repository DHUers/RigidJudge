#include <iostream>
#include <fstream>
using namespace std;

int main(int argc, char *args[]) {
    ifstream in(args[1]);
    ifstream out(args[2]);
    ifstream user(args[3]);
    int ret = 0;  // 0-AC, 1-WA, 2-PE, -1-JE
    int a, b, c;
    while (in >> a >> b) {
        user >> c;
        if (a + b != c) {
            cout << a << " + " << b << " != " << c << endl;
            ret = 1;
            break;
        }
    }
    in.close();
    out.close();
    user.close();
    return ret;
}
