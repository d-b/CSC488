"""
Generate syntactically valid programs for the CSC488 language from the original
specification.
"""

from random import choice as rand_choice
from itertools import chain

import grammar

class Nonterminal(object):
    def __init__(self, func):
        self.func = func
        self.alts = []

    def generate(self, ctx):
        alt = rand_choice(self.alts)
        for term in alt:
            for token in term.generate(ctx):
                yield token

    def __repr__(self):
        return "<%s>" % (self.func.func_name, )

class Terminal(object):
    def __init__(self, token):
        self.token = token

    def generate(self, ctx):
        return [ self.token ]

    def __repr__(self):
        return "%s" % (self.token, )

class GrammarBuilder(object):
    def __init__(self):
        self.start_sym = None
        self.start_term = None
        self.sym2term = {}

    def build(self, start_sym):
        self.start_sym = start_sym

        processed = set()
        queue = set([ self.start_sym ])
        self.start_term = self.sym2term[self.start_sym] = Nonterminal(self.start_sym)

        while len(queue) > 0:
            symbol = queue.pop()
            assert symbol not in processed, symbol
            processed.add(symbol)

            term = self.sym2term[symbol]

            if not callable(symbol):
                raise

            alts = list(symbol() or [])
            alts = [ ( (alt,) if not isinstance(alt, tuple) else alt) for alt in alts ]
            subs = set([ sub for sub in chain(*alts) if callable(sub) ])
            never_before_seen = subs - processed
            queue.update(never_before_seen)

            for alt in alts:
                new_alt = [ self.symbol_to_terminal(t) for t in alt ]
                term.alts.append(new_alt)

    def symbol_to_terminal(self, sym):
        if isinstance(sym, str):
            return Terminal(sym)

        if callable(sym):
            term = self.sym2term.get(sym, None)
            if term is None:
                term = self.sym2term[sym] = Nonterminal(sym)

            return term

        raise ValueError(sym)

if __name__ == '__main__':
    gb = GrammarBuilder()
    gb.build(grammar.program)

    tokens = list(gb.start_term.generate(None))
    print(" ".join(tokens))

