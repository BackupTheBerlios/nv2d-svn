module ioport(clk, rwb, port);
	inout [7:0] port;
	input clk, rwb;

	reg [7:0] buffer;

	assign port = (rwb) ? 4'hz : buffer;

	always @ (posedge clk) begin
		if (rwb) buffer <= port;
	end
endmodule
