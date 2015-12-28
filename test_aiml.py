import pyaiml

k = pyaiml.Kernel()

k.learn( "emma.aiml" )

while True:
    print k.respond( raw_input( "You: " ) )
