module fsm(clk, rst,
	s_sensor, s_prog, wr, expired,
	leds, start_timer, interval, wr_reset);

	parameter S_RESET = 0;
	parameter S_main_green = 1;
	parameter S_main_green_2 = 2;
	parameter S_main_yellow = 3;
	parameter S_side_green = 4;
	parameter S_side_yellow = 5;
	parameter S_walk = 6;
	parameter S_main_extra = 7;
	parameter S_side_extra = 8;

	input clk, rst, s_sensor, s_prog, wr, expired;
	output[6:0] leds;	// Order: Rm Ym Gm Rs Ys Gs W
	output start_timer, wr_reset;
	output[1:0] interval;

	// start_timer and wr_reset need to ouput pulses,
	// so we use two registers to do this (see last part
	// of the 'always' block)
	reg start_timer, start_timer_alpha, wr_reset_alpha, wr_reset;
	reg[3:0] state;
	reg[6:0] leds;
	reg[1:0] interval;

	always @ (posedge clk) begin
		if (rst | s_prog) state <= S_RESET;
		else case (state)
			S_main_green:
				state <= (expired ?
					(s_sensor ? S_main_extra : S_main_green_2) : state);
			S_main_green_2:
				state <= (expired ? S_main_yellow : state);
			S_main_yellow:
				state <= (expired ? (wr ? S_walk : S_side_green) : state);
			S_side_green:
				state <= (expired ?
					(s_sensor ? S_side_extra : S_side_yellow) : state);
			S_side_yellow:
				state <= (expired ? S_main_green : state);
			S_walk: begin
					// at the end of walk, we need to send
					// a walk_reset pulse out
					if (expired) begin
						wr_reset_alpha <= 1;
						state <= S_side_green;
					end
				end
			S_main_extra:
				state <= (expired ? S_main_yellow : state);
			S_side_extra:
				state <= (expired ? S_side_yellow : state);
			default:
				state <= (expired ? S_main_green : state);
		endcase

		// WR_RESET signal pulse
		// make sure that walk_reset signal is a pulse
		if (wr_reset_alpha == 1) wr_reset_alpha <= 0;
		wr_reset <= wr_reset_alpha;

		// the following line makes sure that the 'start_timer'
		// signal is a PULSE whenever states are changed
		if (start_timer_alpha == 1) start_timer_alpha <= 0;
		// NOTE: this must come AFTER the resetting of
		// start_timer to zero
		if (expired) start_timer_alpha <= 1;

		start_timer <= start_timer_alpha;


		// make sure the ouputs are correct for the states.
		case (state)
			S_main_green:
				begin leds <= 7'b0011000; interval <= 2'b00; end
			S_main_green_2:
				begin leds <= 7'b0011000; interval <= 2'b00; end
			S_main_yellow:
				begin leds <= 7'b0101000;	interval <= 2'b10; end
			S_side_green:
				begin leds <= 7'b1000010; interval <= 2'b00; end
			S_side_yellow:
				begin leds <= 7'b1000100; interval <= 2'b10; end
			S_walk:
				begin leds <= 7'b1001001; interval <= 2'b01; end
			S_main_extra:
				begin leds <= 7'b0011000; interval <= 2'b01; end
			S_side_extra:
				begin leds <= 7'b1000010; interval <= 2'b01; end
			default:
				// a few seconds of all red
				begin leds <= 7'b1001000; interval <= 2'b01; end 	
		endcase
	end
endmodule
