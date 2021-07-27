module vgamem (
	input  logic [15:0] address,
	input  logic        clock,
	input  logic        data,
	input  logic        wren,
	output logic        q);

reg [0:0] mem_s [0:65535];
reg [15:0] address_r;

always@(posedge clock) begin
    address_r <= address;
    if(wren) begin
        mem_s[address] <= data;
    end
end
assign q = mem_s[address_r];
endmodule