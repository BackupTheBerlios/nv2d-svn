module walk_register(clk, rst, s_wr, wr, wr_reset);
	input clk, rst;
	input s_wr;		// the signal FROM the synchronizer
	input wr_reset;	// the signal FROM FSM

	output wr;		// the control signal TO the FSM

	reg state;

	always @ (posedge clk) begin
		if (rst | wr_reset) state = 0;
		else if (s_wr) state = 1;
	end

	assign wr = state;
endmodule
