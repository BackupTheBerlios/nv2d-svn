// mt_fsm: memory test FSM
module memtest(clk, reset, addr[3:0], data[3:0], wbar, gbar, success, failure, led_read, led_write,
// debug
correct_val);
   input clk, reset;
   // debug
   output correct_val;
   // debug
   output [3:0] addr;
   inout [3:0] 	data;
   output 	wbar, gbar, success, failure, led_read, led_write;

   // here is our state machine
   // state[1:0] - 4 cycles per access
   // state[5:2] - 16 spots to access in memory
   // state[7:6] - status
   //            - WRITE_1 0b00
   //            - READ_1  0b01
   //            - WRITE_2 0b10
   //            - READ_2  0b11
   // state[8]   - 0 if running, 1 if halted
   reg [8:0] 	state;

   reg 		test_failed, reg_rst_sync_a, rst;

   wire [3:0] 	correct_val;
   wire 	memclk;
   
   getval g_correct_val (state[2] , state[7], correct_val[3:0]);
   divider g_divider (clk, rst, memclk);

   // synchronize the reset register
   always @ (posedge clk) begin
      reg_rst_sync_a <= reset;
      rst <= reg_rst_sync_a;
   end

   // always @ (posedge memclk) begin
   always @ (posedge memclk) begin
      if (rst) begin
         test_failed <= 0;
         state <= 0;
      end


      // if in read phase and it is the end of 2nd cycle, check if memory is
      // returning correct data.  if not, halt + report fail
      if (!state[8] && state[6] && state[1:0] == 2'b01 && {data} != {correct_val}) begin
	 state[8] <= 1;
	 test_failed <= 1;
      end
      // increment state if we are still running
      else if (!state[8]) state <= state + 1;
   end


   // assert memory write enable during write phase on cycle 01
   assign wbar = !(!state[8] && !state[6] && state[1:0] == 2'b01);

   // assert memory output enable during read phase on cycles  01 and 10
   assign gbar = !(!state[8] && state[6] && (state[1:0] == 2'b01 || state[1:0] == 2'b10));

   // memory address comes from the counter
   assign addr = state[5:2];

   // output data to the memory during the write phase on cycles 01 and 10
   wire   oe;
   assign oe = !state[8] && !state[6] && (state[1:0] == 2'b01 || state[1:0] == 2'b10);
   // what to write comes from the little 'getval' module
   assign data = oe ? correct_val : 4'hz;

   // test result indicators
   assign success = state[8] && ~test_failed;
   assign failure = state[8] && test_failed;
   assign led_read = state[6] && !state[8];
   assign led_write = ~state[6] && !state[8];
   

endmodule // mt_fsm
