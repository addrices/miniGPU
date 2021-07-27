package miniGPU

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class Top extends MultiIOModule{
    val sw_spin = IO(Input(UInt(2.W)))      //0 x-spin 1 y-spin 2 z-spin
    val sw_shift = IO(Input(UInt(2.W)))     //0 left-shift 1 right-shift 2 up-shift 3 
    val button_spin = IO(Input(Bool()))
    val button_shift = IO(Input(Bool()))

    val v_addr = IO(Input(UInt(10.W)))
    val h_addr = IO(Input(UInt(10.W)))
    val data   = IO(Output(UInt(24.W)))
    val debug  = IO(Input(UInt(10.W)))

    val ctrl = Module(new Control)
    val dots = Module(new Dots)
    val dot2int = Module(new Dots2Int)
    val vga_mem = Module(new VGA_mem)
    
    ctrl.sw_spin <> sw_spin
    ctrl.sw_shift <> sw_shift
    ctrl.button_spin <> button_spin
    ctrl.button_shift <> button_shift

    dots.update_conf <> ctrl.update_conf
    dots.update_in <> ctrl.update_out
    dots.update_spsh <> ctrl.update_spsh
    dots.update_conf <> ctrl.update_conf

    dots.Dot_io <> dot2int.Dots_in
    dots.update_out <> dot2int.update_in

    vga_mem.update_in <> dot2int.update_out
    vga_mem.dot_Int_in <> dot2int.Dots_out
    vga_mem.v_addr <> v_addr
    vga_mem.h_addr <> h_addr
    vga_mem.data <> data
    vga_mem.debug <> debug
}