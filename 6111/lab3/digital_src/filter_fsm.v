module filter_fsm(clk, reset, start, ram_ptr,
	// control signals
	busy,
	acc_zero, acc_en,
	da_reg_rwb,
	rom_addr,
	ram_addr, ram_we);
	
	//// inputs and outputs
	input clk, reset, start;
	output busy;
	
	// most recent location of RAM written to
	input [4:0] ram_ptr;
	
	// control signals
	output acc_zero, acc_en, da_reg_rwb, ram_we;
	output [4:0] rom_addr, ram_addr;
	
		
	//// registers and assignments
	// state assignments
	// reg[0] - a 2 cycle delay to give math output time to settle
	// reg[4:1] - 4 bit counter
	// reg[5] - done bit
	// the process is to count up until reg[5] is 1, which will
	// indicate that convolution is done.  the accumulator value
	// will then be fed into the da_register buffer
	reg [5:0] state;
	
	//control signal registers -- prevents glitching
	reg acc_zero, acc_en, da_reg_rwb, ram_we;
	reg [4:0] rom_addr, ram_addr;
	
	always @ (posedge clk) begin
		if (acc_zero == 1) acc_zero <= 0; // pulse
	
		if (reset || start) begin
			state <= 0;
			if (acc_zero == 0) acc_zero <= 1; // clear the accumulator
		end
		else if (state == 5'b11111) state <= state; // idle state
		else if (state[5] != 1) state <= state + 1;
		
		// wait a few cycles to write to the D-A buffer
		da_reg_rwb <= (state[5] && ~state[4] && ~state[3] && ~state[2] && state[1] && state[0]);
		
		// addresses
		rom_addr <= state[4:1];
		ram_addr <= ram_ptr - state[4:1];
		
		// ram is only readable for convolution.  all other stages, ram is writable
		ram_we <= state[5];
		// accumulator is enabled at the end of the delay of each state
		acc_en <= ~state[0];	
	end

endmodule