module dfilter(clk, rst, ad_status, ad_rwb, ad_cseb, da_csb, romsel, fsel, bus
// debug signals
//, facc, ad_start, ad_busy, conv_start, conv_busy
, qrom, qram, ram_addr, ram_we//, da_data
);
// debug signals
//output  facc, ad_start, ad_busy, conv_start, conv_busy;
//wire ad_start, ad_busy, conv_start, conv_busy;
output [7:0] qram, qrom;
output ram_we;
output [3:0] ram_addr;
//output [7:0] da_data;
// end debug

	input clk, rst;
	input [1:0] romsel, fsel;
	inout [7:0] bus;

	// control signals from/to outside
	input ad_status;
	output ad_rwb, ad_cseb, da_csb;

	// global signals
	//wire heartbeat; // 20 khz pulse signal
	//wire reset; // sync'd reset

	divider gdiv (.clk(clk), .reset(reset), .pulse(heartbeat));

	// synchronize reset
	sync_bit gs1 (.clk(clk), .in(rst), .out(reset));

	// romsel - synchronized user input signal (top 2 bits of ROM addr)
	// rangesel - non-sync'd range select for filter output (gnd OR src'd)
	wire [7:0] qram, qrom; // data from ram and rom, gets sent into math unit
	//wire ram_we; // RAM write enable
	//wire [3:0] ram_addr, rom_addr; // RAM & ROM address
	wire [7:0] ad_data; // data to write to RAM (IO -> RAM)

	// note the conversion from unsigned to 2's complement in the .data input
	ram mem1 (.address(ram_addr), .we(ram_we),
		// .data({~ad_data[7], ad_data[6:0]}), // used to flip bits, but should not
		.data(ad_data),
		.q(qram));

	wire [3:0] rom_addr;
	rom mem2 (.address({romsel[1:0], rom_addr[3:0]}), .q(qrom));

	//wire io_rwb; // IO Read/Write_bar
	//wire adr_we; // A->D register write enable
	//wire dar_we; // D->A register write enable
	wire [7:0] da_data; // data to write to IO (ARITH -> IO)
	ioport qio (.clk(clk), .rwb(io_rwb), .st_ad(adr_we), .st_da(dar_we),
		.bus_port(bus), .ad_data(ad_data), .da_data(da_data));

	// control signals to the filter to zero the accumulator or to accumulate
	// new value respectively
	//wire fzero, facc;
	filter qf (.clk(clk), .reset((reset || fzero)), .rsel(fsel), .accum(facc),
		.qram(qram), .qrom(qrom), .da_data(da_data));

	// FSM declaration, the following is the list of control signals
	// this module needs to handle:
	//   ram_we
	//   ram_addr
	//   rom_addr
	//   io_rwb
	//   adr_we
	//   dar_we
	//   [signals from pins: ad_rwb, ad_cseb, da_csb]
	//   fzero	[signals to the filter (zero the accumulator)]
	//   facc	[signals to the filter (accumulate)]
	
	majorfsm nmajor(.clk(clk), .reset(reset), .heartbeat(heartbeat),
		// input from outside
		.ad_status(ad_status),
		// control signals
		.ram_we(ram_we), .ram_addr(ram_addr), .rom_addr(rom_addr),
		.io_rwb(io_rwb), .adr_we(adr_we), .dar_we(dar_we),
		.fzero(fzero), .facc(facc),
		// control to pins
		.ad_rwb(ad_rwb), .ad_cseb(ad_cseb), .da_csb(da_csb)
		);

endmodule
