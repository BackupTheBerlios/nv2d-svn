# make sure that the papersize is set to letter (texconfig)

default:
	lgrind -t 4 -i -lverilog ../traffic_src/controller.v > controller.v.latex
	lgrind -t 4 -i -lverilog ../traffic_src/divider.v > divider.v.latex
	lgrind -t 4 -i -lverilog ../traffic_src/exsync.v > exsync.v.latex
	lgrind -t 4 -i -lverilog ../traffic_src/fsm.v > fsm.v.latex
	lgrind -t 4 -i -lverilog ../traffic_src/synchronizer.v > synchronizer.v.latex
	lgrind -t 4 -i -lverilog ../traffic_src/time_params.v > timeparams.v.latex
	lgrind -t 4 -i -lverilog ../traffic_src/timer.v > timer.v.latex
	lgrind -t 4 -i -lverilog ../traffic_src/walk_register.v > walkregister.v.latex

	latex report.tex
	latex report_ci.tex
	dvips -Pcmz -t letter report.dvi -o report.ps
	dvips -Pcmz -t letter report_ci.dvi -o report_ci.ps
