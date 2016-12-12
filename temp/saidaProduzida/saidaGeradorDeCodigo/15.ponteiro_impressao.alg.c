#include <stdio.h>
#include <stdlib.h>
typedef int bool
#define true 1
#define false 0


int main() {
int x;
int* endx;
x = 0;
printf("%d", x);
printf(" e ");
endx = &x;
*endx=1;
printf("%d", x);
return 0;
}

