# make sure that the papersize is set to letter (texconfig)

default:
	lgrind -t 4 -i -lverilog ../digital_src/ad_fsm.v > adfsm.v.latex
	lgrind -t 4 -i -lverilog ../digital_src/conv_fsm.v > convfsm.v.latex
	lgrind -t 4 -i -lverilog ../digital_src/majorfsm.v > majorfsm.v.latex

	latex report.tex
	dvips -Pcmz -t letter report.dvi -o report.ps
	ps2pdf report.ps
