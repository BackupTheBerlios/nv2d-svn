// basic module to synchronize one signal
module sync_bit(clk, in, out);
   input  clk, in;
   output out;
   
   reg 	  r1, r2;
   always @ (posedge clk) begin
      r1 <= in;
      r2 <= r1;
   end
   assign out = r2;
endmodule