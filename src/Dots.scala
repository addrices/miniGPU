package miniGPU

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class Dots_point extends Bundle{
    val x = UInt(32.W)
    val y = UInt(32.W)
    val z = UInt(32.W)
}

class Dots extends MultiIOModule{
    val Dot_io = IO(Output(new Dots_point))
    val update_out = IO(Output(Bool()))

    val update_in = IO(Input(Bool()))
    val update_spsh = IO(Input(Bool()))    //0 spin 1 shift
    val update_conf = IO(Input(UInt(2.W)))

    val sIdle :: sUpdate :: sOutUpdate :: Nil = Enum(3)
    val state_r = RegInit(sIdle)
    val Dots_r = Mem(5,new Dots_point)
    val outDot_count_r = RegInit(0.U(3.W))
    when(reset.asBool()){
        Dots_r(1).x := 23.U
    }

    update_out := false.B
    switch(state_r){
        is(sIdle){
            when(update_in === true.B){
                state_r := sUpdate
            }
        }
        is(sUpdate){

            when(true.B){
                state_r := sOutUpdate
                update_out := true.B
                outDot_count_r := 0.U
            }
        }
        is(sOutUpdate){
            state_r := sIdle
            outDot_count_r := outDot_count_r + 1.U
            when(outDot_count_r === 4.U){
                state_r := sIdle
            }
        }
    }
    Dot_io <> Dots_r(outDot_count_r)

}


