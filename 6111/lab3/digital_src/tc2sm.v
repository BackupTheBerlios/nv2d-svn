// convert the 2's complement to unsigned
module tc2sm(in, out);
   input [7:0] in;
   output [7:0] out;

   assign {out[6:0]} = {in[6:0]};
   assign out[7] = ~in[7];
endmodule
