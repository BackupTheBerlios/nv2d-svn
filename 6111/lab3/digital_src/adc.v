// Analog->Digital controller
module adc(clk, reset, start, busy, io_rwb, st_da, ad_status, ad_rwb, ad_cseb);
   
   input clk, reset;
   
   // done_reading - this is a pulse sent by an outsider to indicate
   //                that it has read the value at the data pins of the
   //                a/d chip it will cause the state of this FSM to
   //                be set back to S_IDLE
   input start; // from system (major fsm)
   input ad_status; // from ad chip (status)
   output ad_rwb, ad_cseb; // go to the ad chip
   output busy; // to major fsm
   output io_rwb; // control ioport
   
   parameter 	S_IDLE = 0;
   parameter 	S_SAMPLE = 1;
   parameter 	S_SAMPLE_WAIT = 2;
   parameter 	S_READ = 3;
   parameter 	S_DATA_READY = 4; // save to register
   
   // registers, control signals
   reg 		busy, io_rwb, st_da;
   // control signals sent to A/D chip (format and BPO are tied to source)
   reg [1:0] 	adsig; // [R/Wb, CEb = CSb]
   reg [2:0]    state;
   
   assign 	ad_rwb = adsig[1];
   assign 	ad_cseb = adsig[0];
   
   // outputs and transition control
   always @ (posedge clk) begin
      if(reset) begin
	 busy <= 0;
	 state <= S_IDLE;
      end
      else begin
	 // outputs in each state
	 case(state)
	   S_IDLE: begin 
	      adsig <= 2'b11;
	      busy <= 0;
	      io_rwb <= 0;
	      st_da <= 0;
	   end
	   S_SAMPLE: begin adsig <= 2'b00; busy <= 1; end
	   S_SAMPLE_WAIT: adsig <= 2'b11;
	   S_READ: begin
	      adsig <= 2'b10; // hold this for two clock cycles
	      io_rwb <= 1;
	   end
	   S_DATA_READY: begin
	      adsig <=2'b10;
	      busy <= 0;
	      st_da <= 1;
	   end
	 endcase // case(state)
	 
	 // transitions for each state
	 case(state)
	   S_IDLE: state <= start ? S_SAMPLE : S_IDLE;
	   // wait until status goes high
	   S_SAMPLE: state <= ad_status ? S_SAMPLE_WAIT : S_SAMPLE;
       // wait until status goes low
	   S_SAMPLE_WAIT: state <= ad_status ? S_SAMPLE_WAIT : S_READ;
	   S_READ: state <= S_DATA_READY;
	   S_DATA_READY: state <= done_reading ? S_IDLE : S_DATA_READY;
	 endcase // case(state)
      end // else: !if(reset)
   end // always @ (posedge clk)
endmodule // adc
