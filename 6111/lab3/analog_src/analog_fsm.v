module analog_fsm(clk, rst, io, ad_status, ad_rwb, ad_cseb, da_csb);
// debug signals
// port_rwb, pulse, ad_busy, ad_start);
// debug signals
// output port_rwb, pulse, ad_busy, ad_start;

   // global signals
   input clk, rst;

   // interface
   inout [7:0] io;
   output      ad_cseb, ad_rwb, da_csb; // da_ceb will be grounded
   input       ad_status;
   
   // internal signals
   wire        reset;
   wire        pulse;
   wire        ad_done, ad_busy, ad_start, port_rwb;

   // modules
   ioport gport (clk, port_rwb, io);
   // ad_data_ready is not really used since we will assume that
   // whenever the busy signal goes from high to low, the data is ready
   adc gadc (clk, reset, ad_start, ad_busy, ad_data_ready, ad_done,
	   ad_status, ad_rwb, ad_cseb);
   divider gdivider (clk, reset, pulse);
   sync_bit gsync (clk, rst, reset);

   // state:
   // [1:0] each state should last at least four clock cycles -- this is
   //       so that the values for the inout ports can settle
   // [3:2] states corresponding to the following:
   //       00: write to DAC
   //       01: call minor adc controller FSM
   //       10: wait for minor FSM to finish
   //       11: read from adc
   reg [3:0]   state;

   reg da_csb; // see comment (***) at the bottom of this module

   always @ (posedge clk) begin
      if (reset) state <= 0;
      // wait for pulse to continue
      else if (~pulse && state == 4'b0011) state <= state;
      // wait for ack that minor fsm has been called
      else if (~ad_busy && state == 4'b0111) state <= state;
      // wait for ack that minor fsm has finished
      else if (ad_busy && state == 4'b1011) state <= state;
      else state <= state + 1;

      da_csb <= ~(~state[3] && ~state[2] && ~state[1]);
   end

   // for the DAC, one enable will be held low and the other high most
   // of the time.  when we want to do a read, the high signal will pulse
   // down, latching a new value
   
   // (***) 0 for 1st half of 00 state  This used to be here, but was causing glitches.  in
   // order to get rid of the glitches, da_csb is now a register
   // assign da_csb = ~(~state[3] && ~state[2] && ~state[1]);

   // send a start pulse at state 01 (1st cycle: state = 0b0100)
   assign ad_start = (~state[3] && state[2] && ~state[1] && ~state[0]);
   // done reading at the last cycle of the 11 state
   assign ad_done = (state[3] && state[2] && state[1] && state[0]);
   // tell ioport to READ from AD chip for 1st half of 11 state
   // assign port_rwb = (state[3] && state[2] && state[1]); // not long enough, needs to be
   assign port_rwb = ~ad_cseb;
endmodule
