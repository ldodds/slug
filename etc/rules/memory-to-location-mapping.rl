#
# Jena rules to infer a LocationMapping configuration
# from a Scutter's memory. The configuration can be 
# used to configure the Jena FileManager and LocationMapper
# to use locally cached data where it has been scuttered
#
@prefix scutter: <http://purl.org/net/scutter/>.
@prefix lm: <http://jena.hpl.hp.com/2004/08/location-mapping#>.

[inferMappingFromLocalCopy:
   (?R rdf:type scutter:Representation), 
     (?R scutter:source ?source),
      (?R scutter:localCopy ?local),
         makeTemp(?mapping),         
         	makeLiteral(?source, ?source-as-literal)    
     -> (?mapping lm:name ?source-as-literal),
        (?mapping lm:altName ?local)]

[createMapping:
   (?mapping lm:name ?source), makeTemp(?x)
      -> (?x lm:mapping ?mapping)]