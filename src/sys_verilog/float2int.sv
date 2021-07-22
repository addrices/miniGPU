module fp_convert
(
    input  logic           clock,
    output logic [31:0]    fp_int,
    input  logic [31:0]    fp_float
);

    import "DPI-C" function void fp2int(input int fp_float,output int fp_int);

    logic [31:0] __fp_float;
    logic [31:0] __fp_int;
    assign __fp_float = fp_float;
    always_ff @(posedge clock) begin
        fp2int(__fp_float,__fp_int);
        fp_int <= __fp_int;
    end
endmodule