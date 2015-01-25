#include <iostream>
#include <cstring>
using namespace std;
int a, b;
long long c[3110000], d[2660000], e[2500000];
int main() {
   memset(c, 0, sizeof(c));
   memset(d, 0, sizeof(d));
   memset(e, 0, sizeof(e));
   for (int i = 0; i < 1000000; i++) {
       c[i] = i;
   }
   while (cin >> a >> b) {
       cout << a + b << endl;
   }
   return 0;
}