module majorfsm (clk, reset, heartbeat,
	// input from outside
	ad_status,
	// control signals
	ram_we, ram_addr, rom_addr, io_rwb, adr_we, dar_we,
	fzero, facc,
	// control to pins
	ad_rwb, ad_cseb, da_csb);

	input clk, reset, heartbeat;
	input ad_status;
	output ram_we, io_rwb, adr_we, dar_we, ad_rwb;
	output ad_cseb, da_csb, fzero, facc;
	output [3:0] ram_addr, rom_addr;

	parameter S_DAC_OUTPUT = 0;
	parameter S_ST_AD_PREP = 1;	// prepare to store to RAM
	parameter S_ST_AD_WE = 2; // send we signal pulse
	parameter S_ST_AD_ST = 3; // send we signal pulse
	parameter S_CALL_ADFSM = 4;
	parameter S_CALL_ADFSM_WAIT = 5;
	parameter S_CALL_CONVOLVE = 6;
	parameter S_CALL_CONVOLVE_WAIT = 7;
	parameter S_WAIT = 8;
	parameter S_IDLE = 9;

	reg [3:0] rbptr; // ring buffer pointer

	reg [3:0] state;

	// internal signals
	wire ad_busy, conv_busy;

	// output registers to prevent glitching
	reg da_csb, ram_we;
	reg ad_start, conv_start;
	reg [3:0] ram_addr;

	wire [3:0] rom_addr;

	// **** A->D Controller MINOR FSM DECLARATION
	ad_fsm qafsm (.clk(clk), .reset(reset), .start(ad_start),
		.busy(ad_busy), .io_rwb(io_rwb), .st_ad(adr_we),
		.ad_status(ad_status), .ad_rwb(ad_rwb), .ad_cseb(ad_cseb));

	// **** Convolution MINOR FSM DECLARATION
	conv_fsm qcfsm (.clk(clk), .reset(reset), .start(conv_start),
		.busy(conv_busy), .counter(rom_addr), .dar_we(dar_we),
		.fzero(fzero), .facc(facc));
	

	always @ (posedge clk) begin
		ram_addr <= rbptr - rom_addr;

		if (reset) begin
			rbptr <= 0;
			state <= S_IDLE;
		end
		else begin
			if (heartbeat) rbptr <= rbptr + 1;

			// outputs
			da_csb <= ~(state == S_DAC_OUTPUT);
			ram_we <= (state == S_ST_AD_WE || state == S_ST_AD_ST);
			ad_start <= (state == S_CALL_ADFSM);
			conv_start <= (state == S_CALL_CONVOLVE);

			// transitions
			case (state)
			  S_DAC_OUTPUT: state <= S_ST_AD_PREP;
			  S_ST_AD_PREP: state <= S_ST_AD_WE;
			  S_ST_AD_WE: state <= S_ST_AD_ST;
			  S_ST_AD_ST: state <= S_CALL_ADFSM;
			  // wait until minor fsm is busy
			  S_CALL_ADFSM: state <= S_CALL_ADFSM_WAIT;
			  S_CALL_ADFSM_WAIT: state <= ad_busy ? S_CALL_CONVOLVE : S_CALL_ADFSM_WAIT;
			  // wait until minor fsm is busy
			  S_CALL_CONVOLVE: state <= S_CALL_CONVOLVE_WAIT;
			  S_CALL_CONVOLVE_WAIT: state <= conv_busy ? S_WAIT : S_CALL_CONVOLVE_WAIT;
			  // wait until both fsms are done
			  S_WAIT: state <= (conv_busy || ad_busy) ? S_WAIT : S_IDLE;
			  // wait for the next divider heartbeat signal
			  S_IDLE: state <= heartbeat ? S_DAC_OUTPUT : S_IDLE;
			endcase
		end
	end
endmodule


