#include "emu.h"
#include <memory>
#include <stdio.h>
// #include <verilated.h>

/*
Dots_r0 42e7b0f2 c18aeb26 c2c80000
Dots_r1 42a23b5e c18aeb26 c2c80000
Dots_r2 c2a23b5e 418aeb26 c2c80000
Dots_r3 c2e7b0f2 418aeb26 c2c80000
Dots_r4 00000000 00000000 42c80000
*/

void fp2int(int fp_float,int *fp_int){
    *fp_int = (int)*(float*)&fp_float;
}

void fp_add(int dataa, int datab, int *result){
    float result_f = (*(float*)&dataa + *(float*)&datab);
    *result = *(int *)&result_f;
}

void fp_mul(int dataa, int datab, int *result){
    float result_f = (*(float*)&dataa * *(float*)&datab);
    *result = *(int *)&result_f;
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

    dut->button_spin = 0;
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

    for(int i = 0;i < 100;i++){
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
        }
    }

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
            if(dut->data == 0xffffff)
                printf("point %d %d %x\n",a,b,dut->data);
        }
    }

    // printf("shift\n");
    // dut->sw_shift = 0;
    // dut->button_shift = 0;
    // dut->eval();
    // dut->clock = 0;
    // dut->eval();
    // dut->clock = 1;
    // dut->eval();

    // dut->button_shift = 1;
    // dut->eval();
    // dut->clock = 0;
    // dut->eval();
    // dut->clock = 1;
    // dut->eval();

    // for(int i = 0;i < 100;i++){
    //     dut->eval();
    //     dut->clock = 0;
    //     dut->eval();
    //     dut->clock = 1;
    //     dut->eval();
    // }

    // for(int b = 0; b < 480;b++){
    //     for(int a = 0; a < 640;a++){
    //         dut->v_addr = a;
    //         dut->h_addr = b;
    //         // dut->debug = 1;
    //         dut->eval();
    //         dut->clock = 0;
    //         dut->eval();
    //         dut->clock = 1;
    //         dut->eval();
    //         // if(dut->data == 0xffffff)
    //             // printf("point %d %d\n",a,b);
    //     }
    // }

    // for(int a = 0; a < 640;a++){
    //     for(int b = 0; b < 480;b++){

    //         dut->v_addr = a;
    //         dut->h_addr = b;
    //         // dut->debug = 1;
    //         dut->eval();
    //         dut->clock = 0;
    //         dut->eval();
    //         dut->clock = 1;
    //         dut->eval();
    //         if(dut->data == 0xffffff)
    //             printf("point %d %d %x\n",a,b,dut->data);
    //     }
    // }

    printf("spin z\n");
    dut->sw_spin = 2;
    dut->button_spin = 0;
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

    for(int i = 0;i < 100;i++){
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
            if(dut->data == 0xffffff)
                printf("point %d %d %x\n",a,b,dut->data);
        }
    }


    return 0;
}