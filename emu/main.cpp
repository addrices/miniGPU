// #include "emu.h"
#include <memory>
#include <stdio.h>
// #include <verilated.h>


int main(void) {
float f;
scanf("%f", &f);
printf("%x\n", *((int*)&f));
}