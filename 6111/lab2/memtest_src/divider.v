module divider(clk, rst, out);
	// 1843200 / 4 = 460800
	parameter CT = 230400;

	input clk, rst;
	output out;

	reg[19:0] ct;
	reg out;		// output

	always @ (posedge clk) begin
		if (rst) begin
			out <= 0;
			ct <= 0;
		end

		else ct <= ct + 1;

		if (ct == CT) begin
			out <= ~out;
			ct <= 0;
		end
	end
endmodule
