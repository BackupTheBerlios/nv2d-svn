module ioport(clk, rwb, st_ad, st_da, bus_port, ad_data, da_data);
	inout [7:0] bus_port;
	output [7:0] ad_data;
	input [7:0] da_data;
	input clk, rwb, st_ad, st_da;
	
	reg [7:0] rda, rad; // d->a and a-> registers respectively
	
	// when rwb signal low, d->a register contents put on bus
	assign bus_port = (rwb) ? 4'hz : rda;
	assign ad_data = rad;
	
	always @ (posedge clk) begin
		if (rwb && st_ad) rad <= bus_port;
		if (st_da) rda <= da_data;
	end
endmodule
