#include <stdio.h>


int main(){
    int f;
    while(1){
        scanf("%x", &f);
        float a = *(float*)&f;
        printf("%f\n",a);
    }
    return 0;
}