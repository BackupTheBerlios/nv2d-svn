// addr_lsb - the LSB current memmory ADDRESS we are working with
// phase - are we on the first runthrough of the check or the second?
// for lab2, these should be: state[2], state[7]
module getval(addr_lsb, phase, value);
   output [3:0] value;
   input 	addr_lsb, phase;
   
   // these are the values we write to
   wire [3:0] 	even, odd, value;
   assign 	even = 4'b0101;
   assign 	odd = 4'b1010;
   // state[2] tells us whether the current address is even or odd (0-even, 1-odd)
   // state[7] tells us whether we are in 1st runthrough or second
   // current_mval should be the value to write/compare to
   assign 	value = (~phase && addr_lsb) ? odd : even;
endmodule // getval
