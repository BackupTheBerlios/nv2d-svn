// a zero signal should be or'd with the reset signal
module filter(clk, reset, rsel, accum, qram, qrom, da_data
// debug
//, mval, aval
);
//output [20:0] mval, aval;
//end debug
	input clk, reset;
	
	input [1:0] rsel; // range select
	input accum; // pulse to advance accumulator
	input [7:0] qram, qrom;
	output [7:0] da_data;
	
	wire [7:0] qram, qrom;

	// wire [7:0] da_data;	// result of selector (unsigned),
	wire [7:0] da2c;	//2's complement version
	wire [7:0] b; // qrom -> 2's complement
	wire [19:0] aval;
	wire [15:0] mval;
	// wire [19:0] mval, aval; // result of mult & accum respectively
	
	// convert sigm multiple to two's complement
	assign b = ~qrom[7] ? qrom : ({1'b1, ~qrom[6:0]} + 1);
	
	mult gm(qram, b, mval);
	
	accumulator ga(.clk(clk), .reset(reset), .en(accum), .in(mval),
		.value(aval));
	
	// range selector mux
	assign da2c = (~rsel[1] && ~rsel[0]) ? aval[17:10] :
		(~rsel[1] && rsel[0]) ? aval[16:9] :
		(rsel[1] && ~rsel[0]) ? aval[15:8] :
		aval[14:7];
	assign da_data = {~da2c[7], da2c[6:0]};
endmodule
	
