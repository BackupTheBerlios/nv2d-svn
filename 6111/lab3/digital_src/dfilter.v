module dfilter(clk, rst, romsel, fsel, bus);
	input clk, rst;
	input [1:0] romsel, fsel;
	inout [7:0] bus;

	// global signals
	wire heartbeat; // 20 khz pulse signal
	wire reset; // sync'd reset

	divider gdiv (.clk(clk), .reset(reset), .pulse(heartbeat));

	// synchronize reset
	sync_bit gs1 (.clk(clk), .in(rst), .out(reset));

	// romsel - synchronized user input signal (top 2 bits of ROM addr)
	// rangesel - non-sync'd range select for filter output (gnd OR src'd)
	wire [1:0] romsel, fsel;
	wire [7:0] qram, qrom; // data from ram and rom, gets sent into math unit
	wire ram_we; // RAM write enable
	wire [3:0] ram_addr, rom_addr; // RAM & ROM address
	wire [7:0] ad_data; // data to write to RAM (IO -> RAM)

	ram mem1 (.address(ram_addr), .we(ram_we),
		.inclock(clk), .outclock(clk), .data(ad_data), .q(qram));
	rom mem2 (.address({romsel, rom_addr}),
		.inclock(clk), .outclock(clk), .q(qrom));

	wire io_rwb; // IO Read/Write_bar
	wire adr_we; // A->D register write enable
	wire dar_we; // D->A register write enable
	wire [7:0] da_data; // data to write to IO (ARITH -> IO)
	ioport qio (.clk(clk), .rwb(io_rwb), .st_ad(adr_we), .st_da(dar_we),
		.bus_port(bus), .ad_data(ad_data), .da_data(da_data));

	filter qf (.clk(clk), .reset(reset), .rsel(fsel), .accum(heartbeat),
		.qram(qram), .qrom(qrom), .da_data(da_data));

	// FSM declaration, the following is the list of control signals
	// this module needs to handle:
	//   ram_we
	//   ram_addr
	//   rom_addr
	//   io_rwb
	//   adr_we
	//   dar_we
	
	// majorfsm qmf (.clk(clk), .reset(reset), .heartbeat(heartbeat),
	//	.ram_we(ram_we), .ram_addr(ram_addr), .rom_addr(rom_addr),
	//	.io_rwb(io_rwb), .adr_we(adr_we), .dar_we(dar_we));

endmodule
