module accumulator(clk, reset, en, in, value);
	// en - accumulate the value at in[15:0] (should be a pulse)
	input clk, reset, en;
	input [15:0] in;
	output [19:0] value;
	
	wire [3:0] signext; // four bits to sign extend in
	
	reg [19:0] value;
	
	assign signext = {in[15], in[15], in[15], in[15]};
	
	always @ (posedge clk) begin
		// in needs to be sign extended
		if (reset) value <= 0;
		else if (en) value <= value + {signext, in};
	end
endmodule