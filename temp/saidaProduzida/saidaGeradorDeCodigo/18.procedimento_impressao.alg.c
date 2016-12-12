#include <stdio.h>
#include <stdlib.h>
typedef int bool
#define true 1
#define false 0


void proc_imprime(char* mensagem){
printf("%s", mensagem);
printf("\n");
}
int main() {
proc_imprime();
return 0;
}

