// This module can be divided into two components, the programming
// portion and the portion controlling the FSM.  Internally, it
// maintains a 3 banks of registers (4 bits wide).  The registers
// can be programmed (which causes FSM to reset).
module time_params(clk, rst, s_prog, interval[1:0], tpsel[1:0],
	tv[3:0], value[3:0]);

	input clk, rst, s_prog;	// standard vars
	
	// the interval the current FSM state is interested in
	input[1:0] interval;
	// for programming; the time selector
	input[1:0] tpsel;
	// for programming; the time value
	input[3:0] tv;
	output[3:0] value;

	reg[3:0] t_base, t_yel, t_ext, value;
	always @ (posedge clk) begin
		if (rst) begin
			t_base <= 4'b0110;	// 6 seconds
			t_ext <= 4'b0011;	// 3 seconds
			t_yel <= 4'b0010;	// 2 seconds
		end

		// programming section
		else if (s_prog) begin
			case (tpsel)
				2'b00 : t_base <= tv;
				2'b01 : t_ext <= tv;	
				2'b10 : t_yel <= tv;	
				default : ;	// nothing
			endcase
		end

		// mux to select output
		case (interval)
			2'b00 : value <= t_base;
			2'b01 : value <= t_ext;
			2'b10 : value <= t_yel;
			default : value <= t_base;
		endcase
	end
endmodule
