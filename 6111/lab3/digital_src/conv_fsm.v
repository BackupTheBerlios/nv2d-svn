module conv_fsm(clk, reset, start, busy,
		counter, dar_we, fzero, facc);

	input clk, reset, start;
	output busy, dar_we, fzero, facc;
	output[3:0] counter;

	parameter S_IDLE = 7'b1000110;

	// wire [3:0] counter;
	// wire busy, fzero, facc, dar_we;
	reg [6:0] state;

	wire [3:0] counter;

	assign counter = state[6] ? 0 : state[5:2];
	assign busy = ~(state == S_IDLE);
	assign fzero = ~busy;
	assign facc = (state[1] && state[0] && ~state[6]); // 7'b0xxxx11
	assign dar_we = (state == 7'b1000010);

	always @ (posedge clk) begin

		if (reset) state <= S_IDLE;
		else if (start) state <= 0;
		// doesn't move
		else if (state == S_IDLE) state <= state;
		else state <= state + 1;
	end
endmodule
