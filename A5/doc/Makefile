#!/bin/sh

INPUT=compiler488.man
PS_OUTPUT=$(INPUT:.man=.ps)
PDF_OUTPUT=$(INPUT:.man=.pdf)

pdf: $(PDF_OUTPUT)

ps: $(PS_OUTPUT)

$(PDF_OUTPUT): $(PS_OUTPUT)
	ps2pdf $<
	
$(PS_OUTPUT): $(INPUT)
	groff -mandoc $< > $@

clean:
	@rm -f $(PS_OUTPUT) $(PDF_OUTPUT)

