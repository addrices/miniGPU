package miniGPU

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

class Control extends MultiIOModule{
    val sw_spin = IO(Input(UInt(2.W)))      //0 x-spin 1 y-spin 2 z-spin
    val sw_shift = IO(Input(UInt(2.W)))     //0 left-shift 1 right-shift 2 up-shift 3 
    val button_spin = IO(Input(Bool()))
    val button_shift = IO(Input(Bool()))
    
    val update_out = IO(Output(Bool()))
    val update_spsh = IO(Output(Bool()))    //0 spin 1 shift
    val update_conf = IO(Output(UInt(2.W)))

    val button_spin_r = RegInit(true.B)
    val button_shift_r = RegInit(true.B)

    button_spin_r := button_spin
    button_shift_r := button_shift

    val button_spin_event = Wire(Bool())
    val button_shift_event = Wire(Bool())
    button_spin_event := button_spin_r === 1.U & button_spin === 0.U
    button_shift_event := button_shift_r === 1.U & button_shift === 0.U

    when(button_spin_event){
        printf("spin %d %d \n",update_spsh,update_conf)
        update_out := true.B
        update_spsh := false.B
        update_conf := sw_spin
    }.elsewhen(button_shift_event){
        printf("shift\n")
        update_out := true.B
        update_spsh := true.B
        update_conf := sw_shift
    }.otherwise{
        update_out := false.B
        update_spsh := DontCare
        update_conf := DontCare        
    }
    // printf("%x %x\n",button_shift_r,button_shift_event)

}