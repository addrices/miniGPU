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
    val Dot_io = IO(Vec(5,Output(new Dots_point)))
    val update_en = IO(Output(Bool()))


    val Dots_r = Seq.fill(5)(Reg(new Dots_point))
    val count = RegInit(0.U(16.W))
    count := count + 1.U
    update_en := count === 10.U
    when(reset.asBool()){
        Dots_r(1).x := 23.U
    }

    for(i <- 0 to 4){
        Dot_io(i).x := Dots_r(i).x
        Dot_io(i).y := Dots_r(i).y
        Dot_io(i).z := Dots_r(i).z
    }
}


