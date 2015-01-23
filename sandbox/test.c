#include <stdio.h>
#include <string.h>
int a, b, c[100000];
int main() {
	memset(c, 0, sizeof(c));
	while (~scanf("%d%d", &a, &b)) {
		printf("%d\n", a+b);
	}
	return 0;
}
