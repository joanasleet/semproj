from pysparql import SPARQLWrapper, JSON

sp = SPARQLWrapper( "http://dbpedia.org/sparql" )

sp.setQuery("""

        """)

sp.setReturnFormat(JSON)

res = sparql.query().convert()

print res

