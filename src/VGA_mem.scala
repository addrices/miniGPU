package miniGPU

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import chisel3.core.SyncReadMem


class VGA_mem extends MultiIOModule{
    val v_addr = IO(Input(UInt(10.W)))
    val h_addr = IO(Input(UInt(10.W)))
    val data   = IO(Output(UInt(24.W)))
    val update_in = IO(Input(Bool()))
    val dot_Int_in = IO(Input(new Dots_point))
    val debug = IO(Input(Bool())) 


    val Dots_Int_r = Mem(5,new Dots_point)
    val read_count_r = RegInit(0.U(3.W))

    val sIdle :: sRead :: sUpdate :: Nil = Enum(3)
    val state_r = RegInit(sIdle)

    val VGAMem = SyncReadMem(16, Bits(width=1.W))
    val VGA_read = Wire(UInt(1.W))
    val VGAMemAddr = Wire(UInt(16.W))

    val up_v = RegInit(0.U(10.W))
    val up_h = RegInit(0.U(10.W))
    val up_addr = RegInit(0.U(20.W))
    val dot_flag = Wire(Bool())
    val check = Module(new CheckPix)

    for(i <- 0 to 4){
        check.dot_Int_In(i) := Dots_Int_r(i)
    }
    check.up_v <> up_v
    check.up_h <> up_h
    check.flag <> dot_flag

    VGAMemAddr := h_addr * 800.U + v_addr
    up_addr := up_h * 800.U + up_v
    VGA_read := VGAMem.read(VGAMemAddr,true.B)
    when(VGA_read === 0.U){
        data := "h000000".U
    }.otherwise{
        data := "hffffff".U
    }

    switch(state_r){
        is(sIdle){
            when(update_in){
                state_r := sRead
                read_count_r := 0.U
            }
        }
        is(sRead){
            read_count_r := read_count_r + 1.U
            Dots_Int_r(read_count_r) := dot_Int_in
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
            when(dot_flag === true.B){
                // printf("up_v:%d up_h:%d write %d 1\n",up_v,up_h,up_addr)
                VGAMem.write(up_addr,1.U)
            }.otherwise{
                VGAMem.write(up_addr,0.U)
            }
        }
    }
    when(debug){
        printf("Dots_Int_r0 %x %x %x\n",Dots_Int_r(0).x, Dots_Int_r(0).y, Dots_Int_r(0).z)
        printf("Dots_Int_r1 %x %x %x\n",Dots_Int_r(1).x, Dots_Int_r(1).y, Dots_Int_r(1).z)
        printf("Dots_Int_r2 %x %x %x\n",Dots_Int_r(2).x, Dots_Int_r(2).y, Dots_Int_r(2).z)
        printf("Dots_Int_r3 %x %x %x\n",Dots_Int_r(3).x, Dots_Int_r(3).y, Dots_Int_r(3).z)
        printf("Dots_Int_r4 %x %x %x\n",Dots_Int_r(4).x, Dots_Int_r(4).y, Dots_Int_r(4).z)
    }
}