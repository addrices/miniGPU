package miniGPU

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import chisel3.core.SyncReadMem
class VGA_mem extends MultiIOModule{
    val v_addr = IO(Input(10.W))
    val h_addr = IO(Input(10.W))
    val data   = IO(Output(23.W))
    val update_en = IO(Input(Bool()))
    val Dot_Int_in = IO(Vec(Input(new Dots_point)))

    val Dots_Int_r = Seq.fill(5)(Reg(new Dots_point))
    val read_count_r = RegInit(0.U(3.W))

    val sIdle :: sRead :: sUpdate :: Nil =  Enum(2)
    val state_r := RegInit(sIdle)

    val VGAMem = SyncReadMem(16, Bit(width=1.W))
    val VGA_read = Wire(UInt(1.W))
    val VGAMemAddr = Wire(UInt(16.W))

    val up_v = RegInit(0.U(10.W))
    val up_h = RegInit(0.U(10.W))
    val up_addr = RegInit(0.U(10.W))

    VGAMemAddr := h_addr * 800 + v_addr
    up_addr := up_h * 800 + up_v
    VGA_read := VGAMem.read(VGAMemAddr,true.B)
    when(VGA_read === 0.U){
        data := "h000000".U
    }.otherwise{
        data := "hffffff".U
    }

    switch(state_r){
        is(sIdle){
            when(update_en){
                state_r := sRead
                read_count_r := 0.U
            }
        }
        is(sRead){
            read_count_r := read_count_r + 1.U
            when(read_count_r === 4.U){
                state_r := sUpdate
                up_v := 0.U
                up_h := 0.U
            }
        }
        is(sUpdate){
            when(up_v === 799.U){
                up_v := 0.U
                when(up_h === 599.U){
                    state_r := sIdle
                }.otherwise{
                    up_h := up_h + 1.U
                }
            }.otherwise{
                up_v := up_v + 1.U
            }
        }
    }
}