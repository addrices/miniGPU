package miniGPU

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class fp_convert extends BlackBox{
    val io = IO(new Bundle {
        val clock = Input(Clock())
        val dataa = Input(UInt(32.W))
        val result = Output(UInt(32.W))     
    })

}

class Dots2Int extends MultiIOModule{
    val Dots_in = IO(Input(new Dots_point)) //float
    val Dots_out = IO(Output(new Dots_point))
    val update_in = IO(Input(Bool()))
    val update_out = IO(Output(Bool()))
    
    val update_r1 = RegInit(0.U)
    val update_r2 = RegInit(0.U)
    val update_r3 = RegInit(0.U)
    val update_r4 = RegInit(0.U)
    val update_r5 = RegInit(0.U)
    val update_r6 = RegInit(0.U)
    val fp_converts = Seq.fill(3)(Module(new fp_convert))
    
    update_r1 := update_in
    update_r2 := update_r1
    update_r3 := update_r2
    update_r4 := update_r3
    update_r5 := update_r4
    update_r6 := update_r5
    
    fp_converts(0).io.clock := clock
    fp_converts(0).io.dataa <> Dots_in.x
    fp_converts(0).io.result <> Dots_out.x

    fp_converts(1).io.clock := clock
    fp_converts(1).io.dataa <> Dots_in.y
    fp_converts(1).io.result <> Dots_out.y

    fp_converts(2).io.clock := clock
    fp_converts(2).io.dataa <> Dots_in.z
    fp_converts(2).io.result <> Dots_out.z
    update_out := update_r6

    

}