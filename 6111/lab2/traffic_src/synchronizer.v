module synchronizer(
	clk, reset, sensor, walk, reprog,	// inputs
	s_reset, s_sensor, s_wr, s_reprog);	// outputs

	input clk, reset, sensor, walk, reprog;
	output s_reset, s_sensor, s_wr, s_reprog;

	reg rs1, rs2;

	exsync i_reset (clk, reset, s_reset);
	// exsync i_sensor(clk, sensor, s_sensor);
	exsync i_wr (clk, walk, s_wr);
	exsync i_reprog (clk, reprog, s_reprog);

	// synchronize sensor input
	always @ (posedge clk) begin
		rs1 <= sensor;
		rs2 <= rs1;
	end
	assign s_sensor = rs2;
endmodule
