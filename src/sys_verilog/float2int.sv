module fp_convert
(
    input  logic           clock,
    input  logic           reset,
    output logic [31:0]    fp_int,
    input  logic [31:0]    fp_float,
    output logic           ge,
    output logic 
)

    import "DPI"