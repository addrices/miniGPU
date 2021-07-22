#include "emu.h"
#include <memory>
#include <stdio.h>
// #include <verilated.h>

void fp2int(int fp_float,int *fp_int){
    *fp_int = (int)*(float*)&fp_float;
}

int main(void) {
    float f;
    scanf("%f", &f);
    printf("%x\n", *((int*)&f));
    int b;
    fp2int(*((int*)&f),&b);
    printf("%d\n",b);
    auto dut = std::make_shared<emu>();

    dut->fp_float = *((int*)&f);
    dut->eval();
    dut->clock = 0;
    dut->eval();
    dut->clock = 1;
    dut->eval();
    printf("int:%d\n",dut->fp_int);
    
    f = f+1;
    dut->fp_float = *((int*)&f);
    dut->eval();
    dut->clock = 0;
    dut->eval();
    dut->clock = 1;
    dut->eval();
    printf("int:%d\n",dut->fp_int);

    return 0;
}