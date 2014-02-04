def program():
    yield scope

def statement():
    yield variable, ":", "=", expression
    yield "if", expression, "then", statement, "fi"
    yield "if", expression, "then", statement, "else", statement, "fi"
    yield "while", expression, "do", statement, "end"
    yield "repeat", statement, "until", expression
    yield "exit"
    yield "exit", "when", expression

    yield "result", expression
    yield "return",
    yield "put", output
    yield "get", _input
    yield procedurename, "(", argumentList, ")"
    yield scope

    # TODO: smarter capture of a `<nonterminal>+` grammar production
    yield statement, statement

def scope():
    yield "{", declaration, statement, "}"
    yield "{", statement, "}"
    #yield "{", "}"

def declaration():
    yield "var", variablenames, ":", _type
    yield functionHead, scope
    yield procedureHead, scope
    yield "forward", functionHead
    yield "forward", procedureHead
    yield declaration, declaration

def functionHead():
    yield "func", functionname, "(", parameterList, ")", ":",  _type

def procedureHead():
    yield "proc", procedurename, "(", parameterList, ")"

def variablenames():
    yield variablename
    yield variablename, "[", bound, "]"
    yield variablename, "[", bound, ",", bound, "]"
    yield variablenames, ",", variablenames

def bound():
    yield integer
    yield generalBound, ".", ".", generalBound

def generalBound():
    yield integer
    yield "-", integer

def _type():
    yield "integer"
    yield "boolean"

def output():
    yield expression
    yield text
    yield "newline"
    yield output, ",", output

def _input():
    yield variable
    yield _input, ",", _input

def argumentList():
    yield arguments

    # TODO: represent `EMPTY` better
    yield "" # EMPTY

def arguments():
    yield expression
    yield arguments, ",", arguments

def parameterList():
    yield parameters
    yield "" # EMPTY

def parameters():
    yield parametername, ":", _type
    yield parameters, ",", parameters

def variable():
    yield variablename
    yield parametername
    yield arrayname, "[", expression, "]"
    yield arrayname, "[", expression, ",", expression, "]"

def expression():
    yield integer
    yield "-", expression
    yield expression, "+", expression
    yield expression, "-", expression
    yield expression, "*", expression
    yield expression, "/", expression
    yield "true"
    yield "false"
    yield "not", expression
    yield expression, "and", expression
    yield expression, "or", expression
    yield expression, "=", expression
    yield expression, "not", "=", expression
    yield expression, "<", expression
    yield expression, "<", "=", expression
    yield expression, ">", expression
    yield expression, ">", "=", expression
    yield "(", expression, ")"
    yield "(", expression, "?", expression, ":", expression, ")"
    yield variable
    yield functionname, "(", argumentList, ")"

def variablename():
    yield identifier

def arrayname():
    yield identifier

def functionname():
    yield identifier

def parametername():
    yield identifier

def procedurename():
    yield identifier

def identifier():
    yield "foo"
    yield "bar"
    yield "xyz"
    yield "abc"

def integer():
    for i in range(10):
        yield str(i)

def text():
    yield '"text string"'

