module divider(clk, reset_sync, out);
	parameter CLOCK_FREQ = 1843200;
	// for simulation and debugging
	// parameter CLOCK_FREQ = 10;

	input clk, reset_sync;
	output out;

	reg[21:0] ct;
	reg out;		// output

	always @ (posedge clk) begin
		// generate a pulse, not a step function
		if (out) out <= 0;

		if (reset_sync) ct <= 0;
		else ct <= ct + 1;

		if (ct == CLOCK_FREQ) begin
			out <= 1;
			ct <= 0;
		end
	end
endmodule
