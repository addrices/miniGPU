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

//0 x-spin 1 y-spin 2 z-spin
class spinDot extends MultiIOModule{
    val Dot_in = IO(Input(new Dots_point))
    val Dot_out = IO(Output(new Dots_point))
    val update_in = IO(Input(Bool()))
    val update_out = IO(Output(Bool()))
    val update_conf = IO(Input(UInt(2.W)))

//latency = 10
    val update_r = Seq.fill(16)(RegInit(false.B))
    val xyz_r = Seq.fill(16)(Reg(UInt(32.W)))
    val update_conf_r = Reg(UInt(2.W))
    for(i <- 1 to 15){
        update_r(i) := update_r(i-1)
        xyz_r(i) := xyz_r(i-1)
    }    
    update_r(0) := update_in
    update_out := update_r(15)
    when(update_in === true.B){
        // printf("spinDot update in")
        update_conf_r := update_conf
    }
    // when(update_out){
    //     printf("spinDot update out")
    // }

    when(update_conf_r === 0.U){    
        xyz_r(0) := Dot_in.x
    }.elsewhen(update_conf === 1.U){
        xyz_r(0) := Dot_in.y
    }.otherwise{
        xyz_r(0) := Dot_in.z
    }

    val mulers = Seq.fill(4)(Module(new fp_mul))
    val adders = Seq.fill(2)(Module(new fp_add))
    
    mulers(0).io.clock := clock
    mulers(1).io.clock := clock
    mulers(2).io.clock := clock
    mulers(3).io.clock := clock

    mulers(0).io.dataa := Mux1H(Seq(
        (update_conf_r === 0.U) -> Dot_in.y,
        (update_conf_r === 1.U) -> Dot_in.z,
        (update_conf_r === 2.U) -> Dot_in.x,
        (update_conf_r === 3.U) -> 0.U
    ))
    mulers(1).io.dataa := Mux1H(Seq(
        (update_conf_r === 0.U) -> Dot_in.z,
        (update_conf_r === 1.U) -> Dot_in.x,
        (update_conf_r === 2.U) -> Dot_in.y,
        (update_conf_r === 3.U) -> 0.U
    ))
    mulers(2).io.dataa := Mux1H(Seq(
        (update_conf_r === 0.U) -> Dot_in.z,
        (update_conf_r === 1.U) -> Dot_in.x,
        (update_conf_r === 2.U) -> Dot_in.y,
        (update_conf_r === 3.U) -> 0.U
    ))
    mulers(3).io.dataa := Mux1H(Seq(
        (update_conf_r === 0.U) -> Dot_in.y,
        (update_conf_r === 1.U) -> Dot_in.z,
        (update_conf_r === 2.U) -> Dot_in.x,
        (update_conf_r === 3.U) -> 0.U
    ))

    mulers(0).io.datab := "h3f7c1c5c".U //cos10
    mulers(1).io.datab := "h3e31d0d4".U //sin10
    mulers(2).io.datab := "h3f7c1c5c".U //cos10
    mulers(3).io.datab := "hbe31d0d4".U //-sin10

    adders(0).io.clock := clock
    adders(1).io.clock := clock

    adders(0).io.dataa := mulers(0).io.result
    adders(0).io.datab := mulers(1).io.result
    adders(1).io.dataa := mulers(2).io.result
    adders(1).io.datab := mulers(3).io.result

    when(update_conf_r === 0.U){
        Dot_out.x := xyz_r(15)
        Dot_out.y := adders(0).io.result
        Dot_out.z := adders(1).io.result
    }.elsewhen(update_conf_r === 1.U){
        Dot_out.x := adders(1).io.result
        Dot_out.y := xyz_r(15)
        Dot_out.z := adders(0).io.result
    }.otherwise{
        Dot_out.x := adders(0).io.result
        Dot_out.y := adders(1).io.result
        Dot_out.z := xyz_r(15)
    }

}