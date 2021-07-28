package miniGPU

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class fp_mul extends BlackBox{
    val io = IO(new Bundle {
        val clock = Input(Clock())
        val dataa = Input(UInt(32.W))
        val datab = Input(UInt(32.W))
        val result = Output(UInt(32.W))
    })
}


class shiftDot extends MultiIOModule{
    val Dot_in = IO(Input(new Dots_point))
    val Dot_out = IO(Output(new Dots_point))
    val update_in = IO(Input(Bool()))
    val update_out = IO(Output(Bool()))
    val update_conf = IO(Input(UInt(2.W)))

//latency = 10
    val update_r = Seq.fill(6)(RegInit(false.B))
    for(i <- 1 to 5){
        update_r(i) := update_r(i-1)
    }    
    update_r(0) := update_in
    update_out := update_r(5)

    val mulers = Seq.fill(3)(Module(new fp_mul))
    mulers(0).io.clock := clock
    mulers(0).io.dataa := Dot_in.x
    mulers(0).io.datab := Mux1H(Seq(
        (update_conf === 0.U) -> "hc0a00000".U,
        (update_conf === 1.U) -> "h40a00000".U,
        (update_conf === 2.U) -> 0.U,
        (update_conf === 3.U) -> 1.U
    ))
    Dot_out.x := mulers(0).io.result

    mulers(1).io.clock := clock
    mulers(1).io.dataa := Dot_in.y
    mulers(1).io.datab := Mux1H(Seq(
        (update_conf === 0.U) -> 0.U,
        (update_conf === 1.U) -> 0.U,
        (update_conf === 2.U) -> "h40a00000".U,
        (update_conf === 3.U) -> 1.U
    ))
    Dot_out.y := mulers(1).io.result

    mulers(2).io.clock := clock
    mulers(2).io.dataa := Dot_in.z
    mulers(2).io.datab := Mux1H(Seq(
        (update_conf === 0.U) -> 0.U,
        (update_conf === 1.U) -> 0.U,
        (update_conf === 2.U) -> 0.U,
        (update_conf === 3.U) -> 1.U
    ))
    Dot_out.z := mulers(2).io.result
}
