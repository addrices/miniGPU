module fp_mul (
	input logic         clock,
	input logic [31:0]  dataa,
	input logic [31:0]  datab,
	output logic [31:0] result);
//latency = 6
    import "DPI-C" function void fp_mul(input int dataa,input int datab,output int result);

    reg [31:0] fp_float_a1;
    reg [31:0] fp_float_a2;
    reg [31:0] fp_float_a3;
    reg [31:0] fp_float_a4;
    reg [31:0] fp_float_a5;
    reg [31:0] fp_float_a6;

    reg [31:0] fp_float_b1;
    reg [31:0] fp_float_b2;
    reg [31:0] fp_float_b3;
    reg [31:0] fp_float_b4;
    reg [31:0] fp_float_b5;
    reg [31:0] fp_float_b6;

    logic [31:0] __fp_int;
    always_ff @(posedge clock) begin
        fp_float_a1 <= dataa;
        fp_float_a2 <= fp_float_a1;
        fp_float_a3 <= fp_float_a2;
        fp_float_a4 <= fp_float_a3;
        fp_float_a5 <= fp_float_a4;
        fp_float_a6 <= fp_float_a5;
        fp_float_b1 <= datab;
        fp_float_b2 <= fp_float_b1;
        fp_float_b3 <= fp_float_b2;
        fp_float_b4 <= fp_float_b3;
        fp_float_b5 <= fp_float_b4;
        fp_float_b6 <= fp_float_b5;
        fp_mul(fp_float_a6,fp_float_b6,__fp_int);
        result = __fp_int;
    end

endmodule