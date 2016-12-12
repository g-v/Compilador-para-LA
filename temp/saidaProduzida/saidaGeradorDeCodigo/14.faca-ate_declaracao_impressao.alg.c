#include <stdio.h>
#include <stdlib.h>
typedef int bool
#define true 1
#define false 0


int main() {
int i;
i = 1;
do{
printf("%d", i);
printf("\n");
i = i+1;
}while(!(i==6));
return 0;
}

