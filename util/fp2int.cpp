#include <stdio.h>


void fp2int(int fp_float,int *fp_int){
    *fp_int = (int)*(float*)&fp_float;
}

int main(){
    float f;
    int b;
    while(1){
        scanf("%f", &f);
        printf("%x\n", *((int*)&f));
        fp2int(*((int*)&f),&b);
        printf("%d\n",b);
    }
    return 0;
}