module ioport(clk, rwb, port);
	inout [7:0] port;
	input clk, rwb;

	wire [7:0] converted_num;

	reg [7:0] buffer;

    tc2sm converter(buffer, converted_num);

	assign port = (rwb) ? 4'hz : converted_num;

	always @ (posedge clk) begin
		if (rwb) buffer <= port;
	end
endmodule
