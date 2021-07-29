package miniGPU

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import chisel3.core.SyncReadMem

class vgamem extends BlackBox{
    val io = IO(new Bundle {
        val address = Input(UInt(16.W))
        val clock   = Input(Clock())
        val data    = Input(Bool())
        val wren    = Input(Bool())
        val q       = Output(Bool())
    })
}

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

    val VGAMem = Seq.fill(8)(Module(new vgamem))
    val VGAMemAddr = Wire(UInt(19.W))

    val up_v = RegInit(0.U(10.W))
    val up_h = RegInit(0.U(10.W))
    val up_addr = RegInit(0.U(19.W))
    val VGAMemAddr_r = RegInit(0.U(19.W))
    val dot_flag = Wire(Bool())
    val check = Module(new CheckPix)
    val q = Wire(Bool())
    // val wren = Wire(Bool())

    for(i <- 0 to 4){
        check.dot_Int_In(i) := Dots_Int_r(i)
    }
    check.up_v <> up_v
    check.up_h <> up_h
    check.flag <> dot_flag

    VGAMemAddr := h_addr * 640.U + v_addr
    up_addr := up_h * 640.U + up_v 
    VGAMemAddr_r := VGAMemAddr

    q := false.B
    switch(VGAMemAddr_r(18,16)){
        is(0.U){q := VGAMem(0).io.q}
        is(1.U){q := VGAMem(1).io.q}
        is(2.U){q := VGAMem(2).io.q}
        is(3.U){q := VGAMem(3).io.q}
        is(4.U){q := VGAMem(4).io.q}
        is(5.U){q := VGAMem(5).io.q}
        is(6.U){q := VGAMem(6).io.q}
        is(7.U){q := VGAMem(7).io.q}
    }
 
    when(q === true.B){
        data := "hffffff".U
    }.elsewhen(h_addr === 240.U || v_addr === 320.U){
        data := "hffff00".U
    }.otherwise{
        data := "h000000".U
    }
    for(i <- 0 to 7){
        VGAMem(i).io.wren := false.B
        VGAMem(i).io.data := false.B
        VGAMem(i).io.address := Mux(state_r === sUpdate,up_addr(15,0),VGAMemAddr(15,0))
        VGAMem(i).io.clock := clock
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
            when(up_v === 639.U){
                up_v := 0.U
                when(up_h === 479.U){
                    printf("update over")
                    state_r := sIdle
                }.otherwise{
                    up_h := up_h + 1.U
                }
            }.otherwise{
                up_v := up_v + 1.U
            }
            for(i <- 0 to 7){
                when(i.U === up_addr(18,16)){
                    VGAMem(i).io.wren := true.B
                    VGAMem(i).io.data := dot_flag
                }
            }
            // when(dot_flag === true.B){
            //     printf("up_v:%d up_h:%d write %d 1\n",up_v,up_h,up_addr)
            // }
        }
    }
    // printf("state_r:%d,VGAMemAddr:%d wren:%d\n",state_r,VGAMemAddr,VGAMem.io.wren)
    when(debug){
        printf("Dots_Int_r0 %x %x %x\n",Dots_Int_r(0).x, Dots_Int_r(0).y, Dots_Int_r(0).z)
        printf("Dots_Int_r1 %x %x %x\n",Dots_Int_r(1).x, Dots_Int_r(1).y, Dots_Int_r(1).z)
        printf("Dots_Int_r2 %x %x %x\n",Dots_Int_r(2).x, Dots_Int_r(2).y, Dots_Int_r(2).z)
        printf("Dots_Int_r3 %x %x %x\n",Dots_Int_r(3).x, Dots_Int_r(3).y, Dots_Int_r(3).z)
        printf("Dots_Int_r4 %x %x %x\n",Dots_Int_r(4).x, Dots_Int_r(4).y, Dots_Int_r(4).z)
    }
}