package miniGPU

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

object MainDriver extends ChiselFlatSpec {
  def main(args: Array[String]): Unit = {
    (new ChiselStage).execute(
      args,
      Seq(ChiselGeneratorAnnotation(() => new VGA_mem())))
  }
}