// bex: non-sync'ed input sync'ed
module exsync(clk, in, out);
	input clk, in;
	output out;

	reg r1, r2, r3;
	always @ (posedge clk) begin
		r1 <= in;
		r2 <= r1;
		r3 <= r2;
	end

	assign out = ~r3 & r2;
endmodule
