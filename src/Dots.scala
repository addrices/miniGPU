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
    val update_comp = Wire(Bool())
    val shift_mod = Module(new shiftDot)
    val comp_count_in_r = RegInit(0.U(3.W))
    val comp_count_in_flag = RegInit(false.B)
    val comp_count_out_r = RegInit(0.U(3.W))
    val comp_count_out_flag = RegInit(false.B)
    val comp_out_update = Wire(Bool())

    val update_spsh_r = RegInit(false.B)
    val update_conf_r = RegInit(0.U(2.W))

    when(reset.asBool()){
        Dots_r(0).x := "h42c80000".U
        Dots_r(0).y := "h42c80000".U
        Dots_r(0).z := "hc2c80000".U

        Dots_r(1).x := "h42c80000".U
        Dots_r(1).y := "hc2c80000".U
        Dots_r(1).z := "hc2c80000".U

        Dots_r(2).x := "hc2c80000".U
        Dots_r(2).y := "h42c80000".U
        Dots_r(2).z := "hc2c80000".U

        Dots_r(3).x := "hc2c80000".U
        Dots_r(3).y := "hc2c80000".U
        Dots_r(3).z := "hc2c80000".U

        Dots_r(4).x := "h0".U
        Dots_r(4).y := "h0".U
        Dots_r(4).z := "h42c80000".U
    }

    update_out := false.B
    update_comp := false.B
    //TODO:
    comp_out_update := shift_mod.update_out


    shift_mod.update_conf := update_conf
    shift_mod.update_in := update_comp === true.B && update_spsh === true.B
    shift_mod.Dot_in := Mux1H(Seq(
        (comp_count_in_r === 0.U) -> Dots_r(0),
        (comp_count_in_r === 1.U) -> Dots_r(1),
        (comp_count_in_r === 2.U) -> Dots_r(2),
        (comp_count_in_r === 3.U) -> Dots_r(3),
        (comp_count_in_r === 4.U) -> Dots_r(4),
        (comp_count_in_r === 5.U) -> Dots_r(0), 
        (comp_count_in_r === 6.U) -> Dots_r(0), 
        (comp_count_in_r === 7.U) -> Dots_r(0), 
    ))

    switch(state_r){
        is(sIdle){
            when(update_in === true.B){
                state_r := sUpdate
                update_comp := true.B
                comp_count_in_r := 0.U
                comp_count_in_flag := true.B
                update_spsh_r := update_spsh
                update_conf_r := update_conf
            }
        }
        is(sUpdate){
            when(update_conf_r === 3.U && update_spsh_r === false.B){
                comp_count_in_flag := false.B
                state_r := sOutUpdate
            }.otherwise{
                // printf("%d %d %d %d %d %d \n",update_spsh_r,comp_count_in_flag,comp_count_in_r,comp_count_out_flag,comp_count_out_r,comp_out_update)
                when(comp_count_in_flag === true.B){
                    when(comp_count_in_r === 4.U){
                        comp_count_in_r := 0.U
                        comp_count_in_flag := false.B
                    }.otherwise{
                        comp_count_in_r := comp_count_in_r + 1.U
                    }
                }

                when(comp_out_update === true.B){
                    comp_count_out_flag := true.B
                    comp_count_out_r := 0.U
                }.elsewhen(comp_count_out_flag === true.B){
                    // TODO:
                    Dots_r(comp_count_out_r) := shift_mod.Dot_out
                    when(comp_count_out_r === 4.U){
                        update_out := true.B
                        outDot_count_r := 0.U
                        state_r := sOutUpdate
                        comp_count_out_flag := false.B
                        comp_count_out_r := 0.U
                    }.otherwise{
                        comp_count_out_r := comp_count_out_r + 1.U
                    }
                }
            }
        }
        is(sOutUpdate){
            outDot_count_r := outDot_count_r + 1.U
            when(outDot_count_r === 4.U){
                state_r := sIdle
                outDot_count_r := 0.U
            }
        }
    }
    Dot_io <> Dots_r(outDot_count_r)
    // printf("update_in:%d state:%d update_comp%d comp_count_in_r%d,shift_update_out:%d,comp_count_out_r:%d\n",update_in,state_r,update_comp,comp_count_in_r,shift_mod.update_out,comp_count_out_r)
    when(state_r === sOutUpdate){
        printf("Dots_r0 %x %x %x\n",Dots_r(0).x, Dots_r(0).y, Dots_r(0).z)
        printf("Dots_r1 %x %x %x\n",Dots_r(1).x, Dots_r(1).y, Dots_r(1).z)
        printf("Dots_r2 %x %x %x\n",Dots_r(2).x, Dots_r(2).y, Dots_r(2).z)
        printf("Dots_r3 %x %x %x\n",Dots_r(3).x, Dots_r(3).y, Dots_r(3).z)
        printf("Dots_r4 %x %x %x\n",Dots_r(4).x, Dots_r(4).y, Dots_r(4).z)
    }
}


