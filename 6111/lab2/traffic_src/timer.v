module timer(clk, rst, divider, value, start_timer, expired);
	input clk, rst, divider, start_timer;
	input[3:0] value;
	output expired;

	reg[3:0] ct;	// the current count
	reg expired;
	reg start_timer_delay;

	// when the timer recieves the start_timer signal,
	// it will grab the value given by the time parameters
	// module and count down using the 1 hz enable signal.
 	// once it reaches zero, it will set expired high
	always @ (posedge clk) begin
		start_timer_delay <= start_timer;

		if (expired == 1) expired <= 0;

		if (rst) ct <= 3;
		// note: the time_value is being read one clock cycle too early.
		// to correct this, we will pass it through another register (start_timer_delay).
		else if (start_timer_delay) ct <= value;
		else if (divider) begin
			ct <= ct - 1;
			if (ct == 1) expired <= 1;
		end
	end
 endmodule

			
