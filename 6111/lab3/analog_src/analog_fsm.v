module analog_fsm(clk, reset, ad_status, ad_rwbar, ad_csebar);
   input       clk, reset;
   input       ad_status;
   output      ad_rwbar, ad_csebar; // ad_csebar goes to ad chip (cebar = csbar)

   wire        start_adc, busy_adc;
   
   wire        data_ready, done_reading;

   // the enables for the A->D and D->A respectively
   wire        ad_cseb, da_cseb; 
   

   // state:
   // [1:0] each state should last at least four clock cycles -- this is
   //       so that the values for the inout ports can settle
   // [3:2] states corresponding to the following:
   //       00: write to DAC
   //       01: call minor adc controller FSM
   //       10: wait for minor FSM to finish
   //       11: read from adc
   reg [3:0]   state;

   always @ (posedge clk) begin
      if (reset) state <= 0;
      else if (~busy_adc && state == 4'b0111) state <= state;
      else if (busy_adc && state == 4'b1011) state <= state;
      else state <= state + 1;
   end
   
   // 0 for 1st half of 00 state
   assign da_cseb = ~(~state[3] && ~state[2] && ~state[1]);
   // done reading at the last cycle of the 11 state
   assign done_reading = (state[3] && state[2] && state[1] && state[0]);
   // tell ioport to READ from AD chip for 1st half of 11 state
   assign portrwb = (state[3] && state[2] && state[1]);
endmodule
