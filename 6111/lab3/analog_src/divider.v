// module sends a pulse at frequency of 20kHz
module divider(clk, reset, pulse);
   parameter CLOCK_FREQ = 92; // for 20khz pulse
   input     clk, reset;
   output    pulse;

   reg [21:0] ct;
   reg 	      pulse; // output

   always @ (posedge clk) begin
      // generate a pulse, not a step function
      if (pulse) pulse <= 0;
      
      if (reset) ct <= 0;
      else ct <= ct + 1;
      
      if (ct == CLOCK_FREQ) begin
	 pulse <= 1;
	 ct <= 0;
      end
   end
endmodule
