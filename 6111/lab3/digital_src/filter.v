// a zero signal should be or'd with the reset signal
module filter(clk, reset, rsel, accum, qram, qrom, da_data);
	input clk, reset;
	
	input [1:0] rsel; // range select
	input accum; // pulse to advance accumulator
	input [7:0] qram, qrom;
	output [7:0] da_data;
	
	wire [7:0] da_data; // result of selector
	wire [7:0] b; // qrom -> 2's complement
	wire [19:0] mval, aval; // result of mult & accum respectively
	
	tc2sm conv2tc (qrom, b);
	
	mult gm(qram, b, mval);
	
	accumulator ga(.clk(clk), .reset(reset), .en(accum), .in(mval),
		.value(aval));
	
	// range selector mux
	assign da_data = (~rsel[1] && ~rsel[0]) ? aval[19:12] :
		(~rsel[1] && rsel[0]) ? aval[18:11] :
		(rsel[1] && ~rsel[0]) ? aval[17:10] :
		aval[16:9];
endmodule
	