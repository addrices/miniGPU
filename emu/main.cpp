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

    dut->reset = 1;
    dut->eval();
    dut->clock = 0;
    dut->eval();
    dut->clock = 1;
    dut->eval();

    dut->reset = 0;
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

    for(int i = 0;i < 1000;i++){
        dut->eval();
        dut->clock = 0;
        dut->eval();
        dut->clock = 1;
        dut->eval();
    }

    for(int b = 0; b < 480;b++){
        for(int a = 0; a < 640;a++){
            dut->v_addr = a;
            dut->h_addr = b;
            // dut->debug = 1;
            dut->eval();
            dut->clock = 0;
            dut->eval();
            dut->clock = 1;
            dut->eval();
            // if(dut->data == 0xffffff)
                // printf("point %d %d\n",a,b);
        }
    }

    printf("aaaaa\n");

    for(int a = 0; a < 640;a++){
        for(int b = 0; b < 480;b++){

            dut->v_addr = a;
            dut->h_addr = b;
            // dut->debug = 1;
            dut->eval();
            dut->clock = 0;
            dut->eval();
            dut->clock = 1;
            dut->eval();
            if(dut->data != 0)
                printf("point %d %d %x\n",a,b,dut->data);
        }
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