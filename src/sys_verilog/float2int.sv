// module fp_convert_chisel
// (
//     input logic io_clock,
//     output logic [31:0] io_result,
//     input logic [31:0] io_dataa
// );

// fp_convert fp_convert_impl(.clock(io_clock),.result(io_result),.dataa(io_dataa));    

// endmodule

module fp_convert
(
    input  logic           clock,
    output logic [31:0]    result,
    input  logic [31:0]    dataa
);

    import "DPI-C" function void fp2int(input int dataa,output int result);

    reg [31:0] fp_float_1;
    reg [31:0] fp_float_2;
    reg [31:0] fp_float_3;
    reg [31:0] fp_float_4;
    reg [31:0] fp_float_5;
    reg [31:0] fp_float_6;

    logic [31:0] __fp_int;
    always_ff @(posedge clock) begin
        fp_float_1 <= dataa;
        fp_float_2 <= fp_float_1;
        fp_float_3 <= fp_float_2;
        fp_float_4 <= fp_float_3;
        fp_float_5 <= fp_float_4;
        fp_float_6 <= fp_float_5;
        // $display("%d",dataa);
        // $display("%d",fp_float_1);
        // $display("%d",fp_float_2);
        // $display("%d",fp_float_3);
        // $display("%d",fp_float_4);
        // $display("%d",fp_float_5);
        // $display("result:%d\n",result);
        fp2int(fp_float_6,__fp_int);
        result = __fp_int;
    end

endmodule