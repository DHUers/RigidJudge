#include <stdio.h>
int main(int argc, char *args[]) {
   FILE *f_in = fopen(args[1], "r");
   FILE *f_out = fopen(args[2], "r");
   FILE *f_user = fopen(args[3], "r");
   int ret = 0;  // 0-AC, 1-WA, 2-PE, -1-JE
   int a, b, c;
   while (fscanf(f_in, "%d %d", &a, &b) != EOF) {
       fscanf(f_user, "%d", &c);
       if (a + b != c) {
           printf("%d + %d != %d\n", a, b, c);
           ret = 1;
           break;
       }
   }
   fclose(f_in);
   fclose(f_out);
   fclose(f_user);
   return ret;
}
