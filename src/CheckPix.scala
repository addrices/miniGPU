package miniGPU

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class CheckPix extends MultiIOModule{
    val dot_Int_In = IO(Vec(5,Input(new Dots_point)))
    val up_v = IO(Input(UInt(10.W)))
    val up_h = IO(Input(UInt(10.W)))
    val flag = IO(Output(Bool()))

    flag := false.B

    val dots_real = Seq.fill(5)(Wire(new Dots_point))
    for(i <- 0 until 5){
        dots_real(i).z := dot_Int_In(i).z
        dots_real(i).x := (dot_Int_In(i).x.asSInt() + 320.S).asUInt()
        dots_real(i).y := (dot_Int_In(i).y.asSInt() + 240.S).asUInt()
    }

    for(i <- 0 until 5){
        when(dots_real(i).x <= up_v + 2.U && dots_real(i).x >= up_v - 2.U && dots_real(i).y <= up_h + 2.U && dots_real(i).y >= up_h - 2.U){
            // printf("%x %x %x %x\n",up_v,up_h,dots_real(i).x,dots_real(i).y)
            flag := true.B
        }
        // when(dots_real(i).x === up_v && dots_real(i).y === up_h){
        //     // printf("%x %x %x %x\n",up_v,up_h,dots_real(i).x,dots_real(i).y)
        //     flag := true.B
        // }
    }
}