module majorfsm (clk, reset, heartbeat,
ram_we, ram_addr, rom_addr,
io_rwb, adr_we, dar_we);

	input clk, reset, heartbeat;
	output ram_we, ram_addr, rom_addr, io_rwb, adr_we, dar_wr;
	
	reg [3:0] rbptr;  // ring buffer pointer
	
	always @ (posedge clk) begin
		if (reset) rbptr <= 0;
		else if (heartbeat) rbptr <= rbptr + 1;
	end
	
	
	
endmodule