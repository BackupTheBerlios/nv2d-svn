module controller(clk, rst,
	sensor,
	walk_request,
	reprogram,
	time_param_sel,
	time_value,
	leds,
	);

	input clk, rst, sensor, walk_request, reprogram;
	input[1:0] time_param_sel;
	input[3:0] time_value;

	output[6:0] leds;

	wire reset_sync, sensor_sync, wr_sync, wr, wr_reset;
	wire prog_sync, start_timer, expired, onehzenable;
	wire[1:0] interval;
	wire[3:0] value;

	synchronizer g0 (clk, rst, sensor, walk_request, reprogram,
		reset_sync, sensor_sync, wr_sync, prog_sync);

	walk_register g1 (clk, reset_sync, wr_sync, wr, wr_reset);

	fsm g2 (clk, reset_sync, sensor_sync, prog_sync, wr, expired,
		leds[6:0], start_timer, interval[1:0], wr_reset);

	time_params g3 (clk, reset_sync, prog_sync, interval[1:0],
		time_param_sel[1:0], time_value[3:0], value[3:0]);

	timer g4 (clk, reset_sync, onehzenable, value[3:0],
		start_timer, expired);

	divider g5 (clk, reset_sync, onehzenable);
endmodule
