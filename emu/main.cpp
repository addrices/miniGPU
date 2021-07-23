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

    dut->sw_spin = 3;
    dut->sw_shift = 0;
    dut->button_spin = 1;
    dut->button_shift = 1;
    dut->eval();
    dut->clock = 0;
    dut->eval();
    dut->clock = 1;
    dut->eval();

    dut->button_spin = 1;
    dut->eval();
    dut->clock = 0;
    dut->eval();
    dut->clock = 1;
    dut->eval();

    dut->button_spin = 0;
    dut->eval();
    dut->clock = 0;
    dut->eval();
    dut->clock = 1;
    dut->eval();

    for(int i = 0;i < 100;i++){
        dut->eval();
        dut->clock = 0;
        dut->eval();
        dut->clock = 1;
        dut->eval();
    }
    
    // dut->eval();
    // printf("fp_float:%f\n\n",f);

    // for(int i = 0;i < 10;i++){
    //     dut->fp_float = *((int*)&f);
    //     dut->clock = 0;
    //     dut->eval();
    //     dut->clock = 1;
    //     dut->eval();
    //     f = f+1;
    // }
    // dut->fp_float = *((int*)&f);
    // dut->eval();
    // dut->clock = 0;
    // dut->eval();
    // dut->clock = 1;
    // dut->eval();
    // printf("int:%d\n",dut->fp_int);

    return 0;
}